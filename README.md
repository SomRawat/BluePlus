# BlueCollar API

Production-ready Spring Boot application with authentication, payment integration, deals system, and coupon management.

## Features

- **Authentication**: OTP-based and Google OAuth login
- **Session Management**: Client-specific session expiry (30 days mobile, 3 days web)
- **Payment Integration**: Razorpay with fallback mechanisms
- **Deals System**: Home page, brand details (PDP), category-wise deals
- **Coupon Management**: City-based coupon creation, redemption with expiry
- **Admin Panel**: CRUD operations with role-based access
- **Security**: Input validation, rate limiting, password encryption
- **Monitoring**: Health checks, metrics, logging

## Tech Stack

- Java 21
- Spring Boot 3.5.3
- MongoDB
- Razorpay

## Quick Start

### Prerequisites
- Java 21
- MongoDB
- Razorpay account

### Local Development
```bash
# Clone repository
git clone <repository-url>
cd BlueCollar

# Set environment variables
cp .env.example .env
# Edit .env with your configuration

# Run application
./gradlew bootRun
```

### Production Deployment
```bash
# Build application
./gradlew build

# Run with Docker
docker-compose up -d
```

## API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs
- **Health Check**: http://localhost:8080/actuator/health

## Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| MONGODB_URI | MongoDB connection string | mongodb://localhost:27017/bluecollar |
| RAZORPAY_KEY_ID | Razorpay key ID | - |
| RAZORPAY_KEY_SECRET | Razorpay key secret | - |
| ALLOWED_ORIGINS | CORS allowed origins | * |
| PORT | Application port | 8080 |

## Security Features

- BCrypt password hashing
- Session-based authentication
- Input validation and sanitization
- Rate limiting for OTP requests
- SQL injection and XSS prevention
- Secure headers (HSTS, X-Frame-Options)

## Monitoring

- Health checks via Spring Actuator
- Prometheus metrics
- Request logging
- Error tracking

## System Architecture

```
┌─────────────────┐    ┌─────────────────┐    ┌─────────────────┐
│   Mobile App    │    │    Web App      │    │   Admin Panel   │
└─────────┬───────┘    └─────────┬───────┘    └─────────┬───────┘
          │                      │                      │
          └──────────────────────┼──────────────────────┘
                                 │
                    ┌─────────────┴─────────────┐
                    │    BlueCollar API         │
                    │  (Spring Boot 3.5.3)     │
                    └─────────────┬─────────────┘
                                  │
                    ┌─────────────┴─────────────┐
                    │      MongoDB Atlas        │
                    │   (Database Storage)      │
                    └───────────────────────────┘
```

## Complete API Flow

### 1. User Authentication Flow
```bash
# Step 1: Send OTP
curl -X POST http://localhost:8080/api/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{"mobile":"9876543210"}'

# Step 2: Verify OTP (Get Session Token)
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -H "Api-Client: web" \
  -d '{"mobile":"9876543210","otp":"123456"}'

# Response: {"result":{"token":"session-token-here","isFirstTime":true,"customerId":"customer-id","message":"Login successful"},"status":200}
```

### 2. Deals System Flow
```bash
# Get Home Page Data
curl -X GET http://localhost:8080/api/deals/home

# Get Brand Details (PDP)
curl -X GET http://localhost:8080/api/deals/brand/{brandId}

# Get Category Deals with Filters
curl -X GET "http://localhost:8080/api/deals/category/{categoryId}?tab=Popular"
```

### 3. Coupon Management Flow
```bash
# Create Coupon for User
curl -X POST http://localhost:8080/api/coupons/create \
  -H "Content-Type: application/json" \
  -H "Session-Token: your-session-token" \
  -d '{"brandId":"brand-id","city":"Mumbai","expiryDays":30}'

# Get User's Active Coupons
curl -X GET http://localhost:8080/api/coupons/active \
  -H "Session-Token: your-session-token"

# Redeem Coupon
curl -X POST http://localhost:8080/api/coupons/redeem/{couponCode} \
  -H "Session-Token: your-session-token"

# Get All User Coupons (Active + Redeemed)
curl -X GET http://localhost:8080/api/coupons/my-coupons \
  -H "Session-Token: your-session-token"
```

### 4. Payment Integration Flow
```bash
# Create Payment
curl -X POST http://localhost:8080/api/payment/create \
  -H "Content-Type: application/json" \
  -H "Session-Token: your-session-token" \
  -d '{"amount":100,"description":"Service payment"}'

# Verify Payment
curl -X POST http://localhost:8080/api/payment/verify \
  -H "Content-Type: application/json" \
  -d '{"razorpayOrderId":"order_id","razorpayPaymentId":"payment_id","razorpaySignature":"signature"}'
```

## Data Models

### Collections in MongoDB:
- **customers**: User profiles and authentication data
- **user_sessions**: Session tokens with expiry
- **banners**: Home page promotional banners
- **brands**: Brand details with PDP information
- **categories**: Deal categories
- **coupons**: User-specific coupons with city and expiry
- **payments**: Payment transactions
- **admins**: Admin user management

## Coupon System Features

### Coupon Creation Rules:
- ✅ One active coupon per user per brand
- ✅ City-based coupon generation
- ✅ Configurable expiry (default 30 days)
- ✅ Unique coupon codes (BC + 8 chars)

### Coupon Redemption Rules:
- ✅ One-time redemption only
- ✅ Expiry date validation
- ✅ User ownership validation
- ✅ Redemption timestamp tracking

## Production Checklist

- ✅ Authentication & Session Management
- ✅ Deals & Coupon System
- ✅ Payment Integration
- ✅ Input validation & Error handling
- ✅ MongoDB indexing
- ✅ Health checks & Monitoring
- ✅ Environment-based configuration