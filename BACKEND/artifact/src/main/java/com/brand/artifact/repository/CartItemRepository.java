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
    
    // Tìm tất cả items trong cart
    List<CartItem> findByCart(Cart cart);
    
    // Tìm theo cart và productItem (để check duplicate)
    Optional<CartItem> findByCartAndProductItem(Cart cart, ProductItem productItem);
    
    // Xóa tất cả items trong cart
    void deleteByCart(Cart cart);
}