package com.brand.artifact.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.OrderItem;
import com.brand.artifact.entity.Review;
import com.brand.artifact.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    
    // Tìm reviews của user
    List<Review> findByUser(User user);
    
    // Tìm reviews của một OrderItem
    List<Review> findByOrderItem(OrderItem orderItem);
    
    // Tìm reviews theo rating
    List<Review> findByRating(Integer rating);
    
    // Tìm reviews với rating >= threshold
    List<Review> findByRatingGreaterThanEqual(Integer rating);
}