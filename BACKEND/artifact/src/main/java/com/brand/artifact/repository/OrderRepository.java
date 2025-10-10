package com.brand.artifact.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brand.artifact.constant.OrderStatus;
import com.brand.artifact.entity.Order;
import com.brand.artifact.entity.User;

@Repository
public interface OrderRepository extends JpaRepository<Order, String> {
    List<Order> findByUser(User user);

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByUserAndStatus(User user, OrderStatus status);

    @Query("SELECT o FROM Order o WHERE o.user = :user ORDER BY o.orderDate DESC")
    List<Order> findByUserOrderByOrderDateDesc(@Param("user") User user);

    Optional<Order> findByOrderNumber(String orderNumber);
}