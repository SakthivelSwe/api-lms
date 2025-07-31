package com.tvm.service;

import com.tvm.dto.QuestionDTO;
import com.tvm.exception.QuestionDoesNotBelongToQuizException;
import com.tvm.exception.QuestionNotFoundException;
import com.tvm.model.Question;
import com.tvm.model.Quiz;
import com.tvm.repository.QuestionRepository;
import com.tvm.repository.QuizRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuestionService {

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private ModelMapper modelMapper;

    public QuestionDTO createQuestion(QuestionDTO questionDTO) {
        Quiz quiz = quizRepository.findById(questionDTO.getQuizId())
                .orElseThrow(() -> new QuestionNotFoundException("Quiz not found with id: " + questionDTO.getQuizId()));

        Question question = modelMapper.map(questionDTO, Question.class);
        question.setQuiz(quiz);

        // Set question order if not provided
        if (question.getQuestionOrder() == null) {
            Long questionCount = questionRepository.countByQuizId(quiz.getId());
            question.setQuestionOrder(questionCount.intValue() + 1);
        }

        Question savedQuestion = questionRepository.save(question);
        return convertToDTO(savedQuestion);
    }

    public QuestionDTO updateQuestion(Long id, QuestionDTO questionDTO) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + id));

        existingQuestion.setQuestionText(questionDTO.getQuestionText());
        existingQuestion.setQuestionType(questionDTO.getQuestionType());
        existingQuestion.setOptionA(questionDTO.getOptionA());
        existingQuestion.setOptionB(questionDTO.getOptionB());
        existingQuestion.setOptionC(questionDTO.getOptionC());
        existingQuestion.setOptionD(questionDTO.getOptionD());
        existingQuestion.setCorrectAnswer(questionDTO.getCorrectAnswer());
        existingQuestion.setExplanation(questionDTO.getExplanation());
        existingQuestion.setPoints(questionDTO.getPoints());
        existingQuestion.setDifficultyLevel(questionDTO.getDifficultyLevel());
        existingQuestion.setQuestionOrder(questionDTO.getQuestionOrder());

        Question updatedQuestion = questionRepository.save(existingQuestion);
        return convertToDTO(updatedQuestion);
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + id));
        questionRepository.delete(question);
    }

    public QuestionDTO getQuestionById(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + id));
        return convertToDTO(question);
    }

    public List<QuestionDTO> getQuestionsByQuiz(Long quizId) {
        List<Question> questions = questionRepository.findByQuizIdOrderByQuestionOrder(quizId);
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuestionDTO> getQuestionsByQuizForStudent(Long quizId) {
        // Return questions without correct answers for students
        List<Question> questions = questionRepository.findByQuizIdOrderByQuestionOrder(quizId);
        return questions.stream()
                .map(this::convertToDTOWithoutAnswer)
                .collect(Collectors.toList());
    }

    public List<QuestionDTO> getQuestionsByType(Long quizId, Question.QuestionType questionType) {
        List<Question> questions = questionRepository.findByQuestionTypeAndQuizId(questionType, quizId);
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuestionDTO> getQuestionsByDifficulty(Long quizId, Question.DifficultyLevel difficultyLevel) {
        List<Question> questions = questionRepository.findByDifficultyLevelAndQuizId(difficultyLevel, quizId);
        return questions.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void reorderQuestions(Long quizId, List<Long> questionIds) {
        for (int i = 0; i < questionIds.size(); i++) {
            Long questionId = questionIds.get(i);
            Question question = questionRepository.findById(questionId)
                    .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + questionId));

            if (!question.getQuiz().getId().equals(quizId)) {
                throw new QuestionDoesNotBelongToQuizException("Question does not belong to the specified quiz");
            }

            question.setQuestionOrder(i + 1);
            questionRepository.save(question);
        }
    }

    private QuestionDTO convertToDTO(Question question) {
        QuestionDTO dto = modelMapper.map(question, QuestionDTO.class);
        dto.setQuizId(question.getQuiz().getId());
        return dto;
    }

    private QuestionDTO convertToDTOWithoutAnswer(Question question) {
        QuestionDTO dto = convertToDTO(question);
        dto.setCorrectAnswer(null); // Hide correct answer from students
        dto.setExplanation(null); // Hide explanation from students during quiz
        return dto;
    }
}