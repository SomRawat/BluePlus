package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.common.exception.ResourceNotFoundException;
import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.model.*;
import org.bluecollar.bluecollar.deals.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DealsService {
    
    @Autowired
    private BannerRepository bannerRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    @Autowired
    private HomePageRepository homePageRepository;
    
    @Autowired
    private PLPRepository plpRepository;
    
    @Autowired
    private PDPRepository pdpRepository;
    
    public HomePageResponse getHomePage() {
        HomePageResponse response = new HomePageResponse();
        
        // Get banners
        List<Banner> banners = bannerRepository.findByActiveTrue();
        response.setBanners(banners.stream().map(this::toBannerDto).collect(Collectors.toList()));
        
        // Get popular brands from PDP
        List<PDP> pdps = pdpRepository.findAll();
        response.setPopularBrands(pdps.stream().map(this::toPopularBrandDto).collect(Collectors.toList()));
        
        // Get handpicked deals (same as banners for now)
        response.setHandpickedDeals(banners.stream().map(this::toHandpickedDealDto).collect(Collectors.toList()));
        
        // Get categories
        List<Category> categories = categoryRepository.findByActiveTrue();
        response.setCategories(categories.stream().map(this::toCategoryDto).collect(Collectors.toList()));
        
        // Set isActive from home page data if exists
        List<HomePage> homePages = homePageRepository.findAll();
        if (!homePages.isEmpty()) {
            response.setActive(homePages.get(0).getData().isActive());
        }
        
        return response;
    }
    
    public BrandDetailsResponse getBrandDetails(String brandId) {
        PDP pdp = pdpRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        
        BrandDetailsResponse response = new BrandDetailsResponse();
        response.setBrandName(pdp.getBrandName());
        response.setBannerLink(pdp.getBannerLink());
        response.setBrandDescription(pdp.getBrandDescription());
        response.setCouponCode(pdp.getCouponCode());
        response.setValidTill(pdp.getValidTill());
        response.setHowItWorksBullets(pdp.getHowItWorksBullets());
        response.setBenefits(pdp.getBenefits());
        response.setHowToRedeemBullets(pdp.getHowToRedeemBullets());
        response.setTermsAndConditions(pdp.getTermsAndConditions());
        
        if (pdp.getFaq() != null) {
            response.setFaq(pdp.getFaq().stream().map(this::toPDPFAQDto).collect(Collectors.toList()));
        }
        
        response.setRedeemLink(pdp.getRedeemLink());
        response.setRedeemed(pdp.isRedeemed());
        response.setActive(pdp.isActive());
        
        // Add coupon info if available
        if (pdp.getCouponInfo() != null) {
            BrandDetailsResponse.CouponInfo couponInfo = new BrandDetailsResponse.CouponInfo();
            couponInfo.setCouponCode(pdp.getCouponInfo().getCouponCode());
            couponInfo.setAvailable(pdp.getCouponInfo().isAvailable());
            couponInfo.setRemainingCount(pdp.getCouponInfo().getRemainingCount());
            response.setCouponInfo(couponInfo);
        }
        
        return response;
    }
    
    public CategoryDealsResponse getCategoryDeals(String categoryId, String tab) {
        CategoryDealsResponse response = new CategoryDealsResponse();
        
        // Try to get category, use categoryId as title if not found
        Category category = categoryRepository.findById(categoryId).orElse(null);
        response.setTitle(category != null ? category.getLabel() : categoryId);
        response.setTabs(Arrays.asList("All Deals", "Nearby", "Popular", "Trending", "Latest", "Top Rated"));
        response.setActiveTab(tab != null ? tab : "All Deals");
        
        // Get offers from PLP data if exists, otherwise use PDPs
        PLP plp = plpRepository.findById(categoryId).orElse(null);
        if (plp != null && plp.getOffers() != null) {
            response.setOffers(plp.getOffers().stream().map(this::toOfferResponseDto).collect(Collectors.toList()));
            response.setActive(plp.isActive());
        } else {
            // Fallback to PDPs as offers
            List<PDP> pdps = pdpRepository.findAll();
            response.setOffers(pdps.stream().map(this::toOfferDto).collect(Collectors.toList()));
        }
        
        return response;
    }

    public BrandDetailsResponse addCouponInfoToBrandDetails(BrandDetailsResponse response, String brandId, String sessionToken) {
        // This method is no longer needed as coupon info is embedded in PDP
        return response;
    }
    
    private HomePageResponse.BannerDto toBannerDto(Banner banner) {
        HomePageResponse.BannerDto dto = new HomePageResponse.BannerDto();
        dto.setId(banner.getId());
        dto.setImageUrl(banner.getImageUrl());
        dto.setRedirectionLink(banner.getRedirectionLink());
        return dto;
    }
    
    private HomePageResponse.PopularBrandDto toPopularBrandDto(PDP pdp) {
        HomePageResponse.PopularBrandDto dto = new HomePageResponse.PopularBrandDto();
        dto.setName(pdp.getBrandName());
        dto.setDiscount(pdp.getCouponCode());
        dto.setImageUrl(pdp.getBannerLink());
        dto.setRedirectionLink("/brand/" + pdp.getId());
        return dto;
    }
    
    private HomePageResponse.HandpickedDealDto toHandpickedDealDto(Banner banner) {
        HomePageResponse.HandpickedDealDto dto = new HomePageResponse.HandpickedDealDto();
        dto.setId(banner.getId());
        dto.setImageUrl(banner.getImageUrl());
        dto.setRedirectionLink(banner.getRedirectionLink());
        return dto;
    }
    
    private HomePageResponse.CategoryDto toCategoryDto(Category category) {
        HomePageResponse.CategoryDto dto = new HomePageResponse.CategoryDto();
        dto.setId(category.getId());
        dto.setLabel(category.getLabel());
        dto.setImageUrl(category.getImageUrl());
        dto.setRedirectionLink(category.getRedirectionLink());
        return dto;
    }
    
    private BrandDetailsResponse.FAQDto toPDPFAQDto(PDP.FAQItem faq) {
        BrandDetailsResponse.FAQDto dto = new BrandDetailsResponse.FAQDto();
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        return dto;
    }
    
    private CategoryDealsResponse.OfferDto toOfferDto(PDP pdp) {
        CategoryDealsResponse.OfferDto dto = new CategoryDealsResponse.OfferDto();
        dto.setId(pdp.getId());
        dto.setBrand(pdp.getBrandName());
        dto.setDiscount(pdp.getCouponCode());
        dto.setDiscountLabel("On " + pdp.getBrandName().toLowerCase());
        dto.setImageUrl(pdp.getBannerLink());
        return dto;
    }
    
    private CategoryDealsResponse.OfferDto toOfferResponseDto(PLP.OfferItem offer) {
        CategoryDealsResponse.OfferDto dto = new CategoryDealsResponse.OfferDto();
        dto.setId(offer.getId());
        dto.setBrand(offer.getBrand());
        dto.setDiscount(offer.getDiscount());
        dto.setDiscountLabel(offer.getDiscountLabel());
        dto.setImageUrl(offer.getImageUrl());
        return dto;
    }
}