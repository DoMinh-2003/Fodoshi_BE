package com.BE.model.request;


import com.BE.model.entity.Address;
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
    // Fields for both guest and authenticated users
    private String returnUrl;
    private String cancelUrl;
    private String description;
    
    // Fields for authenticated users
    @NotNull(message = "cartItemIds cannot be null")
    private List<UUID> cartItemIds;
    @NotNull(message = "productId cannot be null")
    private Long productId;
    @NotNull(message = "addressId cannot be null")
    private Long addressId;
    
    // Guest checkout fields
    private boolean isGuest;
    private String guestName;
    private String guestEmail;
    private String guestPhone;
    private Address guestAddress;
    
    // Used for direct product checkout (guest mode)
    private List<Long> productIds;
}
