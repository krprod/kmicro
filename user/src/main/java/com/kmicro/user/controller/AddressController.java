package com.kmicro.user.controller;

import com.kmicro.user.dtos.AddressDTO;
import com.kmicro.user.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/address")
@Tag(name = "Address Controller", description = "Handles User Addresses Lifecycle")
public class AddressController {

    @Autowired
    AddressService addressService;

    @Operation(summary = "Add-Update Addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Creation/Updation Successful"),
            @ApiResponse(responseCode = "400", description = "Failed GlobalHandler")
    })
    @PutMapping("/add-update")
    public ResponseEntity<AddressDTO> updateAddress(@RequestBody AddressDTO addressDTO){
        AddressDTO address = addressService.addUpdateAddressList(addressDTO);
        return ResponseEntity.ok(address);
    }

    @Operation(summary = "Delete Existing Addresses")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successful"),
            @ApiResponse(responseCode = "400", description = "Failed GlobalHandler")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAddress(@PathVariable(name = "id") @Min(1) Long id){
        addressService.deleteAddress(id);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Returns List Of All Addresses, Exists for User ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Fetch Successful"),
            @ApiResponse(responseCode = "400", description = "Failed GlobalHandler")
    })
    @GetMapping("/{userID}")
    public ResponseEntity<List<AddressDTO>> getAllAddress(@PathVariable(name = "userID") Long userID){
       List<AddressDTO> addressDTOList =  addressService.getAllAddressByUserID(userID);
        return ResponseEntity.status(200).body(addressDTOList);
    }

}
