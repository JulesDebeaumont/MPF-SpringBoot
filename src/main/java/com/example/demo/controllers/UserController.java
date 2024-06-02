package com.example.demo.controllers;

import com.example.demo.dto.UserDTO;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "users")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping(path = "/{id}")
    public UserDTO.GetOut get(UserDTO.GetIn userParam) {
        User newUser = new User();
        newUser.setFullName("Didier");
        userRepository.save(newUser);

        return new UserDTO.GetOut(userParam.id(), "Didier");
    }

    @GetMapping(path="/")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
