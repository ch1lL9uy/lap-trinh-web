package com.brand.artifact.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    
    // Tìm theo tên category (unique)
    Category findByCategoryName(String categoryName);
    
    // Check tồn tại
    boolean existsByCategoryName(String categoryName);
    
    // Tìm theo slug (SEO friendly)
    Category findBySlug(String slug);
    
    // Tìm categories cha (không có parent)
    List<Category> findByParentIdIsNull();
    
    // Tìm subcategories của một category
    List<Category> findByParentId(Long parentId);
}