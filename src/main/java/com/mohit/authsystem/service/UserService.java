package com.mohit.authsystem.service;
import com.mohit.authsystem.entity.User;
import com.mohit.authsystem.repository.UserRepository;
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

    public User registerUser(User user){
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()){
            throw new RuntimeException("User Already Exists.");
        }
        // Encode Password
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
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

        return userDetails.get();
    }
}
