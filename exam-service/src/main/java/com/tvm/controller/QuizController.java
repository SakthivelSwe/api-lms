package com.tvm.controller;

import com.tvm.dto.QuizDTO;
import com.tvm.security.TokenValidator;
import com.tvm.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/quizzes")
@CrossOrigin(origins = "*")
public class QuizController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private TokenValidator tokenValidator;

    // Allowed role sets
    private static final Set<String> ROLE_ADMIN = Set.of("ROLE_ADMIN");
    private static final Set<String> ROLE_INSTRUCTOR_ADMIN = Set.of("ROLE_INSTRUCTOR", "ROLE_ADMIN");
    private static final Set<String> ROLE_ALL = Set.of("ROLE_INSTRUCTOR", "ROLE_ADMIN", "ROLE_STUDENT");

    @PostMapping
    public ResponseEntity<QuizDTO> createQuiz(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody QuizDTO quizDTO) {
        tokenValidator.validateWithRoles(token, ROLE_INSTRUCTOR_ADMIN);
        QuizDTO createdQuiz = quizService.createQuiz(quizDTO);
        return new ResponseEntity<>(createdQuiz, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuizDTO> updateQuiz(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody QuizDTO quizDTO) {
        tokenValidator.validateWithRoles(token, ROLE_INSTRUCTOR_ADMIN);
        QuizDTO updatedQuiz = quizService.updateQuiz(id, quizDTO);
        return new ResponseEntity<>(updatedQuiz, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuiz(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        tokenValidator.validateWithRoles(token, ROLE_ADMIN);
        quizService.deleteQuiz(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuizDTO> getQuizById(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id) {
        tokenValidator.validateWithRoles(token, ROLE_ALL);
        QuizDTO quiz = quizService.getQuizById(id);
        return new ResponseEntity<>(quiz, HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<QuizDTO>> getAllQuizzes(
            @RequestHeader("Authorization") String token) {
        tokenValidator.validateWithRoles(token, ROLE_INSTRUCTOR_ADMIN);
        List<QuizDTO> quizzes = quizService.getAllQuizzes();
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    @GetMapping("/active")
    public ResponseEntity<List<QuizDTO>> getActiveQuizzes(
            @RequestHeader("Authorization") String token) {
        tokenValidator.validateWithRoles(token, ROLE_ALL);
        List<QuizDTO> quizzes = quizService.getActiveQuizzes();
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourse(
            @RequestHeader("Authorization") String token,
            @PathVariable Long courseId) {
        tokenValidator.validateWithRoles(token, ROLE_INSTRUCTOR_ADMIN);
        List<QuizDTO> quizzes = quizService.getQuizzesByCourse(courseId);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    @GetMapping("/module/{moduleId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByModule(
            @RequestHeader("Authorization") String token,
            @PathVariable Long moduleId) {
        tokenValidator.validateWithRoles(token, ROLE_INSTRUCTOR_ADMIN);
        List<QuizDTO> quizzes = quizService.getQuizzesByModule(moduleId);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    @GetMapping("/course/{courseId}/module/{moduleId}")
    public ResponseEntity<List<QuizDTO>> getQuizzesByCourseAndModule(
            @RequestHeader("Authorization") String token,
            @PathVariable Long courseId,
            @PathVariable Long moduleId) {
        tokenValidator.validateWithRoles(token, ROLE_INSTRUCTOR_ADMIN);
        List<QuizDTO> quizzes = quizService.getQuizzesByCourseAndModule(courseId, moduleId);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<QuizDTO>> searchQuizzes(
            @RequestHeader("Authorization") String token,
            @RequestParam String title) {
        tokenValidator.validateWithRoles(token, ROLE_INSTRUCTOR_ADMIN);
        List<QuizDTO> quizzes = quizService.searchQuizzes(title);
        return new ResponseEntity<>(quizzes, HttpStatus.OK);
    }

    @PutMapping("/{quizId}/assign/module/{moduleId}")
    public ResponseEntity<Void> assignQuizToModule(
            @RequestHeader("Authorization") String token,
            @PathVariable Long quizId,
            @PathVariable Long moduleId) {
        tokenValidator.validateWithRoles(token, ROLE_INSTRUCTOR_ADMIN);
        quizService.assignQuizToModule(quizId, moduleId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{quizId}/assign/course/{courseId}")
    public ResponseEntity<Void> assignQuizToCourse(
            @RequestHeader("Authorization") String token,
            @PathVariable Long quizId,
            @PathVariable Long courseId) {
        tokenValidator.validateWithRoles(token, ROLE_INSTRUCTOR_ADMIN);
        quizService.assignQuizToCourse(quizId, courseId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}

