package com.smartLib.managementSystem.service;

import com.smartLib.managementSystem.model.User;
import com.smartLib.managementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;


import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final HashingService hashingService;

    public AuthService(UserRepository userRepository, HashingService hashingService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
    }

    public Optional<User> authenticate(String email, String rawPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) return Optional.empty();

        User user = userOpt.get();

        if (!hashingService.matches(rawPassword, user.getPassword())) {
            return Optional.empty();
        }

        return Optional.of(user);
    }

    public boolean register(User user) {

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return false;
        }

        user.setRole("USER");
        user.setPassword(hashingService.hash(user.getPassword()));

        userRepository.save(user);
        return true;
    }


}
