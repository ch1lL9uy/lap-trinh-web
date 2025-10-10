package com.brand.artifact.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    Optional<Category> findByCategoryName(String categoryName);
    boolean existsByCategoryName(String categoryName);
    Optional<Category> findBySlug(String slug);
    boolean existsBySlug(String slug);
    List<Category> findByParentCategoryIsNull();
    List<Category> findByParentCategory_CategoryId(String parentCategoryId);
}