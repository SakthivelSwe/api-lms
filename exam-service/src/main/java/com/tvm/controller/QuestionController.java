package com.tvm.controller;

import com.tvm.dto.QuestionDTO;
import com.tvm.model.Question;
import com.tvm.security.TokenValidator;
import com.tvm.service.QuestionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/questions")
@CrossOrigin(origins = "*")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private TokenValidator tokenValidator;

    private static final Set<String> ALLOWED_ROLES = Set.of("ROLE_ADMIN", "ROLE_INSTRUCTOR");

    @PostMapping
    public ResponseEntity<?> createQuestion(
            @RequestHeader("Authorization") String tokenHeader,
            @Valid @RequestBody QuestionDTO questionDTO) {
        try {
            tokenValidator.validateWithRoles(tokenHeader, Set.of("ROLE_ADMIN", "ROLE_INSTRUCTOR"));
            QuestionDTO createdQuestion = questionService.createQuestion(questionDTO);
            return new ResponseEntity<>(createdQuestion, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", e.getMessage()));
        }
    }


    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long id,
            @Valid @RequestBody QuestionDTO questionDTO) {
        try {
            tokenValidator.validateWithRoles(tokenHeader, ALLOWED_ROLES);
            QuestionDTO updatedQuestion = questionService.updateQuestion(id, questionDTO);
            return new ResponseEntity<>(updatedQuestion, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long id) {
        try {
            tokenValidator.validateWithRoles(tokenHeader, ALLOWED_ROLES);
            questionService.deleteQuestion(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @PutMapping("/quiz/{quizId}/reorder")
    public ResponseEntity<Void> reorderQuestions(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long quizId,
            @RequestBody List<Long> questionIds) {
        try {
            tokenValidator.validateWithRoles(tokenHeader, ALLOWED_ROLES);
            questionService.reorderQuestions(quizId, questionIds);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long id) {
        try {
            tokenValidator.validateWithRoles(tokenHeader, Set.of("ROLE_ADMIN", "ROLE_INSTRUCTOR"));
            QuestionDTO question = questionService.getQuestionById(id);
            return new ResponseEntity<>(question, HttpStatus.OK);
        } catch (RuntimeException e) {
            e.printStackTrace();  // Log the real issue
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }


    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByQuiz(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long quizId) {
        try {
            tokenValidator.validateWithRoles(tokenHeader, ALLOWED_ROLES);
            List<QuestionDTO> questions = questionService.getQuestionsByQuiz(quizId);
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/quiz/{quizId}/type/{questionType}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByType(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long quizId,
            @PathVariable Question.QuestionType questionType) {
        try {
            tokenValidator.validateWithRoles(tokenHeader, ALLOWED_ROLES);
            List<QuestionDTO> questions = questionService.getQuestionsByType(quizId, questionType);
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/quiz/{quizId}/difficulty/{difficultyLevel}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByDifficulty(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long quizId,
            @PathVariable Question.DifficultyLevel difficultyLevel) {
        try {
            tokenValidator.validateWithRoles(tokenHeader, ALLOWED_ROLES);
            List<QuestionDTO> questions = questionService.getQuestionsByDifficulty(quizId, difficultyLevel);
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }

    @GetMapping("/quiz/{quizId}/student")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByQuizForStudent(
            @RequestHeader("Authorization") String tokenHeader,
            @PathVariable Long quizId) {
        try {
            // This one allows only STUDENT
            tokenValidator.validateWithRoles(tokenHeader, Set.of("STUDENT"));
            List<QuestionDTO> questions = questionService.getQuestionsByQuizForStudent(quizId);
            return new ResponseEntity<>(questions, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        }
    }
}
