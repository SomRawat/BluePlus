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
    public BlueCollarApiResponse<HomePageData> getHomePages(@RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanViewDeals(sessionToken);
        HomePageData response = dealsService.getHomePageData();
        return new BlueCollarApiResponse<>(response, 200);
    }

    @DeleteMapping("/home/item/{type}/{id}")
    public BlueCollarApiResponse<String> deleteHomePageItem(@PathVariable HomePageItemType type, @PathVariable String id, @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        dealsService.deleteHomePageItem(type, id);
        return new BlueCollarApiResponse<>(type.name().toLowerCase().replace("_", " ") + " deleted successfully", 200);
    }

    @PostMapping("/category")
    public BlueCollarApiResponse<CategoryDealsResponse> createCategoryDeals(@Valid @RequestBody PLPData plpData,
                                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        CategoryDealsResponse response = dealsService.createCategoryDeals(plpData);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @GetMapping("/categories")
    public BlueCollarApiResponse<java.util.List<PLPResponse>> getAllCategoryDeals(@RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanViewDeals(sessionToken);
        java.util.List<PLPResponse> response = dealsService.getAllCategoryPagesData();
        return new BlueCollarApiResponse<>(response, 200);
    }

    @DeleteMapping("/category/{categoryId}/offer/{offerId}")
    public BlueCollarApiResponse<String> deleteCategoryOffer(@PathVariable String categoryId, @PathVariable String offerId,
                                                             @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        dealsService.deleteCategoryOffer(categoryId, offerId);
        return new BlueCollarApiResponse<>("Offer deleted successfully", 200);
    }


    @PostMapping("/brand")
    public BlueCollarApiResponse<BrandDetailsResponse> createBrandDetails(@Valid @RequestBody PDPData pdpData,
                                                                          @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        BrandDetailsResponse response = dealsService.createBrandDetails(pdpData);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @GetMapping("/brands")
    public BlueCollarApiResponse<java.util.List<PDPData>> getAllBrandDetails(@RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanViewDeals(sessionToken);
        java.util.List<PDPData> response = dealsService.getAllBrandPagesData();
        return new BlueCollarApiResponse<>(response, 200);
    }


    @DeleteMapping("/brand/{id}")
    public BlueCollarApiResponse<String> deleteBrandDetails(@PathVariable String id,
                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        dealsService.deleteBrandDetailsById(id);
        return new BlueCollarApiResponse<>("Brand details deleted successfully", 200);
    }
    
    @DeleteMapping("/brand/id/{id}")
    public BlueCollarApiResponse<String> deleteBrandDetailsById(@PathVariable String id,
                                                               @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        dealsService.deleteBrandDetailsById(id);
        return new BlueCollarApiResponse<>("Brand details deleted successfully", 200);
    }
    
    @DeleteMapping("/category/{categoryId}")
    public BlueCollarApiResponse<String> deleteCategoryDeals(@PathVariable String categoryId,
                                                            @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanManageDeals(sessionToken);
        dealsService.deleteCategoryDeals(categoryId);
        return new BlueCollarApiResponse<>("Category deals deleted successfully", 200);
    }


}
