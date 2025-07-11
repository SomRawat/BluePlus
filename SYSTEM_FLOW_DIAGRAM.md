# BlueCollar API - Complete System Flow Diagram

## 1. Authentication Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │    │ AuthController│   │ AuthService │    │  MongoDB    │
└──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘
       │                  │                  │                  │
       │ POST /send-otp   │                  │                  │
       ├─────────────────►│                  │                  │
       │                  │ sendOtp()        │                  │
       │                  ├─────────────────►│                  │
       │                  │                  │ save(otpSession) │
       │                  │                  ├─────────────────►│
       │                  │                  │                  │
       │ "OTP sent"       │                  │                  │
       │◄─────────────────┤                  │                  │
       │                  │                  │                  │
       │ POST /verify-otp │                  │                  │
       ├─────────────────►│                  │                  │
       │                  │ verifyOtp()      │                  │
       │                  ├─────────────────►│                  │
       │                  │                  │ validate & save  │
       │                  │                  ├─────────────────►│
       │                  │                  │ createSession()  │
       │                  │                  ├─────────────────►│
       │                  │ LoginResponse    │                  │
       │                  │◄─────────────────┤                  │
       │ {token, customerId}                 │                  │
       │◄─────────────────┤                  │                  │
```

## 2. Deals System Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │    │DealsController│   │DealsService │    │  MongoDB    │
└──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘
       │                  │                  │                  │
       │ GET /deals/home  │                  │                  │
       ├─────────────────►│                  │                  │
       │                  │ getHomePage()    │                  │
       │                  ├─────────────────►│                  │
       │                  │                  │ find banners     │
       │                  │                  ├─────────────────►│
       │                  │                  │ find brands      │
       │                  │                  ├─────────────────►│
       │                  │                  │ find categories  │
       │                  │                  ├─────────────────►│
       │                  │ HomePageResponse │                  │
       │                  │◄─────────────────┤                  │
       │ {banners, brands,│                  │                  │
       │  deals, categories}                 │                  │
       │◄─────────────────┤                  │                  │
       │                  │                  │                  │
       │GET /brand/{id}   │                  │                  │
       ├─────────────────►│                  │                  │
       │                  │getBrandDetails() │                  │
       │                  ├─────────────────►│                  │
       │                  │                  │ findById(brand)  │
       │                  │                  ├─────────────────►│
       │                  │BrandDetailsResp  │                  │
       │                  │◄─────────────────┤                  │
       │ {PDP details,    │                  │                  │
       │  FAQ, T&C}       │                  │                  │
       │◄─────────────────┤                  │                  │
```

## 3. Coupon Management Flow
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   Client    │    │CouponController│  │CouponService│    │  MongoDB    │
└──────┬──────┘    └──────┬──────┘    └──────┬──────┘    └──────┬──────┘
       │                  │                  │                  │
       │POST /coupons/create                 │                  │
       │+ Session-Token    │                  │                  │
       ├─────────────────►│                  │                  │
       │                  │ validateSession()│                  │
       │                  ├─────────────────►│                  │
       │                  │ createCoupon()   │                  │
       │                  ├─────────────────►│                  │
       │                  │                  │ check existing   │
       │                  │                  ├─────────────────►│
       │                  │                  │ generate code    │
       │                  │                  │ save(coupon)     │
       │                  │                  ├─────────────────►│
       │                  │ CouponResponse   │                  │
       │                  │◄─────────────────┤                  │
       │ {couponCode,     │                  │                  │
       │  expiresAt}      │                  │                  │
       │◄─────────────────┤                  │                  │
       │                  │                  │                  │
       │POST /redeem/{code}                  │                  │
       │+ Session-Token    │                  │                  │
       ├─────────────────►│                  │                  │
       │                  │ redeemCoupon()   │                  │
       │                  ├─────────────────►│                  │
       │                  │                  │ validate coupon  │
       │                  │                  ├─────────────────►│
       │                  │                  │ check ownership  │
       │                  │                  │ check expiry     │
       │                  │                  │ mark redeemed    │
       │                  │                  ├─────────────────►│
       │                  │ "Redeemed"       │                  │
       │                  │◄─────────────────┤                  │
       │ Success          │                  │                  │
       │◄─────────────────┤                  │                  │
```

## 4. Complete Data Flow Architecture
```
                    ┌─────────────────────────────────────┐
                    │           CLIENT APPS               │
                    │  Mobile (30d) │ Web (3d) │ Admin   │
                    └─────────────┬───────────────────────┘
                                  │ HTTP Requests
                                  │ Session-Token Header
                    ┌─────────────▼───────────────────────┐
                    │        SPRING BOOT API              │
                    │                                     │
                    │ ┌─────────┐ ┌─────────┐ ┌─────────┐ │
                    │ │  Auth   │ │ Deals   │ │Coupons  │ │
                    │ │Controller│ │Controller│ │Controller│ │
                    │ └─────────┘ └─────────┘ └─────────┘ │
                    │      │           │           │      │
                    │ ┌─────────┐ ┌─────────┐ ┌─────────┐ │
                    │ │  Auth   │ │ Deals   │ │Coupons  │ │
                    │ │ Service │ │ Service │ │ Service │ │
                    │ └─────────┘ └─────────┘ └─────────┘ │
                    │      │           │           │      │
                    │ ┌─────────┐ ┌─────────┐ ┌─────────┐ │
                    │ │Session  │ │ Payment │ │  JWT    │ │
                    │ │ Service │ │ Service │ │ Service │ │
                    │ └─────────┘ └─────────┘ └─────────┘ │
                    └─────────────┬───────────────────────┘
                                  │ MongoDB Queries
                    ┌─────────────▼───────────────────────┐
                    │          MONGODB ATLAS              │
                    │                                     │
                    │ customers    │ user_sessions        │
                    │ banners      │ brands               │
                    │ categories   │ coupons              │
                    │ payments     │ admins               │
                    │ otp_sessions │                      │
                    └─────────────────────────────────────┘
```

## 5. Coupon Lifecycle States
```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│   CREATED   │───►│   ACTIVE    │───►│  REDEEMED   │───►│   EXPIRED   │
│             │    │             │    │             │    │             │
│ • Generated │    │ • Valid     │    │ • Used once │    │ • Past date │
│ • Saved DB  │    │ • Not used  │    │ • Timestamp │    │ • Cleanup   │
│ • City set  │    │ • In expiry │    │ • No reuse  │    │             │
└─────────────┘    └─────────────┘    └─────────────┘    └─────────────┘
```

## 6. Session Management Flow
```
Login ──► Session Created ──► Token Generated ──► Client Stores Token
  │              │                    │                      │
  │              │                    │                      │
  ▼              ▼                    ▼                      ▼
Mobile      30 Days Expiry      UUID Token            Header: Session-Token
  │              │                    │                      │
  │              │                    │                      │
  ▼              ▼                    ▼                      ▼
Web         3 Days Expiry       Stored in DB          API Validation
```

## 7. Error Handling Flow
```
Request ──► Validation ──► Business Logic ──► Database ──► Response
   │            │               │              │            │
   │            │               │              │            │
   ▼            ▼               ▼              ▼            ▼
Invalid    Validation      Business Rule   DB Error    Error Response
Input      Failed          Violation       Occurred    (Status 400)
   │            │               │              │            │
   │            │               │              │            │
   ▼            ▼               ▼              ▼            ▼
400 Bad    400 Bad         400 Bad        500 Internal   JSON Error
Request    Request         Request        Server Error   Message
```