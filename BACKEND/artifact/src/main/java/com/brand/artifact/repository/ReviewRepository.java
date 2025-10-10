package com.brand.artifact.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.OrderItem;
import com.brand.artifact.entity.Review;
import com.brand.artifact.entity.User;

@Repository
public interface ReviewRepository extends JpaRepository<Review, String> {
    List<Review> findByUser(User user);
    List<Review> findByOrderItem(OrderItem orderItem);
    List<Review> findByRating(Integer rating);
    List<Review> findByRatingGreaterThanEqual(Integer rating);
}