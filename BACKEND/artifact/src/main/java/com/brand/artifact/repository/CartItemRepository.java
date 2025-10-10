package com.brand.artifact.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.Cart;
import com.brand.artifact.entity.CartItem;
import com.brand.artifact.entity.ProductItem;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, String> {
    List<CartItem> findByCart(Cart cart);
    Optional<CartItem> findByCartAndProductItem(Cart cart, ProductItem productItem);
    void deleteByCart(Cart cart);
}