package com.kmicro.user.mapper;

import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.entities.UserEntity;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class UserMapper {

    public static UserEntity dtoToEntity(UserDTO userDTO){
        UserEntity userEntity = new UserEntity();
        if(null != userDTO.getId()){
            userEntity.setId(userDTO.getId());
        }
//        userEntity.setUsername(userDTO.getUsername());
//        userEntity.setLastName(userDTO.getLastName());
        userEntity.setEmail(userDTO.getEmail());
        userEntity.setPassword(userDTO.getPassword());
        userEntity.setAvtar(userDTO.getAvtar());
        userEntity.setRoles(userDTO.getRoles());
        return userEntity;
    }

    public static UserEntity dtoToEntityNew(UserDTO user){
        UserEntity userEntity = new UserEntity();
        userEntity.setEmail(user.getEmail());
        userEntity.setPassword(user.getPassword());
        userEntity.setAvtar(user.getAvtar());
        userEntity.setRoles(user.getRoles());
        return userEntity;
    }

    public   static  UserDTO EntityToDTO(UserEntity userEntity){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setLogin_name(userEntity.getLoginName());
        userDTO.setLoggedIn(userEntity.isLoggedIn());
        userDTO.setAvtar(userEntity.getAvtar());
        userDTO.setRoles(userEntity.getRoles());
        userDTO.setLatitude(userEntity.getLatitude());
        userDTO.setLongitude(userEntity.getLongitude());
        // ------ new Updated Fields
        userDTO.setFirstName(userEntity.getFirstName());
        userDTO.setLastName(userEntity.getLastName());
        userDTO.setContactNumber(userEntity.getContact());
        return userDTO;
    }

    public   static  UserDTO EntityWithAddressToDTOWithAddress(UserEntity userEntity){
        UserDTO userDTO = UserMapper.EntityToDTO(userEntity);
        userDTO.setAddresses(AddressMapper.mapEntityListToDTOList(userEntity.getAddresses()));
        return userDTO;
    }

    public static List<UserDTO> EntityListToDTOList(List<UserEntity> usersList){
        return usersList.stream().map(UserMapper::EntityToDTO).toList();
    }


}//EC
