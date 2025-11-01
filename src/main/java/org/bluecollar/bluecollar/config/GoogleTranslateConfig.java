package org.bluecollar.bluecollar.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Configuration
public class GoogleTranslateConfig {
    
    @Value("${google.translate.credentials-json}")
    private String credentialsJson;
    
    @Value("${google.translate.project-id}")
    private String projectId;
    
    @Bean
    public Translate googleTranslate() throws IOException {
        String processedJson = credentialsJson.replace("\\n", "\n");
        GoogleCredentials credentials = GoogleCredentials
            .fromStream(new ByteArrayInputStream(processedJson.getBytes(StandardCharsets.UTF_8)));
        
        return TranslateOptions.newBuilder()
            .setCredentials(credentials)
            .setProjectId(projectId)
            .build()
            .getService();
    }
}
