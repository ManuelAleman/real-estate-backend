package com.realestate.realestate.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import com.realestate.realestate.enums.EstateType;
import com.realestate.realestate.exception.common.ResourceNotFoundException;
import com.realestate.realestate.exception.estate.InvalidEstateStatusException;
import com.realestate.realestate.exception.estate.InvalidEstateTypeException;
import com.realestate.realestate.repository.CategoryRepository;
import com.realestate.realestate.repository.EstateRepository;
import com.realestate.realestate.repository.SellerRepository;
import com.realestate.realestate.repository.UserRepository;
import com.realestate.realestate.util.SecurityUtil;

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
        private final ImageService imageService;

        @Transactional(readOnly = true)
        public Page<EstateResponse> getAllEstates(int page, int size) {
                log.info("Fetching estates - page: {}, size: {}", page, size);
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                Page<Estate> estatesPage = estateRepository.findByStatus(EstateStatus.APPROVED, pageable);
                return estatesPage.map(this::buildEstateResponse);
        }

        @Transactional(readOnly = true)
        public Page<EstateResponse> searchEstates(
                        String city,
                        EstateType type,
                        BigDecimal minPrice,
                        BigDecimal maxPrice,
                        Long categoryId,
                        Pageable pageable) {
                log.info("Searching estates with filters - city: {}, type: {}, minPrice: {}, maxPrice: {}, categoryId: {}",
                                city, type, minPrice, maxPrice, categoryId);
                Page<Estate> estatesPage = estateRepository.searchEstates(city, type, minPrice, maxPrice, categoryId,
                                pageable);
                return estatesPage.map(this::buildEstateResponse);
        }

        @Transactional(readOnly = true)
        public EstateResponse getEstateById(Long id) {
                log.info("Fetching estate with id: {}", id);
                Estate estate = estateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + id));
                log.info("Estate fetched successfully with id: {}", id);
                return buildEstateResponse(estate);
        }

        @Transactional
        public EstateResponse createEstate(CreateEstateRequest request) {
                log.info("Creating estate with name: {}", request.getName());

                User currUser = SecurityUtil.getCurrentUser();

                Seller seller = sellerRepository.findByUser(currUser)
                                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found for user"));

                Category category = categoryRepository.findById(request.getCategoryId())
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Category not found with id: " + request.getCategoryId()));

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

        @Transactional(readOnly = true)
        public Page<EstateResponse> getMyEstates(int page, int size) {
                log.info("Fetching my estates - page: {}, size: {}", page, size);
                User currUser = SecurityUtil.getCurrentUser();

                Seller seller = sellerRepository.findByUser(currUser)
                                .orElseThrow(() -> new ResourceNotFoundException("Seller profile not found for user"));

                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
                Page<Estate> estatesPage = estateRepository.findBySeller(seller, pageable);
                return estatesPage.map(this::buildEstateResponse);
        }

        @Transactional
        public void deleteEstate(Long id) {
                log.info("Deleting estate with id: {}", id);
                Estate estate = estateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + id));
                
                if (estate.getImages() != null && !estate.getImages().isEmpty()) {
                        List<String> imageUrls = estate.getImages().stream()
                                        .map(EstateImage::getS3url)
                                        .collect(Collectors.toList());
                        
                        log.info("Deleting {} images from S3 for estate id: {}", imageUrls.size(), id);
                        imageService.deleteImages(imageUrls);
                }
                
                estateRepository.delete(estate);
                log.info("Estate and associated images deleted successfully with id: {}", id);
        }

        @Transactional
        public EstateResponse markEstateAsSold(Long id) {
                log.info("Marking estate as SOLD with id: {}", id);
                Estate estate = estateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + id));

                if (estate.getStatus() != EstateStatus.APPROVED) {
                        throw new InvalidEstateStatusException(
                                        "Only estates with status APPROVED can be marked as SOLD");
                }

                if (estate.getType() != EstateType.SALE) {
                        throw new InvalidEstateTypeException(
                                        "Only estates of type SALE can be marked as SOLD");
                }

                estate.setStatus(EstateStatus.SOLD);
                Estate soldEstate = estateRepository.save(estate);

                log.info("Estate marked as SOLD successfully with id: {}", id);
                return buildEstateResponse(soldEstate);
        }

        @Transactional
        public EstateResponse markEstateAsRented(Long id) {
                log.info("Marking estate as RENTED with id: {}", id);
                Estate estate = estateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + id));

                if (estate.getStatus() != EstateStatus.APPROVED) {
                        throw new InvalidEstateStatusException(
                                        "Only estates with status APPROVED can be marked as RENTED");
                }

                if (estate.getType() != EstateType.RENT) {
                        throw new InvalidEstateTypeException(
                                        "Only estates of type RENTAL can be marked as RENTED");
                }

                estate.setStatus(EstateStatus.RENTED);
                Estate rentedEstate = estateRepository.save(estate);

                log.info("Estate marked as RENTED successfully with id: {}", id);
                return buildEstateResponse(rentedEstate);
        }

        @Transactional(readOnly = true)
        public Page<EstateResponse> getPendingEstates(int page, int size) {
                log.info("Fetching pending estates - page: {}, size: {}", page, size);
                Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").ascending());
                Page<Estate> estatesPage = estateRepository.findByStatus(EstateStatus.WAITING_FOR_APPROVAL, pageable);
                return estatesPage.map(this::buildEstateResponse);
        }

        @Transactional
        public EstateResponse approveEstate(Long id) {
                log.info("Approving estate with id: {}", id);
                Estate estate = estateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + id));

                if (estate.getStatus() != EstateStatus.WAITING_FOR_APPROVAL) {
                        throw new InvalidEstateStatusException(
                                        "Only estates with status WAITING_FOR_APPROVAL can be approved");
                }

                estate.setStatus(EstateStatus.APPROVED);
                Estate approvedEstate = estateRepository.save(estate);

                log.info("Estate approved successfully with id: {}", id);
                return buildEstateResponse(approvedEstate);
        }

        @Transactional
        public EstateResponse rejectEstate(Long id) {
                log.info("Rejecting estate with id: {}", id);
                Estate estate = estateRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException("Estate not found with id: " + id));
                if (estate.getStatus() != EstateStatus.WAITING_FOR_APPROVAL) {
                        throw new InvalidEstateStatusException(
                                        "Only estates with status WAITING_FOR_APPROVAL can be rejected");
                }

                estate.setStatus(EstateStatus.REJECTED);
                Estate rejectedEstate = estateRepository.save(estate);

                log.info("Estate rejected successfully with id: {}", id);
                return buildEstateResponse(rejectedEstate);
        }

        EstateResponse buildEstateResponse(Estate estate) {
                List<EstateResponse.EstateImageResponse> imageResponses = estate.getImages().stream()
                                .map(img -> EstateResponse.EstateImageResponse.builder()
                                                .id(img.getId())
                                                .url(img.getS3url())
                                                .build())
                                .collect(Collectors.toList());

                List<EstateResponse.CharacteristicResponse> characteristicResponses = estate.getCharacteristics()
                                .stream()
                                .map(char_ -> EstateResponse.CharacteristicResponse.builder()
                                                .id(char_.getId())
                                                .name(char_.getName())
                                                .value(char_.getValue())
                                                .build())
                                .collect(Collectors.toList());

                SellerResponse sellerResponse = SellerResponse.builder()
                                .id(estate.getSeller().getId())
                                .userId(estate.getSeller().getUser().getId())
                                .userName(estate.getSeller().getUser().getName().concat(" ").concat(estate.getSeller().getUser().getLastName()))
                                .userEmail(estate.getSeller().getUser().getEmail())
                                .userPhone(estate.getSeller().getUser().getContactNumber())
                                .userProfilePicture(estate.getSeller().getUser().getProfilePicture())
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
