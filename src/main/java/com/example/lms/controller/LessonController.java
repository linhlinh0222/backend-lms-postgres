package com.example.lms.controller;

import com.example.lms.dto.ApiResponse;
import com.example.lms.entity.Lesson;
import com.example.lms.entity.LessonAttachment;
import com.example.lms.entity.User;
import com.example.lms.service.LessonService;
import java.util.List;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/courses/sections")
@RequiredArgsConstructor
@Tag(name = "Lesson Management", description = "API quản lý bài học trong sections")
@SecurityRequirement(name = "Bearer Authentication")
public class LessonController {

    private final LessonService lessonService;

    @PostMapping("/{sectionId}/lessons")
    @Operation(summary = "Tạo bài học mới", description = "Giảng viên tạo bài học mới trong section")
    public ResponseEntity<ApiResponse<LessonDetail>> createLesson(
            @PathVariable UUID sectionId,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody CreateLessonRequest request
    ) {
        try {
            Lesson lesson = lessonService.createLesson(sectionId, currentUser, request);
            LessonDetail lessonDetail = convertToLessonDetail(lesson);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(lessonDetail));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Có lỗi xảy ra";
            if (msg.toLowerCase().contains("quyền")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(msg));
            }
            if (msg.contains("Không tìm thấy")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(msg));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(msg));
        }
    }

    @PutMapping("/lessons/{lessonId}")
    @Operation(summary = "Cập nhật bài học", description = "Giảng viên cập nhật bài học trong khóa học của mình")
    public ResponseEntity<ApiResponse<LessonDetail>> updateLesson(
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal User currentUser,
            @Valid @RequestBody UpdateLessonRequest request
    ) {
        try {
            Lesson lesson = lessonService.updateLesson(lessonId, currentUser, request);
            LessonDetail lessonDetail = convertToLessonDetail(lesson);
            
            return ResponseEntity.ok(ApiResponse.success(lessonDetail));
        } catch (RuntimeException e) {
            String msg = e.getMessage() != null ? e.getMessage() : "Có lỗi xảy ra";
            if (msg.toLowerCase().contains("quyền")) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ApiResponse.error(msg));
            }
            if (msg.contains("Không tìm thấy")) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.error(msg));
            }
            return ResponseEntity.badRequest().body(ApiResponse.error(msg));
        }
    }

    @DeleteMapping("/lessons/{lessonId}")
    @Operation(summary = "Xóa bài học", description = "Giảng viên xóa bài học trong khóa học của mình")
    public ResponseEntity<ApiResponse<String>> deleteLesson(
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            lessonService.deleteLesson(lessonId, currentUser);
            return ResponseEntity.ok(ApiResponse.success("Bài học đã được xóa"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    @GetMapping("/lessons/{lessonId}")
    @Operation(summary = "Lấy chi tiết bài học", description = "Lấy thông tin chi tiết của một bài học")
    public ResponseEntity<ApiResponse<LessonDetail>> getLessonById(
            @PathVariable UUID lessonId,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            Lesson lesson = lessonService.getLessonById(lessonId, currentUser);
            LessonDetail lessonDetail = convertToLessonDetail(lesson);
            
            return ResponseEntity.ok(ApiResponse.success(lessonDetail));
    } catch (RuntimeException e) {
        String msg = e.getMessage() != null ? e.getMessage() : "Không tìm thấy bài học";
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(ApiResponse.error(msg));
        }
    }

    // Helper method
    private LessonDetail convertToLessonDetail(Lesson lesson) {
        return LessonDetail.builder()
                .id(lesson.getId())
                .title(lesson.getTitle())
                .description(lesson.getDescription())
                .content(lesson.getContent())
                .videoUrl(lesson.getVideoUrl())
                .durationMinutes(lesson.getDurationMinutes())
                .orderIndex(lesson.getOrderIndex())
                .attachments(lesson.getAttachments() != null ?
                    lesson.getAttachments().stream()
                        .map(this::convertToAttachmentDetail)
                        .toList() : java.util.Collections.emptyList())
                .sectionId(lesson.getSection().getId())
                .sectionTitle(lesson.getSection().getTitle())
                .courseId(lesson.getSection().getCourse().getId())
                .courseTitle(lesson.getSection().getCourse().getTitle())
                .createdAt(lesson.getCreatedAt())
                .updatedAt(lesson.getUpdatedAt())
                .build();
    }

    private AttachmentDetail convertToAttachmentDetail(LessonAttachment attachment) {
        return AttachmentDetail.builder()
                .id(attachment.getId())
                .fileName(attachment.getFileName())
                .originalFileName(attachment.getOriginalFileName())
                .fileUrl(attachment.getFileUrl())
                .fileSize(attachment.getFileSize())
                .contentType(attachment.getContentType())
                .fileType(attachment.getFileType())
                .displayOrder(attachment.getDisplayOrder())
                .uploadedAt(attachment.getUploadedAt())
                .build();
    }

    // DTOs
    public static class LessonDetail {
        private UUID id;
        private String title;
        private String description;
        private String content;
        private String videoUrl;
        private Integer durationMinutes;
        private Integer orderIndex;
        private List<AttachmentDetail> attachments;
        private UUID sectionId;
        private String sectionTitle;
        private UUID courseId;
        private String courseTitle;
        private Instant createdAt;
        private Instant updatedAt;

        public static LessonDetailBuilder builder() {
            return new LessonDetailBuilder();
        }

        public static class LessonDetailBuilder {
            private UUID id;
            private String title;
            private String description;
            private String content;
            private String videoUrl;
            private Integer durationMinutes;
            private Integer orderIndex;
            private List<AttachmentDetail> attachments;
            private UUID sectionId;
            private String sectionTitle;
            private UUID courseId;
            private String courseTitle;
            private Instant createdAt;
            private Instant updatedAt;

            public LessonDetailBuilder id(UUID id) { this.id = id; return this; }
            public LessonDetailBuilder title(String title) { this.title = title; return this; }
            public LessonDetailBuilder description(String description) { this.description = description; return this; }
            public LessonDetailBuilder content(String content) { this.content = content; return this; }
            public LessonDetailBuilder videoUrl(String videoUrl) { this.videoUrl = videoUrl; return this; }
            public LessonDetailBuilder durationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; return this; }
            public LessonDetailBuilder orderIndex(Integer orderIndex) { this.orderIndex = orderIndex; return this; }
            public LessonDetailBuilder attachments(List<AttachmentDetail> attachments) { this.attachments = attachments; return this; }
            public LessonDetailBuilder sectionId(UUID sectionId) { this.sectionId = sectionId; return this; }
            public LessonDetailBuilder sectionTitle(String sectionTitle) { this.sectionTitle = sectionTitle; return this; }
            public LessonDetailBuilder courseId(UUID courseId) { this.courseId = courseId; return this; }
            public LessonDetailBuilder courseTitle(String courseTitle) { this.courseTitle = courseTitle; return this; }
            public LessonDetailBuilder createdAt(Instant createdAt) { this.createdAt = createdAt; return this; }
            public LessonDetailBuilder updatedAt(Instant updatedAt) { this.updatedAt = updatedAt; return this; }

            public LessonDetail build() {
                LessonDetail lesson = new LessonDetail();
                lesson.id = this.id;
                lesson.title = this.title;
                lesson.description = this.description;
                lesson.content = this.content;
                lesson.videoUrl = this.videoUrl;
                lesson.durationMinutes = this.durationMinutes;
                lesson.orderIndex = this.orderIndex;
                lesson.attachments = this.attachments;
                lesson.sectionId = this.sectionId;
                lesson.sectionTitle = this.sectionTitle;
                lesson.courseId = this.courseId;
                lesson.courseTitle = this.courseTitle;
                lesson.createdAt = this.createdAt;
                lesson.updatedAt = this.updatedAt;
                return lesson;
            }
        }

        // Getters
        public UUID getId() { return id; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getContent() { return content; }
        public String getVideoUrl() { return videoUrl; }
        public Integer getDurationMinutes() { return durationMinutes; }
        public Integer getOrderIndex() { return orderIndex; }
        public UUID getSectionId() { return sectionId; }
        public String getSectionTitle() { return sectionTitle; }
        public UUID getCourseId() { return courseId; }
        public String getCourseTitle() { return courseTitle; }
        public Instant getCreatedAt() { return createdAt; }
        public Instant getUpdatedAt() { return updatedAt; }
        public List<AttachmentDetail> getAttachments() { return attachments; }
    }

    public static class AttachmentDetail {
        private UUID id;
        private String fileName;
        private String originalFileName;
        private String fileUrl;
        private Long fileSize;
        private String contentType;
        private String fileType;
        private Integer displayOrder;
        private Instant uploadedAt;

        public static AttachmentDetailBuilder builder() {
            return new AttachmentDetailBuilder();
        }

        public static class AttachmentDetailBuilder {
            private UUID id;
            private String fileName;
            private String originalFileName;
            private String fileUrl;
            private Long fileSize;
            private String contentType;
            private String fileType;
            private Integer displayOrder;
            private Instant uploadedAt;

            public AttachmentDetailBuilder id(UUID id) { this.id = id; return this; }
            public AttachmentDetailBuilder fileName(String fileName) { this.fileName = fileName; return this; }
            public AttachmentDetailBuilder originalFileName(String originalFileName) { this.originalFileName = originalFileName; return this; }
            public AttachmentDetailBuilder fileUrl(String fileUrl) { this.fileUrl = fileUrl; return this; }
            public AttachmentDetailBuilder fileSize(Long fileSize) { this.fileSize = fileSize; return this; }
            public AttachmentDetailBuilder contentType(String contentType) { this.contentType = contentType; return this; }
            public AttachmentDetailBuilder fileType(String fileType) { this.fileType = fileType; return this; }
            public AttachmentDetailBuilder displayOrder(Integer displayOrder) { this.displayOrder = displayOrder; return this; }
            public AttachmentDetailBuilder uploadedAt(Instant uploadedAt) { this.uploadedAt = uploadedAt; return this; }

            public AttachmentDetail build() {
                AttachmentDetail attachment = new AttachmentDetail();
                attachment.id = this.id;
                attachment.fileName = this.fileName;
                attachment.originalFileName = this.originalFileName;
                attachment.fileUrl = this.fileUrl;
                attachment.fileSize = this.fileSize;
                attachment.contentType = this.contentType;
                attachment.fileType = this.fileType;
                attachment.displayOrder = this.displayOrder;
                attachment.uploadedAt = this.uploadedAt;
                return attachment;
            }
        }

        // Getters
        public UUID getId() { return id; }
        public String getFileName() { return fileName; }
        public String getOriginalFileName() { return originalFileName; }
        public String getFileUrl() { return fileUrl; }
        public Long getFileSize() { return fileSize; }
        public String getContentType() { return contentType; }
        public String getFileType() { return fileType; }
        public Integer getDisplayOrder() { return displayOrder; }
        public Instant getUploadedAt() { return uploadedAt; }
    }

    public static class CreateLessonRequest {
        @NotBlank(message = "Tiêu đề bài học không được để trống")
        @Size(max = 255, message = "Tiêu đề bài học không được vượt quá 255 ký tự")
        private String title;

        private String description;

        private String content;

        private String videoUrl;

        private Integer durationMinutes;

        private Integer orderIndex;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
        public Integer getOrderIndex() { return orderIndex; }
        public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    }

    public static class UpdateLessonRequest {
        @Size(max = 255, message = "Tiêu đề bài học không được vượt quá 255 ký tự")
        private String title;

        private String description;

        private String content;

        private String videoUrl;

        private Integer durationMinutes;

        private Integer orderIndex;

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getVideoUrl() { return videoUrl; }
        public void setVideoUrl(String videoUrl) { this.videoUrl = videoUrl; }
        public Integer getDurationMinutes() { return durationMinutes; }
        public void setDurationMinutes(Integer durationMinutes) { this.durationMinutes = durationMinutes; }
        public Integer getOrderIndex() { return orderIndex; }
        public void setOrderIndex(Integer orderIndex) { this.orderIndex = orderIndex; }
    }
}