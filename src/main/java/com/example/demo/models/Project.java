package com.example.demo.models;

import jakarta.persistence.Entity;

import java.util.Collection;
import java.util.Date;

@Entity
public class Project {

    private int id;
    private String description;
    private Date deadline;
    private EProjectState state;
    private Collection<ProjectUser> projectUsers;
    private Collection<Sketch> sketches;
    private Collection<ProjectFile> projectFiles;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadline() {
        return deadline;
    }

    public void setDeadline(Date deadline) {
        this.deadline = deadline;
    }

    public EProjectState getState() {
        return state;
    }

    public void setState(EProjectState state) {
        this.state = state;
    }

    public Collection<ProjectUser> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(Collection<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public Collection<Sketch> getSketches() {
        return sketches;
    }

    public void setSketches(Collection<Sketch> sketches) {
        this.sketches = sketches;
    }

    public Collection<ProjectFile> getProjectFiles() {
        return projectFiles;
    }

    public void setProjectFiles(Collection<ProjectFile> projectFiles) {
        this.projectFiles = projectFiles;
    }

    public enum  EProjectState {
        started,
        pending,
        done,
        canceled
    }
}
