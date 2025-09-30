package com.example.lms.service;

import com.example.lms.entity.Course;
import com.example.lms.entity.Section;
import com.example.lms.entity.User;
import com.example.lms.repository.CourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class CourseService {

    private final CourseRepository courseRepository;

    public Page<Course> getApprovedCourses(Pageable pageable, String search, String teacher) {
        if (search != null && !search.trim().isEmpty()) {
            return courseRepository.findByStatusAndTitleContainingIgnoreCase(
                    Course.CourseStatus.APPROVED, search.trim(), pageable);
        }
        return courseRepository.findByStatus(Course.CourseStatus.APPROVED, pageable);
    }

    public Course createCourse(User teacher, com.example.lms.controller.CourseController.CreateCourseRequest request) {
        if (courseRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Mã khóa học đã tồn tại: " + request.getCode());
        }

        Course course = Course.builder()
                .code(request.getCode())
                .title(request.getTitle())
                .description(request.getDescription())
                .teacher(teacher)
                .status(Course.CourseStatus.DRAFT)
                .build();

        return courseRepository.save(course);
    }

    public Page<Course> getCoursesByTeacher(User teacher, Pageable pageable) {
        return courseRepository.findByTeacher(teacher, pageable);
    }

    public Page<Course> getEnrolledCourses(User student, Pageable pageable) {
        return courseRepository.findByEnrolledStudentsContaining(student, pageable);
    }

    public Course getCourseById(UUID courseId) {
        return courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy khóa học với ID: " + courseId));
    }

    public Course updateCourse(UUID courseId, User currentUser, com.example.lms.controller.CourseController.UpdateCourseRequest request) {
        Course course = getCourseById(courseId);
        
        // Check if user is the teacher of this course
        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền chỉnh sửa khóa học này");
        }

        // Only allow editing if course is in DRAFT or REJECTED status
        if (course.getStatus() != Course.CourseStatus.DRAFT && course.getStatus() != Course.CourseStatus.REJECTED) {
            throw new RuntimeException("Chỉ có thể chỉnh sửa khóa học ở trạng thái bản nháp hoặc bị từ chối");
        }

        if (request.getCode() != null && !request.getCode().equals(course.getCode())) {
            if (courseRepository.existsByCode(request.getCode())) {
                throw new RuntimeException("Mã khóa học đã tồn tại: " + request.getCode());
            }
            course.setCode(request.getCode());
        }

        if (request.getTitle() != null) {
            course.setTitle(request.getTitle());
        }

        if (request.getDescription() != null) {
            course.setDescription(request.getDescription());
        }

        return courseRepository.save(course);
    }

    public void submitForApproval(UUID courseId, User currentUser) {
        Course course = getCourseById(courseId);
        
        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền gửi khóa học này để duyệt");
        }

        if (course.getStatus() != Course.CourseStatus.DRAFT) {
            throw new RuntimeException("Chỉ có thể gửi khóa học ở trạng thái bản nháp để duyệt");
        }

        course.setStatus(Course.CourseStatus.PENDING);
        courseRepository.save(course);
    }

    public void deleteCourse(UUID courseId, User currentUser) {
        Course course = getCourseById(courseId);
        
        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xóa khóa học này");
        }

        if (course.getStatus() != Course.CourseStatus.DRAFT) {
            throw new RuntimeException("Chỉ có thể xóa khóa học ở trạng thái bản nháp");
        }

        courseRepository.delete(course);
    }

    public void enrollStudent(UUID courseId, User student) {
        Course course = getCourseById(courseId);
        
        if (course.getStatus() != Course.CourseStatus.APPROVED) {
            throw new RuntimeException("Chỉ có thể đăng ký vào khóa học đã được duyệt");
        }

        if (course.getEnrolledStudents().contains(student)) {
            throw new RuntimeException("Bạn đã đăng ký khóa học này rồi");
        }

        course.getEnrolledStudents().add(student);
        courseRepository.save(course);
    }

    public List<Section> getCourseContent(UUID courseId, User currentUser) {
        Course course = getCourseById(courseId);
        
        // Check if user is enrolled or is the teacher
        boolean hasAccess = course.getTeacher().getId().equals(currentUser.getId()) ||
                          course.getEnrolledStudents().contains(currentUser);
        
        if (!hasAccess) {
            throw new RuntimeException("Bạn không có quyền truy cập nội dung khóa học này");
        }

        return course.getSections().stream()
                .sorted((s1, s2) -> Integer.compare(s1.getOrderIndex(), s2.getOrderIndex()))
                .toList();
    }
}