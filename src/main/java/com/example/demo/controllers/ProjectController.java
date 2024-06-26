package com.example.demo.controllers;

import com.example.demo.dtos.UserDtos;
import com.example.demo.services.ProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "projects")
public class ProjectController {

    @Autowired
    public ProjectService projectService;

    @GetMapping(path="/")
    public ResponseEntity<?> getProjects(@PathVariable Long limit, @PathVariable Long offset) {
        return ResponseEntity.ok(projectService.getProject(limit, offset));
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return ResponseEntity.ok(projectService.getProjectById(id));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> putById(@PathVariable Long id, @RequestBody UserDtos.PutByIdInBody payloadUser) {
        return ResponseEntity.ok(projectService.updateProjectById(id, payloadUser));
    }

    @PostMapping(path = "/")
    public ResponseEntity<?> create(@RequestBody UserDtos.PostIn payloadUser) {
        return ResponseEntity.ok(projectService.createProject(payloadUser));
    }

    @DeleteMapping(path = "/")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        projectService.deleteProject(id);
        return ResponseEntity.ok().build();
    }
}
