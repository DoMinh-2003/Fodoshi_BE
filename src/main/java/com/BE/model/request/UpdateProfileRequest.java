package com.BE.model.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UpdateProfileRequest {

    String image;

    @NotBlank(message = "FullName cannot be blank")
    String name;

    @NotBlank(message = "Email cannot be blank")
    String email;

    @NotBlank(message = "Phone Number cannot be blank")
    String phoneNumber;
}
