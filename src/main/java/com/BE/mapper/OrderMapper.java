package com.BE.mapper;

import com.BE.model.entity.Order;
import com.BE.model.response.OrderResponse;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "spring")
public interface OrderMapper {
    OrderResponse toOrderResponse(Order order);
    List<OrderResponse> toOrderResponses(List<Order> orders);

}
