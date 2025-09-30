package com.example.lms.service;

import com.example.lms.entity.Course;
import com.example.lms.entity.Section;
import com.example.lms.entity.User;
import com.example.lms.repository.CourseRepository;
import com.example.lms.repository.SectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SectionService {

    private final SectionRepository sectionRepository;
    private final CourseRepository courseRepository;

    public Section createSection(UUID courseId, User currentUser, com.example.lms.controller.SectionController.CreateSectionRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + courseId));
        
        // Check if user is the teacher of this course
        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền tạo section cho khóa học này");
        }

        // Only allow creating sections if course is in DRAFT or REJECTED status
        if (course.getStatus() != Course.CourseStatus.DRAFT && course.getStatus() != Course.CourseStatus.REJECTED) {
            throw new RuntimeException("Chỉ có thể tạo section cho khóa học ở trạng thái bản nháp hoặc bị từ chối");
        }

        // Set order index if not provided
        int orderIndex = request.getOrderIndex() != null ? request.getOrderIndex() : 
                        sectionRepository.findMaxOrderIndexByCourse(course) + 1;

        Section section = Section.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .orderIndex(orderIndex)
                .course(course)
                .build();

        return sectionRepository.save(section);
    }

    public Section updateSection(UUID sectionId, User currentUser, com.example.lms.controller.SectionController.UpdateSectionRequest request) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy section với ID: " + sectionId));
        
        // Check if user is the teacher of this course
        if (!section.getCourse().getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa section này");
        }

        // Only allow editing if course is in DRAFT or REJECTED status
        Course.CourseStatus courseStatus = section.getCourse().getStatus();
        if (courseStatus != Course.CourseStatus.DRAFT && courseStatus != Course.CourseStatus.REJECTED) {
            throw new RuntimeException("Chỉ có thể chỉnh sửa section của khóa học ở trạng thái bản nháp hoặc bị từ chối");
        }

        if (request.getTitle() != null) {
            section.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            section.setDescription(request.getDescription());
        }

        if (request.getOrderIndex() != null) {
            section.setOrderIndex(request.getOrderIndex());
        }

        return sectionRepository.save(section);
    }

    public void deleteSection(UUID sectionId, User currentUser) {
        Section section = sectionRepository.findById(sectionId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy section với ID: " + sectionId));
        
        // Check if user is the teacher of this course
        if (!section.getCourse().getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa section này");
        }

        // Only allow deleting if course is in DRAFT or REJECTED status
        Course.CourseStatus courseStatus = section.getCourse().getStatus();
        if (courseStatus != Course.CourseStatus.DRAFT && courseStatus != Course.CourseStatus.REJECTED) {
            throw new RuntimeException("Chỉ có thể xóa section của khóa học ở trạng thái bản nháp hoặc bị từ chối");
        }

        sectionRepository.delete(section);
    }
}