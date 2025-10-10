package com.brand.artifact.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.Cart;
import com.brand.artifact.entity.User;

@Repository
public interface CartRepository extends JpaRepository<Cart, String> {
    Optional<Cart> findByUser(User user);
    Optional<Cart> findByUserUserId(String userId);
    boolean existsByUser(User user);
}