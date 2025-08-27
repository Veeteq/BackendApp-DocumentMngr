package com.veeteq.documentmngr.exception;

import org.zalando.problem.AbstractThrowableProblem;

import static org.zalando.problem.Status.NOT_FOUND;

public class NotFoundException extends AbstractThrowableProblem {

    public NotFoundException() {
        super(null, "Resource not found", NOT_FOUND);
    }

    public NotFoundException(String message) {
        super(null, message, NOT_FOUND);
    }

    public NotFoundException(String message, String detail) {
        super(null, message, NOT_FOUND, detail);
    }

}
