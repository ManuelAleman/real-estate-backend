package com.realestate.realestate.dto.estate;

import java.time.LocalDateTime;
import java.util.List;

import com.realestate.realestate.dto.seller.SellerResponse;
import com.realestate.realestate.enums.EstateStatus;
import com.realestate.realestate.enums.EstateType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstateResponse {
    
    private Long id;
    private String name;
    private String description;
    private Double price;
    private EstateType type; 
    private EstateStatus status; 
    
    private String city;
    private String address;
    
    private Long categoryId;
    private String categoryName;
    
    private SellerResponse seller;
    
    private List<EstateImageResponse> images;
    
    private List<CharacteristicResponse> characteristics;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EstateImageResponse {
        private Long id;
        private String url;
    }
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CharacteristicResponse {
        private Long id;
        private String name;
        private String value;
    }
}
