package com.realestate.realestate.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.realestate.realestate.dto.favorite.FavoriteCheckResponse;
import com.realestate.realestate.dto.favorite.FavoriteCountResponse;
import com.realestate.realestate.dto.favorite.FavoriteResponse;
import com.realestate.realestate.service.FavoriteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
@PreAuthorize("hasRole('USER')")
public class FavoriteController {
    private final FavoriteService favoriteService;

    @PostMapping("/{estateId}")
    public ResponseEntity<FavoriteResponse> addToFavorites(@PathVariable Long estateId) {
        FavoriteResponse response = favoriteService.addToFavorites(estateId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{estateId}")
    public ResponseEntity<Void> removeFromFavorites(@PathVariable Long estateId) {
        favoriteService.removeFromFavorites(estateId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<FavoriteResponse>> getMyFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<FavoriteResponse> favorites = favoriteService.getMyFavorites(page, size);
        return ResponseEntity.ok(favorites);
    }

    @GetMapping("/check/{estateId}")
    public ResponseEntity<FavoriteCheckResponse> isFavorite(@PathVariable Long estateId) {
        FavoriteCheckResponse response = favoriteService.isFavorite(estateId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/count")
    public ResponseEntity<FavoriteCountResponse> countFavorites() {
        FavoriteCountResponse response = favoriteService.countMyFavorites();
        return ResponseEntity.ok(response);
    }
    

}
