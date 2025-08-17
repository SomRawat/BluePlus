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
import java.util.ArrayList;
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

    // Admin panel helper to fetch stored home page data
    public HomePageData getHomePageData() {
        List<HomePage> homePages = homePageRepository.findAll();
        return homePages.isEmpty() ? new HomePageData() : homePages.get(0).getData();
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
        CategoryDealsResponse response = new CategoryDealsResponse();
        
        // Try to get category, use categoryId as title if not found
        Category category = categoryRepository.findById(categoryId).orElse(null);
        response.setTitle(category != null ? category.getLabel() : categoryId);
        response.setTabs(Arrays.asList("All Deals", "Nearby", "Popular", "Trending", "Latest", "Top Rated"));
        response.setActiveTab(tab != null ? tab : "All Deals");
        
        // Get offers from PLP data if exists, otherwise use brands
        PLP plp = plpRepository.findById(categoryId).orElse(null);
        if (plp != null && plp.getOffers() != null) {
            response.setOffers(plp.getOffers().stream().map(this::toOfferResponseDto).collect(Collectors.toList()));
            response.setActive(plp.isActive());
        } else {
            // Fallback to brands as offers
            List<Brand> brands = brandRepository.findByActiveTrue();
            response.setOffers(brands.stream().map(this::toOfferDto).collect(Collectors.toList()));
        }
        
        return response;
    }

    public List<PLPResponse> getAllCategoryPagesData() {
        return plpRepository.findAll().stream()
                .map(plp -> {
                    PLPResponse response = new PLPResponse();
                    response.setCategoryId(plp.getCategoryId());
                    response.setTitle(plp.getTitle());
                    response.setTabs(plp.getTabs());
                    response.setActiveTab(plp.getActiveTab());
                    response.setOffers(plp.getOffers());
                    response.setActive(plp.isActive());
                    return response;
                })
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
        // Generate IDs for items that don't have them
        generateIdsForHomePageItems(homePageData);
        
        List<HomePage> existingPages = homePageRepository.findAll();
        
        if (!existingPages.isEmpty()) {
            // Update existing home page
            HomePage existingPage = existingPages.get(0);
            HomePageData existingData = existingPage.getData();
            
            // Merge items by ID instead of replacing entire arrays
            if (homePageData.getBanners() != null) {
                mergeBanners(existingData.getBanners(), homePageData.getBanners());
            }
            if (homePageData.getPopularBrands() != null) {
                mergePopularBrands(existingData.getPopularBrands(), homePageData.getPopularBrands());
            }
            if (homePageData.getHandpickedDeals() != null) {
                mergeHandpickedDeals(existingData.getHandpickedDeals(), homePageData.getHandpickedDeals());
            }
            if (homePageData.getCategories() != null) {
                mergeCategories(existingData.getCategories(), homePageData.getCategories());
            }
            existingData.setActive(homePageData.isActive());
            
            homePageRepository.save(existingPage);
        } else {
            // Create new home page
            HomePage homePage = new HomePage(homePageData, homePageData.isActive());
            homePageRepository.save(homePage);
        }
        
        return getHomePage();
    }
    
    private void mergeBanners(List<HomePageData.BannerItem> existing, List<HomePageData.BannerItem> newItems) {
        for (HomePageData.BannerItem newItem : newItems) {
            if (newItem.getId() != null && !newItem.getId().isEmpty()) {
                existing.removeIf(item -> newItem.getId().equals(item.getId()));
            }
            existing.add(newItem);
        }
    }
    
    private void mergePopularBrands(List<HomePageData.PopularBrand> existing, List<HomePageData.PopularBrand> newItems) {
        for (HomePageData.PopularBrand newItem : newItems) {
            if (newItem.getId() != null && !newItem.getId().isEmpty()) {
                existing.removeIf(item -> newItem.getId().equals(item.getId()));
            }
            existing.add(newItem);
        }
    }
    
    private void mergeHandpickedDeals(List<HomePageData.HandpickedDeal> existing, List<HomePageData.HandpickedDeal> newItems) {
        for (HomePageData.HandpickedDeal newItem : newItems) {
            if (newItem.getId() != null && !newItem.getId().isEmpty()) {
                existing.removeIf(item -> newItem.getId().equals(item.getId()));
            }
            existing.add(newItem);
        }
    }
    
    private void mergeCategories(List<HomePageData.CategoryItem> existing, List<HomePageData.CategoryItem> newItems) {
        for (HomePageData.CategoryItem newItem : newItems) {
            if (newItem.getId() != null && !newItem.getId().isEmpty()) {
                existing.removeIf(item -> newItem.getId().equals(item.getId()));
            }
            existing.add(newItem);
        }
    }
    
    private void mergeOffers(List<PLPData.OfferItem> existing, List<PLPData.OfferItem> newItems) {
        for (PLPData.OfferItem newItem : newItems) {
            if (newItem.getId() != null && !newItem.getId().isEmpty()) {
                existing.removeIf(item -> newItem.getId().equals(item.getId()));
            }
            existing.add(newItem);
        }
    }
    
    private void generateIdsForPLPData(PLPData data) {
        if (data.getOffers() != null) {
            data.getOffers().forEach(offer -> {
                if (offer.getId() == null || offer.getId().isEmpty()) {
                    offer.setId(java.util.UUID.randomUUID().toString());
                }
            });
        }
    }
    
    private void generateIdsForPDPData(PDPData data) {
        if (data.getId() == null || data.getId().isEmpty()) {
            data.setId(java.util.UUID.randomUUID().toString());
        }
        if (data.getFaq() != null) {
            data.getFaq().forEach(faq -> {
                if (faq.getId() == null || faq.getId().isEmpty()) {
                    faq.setId(java.util.UUID.randomUUID().toString());
                }
            });
        }
    }
    
    private void generateIdsForHomePageItems(HomePageData data) {
        if (data.getBanners() != null) {
            data.getBanners().forEach(banner -> {
                if (banner.getId() == null || banner.getId().isEmpty()) {
                    banner.setId(java.util.UUID.randomUUID().toString());
                }
            });
        }
        if (data.getPopularBrands() != null) {
            data.getPopularBrands().forEach(brand -> {
                if (brand.getId() == null || brand.getId().isEmpty()) {
                    brand.setId(java.util.UUID.randomUUID().toString());
                }
            });
        }
        if (data.getHandpickedDeals() != null) {
            data.getHandpickedDeals().forEach(deal -> {
                if (deal.getId() == null || deal.getId().isEmpty()) {
                    deal.setId(java.util.UUID.randomUUID().toString());
                }
            });
        }
        if (data.getCategories() != null) {
            data.getCategories().forEach(category -> {
                if (category.getId() == null || category.getId().isEmpty()) {
                    category.setId(java.util.UUID.randomUUID().toString());
                }
            });
        }
    }
    
    @Transactional
    public HomePageResponse updateHomePage(HomePageData homePageData) {
        return createHomePage(homePageData); // For now, just recreate
    }
    
    @Transactional
    public CategoryDealsResponse createCategoryDeals(PLPData plpData) {
        String finalCategoryId = plpData.getCategoryId();
        
        if (finalCategoryId != null && !finalCategoryId.isEmpty()) {
            // UPDATE: categoryId provided - try to update existing
            PLP existingPlp = plpRepository.findById(finalCategoryId).orElse(null);
            if (existingPlp != null) {
                existingPlp.setTitle(plpData.getTitle());
                existingPlp.setTabs(plpData.getTabs());
                existingPlp.setActiveTab(plpData.getActiveTab());
                existingPlp.setOffers(plpData.getOffers() != null ?
                    plpData.getOffers().stream().map(PLP.OfferItem::new).collect(java.util.stream.Collectors.toList()) :
                    new ArrayList<>());
                existingPlp.setActive(plpData.isActive());
                plpRepository.save(existingPlp);
                return getCategoryDeals(finalCategoryId, plpData.getActiveTab());
            }
        }

        // CREATE: Let MongoDB auto-generate categoryId (pass null)
        generateIdsForPLPData(plpData);
        PLP plp = new PLP(null, plpData, true); // null = MongoDB auto-generates ID
        PLP savedPlp = plpRepository.save(plp);
        
        return getCategoryDeals(savedPlp.getCategoryId(), plpData.getActiveTab());
    }
    
    @Transactional
    public BrandDetailsResponse createBrandDetails(PDPData pdpData) {
        if (pdpData.getId() != null && !pdpData.getId().isEmpty()) {
            // Update existing PDP by ID
            PDP existingPdp = pdpRepository.findById(pdpData.getId()).orElse(null);
            if (existingPdp != null) {
                existingPdp.setData(pdpData);
                pdpRepository.save(existingPdp);
                return convertPDPDataToBrandDetailsResponse(pdpData);
            }
        }
        
        // Create new PDP
        generateIdsForPDPData(pdpData);
        String brandId = pdpData.getBrandName() != null ? pdpData.getBrandName().toLowerCase().replaceAll("\\s+", "-") : java.util.UUID.randomUUID().toString();
        PDP pdp = new PDP(brandId, pdpData, true);
        pdpRepository.save(pdp);
        
        return convertPDPDataToBrandDetailsResponse(pdpData);
    }
    
    @Transactional
    public void deleteCategoryDeals(String categoryId) {
        plpRepository.deleteById(categoryId);
    }
    
    @Transactional
    public void deleteCategoryOffer(String categoryId, String offerId) {
        PLP plp = plpRepository.findByCategoryIdAndActiveTrue(categoryId);
        if (plp != null) {
            plp.getOffers().removeIf(offer -> offerId.equals(offer.getId()));
            plpRepository.save(plp);
        }
    }
    
    @Transactional
    public void deleteBrandDetails(String brandId) {
        pdpRepository.deleteByBrandId(brandId);
    }
    
    @Transactional
    public void deleteBrandDetailsById(String id) {
        pdpRepository.deleteById(id);
    }
    
    @Transactional
    public void deleteHomePageItem(HomePageItemType type, String id) {
        List<HomePage> homePages = homePageRepository.findAll();
        if (!homePages.isEmpty()) {
            HomePage homePage = homePages.get(0);
            HomePageData data = homePage.getData();
            
            switch (type) {
                case banners -> data.getBanners().removeIf(banner -> id.equals(banner.getId()));
                case popularBrands -> data.getPopularBrands().removeIf(brand -> id.equals(brand.getId()));
                case handpickedDeals -> data.getHandpickedDeals().removeIf(deal -> id.equals(deal.getId()));
                case categories -> data.getCategories().removeIf(category -> id.equals(category.getId()));
            }
            
            homePageRepository.save(homePage);
        }
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
    
    private CategoryDealsResponse.OfferDto toOfferResponseDto(PLP.OfferItem offer) {
        CategoryDealsResponse.OfferDto dto = new CategoryDealsResponse.OfferDto();
        dto.setId(offer.getId());
        dto.setBrand(offer.getBrand());
        dto.setDiscount(offer.getDiscount());
        dto.setDiscountLabel(offer.getDiscountLabel());
        dto.setImageUrl(offer.getImageUrl());
        return dto;
    }
    
    private BrandDetailsResponse convertPDPDataToBrandDetailsResponse(PDPData pdpData) {
        BrandDetailsResponse response = new BrandDetailsResponse();
        response.setBrandName(pdpData.getBrandName());
        response.setBannerLink(pdpData.getBannerLink());
        response.setBrandDescription(pdpData.getBrandDescription());
        response.setDiscountText(pdpData.getDiscountText());
        response.setValidTill(pdpData.getValidTill());
        response.setHowItWorksBullets(pdpData.getHowItWorksBullets());
        response.setBenefits(pdpData.getBenefits());
        response.setHowToRedeemBullets(pdpData.getHowToRedeemBullets());
        response.setTermsAndConditions(pdpData.getTermsAndConditions());
        if (pdpData.getFaq() != null) {
            response.setFaq(pdpData.getFaq().stream().map(this::toPDPFAQDto).collect(Collectors.toList()));
        }
        response.setRedeemLink(pdpData.getRedeemLink());
        response.setRedeemed(pdpData.isRedeemed());
        response.setActive(pdpData.isActive());
        return response;
    }
    
    private BrandDetailsResponse.FAQDto toPDPFAQDto(PDPData.FAQItem faq) {
        BrandDetailsResponse.FAQDto dto = new BrandDetailsResponse.FAQDto();
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        return dto;
    }
}