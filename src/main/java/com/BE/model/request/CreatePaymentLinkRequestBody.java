package com.BE.model.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Getter
@Setter
public class CreatePaymentLinkRequestBody {
    private String description;
    @NotNull(message = "productId cannot be null")
    private Long productId;
    @NotNull(message = "addressId cannot be null")
    private Long addressId;
    @NotNull(message = "cartItemIds cannot be null")
    List<UUID> cartItemIds;
    private String returnUrl;
    private String cancelUrl;

}
