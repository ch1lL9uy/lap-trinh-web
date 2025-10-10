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
    List<ProductItem> findByProduct(Product product);

    Optional<ProductItem> findBySku(String sku);

    @Enumerated(EnumType.STRING)
    List<ProductItem> findByProductAndSize(Product product, Size size);

    List<ProductItem> findByProductAndColor(Product product, String color);

    List<ProductItem> findByStockQuantityGreaterThan(Integer quantity);

    @Query("SELECT pi FROM ProductItem pi WHERE pi.product = :product AND pi.stockQuantity > 0")
    List<ProductItem> findAvailableVariantsByProduct(@Param("product") Product product);
}