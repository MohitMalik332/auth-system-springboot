package com.mohit.authsystem.controller;

import com.mohit.authsystem.entity.User;
import com.mohit.authsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public User registerUser(@RequestBody User user){
        return userService.registerUser(user);
    }

    @PostMapping("/login")
    public User loginUser(@RequestBody User user){
        return userService.loginUser(user.getEmail(), user.getPassword());
    }

    @PostMapping("/verify-otp")
    public String veryfyOtp(@RequestBody User user){
        return userService.verifyOtp(user.getEmail(), user.getOtp());
    }

}
