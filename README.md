# BlueCollar - Deals Management Platform

A comprehensive deals management platform with role-based admin system, customer authentication, and deal management capabilities.

## 🚀 Features

### Customer Features
- **Mobile OTP Authentication** - Secure login with SMS verification
- **Google OAuth Integration** - Social login support
- **Profile Management** - Update customer information
- **Deals Browsing** - View home page, category deals, and brand details

### Admin Features
- **Role-Based Access Control** - Multiple admin roles with different permissions
- **Admin Management** - Create, update, and manage admin users
- **Deal Management** - Create, update, and delete deals
- **Session Management** - Secure admin sessions

## 🏗️ Architecture

### Admin Roles
1. **SUPER_ADMIN** - Full access to all features including user management
2. **ADMIN** - Can manage deals and view reports
3. **DEAL_MANAGER** - Can only manage deals
4. **VIEWER** - Read-only access to view data

### Technology Stack
- **Backend**: Spring Boot 3.5.3 with Java 21
- **Database**: MongoDB
- **Authentication**: JWT tokens, OAuth2
- **SMS**: Twilio integration
- **Payment**: Razorpay integration
- **Security**: Spring Security with BCrypt

## 📁 Project Structure

```
src/main/java/org/bluecollar/bluecollar/
├── admin/                 # Admin management
│   ├── controller/       # Admin controllers
│   ├── dto/             # Admin DTOs
│   ├── model/           # Admin models
│   ├── repository/      # Admin repositories
│   └── service/         # Admin services
├── common/              # Shared components
│   ├── dto/            # Common DTOs
│   ├── exception/      # Custom exceptions
│   ├── service/        # Shared services
│   └── util/           # Utility classes
├── config/             # Configuration classes
├── deals/              # Deals management
├── feedback/           # Feedback system
├── login/              # Authentication
├── payment/            # Payment processing
└── session/            # Session management
```

## 🔧 Setup & Installation

### Prerequisites
- Java 21
- MongoDB
- Gradle

### Environment Variables
Create a `.env` file or set environment variables:

```bash
# MongoDB
MONGODB_URI=mongodb://localhost:27017/bluecollar
MONGODB_DATABASE=bluecollar

# JWT
JWT_SECRET=your-secret-key
JWT_EXPIRATION=86400000

# Razorpay
RAZORPAY_KEY_ID=your-razorpay-key
RAZORPAY_KEY_SECRET=your-razorpay-secret

# Twilio
TWILIO_ACCOUNT_SID=your-twilio-sid
TWILIO_AUTH_TOKEN=your-twilio-token
TWILIO_PHONE_NUMBER=your-twilio-number

# Google OAuth
GOOGLE_OAUTH_CLIENT_ID=your-google-client-id

# CORS
CORS_ALLOWED_ORIGINS=http://localhost:3000,http://localhost:8080
```

### Running the Application

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd BlueCollar
   ```

2. **Build the project**
   ```bash
   ./gradlew clean build
   ```

3. **Run the application**
   ```bash
   ./gradlew bootRun
   ```

4. **Access the application**
   - Application: http://localhost:8080
   - API Documentation: http://localhost:8080/swagger-ui.html

## 🔐 Admin System

### Default Super Admin
The system automatically creates a default super admin on first startup:
- **Email**: admin@bluecollar.com
- **Password**: admin123

### Admin API Endpoints

#### Authentication
```bash
# Admin Login
POST /api/admin/login
{
  "email": "admin@bluecollar.com",
  "password": "admin123"
}

# Admin Logout
POST /api/admin/logout
Header: Admin-Session-Token: <session-token>
```

#### Admin Management (SUPER_ADMIN only)
```bash
# Create Admin
POST /api/admin/create
Header: Admin-Session-Token: <session-token>
{
  "email": "newadmin@example.com",
  "password": "password123",
  "name": "New Admin",
  "role": "DEAL_MANAGER"
}

# List Admins (role-scoped)
GET /api/admin/list
Header: Admin-Session-Token: <session-token>
Note: SUPER_ADMIN sees all; ADMIN sees only VIEWERs

