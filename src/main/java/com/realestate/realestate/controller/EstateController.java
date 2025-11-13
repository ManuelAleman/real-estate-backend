package com.realestate.realestate.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.realestate.dto.estate.CreateEstateRequest;
import com.realestate.realestate.dto.estate.EstateResponse;
import com.realestate.realestate.service.EstateService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/estates")
@RequiredArgsConstructor
public class EstateController {
    
    private final EstateService estateService;

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<EstateResponse> createEstate(@Valid @RequestBody CreateEstateRequest request) {
        EstateResponse estateResponse = estateService.createEstate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(estateResponse);
    }
}
