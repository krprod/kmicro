package com.kmicro.user.service;

import com.kmicro.user.constants.AppContants;
import com.kmicro.user.dtos.ResponseDTO;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.dtos.UserDetailUpdateRec;
import com.kmicro.user.dtos.UserRegistrationRecord;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.exception.AlreadyExistException;
import com.kmicro.user.exception.UserNotFoundException;
import com.kmicro.user.exception.VerificationExecption;
import com.kmicro.user.kafka.producers.ExternalEventProducers;
import com.kmicro.user.kafka.producers.InternalEventProducers;
import com.kmicro.user.mapper.UserMapper;
import com.kmicro.user.repository.UsersRepository;
import com.kmicro.user.security.RolesConstants;
import com.kmicro.user.utils.DBOps;
import com.kmicro.user.utils.RedisOps;
import com.kmicro.user.utils.UserAuthUtil;
import io.jsonwebtoken.Claims;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    private final DBOps dbOps;
    private final InternalEventProducers internalEventProducers;
    private final ExternalEventProducers externalEventProducers;
    private final RedisOps redisOps;

    public ResponseDTO createUser(UserRegistrationRecord user) {
        Optional<UserEntity> newUserRequest = usersRepository.findByEmailAndLoginName(user.email(), user.login_name());
        if(newUserRequest.isPresent()){
            log.info("Email: {} And LoginName: {} already exists", user.email(), user.login_name());
            throw  new AlreadyExistException("Email And LoginName already exists, try another email ");
        }

        UserEntity userEntity = new UserEntity();
        userEntity.setLoginName(user.login_name());
        userEntity.setPassword( passwordEncoder.encode(user.password()));
        userEntity.setEmail(user.email());
        userEntity.setRoles(Set.of(RolesConstants.USER, RolesConstants.ORDERS));
//        userEntity.setActive(true);

        UserEntity savedUser = usersRepository.save(userEntity);
        log.info("User created successfully with ID: {}", savedUser.getId());
        externalEventProducers.emailVerificationNotification(savedUser,"", "");
        return new ResponseDTO("200", "User created successfully with ID: "+savedUser.getId());
    }

    public  UserDTO getUserById(Long id, Boolean withAddress) {
/*        Optional<UserEntity> userEntityOpt = dbOps.findUserByID(id);
        log.info("HITTING DB TO GET DATA");
        if(userEntityOpt.isEmpty()){
            throw new UserNotFoundException("User not Found: "+id);
        }*/

       UserDTO userDTO = redisOps.getCachedUser(id);
        if (!withAddress) {
            userDTO.setAddresses(null);
        }
        return userDTO;
    }

    @CachePut(value = AppContants.CACHE_USER_KEY_PX, key = "#result.getId()", unless = "#result == null")
    @CacheEvict(value = AppContants.CACHE_ADDRESS_KEY_PX, key = "#result.getId()")
    @Transactional
    public UserDTO deleteUser(HttpServletRequest request) {
        Claims token = userAuthUtil.getClaimsAndInvalidate(request);

        String userEmail = token.getSubject();

        UserEntity user =usersRepository.findByEmail(userEmail)
                .orElseThrow(()->new UserNotFoundException("User Email not exists in DB: "+userEmail));

        if(user.isActive() && !user.isLocked()){
            user.deactivateAccount();
            UserEntity blockUser =usersRepository.save(user);
            log.info("User account {} has been successfully deactivated and locked.", userEmail);
            return UserMapper.EntityToDTO(blockUser);
        }
        SecurityContextHolder.clearContext();
        return null;
    }

//    @RolesAllowed(RolesConstants.ADMIN)
    public List<UserDTO> getAllUsers() {
        var usersEntity = usersRepository.findAll();
        return UserMapper.EntityListToDTOList(usersEntity);
    }


    @CachePut(value = AppContants.CACHE_USER_KEY_PX, key = "#result.getId()")
    @Transactional
    public UserDTO updateFieldsOnLogin(String email) {

//        int updatedRows = usersRepository.updateLoginStatusByEmail(true, LocalDateTime.now(), email);

        UserEntity user = usersRepository.findByEmail(email)
                .orElseThrow(()-> new UserNotFoundException("UserNotFoundException"));

        user.setLoggedIn(true);
        user.setLastloginTime(Instant.now());

        return UserMapper.EntityWithAddressToDTOWithAddress(usersRepository.save(user));
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

    @CachePut(value = AppContants.CACHE_USER_KEY_PX, key = "#userID")
    @Transactional
    public UserDTO updateExistingUser(UserDetailUpdateRec userRec, Long userID) {
        Boolean sendWelcomeMail = false;
        UserEntity user =usersRepository.findById(userID)
                .orElseThrow(()-> new UserNotFoundException("User not Found ID: "+ userID));

        if(!user.isVerified() && !user.isActive()) throw new VerificationExecption("Email Not Verified, Cannot Update User.");

        if((null == user.getFirstName() && null == user.getLastName()) && (user.isActive() && user.isVerified())){
            sendWelcomeMail = true;
        }
        user.setFirstName(userRec.firstname());
        user.setLastName(userRec.lastname());
        user.setContact(userRec.contact());
        user.setAvtar(userRec.avtar());

        UserEntity savedUser = usersRepository.save(user);

        if(sendWelcomeMail) externalEventProducers.welcomEmailNotification(savedUser, "","welcomeNewUser_");

        return UserMapper.EntityWithAddressToDTOWithAddress(savedUser);
        // --- IF UPDATING EMAIL, PASSWORD --- VERIFICATION EMAIL SENT WITH LINK
        // this.updateEmailOrPassword();
    }

    // ---- For Other Services
    public UserEntity saveActivatedVerifiedUser(UserEntity user){
        return usersRepository.save(user);
    }

    public  UserEntity getUserById(Long id) {
        Optional<UserEntity> userEntityOpt = dbOps.findUserByID(id);
        if(userEntityOpt.isEmpty()){
            throw new UserNotFoundException("User not Found: "+id);
        }
        return userEntityOpt.get();
    }

    public  UserEntity getUserByEmail(String email) {
        return dbOps.findUserByEmail(email)
                .orElseThrow(()->new UserNotFoundException("User Email not exists in DB: "+ email));
    }
}//EC
