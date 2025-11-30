package com.realestate.realestate.controller;

import java.math.BigDecimal;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.realestate.dto.estate.CreateEstateRequest;
import com.realestate.realestate.dto.estate.EstateBasicResponse;
import com.realestate.realestate.dto.estate.EstateResponse;
import com.realestate.realestate.enums.EstateType;
import com.realestate.realestate.service.EstateService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/estates")
@RequiredArgsConstructor
public class EstateController {

    private final EstateService estateService;

    @GetMapping("/public/sale")
    public ResponseEntity<Page<EstateBasicResponse>> getEstatesForSale(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<EstateBasicResponse> estates = estateService.searchEstates(
                city, EstateType.SALE, minPrice, maxPrice, categoryId, pageable);

        return ResponseEntity.ok(estates);
    }

    @GetMapping("/public/rent")
    public ResponseEntity<Page<EstateBasicResponse>> getEstatesForRent(
            @RequestParam(required = false) String city,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDir) {

        Sort.Direction direction = sortDir.equalsIgnoreCase("ASC") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        Page<EstateBasicResponse> estates = estateService.searchEstates(
                city, EstateType.RENT, minPrice, maxPrice, categoryId, pageable);

        return ResponseEntity.ok(estates);
    }

    @GetMapping("/public/all")
    public ResponseEntity<Page<EstateBasicResponse>> getAllEstates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EstateBasicResponse> estates = estateService.getAllEstates(page, size);
        return ResponseEntity.ok(estates);
    }

    @GetMapping("/public/{id}")
    public ResponseEntity<EstateResponse> getEstateById(@PathVariable Long id) {
        EstateResponse estate = estateService.getEstateById(id);
        return ResponseEntity.ok(estate);
    }

    @PostMapping
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<EstateResponse> createEstate(@Valid @RequestBody CreateEstateRequest request) {
        EstateResponse estateResponse = estateService.createEstate(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(estateResponse);
    }

    @GetMapping("/my-estates")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Page<EstateResponse>> getMyEstates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EstateResponse> estates = estateService.getMyEstates(page, size);
        return ResponseEntity.ok(estates);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<Void> deleteEstate(@PathVariable Long id) {
        estateService.deleteEstate(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/sold")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<EstateResponse> markAsSold(@PathVariable Long id) {
        EstateResponse response = estateService.markEstateAsSold(id);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/rented")
    @PreAuthorize("hasRole('SELLER')")
    public ResponseEntity<EstateResponse> markAsRented(@PathVariable Long id) {
        EstateResponse response = estateService.markEstateAsRented(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<EstateResponse>> getPendingEstates(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EstateResponse> estates = estateService.getPendingEstates(page, size);
        return ResponseEntity.ok(estates);
    }

    @PatchMapping("/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstateResponse> approveEstate(@PathVariable Long id) {
        EstateResponse estateResponse = estateService.approveEstate(id);
        return ResponseEntity.ok(estateResponse);
    }

    @PatchMapping("/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EstateResponse> rejectEstate(@PathVariable Long id) {
        EstateResponse estateResponse = estateService.rejectEstate(id);
        return ResponseEntity.ok(estateResponse);
    }

}
