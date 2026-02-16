#!/bin/bash

##############################################################################
# Online Quiz Application - Quick Deployment Script for EC2
# 
# This script automates the build and deployment of the application
# to an EC2 instance running as a systemd service.
#
# Prerequisites:
# - Application JAR built and running as systemd service (quiz-app)
# - Environment file at: /opt/quiz-app/.env
# - Java 17 installed
#
# Usage: ./deploy.sh [options]
# Options:
#   --no-compile    Skip compilation, use existing JAR
#   --backup        Create backup before deployment
#   --restart-only  Only restart the service without rebuilding
#   --health-check  Perform health check after deployment
##############################################################################

set -e

# Color codes for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
PROJECT_DIR="/home/ec2-user/Online_quiz_application"
BACKEND_DIR="$PROJECT_DIR/backend"
APP_DIR="/opt/quiz-app"
JAR_NAME="quiz-app-1.0.0.jar"
SERVICE_NAME="quiz-app"
LOG_FILE="/var/log/deployment.log"

# Flags
COMPILE=true
BACKUP=false
RESTART_ONLY=false
HEALTH_CHECK=false
BUILD_SKIP_TESTS=true

##############################################################################
# Helper Functions
##############################################################################

log_info() {
    echo -e "${BLUE}[INFO]${NC} $1" | tee -a "$LOG_FILE"
}

log_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1" | tee -a "$LOG_FILE"
}

log_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1" | tee -a "$LOG_FILE"
}

log_error() {
    echo -e "${RED}[ERROR]${NC} $1" | tee -a "$LOG_FILE"
}

print_header() {
    echo "" | tee -a "$LOG_FILE"
    echo "============================================" | tee -a "$LOG_FILE"
    echo "$1" | tee -a "$LOG_FILE"
    echo "============================================" | tee -a "$LOG_FILE"
}

print_step() {
    echo "" | tee -a "$LOG_FILE"
    echo ">>> $1" | tee -a "$LOG_FILE"
}

# Parse command line arguments
parse_arguments() {
    while [[ $# -gt 0 ]]; do
        case $1 in
            --no-compile)
                COMPILE=false
                log_info "Compilation skipped"
                shift
                ;;
            --backup)
                BACKUP=true
                log_info "Backup enabled"
                shift
                ;;
            --restart-only)
                RESTART_ONLY=true
                COMPILE=false
                log_info "Restart only mode"
                shift
                ;;
            --health-check)
                HEALTH_CHECK=true
                log_info "Health check enabled"
                shift
                ;;
            *)
                log_warning "Unknown option: $1"
                shift
                ;;
        esac
    done
}

# Check prerequisites
check_prerequisites() {
    print_step "Checking prerequisites..."

    # Check Java
    if ! command -v java &> /dev/null; then
        log_error "Java is not installed"
        exit 1
    fi
    JAVA_VERSION=$(java -version 2>&1 | grep -oP 'version "\K[^"]+')
    log_info "Java version: $JAVA_VERSION"

    # Check Maven if compilation is enabled
    if [ "$COMPILE" = true ]; then
        if ! command -v mvn &> /dev/null; then
            log_error "Maven is not installed, but compilation is enabled"
            exit 1
        fi
        MVN_VERSION=$(mvn -version | head -n 1)
        log_info "Maven version: $MVN_VERSION"
    fi

    # Check systemctl
    if ! command -v systemctl &> /dev/null; then
        log_error "systemctl is not available"
        exit 1
    fi

    log_success "All prerequisites met"
}

# Pull latest code
pull_code() {
    print_step "Pulling latest code from repository..."

    cd "$BACKEND_DIR" || exit 1

    if [ ! -d ".git" ]; then
        log_warning "Not a git repository, skipping git pull"
        return
    fi

    if ! git pull origin main 2>> "$LOG_FILE"; then
        log_warning "Git pull failed, continuing with current code"
    else
        log_success "Code pulled successfully"
    fi
}

# Compile application
compile_application() {
    if [ "$COMPILE" = false ]; then
        log_info "Skipping compilation"
        return
    fi

    print_step "Compiling application..."

    cd "$BACKEND_DIR" || exit 1

    if [ "$BUILD_SKIP_TESTS" = true ]; then
        log_info "Building with tests skipped..."
        mvn clean package -DskipTests >> "$LOG_FILE" 2>&1
    else
        log_info "Building with tests..."
        mvn clean package >> "$LOG_FILE" 2>&1
    fi

    if [ ! -f "target/$JAR_NAME" ]; then
        log_error "Build failed or JAR not found at target/$JAR_NAME"
        exit 1
    fi

    log_success "Application compiled successfully"
}

