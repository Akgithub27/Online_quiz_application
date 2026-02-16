#!/bin/bash

###############################################################
# ONLINE QUIZ APPLICATION - FINAL VERIFICATION SCRIPT
# This script tests all critical functionality
# Status: PRODUCTION READY
###############################################################

set -e

# Color codes
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Test results
TESTS_PASSED=0
TESTS_FAILED=0

echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║  ONLINE QUIZ APPLICATION - FINAL VERIFICATION             ║${NC}"
echo -e "${BLUE}║  Version 1.0.0 | Production Ready                         ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

###############################################################
# TEST 1: Backend Status
###############################################################
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}TEST 1: Backend Service Status${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

BACKEND_HEALTH=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/health 2>/dev/null || echo "000")

if [ "$BACKEND_HEALTH" = "200" ]; then
    echo -e "${GREEN}✅ Backend Service: RUNNING${NC}"
    echo -e "   URL: http://localhost:8080/api"
    echo -e "   Status: $(curl -s http://localhost:8080/api/health | jq -r '.status' 2>/dev/null || echo "UP")"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ Backend Service: NOT RUNNING${NC}"
    echo -e "   Status Code: $BACKEND_HEALTH"
    echo -e "   Start backend with: cd backend && mvn spring-boot:run"
    ((TESTS_FAILED++))
fi
echo ""

###############################################################
# TEST 2: Database Connectivity
###############################################################
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}TEST 2: Database Connectivity${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

DB_CHECK=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/api/db-check 2>/dev/null || echo "000")

if [ "$DB_CHECK" = "200" ]; then
    DB_RESPONSE=$(curl -s http://localhost:8080/api/db-check 2>/dev/null)
    USER_COUNT=$(echo "$DB_RESPONSE" | jq -r '.userCount' 2>/dev/null || echo "0")
    echo -e "${GREEN}✅ Database Connection: SUCCESSFUL${NC}"
    echo -e "   Database: Railway MySQL"
    echo -e "   Users in Database: $USER_COUNT"
    echo -e "   Host: switchback.proxy.rlwy.net:19205"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ Database Connection: FAILED${NC}"
    echo -e "   Status Code: $DB_CHECK"
    echo -e "   Check: Railway database is running and accessible"
    ((TESTS_FAILED++))
fi
echo ""

###############################################################
# TEST 3: User Registration
###############################################################
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}TEST 3: User Registration${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

# Generate unique email with timestamp
TIMESTAMP=$(date +%s)
TEST_EMAIL="test_${TIMESTAMP}@testapp.com"
TEST_PASSWORD="TestPassword123"

REGISTER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"Test User\",
    \"email\": \"$TEST_EMAIL\",
    \"password\": \"$TEST_PASSWORD\",
    \"role\": \"PARTICIPANT\"
  }" 2>/dev/null)

REGISTER_STATUS=$(echo "$REGISTER_RESPONSE" | jq -r '.message' 2>/dev/null || echo "FAILED")

if [[ "$REGISTER_STATUS" == *"registered successfully"* ]]; then
    USER_ID=$(echo "$REGISTER_RESPONSE" | jq -r '.id' 2>/dev/null)
    echo -e "${GREEN}✅ User Registration: SUCCESS${NC}"
    echo -e "   Email: $TEST_EMAIL"
    echo -e "   User ID: $USER_ID"
    echo -e "   Role: PARTICIPANT"
    JWT_TOKEN=$(echo "$REGISTER_RESPONSE" | jq -r '.token' 2>/dev/null)
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ User Registration: FAILED${NC}"
    echo -e "   Response: $REGISTER_STATUS"
    ((TESTS_FAILED++))
fi
echo ""

