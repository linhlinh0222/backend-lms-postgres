package com.example.lms.controller;

import com.example.lms.entity.Course;
import com.example.lms.repository.CourseRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/courses")
@Tag(name = "Courses", description = "Course APIs backed by PostgreSQL")
public class CourseController {
    private final CourseRepository repo;
    public CourseController(CourseRepository repo) { this.repo = repo; }

    @Operation(summary = "Danh sách khoá học")
    @GetMapping
    public List<Course> list() { return repo.findAll(); }

    @Operation(summary = "Lấy chi tiết theo mã")
    @GetMapping("/{code}")
    public ResponseEntity<Course> get(@PathVariable String code) {
        return repo.findByCode(code).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Tạo/ghi đè khoá học")
    @PutMapping("/{code}")
    @Transactional
    public Course upsert(@PathVariable String code, @RequestBody Map<String, String> body) {
        String title = body.getOrDefault("title", "Untitled");
        return repo.findByCode(code).map(c -> { c.setTitle(title); return c; }).orElseGet(() -> repo.save(new Course(code, title)));
    }

    @Operation(summary = "Xoá khoá học")
    @DeleteMapping("/{code}")
    @Transactional
    public ResponseEntity<Map<String, String>> delete(@PathVariable String code) {
        return repo.findByCode(code).map(c -> { repo.delete(c); return ResponseEntity.ok(Map.of("deleted", code)); })
                .orElse(ResponseEntity.notFound().build());
    }
}
