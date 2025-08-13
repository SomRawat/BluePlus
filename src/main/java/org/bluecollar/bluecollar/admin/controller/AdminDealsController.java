package org.bluecollar.bluecollar.admin.controller;

import jakarta.validation.Valid;
import org.bluecollar.bluecollar.admin.service.AdminSessionService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.common.exception.UnauthorizedException;
import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.service.DealsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/deals")
@Validated
public class AdminDealsController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminDealsController.class);
    
    private final DealsService dealsService;
    private final AdminSessionService sessionService;
    
    @Autowired
    public AdminDealsController(DealsService dealsService, AdminSessionService sessionService) {
        this.dealsService = dealsService;
        this.sessionService = sessionService;
    }
    
    @PostMapping("/home")
    public BlueCollarApiResponse<HomePageResponse> createHomePage(@Valid @RequestBody HomePageData homePageData,
                                                                 @RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Creating home page data");
        try {
            sessionService.validateCanManageDeals(sessionToken);
            HomePageResponse response = dealsService.createHomePage(homePageData);
            logger.info("Home page data created successfully");
            return new BlueCollarApiResponse<>(response, 200);
        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized attempt to create home page: {}", e.getMessage());
            return new BlueCollarApiResponse<HomePageResponse>(null, 401);
        } catch (Exception e) {
            logger.error("Error creating home page: {}", e.getMessage(), e);
            return new BlueCollarApiResponse<HomePageResponse>(null, 500);
        }
    }
    
    @PutMapping("/home")
    public BlueCollarApiResponse<HomePageResponse> updateHomePage(@Valid @RequestBody HomePageData homePageData,
                                                                 @RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Updating home page data");
        try {
            sessionService.validateCanManageDeals(sessionToken);
            HomePageResponse response = dealsService.updateHomePage(homePageData);
            logger.info("Home page data updated successfully");
            return new BlueCollarApiResponse<>(response, 200);
        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized attempt to update home page: {}", e.getMessage());
            return new BlueCollarApiResponse<HomePageResponse>(null, 401);
        } catch (Exception e) {
            logger.error("Error updating home page: {}", e.getMessage(), e);
            return new BlueCollarApiResponse<HomePageResponse>(null, 500);
        }
    }
    
    @PostMapping("/category/{categoryId}")
    public BlueCollarApiResponse<CategoryDealsResponse> createCategoryDeals(@PathVariable String categoryId,
                                                                           @Valid @RequestBody PLPData plpData,
                                                                           @RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Creating category deals for categoryId: {}", categoryId);
        try {
            sessionService.validateCanManageDeals(sessionToken);
            CategoryDealsResponse response = dealsService.createCategoryDeals(categoryId, plpData);
            logger.info("Category deals created successfully for categoryId: {}", categoryId);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized attempt to create category deals: {}", e.getMessage());
            return new BlueCollarApiResponse<CategoryDealsResponse>(null, 401);
        } catch (Exception e) {
            logger.error("Error creating category deals: {}", e.getMessage(), e);
            return new BlueCollarApiResponse<CategoryDealsResponse>(null, 500);
        }
    }
    
    @PutMapping("/category/{categoryId}")
    public BlueCollarApiResponse<CategoryDealsResponse> updateCategoryDeals(@PathVariable String categoryId,
                                                                           @Valid @RequestBody PLPData plpData,
                                                                           @RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Updating category deals for categoryId: {}", categoryId);
        try {
            sessionService.validateCanManageDeals(sessionToken);
            CategoryDealsResponse response = dealsService.updateCategoryDeals(categoryId, plpData);
            logger.info("Category deals updated successfully for categoryId: {}", categoryId);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized attempt to update category deals: {}", e.getMessage());
            return new BlueCollarApiResponse<CategoryDealsResponse>(null, 401);
        } catch (Exception e) {
            logger.error("Error updating category deals: {}", e.getMessage(), e);
            return new BlueCollarApiResponse<CategoryDealsResponse>(null, 500);
        }
    }
    
    @PostMapping("/brand/{brandId}")
    public BlueCollarApiResponse<BrandDetailsResponse> createBrandDetails(@PathVariable String brandId,
                                                                         @Valid @RequestBody PDPData pdpData,
                                                                         @RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Creating brand details for brandId: {}", brandId);
        try {
            sessionService.validateCanManageDeals(sessionToken);
            BrandDetailsResponse response = dealsService.createBrandDetails(brandId, pdpData);
            logger.info("Brand details created successfully for brandId: {}", brandId);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized attempt to create brand details: {}", e.getMessage());
            return new BlueCollarApiResponse<BrandDetailsResponse>(null, 401);
        } catch (Exception e) {
            logger.error("Error creating brand details: {}", e.getMessage(), e);
            return new BlueCollarApiResponse<BrandDetailsResponse>(null, 500);
        }
    }
    
    @PutMapping("/brand/{brandId}")
    public BlueCollarApiResponse<BrandDetailsResponse> updateBrandDetails(@PathVariable String brandId,
                                                                         @Valid @RequestBody PDPData pdpData,
                                                                         @RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Updating brand details for brandId: {}", brandId);
        try {
            sessionService.validateCanManageDeals(sessionToken);
            BrandDetailsResponse response = dealsService.updateBrandDetails(brandId, pdpData);
            logger.info("Brand details updated successfully for brandId: {}", brandId);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized attempt to update brand details: {}", e.getMessage());
            return new BlueCollarApiResponse<BrandDetailsResponse>(null, 401);
        } catch (Exception e) {
            logger.error("Error updating brand details: {}", e.getMessage(), e);
            return new BlueCollarApiResponse<BrandDetailsResponse>(null, 500);
        }
    }
    
    @DeleteMapping("/category/{categoryId}")
    public BlueCollarApiResponse<String> deleteCategoryDeals(@PathVariable String categoryId,
                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Deleting category deals for categoryId: {}", categoryId);
        try {
            sessionService.validateCanManageDeals(sessionToken);
            dealsService.deleteCategoryDeals(categoryId);
            logger.info("Category deals deleted successfully for categoryId: {}", categoryId);
            return new BlueCollarApiResponse<>("Category deals deleted successfully", 200);
        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized attempt to delete category deals: {}", e.getMessage());
            return new BlueCollarApiResponse<String>(null, 401);
        } catch (Exception e) {
            logger.error("Error deleting category deals: {}", e.getMessage(), e);
            return new BlueCollarApiResponse<String>(null, 500);
        }
    }
    
    @DeleteMapping("/brand/{brandId}")
    public BlueCollarApiResponse<String> deleteBrandDetails(@PathVariable String brandId,
                                                           @RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Deleting brand details for brandId: {}", brandId);
        try {
            sessionService.validateCanManageDeals(sessionToken);
            dealsService.deleteBrandDetails(brandId);
            logger.info("Brand details deleted successfully for brandId: {}", brandId);
            return new BlueCollarApiResponse<>("Brand details deleted successfully", 200);
        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized attempt to delete brand details: {}", e.getMessage());
            return new BlueCollarApiResponse<String>(null, 401);
        } catch (Exception e) {
            logger.error("Error deleting brand details: {}", e.getMessage(), e);
            return new BlueCollarApiResponse<String>(null, 500);
        }
    }
    
    @GetMapping("/list")
    public BlueCollarApiResponse<List<String>> listAllDeals(@RequestHeader("Admin-Session-Token") String sessionToken) {
        logger.info("Listing all deals");
        try {
            sessionService.validateCanManageDeals(sessionToken);
            List<String> deals = dealsService.listAllDeals();
            logger.info("All deals listed successfully");
            return new BlueCollarApiResponse<>(deals, 200);
        } catch (UnauthorizedException e) {
            logger.warn("Unauthorized attempt to list deals: {}", e.getMessage());
            return new BlueCollarApiResponse<List<String>>(null, 401);
        } catch (Exception e) {
            logger.error("Error listing deals: {}", e.getMessage(), e);
            return new BlueCollarApiResponse<List<String>>(null, 500);
        }
    }
}
