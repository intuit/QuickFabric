package com.intuit.quickfabric.commons.exceptions;

import org.springframework.http.HttpStatus;

import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;

@XmlRootElement(name = "error")
public class ServiceException {

    private int code;
    private String message;
    private String title;

    public ServiceException() {

    }

    public ServiceException(HttpStatus code, String message) {
        this.code = code.value();
        this.message = message;
        this.title = "Exception";
    }

    public ServiceException(String message) {
        this.code = HttpStatus.INTERNAL_SERVER_ERROR.value();
        this.message = message;
        this.title = "Exception";
    }

    public ServiceException(HttpStatus code, String message, String title) {
        this.code = code.value();
        this.message = message;
        this.title = title;
    }

    public int getCode() {
        return code;
    }

    public void setCode(HttpStatus code) {
        this.code = code.value();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
