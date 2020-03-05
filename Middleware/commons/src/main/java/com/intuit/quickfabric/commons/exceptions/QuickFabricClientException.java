package com.intuit.quickfabric.commons.exceptions;

import org.springframework.http.HttpStatus;

public class QuickFabricClientException extends QuickFabricBaseException {

    public QuickFabricClientException(String message) {
        super(message);
    }

    public QuickFabricClientException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return HttpStatus.BAD_REQUEST;
    }
}
