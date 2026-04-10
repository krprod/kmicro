package com.kmicro.user.repository;

import com.kmicro.user.entities.AddressEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AddressRepository extends JpaRepository<AddressEntity, Long> {
    List<AddressEntity> findAllByUserId(Long userID);
    Boolean existsByUserId(Long userID);

    Optional<AddressEntity> findByIdAndUserId(Long id, Long userID);

    Long countByUserId(Long userID);
}
