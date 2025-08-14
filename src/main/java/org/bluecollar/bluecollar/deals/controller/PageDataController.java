package org.bluecollar.bluecollar.deals.controller;

import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.factory.PageDataFactory;
import org.bluecollar.bluecollar.deals.service.PageDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pages")
public class PageDataController {
    
    @Autowired
    private PageDataFactory pageDataFactory;
    
    @Autowired
    private PageDataService pageDataService;
    
    @GetMapping("/home")
    public BlueCollarApiResponse<HomePageData> getHomePage() {
        HomePageData data = pageDataFactory.getHomePageData();
        return new BlueCollarApiResponse<>(data != null ? data : new HomePageData(), 200);
    }
    
    @GetMapping("/category/{categoryId}")
    public BlueCollarApiResponse<PLPData> getCategoryPage(@PathVariable String categoryId) {
        PLPData data = pageDataFactory.getPLPData(categoryId);
        return new BlueCollarApiResponse<>(data != null ? data : new PLPData(), 200);
    }
    
    @GetMapping("/brand/{brandId}")
    public BlueCollarApiResponse<PDPData> getBrandPage(@PathVariable String brandId) {
        PDPData data = pageDataFactory.getPDPData(brandId);
        return new BlueCollarApiResponse<>(data != null ? data : new PDPData(), 200);
    }
    
    @PostMapping("/home")
    public BlueCollarApiResponse<String> createHomePage(@RequestBody HomePageData data) {
        pageDataService.saveHomePageData(data);
        return new BlueCollarApiResponse<>("Home page created", 200);
    }
    
    @PostMapping("/category/{categoryId}")
    public BlueCollarApiResponse<String> createCategoryPage(@PathVariable String categoryId, @RequestBody PLPData data) {
        pageDataService.savePLPData(categoryId, data);
        return new BlueCollarApiResponse<>("Category page created", 200);
    }
    
    @PostMapping("/brand/{brandId}")
    public BlueCollarApiResponse<String> createBrandPage(@PathVariable String brandId, @RequestBody PDPData data) {
        pageDataService.savePDPData(brandId, data);
        return new BlueCollarApiResponse<>("Brand page created", 200);
    }
}