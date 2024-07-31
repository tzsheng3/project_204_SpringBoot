package com.example.demo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api")
public class DiscordAuthController {

    @Autowired
    private UserService userService;

    private final String clientId = "1253729365624225974";
    private final String clientSecret = "YXPC_61B657ZkmjTIzTMZMZG4-fDXHTX";
    private final String redirectUri = "http://localhost:3000/auth/discord/callback";
    private final String tokenUrl = "https://discord.com/api/oauth2/token";
    private final String userInfoUrl = "https://discord.com/api/users/@me";

    @PostMapping("/login/oauth2/code/discord")
    public ResponseEntity<?> handleDiscordCallback(@RequestBody Map<String, String> payload) {
        String code = payload.get("code");
        if (code == null) {
            return ResponseEntity.badRequest().body("No code provided"); // Return error if no code provided
        }

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/x-www-form-urlencoded");

        String body = "client_id=" + clientId +
                      "&client_secret=" + clientSecret +
                      "&grant_type=authorization_code" +
                      "&code=" + code +
                      "&redirect_uri=" + redirectUri;

        HttpEntity<String> requestEntity = new HttpEntity<>(body, headers);
        ResponseEntity<String> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, requestEntity, String.class);

        if (response.getStatusCode() != HttpStatus.OK) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching access token"); // Return error if token request fails
        }

        try {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> tokenResponse = mapper.readValue(response.getBody(), Map.class);
            String accessToken = (String) tokenResponse.get("access_token");

            headers = new HttpHeaders();
            headers.add("Authorization", "Bearer " + accessToken);
            requestEntity = new HttpEntity<>(headers);
            response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, requestEntity, String.class);

            if (response.getStatusCode() != HttpStatus.OK) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching user info"); // Return error if user info request fails
            }

            Map<String, Object> userInfo = mapper.readValue(response.getBody(), Map.class);
            String discordId = (String) userInfo.get("id");
            String email = (String) userInfo.get("email");
            String username = (String) userInfo.get("username");

            User user = userService.findOrCreateDiscordUser(discordId, email, username);
            String token = userService.generateToken(user);
            return ResponseEntity.ok().body(Map.of("token", token, "user", user)); // Return token and user info
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error processing Discord login: " + e.getMessage()); // Return error if processing fails
        }
    }
}
