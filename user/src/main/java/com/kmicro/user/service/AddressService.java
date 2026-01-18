package com.kmicro.user.service;

import com.kmicro.user.dtos.AddressDTO;
import com.kmicro.user.entities.AddressEntity;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.exception.AddressException;
import com.kmicro.user.exception.UserNotFoundException;
import com.kmicro.user.mapper.AddressMapper;
import com.kmicro.user.repository.AddressRepository;
import com.kmicro.user.repository.UsersRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;

    public void deleteAddress(Long id) {
        if(!addressRepository.existsById(id)){
            throw new AddressException("Address not Exists By ID: "+id);
        }
        addressRepository.deleteById(id);
        log.info("Address Delete ID: {}",id);
    }

    @Transactional(readOnly = true)
    public List<AddressDTO> getAllAddressByUserID(Long userID) {
        if(!usersRepository.existsById(userID)){
            throw new UserNotFoundException("User not exists ID: "+userID);
        }
        List<AddressEntity> addressEntityList = addressRepository.findAllByUserId(userID);
        return AddressMapper.mapEntityListToDTOList(addressEntityList);
    }

    @Transactional
    public AddressDTO addUpdateAddressList(AddressDTO addressList) {
        AddressEntity addressEntity = null;

        if(addressRepository.existsById(addressList.getId()) && addressRepository.existsByUserId(addressList.getUserId())){
            AddressEntity addressEntityOpt = addressRepository.findById(addressList.getId()).get();
            addressEntity = this.updateAddress(addressList,addressEntityOpt);
        }else{
            if(addressRepository.count() >= 5){
                throw  new AddressException("Addresses Limit Reached, Only 5 addresses Can be Added");
            }
            UserEntity user = usersRepository.findById(addressList.getUserId())
                    .orElseThrow(()-> new UserNotFoundException("Address Not Added, User Not Found ID: "+addressList.getUserId()));

            addressEntity = AddressMapper.dtoToEntityNew(addressList,user);
            log.info("New Address Added for User ID: {}", user.getId());
        }

        AddressEntity SaveEntity = addressRepository.save(addressEntity);

        return AddressMapper.EntityToDTO(SaveEntity );
    }

    private AddressEntity updateAddress(AddressDTO addressDTO, AddressEntity oldAddress) {
        AddressEntity updateEntity = AddressMapper.dtoToEntityOld(addressDTO,oldAddress);
        log.info("Address Update for ID: {}", updateEntity.getId());
        return updateEntity;
    }

    @Transactional
    public AddressDTO addUpdateAddress(AddressDTO address) {

        AddressEntity addressEntity = addressRepository.findByIdAndUserId(address.getId(), address.getUserId())
                .orElseGet(AddressEntity::new);

        if(addressEntity.getId() != null){
            addressEntity = this.updateAddress(address,addressEntity);
            log.info("AddressUpdated for User ID: {} AddressID: {}", address.getUserId(), address.getId());
        }
        else{

            if(addressRepository.countByUserId(address.getUserId()) >= 5){
                throw  new AddressException("Addresses Limit Reached, Only 5 addresses Can be Added");
            }
            UserEntity user = usersRepository.findById(address.getUserId())
                    .orElseThrow(()-> new UserNotFoundException("Address Not Added, User Not Found ID: "+address.getUserId()));

            addressEntity = AddressMapper.dtoToEntityNew(address,user);
            log.info("New Address Added for User ID: {}", user.getId());
        }

        AddressEntity SaveEntity = addressRepository.save(addressEntity);
        return AddressMapper.EntityToDTO(SaveEntity );
    }
}
