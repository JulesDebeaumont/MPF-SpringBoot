package com.example.demo.repositories;

import com.example.demo.dtos.ProjectDtos;
import com.example.demo.dtos.UserDtos;
import com.example.demo.models.Project;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends CrudRepository<Project, Long>, PagingAndSortingRepository<Project, Long> {
    @Query("SELECT p.id, p.title, p.deadline, p.state, u.id, u.full_name " +
            "FROM project p" +
            "INNER JOIN project_user pu " +
            "INNER JOIN users u " +
            "LIMIT = :limit " +
            "OFFSET = :offset")
    List<ProjectDtos.getProjectOut> getWithLimitAndOffset(@Param("limit") Long limit, @Param("offset") Long offset);

    @Query("SELECT p.id, p.title, p.deadline, p.state, u.id, u.full_name " +
            "FROM project p" +
            "INNER JOIN project_user pu " +
            "INNER JOIN users u " +
            "WHERE p.id = :id")
    Optional<ProjectDtos.getProjectByIdOut> findByIdRestrict(@Param("id") Long id);
}