# Update Admin Role
PUT /api/admin/{adminId}/role
Header: Admin-Session-Token: <session-token>
{
  "role": "ADMIN"
}

# Activate/Deactivate Admin
PUT /api/admin/{adminId}/activate
PUT /api/admin/{adminId}/deactivate
Header: Admin-Session-Token: <session-token>

# Delete Admin
DELETE /api/admin/{adminId}
Header: Admin-Session-Token: <session-token>
```

#### Deal Management (ADMIN, DEAL_MANAGER, SUPER_ADMIN)
```bash
# Create/Update Home Page
POST /api/admin/deals/home
PUT /api/admin/deals/home
Header: Admin-Session-Token: <session-token>
{
  "banners": [...],
  "popularBrands": [...],
  "handpickedDeals": [...],
  "categories": [...]
}

# Create/Update Category Deals
POST /api/admin/deals/category/{categoryId}
PUT /api/admin/deals/category/{categoryId}
Header: Admin-Session-Token: <session-token>
{
  "title": "Fashion",
  "tabs": ["All Deals", "Popular"],
  "activeTab": "All Deals",
  "offers": [...]
}

# Create/Update Brand Details
POST /api/admin/deals/brand/{brandId}
PUT /api/admin/deals/brand/{brandId}
Header: Admin-Session-Token: <session-token>
{
  "brandName": "Myntra",
  "bannerLink": "...",
  "brandDescription": "...",
  "discountText": "15% off",
  "validTill": "Valid till: Jun 15, 2025",
  "howItWorksBullets": [...],
  "benefits": [...],
  "howToRedeemBullets": [...],
  "termsAndConditions": [...],
  "faq": [...],
  "redeemLink": "/scratchCards/id"
}

# Delete Deals
DELETE /api/admin/deals/category/{categoryId}
DELETE /api/admin/deals/brand/{brandId}
Header: Admin-Session-Token: <session-token>

# List All Deals
GET /api/admin/deals/list
Header: Admin-Session-Token: <session-token>
```

## 👥 Customer API

### Authentication
```bash
# Send OTP
POST /api/auth/send-otp
{
  "mobile": "9876543210",
  "phoneCode": "+91"
}

# Verify OTP
POST /api/auth/verify-otp
{
  "mobile": "9876543210",
  "otp": "123456"
}

# Google OAuth
POST /api/auth/google-auth
{
  "idToken": "google-id-token"
}
```

### Profile Management
```bash
# Update Profile
PUT /api/auth/update-profile
Header: Session-Token: <session-token>
{
  "name": "John Doe",
  "email": "john@example.com"
}

# Get Profile
GET /api/auth/profile
Header: Session-Token: <session-token>
```

### Deals Browsing
```bash
# Get Home Page
GET /api/deals/home

# Get Category Deals
GET /api/deals/category/{categoryId}?tab=Popular

# Get Brand Details
GET /api/deals/brand/{brandId}
```

## 🛡️ Security Features

- **Role-based Access Control** - Different permissions for different admin roles
- **Session Management** - Secure admin and customer sessions
- **Input Validation** - Comprehensive validation for all inputs
- **Exception Handling** - Proper error handling and logging
- **CORS Configuration** - Configurable CORS settings
- **Password Encryption** - BCrypt password hashing

## 📊 Monitoring

- **Health Checks**: `/actuator/health`
- **Metrics**: `/actuator/metrics`
- **Prometheus**: `/actuator/prometheus`

## 🧪 Testing

```bash
# Run tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport
```

## 🐳 Docker

```bash
# Build Docker image
docker build -t bluecollar .

# Run with Docker Compose
docker-compose up -d
```

## 📝 API Documentation

- **Swagger UI**: http://localhost:8080/swagger-ui.html
- **API Docs**: http://localhost:8080/api-docs

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests
5. Submit a pull request

## 📄 License

This project is licensed under the MIT License.

## 🆘 Support

For support and questions, please contact the development team.