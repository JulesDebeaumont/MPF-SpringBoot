package com.example.demo.services;

import com.example.demo.controllers.exceptions.ResourceNotFoundException;
import com.example.demo.controllers.exceptions.UnprocessableResourceException;
import com.example.demo.dtos.ProjectDtos;
import com.example.demo.dtos.UserDtos;
import com.example.demo.models.Project;
import com.example.demo.repositories.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class ProjectService {

    @Autowired
    public ProjectRepository projectRepository;

    public List<ProjectDtos.getProjectOut> getProjects(Long limit, Long offset) {
        if (limit == null || limit > 20L) {
            limit = 20L;
        }
        return projectRepository.getWithLimitAndOffset(limit, offset);
    }

    public ProjectDtos.getProjectByIdOut getProjectById(Long id) {
        Optional<ProjectDtos.getProjectByIdOut> project = projectRepository.findByIdRestrict(id);
        if (project.isEmpty()) {
            throw new ResourceNotFoundException("Project not found with id : " + id);
        }
        return project.get();
    }

    public UserDtos.PutByIdOut updateProjectById(Long id, UserDtos.PutByIdInBody userPayload) {
        if (!Objects.equals(id, userPayload.id())) {
            throw new UnprocessableResourceException("Payload has an id that doesn't match");
        }
        Project project = getProjectByIdAllColumns(id);
        project.setFullName(userPayload.fullName());
        project.setRoles(userPayload.roles());
        projectRepository.save(project);
        return new UserDtos.PutByIdOut(project.getId(), project.getFullName(), project.getRoles());
    }

    public UserDtos.PostOut createProject(UserDtos.PostIn userPayload) {
        Project project = new Project();
        project.setFullName(userPayload.fullName());
        project.setRoles(userPayload.roles());
        projectRepository.save(project);
        return new UserDtos.PostOut(project.getId(), project.getFullName(), project.getRoles());
    }

    public void deleteProject(Long id) {
        projectRepository.deleteById(id);
    }

    private Project getProjectByIdAllColumns(Long id) {
        Optional<Project> project = projectRepository.findById(id);
        if (project.isEmpty()) {
            throw new ResourceNotFoundException("Project not found with id : " + id);
        }
        return project.get();
    }
}
