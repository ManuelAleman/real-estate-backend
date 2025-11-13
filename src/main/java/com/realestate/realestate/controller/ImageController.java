package com.realestate.realestate.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.realestate.dto.image.PresignedUrlResponse;
import com.realestate.realestate.service.ImageService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final ImageService imageService;
    
    @PostMapping("/estates/presigned-urls")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<List<PresignedUrlResponse>> generateEstateImageUrls(
            @RequestParam @Min(1) @Max(20) int count,
            @RequestParam(defaultValue = "image/jpeg") String contentType) {
        
        List<PresignedUrlResponse> urls = imageService.generateEstateImageUrls(count, contentType);
        return ResponseEntity.ok(urls);
    }
}
