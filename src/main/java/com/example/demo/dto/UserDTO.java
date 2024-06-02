package com.example.demo.dto;

public class UserDTO {
    public record GetIn(long id) {};
    public record GetOut(long id, String name) {};

    public record GetAllOut(long id, String name) {};
}

