package com.mohit.authsystem.controller;

import com.mohit.authsystem.dto.LoginRequest;
import com.mohit.authsystem.entity.User;
import com.mohit.authsystem.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

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
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest request){
        String token = userService.loginUser(request);

        return ResponseEntity.ok().body(Map.of(
                "token", token
        ));
    }

    @PostMapping("/verify-otp")
    public String veryfyOtp(@RequestBody User user){
        return userService.verifyOtp(user.getEmail(), user.getOtp());
    }

}
