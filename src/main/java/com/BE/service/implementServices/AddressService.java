package com.BE.service.implementServices;

import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.AddressMapper;
import com.BE.model.entity.Address;
import com.BE.model.entity.User;
import com.BE.model.request.AddressRequest;
import com.BE.repository.AddressRepository;
import com.BE.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AddressService {

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    AddressMapper addressMapper;

    public List<Address> getAddressByUser() {
        User user = accountUtils.getCurrentUser();
        return addressRepository.findAllByUserId(user.getId());
    }

    public Address createAddressByUser(AddressRequest addressRequest) {
        Address address = addressMapper.toAddress(addressRequest);
        User user = accountUtils.getCurrentUser();
        user.getAddresses().add(address);
        address.setUser(user);
        return addressRepository.save(address);
    }

    public Address getAddressById(Long id) {
        return addressRepository.findById(id).orElseThrow(() -> new NotFoundException("Address not found"));
    }

    public Address deleteAddressByUser(Long id) {
        Address address = getAddressById(id);
        address.setIsDeleted(true);
        return addressRepository.save(address);
    }

    public Address updateAddressByUser(Long id, AddressRequest addressRequest) {
        Address address = getAddressById(id);
        addressMapper.updateAddress(address, addressRequest);
        return addressRepository.save(address);

    }
}
