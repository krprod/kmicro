package com.kmicro.user.repository;

import com.kmicro.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;

public interface UsersRepository  extends JpaRepository<UserEntity, Long> {

    public Optional<UserEntity> findByEmail(String email);

    @Modifying // 1. Marks the method as a modifying query (UPDATE, DELETE)
    @Transactional // 2. Ensures the operation runs in a transaction
    @Query("UPDATE users u SET u.isLoggedIn = :isLoggedIn, u.lastloginTime = :lastLoginTime WHERE u.email = :email")
    int updateLoginStatusByEmail(
            @Param("isLoggedIn") boolean isLoggedIn,
            @Param("lastLoginTime") LocalDateTime lastLoginTime,
            @Param("email") String email
    );

    boolean existsByLoginName(String loginName);
    boolean existsByEmail(String email);

}
