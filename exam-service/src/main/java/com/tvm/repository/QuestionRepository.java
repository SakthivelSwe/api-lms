package com.tvm.repository;

import com.tvm.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizIdOrderByQuestionOrder(Long quizId);

    List<Question> findByQuizId(Long quizId);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.quiz.id = :quizId")
    Long countByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT SUM(q.points) FROM Question q WHERE q.quiz.id = :quizId")
    Integer getTotalPointsByQuizId(@Param("quizId") Long quizId);

    List<Question> findByQuestionTypeAndQuizId(Question.QuestionType questionType, Long quizId);

    List<Question> findByDifficultyLevelAndQuizId(Question.DifficultyLevel difficultyLevel, Long quizId);
}
