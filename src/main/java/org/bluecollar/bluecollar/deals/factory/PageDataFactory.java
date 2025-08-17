package org.bluecollar.bluecollar.deals.factory;

import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import java.util.ArrayList;

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
        var plp = plpRepository.findById(categoryId).orElse(null);
        if (plp != null) {
            PLPData data = new PLPData();
            data.setCategoryId(plp.getCategoryId());
            data.setTitle(plp.getTitle());
            data.setTabs(plp.getTabs());
            data.setActiveTab(plp.getActiveTab());
            data.setOffers(plp.getOffers() != null ? 
                plp.getOffers().stream().map(offer -> {
                    PLPData.OfferItem dto = new PLPData.OfferItem();
                    dto.setId(offer.getId());
                    dto.setBrand(offer.getBrand());
                    dto.setDiscount(offer.getDiscount());
                    dto.setDiscountLabel(offer.getDiscountLabel());
                    dto.setImageUrl(offer.getImageUrl());
                    dto.setActive(offer.isActive());
                    return dto;
                }).collect(java.util.stream.Collectors.toList()) : 
                new ArrayList<>());
            data.setActive(plp.isActive());
            return data;
        }
        return null;
    }
    
    public PDPData getPDPData(String brandId) {
        var pdp = pdpRepository.findByBrandIdAndActiveTrue(brandId);
        return pdp != null ? pdp.getData() : null;
    }
}