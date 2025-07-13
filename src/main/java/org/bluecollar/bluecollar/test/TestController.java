package org.bluecollar.bluecollar.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {
    
    @Autowired
    private Environment env;
    
    @GetMapping("/google-config")
    public Map<String, Object> getGoogleConfig() {
        Map<String, Object> config = new HashMap<>();
        String clientId = env.getProperty("google.oauth.client-id");
        
        config.put("clientIdConfigured", clientId != null && !clientId.contains("your-google-client-id"));
        config.put("clientIdMasked", clientId != null ? clientId.substring(0, Math.min(10, clientId.length())) + "..." : "Not configured");
        config.put("message", "Google OAuth configuration status");
        
        return config;
    }
}