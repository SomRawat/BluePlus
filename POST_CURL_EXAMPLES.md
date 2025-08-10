# POST CURL Examples

## Create Home Page Data
```bash
curl -X POST http://localhost:8080/api/pages/home \
  -H "Content-Type: application/json" \
  -d '{
    "banners": [
        {
            "id": "12323dasdasd",
            "imageUrl": "https://static.startuptalky.com/2022/12/Myntra-Logo-Success-Story-Startuptalky--1-.jpg",
            "redirectionLink": "/dealDetails/uniqueID"
        }
    ],
    "popularBrands": [
        {
            "name": "Vodafone",
            "discount": "Upto 12% Off",
            "imageUrl": "https://static.startuptalky.com/2022/12/Myntra-Logo-Success-Story-Startuptalky--1-.jpg",
            "redirectionLink": "/dealDetails/uniqueID"
        }
    ],
    "handpickedDeals": [
        {
            "id": "12323dasdasd",
            "imageUrl": "https://static.startuptalky.com/2022/12/Myntra-Logo-Success-Story-Startuptalky--1-.jpg",
            "redirectionLink": "/dealDetails/uniqueID"
        }
    ],
    "categories": [
        {
            "id": "aassdssss",
            "label": "Fashion",
            "imageUrl": "https://static.startuptalky.com/2022/12/Myntra-Logo-Success-Story-Startuptalky--1-.jpg",
            "redirectionLink": "/categories/uniqueID"
        }
    ]
}'
```

## Create PLP Data
```bash
curl -X POST http://localhost:8080/api/pages/category/fashion123 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Fashion",
    "tabs": ["All Deals", "Nearby", "Popular", "Trending", "Latest", "Top Rated"],
    "activeTab": "All Details",
    "offers": [
        {
            "id": "2132",
            "brand": "First Fashion",
            "discount": "17% Off",
            "discountLabel": "On clothing",
            "imageUrl": ""
        }
    ]
}'
```

## Create PDP Data
```bash
curl -X POST http://localhost:8080/api/pages/brand/myntra123 \
  -H "Content-Type: application/json" \
  -d '{
    "brandName": "Myntra",
    "bannerLink": "https://static.startuptalky.com/2022/12/Myntra-Logo-Success-Story-Startuptalky--1-.jpg",
    "brandDescription": "Myntra is a one stop shop for all your fashion and lifestyle needs.",
    "discountText": "15% off Lorem ipsum dolor sit amet.",
    "validTill": "Valid till: Jun 15, 2025",
    "howItWorksBullets": [
        "Lorem ipsum dolor sit amet, consectetur adi lit."
    ],
    "benefits": [
        "Lorem ipsum dolor sit amet, consectetur adi lit amet, consectetur adi."
    ],
    "howToRedeemBullets": [
        "Lorem ipsum dolor sit amet, consectetur adi lit."
    ],
    "termsAndConditions": [
        "Lorem ipsum dolor sit amet, consectetur adi lit."
    ],
    "faq": [
        {
            "question": "Lorem ipsum dolor sit amet?",
            "answer": "Lorem ipsum dolor sit amet, consectetur adipiscing elit."
        }
    ],
    "redeemLink": "/scratchCards/id"
}'
```