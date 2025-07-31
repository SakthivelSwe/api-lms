package com.tvm.repository;

import com.tvm.model.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {

    List<StudentAnswer> findByQuizAttemptId(Long quizAttemptId);

    Optional<StudentAnswer> findByQuizAttemptIdAndQuestionId(Long quizAttemptId, Long questionId);

    @Query("SELECT COUNT(sa) FROM StudentAnswer sa WHERE sa.quizAttempt.id = :quizAttemptId AND sa.isCorrect = true")
    Long countCorrectAnswersByAttemptId(@Param("quizAttemptId") Long quizAttemptId);

    @Query("SELECT SUM(sa.pointsEarned) FROM StudentAnswer sa WHERE sa.quizAttempt.id = :quizAttemptId")
    Double getTotalPointsByAttemptId(@Param("quizAttemptId") Long quizAttemptId);
}

