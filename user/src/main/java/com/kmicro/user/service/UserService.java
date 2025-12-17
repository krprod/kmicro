package com.kmicro.user.service;

import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.mapper.UserMapper;
import com.kmicro.user.repository.UsersRepository;
import com.kmicro.user.security.RolesConstants;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

   private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;

    public String createUser(UserDTO user) {
      try{
          String hashPwd = passwordEncoder.encode(user.getPassword());
          user.setPassword(hashPwd);
          var roles  = Set.of(RolesConstants.USER, RolesConstants.ORDERS);
          user.setRoles(roles);
          UserEntity userEntity = usersRepository.save(UserMapper.dtoToEntity(user));
          if(null == userEntity){
              return "Something Cause Failure";
          }
          log.info("User created with ID: {}", userEntity.toString());
          return "success";
      }catch (Exception ex){
          log.info("Exception while user creation: {}",ex);
          throw ex;
      }

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

//    @RolesAllowed(RolesConstants.ADMIN)
    public List<UserDTO> getAllUsers() {
        var usersEntity = usersRepository.findAll();
        return UserMapper.EntityListToDTOList(usersEntity);
    }

    @Transactional
    public void updateFieldsOnLogin(String email) {

        int updatedRows = usersRepository.updateLoginStatusByEmail(true, LocalDateTime.now(), email);

        // 2. Clear the cache to ensure future SELECTs get fresh data from DB
//        entityManager.clear();

        // 3. Now, if you fetch the user, it will hit the database and get the updated data
//        UserEntity user = userRepository.findByEmail(email).orElse(null);
    }
}
