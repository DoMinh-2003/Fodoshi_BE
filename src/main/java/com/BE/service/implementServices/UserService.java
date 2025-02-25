package com.BE.service.implementServices;


import com.BE.exception.exceptions.NotFoundException;
import com.BE.model.entity.User;
import com.BE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;


    public User getUserByPhone(String phone) {
        User user = userRepository.findByPhoneNumber(phone).orElseThrow(() -> new NotFoundException("Couldn't find user'"));
        return user;
    }
}
