package com.realestate.realestate.repository;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.realestate.realestate.entity.Estate;
import com.realestate.realestate.entity.Seller;
import com.realestate.realestate.enums.EstateStatus;
import com.realestate.realestate.enums.EstateType;

@Repository
public interface EstateRepository extends JpaRepository<Estate, Long> {
        Page<Estate> findByStatus(EstateStatus status, Pageable pageable);

        @Query("SELECT e FROM Estate e WHERE " +
                        "(:city IS NULL OR e.city = :city) AND " +
                        "(:type IS NULL OR e.type = :type) AND " +
                        "(:minPrice IS NULL OR e.price >= :minPrice) AND " +
                        "(:maxPrice IS NULL OR e.price <= :maxPrice) AND " +
                        "(:categoryId IS NULL OR e.category.id = :categoryId) AND " +
                        "e.status = 'APPROVED'")
        Page<Estate> searchEstates(
                        @Param("city") String city,
                        @Param("type") EstateType type,
                        @Param("minPrice") BigDecimal minPrice,
                        @Param("maxPrice") BigDecimal maxPrice,
                        @Param("categoryId") Long categoryId,
                        Pageable pageable);

        Page<Estate> findBySeller(Seller seller, Pageable pageable);
}
