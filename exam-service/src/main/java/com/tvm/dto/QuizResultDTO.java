package com.tvm.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QuizResultDTO {

    private Long attemptId;
    private String studentId;
    private String studentName;
    private String quizTitle;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer timeTakenMinutes;
    private Double totalScore;
    private Integer maxScore;
    private Double percentage;
    private Boolean isPassed;
    private String feedback;
    private Integer correctAnswers;
    private Integer totalQuestions;
    private List<QuestionResultDTO> questionResults;

    // Constructors
    public QuizResultDTO() {}

    // Getters and Setters
    public Long getAttemptId() { return attemptId; }
    public void setAttemptId(Long attemptId) { this.attemptId = attemptId; }

    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getQuizTitle() { return quizTitle; }
    public void setQuizTitle(String quizTitle) { this.quizTitle = quizTitle; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getTimeTakenMinutes() { return timeTakenMinutes; }
    public void setTimeTakenMinutes(Integer timeTakenMinutes) { this.timeTakenMinutes = timeTakenMinutes; }

    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }

    public Integer getMaxScore() { return maxScore; }
    public void setMaxScore(Integer maxScore) { this.maxScore = maxScore; }

    public Double getPercentage() { return percentage; }
    public void setPercentage(Double percentage) { this.percentage = percentage; }

    public Boolean getIsPassed() { return isPassed; }
    public void setIsPassed(Boolean isPassed) { this.isPassed = isPassed; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Integer getCorrectAnswers() { return correctAnswers; }
    public void setCorrectAnswers(Integer correctAnswers) { this.correctAnswers = correctAnswers; }

    public Integer getTotalQuestions() { return totalQuestions; }
    public void setTotalQuestions(Integer totalQuestions) { this.totalQuestions = totalQuestions; }

    public List<QuestionResultDTO> getQuestionResults() { return questionResults; }
    public void setQuestionResults(List<QuestionResultDTO> questionResults) { this.questionResults = questionResults; }

    // Helper class for question results
    public static class QuestionResultDTO {
        private Long questionId;
        private String questionText;
        private String studentAnswer;
        private String correctAnswer;
        private Boolean isCorrect;
        private Double pointsEarned;
        private Integer totalPoints;
        private String explanation;

        // Constructors
        public QuestionResultDTO() {}

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
