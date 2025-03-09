package com.BE.service.implementServices;



import com.BE.enums.CartItemStatus;
import com.BE.enums.OrderStatus;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.OrderMapper;
import com.BE.model.entity.*;
import com.BE.model.request.OrderRequest;
import com.BE.model.request.OrderStatusRequest;
import com.BE.model.response.OrderResponse;
import com.BE.repository.CartItemRepository;
import com.BE.repository.OrderRepository;
import com.BE.repository.ProductRepository;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    CartItemRepository cartItemRepository;

    @Autowired
    ProductService productService ;

    public OrderResponse created(List<UUID> cartItemIds) {

        User account = accountUtils.getCurrentUser();
        Order order = new Order();

        order.setCreatedAt(dateNowUtils.dateNow());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setUser(account);

        cartItemIds.stream().forEach((cartItemId) -> {
            CartItem cartItem = cartItemRepository.findByIdAndStatus(cartItemId, CartItemStatus.ADDED).orElseThrow(() -> new NotFoundException("CartItem not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setPrice(cartItem.getProduct().getSellingPrice());
            order.setTotalPrice(order.getTotalPrice().add(cartItem.getProduct().getSellingPrice()));
            order.getOrderItems().add(orderItem);
            cartItem.setStatus(CartItemStatus.PURCHASED);
            cartItemRepository.save(cartItem);
        });

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    public OrderResponse payment(Long id){
        Product product = productService.getProductById(id);

        User account = accountUtils.getCurrentUser();
        Order order = new Order();
        OrderItem orderItem = new OrderItem();

        order.setCreatedAt(dateNowUtils.dateNow());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setUser(account);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setPrice(product.getSellingPrice());
        order.setTotalPrice(order.getTotalPrice().add(product.getSellingPrice()));
        order.getOrderItems().add(orderItem);
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }


    public List<OrderResponse> getByAccount() {
        User account = accountUtils.getCurrentUser();
        return orderMapper.toOrderResponses(orderRepository.findByUserId(account.getId()));
    }

    public OrderResponse getdetail(UUID id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new NotFoundException("Order Not found"));
        return orderMapper.toOrderResponse(order);
    }

    public OrderResponse changeStatus(UUID id, OrderStatusRequest statusRequest) {
        Order order = orderRepository.findByIdAndStatus(id,OrderStatus.PENDING_PAYMENT).orElseThrow(() -> new NotFoundException("Order Not found"));
        order.setStatus(statusRequest.getStatus());
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }
}
