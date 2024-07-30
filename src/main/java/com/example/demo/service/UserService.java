package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.security.sasl.AuthenticationException;
import java.security.Key;
import java.util.Date;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
    private final Key jwtSecret = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User authenticate(String email, String password) throws AuthenticationException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new AuthenticationException("User not found"));

        if (passwordEncoder.matches(password, user.getPassword())) {
            return user;
        } else {
            throw new AuthenticationException("Incorrect password");
        }
    }

    public boolean userExists(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    public User findOrCreateDiscordUser(String discordId, String email, String username) {
        Optional<User> existingUser = userRepository.findByEmail(email);
        if (existingUser.isPresent()) {
            return existingUser.get();
        }

        User newUser = new User();
        newUser.setEmail(email);
        newUser.setName(username);
        newUser.setPassword(passwordEncoder.encode(discordId)); // Use discordId as password
        return userRepository.save(newUser);
    }

    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + 86400000)) // 1 day
                .signWith(jwtSecret)
                .compact();
    }
}