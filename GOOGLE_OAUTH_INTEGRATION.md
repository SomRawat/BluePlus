# Google OAuth Integration Guide

## Overview
Complete production-ready Google OAuth integration with token verification, user management, and session handling.

## Configuration

### 1. Environment Variables (application-prod.properties)
```properties
# Google OAuth Configuration
google.oauth.client-id=${GOOGLE_CLIENT_ID:your-google-client-id}
google.oauth.client-secret=${GOOGLE_CLIENT_SECRET:your-google-client-secret}
```

### 2. Google Cloud Console Setup
1. Go to [Google Cloud Console](https://console.cloud.google.com/)
2. Create/Select project
3. Enable Google+ API
4. Create OAuth 2.0 credentials
5. Add authorized origins: `http://localhost:8080`, `https://yourdomain.com`

## API Endpoints

### Google Authentication
```bash
POST /api/auth/google-auth
Content-Type: application/json
Api-Client: web|mobile

{
  "idToken": "google-id-token-from-frontend"
}
```

### Response
```json
{
  "result": {
    "token": "session-token-here",
    "isFirstTime": true,
    "customerId": "customer-id",
    "message": "Google login successful"
  },
  "status": 200
}
```

## Frontend Integration

### Web (JavaScript)
```javascript
// 1. Load Google API
<script src="https://apis.google.com/js/api:client.js"></script>

// 2. Initialize Google Auth
gapi.load('auth2', function() {
  gapi.auth2.init({
    client_id: 'YOUR_GOOGLE_CLIENT_ID'
  });
});

// 3. Handle Sign In
function signInWithGoogle() {
  const authInstance = gapi.auth2.getAuthInstance();
  authInstance.signIn().then(function(googleUser) {
    const idToken = googleUser.getAuthResponse().id_token;
    
    // Send to backend
    fetch('/api/auth/google-auth', {
      method: 'POST',
      headers: {
        'Content-Type': 'application/json',
        'Api-Client': 'web'
      },
      body: JSON.stringify({ idToken: idToken })
    })
    .then(response => response.json())
    .then(data => {
      if (data.status === 200) {
        localStorage.setItem('sessionToken', data.result.token);
        // Redirect to dashboard
      }
    });
  });
}
```

### Mobile (Android)
```kotlin
// 1. Add dependency in build.gradle
implementation 'com.google.android.gms:play-services-auth:20.7.0'

// 2. Configure Google Sign In
val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
    .requestIdToken("YOUR_GOOGLE_CLIENT_ID")
    .requestEmail()
    .build()

val googleSignInClient = GoogleSignIn.getClient(this, gso)

// 3. Handle Sign In
private fun signInWithGoogle() {
    val signInIntent = googleSignInClient.signInIntent
    startActivityForResult(signInIntent, RC_SIGN_IN)
}

override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
    super.onActivityResult(requestCode, resultCode, data)
    
    if (requestCode == RC_SIGN_IN) {
        val task = GoogleSignIn.getSignedInAccountFromIntent(data)
        val account = task.getResult(ApiException::class.java)
        val idToken = account.idToken
        
        // Send to backend
        authenticateWithBackend(idToken)
    }
}
```

## Security Features

### Token Verification
- ✅ Google ID token signature verification
- ✅ Audience validation (client ID)
- ✅ Email verification status check
- ✅ Token expiry validation

### User Management
- ✅ Unique Google ID indexing
- ✅ Email and name updates on login
- ✅ First-time user detection
- ✅ Session management with client-specific expiry

### Error Handling
- ✅ Invalid token handling
- ✅ Network error handling
- ✅ Duplicate account prevention
- ✅ Comprehensive error messages

## Testing

### Manual Testing
```bash
# Test with valid Google ID token
curl -X POST http://localhost:8080/api/auth/google-auth \
  -H "Content-Type: application/json" \
  -H "Api-Client: web" \
  -d '{"idToken":"VALID_GOOGLE_ID_TOKEN"}'

# Test with invalid token
curl -X POST http://localhost:8080/api/auth/google-auth \
  -H "Content-Type: application/json" \
  -H "Api-Client: mobile" \
  -d '{"idToken":"invalid-token"}'

# Test without token
curl -X POST http://localhost:8080/api/auth/google-auth \
  -H "Content-Type: application/json" \
  -d '{}'
```

## Production Deployment

### Environment Setup
```bash
# Set environment variables
export GOOGLE_CLIENT_ID="your-production-client-id"
export GOOGLE_CLIENT_SECRET="your-production-client-secret"

# Build and deploy
./gradlew build
docker-compose up -d
```

### Monitoring
- Health check: `GET /actuator/health`
- Metrics: `GET /actuator/metrics`
- Google auth success rate monitoring
- Failed authentication alerts

## Database Schema

### Customer Collection
```javascript
{
  "_id": "ObjectId",
  "mobile": "9876543210", // Optional for Google users
  "email": "user@gmail.com",
  "name": "John Doe",
  "googleId": "google-user-id", // Unique, indexed
  "address": "User Address",
  "city": "Mumbai",
  "state": "Maharashtra",
  "pincode": "400001",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

## Error Codes

| Code | Message | Description |
|------|---------|-------------|
| 400 | Google ID token is required | Missing token in request |
| 400 | Invalid Google ID token | Token verification failed |
| 400 | Google email not verified | Email not verified by Google |
| 400 | Google authentication failed | General auth failure |
| 500 | Internal server error | Server-side error |

## Best Practices

1. **Token Security**: Never log or store Google ID tokens
2. **Session Management**: Use secure session tokens with appropriate expiry
3. **Error Handling**: Provide user-friendly error messages
4. **Rate Limiting**: Implement rate limiting for auth endpoints
5. **Monitoring**: Track authentication success/failure rates
6. **Fallback**: Provide alternative login methods (OTP)