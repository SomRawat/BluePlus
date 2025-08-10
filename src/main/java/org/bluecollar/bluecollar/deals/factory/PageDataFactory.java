package org.bluecollar.bluecollar.deals.factory;

import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PageDataFactory {
    
    @Autowired
    private HomePageRepository homePageRepository;
    
    @Autowired
    private PLPRepository plpRepository;
    
    @Autowired
    private PDPRepository pdpRepository;
    
    public HomePageData getHomePageData() {
        var homePage = homePageRepository.findByActiveTrue();
        return homePage != null ? homePage.getData() : null;
    }
    
    public PLPData getPLPData(String categoryId) {
        var plp = plpRepository.findByCategoryIdAndActiveTrue(categoryId);
        return plp != null ? plp.getData() : null;
    }
    
    public PDPData getPDPData(String brandId) {
        var pdp = pdpRepository.findByBrandIdAndActiveTrue(brandId);
        return pdp != null ? pdp.getData() : null;
    }
}