package com.tvm.repository;

import com.tvm.model.QuizAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuizAttemptRepository extends JpaRepository<QuizAttempt, Long> {

    List<QuizAttempt> findByStudentIdOrderByStartTimeDesc(String studentId);

    List<QuizAttempt> findByQuizIdOrderByStartTimeDesc(Long quizId);

    List<QuizAttempt> findByStudentIdAndQuizIdOrderByAttemptNumberDesc(String studentId, Long quizId);

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.studentId = :studentId AND qa.quiz.id = :quizId")
    Long countByStudentIdAndQuizId(@Param("studentId") String studentId, @Param("quizId") Long quizId);

    @Query("SELECT qa FROM QuizAttempt qa WHERE qa.studentId = :studentId AND qa.quiz.id = :quizId AND qa.status = 'IN_PROGRESS'")
    Optional<QuizAttempt> findInProgressAttempt(@Param("studentId") String studentId, @Param("quizId") Long quizId);

    @Query("SELECT AVG(qa.percentage) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId AND qa.status = 'COMPLETED'")
    Double getAverageScoreByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT COUNT(qa) FROM QuizAttempt qa WHERE qa.quiz.id = :quizId AND qa.isPassed = true")
    Long countPassedAttemptsByQuizId(@Param("quizId") Long quizId);
}