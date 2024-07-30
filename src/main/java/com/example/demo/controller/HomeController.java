package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "Welcome to Know-Your-Neighborhood";
    }
    
    @GetMapping("/contact-us")
    public String contactUs() {
        return "Contact Us Page";
    }

    @GetMapping("/about-us")
    public String aboutUs() {
        return "About Us Page";
    }

    @GetMapping("/terms-conditions")
    public String termsConditions() {
        return "Terms and Conditions Page";
    }
}

