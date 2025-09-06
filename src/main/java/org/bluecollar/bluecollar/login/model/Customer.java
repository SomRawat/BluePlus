package org.bluecollar.bluecollar.login.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Document(collection = "customers")
public class Customer {
    
    @Id
    private String id;

    @Indexed(unique = true, sparse = true)
    private String mobile;
    
    private String email;
    private String name;
    private String profilePhoto;
    private String phoneCode;
    private String relationType;
    private LocalDate dob;
    private String country;
    
    @Indexed(unique = true, sparse = true)
    private String googleId;
    private String address;
    private String city;
    private String state;
    private String pincode;
    
    // Track coupon redemption clicks
    private java.util.List<String> redeemedCoupons = new java.util.ArrayList<>();
    
    @CreatedDate
    private LocalDateTime createdAt;
    
    @LastModifiedDate
    private LocalDateTime updatedAt;

}