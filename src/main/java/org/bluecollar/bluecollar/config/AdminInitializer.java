package org.bluecollar.bluecollar.config;

import org.bluecollar.bluecollar.admin.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AdminInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminInitializer.class);
    
    private final AdminService adminService;
    
    @Autowired
    public AdminInitializer(AdminService adminService) {
        this.adminService = adminService;
    }
    
    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing default admin user...");
        try {
            adminService.initializeDefaultAdmin();
            logger.info("Default admin user initialized successfully");
        } catch (Exception e) {
            logger.error("Failed to initialize default admin user: {}", e.getMessage(), e);
        }
    }
}
