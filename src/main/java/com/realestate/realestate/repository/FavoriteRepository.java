package com.realestate.realestate.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.realestate.entity.Estate;
import com.realestate.realestate.entity.Favorite;
import com.realestate.realestate.entity.User;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    boolean existsByUserAndEstate(User user, Estate estate);

    Optional<Favorite> findByUserAndEstate(User user, Estate estate);

    Page<Favorite> findByUserOrderByCreatedAtDesc(User user, Pageable pageable);

    long countByUser(User user);

    long countByEstate(Estate estate);

    void deleteByUser(User user);

    void deleteByEstate(Estate estate);
}
