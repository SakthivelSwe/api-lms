package com.tvm.dto;

import com.tvm.model.Question;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class QuestionDTO {

    private Long id;

    @NotBlank(message = "Question text is required")
    private String questionText;

    @NotNull(message = "Question type is required")
    private Question.QuestionType questionType;

    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;

    @NotBlank(message = "Correct answer is required")
    private String correctAnswer;

    private String explanation;

    @NotNull(message = "Points is required")
    @Positive(message = "Points must be positive")
    private Integer points = 1;

    private Question.DifficultyLevel difficultyLevel = Question.DifficultyLevel.MEDIUM;
    private Integer questionOrder;
    private Long quizId;

    // Constructors
    public QuestionDTO() {}

    public QuestionDTO(String questionText, Question.QuestionType questionType, String correctAnswer, Integer points) {
        this.questionText = questionText;
        this.questionType = questionType;
        this.correctAnswer = correctAnswer;
        this.points = points;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public Question.QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(Question.QuestionType questionType) { this.questionType = questionType; }

    public String getOptionA() { return optionA; }
    public void setOptionA(String optionA) { this.optionA = optionA; }

    public String getOptionB() { return optionB; }
    public void setOptionB(String optionB) { this.optionB = optionB; }

    public String getOptionC() { return optionC; }
    public void setOptionC(String optionC) { this.optionC = optionC; }

    public String getOptionD() { return optionD; }
    public void setOptionD(String optionD) { this.optionD = optionD; }

    public String getCorrectAnswer() { return correctAnswer; }
    public void setCorrectAnswer(String correctAnswer) { this.correctAnswer = correctAnswer; }

    public String getExplanation() { return explanation; }
    public void setExplanation(String explanation) { this.explanation = explanation; }

    public Integer getPoints() { return points; }
    public void setPoints(Integer points) { this.points = points; }

    public Question.DifficultyLevel getDifficultyLevel() { return difficultyLevel; }
    public void setDifficultyLevel(Question.DifficultyLevel difficultyLevel) { this.difficultyLevel = difficultyLevel; }

    public Integer getQuestionOrder() { return questionOrder; }
    public void setQuestionOrder(Integer questionOrder) { this.questionOrder = questionOrder; }

    public Long getQuizId() { return quizId; }
    public void setQuizId(Long quizId) { this.quizId = quizId; }
}
