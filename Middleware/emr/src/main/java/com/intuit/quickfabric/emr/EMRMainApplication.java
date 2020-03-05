package com.intuit.quickfabric.emr;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.retry.annotation.EnableRetry;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class }, scanBasePackages = {"com.intuit.quickfabric"})
@EnableRetry
public class EMRMainApplication extends SpringBootServletInitializer
{
    private static final Logger logger = LogManager.getLogger(EMRMainApplication.class);

    public static void main (String args[]) 
    {
        SpringApplication.run(EMRMainApplication.class,args);
        logger.info("----Application Started----");

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application;
    }
}