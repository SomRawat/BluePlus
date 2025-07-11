package org.bluecollar.bluecollar.config;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.ServerApi;
import com.mongodb.ServerApiVersion;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {
    
    private static final Logger logger = LoggerFactory.getLogger(MongoConfig.class);
    
    @Value("${spring.data.mongodb.uri}")
    private String connectionString;
    
    @Value("${spring.data.mongodb.database}")
    private String database;
    
    @Override
    protected String getDatabaseName() {
        return database;
    }
    
    @Override
    @Bean
    public MongoClient mongoClient() {
        try {
            ServerApi serverApi = ServerApi.builder()
                    .version(ServerApiVersion.V1)
                    .build();
            
            MongoClientSettings settings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(connectionString))
                    .serverApi(serverApi)
                    .build();
            
            MongoClient client = MongoClients.create(settings);
            logger.info("MongoDB client created successfully with ServerApi V1");
            return client;
        } catch (Exception e) {
            logger.error("Failed to create MongoDB client: ", e);
            throw e;
        }
    }
}