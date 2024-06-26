package com.example.demo.repositories;

import com.example.demo.dtos.UserDtos;
import com.example.demo.dtos.UserFileDtos;
import com.example.demo.models.UserFile;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserFileRepository extends CrudRepository<UserFile, Long>, PagingAndSortingRepository<UserFile, Long>  {
    @Query("SELECT uf.id, uf.fileName, uf.mimeType, uf.createdAt " +
            "FROM userFile " +
            "WHERE uf.userId = :userId")
    List<UserFileDtos.GetUserFileOut> getUserFiles(@Param("userId") Long userId);
}
