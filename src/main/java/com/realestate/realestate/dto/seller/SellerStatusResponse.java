package com.realestate.realestate.dto.seller;

import java.time.LocalDateTime;

import com.realestate.realestate.enums.SellerStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SellerStatusResponse {
    
    private Long id;
    private SellerStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime verifiedAt;
    private String verificationNotes;
    private String city;
    private String address;
    private String companyName;
    private String licenseNumber;
    private String bio;
}
