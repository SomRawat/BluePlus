package org.bluecollar.bluecollar.admin.controller;

import jakarta.validation.Valid;
import org.bluecollar.bluecollar.admin.service.AdminSessionService;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.service.DealsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/deals")
@Validated
public class AdminDealsController {

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
        sessionService.validateCanManageDeals(sessionToken);
        HomePageResponse response = dealsService.createHomePage(homePageData);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @GetMapping("/home")
    public BlueCollarApiResponse<java.util.List<HomePageData>> getHomePages(@RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanViewDeals(sessionToken);
        java.util.List<HomePageData> response = dealsService.getAllHomePagesData();
        return new BlueCollarApiResponse<>(response, 200);
    }
    
    @DeleteMapping("/home")
    public BlueCollarApiResponse<String> deleteHomePage(@RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        dealsService.deleteHomePage();
        return new BlueCollarApiResponse<>("Home page deleted successfully", 200);
    }

    @PostMapping("/category/{categoryId}")
    public BlueCollarApiResponse<CategoryDealsResponse> createCategoryDeals(@PathVariable String categoryId,
                                                                            @Valid @RequestBody PLPData plpData,
                                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        CategoryDealsResponse response = dealsService.createCategoryDeals(categoryId, plpData);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @GetMapping("/category")
    public BlueCollarApiResponse<java.util.List<PLPData>> getAllCategoryDeals(@RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanViewDeals(sessionToken);
        java.util.List<PLPData> response = dealsService.getAllCategoryPagesData();
        return new BlueCollarApiResponse<>(response, 200);
    }

    @PostMapping("/brand/{brandId}")
    public BlueCollarApiResponse<BrandDetailsResponse> createBrandDetails(@PathVariable String brandId,
                                                                          @Valid @RequestBody PDPData pdpData,
                                                                          @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        BrandDetailsResponse response = dealsService.createBrandDetails(brandId, pdpData);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @GetMapping("/brand")
    public BlueCollarApiResponse<java.util.List<PDPData>> getAllBrandDetails(@RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanViewDeals(sessionToken);
        java.util.List<PDPData> response = dealsService.getAllBrandPagesData();
        return new BlueCollarApiResponse<>(response, 200);
    }

    @DeleteMapping("/category/{categoryId}")
    public BlueCollarApiResponse<String> deleteCategoryDeals(@PathVariable String categoryId,
                                                             @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        dealsService.deleteCategoryDeals(categoryId);
        return new BlueCollarApiResponse<>("Category deals deleted successfully", 200);
    }

    @DeleteMapping("/brand/{brandId}")
    public BlueCollarApiResponse<String> deleteBrandDetails(@PathVariable String brandId,
                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        dealsService.deleteBrandDetails(brandId);
        return new BlueCollarApiResponse<>("Brand details deleted successfully", 200);
    }


}
