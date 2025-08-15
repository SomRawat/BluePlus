package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.common.exception.ResourceNotFoundException;
import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.model.*;
import org.bluecollar.bluecollar.deals.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
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
        
        // Get popular brands
        List<Brand> brands = brandRepository.findByActiveTrue();
        response.setPopularBrands(brands.stream().map(this::toPopularBrandDto).collect(Collectors.toList()));
        
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

    // Admin panel helpers to fetch all stored raw page data
    public List<HomePageData> getAllHomePagesData() {
        return homePageRepository.findAll().stream()
                .map(HomePage::getData)
                .collect(Collectors.toList());
    }
    
    public BrandDetailsResponse getBrandDetails(String brandId) {
        Brand brand = brandRepository.findByIdAndActiveTrue(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));
        
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
        // Set redeemed flag default
        response.setRedeemed(false);
        
        // Set isActive from PDP data if exists
        PDP pdp = pdpRepository.findByBrandIdAndActiveTrue(brandId);
        if (pdp != null) {
            response.setActive(pdp.getData().isActive());
        }
        
        return response;
    }
    
    public CategoryDealsResponse getCategoryDeals(String categoryId, String tab) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));
        
        CategoryDealsResponse response = new CategoryDealsResponse();
        response.setTitle(category.getLabel());
        response.setTabs(Arrays.asList("All Deals", "Nearby", "Popular", "Trending", "Latest", "Top Rated"));
        response.setActiveTab(tab != null ? tab : "All Deals");
        
        // Get brands as offers
        List<Brand> brands = brandRepository.findByActiveTrue();
        response.setOffers(brands.stream().map(this::toOfferDto).collect(Collectors.toList()));
        
        // Set isActive from PLP data if exists
        PLP plp = plpRepository.findByCategoryIdAndActiveTrue(categoryId);
        if (plp != null) {
            response.setActive(plp.getData().isActive());
        }
        
        return response;
    }

    public List<PLPData> getAllCategoryPagesData() {
        return plpRepository.findAll().stream()
                .map(PLP::getData)
                .collect(Collectors.toList());
    }

    public List<PDPData> getAllBrandPagesData() {
        return pdpRepository.findAll().stream()
                .map(PDP::getData)
                .collect(Collectors.toList());
    }
    
    // Admin methods for managing deals
    @Transactional
    public HomePageResponse createHomePage(HomePageData homePageData) {
        List<HomePage> existingPages = homePageRepository.findAll();
        
        if (!existingPages.isEmpty()) {
            // Update existing home page
            HomePage existingPage = existingPages.get(0);
            HomePageData existingData = existingPage.getData();
            
            // Update only provided fields
            if (homePageData.getBanners() != null) existingData.setBanners(homePageData.getBanners());
            if (homePageData.getPopularBrands() != null) existingData.setPopularBrands(homePageData.getPopularBrands());
            if (homePageData.getHandpickedDeals() != null) existingData.setHandpickedDeals(homePageData.getHandpickedDeals());
            if (homePageData.getCategories() != null) existingData.setCategories(homePageData.getCategories());
            existingData.setActive(homePageData.isActive());
            
            homePageRepository.save(existingPage);
        } else {
            // Create new home page
            HomePage homePage = new HomePage(homePageData, homePageData.isActive());
            homePageRepository.save(homePage);
        }
        
        return getHomePage();
    }
    
    @Transactional
    public HomePageResponse updateHomePage(HomePageData homePageData) {
        return createHomePage(homePageData); // For now, just recreate
    }
    
    @Transactional
    public CategoryDealsResponse createCategoryDeals(String categoryId, PLPData plpData) {
        PLP existingPlp = plpRepository.findByCategoryIdAndActiveTrue(categoryId);
        
        if (existingPlp != null) {
            // Update existing record with only provided fields
            PLPData existingData = existingPlp.getData();
            if (plpData.getTitle() != null) existingData.setTitle(plpData.getTitle());
            if (plpData.getTabs() != null) existingData.setTabs(plpData.getTabs());
            if (plpData.getActiveTab() != null) existingData.setActiveTab(plpData.getActiveTab());
            if (plpData.getOffers() != null) existingData.setOffers(plpData.getOffers());
            existingData.setActive(plpData.isActive());
            plpRepository.save(existingPlp);
        } else {
            // Create new record
            PLP plp = new PLP(categoryId, plpData, true);
            plpRepository.save(plp);
        }
        
        return getCategoryDeals(categoryId, plpData.getActiveTab());
    }
    
    @Transactional
    public BrandDetailsResponse createBrandDetails(String brandId, PDPData pdpData) {
        PDP existingPdp = pdpRepository.findByBrandIdAndActiveTrue(brandId);
        
        if (existingPdp != null) {
            // Update existing record with only provided fields
            PDPData existingData = existingPdp.getData();
            if (pdpData.getCategoryId() != null) existingData.setCategoryId(pdpData.getCategoryId());
            if (pdpData.getBrandName() != null) existingData.setBrandName(pdpData.getBrandName());
            if (pdpData.getBannerLink() != null) existingData.setBannerLink(pdpData.getBannerLink());
            if (pdpData.getBrandDescription() != null) existingData.setBrandDescription(pdpData.getBrandDescription());
            if (pdpData.getDiscountText() != null) existingData.setDiscountText(pdpData.getDiscountText());
            if (pdpData.getValidTill() != null) existingData.setValidTill(pdpData.getValidTill());
            if (pdpData.getHowItWorksBullets() != null) existingData.setHowItWorksBullets(pdpData.getHowItWorksBullets());
            if (pdpData.getBenefits() != null) existingData.setBenefits(pdpData.getBenefits());
            if (pdpData.getHowToRedeemBullets() != null) existingData.setHowToRedeemBullets(pdpData.getHowToRedeemBullets());
            if (pdpData.getTermsAndConditions() != null) existingData.setTermsAndConditions(pdpData.getTermsAndConditions());
            if (pdpData.getFaq() != null) existingData.setFaq(pdpData.getFaq());
            if (pdpData.getRedeemLink() != null) existingData.setRedeemLink(pdpData.getRedeemLink());
            existingData.setActive(pdpData.isActive());
            pdpRepository.save(existingPdp);
        } else {
            // Create new record
            PDP pdp = new PDP(brandId, pdpData, true);
            pdpRepository.save(pdp);
        }
        
        return getBrandDetails(brandId);
    }
    
    @Transactional
    public void deleteCategoryDeals(String categoryId) {
        plpRepository.deleteByCategoryId(categoryId);
    }
    
    @Transactional
    public void deleteBrandDetails(String brandId) {
        pdpRepository.deleteByBrandId(brandId);
    }
    
    @Transactional
    public void deleteHomePage() {
        homePageRepository.deleteAll();
    }
    
    public List<String> listAllDeals() {
        List<String> deals = new java.util.ArrayList<>();
        
        // Add all category deals
        List<PLP> plps = plpRepository.findAll();
        deals.addAll(plps.stream().map(plp -> "Category: " + plp.getCategoryId()).collect(Collectors.toList()));
        
        // Add all brand deals
        List<PDP> pdps = pdpRepository.findAll();
        deals.addAll(pdps.stream().map(pdp -> "Brand: " + pdp.getBrandId()).collect(Collectors.toList()));
        
        return deals;
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