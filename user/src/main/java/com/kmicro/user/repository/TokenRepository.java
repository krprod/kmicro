package com.kmicro.user.repository;

import com.kmicro.user.entities.TokenVerification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TokenRepository extends JpaRepository<TokenVerification, Long> {
    Optional<TokenVerification> findByToken(String hashedToken);
    Optional<TokenVerification> findByUserId(Long userId);
}
