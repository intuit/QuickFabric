package com.intuit.quickfabric.commons.exceptions;

import org.springframework.http.HttpStatus;

public abstract class QuickFabricBaseException extends RuntimeException {

    public abstract HttpStatus getHttpStatusCode();

    public QuickFabricBaseException(String message) {
        super(message);
    }

    public QuickFabricBaseException(Throwable e) {
        super(e);
    }

    public QuickFabricBaseException(String message, Throwable cause) {
        super(message, cause);
    }
}

