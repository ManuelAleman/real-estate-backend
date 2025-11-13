package com.realestate.realestate.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.realestate.realestate.entity.Estate;

@Repository
public interface EstateRepository extends JpaRepository<Estate, Long>{
    
}
