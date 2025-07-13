# Complete Google OAuth Implementation

## Overview
This implementation provides two ways to authenticate with Google:
1. **Direct ID Token** - Frontend gets ID token and sends to backend
2. **Authorization Code Flow** - Backend handles complete OAuth flow

## API Endpoints

### 1. Get Google Authorization URL
```bash
GET /api/auth/google/auth-url?redirectUri=http://localhost:8080/callback&state=random123
```

**Response:**
```json
{
  "result": {
    "authUrl": "https://accounts.google.com/oauth/authorize?client_id=...&redirect_uri=...",
    "message": "Google authorization URL generated"
  },
  "status": 200
}
```

### 2. Handle Google Callback (Authorization Code)
```bash
POST /api/auth/google/callback
Content-Type: application/json
Api-Client: web

{
  "code": "authorization-code-from-google",
  "redirectUri": "http://localhost:8080/callback"
}
```

### 3. Direct Google ID Token Authentication
```bash
POST /api/auth/google-auth
Content-Type: application/json
Api-Client: web

{
  "idToken": "google-id-token-from-frontend"
}
```

## Complete Implementation Examples

### Method 1: Authorization Code Flow (Recommended)

#### Frontend (JavaScript)
```javascript
// Step 1: Get authorization URL from backend
async function initiateGoogleLogin() {
    const redirectUri = window.location.origin + '/callback';
    const state = Math.random().toString(36).substring(7);
    
    const response = await fetch(`/api/auth/google/auth-url?redirectUri=${redirectUri}&state=${state}`);
    const data = await response.json();
    
    // Store state for verification
    localStorage.setItem('oauth_state', state);
    
    // Redirect user to Google
    window.location.href = data.result.authUrl;
}

// Step 2: Handle callback (on /callback page)
async function handleGoogleCallback() {
    const urlParams = new URLSearchParams(window.location.search);
    const code = urlParams.get('code');
    const state = urlParams.get('state');
    const error = urlParams.get('error');
    
    if (error) {
        console.error('Google OAuth error:', error);
        return;
    }
    
    // Verify state
    const storedState = localStorage.getItem('oauth_state');
    if (state !== storedState) {
        console.error('Invalid state parameter');
        return;
    }
    
    // Exchange code for session token
    const response = await fetch('/api/auth/google/callback', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Api-Client': 'web'
        },
        body: JSON.stringify({
            code: code,
            redirectUri: window.location.origin + '/callback'
        })
    });
    
    const data = await response.json();
    
    if (data.status === 200) {
        localStorage.setItem('sessionToken', data.result.token);
        localStorage.removeItem('oauth_state');
        
        // Redirect to dashboard
        window.location.href = '/dashboard';
    } else {
        console.error('Authentication failed:', data.result);
    }
}

// Call this on callback page load
if (window.location.pathname === '/callback') {
    handleGoogleCallback();
}
```

#### HTML Pages
```html
<!-- login.html -->
<!DOCTYPE html>
<html>
<head>
    <title>Login</title>
</head>
<body>
    <h2>Login to BlueCollar</h2>
    <button onclick="initiateGoogleLogin()">Sign in with Google</button>
    
    <script>
        async function initiateGoogleLogin() {
            const redirectUri = window.location.origin + '/callback.html';
            const state = Math.random().toString(36).substring(7);
            
            const response = await fetch(`/api/auth/google/auth-url?redirectUri=${redirectUri}&state=${state}`);
            const data = await response.json();
            
            localStorage.setItem('oauth_state', state);
            window.location.href = data.result.authUrl;
        }
    </script>
</body>
</html>

<!-- callback.html -->
<!DOCTYPE html>
<html>
<head>
    <title>Processing...</title>
</head>
<body>
    <h2>Processing login...</h2>
    
    <script>
        async function handleCallback() {
            const urlParams = new URLSearchParams(window.location.search);
            const code = urlParams.get('code');
            const state = urlParams.get('state');
            
            if (!code) {
                document.body.innerHTML = '<h2>Login failed</h2>';
                return;
            }
            
            const storedState = localStorage.getItem('oauth_state');
            if (state !== storedState) {
                document.body.innerHTML = '<h2>Security error</h2>';
                return;
            }
            
            try {
                const response = await fetch('/api/auth/google/callback', {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        'Api-Client': 'web'
                    },
                    body: JSON.stringify({
                        code: code,
                        redirectUri: window.location.origin + '/callback.html'
                    })
                });
                
                const data = await response.json();
                
                if (data.status === 200) {
                    localStorage.setItem('sessionToken', data.result.token);
                    localStorage.removeItem('oauth_state');
                    window.location.href = '/dashboard.html';
                } else {
                    document.body.innerHTML = '<h2>Login failed: ' + data.result + '</h2>';
                }
            } catch (error) {
                document.body.innerHTML = '<h2>Network error</h2>';
            }
        }
        
        handleCallback();
    </script>
</body>
</html>
```

