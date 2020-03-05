package com.intuit.quickfabric.commons.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.helper.ConfigHelper;

@Aspect
@Component
public class EnableMethodAspect {
    
    private static final Logger logger = LogManager.getLogger(EnableMethodAspect.class);

    @Autowired
    private ConfigHelper configHelper;
    
    @Around(value = "@annotation(enabledMethod)", argNames = "joinPoint , enabledMethod")
    public void handle(ProceedingJoinPoint joinPoint, EnableMethod enabledMethod) throws Throwable {
        String configName = enabledMethod.configName();
        
        boolean enabled;
        try {
            enabled = configHelper.getConfigValue(configName);
        } catch(Exception e) {
            logger.error("Error fetching config with name \"{}\". Defaulting to disabling method. "
                    + "Error: {}", configName, e.getMessage());
            enabled = false;
        }

        if(enabled) {
            joinPoint.proceed();
        } else {
            logger.info("Config with name \"{}\" is turned off, disabling associated method.", configName);
        }
    }

}
