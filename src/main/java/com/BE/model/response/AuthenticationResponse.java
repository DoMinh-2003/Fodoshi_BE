package com.BE.model.response;


import com.BE.enums.UserRole;
import com.BE.model.entity.RefreshToken;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToMany;
import lombok.*;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Builder
public class AuthenticationResponse {
     private UUID id;
     private String name;
     private String email;
     private String phoneNumber;
     private String password;
     @Enumerated(EnumType.STRING)
     private UserRole role;
     private LocalDateTime createdAt = LocalDateTime.now();
     String token;
     String refreshToken;
     String image;

}

