package com.BE.utils;

import com.BE.model.entity.User;
import com.BE.repository.UserRepository;
import com.BE.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


@Component
public class AccountUtils implements ApplicationContextAware {

    private static UserRepository userRepository;

    private static JWTService jwtService;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        userRepository = applicationContext.getBean(UserRepository.class);
        jwtService = applicationContext.getBean(JWTService.class);

    }


    public static String extractTokenFromRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            String header = request.getHeader("Authorization");
            if (header != null && header.startsWith("Bearer ")) {
                return header.substring(7); // Bỏ "Bearer "
            }
        }
        return null;
    }
    public static User getCurrentUser() {
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        String token = extractTokenFromRequest();

        if (token == null) {
            throw new RuntimeException("No token found in request");
        }

        String authType = jwtService.getTypeClaim(token);

        if ("google".equalsIgnoreCase(authType)) {
            return userRepository.findByEmail(userName)
                    .orElseThrow(() -> new RuntimeException("User not found by email: " + authType));
        }

        return userRepository.findByPhoneNumber(userName)
                .orElseThrow(() -> new RuntimeException("User not found by phone: " + userName));
    }
}
