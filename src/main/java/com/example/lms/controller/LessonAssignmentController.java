package com.example.lms.controller;

import com.example.lms.dto.ApiResponse;
import com.example.lms.entity.LessonAssignment;
import com.example.lms.entity.User;
import com.example.lms.service.LessonAssignmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/lesson-assignments")
@RequiredArgsConstructor
@Tag(name = "Lesson Assignments", description = "Phân phối bài học đến học viên cụ thể")
@SecurityRequirement(name = "Bearer Authentication")
public class LessonAssignmentController {

    private final LessonAssignmentService service;

    @PostMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(summary = "Phân phối bài học cho học viên")
    public ResponseEntity<ApiResponse<Item>> assign(
            @AuthenticationPrincipal User currentUser,
            @RequestParam @NotNull UUID lessonId,
            @RequestParam @NotNull UUID studentId
    ) {
        try {
            LessonAssignment la = service.assignLessonToStudent(lessonId, studentId, currentUser);
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(toItem(la)));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Có lỗi xảy ra";
            if (msg.contains("Không tìm thấy")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(msg));
            }
            if (msg.toLowerCase().contains("quyền")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(msg));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(msg));
        }
    }

    @DeleteMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(summary = "Hủy phân phối bài học cho học viên")
    public ResponseEntity<ApiResponse<String>> unassign(
            @AuthenticationPrincipal User currentUser,
            @RequestParam @NotNull UUID lessonId,
            @RequestParam @NotNull UUID studentId
    ) {
        try {
            service.unassignLessonFromStudent(lessonId, studentId, currentUser);
            return ResponseEntity.ok(ApiResponse.success("OK"));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Có lỗi xảy ra";
            if (msg.contains("Không tìm thấy")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(msg));
            }
            if (msg.toLowerCase().contains("quyền")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(msg));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(msg));
        }
    }

    @GetMapping
    @PreAuthorize("hasRole('TEACHER') or hasRole('ADMIN')")
    @Operation(summary = "Danh sách học viên được phân phối của một bài học")
    public ResponseEntity<ApiResponse<List<Item>>> listByLesson(
            @AuthenticationPrincipal User currentUser,
            @RequestParam @NotNull UUID lessonId
    ) {
        try {
            List<LessonAssignment> list = service.listAssignmentsByLesson(lessonId, currentUser);
            return ResponseEntity.ok(ApiResponse.success(list.stream().map(this::toItem).toList()));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Có lỗi xảy ra";
            if (msg.contains("Không tìm thấy")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(msg));
            }
            if (msg.toLowerCase().contains("quyền")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(msg));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(msg));
        }
    }

    private Item toItem(LessonAssignment la) {
        return Item.builder()
                .id(la.getId())
                .lessonId(la.getLesson().getId())
                .studentId(la.getStudent().getId())
                .studentName(la.getStudent().getFullName())
                .createdAt(la.getCreatedAt())
                .build();
    }

    @lombok.Builder
    @lombok.Data
    public static class Item {
        private UUID id;
        private UUID lessonId;
        private UUID studentId;
        private String studentName;
        private java.time.Instant createdAt;
    }
}
