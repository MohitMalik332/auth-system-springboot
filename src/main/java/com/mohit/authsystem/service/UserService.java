package com.mohit.authsystem.service;
import com.mohit.authsystem.entity.User;
import com.mohit.authsystem.repository.UserRepository;
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

    public User loginUser(String email, String password){
        Optional<User> userDetails = userRepository.findByEmail(email);

        if (userDetails.isEmpty()){
            throw new RuntimeException("User not Found.");
        }

        // Match Password
        if (!passwordEncoder.matches(password, userDetails.get().getPassword())){
            throw new RuntimeException("Invalid Password");
        }

        if (!userDetails.get().getIsVerified()){
            throw new RuntimeException("User Not Verified.");
        }

        return userDetails.get();
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
