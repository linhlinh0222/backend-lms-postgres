package com.example.lms.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "course")
public class Course {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 64)
    private String code;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    public Course() {}
    public Course(String code, String title) { this.code = code; this.title = title; }

    public Long getId() { return id; }
    public String getCode() { return code; }
    public String getTitle() { return title; }
    public Instant getCreatedAt() { return createdAt; }

    public void setId(Long id) { this.id = id; }
    public void setCode(String code) { this.code = code; }
    public void setTitle(String title) { this.title = title; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
