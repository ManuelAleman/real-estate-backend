package com.realestate.realestate.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.realestate.dto.estate.EstateResponse;
import com.realestate.realestate.dto.favorite.FavoriteCheckResponse;
import com.realestate.realestate.dto.favorite.FavoriteCountResponse;
import com.realestate.realestate.dto.favorite.FavoriteResponse;
import com.realestate.realestate.entity.Estate;
import com.realestate.realestate.entity.Favorite;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.enums.EstateStatus;
import com.realestate.realestate.exception.common.DuplicateResourceException;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.repository.EstateRepository;
import com.realestate.realestate.repository.FavoriteRepository;
import com.realestate.realestate.util.SecurityUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class FavoriteService {
    private final FavoriteRepository favoriteRepository;
    private final EstateRepository estateRepository;
    private final EstateService estateService;
    private final SecurityUtil securityUtil;

    @Transactional
    public FavoriteResponse addToFavorites(Long estateId) {
        log.info("Adding estate {} to favorites", estateId);

        User currentUser = securityUtil.getCurrentUser();
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + estateId));

        if (estate.getStatus() != EstateStatus.APPROVED) {
            throw new IllegalStateException("Only approved estates can be favorited");
        }

        if (favoriteRepository.existsByUserAndEstate(currentUser, estate)) {
            throw new DuplicateResourceException("Estate is already in favorites");
        }

        Favorite favorite = Favorite.builder()
                .user(currentUser)
                .estate(estate)
                .build();

        Favorite savedFavorite = favoriteRepository.save(favorite);

        log.info("Estate {} added to favorites successfully", estateId);
        return buildFavoriteResponse(savedFavorite);
    }

    @Transactional
    public void removeFromFavorites(Long estateId) {
        log.info("Removing estate {} from favorites", estateId);

        User currentUser = securityUtil.getCurrentUser();

        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + estateId));

        Favorite favorite = favoriteRepository.findByUserAndEstate(currentUser, estate)
                .orElseThrow(() -> new ResourceNotFoundException("Favorite not found for the given estate and user"));

        favoriteRepository.delete(favorite);
        log.info("Estate {} removed from favorites successfully", estateId);
    }

    @Transactional(readOnly = true)
    public Page<FavoriteResponse> getMyFavorites(int page, int size) {
        log.info("Fetching user favorites - page: {}, size: {}", page, size);

        User currentUser = securityUtil.getCurrentUser();
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Favorite> favorites = favoriteRepository.findByUserOrderByCreatedAtDesc(currentUser, pageable);

        return favorites.map(this::buildFavoriteResponse);
    }

    @Transactional(readOnly = true)
    public FavoriteCheckResponse isFavorite(Long estateId) {
        log.info("Checking if estate {} is in favorites", estateId);

        User currentUser = securityUtil.getCurrentUser();
        Estate estate = estateRepository.findById(estateId)
                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + estateId));
        Boolean isFavorite = favoriteRepository.existsByUserAndEstate(currentUser, estate);
        return buildFavoriteCheckResponse(isFavorite);
    }

    @Transactional(readOnly = true)
    public FavoriteCountResponse countMyFavorites() {
        log.info("Counting user favorites");

        User currentUser = securityUtil.getCurrentUser();
        Long count = favoriteRepository.countByUser(currentUser);
        return buildFavoriteCountResponse(count);
    }

    private FavoriteResponse buildFavoriteResponse(Favorite favorite) {
        EstateResponse estateResponse = estateService.buildEstateResponse(favorite.getEstate());

        return FavoriteResponse.builder()
                .id(favorite.getId())
                .estate(estateResponse)
                .createdAt(favorite.getCreatedAt())
                .build();
    }

    private FavoriteCountResponse buildFavoriteCountResponse(long count) {
        return FavoriteCountResponse.builder()
                .count(count)
                .build();
    }

    private FavoriteCheckResponse buildFavoriteCheckResponse(boolean isFavorite) {
        return FavoriteCheckResponse.builder()
                .isFavorite(isFavorite)
                .build();
    }
}
