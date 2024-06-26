package com.example.demo.dtos;

import com.example.demo.models.*;

import java.util.List;

public class UserDtos {
    public record GetByIdOut(Long id, String name) {};

    public record GetByFullNameOut(Long id, String name) {};

    public record PutByIdInBody(Long id, String fullName, List<User.EUserRoles> roles) {};
    public record PutByIdOut(Long id, String fullName, List<User.EUserRoles> roles) {};

    public record PostIn(String fullName, List<User.EUserRoles> roles) {};
    public record PostOut(Long id, String fullName, List<User.EUserRoles> roles) {};

}
