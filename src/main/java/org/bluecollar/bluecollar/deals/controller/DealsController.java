package org.bluecollar.bluecollar.deals.controller;

import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.service.DealsService;
import org.bluecollar.bluecollar.deals.repository.CouponRepository;
import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deals")
public class DealsController {
    
    @Autowired
    private DealsService dealsService;
    
    @GetMapping("/home")
    public BlueCollarApiResponse<HomePageResponse> getHomePage() {
        try {
            HomePageResponse response = dealsService.getHomePage();
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
    
    @GetMapping("/category/{categoryId}")
    public BlueCollarApiResponse<CategoryDealsResponse> getCategoryDeals(
            @PathVariable String categoryId,
            @RequestParam(required = false) String tab) {
        try {
            CategoryDealsResponse response = dealsService.getCategoryDeals(categoryId, tab);
            return new BlueCollarApiResponse<>(response, 200);
        } catch (Exception e) {
            return new BlueCollarApiResponse<>(null, 400);
        }
    }
}