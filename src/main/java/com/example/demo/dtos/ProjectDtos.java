package com.example.demo.dtos;

import com.example.demo.models.Project;

import java.util.Date;
import java.util.List;

public class ProjectDtos {
    public record getProjectOut(
            Long id,
            String title,
            Date deadline,
            Project.EProjectState state,
            List<GetProjectOutUserDto> users
    ) {};
    private record GetProjectOutUserDto(Long id, String fullName) {};

    public record getProjectByIdOut(
            Long id,
            String title,
            Date deadline,
            Project.EProjectState state,
            List<getProjectByIdProjectUserOut> projectUser
    ) {};
    private record getProjectByIdProjectUserOut(Long id, String fullName) {};


}
