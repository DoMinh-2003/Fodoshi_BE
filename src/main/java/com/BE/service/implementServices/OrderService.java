package com.BE.service.implementServices;



import com.BE.enums.CartItemStatus;
import com.BE.enums.OrderStatus;
import com.BE.enums.ShippingType;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.OrderMapper;
import com.BE.model.entity.*;
import com.BE.model.request.OrderRequest;
import com.BE.model.request.OrderStatusRequest;
import com.BE.model.response.OrderResponse;
import com.BE.repository.*;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    ProductRepository productRepository;

    @Autowired
    ProductService productService;

    @Autowired
    AddressService addressService;

    @Autowired
    OrderHistoryRepository orderHistoryRepository;

    @Autowired
    ProductHistoryRepository productHistoryRepository;

    public OrderResponse created(List<UUID> cartItemIds, Long addressId , BigDecimal totalPrice , ShippingType shippingType) {

        User account = accountUtils.getCurrentUser();
        Order order = new Order();

        order.setCreatedAt(dateNowUtils.dateNow());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setUser(account);
        Address address = addressService.getAddressById(addressId);
        order.setAddress(address);

        cartItemIds.stream().forEach((cartItemId) -> {
            CartItem cartItem = cartItemRepository.findByIdAndStatus(cartItemId, CartItemStatus.ADDED).orElseThrow(() -> new NotFoundException("CartItem not found"));
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setPrice(cartItem.getProduct().getSellingPrice());
            order.getOrderItems().add(orderItem);

        });
        order.setShippingType(shippingType);
        order.setTotalPrice(totalPrice);

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setCreatedAt(dateNowUtils.dateNow());
        orderHistory.setOrder(order);
        orderHistory.setStatus(OrderStatus.PENDING_PAYMENT);

        order.getOrderHistories().add(orderHistory);

        orderHistoryRepository.save(orderHistory);

        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    public OrderResponse payment(Long id,Long addressId, BigDecimal totalPrice,  ShippingType shippingType){
        Product product = productService.getProductById(id);

        User account = accountUtils.getCurrentUser();
        Order order = new Order();
        OrderItem orderItem = new OrderItem();
        Address address = addressService.getAddressById(addressId);
        order.setAddress(address);

        order.setCreatedAt(dateNowUtils.dateNow());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setUser(account);
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setPrice(product.getSellingPrice());
        order.setShippingType(shippingType);
        order.setTotalPrice(totalPrice);
        order.getOrderItems().add(orderItem);

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setCreatedAt(dateNowUtils.dateNow());
        orderHistory.setOrder(order);
        orderHistory.setStatus(OrderStatus.PENDING_PAYMENT);

        order.getOrderHistories().add(orderHistory);

        orderHistoryRepository.save(orderHistory);

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
        User account = accountUtils.getCurrentUser();


        if(statusRequest.getStatus().equals(OrderStatus.PAID)){

            account.getCart().getCartItems().stream().forEach((cartItem) -> {
                order.getOrderItems().stream().forEach((orderItem) -> {
                    if(cartItem.getProduct().getId().equals(orderItem.getProduct().getId())){
                        Product product = orderItem.getProduct();
                        product.setDeleted(true);
                        cartItem.setStatus(CartItemStatus.PURCHASED);
                        productRepository.save(product);
                        cartItemRepository.save(cartItem);
                    }
                });
            });
        }

        if(statusRequest.getStatus().equals(OrderStatus.PAID) || statusRequest.getStatus().equals(OrderStatus.AWAITING_DELIVERY) || statusRequest.getStatus().equals(OrderStatus.AWAITING_PICKUP) || statusRequest.getStatus().equals(OrderStatus.COMPLETED)){
            order.getOrderItems().stream().forEach((orderItem) -> {
                Product product = orderItem.getProduct();
                ProductHistory productHistory = new ProductHistory();
                productHistory.setCreatedAt(dateNowUtils.dateNow());
                productHistory.setProduct(product);
                productHistory.setStatus(statusRequest.getStatus().name());

                product.getProductHistories().add(productHistory);
                productHistoryRepository.save(productHistory);
                productRepository.save(product);

            });

        }
        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setCreatedAt(dateNowUtils.dateNow());
        orderHistory.setOrder(order);
        orderHistory.setStatus(statusRequest.getStatus());

        if(statusRequest.getStatus().equals(OrderStatus.COMPLETED)){
            orderHistory.setNote(statusRequest.getNoteCompleted());
            orderHistory.setImage(statusRequest.getImageCompleted());
        }

        order.getOrderHistories().add(orderHistory);

        orderHistoryRepository.save(orderHistory);

        order.setStatus(statusRequest.getStatus());
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    public List<OrderResponse> getAll() {
        return orderMapper.toOrderResponses(orderRepository.findAll());
    }

    public OrderResponse createGuestOrder(List<Long> productIds, Address guestAddress, BigDecimal totalPrice,  ShippingType shippingType) {
        Order order = new Order();
        
        // Create and save guest address
        guestAddress.setUser(null); // No user for guest address
        Address savedAddress = addressService.saveAddress(guestAddress);
        order.setAddress(savedAddress);
        
        // Set order metadata
        order.setCreatedAt(dateNowUtils.dateNow());
        order.setStatus(OrderStatus.PENDING_PAYMENT);
        order.setUser(null); // No user for guest order
        order.setTotalPrice(BigDecimal.ZERO); // Initialize total price
        
        // Add products to order
        if (productIds != null && !productIds.isEmpty()) {
            productIds.forEach(productId -> {
                Product product = productService.getProductById(productId);
                OrderItem orderItem = new OrderItem();
                orderItem.setOrder(order);
                orderItem.setProduct(product);
                orderItem.setPrice(product.getSellingPrice());
//                order.setTotalPrice(order.getTotalPrice().add(product.getSellingPrice()));
                order.getOrderItems().add(orderItem);
            });
        }
        order.setShippingType(shippingType);
        order.setTotalPrice(totalPrice);

        OrderHistory orderHistory = new OrderHistory();
        orderHistory.setCreatedAt(dateNowUtils.dateNow());
        orderHistory.setOrder(order);
        orderHistory.setStatus(OrderStatus.PENDING_PAYMENT);

        order.getOrderHistories().add(orderHistory);

        orderHistoryRepository.save(orderHistory);
        
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }

    public OrderResponse changeGuestOrderStatus(UUID id, OrderStatusRequest statusRequest, String guestEmail) {
        Order order = orderRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Order not found"));
        
        // Verify this is a guest order
        if (order.getUser() != null) {
            throw new IllegalArgumentException("This is not a guest order");
        }
        

        
        // Update the order status
        order.setStatus(statusRequest.getStatus());
        return orderMapper.toOrderResponse(orderRepository.save(order));
    }
}
