package org.bluecollar.bluecollar.deals.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.ArrayList;

@Data
public class HomePageData {
    private List<BannerItem> banners = new ArrayList<>();
    private List<PopularBrand> popularBrands = new ArrayList<>();
    private List<HandpickedDeal> handpickedDeals = new ArrayList<>();
    private List<CategoryItem> categories = new ArrayList<>();
    private boolean isActive = true;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BannerItem implements HasId {
        private String id;
        private String imageUrl;
        private String redirectionLink;
        private String categoryId;
        private String pdpId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PopularBrand implements HasId {
        private String id;
        private String name;
        private String discount;
        private String imageUrl;
        private String redirectionLink;
        private String categoryId;
        private String pdpId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class HandpickedDeal implements HasId {
        private String id;
        private String imageUrl;
        private String redirectionLink;
        private String categoryId;
        private String pdpId;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryItem implements HasId {
        private String id;
        private String label;
        private String imageUrl;
        private String redirectionLink;
        private String categoryId;
        private String pdpId;
    }
}