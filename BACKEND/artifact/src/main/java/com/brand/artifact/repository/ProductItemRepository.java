package com.brand.artifact.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brand.artifact.constant.Size;
import com.brand.artifact.entity.Product;
import com.brand.artifact.entity.ProductItem;

import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;


@Repository
public interface ProductItemRepository extends JpaRepository<ProductItem, String> {
    
    // Tìm variants của một product
    List<ProductItem> findByProduct(Product product);
    
    // Tìm theo SKU (unique)
    Optional<ProductItem> findBySku(String sku);
    
    // Tìm theo product và size
    @Enumerated(EnumType.STRING)
    List<ProductItem> findByProductAndSize(Product product, Size size);
    
    // Tìm theo product và color
    List<ProductItem> findByProductAndColor(Product product, String color);
    
    // Tìm sản phẩm còn hàng
    List<ProductItem> findByStockQuantityGreaterThan(Integer quantity);
    
    // Custom query: Tìm variants available của một product
    @Query("SELECT pi FROM ProductItem pi WHERE pi.product = :product AND pi.stockQuantity > 0")
    List<ProductItem> findAvailableVariantsByProduct(@Param("product") Product product);
}