package com.example.journal.service;

import com.example.journal.dto.UserDTO;
import com.example.journal.entity.User;
import com.example.journal.mapper.UserMapper;
import com.example.journal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional
    public UserDTO createUser(UserDTO dto) {
        if (dto == null || dto.getUsername() == null || dto.getUsername().trim().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username is required");
        }

        String username = dto.getUsername().trim();

        if (userRepository.findByUsername(username).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
        }

        User user = userMapper.toEntity(dto);
        user.setUsername(username);

        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Transactional(readOnly = true)
    public List<UserDTO> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserDTO getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional(readOnly = true)
    public UserDTO getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDTO)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
    }

    @Transactional
    public UserDTO updateUser(String username, UserDTO dto) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));

        if (dto != null && dto.getUsername() != null && !dto.getUsername().trim().isEmpty()) {
            String newUsername = dto.getUsername().trim();

            if (!newUsername.equals(username) && userRepository.findByUsername(newUsername).isPresent()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Username already exists");
            }

            user.setUsername(newUsername);
        }

        User saved = userRepository.save(user);
        return userMapper.toDTO(saved);
    }

    @Transactional
    public void deleteUserById(Long id) {
        if (!userRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found");
        }
        userRepository.deleteById(id);
    }
}