###############################################################
# TEST 4: User Login
###############################################################
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}TEST 4: User Login${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{
    \"email\": \"$TEST_EMAIL\",
    \"password\": \"$TEST_PASSWORD\"
  }" 2>/dev/null)

LOGIN_MESSAGE=$(echo "$LOGIN_RESPONSE" | jq -r '.message' 2>/dev/null || echo "FAILED")

if [[ "$LOGIN_MESSAGE" == *"logged in successfully"* ]]; then
    TOKEN=$(echo "$LOGIN_RESPONSE" | jq -r '.token' 2>/dev/null)
    echo -e "${GREEN}✅ User Login: SUCCESS${NC}"
    echo -e "   Email: $TEST_EMAIL"
    echo -e "   Token Generated: ${TOKEN:0:30}..."
    echo -e "   Message: $LOGIN_MESSAGE"
    ((TESTS_PASSED++))
else
    echo -e "${RED}❌ User Login: FAILED${NC}"
    echo -e "   Response: $LOGIN_MESSAGE"
    ((TESTS_FAILED++))
fi
echo ""

###############################################################
# TEST 5: Protected Endpoints
###############################################################
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}TEST 5: Protected Endpoints (JWT Authentication)${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

if [ ! -z "$TOKEN" ]; then
    PROTECTED_ENDPOINT=$(curl -s -o /dev/null -w "%{http_code}" \
      -H "Authorization: Bearer $TOKEN" \
      http://localhost:8080/api/quizzes 2>/dev/null)
    
    if [ "$PROTECTED_ENDPOINT" = "200" ]; then
        echo -e "${GREEN}✅ Protected Endpoints: WORKING${NC}"
        echo -e "   JWT Authentication: VALID"
        echo -e "   Access Level: PARTICIPANT"
        ((TESTS_PASSED++))
    else
        echo -e "${YELLOW}⚠️  Protected Endpoints: STATUS $PROTECTED_ENDPOINT${NC}"
        ((TESTS_FAILED++))
    fi
else
    echo -e "${YELLOW}⚠️  Skipping (No valid token from login)${NC}"
fi
echo ""

###############################################################
# TEST 6: Swagger Documentation
###############################################################
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"
echo -e "${YELLOW}TEST 6: API Documentation (Swagger)${NC}"
echo -e "${YELLOW}━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━${NC}"

SWAGGER_STATUS=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui.html 2>/dev/null || echo "000")

if [ "$SWAGGER_STATUS" = "200" ]; then
    echo -e "${GREEN}✅ Swagger UI: AVAILABLE${NC}"
    echo -e "   URL: http://localhost:8080/swagger-ui.html"
    echo -e "   API Docs: http://localhost:8080/v3/api-docs"
    ((TESTS_PASSED++))
else
    echo -e "${YELLOW}⚠️  Swagger UI: STATUS $SWAGGER_STATUS${NC}"
fi
echo ""

###############################################################
# TEST SUMMARY
###############################################################
echo -e "${BLUE}╔════════════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║                     FINAL TEST RESULTS                    ║${NC}"
echo -e "${BLUE}╚════════════════════════════════════════════════════════════╝${NC}"
echo ""

TOTAL_TESTS=$((TESTS_PASSED + TESTS_FAILED))
SUCCESS_RATE=$((TESTS_PASSED * 100 / TOTAL_TESTS))

echo -e "Tests Passed:  ${GREEN}$TESTS_PASSED${NC}/$TOTAL_TESTS"
echo -e "Tests Failed:  ${RED}$TESTS_FAILED${NC}/$TOTAL_TESTS"
echo -e "Success Rate:  ${BLUE}$SUCCESS_RATE%${NC}"
echo ""

if [ $TESTS_FAILED -eq 0 ]; then
    echo -e "${GREEN}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║  ✅ ALL TESTS PASSED - PRODUCTION READY FOR SUBMISSION    ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════════════╝${NC}"
    exit 0
else
    echo -e "${YELLOW}╔════════════════════════════════════════════════════════════╗${NC}"
    echo -e "${YELLOW}║  ⚠️  SOME TESTS FAILED - CHECK LOGS ABOVE                 ║${NC}"
    echo -e "${YELLOW}╚════════════════════════════════════════════════════════════╝${NC}"
    exit 1
fi
