package com.example.demo.controllers;

import com.example.demo.dtos.UserDtos;
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
    public UserDtos.GetOut get(UserDtos.GetIn userParam) {
        User newUser = new User();
        newUser.setFullName("Didier");
        userRepository.save(newUser);

        return new UserDtos.GetOut(userParam.id(), "Didier");
    }

    @GetMapping(path="/")
    public Iterable<User> getAllUsers() {
        return userRepository.findAll();
    }
}
