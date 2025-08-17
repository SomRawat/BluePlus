package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.model.*;
import org.bluecollar.bluecollar.deals.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PageDataService {
    
    @Autowired
    private HomePageRepository homePageRepository;
    
    @Autowired
    private PLPRepository plpRepository;
    
    @Autowired
    private PDPRepository pdpRepository;
    
    public void saveHomePageData(HomePageData data) {
        // Upsert: deactivate current active and insert new
        var existing = homePageRepository.findByActiveTrue();
        if (existing != null) {
            existing.setActive(false);
            homePageRepository.save(existing);
        }
        HomePage homePage = new HomePage(data, true);
        homePageRepository.save(homePage);
    }
    
    public void savePLPData(String categoryId, PLPData data) {
        // Upsert by category key
        var existing = plpRepository.findById(categoryId).orElse(null);
        if (existing != null) {
            existing.setActive(false);
            plpRepository.save(existing);
        }
        PLP plp = new PLP(categoryId, data, true);
        plpRepository.save(plp);
    }
    
    public void savePDPData(String brandId, PDPData data) {
        // Ensure PDP has linked PLP category
        if (data.getCategoryId() != null && !data.getCategoryId().isEmpty()) {
            var plp = plpRepository.findById(data.getCategoryId()).orElse(null);
            if (plp == null) {
                // If PLP not present for the category, create a minimal PLP placeholder
                PLPData plpData = new PLPData();
                plpData.setTitle("Deals");
                plpData.setTabs(java.util.Arrays.asList("All Deals"));
                plpData.setActiveTab("All Deals");
                PLP newPlp = new PLP(data.getCategoryId(), plpData, true);
                plpRepository.save(newPlp);
            }
        }
        // Upsert by brand key
        var existing = pdpRepository.findByBrandIdAndActiveTrue(brandId);
        if (existing != null) {
            existing.setActive(false);
            pdpRepository.save(existing);
        }
        PDP pdp = new PDP(brandId, data, true);
        pdpRepository.save(pdp);
    }
}