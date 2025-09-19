package com.brand.artifact.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.UuidGenerator;

import com.brand.artifact.constant.PaymentMethod;
import com.brand.artifact.constant.PaymentStatus;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @UuidGenerator
    private String paymentId;

    private Double amount;
    private String currency; // VND, USD...
    
    @Enumerated(EnumType.STRING)
    private PaymentMethod method;   // VNPAY, MOMO, CASH...
    
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;   // PENDING, SUCCESS, FAILED, REFUNDED
    private String externalRef;
    private LocalDateTime paidAt;

    @ManyToOne
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
}
