package com.example.demo.repositories;

import com.example.demo.models.Sketch;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface SketchRepository extends CrudRepository<Sketch, Long>, PagingAndSortingRepository<Sketch, Long> {
}
