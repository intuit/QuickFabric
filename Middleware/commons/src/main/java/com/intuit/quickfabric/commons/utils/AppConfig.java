package com.intuit.quickfabric.commons.utils;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.http.client.*;
import org.springframework.web.client.RestTemplate;

import com.intuit.quickfabric.commons.utils.ApiKeyRequestInterceptor;
import com.intuit.quickfabric.commons.utils.LoggingRequestInterceptor;

@Configuration
public class AppConfig 
{
    @Autowired
    ApiKeyRequestInterceptor apiKeyRequestInterceptor;
    
    @Autowired
    LoggingRequestInterceptor loggingRequestInterceptor;
    
	@Bean
	@Scope(value = "prototype")
	public RestTemplate restTemplate(String account_id) {
		SimpleClientHttpRequestFactory simpleClientHttpRequestFactory = new SimpleClientHttpRequestFactory();
		BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory = new BufferingClientHttpRequestFactory(simpleClientHttpRequestFactory);
		RestTemplate restTemplate = new RestTemplate(bufferingClientHttpRequestFactory);
		apiKeyRequestInterceptor.setAccountId(account_id);
		restTemplate.getInterceptors().add(apiKeyRequestInterceptor);
		restTemplate.getInterceptors().add(loggingRequestInterceptor);
		return restTemplate;
	}
}
