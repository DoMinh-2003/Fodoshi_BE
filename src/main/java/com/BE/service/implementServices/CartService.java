package com.BE.service.implementServices;


import com.BE.enums.CartItemStatus;
import com.BE.exception.exceptions.DuplicateException;
import com.BE.exception.exceptions.NotFoundException;
import com.BE.mapper.CartMapper;
import com.BE.model.entity.Cart;
import com.BE.model.entity.CartItem;
import com.BE.model.entity.Product;
import com.BE.model.entity.User;
import com.BE.model.request.AddToCartRequest;
import com.BE.model.response.CartResponse;
import com.BE.repository.CartItemRepository;
import com.BE.repository.CartRepository;
import com.BE.repository.ProductRepository;
import com.BE.utils.AccountUtils;
import com.BE.utils.DateNowUtils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class CartService {

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    DateNowUtils dateNowUtils;

    @Autowired
    CartRepository cartRepository;

    @Autowired
    CartMapper cartMapper;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    CartItemRepository cartItemRepository;


    public CartResponse created(AddToCartRequest addToCartRequest) {
        Product product = productRepository.findById(addToCartRequest.getProductId()).orElseThrow(() -> new NotFoundException("Product not found"));
        User account = accountUtils.getCurrentUser();
        Cart cart = account.getCart();
        if(!cart.getCartItems().isEmpty()){
            cart.getCartItems().stream().filter(cartItem -> cartItem.getStatus().equals(CartItemStatus.ADDED)).forEach((cartItem) -> {
                if(cartItem.getProduct().getId().equals(product.getId())){
                  throw new DuplicateException("Product already exists in Cart");
                }
            });
        }
        CartItem cartItem = new CartItem();
        cartItem.setCart(cart);
        cartItem.setCreatedAt(dateNowUtils.dateNow());
        cartItem.setProduct(product);
        cartItem.setStatus(CartItemStatus.ADDED);
        cart.getCartItems().add(cartItem);
        return cartMapper.toCartResponse(cartRepository.save(cart));
    }


    public CartResponse delete(UUID id) {
        CartItem cartItem = cartItemRepository.findByIdAndStatus(id, CartItemStatus.ADDED).orElseThrow(() -> new NotFoundException("CartItem not found"));
        cartItem.setStatus(CartItemStatus.REMOVED);
        cartItem = cartItemRepository.save(cartItem);
        return cartMapper.toCartResponse(cartRepository.save(cartItem.getCart()));
    }

    public CartResponse getDetail() {
        User account = accountUtils.getCurrentUser();
        Cart cart = account.getCart();
        return cartMapper.toCartResponse(cart);
    }

}
