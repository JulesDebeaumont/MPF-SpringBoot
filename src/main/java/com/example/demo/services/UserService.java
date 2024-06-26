package com.example.demo.services;

import com.example.demo.controllers.exceptions.ResourceNotFoundException;
import com.example.demo.controllers.exceptions.UnprocessableResourceException;
import com.example.demo.dtos.UserDtos;
import com.example.demo.models.User;
import com.example.demo.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public List<UserDtos.GetByFullNameOut> findByFullName(String fullName) {
        return userRepository.findByFullName(fullName);
    }

    public UserDtos.GetByIdOut getUserById(Long id) {
        Optional<UserDtos.GetByIdOut> user = userRepository.findByIdRestrict(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id : " + id);
        }
        return user.get();
    }

    public UserDtos.PutByIdOut updateUserById(Long id, UserDtos.PutByIdInBody userPayload) {
        if (!Objects.equals(id, userPayload.id())) {
            throw new UnprocessableResourceException("Payload has an id that doesn't match");
        }
        User user = getUserByIdAllColumns(id);
        user.setFullName(userPayload.fullName());
        user.setRoles(userPayload.roles());
        userRepository.save(user);
        return new UserDtos.PutByIdOut(user.getId(), user.getFullName(), user.getRoles());
    }

    public UserDtos.PostOut createUser(UserDtos.PostIn userPayload) {
        User user = new User();
        user.setFullName(userPayload.fullName());
        user.setRoles(userPayload.roles());
        userRepository.save(user);
        return new UserDtos.PostOut(user.getId(), user.getFullName(), user.getRoles());
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    private User getUserByIdAllColumns(Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new ResourceNotFoundException("User not found with id : " + id);
        }
        return user.get();
    }
}
