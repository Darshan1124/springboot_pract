package com.example.journal.controller;

import com.example.journal.entity.User;
import com.example.journal.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    
    @GetMapping("/health")
    public ResponseEntity<?> health(){
    	return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/getallusers")
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PostMapping("createuser")
    public ResponseEntity<User> createUser(@RequestBody User user) {
        try {
            User savedUser = userService.saveNewUser(user);
            return new ResponseEntity<>(savedUser, HttpStatus.CREATED);
        } catch (Exception e) {
            // Catches exceptions like duplicate usernames (due to unique=true in entity)
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{username}")
    public ResponseEntity<User> updateUser(@PathVariable String username, @RequestBody User user) {
        User updatedUser = userService.updateUser(username, user);
        if (updatedUser != null) {
            return new ResponseEntity<>(updatedUser, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @DeleteMapping("/id/{myId}")
    public ResponseEntity<?> deleteUserById(@PathVariable Long myId) {
        // Because of CascadeType.ALL, deleting the user will also automatically 
        // delete all journal_entries associated with this user in the MySQL database!
        userService.deleteById(myId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT); 
    }
}
