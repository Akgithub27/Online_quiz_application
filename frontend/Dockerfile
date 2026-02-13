# Frontend Dockerfile for Production
FROM node:18-alpine as builder

WORKDIR /app

# Copy package files
COPY package*.json ./

# Install dependencies
RUN npm ci

# Copy source code
COPY . .

# Build for production
RUN npm run build

# Production stage
FROM node:18-alpine

WORKDIR /app

# Install serve to run static files
RUN npm install -g serve

# Copy built application from builder
COPY --from=builder /app/dist ./dist

# Expose port
EXPOSE 3000

# Start application
CMD ["serve", "-s", "dist", "-l", "3000"]
