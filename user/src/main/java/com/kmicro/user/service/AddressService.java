package com.kmicro.user.service;

import com.kmicro.user.dtos.AddressDTO;
import com.kmicro.user.entities.AddressEntity;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.mapper.AddressMapper;
import com.kmicro.user.repository.AddressRepository;
import com.kmicro.user.repository.UsersRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class AddressService {
    @Autowired
    AddressRepository addressRepository;

    @Autowired
    UsersRepository usersRepository;

    public void updateAddress(AddressDTO addressDTO) {
        Optional<UserEntity> currentUser = usersRepository.findById(addressDTO.getUserId());
       AddressEntity addressEntity = addressRepository.save(AddressMapper.dtoToEntity(addressDTO,currentUser.get()));
        log.info("User created with ID: {}", addressEntity.toString());
    }

    public void deleteAddress(Long id) {
        addressRepository.deleteById(id);
    }

    public List<AddressDTO> getAllAddressByUserID(Long userID) {
        List<AddressEntity> addressEntityList = addressRepository.findAllByUserId(userID);
        return AddressMapper.mapEntityListToDTOList(addressEntityList);
    }
}
