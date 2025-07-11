package org.bluecollar.bluecollar.deals.dto;

import java.util.List;

public class HomePageResponse {
    private List<BannerDto> banners;
    private List<PopularBrandDto> popularBrands;
    private List<HandpickedDealDto> handpickedDeals;
    private List<CategoryDto> categories;
    
    public HomePageResponse() {}
    
    // Getters and Setters
    public List<BannerDto> getBanners() { return banners; }
    public void setBanners(List<BannerDto> banners) { this.banners = banners; }
    
    public List<PopularBrandDto> getPopularBrands() { return popularBrands; }
    public void setPopularBrands(List<PopularBrandDto> popularBrands) { this.popularBrands = popularBrands; }
    
    public List<HandpickedDealDto> getHandpickedDeals() { return handpickedDeals; }
    public void setHandpickedDeals(List<HandpickedDealDto> handpickedDeals) { this.handpickedDeals = handpickedDeals; }
    
    public List<CategoryDto> getCategories() { return categories; }
    public void setCategories(List<CategoryDto> categories) { this.categories = categories; }
    
    public static class BannerDto {
        private String id;
        private String imageUrl;
        private String redirectionLink;
        
        public BannerDto() {}
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getRedirectionLink() { return redirectionLink; }
        public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    }
    
    public static class PopularBrandDto {
        private String name;
        private String discount;
        private String imageUrl;
        private String redirectionLink;
        
        public PopularBrandDto() {}
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getDiscount() { return discount; }
        public void setDiscount(String discount) { this.discount = discount; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getRedirectionLink() { return redirectionLink; }
        public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    }
    
    public static class HandpickedDealDto {
        private String id;
        private String imageUrl;
        private String redirectionLink;
        
        public HandpickedDealDto() {}
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getRedirectionLink() { return redirectionLink; }
        public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    }
    
    public static class CategoryDto {
        private String id;
        private String label;
        private String imageUrl;
        private String redirectionLink;
        
        public CategoryDto() {}
        
        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        
        public String getRedirectionLink() { return redirectionLink; }
        public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    }
}