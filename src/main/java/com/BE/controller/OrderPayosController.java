package com.BE.controller;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.BE.model.entity.Address;
import com.BE.model.entity.OrderItem;
import com.BE.model.request.CreatePaymentLinkRequestBody;
import com.BE.model.response.OrderResponse;
import com.BE.service.implementServices.OrderService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import vn.payos.PayOS;
import vn.payos.type.CheckoutResponseData;
import vn.payos.type.ItemData;
import vn.payos.type.PaymentData;
import vn.payos.type.PaymentLinkData;

@RestController
@RequestMapping("api/payment")
public class OrderPayosController {
    private final PayOS payOS;

    @Autowired
    OrderService orderService;

    public OrderPayosController(PayOS payOS) {
        super();
        this.payOS = payOS;
    }

    // Guest checkout endpoint - no security requirement
    @PostMapping(path = "/create/guest")
    public ObjectNode createGuestPaymentLink(@RequestBody CreatePaymentLinkRequestBody requestBody) throws Exception {
        // Guest checkout flow
        List<Long> productIds = new ArrayList<>();
        if (requestBody.getProductId() != null) {
            // Single product checkout
            productIds.add(requestBody.getProductId());
        } else if (requestBody.getProductIds() != null && !requestBody.getProductIds().isEmpty()) {
            // Multiple products checkout
            productIds.addAll(requestBody.getProductIds());
        }
        
        // Create guest address with contact information
        Address guestAddress = requestBody.getGuestAddress();
        guestAddress.setGuestName(requestBody.getGuestName());
        guestAddress.setGuestEmail(requestBody.getGuestEmail());
        guestAddress.setGuestPhone(requestBody.getGuestPhone());
        
        // Create guest order
        OrderResponse orderResponse = orderService.createGuestOrder(productIds, guestAddress,  requestBody.getTotalPrice(), requestBody.getShippingType());
        
        // Process payment using common method
        return processPayment(orderResponse, requestBody);
    }

    // Authenticated user checkout - requires security
    @PostMapping(path = "/create")
    @SecurityRequirement(name = "api")
    public ObjectNode createAuthenticatedPaymentLink(@RequestBody CreatePaymentLinkRequestBody requestBody) throws Exception {
        OrderResponse orderResponse;
        
        // Authenticated user flow
        if (requestBody.getProductId() == null) {
            orderResponse = orderService.created(requestBody.getCartItemIds(), requestBody.getAddressId(), requestBody.getTotalPrice(), requestBody.getShippingType());
        } else {
            orderResponse = orderService.payment(requestBody.getProductId(), requestBody.getAddressId(), requestBody.getTotalPrice(), requestBody.getShippingType());
        }
        
        // Process payment using common method
        return processPayment(orderResponse, requestBody);
    }
    
    // Common payment processing method to avoid code duplication
    private ObjectNode processPayment(OrderResponse orderResponse, CreatePaymentLinkRequestBody requestBody) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        
        // Payment processing
        final String description = requestBody.getDescription();
        final String returnUrl = requestBody.getReturnUrl() + "?orderId=" + orderResponse.getId();
        final String cancelUrl = requestBody.getCancelUrl();
        final int price = orderResponse.getTotalPrice().intValueExact();
        
        // Generate order code
        String currentTimeString = String.valueOf(new Date().getTime());
        long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));
        
        List<ItemData> itemDataList = new ArrayList<>();
        for (OrderItem orderItem: orderResponse.getOrderItems()) {
            ItemData item = ItemData.builder()
                .name(orderItem.getProduct().getName())
                .price(orderItem.getPrice().intValueExact())
                .quantity(1)
                .build();
            itemDataList.add(item);
        }
        
        PaymentData paymentData = PaymentData.builder()
            .orderCode(orderCode)
            .description(description)
            .amount(price)
            .items(itemDataList)
            .returnUrl(returnUrl)
            .cancelUrl(cancelUrl)
            .build();

        CheckoutResponseData data = payOS.createPaymentLink(paymentData);

        response.put("error", 0);
        response.put("message", "success");
        response.set("data", objectMapper.valueToTree(data));
        return response;
    }

    // Secure these existing endpoints with the security requirement
    @GetMapping(path = "/{orderId}")
    @SecurityRequirement(name = "api")
    public ObjectNode getOrderById(@PathVariable("orderId") long orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();

        try {
            PaymentLinkData order = payOS.getPaymentLinkInformation(orderId);

            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PutMapping(path = "/{orderId}")
    @SecurityRequirement(name = "api")
    public ObjectNode cancelOrder(@PathVariable("orderId") int orderId) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            PaymentLinkData order = payOS.cancelPaymentLink(orderId, null);
            response.set("data", objectMapper.valueToTree(order));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }

    @PostMapping(path = "/confirm-webhook")
    @SecurityRequirement(name = "api")
    public ObjectNode confirmWebhook(@RequestBody Map<String, String> requestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            String str = payOS.confirmWebhook(requestBody.get("webhookUrl"));
            response.set("data", objectMapper.valueToTree(str));
            response.put("error", 0);
            response.put("message", "ok");
            return response;
        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", e.getMessage());
            response.set("data", null);
            return response;
        }
    }
}
