package com.kmicro.user.mapper;

import com.kmicro.user.dtos.AddressDTO;
import com.kmicro.user.dtos.UserDTO;
import com.kmicro.user.entities.AddressEntity;
import com.kmicro.user.entities.UserEntity;

import java.util.List;

public class AddressMapper {

    public static AddressEntity dtoToEntity(AddressDTO addressDTO, UserEntity userEntity){
        AddressEntity addressEntity = new AddressEntity();
        if(null != addressDTO.getId()){
            addressEntity.setId(addressDTO.getId());
        }
        addressEntity.setUser(userEntity);
        addressEntity.setAddressLine1(addressDTO.getAddressLine1());
        addressEntity.setAddressLine2(addressDTO.getAddressLine2());
        addressEntity.setCity(addressDTO.getCity());
        addressEntity.setState(addressDTO.getState());
        addressEntity.setZipCode(addressDTO.getZipCode());
        addressEntity.setCountry(addressDTO.getCountry());
        return addressEntity;
    }

    public static List<AddressDTO> mapEntityListToDTOList(List<AddressEntity> addressEntityList) {
        List<AddressDTO> addressDTOList = addressEntityList.stream()
                .map(AddressMapper::EntityToDTO)
                .toList();
        return addressDTOList;
    }

    public   static  AddressDTO EntityToDTO(AddressEntity addressEntity){
        AddressDTO addressDTO = new AddressDTO();
        addressDTO.setId(addressEntity.getId());
        addressDTO.setUserId(addressEntity.getUser().getId());
        addressDTO.setAddressLine1(addressEntity.getAddressLine1());
        addressDTO.setAddressLine2(addressEntity.getAddressLine2());
        addressDTO.setCity(addressEntity.getCity());
        addressDTO.setState(addressEntity.getState());
        addressDTO.setZipCode(addressEntity.getZipCode());
        addressDTO.setCountry(addressEntity.getCountry());
        return addressDTO;
    }
}
