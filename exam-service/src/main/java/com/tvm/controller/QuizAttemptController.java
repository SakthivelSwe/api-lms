package com.tvm.controller;

import com.tvm.dto.QuizAttemptDTO;
import com.tvm.dto.QuizResultDTO;
import com.tvm.security.TokenValidator;
import com.tvm.service.QuizAttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/quiz-attempts")
@CrossOrigin(origins = "*")
public class QuizAttemptController {

    @Autowired
    private QuizAttemptService quizAttemptService;

    @Autowired
    private TokenValidator tokenValidator;

    private static final Set<String> ALLOWED_ROLES = Set.of("ROLE_STUDENT");

    @PostMapping("/start")
    public ResponseEntity<QuizAttemptDTO> startQuiz(
            @RequestHeader("Authorization") String tokenHeader,
            @RequestBody StartQuizRequest request) {
        try {
            tokenValidator.validateWithRoles(tokenHeader, ALLOWED_ROLES);
            QuizAttemptDTO attempt = quizAttemptService.startQuiz(
                    request.getStudentId(), request.getQuizId(), request.getStudentName());
            return new ResponseEntity<>(attempt, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{attemptId}/answer")
    public ResponseEntity<QuizAttemptDTO> saveAnswer(@PathVariable Long attemptId, @RequestBody SaveAnswerRequest request) {
        try {
            QuizAttemptDTO attempt = quizAttemptService.saveAnswer(
                    attemptId, request.getQuestionId(), request.getAnswer());
            return new ResponseEntity<>(attempt, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{attemptId}/submit")
    public ResponseEntity<QuizResultDTO> submitQuiz(@PathVariable Long attemptId) {
        try {
            QuizResultDTO result = quizAttemptService.submitQuiz(attemptId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/submit-complete")
    public ResponseEntity<QuizAttemptDTO> submitQuizWithAnswers(@RequestBody SubmitQuizRequest request) {
        try {
            QuizAttemptDTO attempt = quizAttemptService.submitQuizWithAnswers(
                    request.getStudentId(), request.getQuizId(), request.getAnswers(), request.getStudentName());
            return new ResponseEntity<>(attempt, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{attemptId}/result")
    public ResponseEntity<QuizResultDTO> getQuizResult(@PathVariable Long attemptId) {
        try {
            QuizResultDTO result = quizAttemptService.getQuizResult(attemptId);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<QuizAttemptDTO>> getStudentAttempts(@PathVariable String studentId) {
        try {
            List<QuizAttemptDTO> attempts = quizAttemptService.getStudentAttempts(studentId);
            return new ResponseEntity<>(attempts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptDTO>> getQuizAttempts(@PathVariable Long quizId) {
        try {
            List<QuizAttemptDTO> attempts = quizAttemptService.getQuizAttempts(quizId);
            return new ResponseEntity<>(attempts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/student/{studentId}/quiz/{quizId}")
    public ResponseEntity<List<QuizAttemptDTO>> getStudentQuizAttempts(
            @PathVariable String studentId, @PathVariable Long quizId) {
        try {
            List<QuizAttemptDTO> attempts = quizAttemptService.getStudentQuizAttempts(studentId, quizId);
            return new ResponseEntity<>(attempts, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/{attemptId}/timeout")
    public ResponseEntity<Void> timeoutQuiz(@PathVariable Long attemptId) {
        try {
            quizAttemptService.timeoutQuiz(attemptId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Request classes
    public static class StartQuizRequest {
        private String studentId;
        private String studentName;
        private Long quizId;

        // Getters and setters
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public Long getQuizId() { return quizId; }
        public void setQuizId(Long quizId) { this.quizId = quizId; }
    }

    public static class SaveAnswerRequest {
        private Long questionId;
        private String answer;

        // Getters and setters
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }

        public String getAnswer() { return answer; }
        public void setAnswer(String answer) { this.answer = answer; }
    }

    public static class SubmitQuizRequest {
        private String studentId;
        private String studentName;
        private Long quizId;
        private Map<Long, String> answers; // questionId -> answer

        // Getters and setters
        public String getStudentId() { return studentId; }
        public void setStudentId(String studentId) { this.studentId = studentId; }

        public String getStudentName() { return studentName; }
        public void setStudentName(String studentName) { this.studentName = studentName; }

        public Long getQuizId() { return quizId; }
        public void setQuizId(Long quizId) { this.quizId = quizId; }

        public Map<Long, String> getAnswers() { return answers; }
        public void setAnswers(Map<Long, String> answers) { this.answers = answers; }
    }
}
