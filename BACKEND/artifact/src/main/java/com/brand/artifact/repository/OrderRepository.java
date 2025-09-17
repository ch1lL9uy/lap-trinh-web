package com.brand.artifact.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.constant.OrderStatus;
import com.brand.artifact.entity.Order;
import com.brand.artifact.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Tìm orders của user
    List<Order> findByUser(User user);
    
    // Tìm orders theo status
    List<Order> findByStatus(OrderStatus status);
    
    // Tìm orders của user theo status
    List<Order> findByUserAndStatus(User user, OrderStatus status);
}