package com.tvm.service;

import com.tvm.dto.QuizDTO;
import com.tvm.exception.QuizNotFoundException;
import com.tvm.model.Quiz;
import com.tvm.repository.QuestionRepository;
import com.tvm.repository.QuizRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizService {

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private ModelMapper modelMapper;

    public QuizDTO createQuiz(QuizDTO quizDTO) {
        Quiz quiz = modelMapper.map(quizDTO, Quiz.class);
        Quiz savedQuiz = quizRepository.save(quiz);
        return convertToDTO(savedQuiz);
    }

    public QuizDTO updateQuiz(Long id, @Valid QuizDTO quizDTO) {
        Quiz existingQuiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + id));

        existingQuiz.setTitle(quizDTO.getTitle());
        existingQuiz.setDescription(quizDTO.getDescription());
        existingQuiz.setCourseId(quizDTO.getCourseId());
        existingQuiz.setModuleId(quizDTO.getModuleId());
        existingQuiz.setDurationMinutes(quizDTO.getDurationMinutes());
        existingQuiz.setMaxAttempts(quizDTO.getMaxAttempts());
        existingQuiz.setPassingScore(quizDTO.getPassingScore());
        existingQuiz.setActive(quizDTO.getIsActive());

        Quiz updatedQuiz = quizRepository.save(existingQuiz);
        return convertToDTO(updatedQuiz);
    }

    public void deleteQuiz(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + id));
        quizRepository.delete(quiz);
    }

    public QuizDTO getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + id));
        return convertToDTO(quiz);
    }

    public List<QuizDTO> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAll();
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuizDTO> getActiveQuizzes() {
        List<Quiz> quizzes = quizRepository.findByIsActiveTrue();
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuizDTO> getQuizzesByCourse(Long courseId) {
        List<Quiz> quizzes = quizRepository.findByCourseId(courseId);
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuizDTO> getQuizzesByModule(Long moduleId) {
        List<Quiz> quizzes = quizRepository.findByModuleId(moduleId);
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuizDTO> getQuizzesByCourseAndModule(Long courseId, Long moduleId) {
        List<Quiz> quizzes = quizRepository.findByCourseIdAndModuleId(courseId, moduleId);
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuizDTO> searchQuizzes(String title) {
        List<Quiz> quizzes = quizRepository.findByTitleContainingAndIsActiveTrue(title);
        return quizzes.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public void assignQuizToModule(Long quizId, Long moduleId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + quizId));
        quiz.setModuleId(moduleId);
        quizRepository.save(quiz);
    }

    public void assignQuizToCourse(Long quizId, Long courseId) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizNotFoundException("Quiz not found with id: " + quizId));
        quiz.setCourseId(courseId);
        quizRepository.save(quiz);
    }

    private QuizDTO convertToDTO(Quiz quiz) {
        QuizDTO dto = modelMapper.map(quiz, QuizDTO.class);
        dto.setTotalQuestions(questionRepository.countByQuizId(quiz.getId()).intValue());
        dto.setTotalPoints(questionRepository.getTotalPointsByQuizId(quiz.getId()));
        return dto;
    }
}
