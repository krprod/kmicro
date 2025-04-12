package com.kmicro.user.repository;

import com.kmicro.user.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsersRepository  extends JpaRepository<UserEntity, Long> {
}
