package com.kmicro.notification.repository;

import com.kmicro.notification.entities.UserDataEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserDataRepository extends JpaRepository<UserDataEntity, UUID> {
    Optional<UserDataEntity> findByUserId(Integer userId);
}
