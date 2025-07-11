package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.model.*;
import org.bluecollar.bluecollar.deals.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DealsService {
    
    @Autowired
    private BannerRepository bannerRepository;
    
    @Autowired
    private BrandRepository brandRepository;
    
    @Autowired
    private CategoryRepository categoryRepository;
    
    public HomePageResponse getHomePage() {
        HomePageResponse response = new HomePageResponse();
        
        // Get banners
        List<Banner> banners = bannerRepository.findByActiveTrue();
        response.setBanners(banners.stream().map(this::toBannerDto).collect(Collectors.toList()));
        
        // Get popular brands
        List<Brand> brands = brandRepository.findByActiveTrue();
        response.setPopularBrands(brands.stream().map(this::toPopularBrandDto).collect(Collectors.toList()));
        
        // Get handpicked deals (same as banners for now)
        response.setHandpickedDeals(banners.stream().map(this::toHandpickedDealDto).collect(Collectors.toList()));
        
        // Get categories
        List<Category> categories = categoryRepository.findByActiveTrue();
        response.setCategories(categories.stream().map(this::toCategoryDto).collect(Collectors.toList()));
        
        return response;
    }
    
    public BrandDetailsResponse getBrandDetails(String brandId) {
        Brand brand = brandRepository.findByIdAndActiveTrue(brandId)
                .orElseThrow(() -> new RuntimeException("Brand not found"));
        
        BrandDetailsResponse response = new BrandDetailsResponse();
        response.setBrandName(brand.getName());
        response.setBannerLink(brand.getImageUrl());
        response.setBrandDescription(brand.getDescription());
        response.setDiscountText(brand.getDiscountText());
        
        if (brand.getValidTill() != null) {
            response.setValidTill("Valid till: " + brand.getValidTill().format(DateTimeFormatter.ofPattern("MMM dd, yyyy")));
        }
        
        response.setHowItWorksBullets(Arrays.asList(brand.getHowItWorksBullets() != null ? brand.getHowItWorksBullets() : new String[0]));
        response.setBenefits(Arrays.asList(brand.getBenefits() != null ? brand.getBenefits() : new String[0]));
        response.setHowToRedeemBullets(Arrays.asList(brand.getHowToRedeemBullets() != null ? brand.getHowToRedeemBullets() : new String[0]));
        response.setTermsAndConditions(Arrays.asList(brand.getTermsAndConditions() != null ? brand.getTermsAndConditions() : new String[0]));
        
        if (brand.getFaq() != null) {
            response.setFaq(Arrays.stream(brand.getFaq()).map(this::toFAQDto).collect(Collectors.toList()));
        }
        
        response.setRedeemLink(brand.getRedeemLink());
        
        return response;
    }
    
    public CategoryDealsResponse getCategoryDeals(String categoryId, String tab) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        
        CategoryDealsResponse response = new CategoryDealsResponse();
        response.setTitle(category.getLabel());
        response.setTabs(Arrays.asList("All Deals", "Nearby", "Popular", "Trending", "Latest", "Top Rated"));
        response.setActiveTab(tab != null ? tab : "All Deals");
        
        // Get brands as offers
        List<Brand> brands = brandRepository.findByActiveTrue();
        response.setOffers(brands.stream().map(this::toOfferDto).collect(Collectors.toList()));
        
        return response;
    }
    
    private HomePageResponse.BannerDto toBannerDto(Banner banner) {
        HomePageResponse.BannerDto dto = new HomePageResponse.BannerDto();
        dto.setId(banner.getId());
        dto.setImageUrl(banner.getImageUrl());
        dto.setRedirectionLink(banner.getRedirectionLink());
        return dto;
    }
    
    private HomePageResponse.PopularBrandDto toPopularBrandDto(Brand brand) {
        HomePageResponse.PopularBrandDto dto = new HomePageResponse.PopularBrandDto();
        dto.setName(brand.getName());
        dto.setDiscount(brand.getDiscount());
        dto.setImageUrl(brand.getImageUrl());
        dto.setRedirectionLink(brand.getRedirectionLink());
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
    
    private BrandDetailsResponse.FAQDto toFAQDto(Brand.FAQ faq) {
        BrandDetailsResponse.FAQDto dto = new BrandDetailsResponse.FAQDto();
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        return dto;
    }
    
    private CategoryDealsResponse.OfferDto toOfferDto(Brand brand) {
        CategoryDealsResponse.OfferDto dto = new CategoryDealsResponse.OfferDto();
        dto.setId(brand.getId());
        dto.setBrand(brand.getName());
        dto.setDiscount(brand.getDiscount());
        dto.setDiscountLabel("On " + brand.getName().toLowerCase());
        dto.setImageUrl(brand.getImageUrl());
        return dto;
    }
}