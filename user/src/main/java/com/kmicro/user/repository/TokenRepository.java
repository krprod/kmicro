package com.kmicro.user.repository;

import com.kmicro.user.entities.TokenEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenEntity, Long> {
    Optional<TokenEntity> findByToken(String hashedToken);
    Optional<TokenEntity> findByUserId(Long userId);
}
