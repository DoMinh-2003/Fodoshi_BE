package com.BE.service;


import com.BE.model.entity.User;
import com.BE.repository.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Service
public class JWTService {

    @Value("${spring.secretkey}")
    private String SECRET_KEY;

    @Value("${spring.duration}")
    private long DURATION;
    

    @Autowired
    UserRepository userRepository;

    @Autowired
    RefreshTokenService refreshTokenService;



    public String generateToken(User user, String refresh, boolean isRefresh) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        System.out.println(user.getUsername());
        String authType = (user.getPhoneNumber() != null && !user.getPhoneNumber().isEmpty())
                ? "local"
                : "google";

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUsername())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(DURATION, ChronoUnit.SECONDS)))
                .claim("scope", "ROLE_" + user.getRole())
                .claim("refresh", refresh)
                .claim("auth_type", authType)
                .build();

        if(!isRefresh){
            refreshTokenService.saveRefreshToken(refresh,user.getId());
        }

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }


    public String generateToken(User user) {

        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);

        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getPhoneNumber())
                .issueTime(Date.from(Instant.now()))
                .expirationTime(Date.from(Instant.now().plus(DURATION, ChronoUnit.SECONDS)))
                .claim("scope", "ROLE_" + user.getRole())
                .build();

        Payload payload = new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header, payload);

        try {
            jwsObject.sign(new MACSigner(SECRET_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }
    public User getUserByToken(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);

            MACVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
            if (!jwsObject.verify(verifier)) {
                throw new RuntimeException("Invalid token signature");
            }

            JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());
            String username = claimsSet.getSubject();

            return userRepository.findByPhoneNumber(username).orElse(null);
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }

    public String getRefreshClaim(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);

            MACVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
            if (!jwsObject.verify(verifier)) {
                throw new RuntimeException("Invalid token signature");
            }
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            return claimsSet.getStringClaim("refresh");
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }

    public String getTypeClaim(String token) {
        try {
            JWSObject jwsObject = JWSObject.parse(token);

            MACVerifier verifier = new MACVerifier(SECRET_KEY.getBytes());
            if (!jwsObject.verify(verifier)) {
                throw new RuntimeException("Invalid token signature");
            }
            JWTClaimsSet claimsSet = JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

            return claimsSet.getStringClaim("auth_type");
        } catch (ParseException | JOSEException e) {
            throw new RuntimeException("Error parsing token", e);
        }
    }



}






