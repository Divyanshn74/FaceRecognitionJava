package com.example.facerecog.service;

import com.example.facerecog.model.User;
import com.example.facerecog.repository.UserRepository;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void changePassword(String email, String newPassword) {
        userRepository.findByEmail(email).ifPresent(user -> {
            user.setPassword(passwordEncoder.encode(newPassword));
            user.setDefaultPasswordUsed(false); // Flip the flag
            userRepository.save(user);
        });
    }

    public boolean changeUserPassword(String email, String oldPassword, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            return false; // User not found
        }
        User user = userOptional.get();

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return false; // Old password does not match
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        // If they are changing their password, it's no longer the default one
        user.setDefaultPasswordUsed(false);
        userRepository.save(user);
        return true;
    }

    @Transactional
    public void importUsersFromCsv(MultipartFile file) throws IOException, CsvValidationException {
        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {
            String[] line;
            // Skip header row
            reader.readNext();
            
            List<User> usersToSave = new ArrayList<>();
            while ((line = reader.readNext()) != null) {
                User user = new User();
                user.setName(line[0]);
                user.setEmail(line[1]);
                user.setEnrollmentNumber(line[2]);
                user.setPassword(passwordEncoder.encode("12345678"));
                user.setRole("ROLE_USER");
                user.setDefaultPasswordUsed(true);
                usersToSave.add(user);
            }
            userRepository.saveAll(usersToSave);
        }
    }
}
