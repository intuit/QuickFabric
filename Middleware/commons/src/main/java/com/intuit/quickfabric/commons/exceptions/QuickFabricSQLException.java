package com.intuit.quickfabric.commons.exceptions;

import org.springframework.http.HttpStatus;

public class QuickFabricSQLException extends QuickFabricBaseException {

    public QuickFabricSQLException(String message) {
        super(message);
    }

    public QuickFabricSQLException(String message, Throwable cause) {
        super(message, cause);
    }

    public QuickFabricSQLException(Exception e) {
        super(e);
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
