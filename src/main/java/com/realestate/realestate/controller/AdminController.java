package com.realestate.realestate.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.realestate.dto.auth.MessageResponse;
import com.realestate.realestate.dto.seller.SellerResponse;
import com.realestate.realestate.dto.seller.VerifySellerRequest;
import com.realestate.realestate.service.SellerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final SellerService sellerService;

    @GetMapping("/sellers/pending")
    public ResponseEntity<List<SellerResponse>> getPendingSellers() {
        List<SellerResponse> pendingSellers = sellerService.getPendingSellers();
        return ResponseEntity.ok(pendingSellers);
    }

    @PutMapping("/sellers/{id}/verify")
    public ResponseEntity<MessageResponse> verifySeller(
            @PathVariable Long id,
            @Valid @RequestBody VerifySellerRequest request) {
        
        sellerService.verifySeller(id, request);
        
        String message = request.getApproved() 
            ? "Seller approved successfully" 
            : "Seller rejected successfully";
        
        return ResponseEntity.ok(MessageResponse.builder()
                .message(message)
                .build());
    }
}
