package org.bluecollar.bluecollar.deals.controller;

import org.bluecollar.bluecollar.common.dto.BlueCollarApiResponse;
import org.bluecollar.bluecollar.common.service.TranslationService;
import org.bluecollar.bluecollar.deals.dto.*;
import org.bluecollar.bluecollar.deals.model.UserCoupon;
import org.bluecollar.bluecollar.deals.service.CustomerCouponService;
import org.bluecollar.bluecollar.deals.service.DealsService;
import org.bluecollar.bluecollar.login.model.Customer;
import org.bluecollar.bluecollar.login.repository.CustomerRepository;
import org.bluecollar.bluecollar.session.service.SessionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/deals")
public class DealsController {

    private final DealsService dealsService;
    private final SessionService sessionService;
    private final CustomerRepository customerRepository;
    private final CustomerCouponService customerCouponService;
    private final TranslationService translationService;

    @Autowired
    public DealsController(DealsService dealsService, SessionService sessionService, 
                             CustomerRepository customerRepository, CustomerCouponService customerCouponService,
                             org.bluecollar.bluecollar.common.service.TranslationService translationService) {
            this.dealsService = dealsService;
            this.sessionService = sessionService;
            this.customerRepository = customerRepository;
            this.customerCouponService = customerCouponService;
            this.translationService = translationService;
        }

    @GetMapping("/home")
    public BlueCollarApiResponse<HomePageResponse> getHomePage(
            @RequestHeader("Session-Token") String sessionToken,
            @RequestHeader(value = "X-Accept-Language", required = false) String acceptLanguage) {
        String customerId = sessionService.getCustomerIdFromToken(sessionToken);
        HomePageResponse response = dealsService.getHomePage();
        
        // Add user-specific data if needed
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            // Mark brands as redeemed if customer has clicked redeem
            markRedeemedBrands(response, customer);
        }
        
        return new BlueCollarApiResponse<>(maybeTranslate(response, acceptLanguage), 200);
    }

    @GetMapping("/category/{categoryId}")
    public BlueCollarApiResponse<CategoryDealsResponse> getCategoryDeals(
                @PathVariable String categoryId,
                @RequestParam(required = false) String tab,
                @RequestHeader("Session-Token") String sessionToken,
                @RequestHeader(value = "X-Accept-Language", required = false) String acceptLanguage) {
        
        String customerId = sessionService.getCustomerIdFromToken(sessionToken);
        CategoryDealsResponse response = dealsService.getCategoryDeals(categoryId, tab);
        
        // Add user-specific data if needed
        Customer customer = customerRepository.findById(customerId).orElse(null);
        if (customer != null) {
            // Mark offers as redeemed if customer has clicked redeem
            markRedeemedOffers(response, customer);
        }
        
        return new BlueCollarApiResponse<>(maybeTranslate(response, acceptLanguage), 200);
    }

    @GetMapping("/brand/{brandId}")
    public BlueCollarApiResponse<BrandDetailsResponse> getBrandDetails(
                @PathVariable String brandId,
                @RequestHeader("Session-Token") String sessionToken,
                @RequestHeader(value = "X-Accept-Language", required = false) String acceptLanguage) {
        
        String customerId = sessionService.getCustomerIdFromToken(sessionToken);
        BrandDetailsResponse response = dealsService.getBrandDetails(brandId);
        
        // Add user-specific coupon data
        UserCoupon userCoupon = customerCouponService.getUserCoupon(customerId, brandId);
        if (userCoupon != null) {
            response.setRedeemed(userCoupon.isRedeemed());
            response.setUserCouponCode(userCoupon.getCouponCode());
        }
        
        return new BlueCollarApiResponse<>(maybeTranslate(response, acceptLanguage), 200);
    }



    @PostMapping("/brand/{brandId}/redeem")
    public BlueCollarApiResponse<String> redeemCoupon(
            @PathVariable String brandId,
            @RequestHeader("Session-Token") String sessionToken) {
        
        String customerId = sessionService.getCustomerIdFromToken(sessionToken);
        String couponCode = customerCouponService.redeemCoupon(customerId, brandId);
        
        return new BlueCollarApiResponse<>(couponCode, 200);
    }

    @GetMapping("/brand/{brandId}/coupon")
    public BlueCollarApiResponse<UserCoupon> getUserCoupon(
            @PathVariable String brandId,
            @RequestHeader("Session-Token") String sessionToken,
            @RequestHeader(value = "X-Accept-Language", required = false) String acceptLanguage) {
        
        String customerId = sessionService.getCustomerIdFromToken(sessionToken);
        UserCoupon userCoupon = customerCouponService.getUserCoupon(customerId, brandId);
        
        if (userCoupon == null) {
            throw new RuntimeException("No coupon found for this brand");
        }
        
        return new BlueCollarApiResponse<>(maybeTranslate(userCoupon, acceptLanguage), 200);
    }

    @GetMapping("/coupon/brand/{brandId}")
    public BlueCollarApiResponse<CouponRequest> getCouponByBrandId(
            @PathVariable String brandId,
            @RequestHeader("Session-Token") String sessionToken,
            @RequestHeader(value = "X-Accept-Language", required = false) String acceptLanguage) {
        
        sessionService.getCustomerIdFromToken(sessionToken);
        CouponRequest coupon = dealsService.getCouponByBrandId(brandId);
        
        return new BlueCollarApiResponse<>(maybeTranslate(coupon, acceptLanguage), 200);
    }

    private <T> T maybeTranslate(T body, String acceptLanguage) {
        if (acceptLanguage == null || acceptLanguage.equals("en")) {
            return body;
        }
        return translationService.translateObject(body, acceptLanguage);
    }

    private void markRedeemedBrands(HomePageResponse response, Customer customer) {
        if (response.getPopularBrands() != null) {
            response.getPopularBrands().forEach(brand -> {
                UserCoupon userCoupon = customerCouponService.getUserCoupon(customer.getId(), brand.getBrandId());
                if (userCoupon != null) {
                    brand.setRedeemed(true);
                }
            });
        }
    }

    private void markRedeemedOffers(CategoryDealsResponse response, Customer customer) {
        if (response.getOffers() != null) {
            response.getOffers().forEach(offer -> {
                UserCoupon userCoupon = customerCouponService.getUserCoupon(customer.getId(), offer.getId());
                if (userCoupon != null) {
                    offer.setRedeemed(true);
                }
            });
        }
    }
}