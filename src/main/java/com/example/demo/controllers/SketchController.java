package com.example.demo.controllers;

import com.example.demo.models.Sketch;
import com.example.demo.services.SketchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "sketches")
public class SketchController {

    @Autowired
    public SketchService sketchService;

    @GetMapping(path = "/{id}")
    public ResponseEntity<?> getSketchById(@PathVariable Long sketchId) {
        return ResponseEntity.ok(sketchService.getSketchById(sketchId));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<?> putById(@PathVariable Long id, @RequestBody Sketch sketchPayload) {
        return ResponseEntity.ok(sketchService.updateSketchById(id, sketchPayload));
    }

    @PostMapping(path = "/")
    public ResponseEntity<?> create( @RequestBody Sketch sketchPayload) {
        return ResponseEntity.ok(sketchService.createSketch(sketchPayload));
    }

    @DeleteMapping(path = "/")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        sketchService.deleteSketch(id);
        return ResponseEntity.ok().build();
    }
}
