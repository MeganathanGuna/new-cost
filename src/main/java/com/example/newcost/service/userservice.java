package com.example.newcost.service;

import com.example.newcost.model.user;
import com.example.newcost.repo.userrepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class userservice {
    @Autowired
    private userrepo userRepository;

    public user registerUser(user user) {

        return userRepository.save(user);
    }

    public user findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public user getUserById(Long userId) {
        Optional<user> userOptional = userRepository.findById(userId);
        if (userOptional.isPresent()) {
            return userOptional.get();
        } else {
            throw new RuntimeException("User not found");
        }
    }
}
