package com.mohit.authsystem.service;
import com.mohit.authsystem.entity.User;
import com.mohit.authsystem.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User registerUser(User user){
        Optional<User> existingUser = userRepository.findByEmail(user.getEmail());

        if (existingUser.isPresent()){
            throw new RuntimeException("User Already Exists.");
        }

        return userRepository.save(user);
    }

    public User loginUser(String email, String password){
        Optional<User> userDetails = userRepository.findByEmail(email);

        if (userDetails.isEmpty()){
            throw new RuntimeException("User not Found.");
        }

        if (!userDetails.get().getPassword().equals(password)){
            throw new RuntimeException("Invalid Password");
        }

        return userDetails.get();
    }
}
