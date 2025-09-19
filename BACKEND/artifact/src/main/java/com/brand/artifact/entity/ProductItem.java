package com.brand.artifact.entity;

import java.util.List;

import org.hibernate.annotations.UuidGenerator;

import com.brand.artifact.constant.Size;

import jakarta.persistence.Entity;
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
@Table(name = "product_items")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductItem {
    @Id
    @UuidGenerator
    private String productItemId;

    private String sku;
    private Double price;
    private Integer stockQuantity;
    private Size size;   
    private String color;  

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @OneToMany(mappedBy = "productItem")
    private List<CartItem> cartItems;
}