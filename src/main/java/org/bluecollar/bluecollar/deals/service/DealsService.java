package org.bluecollar.bluecollar.deals.service;

import org.bluecollar.bluecollar.common.exception.ResourceNotFoundException;
import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.model.*;
import org.bluecollar.bluecollar.deals.repository.BrandRepository;
import org.bluecollar.bluecollar.deals.repository.HomePageRepository;
import org.bluecollar.bluecollar.deals.repository.PDPRepository;
import org.bluecollar.bluecollar.deals.repository.PLPRepository;
import org.bluecollar.bluecollar.deals.repository.UserCouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DealsService {

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private HomePageRepository homePageRepository;

    @Autowired
    private PLPRepository plpRepository;

    @Autowired
    private PDPRepository pdpRepository;
    
    @Autowired
    private UserCouponRepository userCouponRepository;

    public HomePageResponse getHomePage() {
        List<HomePage> homePages = homePageRepository.findAll();
        if (homePages.isEmpty()) {
            HomePageResponse emptyResponse = new HomePageResponse();
            emptyResponse.setBanners(new ArrayList<>());
            emptyResponse.setPopularBrands(new ArrayList<>());
            emptyResponse.setHandpickedDeals(new ArrayList<>());
            emptyResponse.setCategories(new ArrayList<>());
            return emptyResponse;
        }

        HomePage homePage = homePages.get(0);
        HomePageData data = homePage.getData();

        HomePageResponse response = new HomePageResponse();

        // Convert stored data to response DTOs, always initialize lists
        response.setBanners(data.getBanners() != null ? 
            data.getBanners().stream().map(this::toHomePageBannerDto).collect(Collectors.toList()) : 
            new ArrayList<>());

        response.setPopularBrands(data.getPopularBrands() != null ? 
            data.getPopularBrands().stream().map(this::toHomePagePopularBrandDto).collect(Collectors.toList()) : 
            new ArrayList<>());

        response.setHandpickedDeals(data.getHandpickedDeals() != null ? 
            data.getHandpickedDeals().stream().map(this::toHomePageHandpickedDealDto).collect(Collectors.toList()) : 
            new ArrayList<>());

        response.setCategories(data.getCategories() != null ? 
            data.getCategories().stream().map(this::toHomePageCategoryDto).collect(Collectors.toList()) : 
            new ArrayList<>());

        response.setActive(data.isActive());

        return response;
    }

    // Admin panel helper to fetch stored home page data
    public HomePageData getHomePageData() {
        List<HomePage> homePages = homePageRepository.findAll();
        return homePages.isEmpty() ? new HomePageData() : homePages.get(0).getData();
    }

    public BrandDetailsResponse getBrandDetails(String brandId) {
        PDP pdp = pdpRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        return convertPDPToBrandDetailsResponse(pdp);
    }

    public CategoryDealsResponse getCategoryDeals(String categoryId, String tab) {
        CategoryDealsResponse response = new CategoryDealsResponse();

        // Get PLP data by categoryId
        PLP plp = plpRepository.findById(categoryId).orElse(null);
        if (plp != null) {
            response.setTitle(plp.getTitle());
            response.setTabs(plp.getTabs() != null ? plp.getTabs() : List.of("All Deals"));
            response.setActiveTab(tab != null ? tab : plp.getActiveTab());
            response.setOffers(plp.getOffers() != null ?
                    plp.getOffers().stream().map(this::toOfferResponseDto).collect(Collectors.toList()) :
                    new ArrayList<>());
            response.setActive(plp.isActive());
        } else {
            // Fallback when no PLP found
            response.setTitle(categoryId);
            response.setTabs(Arrays.asList("All Deals", "Nearby", "Popular", "Trending", "Latest", "Top Rated"));
            response.setActiveTab(tab != null ? tab : "All Deals");
            response.setOffers(new ArrayList<>());
            response.setActive(true);
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
                .map(this::convertPDPToPDPData)
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

                // Merge offers instead of replacing
                if (plpData.getOffers() != null) {
                    for (PLPData.OfferItem newOffer : plpData.getOffers()) {
                        if (newOffer.getId() == null || newOffer.getId().isEmpty()) {
                            // New offer - generate ID and add
                            newOffer.setId(java.util.UUID.randomUUID().toString());
                            existingPlp.getOffers().add(new PLP.OfferItem(newOffer));
                        } else {
                            // Update existing offer by ID
                            existingPlp.getOffers().removeIf(offer -> newOffer.getId().equals(offer.getId()));
                            existingPlp.getOffers().add(new PLP.OfferItem(newOffer));
                        }
                    }
                }

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
                updatePDPFromData(existingPdp, pdpData);
                pdpRepository.save(existingPdp);
                return convertPDPToBrandDetailsResponse(existingPdp);
            }
        }

        // Create new PDP
        generateIdsForPDPData(pdpData);
        PDP pdp = new PDP(
                null, // brandId
                pdpData.getCategoryId(),
                pdpData.getBrandName(),
                pdpData.getBannerLink(),
                pdpData.getBrandDescription(),
                pdpData.getDiscountText(),
                pdpData.getValidTill(),
                pdpData.getHowItWorksBullets(),
                pdpData.getBenefits(),
                pdpData.getHowToRedeemBullets(),
                pdpData.getTermsAndConditions(),
                pdpData.getFaq() != null ? pdpData.getFaq().stream().map(PDP.FAQItem::new).collect(Collectors.toList()) : new ArrayList<>(),
                pdpData.getRedeemLink(),
                pdpData.isRedeemed(),
                pdpData.isActive(),
                null // CouponCampaign, pass null or adjust if needed
        );
        PDP savedPdp = pdpRepository.save(pdp);
        return convertPDPToBrandDetailsResponse(savedPdp);
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
        // This method is now deprecated, use deleteBrandDetailsById instead
        throw new UnsupportedOperationException("Use deleteBrandDetailsById instead");
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

    private HomePageResponse.BannerDto toBannerDto(Banner banner) {
        HomePageResponse.BannerDto dto = new HomePageResponse.BannerDto();
        dto.setId(banner.getId());
        dto.setImageUrl(banner.getImageUrl());
        dto.setRedirectionLink(banner.getRedirectionLink());
        return dto;
    }

    private HomePageResponse.PopularBrandDto toPopularBrandDto(Brand brand) {
        HomePageResponse.PopularBrandDto dto = new HomePageResponse.PopularBrandDto();
        dto.setBrandId(brand.getId());
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
        dto.setBrandId(offer.getBrandId());
        return dto;
    }

    private BrandDetailsResponse convertPDPToBrandDetailsResponse(PDP pdp) {
        BrandDetailsResponse response = new BrandDetailsResponse();
        response.setBrandName(pdp.getBrandName());
        response.setBannerLink(pdp.getBannerLink());
        response.setBrandDescription(pdp.getBrandDescription());
        response.setCouponCode(pdp.getDiscountText());
        response.setValidTill(pdp.getValidTill());
        response.setHowItWorksBullets(pdp.getHowItWorksBullets());
        response.setBenefits(pdp.getBenefits());
        response.setHowToRedeemBullets(pdp.getHowToRedeemBullets());
        response.setTermsAndConditions(pdp.getTermsAndConditions());
        if (pdp.getFaq() != null) {
            response.setFaq(pdp.getFaq().stream().map(this::toPDPEntityFAQDto).collect(Collectors.toList()));
        }
        response.setRedeemLink(pdp.getRedeemLink());
        response.setRedeemed(pdp.isRedeemed());
        response.setActive(pdp.isActive());
        
        // Add coupon campaign info
        if (pdp.getCampaign() != null) {
            BrandDetailsResponse.CouponInfo couponInfo = new BrandDetailsResponse.CouponInfo();
            couponInfo.setCouponCode(pdp.getCampaign().getCouponCode());
            couponInfo.setAvailable(pdp.getCampaign().isActive());
            
            // Calculate remaining coupons
            long usedCount = userCouponRepository.countByCampaignId(pdp.getCampaign().getId());
            int remaining = Math.max(0, pdp.getCampaign().getNoOfCoupons() - (int)usedCount);
            couponInfo.setRemainingCount(remaining);
            
            response.setCouponInfo(couponInfo);
        }
        
        return response;
    }

    private PDPData convertPDPToPDPData(PDP pdp) {
        PDPData data = new PDPData();
        data.setId(pdp.getId());
        data.setCategoryId(pdp.getCategoryId());
        data.setBrandName(pdp.getBrandName());
        data.setBannerLink(pdp.getBannerLink());
        data.setBrandDescription(pdp.getBrandDescription());
        data.setDiscountText(pdp.getDiscountText());
        data.setValidTill(pdp.getValidTill());
        data.setHowItWorksBullets(pdp.getHowItWorksBullets());
        data.setBenefits(pdp.getBenefits());
        data.setHowToRedeemBullets(pdp.getHowToRedeemBullets());
        data.setTermsAndConditions(pdp.getTermsAndConditions());
        if (pdp.getFaq() != null) {
            data.setFaq(pdp.getFaq().stream().map(faq -> {
                PDPData.FAQItem dto = new PDPData.FAQItem();
                dto.setId(faq.getId());
                dto.setQuestion(faq.getQuestion());
                dto.setAnswer(faq.getAnswer());
                return dto;
            }).collect(Collectors.toList()));
        }
        data.setRedeemLink(pdp.getRedeemLink());
        data.setRedeemed(pdp.isRedeemed());
        data.setActive(pdp.isActive());
        return data;
    }

    private void updatePDPFromData(PDP pdp, PDPData data) {
        pdp.setCategoryId(data.getCategoryId());
        pdp.setBrandName(data.getBrandName());
        pdp.setBannerLink(data.getBannerLink());
        pdp.setBrandDescription(data.getBrandDescription());
        pdp.setDiscountText(data.getDiscountText());
        pdp.setValidTill(data.getValidTill());
        pdp.setHowItWorksBullets(data.getHowItWorksBullets());
        pdp.setBenefits(data.getBenefits());
        pdp.setHowToRedeemBullets(data.getHowToRedeemBullets());
        pdp.setTermsAndConditions(data.getTermsAndConditions());
        pdp.setFaq(data.getFaq() != null ?
                data.getFaq().stream().map(PDP.FAQItem::new).collect(Collectors.toList()) :
                new ArrayList<>());
        pdp.setRedeemLink(data.getRedeemLink());
        pdp.setRedeemed(data.isRedeemed());
        pdp.setActive(data.isActive());
    }

    private BrandDetailsResponse.FAQDto toPDPFAQDto(PDPData.FAQItem faq) {
        BrandDetailsResponse.FAQDto dto = new BrandDetailsResponse.FAQDto();
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        return dto;
    }

    private BrandDetailsResponse.FAQDto toPDPEntityFAQDto(PDP.FAQItem faq) {
        BrandDetailsResponse.FAQDto dto = new BrandDetailsResponse.FAQDto();
        dto.setQuestion(faq.getQuestion());
        dto.setAnswer(faq.getAnswer());
        return dto;
    }

    // HomePage data converters
    private HomePageResponse.BannerDto toHomePageBannerDto(HomePageData.BannerItem banner) {
        HomePageResponse.BannerDto dto = new HomePageResponse.BannerDto();
        dto.setId(banner.getId());
        dto.setImageUrl(banner.getImageUrl());
        dto.setRedirectionLink(banner.getRedirectionLink());
        dto.setCategoryId(banner.getCategoryId());
        dto.setPdpId(banner.getPdpId());
        return dto;
    }

    private HomePageResponse.PopularBrandDto toHomePagePopularBrandDto(HomePageData.PopularBrand brand) {
        HomePageResponse.PopularBrandDto dto = new HomePageResponse.PopularBrandDto();
        dto.setBrandId(brand.getPdpId());
        dto.setName(brand.getName());
        dto.setDiscount(brand.getDiscount());
        dto.setImageUrl(brand.getImageUrl());
        dto.setRedirectionLink(brand.getRedirectionLink());
        dto.setCategoryId(brand.getCategoryId());
        dto.setPdpId(brand.getPdpId());
        return dto;
    }

    private HomePageResponse.HandpickedDealDto toHomePageHandpickedDealDto(HomePageData.HandpickedDeal deal) {
        HomePageResponse.HandpickedDealDto dto = new HomePageResponse.HandpickedDealDto();
        dto.setId(deal.getId());
        dto.setImageUrl(deal.getImageUrl());
        dto.setRedirectionLink(deal.getRedirectionLink());
        dto.setCategoryId(deal.getCategoryId());
        dto.setPdpId(deal.getPdpId());
        return dto;
    }

    private HomePageResponse.CategoryDto toHomePageCategoryDto(HomePageData.CategoryItem category) {
        HomePageResponse.CategoryDto dto = new HomePageResponse.CategoryDto();
        dto.setId(category.getId());
        dto.setLabel(category.getLabel());
        dto.setImageUrl(category.getImageUrl());
        dto.setRedirectionLink(category.getRedirectionLink());
        dto.setCategoryId(category.getCategoryId());
        dto.setPdpId(category.getPdpId());
        return dto;
    }
}