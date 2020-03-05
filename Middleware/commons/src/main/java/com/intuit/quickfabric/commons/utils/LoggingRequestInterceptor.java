package com.intuit.quickfabric.commons.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@Component
public class LoggingRequestInterceptor implements ClientHttpRequestInterceptor {

    final static Logger log = LogManager.getLogger(LoggingRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        traceRequest(request, body);
        ClientHttpResponse response = execution.execute(request, body);
        traceResponse(response);
        return response;
    }

    private void traceRequest(HttpRequest request, byte[] body) throws IOException {
        log.info("Request URI         : {}", request.getURI());
        log.info("Request Method      : {}", request.getMethod());
        log.info("Request Headers     : {}", request.getHeaders());
        log.info("Request body: {}", new String(body, "UTF-8"));
    }

    private void traceResponse(ClientHttpResponse response) throws IOException {

        if (response != null) {
            if (response.getClass().getName().toLowerCase().contains("bufferingclienthttpresponsewrapper")) {
                log.info("Response Status code  : {}", response.getStatusCode());
                log.info("Response Status text  : {}", response.getStatusText());
                log.info("Response Headers      : {}", response.getHeaders());
                if (response.getBody() != null) {
                    StringBuilder inputStringBuilder = new StringBuilder();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(response.getBody(), "UTF-8"));
                    String line = bufferedReader.readLine();
                    while (line != null) {
                        inputStringBuilder.append(line);
                        line = bufferedReader.readLine();
                    }
                    log.info("Response body: {}", inputStringBuilder.toString());
                } else {
                    log.info("Response body: was null");
                }
            } else {
                log.info("Response body: not of type BufferingClientHttpResponseWrapper. Type:" + response.getClass().getName());
            }
        } else {
            log.info("Response body: Response is null.");
        }

    }
}