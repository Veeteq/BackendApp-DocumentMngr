package com.veeteq.documentmngr.exception;

import jakarta.persistence.OptimisticLockException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.NativeWebRequest;
import org.zalando.problem.Problem;
import org.zalando.problem.Status;
import org.zalando.problem.spring.web.advice.AdviceTrait;

public interface OptimisticLockExceptionAdviceTrait extends AdviceTrait {

    @ExceptionHandler(OptimisticLockException.class)
    default ResponseEntity<Problem> handleOptimisticLockException(OptimisticLockException exception, NativeWebRequest request) {
        return create(Status.CONFLICT, exception, request);
    }
}
