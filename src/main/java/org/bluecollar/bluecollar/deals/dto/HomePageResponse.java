package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import java.util.List;

@Data
public class HomePageResponse {
    private List<BannerDto> banners;
    private List<PopularBrandDto> popularBrands;
    private List<HandpickedDealDto> handpickedDeals;
    private List<CategoryDto> categories;
    private boolean isActive = true;
    
    @Data
    public static class BannerDto {
        private String id;
        private String imageUrl;
        private String redirectionLink;
    }
    
    @Data
    public static class PopularBrandDto {
        private String name;
        private String discount;
        private String imageUrl;
        private String redirectionLink;
    }
    
    @Data
    public static class HandpickedDealDto {
        private String id;
        private String imageUrl;
        private String redirectionLink;
    }
    
    @Data
    public static class CategoryDto {
        private String id;
        private String label;
        private String imageUrl;
        private String redirectionLink;
    }
}