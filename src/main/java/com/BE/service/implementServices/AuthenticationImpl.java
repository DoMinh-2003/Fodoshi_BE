package com.BE.service.implementServices;


import com.BE.enums.UserRole;
import com.BE.exception.exceptions.BadRequestException;
import com.BE.exception.exceptions.InvalidRefreshTokenException;
import com.BE.mapper.UserMapper;
import com.BE.model.EmailDetail;
import com.BE.model.entity.Cart;
import com.BE.model.request.*;
import com.BE.model.response.AuthenResponse;
import com.BE.model.response.AuthenticationResponse;
import com.BE.model.entity.User;
import com.BE.repository.UserRepository;
import com.BE.service.EmailService;
import com.BE.service.JWTService;
import com.BE.service.RefreshTokenService;
import com.BE.service.interfaceServices.IAuthenticationService;
import com.BE.utils.AccountUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthenticationImpl implements IAuthenticationService {


    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    JWTService jwtService;

    @Autowired
    UserMapper userMapper;

    @Autowired
    AccountUtils accountUtils;

    @Autowired
    EmailService emailService;

    @Autowired
    RefreshTokenService refreshTokenService;

    public User register(AuthenticationRequest request) {
        User user = userMapper.toUser(request);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(UserRole.CONSIGNOR);

        Cart cart = new Cart();
        cart.setUser(user);
        user.setCart(cart);

       try {
           return userRepository.save(user);
       }catch (DataIntegrityViolationException e){
           System.out.println(e.getMessage());
           throw new DataIntegrityViolationException("Duplicate");
       }
    }
//    @Cacheable()
    public AuthenticationResponse authenticate(LoginRequestDTO request){
        Authentication authentication = null;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getPhoneNumber().trim(),
                            request.getPassword().trim()
                    )
            );
        } catch (Exception e) {
            throw new NullPointerException("Wrong Id Or Password") ;
        }

        User user = (User) authentication.getPrincipal();
        AuthenticationResponse authenticationResponse = userMapper.toAuthenticationResponse(user);
        String refresh = UUID.randomUUID().toString();
        authenticationResponse.setToken(jwtService.generateToken(user,refresh ,false));
        authenticationResponse.setRefreshToken(refresh);
        return authenticationResponse;
    }


    public AuthenticationResponse loginGoogle(LoginGoogleRequest loginGoogleRequest) {
        try {
            FirebaseToken decodeToken = FirebaseAuth.getInstance().verifyIdToken(loginGoogleRequest.getToken());
            String email = decodeToken.getEmail();
            // Check if user exists first
            User user = userRepository.findByEmail(email).orElse(null);
            
            // Create new user if not found
            if (user == null) {
                user = new User();
                user.setName(decodeToken.getName());
                user.setEmail(email);
                user.setRole(UserRole.CONSIGNOR);
                user.setPhoneNumber(email);
                // Create csetsetart for new user if needed
                Cart cart = new Cart();
                cart.setUser(user);
                user.setCart(cart);
                
                user = userRepository.save(user);
            }



            AuthenticationResponse authenticationResponse = userMapper.toAuthenticationResponse(user);
            String refresh = UUID.randomUUID().toString();
            authenticationResponse.setToken(jwtService.generateToken(user, refresh, false));
            authenticationResponse.setRefreshToken(refresh);
            return authenticationResponse;
        } catch (FirebaseAuthException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void forgotPasswordRequest(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new BadRequestException("Email Not Found"));

        EmailDetail emailDetail = new EmailDetail();
        emailDetail.setRecipient(user.getEmail());
        emailDetail.setSubject("Reset password for account " + user.getEmail() + "!");
        emailDetail.setMsgBody("aaa");
        emailDetail.setButtonValue("Reset Password");
        emailDetail.setFullName(user.getName());
        emailDetail.setLink("https://fodoshi.shop?token=" + jwtService.generateToken(user));

        Runnable r = new Runnable() {
            @Override
            public void run() {
                emailService.sendMailTemplate(emailDetail);
            }

        };
        new Thread(r).start();

    }

    public User resetPassword(ResetPasswordRequest resetPasswordRequest) {
        User user = accountUtils.getCurrentUser();
        user.setPassword(passwordEncoder.encode(resetPasswordRequest.getPassword()));
        return userRepository.save(user);
    }


    public String admin(){
        String name = accountUtils.getCurrentUser().getUsername();
        return name;
    }

    @Override
    public AuthenResponse refresh(RefreshRequest refreshRequest) {
        AuthenResponse authenResponse = new AuthenResponse();
//        String refresh = jwtService.getRefreshClaim(refreshRequest.getToken());
        if (refreshTokenService.validateRefreshToken(refreshRequest.getRefreshToken())) {
            System.out.println(refreshTokenService.getIdFromRefreshToken(refreshRequest.getRefreshToken()));
            User user = userRepository.findById(refreshTokenService.getIdFromRefreshToken(refreshRequest.getRefreshToken())).orElseThrow(() -> new BadRequestException("User Not Found"));
            authenResponse.setToken(jwtService.generateToken(user, refreshRequest.getRefreshToken(),true));
        }else{
            throw new InvalidRefreshTokenException("Invalid refresh token");
        }
        return authenResponse;
    }
    @Override
    public void logout(RefreshRequest refreshRequest) {
        String refresh = refreshRequest.getRefreshToken();
        refreshTokenService.deleteRefreshToken(refresh);
    }

    @Override
    public AuthenticationResponse getCurrentAccount() {
       User user =  accountUtils.getCurrentUser();
       return userMapper.toAuthenticationResponse(user);
    }
}

