package com.intuit.quickfabric.commons.exceptions;

import org.springframework.http.HttpStatus;

public class QuickFabricUnauthenticatedException extends QuickFabricBaseException {
    public QuickFabricUnauthenticatedException(String message) {
        super(message);
    }

    public QuickFabricUnauthenticatedException(Throwable e) {
        super(e);
    }

    public QuickFabricUnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return HttpStatus.UNAUTHORIZED;
    }
}
