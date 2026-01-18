package com.kmicro.user.service;

import com.kmicro.user.dtos.ResponseDTO;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.dtos.UserDetailUpdateRec;
import com.kmicro.user.dtos.UserRegistrationRecord;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.exception.AlreadyExistException;
import com.kmicro.user.exception.UserNotFoundException;
import com.kmicro.user.mapper.UserMapper;
import com.kmicro.user.repository.UsersRepository;
import com.kmicro.user.security.RolesConstants;
import com.kmicro.user.utils.UserAuthUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService{

   private final UsersRepository usersRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserAuthUtil userAuthUtil;

    @Transactional
    public ResponseDTO createUser(UserRegistrationRecord user) {
        if(this.LoginNameExists(user.login_name())){
            throw new AlreadyExistException("Login Name already exists: "+user.login_name());
        }
        if(this.emailExists(user.email())){
            throw  new AlreadyExistException("Email already exists: "+user.email());
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setLoginName(user.login_name());
        userEntity.setPassword( passwordEncoder.encode(user.password()));
        userEntity.setEmail(user.email());

        userEntity.setRoles(Set.of(RolesConstants.USER, RolesConstants.ORDERS));
        userEntity.setActive(true);

        UserEntity savedUser = usersRepository.save(userEntity);
        log.info("User created successfully with ID: {}", savedUser.getId());

        return new ResponseDTO("200", "User created successfully with ID: "+savedUser.getId());
    }

    @Transactional(readOnly = true)
    public  UserDTO getUserById(Long id, Boolean withAddress) {
        Optional<UserEntity> userEntityOpt = usersRepository.findById(id);
        if(userEntityOpt.isEmpty()){
            throw new UserNotFoundException("User not Found: "+id);
        }

        return withAddress ?
                UserMapper.EntityWithAddressToDTOWithAddress(userEntityOpt.get()):
                UserMapper.EntityToDTO(userEntityOpt.get());
    }

    @Transactional
    public void deleteUser(HttpServletRequest request) {

        Claims token = userAuthUtil.getClaimsAndInvalidate(request);

        String userEmail = token.getSubject();

        UserEntity user = usersRepository.findByEmail(userEmail)
                .orElseThrow(()->new UserNotFoundException("User Email not exists in DB: "+userEmail));

        user.deactivateAccount();

        UserEntity blockUser = usersRepository.save(user);
        SecurityContextHolder.clearContext();
        log.info("User account {} has been successfully deactivated and locked.", userEmail);
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

    public boolean LoginNameExists(String loginName) {
        return usersRepository.existsByLoginName(loginName);
    }

    public boolean emailExists(String loginName) {
        return usersRepository.existsByEmail(loginName);
    }

    @Transactional
    public UserDTO updateExistingUser(UserDetailUpdateRec userRec, Long userID) {
        UserEntity user = usersRepository.findById(userID)
                .orElseThrow(()-> new UserNotFoundException("User not Found:  ID: "+ userID));

        user.setFirstName(userRec.firstname());
        user.setLastName(userRec.lastname());
        user.setContact(userRec.contact());
        user.setAvtar(userRec.avtar());

        return UserMapper.EntityToDTO(usersRepository.save(user));
        // --- IF UPDATING EMAIL, PASSWORD --- VERIFICATION EMAIL SENT WITH LINK
        // this.updateEmailOrPassword();
    }
}//EC
