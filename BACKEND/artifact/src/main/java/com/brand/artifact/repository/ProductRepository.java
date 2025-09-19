package com.brand.artifact.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.Category;
import com.brand.artifact.entity.Product;

@Repository
public interface ProductRepository extends JpaRepository<Product, String> {
    List<Product> findByProductName(String productName);
    List<Product> findByCategory(Category category);
    List<Product> findByProductNameContainingIgnoreCase(String productName); 

}