# Create backup
create_backup() {
    if [ "$BACKUP" = false ]; then
        return
    fi

    print_step "Creating backup..."

    if [ ! -f "$APP_DIR/$JAR_NAME" ]; then
        log_warning "No existing JAR to backup"
        return
    fi

    BACKUP_DIR="$APP_DIR/backups"
    mkdir -p "$BACKUP_DIR"

    BACKUP_FILE="$BACKUP_DIR/${JAR_NAME%.*}-$(date +%Y%m%d-%H%M%S).jar"
    cp "$APP_DIR/$JAR_NAME" "$BACKUP_FILE"

    log_success "Backup created: $BACKUP_FILE"

    # Keep only last 5 backups
    ls -trQ "$BACKUP_DIR"/${JAR_NAME%.*}*.jar 2>/dev/null | head -n -5 | xargs rm -f 2>/dev/null || true
}

# Stop service
stop_service() {
    print_step "Stopping $SERVICE_NAME service..."

    if ! sudo systemctl is-active --quiet "$SERVICE_NAME"; then
        log_warning "Service is not running"
        return
    fi

    if ! sudo systemctl stop "$SERVICE_NAME" 2>> "$LOG_FILE"; then
        log_error "Failed to stop service"
        exit 1
    fi

    # Wait for service to fully stop
    sleep 2

    log_success "Service stopped"
}

# Deploy new JAR
deploy_jar() {
    if [ "$RESTART_ONLY" = false ]; then
        print_step "Deploying new JAR..."

        if [ ! -f "$BACKEND_DIR/target/$JAR_NAME" ]; then
            log_error "JAR file not found at $BACKEND_DIR/target/$JAR_NAME"
            exit 1
        fi

        if ! sudo cp "$BACKEND_DIR/target/$JAR_NAME" "$APP_DIR/$JAR_NAME" 2>> "$LOG_FILE"; then
            log_error "Failed to copy JAR to $APP_DIR"
            exit 1
        fi

        # Set proper permissions
        sudo chmod 755 "$APP_DIR/$JAR_NAME"

        log_success "JAR deployed successfully"
    fi
}

# Start service
start_service() {
    print_step "Starting $SERVICE_NAME service..."

    if ! sudo systemctl start "$SERVICE_NAME" 2>> "$LOG_FILE"; then
        log_error "Failed to start service"
        exit 1
    fi

    # Wait for service to start
    sleep 3

    if ! sudo systemctl is-active --quiet "$SERVICE_NAME"; then
        log_error "Service status check failed"
        log_info "Last 20 lines of logs:"
        sudo journalctl -u "$SERVICE_NAME" -n 20 | tee -a "$LOG_FILE"
        exit 1
    fi

    log_success "Service started successfully"
}

# Health check
check_health() {
    if [ "$HEALTH_CHECK" = false ]; then
        return
    fi

    print_step "Performing health check..."

    # Try up to 30 times with 1 second interval
    for i in {1..30}; do
        if curl -sf http://localhost:8080/api/auth/register \
            -H "Content-Type: application/json" \
            -d '{"name":"test","email":"test@test.com","password":"test123","role":"PARTICIPANT"}' &> /dev/null || \
           curl -sf http://localhost:8080/api/swagger-ui.html &> /dev/null; then
            log_success "Application is healthy and responding to requests"
            return
        fi
        sleep 1
    done

    log_warning "Health check did not receive expected response, but service is running"
    log_info "Checking service status:"
    sudo systemctl status "$SERVICE_NAME" | tee -a "$LOG_FILE"
}

# Display summary
display_summary() {
    print_header "DEPLOYMENT SUMMARY"

    log_success "Deployment completed successfully!"
    echo "" | tee -a "$LOG_FILE"
    echo "Application Details:" | tee -a "$LOG_FILE"
    echo "  Service: $SERVICE_NAME" | tee -a "$LOG_FILE"
    echo "  Location: $APP_DIR/$JAR_NAME" | tee -a "$LOG_FILE"
    echo "  API URL: http://localhost:8080/api" | tee -a "$LOG_FILE"
    echo "  Swagger UI: http://localhost:8080/api/swagger-ui.html" | tee -a "$LOG_FILE"
    echo "" | tee -a "$LOG_FILE"
    echo "Useful Commands:" | tee -a "$LOG_FILE"
    echo "  View logs: sudo journalctl -u $SERVICE_NAME -f" | tee -a "$LOG_FILE"
    echo "  Check status: sudo systemctl status $SERVICE_NAME" | tee -a "$LOG_FILE"
    echo "  Restart service: sudo systemctl restart $SERVICE_NAME" | tee -a "$LOG_FILE"
    echo "  Stop service: sudo systemctl stop $SERVICE_NAME" | tee -a "$LOG_FILE"
    echo "" | tee -a "$LOG_FILE"
}

##############################################################################
# Main Execution
##############################################################################

main() {
    print_header "ONLINE QUIZ APPLICATION - DEPLOYMENT SCRIPT"
    
    # Initialize log file
    echo "Deployment started: $(date)" > "$LOG_FILE"

    # Parse arguments
    parse_arguments "$@"

    # Run deployment steps
    check_prerequisites
    
    if [ "$RESTART_ONLY" = false ]; then
        pull_code
        compile_application
        create_backup
    fi
    
    stop_service
    deploy_jar
    start_service
    check_health
    display_summary

    log_success "All done! Deployment log saved to: $LOG_FILE"
}

# Execute main function
main "$@"
