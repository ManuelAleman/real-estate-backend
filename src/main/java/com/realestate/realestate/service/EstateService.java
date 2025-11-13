package com.realestate.realestate.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.realestate.realestate.dto.estate.CreateEstateRequest;
import com.realestate.realestate.dto.estate.EstateResponse;
import com.realestate.realestate.dto.seller.SellerResponse;
import com.realestate.realestate.entity.Category;
import com.realestate.realestate.entity.Estate;
import com.realestate.realestate.entity.EstateCharacteristic;
import com.realestate.realestate.entity.EstateImage;
import com.realestate.realestate.entity.Seller;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.enums.EstateStatus;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.repository.CategoryRepository;
import com.realestate.realestate.repository.EstateRepository;
import com.realestate.realestate.repository.SellerRepository;
import com.realestate.realestate.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EstateService {
    
    private final EstateRepository estateRepository;
    private final CategoryRepository categoryRepository;
    private final SellerRepository sellerRepository;
    private final UserRepository userRepository;

    @Transactional
    public EstateResponse createEstate(CreateEstateRequest request) {
        log.info("Creating estate with name: {}", request.getName());

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Seller seller = sellerRepository.findByUser(user)
                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found for user"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + request.getCategoryId()));

        Estate estate = Estate.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .type(request.getType())
                .category(category)
                .seller(seller)
                .city(request.getCity())
                .address(request.getAddress())
                .status(EstateStatus.WAITING_FOR_APPROVAL)
                .build();

        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            List<EstateImage> images = request.getImageUrls().stream()
                    .map(url -> EstateImage.builder()
                            .s3url(url)
                            .estate(estate)
                            .build())
                    .collect(Collectors.toList());
            estate.setImages(images);
        }

        if (request.getCharacteristics() != null && !request.getCharacteristics().isEmpty()) {
            List<EstateCharacteristic> characteristics = request.getCharacteristics().stream()
                    .map(charReq -> EstateCharacteristic.builder()
                            .name(charReq.getName())
                            .value(charReq.getValue())
                            .estate(estate)
                            .build())
                    .collect(Collectors.toList());
            
            estate.setCharacteristics(characteristics);
        }

        Estate savedEstate = estateRepository.save(estate);
        log.info("Estate created successfully with id: {}", savedEstate.getId());

        return buildEstateResponse(savedEstate);
    }

    private EstateResponse buildEstateResponse(Estate estate) {
        List<EstateResponse.EstateImageResponse> imageResponses = estate.getImages().stream()
                .map(img -> EstateResponse.EstateImageResponse.builder()
                        .id(img.getId())
                        .url(img.getS3url())
                        .build())
                .collect(Collectors.toList());

        List<EstateResponse.CharacteristicResponse> characteristicResponses = estate.getCharacteristics().stream()
                .map(char_ -> EstateResponse.CharacteristicResponse.builder()
                        .id(char_.getId())
                        .name(char_.getName())
                        .value(char_.getValue())
                        .build())
                .collect(Collectors.toList());

        SellerResponse sellerResponse = SellerResponse.builder()
                .id(estate.getSeller().getId())
                .city(estate.getSeller().getCity())
                .address(estate.getSeller().getAddress())
                .companyName(estate.getSeller().getCompanyName())
                .licenseNumber(estate.getSeller().getLicenseNumber())
                .bio(estate.getSeller().getBio())
                .status(estate.getSeller().getStatus())
                .rating(estate.getSeller().getRating())
                .build();

        return EstateResponse.builder()
                .id(estate.getId())
                .name(estate.getName())
                .description(estate.getDescription())
                .price(estate.getPrice())
                .type(estate.getType())
                .status(estate.getStatus())
                .city(estate.getCity())
                .address(estate.getAddress())
                .categoryId(estate.getCategory().getId())
                .categoryName(estate.getCategory().getName())
                .seller(sellerResponse)
                .images(imageResponses)
                .characteristics(characteristicResponses)
                .createdAt(estate.getCreatedAt())
                .updatedAt(estate.getUpdatedAt())
                .build();
    }
}
