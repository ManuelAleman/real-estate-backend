package com.realestate.realestate.dto.seller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerApplicationRequest {
    
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;
    
    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 200, message = "Address must be between 5 and 200 characters")
    private String address;
    
    @Size(max = 100, message = "Company name must not exceed 100 characters")
    private String companyName;
    
    @Size(max = 50, message = "License number must not exceed 50 characters")
    private String licenseNumber;
    
    @Size(max = 500, message = "Bio must not exceed 500 characters")
    private String bio;
}
