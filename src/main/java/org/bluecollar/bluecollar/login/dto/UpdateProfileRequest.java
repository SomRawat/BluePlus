package org.bluecollar.bluecollar.login.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UpdateProfileRequest {
    private String mobile;
    private String name;
    private String email;
    private String phoneCode;
    private String relationType;
    private LocalDate dob;
    private String country;
    private String address;
    private String city;
    private String state;
    private String pincode;
}