package com.BE.mapper;


import com.BE.model.entity.Address;
import com.BE.model.request.AddressRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toAddress(AddressRequest address);

    void updateAddress(@MappingTarget Address address,AddressRequest addressRequest);

}
