package com.example.lms.repository;

import com.example.lms.entity.LessonAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface LessonAssignmentRepository extends JpaRepository<LessonAssignment, UUID> {

    @Query("SELECT la FROM LessonAssignment la WHERE la.lesson.id = :lessonId")
    List<LessonAssignment> findByLessonId(@Param("lessonId") UUID lessonId);

    @Query("SELECT la FROM LessonAssignment la WHERE la.student.id = :studentId")
    List<LessonAssignment> findByStudentId(@Param("studentId") UUID studentId);

    @Query("SELECT la FROM LessonAssignment la WHERE la.lesson.id = :lessonId AND la.student.id = :studentId")
    Optional<LessonAssignment> findByLessonIdAndStudentId(@Param("lessonId") UUID lessonId, @Param("studentId") UUID studentId);

    long deleteByLesson_IdAndStudent_Id(UUID lessonId, UUID studentId);
}
