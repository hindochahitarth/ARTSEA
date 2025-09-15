package com.art.artsea.service;

import com.art.artsea.model.User;
import com.art.artsea.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;

    // constructor injection
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // Find user by ID
//    public User findById(Long userId) {
//        return userRepository.findById(userId)
//                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));
//    }


    // Find user by Email
//    public Optional<User> getUserByEmail(String email) {
//        return userRepository.findByEmail(email);
//    }
//
//    // Find user by Username
//    public Optional<User> getUserByUsername(String username) {
//        return userRepository.findByUsername(username);
//    }

    @Transactional
    public void updateProfile(Long userId, String username, Long phone) {
        userRepository.updateUserProfile(userId, username, phone);
    }

}