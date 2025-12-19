package com.smartLib.managementSystem.service;

import com.smartLib.managementSystem.model.User;
import com.smartLib.managementSystem.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final HashingService hashingService;

    public UserService(UserRepository userRepository,HashingService hashingService) {
        this.userRepository = userRepository;
        this.hashingService = hashingService;
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public void save(User user) {
        userRepository.save(user);
    }

    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    public void updateUser(User user) {
        userRepository.update(user);
    }

    public boolean createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return false;
        }
        user.setPassword(hashingService.hash(user.getPassword()));
        userRepository.save(user);
        return true;
    }
}
