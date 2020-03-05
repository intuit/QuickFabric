package com.intuit.quickfabric.schedulers;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {SecurityAutoConfiguration.class}, scanBasePackages = {"com.intuit.quickfabric"})
@EnableScheduling
@EnableRetry
@EnableAsync
public class SchedulersMainApplication extends SpringBootServletInitializer {
    private static final Logger logger = LogManager.getLogger(SchedulersMainApplication.class);

    public static void main(String args[]) {
        SpringApplication.run(SchedulersMainApplication.class, args);
        logger.info("----Application Started----");

    }

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {

        return application;
    }
}