### Method 2: Direct ID Token (Alternative)

#### Frontend with Google Sign-In Library
```html
<!DOCTYPE html>
<html>
<head>
    <title>Google Sign-In</title>
    <script src="https://accounts.google.com/gsi/client" async defer></script>
</head>
<body>
    <div id="g_id_onload"
         data-client_id="YOUR_GOOGLE_CLIENT_ID"
         data-callback="handleCredentialResponse">
    </div>
    <div class="g_id_signin" data-type="standard"></div>
    
    <script>
        function handleCredentialResponse(response) {
            // response.credential contains the ID token
            fetch('/api/auth/google-auth', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Api-Client': 'web'
                },
                body: JSON.stringify({
                    idToken: response.credential
                })
            })
            .then(response => response.json())
            .then(data => {
                if (data.status === 200) {
                    localStorage.setItem('sessionToken', data.result.token);
                    window.location.href = '/dashboard.html';
                }
            });
        }
    </script>
</body>
</html>
```

### Mobile Implementation (Android)

```kotlin
// MainActivity.kt
class MainActivity : AppCompatActivity() {
    
    private lateinit var googleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Configure Google Sign In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.google_client_id))
            .requestEmail()
            .build()
            
        googleSignInClient = GoogleSignIn.getClient(this, gso)
        
        findViewById<Button>(R.id.sign_in_button).setOnClickListener {
            signIn()
        }
    }
    
    private fun signIn() {
        val signInIntent = googleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }
    
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)!!
                authenticateWithBackend(account.idToken!!)
            } catch (e: ApiException) {
                Log.w("GoogleSignIn", "signInResult:failed code=" + e.statusCode)
            }
        }
    }
    
    private fun authenticateWithBackend(idToken: String) {
        val client = OkHttpClient()
        val json = JSONObject().put("idToken", idToken)
        
        val body = RequestBody.create(
            "application/json; charset=utf-8".toMediaType(),
            json.toString()
        )
        
        val request = Request.Builder()
            .url("https://your-api.com/api/auth/google-auth")
            .post(body)
            .addHeader("Api-Client", "mobile")
            .build()
            
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                val responseData = response.body?.string()
                // Handle success - save session token
            }
            
            override fun onFailure(call: Call, e: IOException) {
                // Handle error
            }
        })
    }
}
```

## Testing Commands

```bash
# Test authorization URL generation
curl -X GET "http://localhost:8080/api/auth/google/auth-url?redirectUri=http://localhost:8080/callback&state=test123"

# Test callback with authorization code (replace with actual code)
curl -X POST http://localhost:8080/api/auth/google/callback \
  -H "Content-Type: application/json" \
  -H "Api-Client: web" \
  -d '{"code":"4/0AX4XfWh...","redirectUri":"http://localhost:8080/callback"}'

# Test direct ID token (replace with actual token)
curl -X POST http://localhost:8080/api/auth/google-auth \
  -H "Content-Type: application/json" \
  -H "Api-Client: mobile" \
  -d '{"idToken":"eyJhbGciOiJSUzI1NiIs..."}'

# Check configuration
curl -X GET http://localhost:8080/api/test/google-config
```

## Security Features

- ✅ State parameter validation (CSRF protection)
- ✅ Redirect URI validation
- ✅ ID token signature verification
- ✅ Email verification requirement
- ✅ Client-specific session management
- ✅ Comprehensive error handling
- ✅ Rate limiting ready

## Production Deployment

1. Set environment variables:
```bash
export GOOGLE_CLIENT_ID="your-production-client-id"
export GOOGLE_CLIENT_SECRET="your-production-client-secret"
```

2. Configure authorized redirect URIs in Google Console:
```
https://yourdomain.com/callback
https://yourdomain.com/callback.html
```

3. Build and deploy:
```bash
./gradlew build
docker-compose up -d
```

This implementation provides a complete, production-ready Google OAuth flow with both authorization code and direct ID token methods.