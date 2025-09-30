package com.example.lms.service;

import com.example.lms.entity.Course;
import com.example.lms.entity.Lesson;
import com.example.lms.entity.Section;
import com.example.lms.entity.User;
import com.example.lms.repository.LessonRepository;
import com.example.lms.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonService {

    private final LessonRepository lessonRepository;
    private final SectionRepository sectionRepository;

    public Lesson createLesson(UUID sectionId, User currentUser, com.example.lms.controller.LessonController.CreateLessonRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy section với ID: " + sectionId));
        
        // Check if user is the teacher of this course
        if (!section.getCourse().getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền tạo bài học cho section này");
        }

        // Only allow creating lessons if course is in DRAFT or REJECTED status
        Course.CourseStatus courseStatus = section.getCourse().getStatus();
        if (courseStatus != Course.CourseStatus.DRAFT && courseStatus != Course.CourseStatus.REJECTED) {
            throw new RuntimeException("Chỉ có thể tạo bài học cho khóa học ở trạng thái bản nháp hoặc bị từ chối");
        }

        // Set order index if not provided
        int orderIndex = request.getOrderIndex() != null ? request.getOrderIndex() : 
                        lessonRepository.findMaxOrderIndexBySection(section) + 1;

        Lesson lesson = Lesson.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .orderIndex(orderIndex)
                .section(section)
                .build();

        return lessonRepository.save(lesson);
    }

    public Lesson updateLesson(UUID lessonId, User currentUser, com.example.lms.controller.LessonController.UpdateLessonRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học với ID: " + lessonId));
        
        // Check if user is the teacher of this course
        if (!lesson.getSection().getCourse().getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa bài học này");
        }

        // Only allow editing if course is in DRAFT or REJECTED status
        Course.CourseStatus courseStatus = lesson.getSection().getCourse().getStatus();
        if (courseStatus != Course.CourseStatus.DRAFT && courseStatus != Course.CourseStatus.REJECTED) {
            throw new RuntimeException("Chỉ có thể chỉnh sửa bài học của khóa học ở trạng thái bản nháp hoặc bị từ chối");
        }

        if (request.getTitle() != null) {
            lesson.setTitle(request.getTitle());
        }

        if (request.getContent() != null) {
            lesson.setContent(request.getContent());
        }

        if (request.getOrderIndex() != null) {
            lesson.setOrderIndex(request.getOrderIndex());
        }

        return lessonRepository.save(lesson);
    }

    public void deleteLesson(UUID lessonId, User currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học với ID: " + lessonId));
        
        // Check if user is the teacher of this course
        if (!lesson.getSection().getCourse().getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa bài học này");
        }

        // Only allow deleting if course is in DRAFT or REJECTED status
        Course.CourseStatus courseStatus = lesson.getSection().getCourse().getStatus();
        if (courseStatus != Course.CourseStatus.DRAFT && courseStatus != Course.CourseStatus.REJECTED) {
            throw new RuntimeException("Chỉ có thể xóa bài học của khóa học ở trạng thái bản nháp hoặc bị từ chối");
        }

        lessonRepository.delete(lesson);
    }

    public Lesson getLessonById(UUID lessonId, User currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học với ID: " + lessonId));
        
        // Check if user has access (is teacher or enrolled student)
        Course course = lesson.getSection().getCourse();
        boolean hasAccess = course.getTeacher().getId().equals(currentUser.getId()) ||
                          course.getEnrolledStudents().contains(currentUser);
        
        if (!hasAccess) {
            throw new RuntimeException("Bạn không có quyền truy cập bài học này");
        }

        return lesson;
    }
}