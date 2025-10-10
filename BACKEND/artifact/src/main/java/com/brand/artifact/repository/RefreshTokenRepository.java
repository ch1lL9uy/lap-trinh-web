package com.brand.artifact.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.brand.artifact.entity.RefreshToken;
import com.brand.artifact.entity.User;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByToken(String token);
    List<RefreshToken> findByUser(User user);
    List<RefreshToken> findByUser_UserId(String userId);
    void deleteByUser(User user);
    long deleteByExpiresAtBefore(LocalDateTime dateTime);
}
