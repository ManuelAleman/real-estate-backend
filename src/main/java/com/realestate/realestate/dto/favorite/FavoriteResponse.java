package com.realestate.realestate.dto.favorite;

import java.time.LocalDateTime;

import com.realestate.realestate.dto.estate.EstateResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteResponse {
    private Long id;
    private EstateResponse estate;
    private LocalDateTime createdAt;
}
