package org.bluecollar.bluecollar.deals.controller;

import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.deals.dto.HomePageData;
import org.bluecollar.bluecollar.deals.dto.PDPData;
import org.bluecollar.bluecollar.deals.dto.PLPData;
import org.bluecollar.bluecollar.deals.factory.PageDataFactory;
import org.bluecollar.bluecollar.deals.service.PageDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}