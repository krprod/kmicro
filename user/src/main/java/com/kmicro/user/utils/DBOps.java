package com.kmicro.user.utils;

import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DBOps {

    private final UsersRepository usersRepository;

    @Transactional
    public UserEntity saveUser(UserEntity user){
       return usersRepository.save(user);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findUserByID(Long id){
        return usersRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<UserEntity> findUserByEmail(String email){
        return  usersRepository.findByEmail(email);
    }
}
