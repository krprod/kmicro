package com.kmicro.notification.repository;

import com.kmicro.notification.entities.UserAddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAddressRepository extends JpaRepository<UserAddressEntity, Long> {

    Optional<UserAddressEntity> findByAddressIdAndUserId(Integer addressId, Integer userId);

}
