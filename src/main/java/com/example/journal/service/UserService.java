package com.example.journal.service;


import com.example.journal.entity.User;
import com.example.journal.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public User saveNewUser(User user) {
        // In a real production app, you would encode/hash the password here before saving
        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Transactional // Unlocks Hibernate Dirty Checking for the User entity
    public User updateUser(String username, User newUser) {
        User oldUser = userRepository.findByUsername(username).orElse(null);
        
        if (oldUser != null) {
            // Check if the new username is valid before updating
            if (newUser.getUsername() != null && !newUser.getUsername().trim().isEmpty()) {
                oldUser.setUsername(newUser.getUsername());
            }
            
            // Note: Because of CascadeType.ALL on the User entity, any changes to 
            // oldUser.getJournalEntries() here would also be automatically saved to MySQL.
            return oldUser;
        }
        return null;
    }
}
