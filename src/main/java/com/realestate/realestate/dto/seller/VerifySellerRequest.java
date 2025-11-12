package com.realestate.realestate.dto.seller;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifySellerRequest {
    
    @NotNull(message = "Approved status is required")
    private Boolean approved;
    
    @Size(max = 500, message = "Notes must not exceed 500 characters")
    private String notes;
}
