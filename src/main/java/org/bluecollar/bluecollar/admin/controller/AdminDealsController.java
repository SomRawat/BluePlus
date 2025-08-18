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

    @GetMapping("/home")
    public BlueCollarApiResponse<HomePageResponse> getHomePage(@RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanViewDeals(sessionToken);
        HomePageResponse response = dealsService.getHomePage();
        return new BlueCollarApiResponse<>(response, 200);
    }

    @GetMapping("/brand/{brandId}")
    public BlueCollarApiResponse<BrandDetailsResponse> getBrandDetails(@PathVariable String brandId,
                                                                       @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanViewDeals(sessionToken);
        BrandDetailsResponse response = dealsService.getBrandDetails(brandId);
        return new BlueCollarApiResponse<>(response, 200);
    }

    @GetMapping("/category/{categoryId}")
    public BlueCollarApiResponse<CategoryDealsResponse> getCategoryDeals(@PathVariable String categoryId,
                                                                         @RequestParam(required = false) String tab,
                                                                         @RequestHeader("Admin-Session-Token") String sessionToken) {
        sessionService.validateCanViewDeals(sessionToken);
        CategoryDealsResponse response = dealsService.getCategoryDeals(categoryId, tab);
        return new BlueCollarApiResponse<>(response, 200);
    }


}
