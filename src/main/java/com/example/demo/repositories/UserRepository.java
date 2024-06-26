package com.example.demo.repositories;

import com.example.demo.dtos.UserDtos;
import com.example.demo.models.User;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long>, PagingAndSortingRepository<User, Long> {
    @Query("SELECT u.id, u.fullName " +
            "FROM user u " +
            "WHERE u.fullname ILIKE %:fullName% " +
            "LIMIT 20")
    List<UserDtos.GetByFullNameOut> findByFullName(@Param("fullName") String fullName);

    @Query("SELECT u.id, u.fullName " +
            "FROM user u " +
            "WHERE u.id = :id")
    Optional<UserDtos.GetByIdOut> findByIdRestrict(@Param("id") Long id);
}
