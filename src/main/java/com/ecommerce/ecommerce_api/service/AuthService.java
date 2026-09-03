package com.ecommerce.ecommerce_api.service;

import com.ecommerce.ecommerce_api.entity.User;
import com.ecommerce.ecommerce_api.repository.UserRepository;
import com.ecommerce.ecommerce_api.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtUtil jwtUtil;

    public String register(User user) {
        if (userRepository.findByUsername(user.getUsername()).isPresent()) {
            throw new IllegalArgumentException("Username already taken");
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole("USER");
        userRepository.save(user);
        return "User registered successfully";
    }

    public String login(User loginRequest) {
        User existingUser = userRepository.findByUsername(loginRequest.getUsername()).orElse(null);
        if (existingUser == null || !passwordEncoder.matches(loginRequest.getPassword(), existingUser.getPassword())) {
            throw new IllegalArgumentException("Invalid username or password");
        }
        return jwtUtil.generateToken(existingUser.getUsername(), existingUser.getRole());
    }
}