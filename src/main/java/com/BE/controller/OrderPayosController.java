package com.BE.controller;
        import java.util.ArrayList;
        import java.util.Date;
        import java.util.List;
        import java.util.Map;

        import com.BE.model.entity.OrderItem;
        import com.BE.model.request.CreatePaymentLinkRequestBody;
        import com.BE.model.response.OrderResponse;
        import com.BE.service.implementServices.OrderService;
        import io.swagger.v3.oas.annotations.security.SecurityRequirement;
        import org.springframework.beans.factory.annotation.Autowired;
        import org.springframework.web.bind.annotation.GetMapping;
        import org.springframework.web.bind.annotation.PathVariable;
        import org.springframework.web.bind.annotation.PostMapping;
        import org.springframework.web.bind.annotation.PutMapping;
        import org.springframework.web.bind.annotation.RequestBody;
        import org.springframework.web.bind.annotation.RequestMapping;
        import org.springframework.web.bind.annotation.RestController;

        import com.fasterxml.jackson.databind.ObjectMapper;
        import com.fasterxml.jackson.databind.node.ObjectNode;

        import vn.payos.PayOS;
        import vn.payos.type.CheckoutResponseData;
        import vn.payos.type.ItemData;
        import vn.payos.type.PaymentData;
        import vn.payos.type.PaymentLinkData;

@RestController
@RequestMapping("api/payment")
@SecurityRequirement(name = "api")

public class OrderPayosController {
    private final PayOS payOS;

    @Autowired
    OrderService orderService;

    public OrderPayosController(PayOS payOS) {
        super();
        this.payOS = payOS;
    }

    @PostMapping(path = "/create")
    public ObjectNode createPaymentLink(@RequestBody CreatePaymentLinkRequestBody RequestBody) {
        ObjectMapper objectMapper = new ObjectMapper();
        ObjectNode response = objectMapper.createObjectNode();
        try {
            OrderResponse orderResponse = new OrderResponse();
            if(RequestBody.getProductId() == null){
                orderResponse =  orderService.created(RequestBody.getCartItemIds(),RequestBody.getAddressId());
            }else{
                orderResponse =  orderService.payment(RequestBody.getProductId(),RequestBody.getAddressId());
            }
            final String description = RequestBody.getDescription();
            final String returnUrl = RequestBody.getReturnUrl() + "?orderId=" + orderResponse.getId();
            final String cancelUrl = RequestBody.getCancelUrl();
            final int price = orderResponse.getTotalPrice().intValueExact();
            // Gen order code
            String currentTimeString = String.valueOf(String.valueOf(new Date().getTime()));
            long orderCode = Long.parseLong(currentTimeString.substring(currentTimeString.length() - 6));

                    List<ItemData> itemDataList = new ArrayList<>();
                    for (OrderItem orderItem: orderResponse.getOrderItems()) {
                        ItemData item = ItemData.builder().name(orderItem.getProduct().getName()).price(orderItem.getPrice().intValueExact()).quantity(1).build();
                        itemDataList.add(item);
                    }
                    PaymentData paymentData = PaymentData.builder().orderCode(orderCode).description(description).amount(price)
                            .items(itemDataList).returnUrl(returnUrl).cancelUrl(cancelUrl).build();


            CheckoutResponseData data = payOS.createPaymentLink(paymentData);

            response.put("error", 0);
            response.put("message", "success");
            response.set("data", objectMapper.valueToTree(data));
            return response;

        } catch (Exception e) {
            e.printStackTrace();
            response.put("error", -1);
            response.put("message", "fail");
            response.set("data", null);
            return response;

        }
    }

    @GetMapping(path = "/{orderId}")
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
