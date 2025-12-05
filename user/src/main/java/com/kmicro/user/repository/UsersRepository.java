package com.kmicro.user.repository;

import com.kmicro.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UsersRepository  extends JpaRepository<UserEntity, Long> {

    public Optional<UserEntity> findByEmail(String email);
}
