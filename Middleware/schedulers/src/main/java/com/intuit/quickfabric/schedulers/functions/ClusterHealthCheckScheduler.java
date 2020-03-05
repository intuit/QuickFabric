package com.intuit.quickfabric.schedulers.functions;

import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.utils.EnableMethod;
import com.intuit.quickfabric.schedulers.helpers.AutoScalingHealthCheckHelper;
import com.intuit.quickfabric.schedulers.helpers.BootstrapHealthCheckHelper;
import com.intuit.quickfabric.schedulers.helpers.ConnectivityHealthCheckHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(value = "scheduling.enabled", havingValue = "true")
public class ClusterHealthCheckScheduler {

    private final Logger logger = LogManager.getLogger(ClusterHealthCheckScheduler.class);

    @Autowired
    ConnectivityHealthCheckHelper connectivityHealthCheckHelper;

    @Autowired
    BootstrapHealthCheckHelper bootstrapHealthCheckHelper;

    @Autowired
    AutoScalingHealthCheckHelper autoScalingHealthCheckHelper;

    @Scheduled(cron = "${verifyNumberOfBootstrapsSchedule}")
    @Async
    @EnableMethod(configName = "verify_number_of_bootstraps_scheduler")
    public void verifyNumberOfBootstraps() {
        try {
            logger.info("ClusterHealthCheckScheduler -> verifyNumberOfBootstraps starting");
            bootstrapHealthCheckHelper.verifyNoOfBootstraps();
            logger.info("ClusterHealthCheckScheduler -> verifyNumberOfBootstraps completed");
        } catch (Exception e) {
            CommonUtils.logErrorResponse(e);
        }
    }

    @Scheduled(cron = "${initiateConnectivityTestSchedule}")
    @Async
    @EnableMethod(configName = "connectivity_test_scheduler")
    public void initiateConnectivityTest() {
        try {
            logger.info("ClusterHealthCheckScheduler -> initiateConnectivityTest starting");
            connectivityHealthCheckHelper.initiateConnectivityTests();
            logger.info("ClusterHealthCheckScheduler -> initiateConnectivityTest completed");
        } catch (Exception e) {
            CommonUtils.logErrorResponse(e);
        }
    }

    @Scheduled(cron = "${autoScalingConfigurationTestSchedule}")
    @Async
    @EnableMethod(configName = "autoscaling_config_test_scheduler")
    public void autoScalingConfigurationTest() {
        try {
            logger.info("ClusterHealthCheckScheduler -> autoScalingConfigurationTest starting");
            autoScalingHealthCheckHelper.checkAutoScalingConfiguration();
            logger.info("ClusterHealthCheckScheduler -> autoScalingConfigurationTest completed");
        } catch (Exception e) {
            CommonUtils.logErrorResponse(e);
        }
    }
}
