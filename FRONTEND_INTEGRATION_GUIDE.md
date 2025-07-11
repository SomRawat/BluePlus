# BlueCollar API - Frontend Integration Guide

## Base URL
```
Production: https://your-domain.com
Development: http://localhost:8080
```

## Authentication Required
All APIs except authentication endpoints require `Session-Token` header.

---

## 1. Authentication APIs

### Send OTP
```http
POST /api/auth/send-otp
Content-Type: application/json

{
  "mobile": "9876543210"
}
```

**Response:**
```json
{
  "result": "OTP sent successfully",
  "status": 200
}
```

### Verify OTP & Get Session Token
```http
POST /api/auth/verify-otp
Content-Type: application/json
Api-Client: web|android|ios

{
  "mobile": "9876543210",
  "otp": "123456"
}
```

**Response:**
```json
{
  "result": {
    "token": "uuid-session-token-here",
    "isFirstTime": true,
    "customerId": "customer-id",
    "message": "Login successful"
  },
  "status": 200
}
```

**Store the `token` for all subsequent API calls.**

---

## 2. Deals System APIs

### Home Page Data
```http
GET /api/deals/home
```

**Response:**
```json
{
  "result": {
    "banners": [
      {
        "id": "12323dasdasd",
        "imageUrl": "https://example.com/banner.jpg",
        "redirectionLink": "/dealDetails/uniqueID"
      }
    ],
    "popularBrands": [
      {
        "name": "Vodafone",
        "discount": "Upto 12% Off",
        "imageUrl": "https://example.com/vodafone.jpg",
        "redirectionLink": "/dealDetails/uniqueID"
      }
    ],
    "handpickedDeals": [
      {
        "id": "aassdssss",
        "imageUrl": "https://example.com/deal.jpg",
        "redirectionLink": "/dealDetails/uniqueID"
      }
    ],
    "categories": [
      {
        "id": "cat123",
        "label": "Fashion",
        "imageUrl": "https://example.com/fashion.jpg",
        "redirectionLink": "/categories/uniqueID"
      }
    ]
  },
  "status": 200
}
```

### Brand Details (PDP)
```http
GET /api/deals/brand/{brandId}
```

**Response:**
```json
{
  "result": {
    "brandName": "Myntra",
    "bannerLink": "https://example.com/myntra-banner.jpg",
    "brandDescription": "Myntra is a one stop shop for all your fashion needs...",
    "discountText": "15% off Lorem ipsum dolor sit amet.",
    "validTill": "Valid till: Jun 15, 2025",
    "howItWorksBullets": [
      "Lorem ipsum dolor sit amet, consectetur adi lit.",
      "Lorem ipsum dolor sit amet, consectetur adi lit."
    ],
    "benefits": [
      "Lorem ipsum dolor sit amet, consectetur adi lit amet.",
      "Lorem ipsum dolor sit amet, consectetur adi lit amet."
    ],
    "howToRedeemBullets": [
      "Lorem ipsum dolor sit amet, consectetur adi lit.",
      "Lorem ipsum dolor sit amet, consectetur adi lit."
    ],
    "termsAndConditions": [
      "Lorem ipsum dolor sit amet, consectetur adi lit.",
      "Lorem ipsum dolor sit amet, consectetur adi lit."
    ],
    "faq": [
      {
        "question": "Lorem ipsum dolor sit amet?",
        "answer": "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
      }
    ],
    "redeemLink": "/scratchCards/id"
  },
  "status": 200
}
```

### Category Deals
```http
GET /api/deals/category/{categoryId}?tab=Popular
```

**Available tabs:** `All Deals`, `Nearby`, `Popular`, `Trending`, `Latest`, `Top Rated`

**Response:**
```json
{
  "result": {
    "title": "Fashion",
    "tabs": ["All Deals", "Nearby", "Popular", "Trending", "Latest", "Top Rated"],
    "activeTab": "Popular",
    "offers": [
      {
        "id": "2132",
        "brand": "First Fashion",
        "discount": "17% Off",
        "discountLabel": "On clothing",
        "imageUrl": "https://example.com/offer.jpg"
      }
    ]
  },
  "status": 200
}
```

---

## 3. Coupon Management APIs

### Create Coupon
```http
POST /api/coupons/create
Content-Type: application/json
Session-Token: your-session-token

{
  "brandId": "brand-id-here",
  "city": "Mumbai",
  "expiryDays": 30
}
```

