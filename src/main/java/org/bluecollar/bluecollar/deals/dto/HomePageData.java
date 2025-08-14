package org.bluecollar.bluecollar.deals.dto;

import java.util.List;
import java.util.ArrayList;

public class HomePageData {
    private List<BannerItem> banners;
    private List<PopularBrand> popularBrands;
    private List<HandpickedDeal> handpickedDeals;
    private List<CategoryItem> categories;

    public static class BannerItem {
        private String id;
        private String imageUrl;
        private String redirectionLink;

        public BannerItem() {}

        public BannerItem(String id, String imageUrl, String redirectionLink) {
            this.id = id;
            this.imageUrl = imageUrl;
            this.redirectionLink = redirectionLink;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getRedirectionLink() { return redirectionLink; }
        public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    }

    public static class PopularBrand {
        private String name;
        private String discount;
        private String imageUrl;
        private String redirectionLink;

        public PopularBrand() {}

        public PopularBrand(String name, String discount, String imageUrl, String redirectionLink) {
            this.name = name;
            this.discount = discount;
            this.imageUrl = imageUrl;
            this.redirectionLink = redirectionLink;
        }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDiscount() { return discount; }
        public void setDiscount(String discount) { this.discount = discount; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getRedirectionLink() { return redirectionLink; }
        public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    }

    public static class HandpickedDeal {
        private String id;
        private String imageUrl;
        private String redirectionLink;

        public HandpickedDeal() {}

        public HandpickedDeal(String id, String imageUrl, String redirectionLink) {
            this.id = id;
            this.imageUrl = imageUrl;
            this.redirectionLink = redirectionLink;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getRedirectionLink() { return redirectionLink; }
        public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    }

    public static class CategoryItem {
        private String id;
        private String label;
        private String imageUrl;
        private String redirectionLink;

        public CategoryItem() {}

        public CategoryItem(String id, String label, String imageUrl, String redirectionLink) {
            this.id = id;
            this.label = label;
            this.imageUrl = imageUrl;
            this.redirectionLink = redirectionLink;
        }

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getLabel() { return label; }
        public void setLabel(String label) { this.label = label; }
        public String getImageUrl() { return imageUrl; }
        public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
        public String getRedirectionLink() { return redirectionLink; }
        public void setRedirectionLink(String redirectionLink) { this.redirectionLink = redirectionLink; }
    }

    public HomePageData() {
        this.banners = new ArrayList<>();
        this.popularBrands = new ArrayList<>();
        this.handpickedDeals = new ArrayList<>();
        this.categories = new ArrayList<>();
    }

    public List<BannerItem> getBanners() { return banners; }
    public void setBanners(List<BannerItem> banners) { this.banners = banners; }
    public List<PopularBrand> getPopularBrands() { return popularBrands; }
    public void setPopularBrands(List<PopularBrand> popularBrands) { this.popularBrands = popularBrands; }
    public List<HandpickedDeal> getHandpickedDeals() { return handpickedDeals; }
    public void setHandpickedDeals(List<HandpickedDeal> handpickedDeals) { this.handpickedDeals = handpickedDeals; }
    public List<CategoryItem> getCategories() { return categories; }
    public void setCategories(List<CategoryItem> categories) { this.categories = categories; }
}