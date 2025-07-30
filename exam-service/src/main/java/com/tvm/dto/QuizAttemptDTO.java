package com.tvm.dto;

import com.tvm.model.QuizAttempt;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class QuizAttemptDTO {

    private Long id;

    @NotBlank(message = "Student ID is required")
    private String studentId;

    private String studentName;
    private Integer attemptNumber;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Double totalScore;
    private Integer maxScore;
    private Double percentage;
    private QuizAttempt.AttemptStatus status;
    private Integer timeTakenMinutes;
    private Boolean isPassed;
    private String feedback;

    @NotNull(message = "Quiz ID is required")
    private Long quizId;

    private String quizTitle;
    private List<StudentAnswerDTO> studentAnswers;
    private Map<Long, String> answers; // questionId -> answer

    // Constructors
    public QuizAttemptDTO() {}

    public QuizAttemptDTO(String studentId, Long quizId) {
        this.studentId = studentId;
        this.quizId = quizId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Integer getAttemptNumber() { return attemptNumber; }
    public void setAttemptNumber(Integer attemptNumber) { this.attemptNumber = attemptNumber; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public QuizAttempt.AttemptStatus getStatus() { return status; }
    public void setStatus(QuizAttempt.AttemptStatus status) { this.status = status; }

    public Integer getTimeTakenMinutes() { return timeTakenMinutes; }
    public void setTimeTakenMinutes(Integer timeTakenMinutes) { this.timeTakenMinutes = timeTakenMinutes; }

    public Boolean getIsPassed() { return isPassed; }
    public void setIsPassed(Boolean isPassed) { this.isPassed = isPassed; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public List<StudentAnswerDTO> getStudentAnswers() { return studentAnswers; }
    public void setStudentAnswers(List<StudentAnswerDTO> studentAnswers) { this.studentAnswers = studentAnswers; }

    public Map<Long, String> getAnswers() { return answers; }
    public void setAnswers(Map<Long, String> answers) { this.answers = answers; }

    // Helper class for student answers
    public static class StudentAnswerDTO {
        private Long questionId;
        private String questionText;
        private String studentAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private Double pointsEarned;
        private Integer totalPoints;
        private String explanation;

        // Constructors
        public StudentAnswerDTO() {}

        public StudentAnswerDTO(Long questionId, String studentAnswer, Boolean isCorrect, Double pointsEarned) {
            this.questionId = questionId;
            this.studentAnswer = studentAnswer;
            this.isCorrect = isCorrect;
            this.pointsEarned = pointsEarned;
        }

        // Getters and Setters
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }

        public String getQuestionText() { return questionText; }
        public void setQuestionText(String questionText) { this.questionText = questionText; }

        public String getStudentAnswer() { return studentAnswer; }
        public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }

        public String getCorrectAnswer() { return correctAnswer; }
        public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

        public Boolean getIsCorrect() { return isCorrect; }
        public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

        public Double getPointsEarned() { return pointsEarned; }
        public void setPointsEarned(Double pointsEarned) { this.pointsEarned = pointsEarned; }

        public Integer getTotalPoints() { return totalPoints; }
        public void setTotalPoints(Integer totalPoints) { this.totalPoints = totalPoints; }

        public String getExplanation() { return explanation; }
        public void setExplanation(String explanation) { this.explanation = explanation; }
    }
}
