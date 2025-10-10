package com.brand.artifact.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.User;
import com.brand.artifact.entity.UserInfo;

@Repository
public interface UserInfoRepository extends JpaRepository<UserInfo, String> {
    Optional<UserInfo> findByUser(User user);
    
    @Query("SELECT ui FROM UserInfo ui WHERE ui.user.userId = :userId")
    Optional<UserInfo> findByUserId(@Param("userId") String userId);

    void deleteByUser(User user);
}