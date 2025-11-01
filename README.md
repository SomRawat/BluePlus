# 🎯 BluePlus - Enterprise Deals & Coupon Management Platform

A comprehensive, production-ready deals management platform with role-based admin system, customer authentication, real-time coupon management, and thread-safe redemption system.

## 📑 Table of Contents
- [Overview](#-overview)
- [Architecture](#-architecture)
- [Features](#-features)
- [Technology Stack](#-technology-stack)
- [Complete Flow Diagrams](#-complete-flow-diagrams)
- [Setup & Installation](#-setup--installation)
- [API Documentation](#-api-documentation)
- [Security & Best Practices](#-security--best-practices)
- [Monitoring & Observability](#-monitoring--observability)

---

## 🌟 Overview

BluePlus is an enterprise-grade platform for managing deals, coupons, and promotional campaigns with:
- **Thread-safe coupon redemption** with optimistic locking
- **Multi-instance deployment** support
- **Role-based access control** (RBAC) for admins
- **Real-time coupon tracking** with cached counts
- **OTP & OAuth authentication**
- **Multi-language support** via Google Translate
- **Production-ready** with comprehensive error handling

---

## 🏗️ Architecture

### High-Level System Architecture

```mermaid
graph TB
    subgraph "Client Layer"
        A[Mobile App - Android]
        B[Mobile App - iOS]
        C[Admin Web Panel]
    end
    
    subgraph "API Gateway Layer"
        D[Spring Boot Application<br/>Port: 8080]
    end
    
    subgraph "Service Layer"
        E[Authentication Service]
        F[Deals Service]
        G[Coupon Service]
        H[Admin Service]
        I[Session Service]
        J[Translation Service]
    end
    
    subgraph "Data Layer"
        K[(MongoDB)]
        L[Twilio SMS]
        M[Google OAuth]
        N[Google Translate]
    end
    
    A --> D
    B --> D
    C --> D
    D --> E
    D --> F
    D --> G
    D --> H
    E --> I
    F --> G
    G --> K
    E --> K
    F --> K
    H --> K
    I --> K
    E --> L
    E --> M
    F --> N
    
    style D fill:#4CAF50
    style K fill:#FF9800
```

### Database Collections Structure

```mermaid
graph LR
    subgraph "MongoDB Collections"
        A[customers]
        B[admins]
        C[user_sessions]
        D[admin_sessions]
        E[pdp_pages]
        F[plp_pages]
        G[home_pages]
        H[user_coupons]
        I[coupons]
        J[otp_sessions]
        K[feedback]
    end
    
    A -.redeemedCoupons.-> E
    B -.manages.-> E
    C -.authenticates.-> A
    D -.authenticates.-> B
    E -.campaign.-> H
    H -.redemptions.-> A
    I -.metadata.-> E
    
    style E fill:#2196F3
    style H fill:#FF5722
    style A fill:#4CAF50
    style B fill:#9C27B0
```

---

## ✨ Features

### 🎫 Customer Features
- ✅ Mobile OTP Authentication (SMS via Twilio)
- ✅ Google OAuth 2.0 Integration
- ✅ Profile Management (Name, Email, Phone)
- ✅ Home Page with Banners & Popular Brands
- ✅ Category-wise Deal Browsing (PLP)
- ✅ Brand Details Page (PDP) with FAQ
- ✅ **Thread-Safe Coupon Redemption**
- ✅ Real-time Coupon Availability Tracking
- ✅ Multi-language Support (Auto-translation)
- ✅ Feedback Submission

### 👨‍💼 Admin Features
- ✅ **Role-Based Access Control** (4 Roles)
- ✅ Admin User Management (CRUD)
- ✅ Deal Management (Home, PLP, PDP)
- ✅ Coupon Campaign Creation & Management
- ✅ Real-time Coupon Tracking Dashboard
- ✅ Admin Session Management
- ✅ Password Reset (OTP via Email)
- ✅ Activity Logging

---

## 🛠️ Technology Stack

### Backend
- **Framework**: Spring Boot 3.5.3
- **Language**: Java 21
- **Build Tool**: Gradle
- **Database**: MongoDB (with Optimistic Locking)
- **Security**: Spring Security + BCrypt
- **API Documentation**: Springdoc OpenAPI (Swagger)

### Integrations
- **SMS**: Twilio API
- **OAuth**: Google OAuth 2.0
- **Translation**: Google Cloud Translation API
- **Email**: JavaMailSender (SMTP)

### Key Libraries
- **Lombok**: Code generation
- **Jackson**: JSON processing
- **SLF4J + Logback**: Logging
- **Spring Data MongoDB**: Data access
- **Spring Boot Actuator**: Monitoring

---

## 📊 Complete Flow Diagrams

### 1️⃣ Customer Authentication Flow

```mermaid
sequenceDiagram
    participant C as Customer (Mobile App)
    participant A as AuthController
    participant AS as AuthService
    participant T as Twilio SMS
    participant M as MongoDB
    participant S as SessionService
    
    C->>A: POST /api/auth/send-otp<br/>{mobile: "9876543210"}
    A->>AS: sendOtp(request)
    AS->>AS: Validate mobile format
    AS->>AS: Generate 6-digit OTP
    AS->>M: Save OtpSession
    AS->>T: Send SMS
    T-->>C: SMS with OTP
    AS-->>A: "OTP sent successfully"
    A-->>C: 200 OK
    
    Note over C: User receives OTP
    
    C->>A: POST /api/auth/verify-otp<br/>{mobile, otp, clientType}
    A->>AS: verifyOtp(request)
    AS->>M: Find OtpSession
    AS->>AS: Verify OTP & Check expiry
    AS->>M: Find or Create Customer
    AS->>S: createSession(customerId)
    S->>M: Save UserSession
    AS-->>A: LoginResponse{sessionToken}
    A-->>C: 200 OK + Session Token
    
    Note over C: Store Session Token<br/>for all future requests
```

### 2️⃣ Admin Authentication & Role Management

```mermaid
sequenceDiagram
    participant A as Admin (Web Panel)
    participant AC as AdminController
    participant AS as AdminService
    participant ASS as AdminSessionService
    participant M as MongoDB
    
    A->>AC: POST /api/admin/login<br/>{email, password}
    AC->>AS: adminLogin(request)
    AS->>M: findByEmail(email)
    AS->>AS: Verify password (BCrypt)
    AS->>ASS: createSession(adminId, role)
    ASS->>M: Save AdminSession
    AS-->>AC: {sessionToken, role}
    AC-->>A: 200 OK + Session Token
    
    Note over A,M: Role-Based Access Control
    
    A->>AC: POST /api/admin/create<br/>Header: Admin-Session-Token
    AC->>AS: createAdmin(request, token)
    AS->>ASS: validateCanCreateAdmins(token)
    ASS->>ASS: Check role permissions<br/>(SUPER_ADMIN only)
    
    alt Has Permission
        AS->>AS: Validate admin creation rules
        AS->>M: Save new Admin
        AS-->>AC: AdminResponse
        AC-->>A: 201 Created
    else No Permission
        ASS-->>AS: UnauthorizedException
        AS-->>AC: 403 Forbidden
        AC-->>A: Error Response
    end
```

### 3️⃣ Complete Deals Flow (Home → PLP → PDP → Redeem)

```mermaid
sequenceDiagram
    participant C as Customer
    participant DC as DealsController
    participant DS as DealsService
    participant CS as CustomerCouponService
    participant M as MongoDB
    
    Note over C: 1. Home Page
    C->>DC: GET /api/deals/home<br/>Header: Session-Token
    DC->>DS: getHomePage()
    DS->>M: Find HomePage document
    DS->>M: Find Customer
    DS->>DS: markRedeemedBrands()<br/>(in-memory check)
    DS-->>DC: HomePageResponse
    DC-->>C: Banners, Brands, Categories
    
    Note over C: User selects category
    
    Note over C: 2. Category Listing (PLP)
    C->>DC: GET /api/deals/category/{categoryId}<br/>?tab=Popular
    DC->>DS: getCategoryDeals(categoryId, tab)
    DS->>M: Find PLP document
    DS->>DS: Filter by tab
    DS->>DS: markRedeemedOffers()
    DS-->>DC: CategoryDealsResponse
    DC-->>C: List of Offers
    
    Note over C: User selects brand
    
    Note over C: 3. Brand Details (PDP)
    C->>DC: GET /api/deals/brand/{brandId}
    DC->>DS: getBrandDetails(brandId)
    DS->>M: Find PDP document (with version)
    DS->>DS: Get campaign.remaining<br/>(cached count)
    DS-->>DC: BrandDetailsResponse
    DC->>CS: getUserCoupon(customerId, brandId)
    CS->>M: Find UserCoupon
    CS-->>DC: UserCoupon or null
    DC-->>C: Brand Info + Remaining Coupons<br/>+ User's coupon (if redeemed)
    
    Note over C: User clicks "Redeem"
    
    Note over C: 4. Coupon Redemption<br/>(Thread-Safe)
    C->>DC: POST /api/deals/brand/{brandId}/redeem
    DC->>CS: redeemCoupon(customerId, brandId)
    
    loop Retry up to 3 times
        CS->>M: Find PDP (with version)
        CS->>CS: Check campaign.remaining > 0
        
        alt Coupons Available
            CS->>M: Save UserCoupon
            CS->>M: Update PDP.campaign.remaining--<br/>(with version check)
            
            alt Version Match (Success)
                CS->>M: Update customer.redeemedCoupons[]
                CS-->>DC: Coupon Code
                DC-->>C: 200 OK + Coupon Code
            else OptimisticLockingFailure
                Note over CS: Version conflict!<br/>Another user redeemed
                CS->>CS: Wait 50-100ms
                CS->>CS: Retry with fresh data
            end
        else No Coupons
            CS-->>DC: BadRequestException
            DC-->>C: 400 "All coupons claimed"
        end
    end
```

### 4️⃣ Optimistic Locking Mechanism (Race Condition Prevention)

```mermaid
sequenceDiagram
    participant U1 as User 1 (Instance A)
    participant U2 as User 2 (Instance B)
    participant M as MongoDB
    
    Note over M: PDP: {remaining: 1, version: 100}
    
    par Simultaneous Requests
        U1->>M: Read PDP<br/>remaining=1, version=100
        U2->>M: Read PDP<br/>remaining=1, version=100
    end
    
    Note over U1,U2: Both see 1 coupon available!
    
    U1->>M: Save PDP<br/>{remaining: 0, version: 101}<br/>WHERE version=100
    
    Note over M: ✅ Version matches!<br/>Update successful
    
    M-->>U1: Success ✅<br/>Coupon redeemed
    
    U2->>M: Save PDP<br/>{remaining: 0, version: 101}<br/>WHERE version=100
    
    Note over M: ❌ Version mismatch!<br/>Current version is 101
    
    M-->>U2: OptimisticLockingFailureException
    
    Note over U2: Retry with fresh data
    
    U2->>M: Read PDP<br/>remaining=0, version=101
    U2->>U2: Check: remaining <= 0
    U2-->>U2: Error: "All coupons claimed"
```

### 5️⃣ Admin Coupon Campaign Creation

```mermaid
sequenceDiagram
    participant A as Admin
    participant ACC as AdminCouponController
    participant ACS as AdminCouponService
    participant M as MongoDB
    
    A->>ACC: POST /api/admin/coupon/create<br/>Header: Admin-Session-Token
    ACC->>ACC: validateCanManageDeals()
    ACC->>ACS: upsertCouponCampaign(request)
    
    ACS->>M: Find PDP by brandId
    
    alt PDP Exists
        ACS->>ACS: Create CouponCampaign
        ACS->>ACS: Set campaign.remaining<br/>= noOfCoupons
        ACS->>ACS: Calculate expiresAt<br/>(expiryDays from now)
        ACS->>M: Update PDP.campaign
        ACS->>M: Save/Update Coupon document
        ACS-->>ACC: CouponResponse
        ACC-->>A: 200 OK
    else PDP Not Found
        ACS-->>ACC: ResourceNotFoundException
        ACC-->>A: 404 Not Found
    end
    
    Note over A: Campaign is now live<br/>Users can start redeeming
```

---

## 🚀 Setup & Installation

### Prerequisites
- **Java 21** or higher
- **MongoDB 6.0+** (local or cloud)
- **Gradle 8.x** (wrapper included)
- **Twilio Account** (for SMS OTP)
- **Google OAuth Client ID** (for social login)
- **Google Cloud Project** (for translation, optional)

### 1. Clone Repository
```bash
git clone <repository-url>
cd BluePlus
```

### 2. Configure Application

Create `src/main/resources/application-local.properties`:

```properties
# MongoDB Configuration
spring.data.mongodb.uri=mongodb://localhost:27017/blueplus
spring.data.mongodb.database=blueplus
spring.data.mongodb.auto-index-creation=true

# Security - Admin Password
admin.default.password=ChangeThisPassword123!

# JWT Configuration
jwt.secret=your-super-secret-key-min-256-bits
jwt.expiration=86400000

# Twilio Configuration (SMS OTP)
twilio.account.sid=YOUR_TWILIO_ACCOUNT_SID
twilio.auth.token=YOUR_TWILIO_AUTH_TOKEN
twilio.phone.number=YOUR_TWILIO_PHONE_NUMBER

# Google OAuth
google.oauth.client.id=YOUR_GOOGLE_CLIENT_ID

# Google Translate API (Optional)
google.translate.api.key=YOUR_GOOGLE_TRANSLATE_KEY

# Email Configuration (Admin password reset)
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true

# CORS Configuration
cors.allowed.origins=http://localhost:3000,http://localhost:8080

# Server Configuration
server.port=8080

# Logging
logging.level.org.bluecollar.bluecollar=INFO
logging.level.org.springframework.data.mongodb=DEBUG
```

### 3. Build & Run

```bash
# Build the project
./gradlew clean build

# Run the application
./gradlew bootRun

# Or run with specific profile
./gradlew bootRun --args='--spring.profiles.active=local'
```

### 4. Verify Installation

```bash
# Health check
curl http://localhost:8080/actuator/health

# API documentation
open http://localhost:8080/swagger-ui.html
```

### 5. Default Admin Access

The system creates a default Super Admin on first startup:
- **Email**: `admin@bluecollar.com`
- **Password**: (from `admin.default.password` config)
- **Role**: `SUPER_ADMIN`

---

## 📚 API Documentation

### 🔐 Customer APIs

#### Authentication

**1. Send OTP**
```bash
curl -X POST http://localhost:8080/api/auth/send-otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobile": "9876543210",
    "phoneCode": "+91"
  }'

# Response:
# {
#   "data": "OTP sent successfully to +919876543210",
#   "statusCode": 200
# }
```

**2. Verify OTP & Login**
```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "mobile": "9876543210",
    "otp": "123456",
    "phoneCode": "+91"
  }' \
  -H "Client-Type: android"

# Response:
# {
#   "data": {
#     "sessionToken": "abc123xyz789...",
#     "customer": {
#       "id": "customer_id",
#       "name": "John Doe",
#       "mobile": "9876543210",
#       "email": "john@example.com"
#     }
#   },
#   "statusCode": 200
# }
```

**3. Google OAuth Login**
```bash
curl -X POST http://localhost:8080/api/auth/google-auth \
  -H "Content-Type: application/json" \
  -H "Client-Type: android" \
  -d '{
    "idToken": "google_id_token_here"
  }'

# Response: Same as OTP verify
```

**4. Update Profile**
```bash
curl -X PUT http://localhost:8080/api/auth/update-profile \
  -H "Content-Type: application/json" \
  -H "Session-Token: YOUR_SESSION_TOKEN" \
  -d '{
    "name": "John Updated",
    "email": "john.updated@example.com"
  }'
```

**5. Get Profile**
```bash
curl -X GET http://localhost:8080/api/auth/profile \
  -H "Session-Token: YOUR_SESSION_TOKEN"
```

---

#### Deals & Coupons

**1. Get Home Page**
```bash
curl -X GET http://localhost:8080/api/deals/home \
  -H "Session-Token: YOUR_SESSION_TOKEN" \
  -H "X-Accept-Language: en"

# Response:
# {
#   "data": {
#     "banners": [
#       {"id": "b1", "imageUrl": "...", "redirectionLink": "..."}
#     ],
#     "popularBrands": [
#       {
#         "id": "brand1",
#         "brandName": "Myntra",
#         "imageUrl": "...",
#         "discount": "15% Off",
#         "redeemed": false
#       }
#     ],
#     "categories": [...],
#     "handpickedDeals": [...]
#   },
#   "statusCode": 200
# }
```

**2. Get Category Deals (PLP)**
```bash
curl -X GET "http://localhost:8080/api/deals/category/fashion?tab=Popular" \
  -H "Session-Token: YOUR_SESSION_TOKEN" \
  -H "X-Accept-Language: en"

# Response:
# {
#   "data": {
#     "title": "Fashion",
#     "tabs": ["All Deals", "Popular", "Trending"],
#     "activeTab": "Popular",
#     "offers": [
#       {
#         "id": "offer1",
#         "brand": "Myntra",
#         "discount": "15% Off",
#         "imageUrl": "...",
#         "brandId": "brand_myntra",
#         "redeemed": false
#       }
#     ]
#   },
#   "statusCode": 200
# }
```

**3. Get Brand Details (PDP)**
```bash
curl -X GET http://localhost:8080/api/deals/brand/brand_myntra \
  -H "Session-Token: YOUR_SESSION_TOKEN" \
  -H "X-Accept-Language: en"

# Response:
# {
#   "data": {
#     "brandName": "Myntra",
#     "bannerLink": "...",
#     "brandDescription": "...",
#     "couponCode": "MYNTRA15",
#     "validTill": "Valid till: Dec 31, 2024",
#     "howItWorksBullets": [...],
#     "benefits": [...],
#     "howToRedeemBullets": [...],
#     "termsAndConditions": [...],
#     "faq": [
#       {"question": "...", "answer": "..."}
#     ],
#     "redeemLink": "/scratchCards/myntra",
#     "redeemed": false,
#     "couponInfo": {
#       "couponCode": "MYNTRA15",
#       "available": true,
#       "remainingCount": 487
#     },
#     "userCouponCode": null
#   },
#   "statusCode": 200
# }
```

**4. Redeem Coupon** ⭐
```bash
curl -X POST http://localhost:8080/api/deals/brand/brand_myntra/redeem \
  -H "Session-Token: YOUR_SESSION_TOKEN"

# Success Response:
# {
#   "data": "MYNTRA15",
#   "statusCode": 200
# }

# Error Responses:
# - Already redeemed: 400 "You have already redeemed a coupon for this brand"
# - No coupons left: 400 "All coupons for this campaign have been claimed"
# - Campaign expired: 400 "This coupon campaign has expired"
# - Campaign inactive: 400 "This coupon campaign is no longer active"
```

**5. Get User's Coupon**
```bash
curl -X GET http://localhost:8080/api/deals/brand/brand_myntra/coupon \
  -H "Session-Token: YOUR_SESSION_TOKEN"

# Response:
# {
#   "data": {
#     "id": "user_coupon_id",
#     "customerId": "customer_id",
#     "brandId": "brand_myntra",
#     "campaignId": "campaign_id",
#     "couponCode": "MYNTRA15",
#     "redeemed": true,
#     "redeemedAt": "2024-11-01T10:30:00Z",
#     "expiresAt": "2024-12-31T23:59:59Z"
#   },
#   "statusCode": 200
# }
```

**6. Submit Feedback**
```bash
curl -X POST http://localhost:8080/api/feedback/submit \
  -H "Content-Type: application/json" \
  -H "Session-Token: YOUR_SESSION_TOKEN" \
  -d '{
    "message": "Great app! Love the deals."
  }'
```

---

### 👨‍💼 Admin APIs

#### Admin Authentication

**1. Admin Login**
```bash
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@bluecollar.com",
    "password": "YourPassword123"
  }'

# Response:
# {
#   "data": {
#     "sessionToken": "admin_session_token_here",
#     "role": "SUPER_ADMIN",
#     "admin": {
#       "id": "admin_id",
#       "email": "admin@bluecollar.com",
#       "name": "Super Admin",
#       "role": "SUPER_ADMIN",
#       "active": true
#     }
#   },
#   "statusCode": 200
# }
```

**2. Admin Logout**
```bash
curl -X POST http://localhost:8080/api/admin/logout \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"
```

**3. Forgot Password (Send OTP)**
```bash
curl -X POST http://localhost:8080/api/admin/forgot-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@bluecollar.com"
  }'

# OTP sent to email
```

**4. Reset Password (Verify OTP)**
```bash
curl -X POST http://localhost:8080/api/admin/reset-password \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@bluecollar.com",
    "otp": "123456",
    "newPassword": "NewPassword123"
  }'
```

---

#### Admin Management (SUPER_ADMIN Only)

**1. Create Admin**
```bash
curl -X POST http://localhost:8080/api/admin/create \
  -H "Content-Type: application/json" \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN" \
  -d '{
    "email": "newadmin@example.com",
    "password": "SecurePass123",
    "name": "John Admin",
    "role": "ADMIN"
  }'

# Available roles: SUPER_ADMIN, ADMIN, DEAL_MANAGER, VIEWER
```

**2. List All Admins**
```bash
curl -X GET http://localhost:8080/api/admin/list \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"

# Note: 
# - SUPER_ADMIN sees all admins
# - ADMIN sees only VIEWERs
```

**3. Update Admin Role**
```bash
curl -X PUT http://localhost:8080/api/admin/admin_id/role \
  -H "Content-Type: application/json" \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN" \
  -d '{
    "role": "DEAL_MANAGER"
  }'

# Restrictions:
# - Cannot change last SUPER_ADMIN's role
# - Cannot create multiple SUPER_ADMINs
# - ADMIN can only update VIEWER roles
```

**4. Update Admin Details**
```bash
curl -X PUT http://localhost:8080/api/admin/admin_id \
  -H "Content-Type: application/json" \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN" \
  -d '{
    "name": "Updated Name",
    "email": "updated@example.com",
    "password": "NewPassword123"
  }'
```

**5. Activate/Deactivate Admin**
```bash
# Activate
curl -X PUT http://localhost:8080/api/admin/admin_id/activate \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"

# Deactivate
curl -X PUT http://localhost:8080/api/admin/admin_id/deactivate \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"

# Restrictions:
# - Cannot deactivate yourself
# - Cannot deactivate last active SUPER_ADMIN
# - ADMIN can only deactivate VIEWERs
```

**6. Delete Admin**
```bash
curl -X DELETE http://localhost:8080/api/admin/admin_id \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"

# Restrictions:
# - Cannot delete last SUPER_ADMIN
# - ADMIN can only delete VIEWERs
```

---

#### Deal Management

**1. Create/Update Home Page**
```bash
curl -X POST http://localhost:8080/api/admin/deals/home \
  -H "Content-Type: application/json" \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN" \
  -d '{
    "banners": [
      {
        "id": "banner1",
        "imageUrl": "https://example.com/banner1.jpg",
        "redirectionLink": "/deals/fashion",
        "categoryId": "fashion",
        "pdpId": null
      }
    ],
    "popularBrands": [
      {
        "id": "brand1",
        "brandName": "Myntra",
        "imageUrl": "https://example.com/myntra.jpg",
        "discount": "15% Off",
        "brandId": "brand_myntra"
      }
    ],
    "categories": [
      {
        "id": "cat1",
        "name": "Fashion",
        "imageUrl": "https://example.com/fashion.jpg",
        "categoryId": "fashion"
      }
    ],
    "handpickedDeals": [...],
    "active": true
  }'
```

**2. Create/Update Category Page (PLP)**
```bash
curl -X POST http://localhost:8080/api/admin/deals/category/fashion \
  -H "Content-Type: application/json" \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN" \
  -d '{
    "title": "Fashion Deals",
    "tabs": ["All Deals", "Popular", "Trending", "Latest"],
    "activeTab": "All Deals",
    "offers": [
      {
        "id": "offer1",
        "brand": "Myntra",
        "discount": "15% Off",
        "discountLabel": "Up to 70% Off",
        "imageUrl": "https://example.com/myntra_offer.jpg",
        "brandId": "brand_myntra"
      }
    ],
    "active": true
  }'
```

**3. Create/Update Brand Page (PDP)**
```bash
curl -X POST http://localhost:8080/api/admin/deals/brand/brand_myntra \
  -H "Content-Type: application/json" \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN" \
  -d '{
    "id": "brand_myntra",
    "categoryId": "fashion",
    "brandName": "Myntra",
    "bannerLink": "https://example.com/myntra_banner.jpg",
    "brandDescription": "India'\''s leading fashion e-commerce platform",
    "discountText": "15% Off on first purchase",
    "validTill": "Valid till: Dec 31, 2024",
    "howItWorksBullets": [
      "Browse products on Myntra app",
      "Add items to cart",
      "Apply coupon at checkout"
    ],
    "benefits": [
      "Flat 15% discount",
      "Free shipping on orders above ₹999",
      "Easy returns within 30 days"
    ],
    "howToRedeemBullets": [
      "Click on Redeem button",
      "Copy the coupon code",
      "Visit Myntra app/website",
      "Apply code at checkout"
    ],
    "termsAndConditions": [
      "Valid for new users only",
      "Cannot be combined with other offers",
      "Expires in 30 days from redemption"
    ],
    "faq": [
      {
        "id": "faq1",
        "question": "Can I use this multiple times?",
        "answer": "No, each coupon can be used only once per user."
      }
    ],
    "redeemLink": "/scratchCards/myntra",
    "active": true
  }'
```

**4. Delete Category/Brand**
```bash
# Delete Category
curl -X DELETE http://localhost:8080/api/admin/deals/category/fashion \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"

# Delete Brand
curl -X DELETE http://localhost:8080/api/admin/deals/brand/brand_myntra \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"
```

**5. List All Deals**
```bash
curl -X GET http://localhost:8080/api/admin/deals/list \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"
```

---

#### Coupon Campaign Management

**1. Create Coupon Campaign**
```bash
curl -X POST http://localhost:8080/api/admin/coupon/create \
  -H "Content-Type: application/json" \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN" \
  -d '{
    "brandId": "brand_myntra",
    "campaignName": "Myntra Diwali Sale 2024",
    "couponCode": "DIWALI15",
    "noOfCoupons": 1000,
    "expiryDays": 30,
    "city": "All India"
  }'

# Response:
# {
#   "data": {
#     "id": "campaign_id",
#     "campaignName": "Myntra Diwali Sale 2024",
#     "couponCode": "DIWALI15",
#     "noOfCoupons": 1000,
#     "remaining": 1000,
#     "expiryDays": 30,
#     "active": true,
#     "createdAt": "2024-11-01T00:00:00Z",
#     "expiresAt": "2024-12-01T23:59:59Z"
#   },
#   "statusCode": 200
# }
```

**2. Update Coupon Campaign**
```bash
curl -X PUT http://localhost:8080/api/admin/coupon/update/campaign_id \
  -H "Content-Type: application/json" \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN" \
  -d '{
    "brandId": "brand_myntra",
    "campaignName": "Myntra Diwali Sale 2024 - Extended",
    "couponCode": "DIWALI20",
    "noOfCoupons": 2000,
    "expiryDays": 45,
    "city": "All India"
  }'
```

**3. Deactivate Coupon Campaign**
```bash
curl -X PUT http://localhost:8080/api/admin/coupon/deactivate/campaign_id \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"
```

**4. Get Campaign Details**
```bash
curl -X GET http://localhost:8080/api/admin/coupon/campaign_id \
  -H "Admin-Session-Token: YOUR_ADMIN_TOKEN"
```

---

## 🔒 Security & Best Practices

### Role-Based Access Control (RBAC)

| Role | Permissions |
|------|------------|
| **SUPER_ADMIN** | Full access: Manage all admins, deals, coupons, system config |
| **ADMIN** | Manage VIEWERs, all deals/coupons, view reports |
| **DEAL_MANAGER** | Manage deals and coupons only |
| **VIEWER** | Read-only access to dashboard and reports |

### Security Features Implemented

✅ **Authentication**
- Session-based authentication (no JWTs in localStorage)
- Secure session tokens (cryptographically random)
- Session expiry management
- OTP verification with expiry (5 minutes)

✅ **Password Security**
- BCrypt hashing (cost factor: 10)
- Configurable default admin password
- Password reset via OTP (email)
- No password in logs/responses

✅ **Input Validation**
- Mobile number format validation
- Email format validation
- Request body validation
- SQL/NoSQL injection prevention

✅ **Role Protection**
- Cannot delete/deactivate last SUPER_ADMIN
- Cannot self-deactivate
- Role hierarchy enforcement
- Permission checks on every admin operation

✅ **Thread Safety**
- Optimistic locking for coupon redemption
- Version-based conflict detection
- Automatic retry mechanism (3 attempts)
- Race condition prevention

✅ **API Security**
- CORS configuration
- Rate limiting (configurable)
- Exception handling (no stack traces exposed)
- Secure headers

### Environment Variables for Production

```bash
# NEVER commit these to git!
export MONGODB_URI="mongodb+srv://user:pass@cluster.mongodb.net/blueplus"
export JWT_SECRET="your-super-secret-key-min-256-bits"
export ADMIN_DEFAULT_PASSWORD="SuperSecurePassword123!"
export TWILIO_ACCOUNT_SID="AC..."
export TWILIO_AUTH_TOKEN="..."
export GOOGLE_OAUTH_CLIENT_ID="..."
export EMAIL_USERNAME="..."
export EMAIL_PASSWORD="..."
```

---

## 📊 Monitoring & Observability

### Spring Boot Actuator Endpoints

```bash
# Health check
curl http://localhost:8080/actuator/health

# Application info
curl http://localhost:8080/actuator/info

# Metrics
curl http://localhost:8080/actuator/metrics

# Prometheus metrics
curl http://localhost:8080/actuator/prometheus
```

### Key Metrics to Monitor

1. **Application Metrics**
   - `http.server.requests` - API request count & latency
   - `jvm.memory.used` - Memory usage
   - `jvm.threads.live` - Active threads

2. **MongoDB Metrics**
   - Connection pool size
   - Query execution time
   - Document count per collection

3. **Business Metrics**
   - Coupon redemption rate
   - Failed redemption count (race conditions)
   - Active user sessions
   - Admin operations count

### Logging

```properties
# Configure log levels
logging.level.org.bluecollar.bluecollar=INFO
logging.level.org.springframework.data.mongodb=WARN
logging.level.org.springframework.security=WARN

# Log patterns
logging.pattern.console=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n
logging.pattern.file=%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{36} - %msg%n

# File logging
logging.file.name=logs/blueplus.log
logging.file.max-size=10MB
logging.file.max-history=30
```

---

## 🐳 Docker Deployment

### Dockerfile
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY build/libs/BluePlus-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Docker Compose
```yaml
version: '3.8'
services:
  blueplus-api:
    build: .
    ports:
      - "8080:8080"
    environment:
      SPRING_DATA_MONGODB_URI: mongodb://mongo:27017/blueplus
      SPRING_PROFILES_ACTIVE: prod
    depends_on:
      - mongo
  
  mongo:
    image: mongo:6.0
    ports:
      - "27017:27017"
    volumes:
      - mongodb_data:/data/db

volumes:
  mongodb_data:
```

### Build & Run
```bash
# Build Docker image
docker build -t blueplus-api:latest .

# Run with Docker Compose
docker-compose up -d

# View logs
docker-compose logs -f blueplus-api

# Stop
docker-compose down
```

---

## 🧪 Testing

### Run Tests
```bash
# Run all tests
./gradlew test

# Run with coverage
./gradlew test jacocoTestReport

# View coverage report
open build/reports/jacoco/test/html/index.html
```

### Manual Testing with cURL

**Test Coupon Redemption Race Condition:**
```bash
# Terminal 1
curl -X POST http://localhost:8080/api/deals/brand/brand_myntra/redeem \
  -H "Session-Token: TOKEN_USER_1" &

# Terminal 2 (immediately)
curl -X POST http://localhost:8080/api/deals/brand/brand_myntra/redeem \
  -H "Session-Token: TOKEN_USER_2" &

# Only ONE should succeed, other gets "All coupons claimed"
```

---

## 📈 Performance Optimization

### Implemented Optimizations

1. ✅ **N+1 Query Prevention**
   - Use in-memory checks for redeemed coupons
   - Reduced DB queries from 10+ to 0 on home page

2. ✅ **Caching**
   - Campaign `remaining` count cached in PDP document
   - No dynamic counting on every request

3. ✅ **Optimistic Locking**
   - Version-based updates prevent dirty reads
   - Automatic retry with exponential backoff

4. ✅ **Index Optimization**
   - MongoDB indexes on frequently queried fields
   - Compound indexes for complex queries

### Recommended Indexes

```javascript
// MongoDB indexes
db.customers.createIndex({ "mobile": 1 }, { unique: true })
db.customers.createIndex({ "email": 1 })
db.admins.createIndex({ "email": 1 }, { unique: true })
db.user_coupons.createIndex({ "customerId": 1, "brandId": 1 })
db.user_coupons.createIndex({ "campaignId": 1 })
db.user_sessions.createIndex({ "sessionToken": 1 }, { unique: true })
db.admin_sessions.createIndex({ "sessionToken": 1 }, { unique: true })
```

---

## 🔧 Troubleshooting

### Common Issues

**1. MongoDB Connection Failed**
```bash
# Check MongoDB is running
docker ps | grep mongo

# Test connection
mongo mongodb://localhost:27017/blueplus
```

**2. SMS Not Sending (Twilio)**
```bash
# Verify credentials
curl -u $TWILIO_ACCOUNT_SID:$TWILIO_AUTH_TOKEN \
  https://api.twilio.com/2010-04-01/Accounts/$TWILIO_ACCOUNT_SID.json

# Check logs
tail -f logs/blueplus.log | grep Twilio
```

**3. Coupon Count Mismatch**
```javascript
// Verify in MongoDB
db.pdp_pages.find({ "campaign.id": "campaign_id" })
db.user_coupons.countDocuments({ "campaignId": "campaign_id" })

// Recalculate remaining
const used = db.user_coupons.countDocuments({ "campaignId": "campaign_id" })
const total = db.pdp_pages.findOne({}).campaign.noOfCoupons
const remaining = total - used
```

**4. Admin Cannot Login**
```javascript
// Reset admin password
db.admins.updateOne(
  { email: "admin@bluecollar.com" },
  { $set: { password: "$2a$10$..." } } // BCrypt hash
)
```

---

## 📞 Support & Contact

- **Documentation**: This README
- **API Docs**: http://localhost:8080/swagger-ui.html
- **Issues**: Create an issue in the repository
- **Email**: support@blueplus.com

---

## 📄 License

This project is licensed under the MIT License - see the LICENSE file for details.

---

## 🎉 Acknowledgments

- Spring Boot Team for the excellent framework
- MongoDB for the flexible database
- Twilio for SMS services
- Google for OAuth & Translation APIs

---

**Built with ❤️ by the BluePlus Team**

**Version**: 1.0.0  
**Last Updated**: November 2024  
**Status**: ✅ Production Ready
