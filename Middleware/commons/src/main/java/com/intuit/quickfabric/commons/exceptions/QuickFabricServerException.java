package com.intuit.quickfabric.commons.exceptions;

import org.springframework.http.HttpStatus;

public class QuickFabricServerException extends QuickFabricBaseException {

    public QuickFabricServerException(String message) {
        super(message);
    }

    public QuickFabricServerException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuickFabricServerException(Exception e) {
        super(e);
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
