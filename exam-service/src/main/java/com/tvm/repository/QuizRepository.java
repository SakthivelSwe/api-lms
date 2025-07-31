package com.tvm.repository;

import com.tvm.model.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByIsActiveTrue();

    List<Quiz> findByCourseId(Long courseId);

    List<Quiz> findByModuleId(Long moduleId);

    List<Quiz> findByCourseIdAndModuleId(Long courseId, Long moduleId);

    List<Quiz> findByCreatedBy(String createdBy);

    @Query("SELECT q FROM Quiz q WHERE q.title LIKE %:title% AND q.isActive = true")
    List<Quiz> findByTitleContainingAndIsActiveTrue(@Param("title") String title);

    @Query("SELECT COUNT(q) FROM Quiz q WHERE q.courseId = :courseId")
    Long countByCourseId(@Param("courseId") Long courseId);
}