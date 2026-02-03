package com.kmicro.user.service;

import com.kmicro.user.constants.AppContants;
import com.kmicro.user.dtos.AddressDTO;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.entities.AddressEntity;
import com.kmicro.user.entities.UserEntity;
import com.kmicro.user.exception.AddressException;
import com.kmicro.user.exception.NotExistException;
import com.kmicro.user.exception.UserNotFoundException;
import com.kmicro.user.mapper.AddressMapper;
import com.kmicro.user.repository.AddressRepository;
import com.kmicro.user.repository.UsersRepository;
import com.kmicro.user.utils.RedisOps;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class AddressService {

    private final AddressRepository addressRepository;
    private final UsersRepository usersRepository;
    private final RedisOps redisOps;

    @Caching(
            evict = {
                    @CacheEvict(value = AppContants.CACHE_ADDRESS_KEY_PX, key = "#addressDTO.userId"),
                    @CacheEvict(value = AppContants.CACHE_USER_KEY_PX, key = "#addressDTO.userId")
            }
    )
    public void deleteAddress(Long id, AddressDTO addressDTO) {
        if(!addressRepository.existsById(id)){
            throw new AddressException("Address not Exists By ID: "+id);
        }
        addressRepository.deleteById(id);
        log.info("Address Delete ID: {}",id);
    }

    @Cacheable(value = AppContants.CACHE_ADDRESS_KEY_PX, key = "#userID", unless = "#result.size() == 0")
    @Transactional(readOnly = true)
    public List<AddressDTO> getAllAddressByUserID(Long userID) {
//        if(!usersRepository.existsById(userID)){
//            throw new UserNotFoundException("User not exists ID: "+userID);
//        }
        UserDTO userDTO = redisOps.getCachedUser(userID);
        if(null != userDTO && !userDTO.isActive()){
            throw new NotExistException("This account is deactivated or not verified yet.");
        }
        List<AddressEntity> addressEntityList = addressRepository.findAllByUserId(userID);
        List<AddressDTO> dtos = AddressMapper.mapEntityListToDTOList(addressEntityList);
        return new ArrayList<>(dtos);
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

    @Caching(
            evict = {
                    @CacheEvict(value = AppContants.CACHE_ADDRESS_KEY_PX, key = "#address.userId"),
                    @CacheEvict(value = AppContants.CACHE_USER_KEY_PX, key = "#address.userId")
            }
    )
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
            if (!user.isActive()) throw new NotExistException("This account is deactivated or not verified yet.");
            addressEntity = AddressMapper.dtoToEntityNew(address,user);
            log.info("New Address Added for User ID: {}", user.getId());
        }

        AddressEntity SaveEntity = addressRepository.save(addressEntity);
        return AddressMapper.EntityToDTO(SaveEntity );
    }
}
