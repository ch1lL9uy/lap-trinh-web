package com.brand.artifact.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    
    // Tìm theo tên category (unique)
    Optional<Category> findByCategoryName(String categoryName);

    // Check tồn tại
    boolean existsByCategoryName(String categoryName);
    
    // Tìm theo slug (SEO friendly)
    Optional<Category> findBySlug(String slug);

    boolean existsBySlug(String slug);
    
    // Tìm categories cha (không có parent)
    List<Category> findByParentCategoryIsNull();
    
    // Tìm subcategories của một category
    List<Category> findByParentCategory_CategoryId(String parentCategoryId);
}