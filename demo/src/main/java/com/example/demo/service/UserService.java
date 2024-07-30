package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

import javax.security.sasl.AuthenticationException;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User authenticate(String username, String password) throws AuthenticationException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new AuthenticationException("Incorrect password");
        }
    }

    public boolean userExists(String username) {
        return userRepository.findByUsername(username).isPresent();
    }
}
