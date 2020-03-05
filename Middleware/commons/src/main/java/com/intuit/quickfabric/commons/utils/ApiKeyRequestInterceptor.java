package com.intuit.quickfabric.commons.utils;

import com.intuit.quickfabric.commons.helper.ConfigHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ApiKeyRequestInterceptor implements ClientHttpRequestInterceptor {

    private String accountId;

    @Autowired
    ConfigHelper configHelper;

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
    	request.getHeaders().set("x-api-key", configHelper.getConfigValue("gateway_api_key", accountId));
    	request.getHeaders().set(HttpHeaders.ACCEPT, "application/json");
    	request.getHeaders().set(HttpHeaders.CONTENT_TYPE, "application/json");
    	return execution.execute(request, body);
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }
}

