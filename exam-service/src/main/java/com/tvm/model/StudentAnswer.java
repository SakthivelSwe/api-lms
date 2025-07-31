package com.tvm.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "student_answers")
public class StudentAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "student_answer", columnDefinition = "TEXT")
    private String studentAnswer;

    @Column(name = "is_correct")
    private Boolean isCorrect = false;

    @Column(name = "points_earned")
    private Double pointsEarned = 0.0;

    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_attempt_id", nullable = false)
    private QuizAttempt quizAttempt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;

    @PrePersist
    protected void onCreate() {
        answeredAt = LocalDateTime.now();
    }

    // Constructors
    public StudentAnswer() {}

    public StudentAnswer(String studentAnswer, QuizAttempt quizAttempt, Question question) {
        this.studentAnswer = studentAnswer;
        this.quizAttempt = quizAttempt;
        this.question = question;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getStudentAnswer() { return studentAnswer; }
    public void setStudentAnswer(String studentAnswer) { this.studentAnswer = studentAnswer; }

    public Boolean getIsCorrect() { return isCorrect; }
    public void setIsCorrect(Boolean isCorrect) { this.isCorrect = isCorrect; }

    public Double getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(Double pointsEarned) { this.pointsEarned = pointsEarned; }

    public LocalDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(LocalDateTime answeredAt) { this.answeredAt = answeredAt; }

    public QuizAttempt getQuizAttempt() { return quizAttempt; }
    public void setQuizAttempt(QuizAttempt quizAttempt) { this.quizAttempt = quizAttempt; }

    public Question getQuestion() { return question; }
    public void setQuestion(Question question) { this.question = question; }
}
