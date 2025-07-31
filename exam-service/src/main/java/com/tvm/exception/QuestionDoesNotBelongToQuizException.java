package com.tvm.exception;

public class QuestionDoesNotBelongToQuizException extends RuntimeException {
    public QuestionDoesNotBelongToQuizException(String message) {
        super(message);
    }
}
