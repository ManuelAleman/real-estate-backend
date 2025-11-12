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
public class SellerResponse {
    
    private Long id;
    private Long userId;
    private String userName;
    private String userEmail;
    private String userPhone;
    private String userProfilePicture;
    
    private String city;
    private String address;
    private String companyName;
    private String licenseNumber;
    private String bio;
    
    private SellerStatus status;
    private Double rating;
    private LocalDateTime verifiedAt;
    private LocalDateTime createdAt;
    
    // Para admins (opcional, se puede omitir en respuestas públicas)
    private String verificationNotes;
}
