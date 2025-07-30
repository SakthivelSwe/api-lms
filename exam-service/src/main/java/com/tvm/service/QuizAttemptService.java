package com.tvm.service;

import com.tvm.dto.QuizAttemptDTO;
import com.tvm.dto.QuizResultDTO;
import com.tvm.exception.QuestionDoesNotBelongToQuizException;
import com.tvm.exception.QuestionNotFoundException;
import com.tvm.exception.QuizAttemptException;
import com.tvm.model.Question;
import com.tvm.model.Quiz;
import com.tvm.model.QuizAttempt;
import com.tvm.model.StudentAnswer;
import com.tvm.repository.QuestionRepository;
import com.tvm.repository.QuizAttemptRepository;
import com.tvm.repository.QuizRepository;
import com.tvm.repository.StudentAnswerRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class QuizAttemptService {

    @Autowired
    private QuizAttemptRepository quizAttemptRepository;

    @Autowired
    private QuizRepository quizRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private StudentAnswerRepository studentAnswerRepository;

    @Autowired
    private ModelMapper modelMapper;

    public QuizAttemptDTO startQuiz(String studentId, Long quizId, String studentName) {
        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new QuizAttemptException("Quiz not found with id: " + quizId));

        if (!quiz.getActive()) {
            throw new QuizAttemptException("Quiz is not active");
        }

        // Check if student has an in-progress attempt
        quizAttemptRepository.findInProgressAttempt(studentId, quizId)
                .ifPresent(attempt -> {
                    throw new QuizAttemptException("Student already has an in-progress attempt for this quiz");
                });

        // Check attempt limit
        Long attemptCount = quizAttemptRepository.countByStudentIdAndQuizId(studentId, quizId);
        if (quiz.getMaxAttempts() != null && attemptCount >= quiz.getMaxAttempts()) {
            throw new QuizAttemptException("Maximum attempts exceeded for this quiz");
        }

        QuizAttempt attempt = new QuizAttempt();
        attempt.setStudentId(studentId);
        attempt.setStudentName(studentName);
        attempt.setQuiz(quiz);
        attempt.setAttemptNumber(attemptCount.intValue() + 1);
        attempt.setMaxScore(questionRepository.getTotalPointsByQuizId(quizId));
        attempt.setStatus(QuizAttempt.AttemptStatus.IN_PROGRESS);

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        return convertToDTO(savedAttempt);
    }

    public QuizAttemptDTO saveAnswer(Long attemptId, Long questionId, String answer) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new QuizAttemptException("Quiz attempt not found with id: " + attemptId));

        if (attempt.getStatus() != QuizAttempt.AttemptStatus.IN_PROGRESS) {
            throw new QuizAttemptException("Quiz attempt is not in progress");
        }

        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new QuestionNotFoundException("Question not found with id: " + questionId));

        if (!question.getQuiz().getId().equals(attempt.getQuiz().getId())) {
            throw new QuestionDoesNotBelongToQuizException("Question does not belong to this quiz");
        }

        // Check if answer already exists and update or create new one
        StudentAnswer studentAnswer = studentAnswerRepository
                .findByQuizAttemptIdAndQuestionId(attemptId, questionId)
                .orElse(new StudentAnswer());

        studentAnswer.setStudentAnswer(answer);
        studentAnswer.setQuizAttempt(attempt);
        studentAnswer.setQuestion(question);

        // Evaluate the answer
        evaluateAnswer(studentAnswer, question);

        studentAnswerRepository.save(studentAnswer);
        return convertToDTO(attempt);
    }

    public QuizResultDTO submitQuiz(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new QuizAttemptException("Quiz attempt not found with id: " + attemptId));

        if (attempt.getStatus() != QuizAttempt.AttemptStatus.IN_PROGRESS) {
            throw new QuizAttemptException("Quiz attempt is not in progress");
        }

        attempt.setEndTime(LocalDateTime.now());
        attempt.setStatus(QuizAttempt.AttemptStatus.COMPLETED);

        // Calculate time taken
        long timeTaken = ChronoUnit.MINUTES.between(attempt.getStartTime(), attempt.getEndTime());
        attempt.setTimeTakenMinutes((int) timeTaken);

        // Calculate total score
        Double totalScore = studentAnswerRepository.getTotalPointsByAttemptId(attemptId);
        if (totalScore == null) totalScore = 0.0;

        attempt.setTotalScore(totalScore);

        // Calculate percentage
        if (attempt.getMaxScore() != null && attempt.getMaxScore() > 0) {
            double percentage = (totalScore / attempt.getMaxScore()) * 100;
            attempt.setPercentage(percentage);

            // Check if passed
            attempt.setIsPassed(percentage >= attempt.getQuiz().getPassingScore());
        }

        // Generate feedback
        generateFeedback(attempt);

        QuizAttempt savedAttempt = quizAttemptRepository.save(attempt);
        return convertToResultDTO(savedAttempt);
    }

    public QuizResultDTO getQuizResult(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new QuizAttemptException("Quiz attempt not found with id: " + attemptId));

        if (attempt.getStatus() != QuizAttempt.AttemptStatus.COMPLETED) {
            throw new QuizAttemptException("Quiz attempt is not completed yet");
        }

        return convertToResultDTO(attempt);
    }

    public List<QuizAttemptDTO> getStudentAttempts(String studentId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByStudentIdOrderByStartTimeDesc(studentId);
        return attempts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuizAttemptDTO> getQuizAttempts(Long quizId) {
        List<QuizAttempt> attempts = quizAttemptRepository.findByQuizIdOrderByStartTimeDesc(quizId);
        return attempts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<QuizAttemptDTO> getStudentQuizAttempts(String studentId, Long quizId) {
        List<QuizAttempt> attempts = quizAttemptRepository
                .findByStudentIdAndQuizIdOrderByAttemptNumberDesc(studentId, quizId);
        return attempts.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public QuizAttemptDTO submitQuizWithAnswers(String studentId, Long quizId, Map<Long, String> answers, String studentName) {
        // Start the quiz
        QuizAttemptDTO attemptDTO = startQuiz(studentId, quizId, studentName);

        // Save all answers
        for (Map.Entry<Long, String> entry : answers.entrySet()) {
            saveAnswer(attemptDTO.getId(), entry.getKey(), entry.getValue());
        }

        // Submit the quiz
        QuizResultDTO result = submitQuiz(attemptDTO.getId());

        // Convert result back to attempt DTO
        QuizAttemptDTO finalAttempt = new QuizAttemptDTO();
        finalAttempt.setId(result.getAttemptId());
        finalAttempt.setStudentId(result.getStudentId());
        finalAttempt.setStudentName(result.getStudentName());
        finalAttempt.setTotalScore(result.getTotalScore());
        finalAttempt.setMaxScore(result.getMaxScore());
        finalAttempt.setPercentage(result.getPercentage());
        finalAttempt.setIsPassed(result.getIsPassed());
        finalAttempt.setStatus(QuizAttempt.AttemptStatus.COMPLETED);

        return finalAttempt;
    }

    public void timeoutQuiz(Long attemptId) {
        QuizAttempt attempt = quizAttemptRepository.findById(attemptId)
                .orElseThrow(() -> new QuizAttemptException("Quiz attempt not found with id: " + attemptId));

        if (attempt.getStatus() == QuizAttempt.AttemptStatus.IN_PROGRESS) {
            attempt.setEndTime(LocalDateTime.now());
            attempt.setStatus(QuizAttempt.AttemptStatus.TIMEOUT);

            // Calculate score for submitted answers
            Double totalScore = studentAnswerRepository.getTotalPointsByAttemptId(attemptId);
            if (totalScore == null) totalScore = 0.0;

            attempt.setTotalScore(totalScore);

            if (attempt.getMaxScore() != null && attempt.getMaxScore() > 0) {
                double percentage = (totalScore / attempt.getMaxScore()) * 100;
                attempt.setPercentage(percentage);
                attempt.setIsPassed(percentage >= attempt.getQuiz().getPassingScore());
            }

            attempt.setFeedback("Quiz timed out. Score calculated based on submitted answers.");
            quizAttemptRepository.save(attempt);
        }
    }

    private void evaluateAnswer(StudentAnswer studentAnswer, Question question) {
        String correctAnswer = question.getCorrectAnswer().trim().toLowerCase();
        String givenAnswer = studentAnswer.getStudentAnswer().trim().toLowerCase();

        boolean isCorrect = false;
        double pointsEarned = 0.0;

        switch (question.getQuestionType()) {
            case MULTIPLE_CHOICE:
            case TRUE_FALSE:
                isCorrect = correctAnswer.equals(givenAnswer);
                pointsEarned = isCorrect ? question.getPoints() : 0.0;
                break;

            case SHORT_ANSWER:
                // Simple string matching - can be enhanced with fuzzy matching
                isCorrect = correctAnswer.equals(givenAnswer);
                pointsEarned = isCorrect ? question.getPoints() : 0.0;
                break;

            case ESSAY:
                // For essay questions, manual grading might be required
                // For now, we'll give partial points
                pointsEarned = question.getPoints() * 0.5; // 50% for attempt
                isCorrect = false; // Requires manual review
                break;
        }

        studentAnswer.setIsCorrect(isCorrect);
        studentAnswer.setPointsEarned(pointsEarned);
    }

    private void generateFeedback(QuizAttempt attempt) {
        StringBuilder feedback = new StringBuilder();

        if (attempt.getIsPassed()) {
            feedback.append("Congratulations! You have passed the quiz. ");
        } else {
            feedback.append("You did not meet the passing criteria. ");
        }

        feedback.append(String.format("Your score: %.1f out of %d (%.1f%%). ",
                attempt.getTotalScore(), attempt.getMaxScore(), attempt.getPercentage()));

        Long correctAnswers = studentAnswerRepository.countCorrectAnswersByAttemptId(attempt.getId());
        Long totalQuestions = questionRepository.countByQuizId(attempt.getQuiz().getId());

        feedback.append(String.format("You answered %d out of %d questions correctly.",
                correctAnswers, totalQuestions));

        if (attempt.getTimeTakenMinutes() != null) {
            feedback.append(String.format(" Time taken: %d minutes.", attempt.getTimeTakenMinutes()));
        }

        attempt.setFeedback(feedback.toString());
    }

    private QuizAttemptDTO convertToDTO(QuizAttempt attempt) {
        QuizAttemptDTO dto = modelMapper.map(attempt, QuizAttemptDTO.class);
        dto.setQuizId(attempt.getQuiz().getId());
        dto.setQuizTitle(attempt.getQuiz().getTitle());
        return dto;
    }

    private QuizResultDTO convertToResultDTO(QuizAttempt attempt) {
        QuizResultDTO result = new QuizResultDTO();
        result.setAttemptId(attempt.getId());
        result.setStudentId(attempt.getStudentId());
        result.setStudentName(attempt.getStudentName());
        result.setQuizTitle(attempt.getQuiz().getTitle());
        result.setStartTime(attempt.getStartTime());
        result.setEndTime(attempt.getEndTime());
        result.setTimeTakenMinutes(attempt.getTimeTakenMinutes());
        result.setTotalScore(attempt.getTotalScore());
        result.setMaxScore(attempt.getMaxScore());
        result.setPercentage(attempt.getPercentage());
        result.setIsPassed(attempt.getIsPassed());
        result.setFeedback(attempt.getFeedback());

        // Get question results
        List<StudentAnswer> studentAnswers = studentAnswerRepository.findByQuizAttemptId(attempt.getId());
        Long correctAnswers = studentAnswerRepository.countCorrectAnswersByAttemptId(attempt.getId());
        Long totalQuestions = questionRepository.countByQuizId(attempt.getQuiz().getId());

        result.setCorrectAnswers(correctAnswers.intValue());
        result.setTotalQuestions(totalQuestions.intValue());

        List<QuizResultDTO.QuestionResultDTO> questionResults = studentAnswers.stream()
                .map(this::convertToQuestionResultDTO)
                .collect(Collectors.toList());

        result.setQuestionResults(questionResults);
        return result;
    }

    private QuizResultDTO.QuestionResultDTO convertToQuestionResultDTO(StudentAnswer studentAnswer) {
        QuizResultDTO.QuestionResultDTO questionResult = new QuizResultDTO.QuestionResultDTO();
        questionResult.setQuestionId(studentAnswer.getQuestion().getId());
        questionResult.setQuestionText(studentAnswer.getQuestion().getQuestionText());
        questionResult.setStudentAnswer(studentAnswer.getStudentAnswer());
        questionResult.setCorrectAnswer(studentAnswer.getQuestion().getCorrectAnswer());
        questionResult.setIsCorrect(studentAnswer.getIsCorrect());
        questionResult.setPointsEarned(studentAnswer.getPointsEarned());
        questionResult.setTotalPoints(studentAnswer.getQuestion().getPoints());
        questionResult.setExplanation(studentAnswer.getQuestion().getExplanation());
        return questionResult;
    }
}