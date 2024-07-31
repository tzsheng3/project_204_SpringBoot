package com.example.demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HomeController {

    @GetMapping("/home")
    public String home() {
        return "Welcome to Know-Your-Neighborhood"; // Return welcome message
    }
    
    @GetMapping("/contact-us")
    public String contactUs() {
        return "Contact Us Page"; // Return contact us message
    }

    @GetMapping("/about-us")
    public String aboutUs() {
        return "About Us Page"; // Return about us message
    }

    @GetMapping("/terms-conditions")
    public String termsConditions() {
        return "Terms and Conditions Page"; // Return terms and conditions message
    }
}
