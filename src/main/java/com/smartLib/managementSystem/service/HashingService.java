    package com.smartLib.managementSystem.service;

    import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
    import org.springframework.stereotype.Service;

    @Service
    public class HashingService {

        private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        public String hash(String rawPassword) {
            return encoder.encode(rawPassword);
        }

        public boolean matches(String rawPassword, String hashedPassword) {
            return encoder.matches(rawPassword, hashedPassword);
        }
    }
