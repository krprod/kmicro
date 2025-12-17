package com.kmicro.user.controller;

import com.kmicro.user.dtos.AddressDTO;
import com.kmicro.user.service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/address")
public class AddressController {

    @Autowired
    AddressService addressService;

    @PutMapping("/update")
    public String updateAddress(@RequestBody AddressDTO addressDTO){
        addressService.updateAddress(addressDTO);
        return "Address Updated";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteAddress(@PathVariable(name = "id") Long id){
        addressService.deleteAddress(id);
        return "Address Deleted";
    }

    @GetMapping("/get/{userID}")
    public ResponseEntity<List<AddressDTO>> getAllAddress(@PathVariable(name = "userID") Long userID){
       List<AddressDTO> addressDTOList =  addressService.getAllAddressByUserID(userID);
        return ResponseEntity.status(200).body(addressDTOList);
    }

}
