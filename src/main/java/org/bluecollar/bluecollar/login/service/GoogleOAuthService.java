package org.bluecollar.bluecollar.login.service;

import com.google.api.client.googleapis.auth.oauth2.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.*;

@Service
public class GoogleOAuthService {
    
    @Value("${google.oauth.client-id}")
    private String clientId;
    
    @Value("${google.oauth.client-secret}")
    private String clientSecret;
    
    private final GoogleIdTokenVerifier verifier;
    private final RestTemplate restTemplate;
    
    public GoogleOAuthService(@Value("${google.oauth.client-id}") String clientId) {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
        this.restTemplate = new RestTemplate();
    }
    
    public GoogleIdToken.Payload verifyToken(String idTokenString) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken != null) {
                return idToken.getPayload();
            }
            throw new RuntimeException("Invalid Google ID token");
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google token: " + e.getMessage());
        }
    }
    
    public GoogleIdToken.Payload exchangeCodeForToken(String code, String redirectUri) {
        try {
            GoogleTokenResponse tokenResponse = new GoogleAuthorizationCodeTokenRequest(
                new NetHttpTransport(),
                new GsonFactory(),
                "https://oauth2.googleapis.com/token",
                clientId,
                clientSecret,
                code,
                redirectUri
            ).execute();
            
            String idTokenString = tokenResponse.getIdToken();
            return verifyToken(idTokenString);
            
        } catch (Exception e) {
            throw new RuntimeException("Failed to exchange code for token: " + e.getMessage());
        }
    }
    
    public String getAuthorizationUrl(String redirectUri, String state) {
        GoogleAuthorizationCodeRequestUrl url = new GoogleAuthorizationCodeRequestUrl(
            clientId,
            redirectUri,
            Arrays.asList("openid", "email", "profile")
        );
        
        if (state != null) {
            url.setState(state);
        }
        
        return url.build();
    }
    
    public Map<String, Object> getUserInfo(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);
            HttpEntity<String> entity = new HttpEntity<>(headers);
            
            ResponseEntity<Map> response = restTemplate.exchange(
                "https://www.googleapis.com/oauth2/v2/userinfo",
                HttpMethod.GET,
                entity,
                Map.class
            );
            
            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException("Failed to get user info: " + e.getMessage());
        }
    }
}