package com.example.lms.service;

import com.example.lms.entity.Course;
import com.example.lms.entity.Lesson;
import com.example.lms.entity.LessonAssignment;
import com.example.lms.entity.User;
import com.example.lms.repository.LessonAssignmentRepository;
import com.example.lms.repository.LessonRepository;
import com.example.lms.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class LessonAssignmentService {

    private final LessonAssignmentRepository lessonAssignmentRepository;
    private final LessonRepository lessonRepository;
    private final UserRepository userRepository;

    public LessonAssignment assignLessonToStudent(UUID lessonId, UUID studentId, User currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học với ID: " + lessonId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy học viên với ID: " + studentId));

        // Permission: only the teacher who owns the course can assign
        Course course = lesson.getSection().getCourse();
        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền phân phối bài học này");
        }

        // Ensure the student is enrolled in the course; update owning side (User.enrolledCourses)
        if (!course.getEnrolledStudents().contains(student)) {
            student.getEnrolledCourses().add(course);
            userRepository.save(student);
        }

        // Create if not exists
        LessonAssignment la = lessonAssignmentRepository
                .findByLessonIdAndStudentId(lessonId, studentId)
                .orElseGet(() -> LessonAssignment.builder()
                        .lesson(lesson)
                        .student(student)
                        .build());
        return lessonAssignmentRepository.save(la);
    }

    public void unassignLessonFromStudent(UUID lessonId, UUID studentId, User currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học với ID: " + lessonId));
        Course course = lesson.getSection().getCourse();
        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền hủy phân phối bài học này");
        }
        lessonAssignmentRepository.deleteByLesson_IdAndStudent_Id(lessonId, studentId);
    }

    public List<LessonAssignment> listAssignmentsByLesson(UUID lessonId, User currentUser) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài học với ID: " + lessonId));
        Course course = lesson.getSection().getCourse();
        if (!course.getTeacher().getId().equals(currentUser.getId())) {
            throw new RuntimeException("Bạn không có quyền xem danh sách phân phối của bài học này");
        }
        return lessonAssignmentRepository.findByLessonId(lessonId);
    }
}
