package com.example.demo.services;

import com.example.demo.controllers.exceptions.ResourceNotFoundException;
import com.example.demo.controllers.exceptions.UnprocessableResourceException;
import com.example.demo.models.Sketch;
import com.example.demo.repositories.SketchRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Optional;

@Service
public class SketchService {

    @Autowired
    public SketchRepository sketchRepository;

    public Sketch getSketchById(Long sketchId) {
        Optional<Sketch> sketch = sketchRepository.findById(sketchId);
        if (sketch.isEmpty()) {
            throw new ResourceNotFoundException("Sketch not found with id : " + sketchId);
        }
        return sketch.get();
    }

    public Sketch updateSketchById(Long id, Sketch sketchPayload) {
        if (!Objects.equals(id, sketchPayload.getId())) {
            throw new UnprocessableResourceException("Payload has an id that doesn't match");
        }
        Sketch sketch = new Sketch();
        sketch.setAuthorId(1L); // TODO
        sketch.setCore(sketchPayload.getCore());
        sketchRepository.save(sketch);
        return sketch;
    }

    public Sketch createSketch(Sketch sketchPayload) {
        Sketch sketch = new Sketch();
        sketch.setAuthorId(1L); // TODO
        sketch.setCore(sketchPayload.getCore());
        sketchRepository.save(sketch);
        return sketch;
    }

    public void deleteSketch(Long id) {
        sketchRepository.deleteById(id);
    }

    private Sketch getSketchByIdAllColumns(Long id) {
        Optional<Sketch> sketch = sketchRepository.findById(id);
        if (sketch.isEmpty()) {
            throw new ResourceNotFoundException("Sketch not found with id : " + id);
        }
        return sketch.get();
    }
}
