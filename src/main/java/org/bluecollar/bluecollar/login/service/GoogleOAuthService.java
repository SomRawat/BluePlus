package org.bluecollar.bluecollar.login.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class GoogleOAuthService {
    private final JsonFactory jsonFactory;

    public GoogleOAuthService(@Value("${google.oauth.client-id}") String clientId) {
        this.jsonFactory = new GsonFactory();
    }

    public GoogleIdToken.Payload verifyToken(String idTokenString) {
        try {
            // Parse the token without verification first
            GoogleIdToken idToken = GoogleIdToken.parse(jsonFactory, idTokenString);
            GoogleIdToken.Payload payload = idToken.getPayload();

            // Basic validation
            if (payload == null) {
                throw new RuntimeException("Invalid token: payload is null");
            }

            // Check if email is verified
            if (payload.getEmailVerified() == null || !payload.getEmailVerified()) {
                throw new RuntimeException("Email not verified");
            }

            return payload;
        } catch (IOException e) {
            throw new RuntimeException("Failed to parse Google token: " + e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Failed to verify Google token: " + e.getMessage());
        }
    }
}