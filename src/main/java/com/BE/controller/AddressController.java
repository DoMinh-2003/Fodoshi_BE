package com.BE.controller;


import com.BE.model.request.AddressRequest;
import com.BE.service.implementServices.AddressService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/address")
@SecurityRequirement(name ="api")
public class AddressController {

    @Autowired
    AddressService addressService;

    @GetMapping("/user")
    public ResponseEntity getAddressByUser() {
        return ResponseEntity.ok(addressService.getAddressByUser());
    }

    @PostMapping("/user")
    public ResponseEntity createAddressByUser(@RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.createAddressByUser(addressRequest));
    }


    @GetMapping("/user/{id}")
    public ResponseEntity getAddressById(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.getAddressById(id));
    }
    @DeleteMapping("/user/{id}")
    public ResponseEntity deleteAddressByUser(@PathVariable Long id) {
        return ResponseEntity.ok(addressService.deleteAddressByUser(id));
    }

    @PutMapping("/user/{id}")
    public ResponseEntity updateAddressByUser(@PathVariable Long id,@RequestBody AddressRequest addressRequest) {
        return ResponseEntity.ok(addressService.updateAddressByUser(id,addressRequest));
    }



}
