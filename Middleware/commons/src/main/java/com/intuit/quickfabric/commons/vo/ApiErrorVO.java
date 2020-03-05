package com.intuit.quickfabric.commons.vo;

import java.util.UUID;

public class ApiErrorVO {

    private String errorMessage;

    private String messageDetails;

    private String errorId;

    public ApiErrorVO(String errorMessage) {
        this.errorMessage = errorMessage;
        errorId = UUID.randomUUID().toString();
    }

    public ApiErrorVO(String errorMessage, String messageDetails) {
        this.errorMessage = errorMessage;
        this.messageDetails = messageDetails;
        errorId = UUID.randomUUID().toString();
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getMessageDetails() {
        return messageDetails;
    }

    public void setMessageDetails(String messageDetails) {
        this.messageDetails = messageDetails;
    }

    public String getErrorId() {
        return errorId;
    }

}
