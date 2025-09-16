package com.brand.artifact.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "sizes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Size {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long sizeId;
    
    @Column(name = "size_name", nullable = false)
    private String sizeName;  // "S", "M", "L", "38", "39"
    
    @Column(name = "size_code")
    private String sizeCode;  // "SM", "MD", "LG"
    
    @Column(name = "category")
    private String category;  // "CLOTHING", "SHOES", "ACCESSORIES"
    
    @Column(name = "sort_order")
    private Integer sortOrder;  // Thứ tự hiển thị: S(1), M(2), L(3)
    
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    
    @CreationTimestamp
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

}
