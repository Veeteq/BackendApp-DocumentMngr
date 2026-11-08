package com.veeteq.documentmngr.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.AdviceTrait;

public interface IllegalArgumentAdviceTrait extends AdviceTrait {

    @ExceptionHandler(IllegalArgumentException.class)
    default ResponseEntity<Problem> handleIllegalArgumentException(
            IllegalArgumentException exception,
            NativeWebRequest request) {

        return create(
                Status.BAD_REQUEST,
                exception,
                request
        );
    }
}
