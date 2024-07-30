package com.example.demo.controller;

import com.example.demo.entity.User;
import com.example.demo.errorhanding.ErrorResponse;
import com.example.demo.service.UserService;

import java.util.Map;

import javax.security.sasl.AuthenticationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://localhost:3000")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.userExists(user.getEmail())) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Email already exists"));
        }
        User registeredUser = userService.register(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User loginRequest) {
        try {
            User user = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
            String token = userService.generateToken(user);
            return ResponseEntity.ok().body(Map.of("token", token, "user", user));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponse("Incorrect Email or Password"));
        }
    }
    
}