package com.BE.service.implementServices;


import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.entity.User;
import com.BE.model.request.ChangePasswordRequest;
import com.BE.model.request.ResetPasswordRequest;
import com.BE.model.request.UpdateProfileRequest;
import com.BE.repository.UserRepository;
import com.BE.utils.AccountUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    AccountUtils accountUtils;


    @Autowired
    PasswordEncoder passwordEncoder;


    public User getUserByPhone(String phone) {
        User user = userRepository.findByPhoneNumber(phone).orElseThrow(() -> new NotFoundException("Couldn't find user'"));
        return user;
    }

    public User updateProfile(UpdateProfileRequest updateProfileRequest) {
        User user = accountUtils.getCurrentUser();
        user.setImage(updateProfileRequest.getImage());
        user.setEmail(updateProfileRequest.getEmail());
        user.setName(updateProfileRequest.getName());
        user.setPhoneNumber(updateProfileRequest.getPhoneNumber());
        return userRepository.save(user);
    }



    public User changePassword(ChangePasswordRequest changePasswordRequest) {
        User user = accountUtils.getCurrentUser();

        if (!passwordEncoder.matches(changePasswordRequest.getOldPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Old password is incorrect");
        }
        user.setPassword(passwordEncoder.encode(changePasswordRequest.getNewPassword()));
        return userRepository.save(user);
    }
}
