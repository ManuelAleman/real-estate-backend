package com.realestate.realestate.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.realestate.realestate.entity.Seller;
import com.realestate.realestate.entity.User;
import com.realestate.realestate.enums.SellerStatus;

public interface SellerRepository extends JpaRepository<Seller, Long> {
    
    Optional<Seller> findByUser(User user);
    
    boolean existsByUser(User user);
    
    @Query("SELECT s FROM Seller s JOIN FETCH s.user WHERE s.status = :status")
    List<Seller> findByStatus(SellerStatus status);
    
    @Query("SELECT s FROM Seller s JOIN FETCH s.user WHERE s.id = :id")
    Optional<Seller> findByIdWithUser(Long id);
}
