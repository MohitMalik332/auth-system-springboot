package com.mohit.authsystem.service;
import com.mohit.authsystem.dto.LoginRequest;
import com.mohit.authsystem.entity.User;
import com.mohit.authsystem.repository.UserRepository;
import com.mohit.authsystem.util.JwtUtil;
import com.mohit.authsystem.util.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    private final JwtUtil jwtUtil;

    public UserService(JwtUtil jwtUtil){
        this.jwtUtil = jwtUtil;
    }

    public User registerUser(User user){
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()){
            throw new RuntimeException("User Already Exists.");
        }
        // Encode Password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        // Generate OTP
        String otp = OtpUtil.generateOtp();

        // Set OTP details
        user.setOtp(otp);
        user.setOtpGeneratedTime(System.currentTimeMillis());
        user.setIsVerified(false);

        // Save user
        User savedUser = userRepository.save(user);

        // Send OTP
        emailService.sendOtpEmail(user.getEmail(), otp);

        return savedUser;
    }

    public String loginUser(LoginRequest request){
        Optional<User> userDetails = userRepository.findByEmail(request.getEmail());

        if (userDetails.isEmpty()){
            throw new RuntimeException("User not Found.");
        }

        if (!userDetails.get().getIsVerified()){
            throw new RuntimeException("User Not Verified.");
        }

        // Match Password
        if (!passwordEncoder.matches(request.getPassword(), userDetails.get().getPassword())){
            throw new RuntimeException("Invalid Password");
        }

        return jwtUtil.generateToken(userDetails.get().getEmail());
    }

    public String verifyOtp(String email, String otp){
        Optional<User> user = userRepository.findByEmail(email);

        if(user.isEmpty()){
            throw new RuntimeException("User Not Found.");
        }

        // Match OTP
        if(!user.get().getOtp().equals(otp)){
            throw new RuntimeException("Invalid OTP");
        }

        // Check OTP time
        long currentTime = System.currentTimeMillis();
        long diff = currentTime - user.get().getOtpGeneratedTime();

        if (diff > 5 * 60 * 1000){
            throw new RuntimeException("OTP Expired");
        }

        user.get().setIsVerified(true);
        user.get().setOtp(null);

        userRepository.save(user.get());

        return "User Verified Successfully";
    }
}
