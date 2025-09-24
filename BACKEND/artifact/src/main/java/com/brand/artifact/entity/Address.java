package com.brand.artifact.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "addresses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Address {
    @Id
    @UuidGenerator
    @Column(name = "id")
    private String addressId;

    @Column(name = "recipient_name", nullable = false, length = 100)
    private String recipientName;

    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @Column(name = "address", nullable = false, length = 255)
    private String address; 

    @Column(name = "ward", length = 100)
    private String ward; 

    @Column(name = "district", nullable = false, length = 100)
    private String district; 

    @Column(name = "province", nullable = false, length = 100)
    private String province; 

    @Column(name = "is_default")
    @Builder.Default
    private Boolean isDefault = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public String getFullAddress() {
        StringBuilder fullAddress = new StringBuilder();
        fullAddress.append(address);
        
        if (ward != null && !ward.trim().isEmpty()) {
            fullAddress.append(", ").append(ward);
        }
        
        fullAddress.append(", ").append(district);
        fullAddress.append(", ").append(province);
        
        return fullAddress.toString();
    }
}