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

    public   static  UserDTO EntityToDTO(UserEntity userEntity){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setPassword(userEntity.getPassword());
//        userDTO.setFirstName(userEntity.getFirstName());
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setAvtar(userEntity.getAvtar());
        return userDTO;
    }

    public   static  UserDTO EntityWithAddressToDTOWithAddress(UserEntity userEntity){
        UserDTO userDTO = new UserDTO();
        userDTO.setId(userEntity.getId());
        userDTO.setEmail(userEntity.getEmail());
        userDTO.setPassword(userEntity.getPassword());
//        userDTO.setFirstName(userEntity.getFirstName());
        userDTO.setUsername(userEntity.getUsername());
        userDTO.setAvtar(userEntity.getAvtar());
        log.info("Ab Address call krenge");
        userDTO.setAddresses(AddressMapper.mapEntityListToDTOList(userEntity.getAddresses()));
       log.info("Address aa chuke guys");
        return userDTO;
    }

    public static List<UserDTO> EntityListToDTOList(List<UserEntity> usersList){
        return usersList.stream().map(UserMapper::EntityToDTO).toList();
    }


}//EC
