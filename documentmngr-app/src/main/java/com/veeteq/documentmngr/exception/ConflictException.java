package com.veeteq.documentmngr.exception;

import org.zalando.problem.AbstractThrowableProblem;

import static org.zalando.problem.Status.CONFLICT;

public class ConflictException extends AbstractThrowableProblem {

    public ConflictException() {
        super(null, "Conflict", CONFLICT);
    }

    public ConflictException(String message) {
        super(null, message, CONFLICT);
    }

    public ConflictException(String message, String detail) {
        super(null, message, CONFLICT, detail);
    }

}
