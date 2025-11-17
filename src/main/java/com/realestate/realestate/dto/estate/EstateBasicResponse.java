package com.realestate.realestate.dto.estate;

import com.realestate.realestate.enums.EstateType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EstateBasicResponse {
    private Long id;
    private String name;
    private Double price;
    private EstateType type;
    private String city;
    private String address;
    private String mainImageUrl;
}
