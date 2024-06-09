package com.example.demo.models;

import jakarta.persistence.Entity;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
public class User {
    private int idRes;
    private String fullName;
    private String refreshToken;
    private Date refreshTokenExpiry;
    private List<EUserRoles> roles;
    private Collection<ProjectUser> projectUsers;
    private Collection<ProjectFile> projectFiles;
    private Collection<UserFile> userFiles;
    private Collection<Sketch> sketches;

    public enum EUserRoles
    {
        Admin,
        Worker
    }

    public Integer getIdRes() {
        return idRes;
    }

    public void setIdRes(Integer idRes) {
        this.idRes = idRes;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Date getRefreshTokenExpiry() {
        return refreshTokenExpiry;
    }

    public void setRefreshTokenExpiry(Date refreshTokenExpiry) {
        this.refreshTokenExpiry = refreshTokenExpiry;
    }

    public List<EUserRoles> getRoles() {
        return roles;
    }

    public void setRoles(List<EUserRoles> roles) {
        this.roles = roles;
    }

    public Collection<ProjectUser> getProjectUsers() {
        return projectUsers;
    }

    public void setProjectUsers(Collection<ProjectUser> projectUsers) {
        this.projectUsers = projectUsers;
    }

    public Collection<ProjectFile> getProjectFiles() {
        return projectFiles;
    }

    public void setProjectFiles(Collection<ProjectFile> projectFiles) {
        this.projectFiles = projectFiles;
    }

    public Collection<UserFile> getUserFiles() {
        return userFiles;
    }

    public void setUserFiles(Collection<UserFile> userFiles) {
        this.userFiles = userFiles;
    }

    public Collection<Sketch> getSketches() {
        return sketches;
    }

    public void setSketches(Collection<Sketch> sketches) {
        this.sketches = sketches;
    }
}
