package com.example.demo.controllers;

import com.example.demo.dtos.UserDtos;
import com.example.demo.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping(path="/")
    public ResponseEntity<?> getByFullName(@PathVariable String fullName) {
        return ResponseEntity.ok(userService.findByFullName(fullName));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> putById(@PathVariable Long id, @RequestBody UserDtos.PutByIdInBody payloadUser) {
        return ResponseEntity.ok(userService.updateUserById(id, payloadUser));
    }

    @PostMapping(path = "/")
    public ResponseEntity<?> create(@RequestBody UserDtos.PostIn payloadUser) {
        return ResponseEntity.ok(userService.createUser(payloadUser));
    }

    @DeleteMapping(path = "/")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
