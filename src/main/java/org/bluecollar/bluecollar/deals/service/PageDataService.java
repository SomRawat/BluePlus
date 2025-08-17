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

}