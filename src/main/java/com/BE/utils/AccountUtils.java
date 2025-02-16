package com.BE.utils;

import com.BE.model.entity.User;
import com.BE.repository.UserRepository;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class AccountUtils implements ApplicationContextAware {

    private static UserRepository userRepository;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        userRepository = applicationContext.getBean(UserRepository.class);
    }

    public static User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByPhoneNumber(userName).orElseThrow();
    }
}
