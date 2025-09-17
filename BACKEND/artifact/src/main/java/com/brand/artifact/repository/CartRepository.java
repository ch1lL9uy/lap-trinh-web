package com.brand.artifact.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.Cart;
import com.brand.artifact.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, Long> {
    
    // Tìm cart của user (mỗi user chỉ có 1 cart)
    Optional<Cart> findByUser(User user);
    
    // Tìm cart theo userId
    Optional<Cart> findByUserUserId(Long userId);
}