**Response:**
```json
{
  "result": {
    "id": "coupon-id",
    "couponCode": "BC12345678",
    "brandName": "Myntra",
    "city": "Mumbai",
    "expiresAt": "2024-02-15T10:30:00",
    "redeemed": false,
    "message": "Coupon created successfully"
  },
  "status": 200
}
```

### Get Active Coupons
```http
GET /api/coupons/active
Session-Token: your-session-token
```

**Response:**
```json
{
  "result": [
    {
      "id": "coupon-id",
      "couponCode": "BC12345678",
      "brandName": "Myntra",
      "city": "Mumbai",
      "expiresAt": "2024-02-15T10:30:00",
      "redeemed": false
    }
  ],
  "status": 200
}
```

### Redeem Coupon
```http
POST /api/coupons/redeem/{couponCode}
Session-Token: your-session-token
```

**Response:**
```json
{
  "result": {
    "id": "coupon-id",
    "couponCode": "BC12345678",
    "brandName": "Myntra",
    "city": "Mumbai",
    "expiresAt": "2024-02-15T10:30:00",
    "redeemed": true,
    "redeemedAt": "2024-01-20T14:30:00",
    "message": "Coupon redeemed successfully"
  },
  "status": 200
}
```

### Get All User Coupons
```http
GET /api/coupons/my-coupons
Session-Token: your-session-token
```

**Response:** Same as active coupons but includes redeemed ones.

---

## 4. Payment APIs

### Create Payment
```http
POST /api/payment/create
Content-Type: application/json
Session-Token: your-session-token

{
  "amount": 100,
  "description": "Service payment"
}
```

### Verify Payment
```http
POST /api/payment/verify
Content-Type: application/json

{
  "razorpayOrderId": "order_id",
  "razorpayPaymentId": "payment_id",
  "razorpaySignature": "signature"
}
```

---

## 5. Error Handling

### Error Response Format
```json
{
  "result": null,
  "status": 400,
  "traceId": "trace-id-for-debugging"
}
```

### Common Error Codes
- `400` - Bad Request (validation failed, business rule violation)
- `401` - Unauthorized (invalid/expired session)
- `404` - Not Found (resource doesn't exist)
- `500` - Internal Server Error

### Session Token Errors
```json
{
  "result": null,
  "status": 400
}
```
**Action:** Redirect user to login page.

---

## 6. Frontend Implementation Notes

### Session Management
```javascript
// Store token after login
localStorage.setItem('sessionToken', response.result.token);

// Add to all API calls
const headers = {
  'Content-Type': 'application/json',
  'Session-Token': localStorage.getItem('sessionToken')
};
```

### Client Type Header
```javascript
// For login APIs
const headers = {
  'Content-Type': 'application/json',
  'Api-Client': 'web' // or 'android', 'ios'
};
```

### Coupon Rules
1. **One coupon per user per brand** - Check before creating
2. **City-based coupons** - Always send user's city
3. **Expiry validation** - Check `expiresAt` before showing
4. **One-time redemption** - Disable redeem button after use

### Date Handling
All dates are in ISO format: `2024-01-20T14:30:00`
```javascript
const expiryDate = new Date(coupon.expiresAt);
const isExpired = expiryDate < new Date();
```

### Image Handling
All `imageUrl` fields contain full URLs ready for display.

### Navigation
Use `redirectionLink` fields for internal navigation:
- `/dealDetails/{id}` - Navigate to brand PDP
- `/categories/{id}` - Navigate to category page
- `/scratchCards/{id}` - Navigate to coupon redemption

---

## 7. Testing Endpoints

### Development Server
```bash
# Start server
gradle bootRun

# Test endpoints
curl -X GET http://localhost:8080/api/deals/home
```

### Sample Test Flow
1. Send OTP to `9876543210`
2. Verify with OTP `123456` (hardcoded for testing)
3. Use returned token for authenticated APIs
4. Create coupon for any brand
5. Redeem using coupon code

---

## 8. Production Considerations

### Rate Limiting
- OTP requests: 5 per minute per mobile
- API requests: 100 per minute per session

### Session Expiry
- **Mobile apps**: 30 days
- **Web apps**: 3 days

### CORS
Configured for all origins in development. Update for production domains.

### Health Check
```http
GET /actuator/health
```

---

## Support
For integration issues, contact the backend team with:
- API endpoint used
- Request payload
- Response received
- Expected behavior