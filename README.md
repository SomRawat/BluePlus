# BlueCollar API

Production-ready Spring Boot application with authentication, payment integration, and admin panel.

## Features

- **Authentication**: OTP-based and Google OAuth login
- **Session Management**: Client-specific session expiry (30 days mobile, 3 days web)
- **Payment Integration**: Razorpay with fallback mechanisms
- **Admin Panel**: CRUD operations with role-based access
- **Security**: Input validation, rate limiting, password encryption
- **Monitoring**: Health checks, metrics, logging
- **Documentation**: Swagger/OpenAPI 3.0

## Tech Stack

- Java 21
- Spring Boot 3.5.3
- MongoDB
- Razorpay
- Spring Security
- Swagger/OpenAPI

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

## Production Checklist

- ✅ Security configuration
- ✅ Input validation
- ✅ Error handling
- ✅ Logging configuration
- ✅ Health checks
- ✅ Docker support
- ✅ Environment-based configuration
- ✅ Database indexing
- ✅ Payment fallback mechanisms
- ✅ Session management
- ✅ API documentation