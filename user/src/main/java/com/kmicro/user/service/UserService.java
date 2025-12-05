package com.kmicro.user.service;

import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.mapper.UserMapper;
import com.kmicro.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

   private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public void createUser(UserDTO user) {
        String hashPwd = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashPwd);
       UserEntity userEntity = usersRepository.save(UserMapper.dtoToEntity(user));
       log.info("User created with ID: {}", userEntity.toString());
    }


    public UserDTO getUserByIdWithoutAddress(Long id) {
        UserEntity userEntity = usersRepository.findById(id).get();
        return UserMapper.EntityToDTO(userEntity);
    }

    public  UserDTO getUserByIdWithAddress(Long id) {
        UserEntity userEntity = usersRepository.findById(id).get();
        log.info("User aa chuka hai");
        return UserMapper.EntityWithAddressToDTOWithAddress(userEntity);
    }


    public void deleteUser(Long id) {
        usersRepository.deleteById(id);
    }

    public List<UserDTO> getAllUsers() {
        var usersEntity = usersRepository.findAll();
        return UserMapper.EntityListToDTOList(usersEntity);
    }
}
