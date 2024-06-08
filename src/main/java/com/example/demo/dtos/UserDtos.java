package com.example.demo.dtos;

public class UserDtos {
    public record GetIn(long id) {};
    public record GetOut(long id, String name) {};

    public record GetAllOut(long id, String name) {};
}

