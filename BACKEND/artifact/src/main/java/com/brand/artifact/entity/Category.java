package com.brand.artifact.entity;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @UuidGenerator
    private String categoryId;

    // 1. Tên category rõ ràng hơn
    @Column(name = "category_name", nullable = false, length = 100)
    private String categoryName;  // thay vì nameCategory
    
    // 2. Mô tả category
    @Column(name = "description", length = 500)
    private String description;
    
    // 3. Image URL
    @Column(name = "image_url")
    private String imageUrl;

    // 4. Hierarchy support (Category con/cha)
    @Column(name = "parent_id")
    private String parentId; 
    
    // 5. Display order
    @Column(name = "sort_order")
    private Integer sortOrder;
    
    // 7. SEO friendly slug
    @Column(name = "slug", unique = true)
    private String slug;  // "ao-thun-nam", "quan-jean-nu"
    
    // 8. Timestamps
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 9. Relationships
    @OneToMany(mappedBy = "category", fetch = FetchType.LAZY)
    private List<Product> products;
    
    // 10. Self-referencing for subcategories
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", insertable = false, updatable = false)
    private Category parentCategory;
    
    @OneToMany(mappedBy = "parentCategory", fetch = FetchType.LAZY)
    private List<Category> subCategories;
}
