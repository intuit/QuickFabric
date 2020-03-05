package com.intuit.quickfabric.commons.exceptions;

import org.springframework.http.HttpStatus;

public class QuickFabricUnauthorizedException extends QuickFabricBaseException {

    public QuickFabricUnauthorizedException(){
        this("Access is denied.");
    }

    public QuickFabricUnauthorizedException(String message) {
        super(message);
    }

    public QuickFabricUnauthorizedException(String message, Throwable cause) {
        super(message, cause);
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return HttpStatus.FORBIDDEN;
    }
}
