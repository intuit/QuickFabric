package com.intuit.quickfabric.commons.exceptions;

import org.springframework.http.HttpStatus;

public class QuickFabricRestHandlerException extends QuickFabricBaseException {

    public QuickFabricRestHandlerException(String message) {
        super(message);
    }

    public QuickFabricRestHandlerException(Throwable e) {
        super(e);
    }

    public QuickFabricRestHandlerException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
