package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.deals.dto.*;

import java.util.List;

public interface IDealsService {
    
    HomePageResponse getHomePage();
    
    HomePageData getHomePageData();
    
    BrandDetailsResponse getBrandDetails(String brandId);
    
    CategoryDealsResponse getCategoryDeals(String categoryId, String tab);
    
    List<PLPResponse> getAllCategoryPagesData();
    
    List<PDPData> getAllBrandPagesData();
    
    HomePageResponse createHomePage(HomePageData homePageData);
    
    HomePageResponse updateHomePage(HomePageData homePageData);
    
    CategoryDealsResponse createCategoryDeals(PLPData plpData);
    
    BrandDetailsResponse createBrandDetails(PDPData pdpData);
    
    void deleteCategoryDeals(String categoryId);
    
    void deleteCategoryOffer(String categoryId, String offerId);
    
    void deleteBrandDetails(String brandId);
    
    void deleteBrandDetailsById(String id);
    
    void deleteHomePageItem(HomePageItemType type, String id);
    
    CouponRequest getCouponByBrandId(String brandId);
}

