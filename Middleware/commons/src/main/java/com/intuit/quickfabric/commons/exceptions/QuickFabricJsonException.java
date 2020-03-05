package com.intuit.quickfabric.commons.exceptions;

import org.json.JSONException;
import org.springframework.http.HttpStatus;

public class QuickFabricJsonException extends QuickFabricBaseException {

    public QuickFabricJsonException(Exception e) {
        this("Error happen during json manipulation", e);
    }

    public QuickFabricJsonException(String message, Exception e) {
        super(message, e);
    }

    @Override
    public HttpStatus getHttpStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }
}
