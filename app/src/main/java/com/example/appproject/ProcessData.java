package com.example.appproject;

import android.graphics.Bitmap;

public class ProcessData {
    private String productName;
    private String brandName;
    private String price;
    private String link;
    private String imageUrl; // 이미지 URL을 저장할 변수 추가

        public ProcessData(String productName, String brandName, String price, String link, Bitmap imageUrl) {
            this.productName = productName;
            this.brandName = brandName;
            this.price = price;
            this.link = link;
            this.imageUrl = imageUrl.toString();
        }

        public String getBrandName() {
            return brandName;
        }

        public String getProductName() {
            return productName;
        }

        public String getImageUrl() {
            return imageUrl;
        }

        public String getPrice() {
            return price;
        }
    }


