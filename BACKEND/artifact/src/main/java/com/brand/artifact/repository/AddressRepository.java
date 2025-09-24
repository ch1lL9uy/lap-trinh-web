package com.brand.artifact.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.Address;
import com.brand.artifact.entity.User;

@Repository
public interface AddressRepository extends JpaRepository<Address, String> {
    // Tìm addresses theo userId
    List<Address> findByUserUserId(String userId);
    
    // Tìm default address của user
    Optional<Address> findByUserAndIsDefaultTrue(User user);
    
    // Đếm số addresses của user
    long countByUser(User user);
}