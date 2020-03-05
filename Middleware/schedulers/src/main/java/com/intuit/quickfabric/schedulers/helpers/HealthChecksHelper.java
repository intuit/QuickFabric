package com.intuit.quickfabric.schedulers.helpers;

import com.intuit.quickfabric.commons.vo.ClusterHealthStatus;
import com.intuit.quickfabric.commons.vo.ClusterVO;
import com.intuit.quickfabric.commons.vo.EMRClusterHealthTestCase;
import com.intuit.quickfabric.schedulers.dao.ClusterHealthCheckDao;
import com.intuit.quickfabric.schedulers.dao.EMRClusterMetadataDao;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class HealthChecksHelper {

    private final Logger logger = LogManager.getLogger(HealthChecksHelper.class);

    @Autowired
    ClusterHealthCheckDao clusterHealthCheckDao;

    @Autowired
    EMRClusterMetadataDao clusterMetadataDao;

    public void submitHealthChecks(List<String> clusters) {
        try {
            logger.info("Clusters to submit health check:" + clusters.toString());
            for (String clusterId : clusters) {
                try {
                    logger.info("Running default steps health check for cluster Id:" + clusterId);
                    ClusterVO cluster = clusterMetadataDao.getClusterMetadata(clusterId);

                    List<ClusterHealthStatus> currentTestCases = clusterHealthCheckDao.getEMRClusterHealthStatus(clusterId);
                    if (currentTestCases.size() > 0) {
                        logger.warn("submitHealthChecks - Tests are already there for clusterId:" + clusterId +
                                " Tests:" + currentTestCases.stream().map(ClusterHealthStatus::getTestName)
                                .collect(Collectors.joining(",")));
                        continue;
                    }

                    List<EMRClusterHealthTestCase> testCases = clusterHealthCheckDao.getEMRClusterTestSuites(
                            cluster.getSegment(),
                            cluster.getType().getValue());
                    if (testCases.size() == 0) {
                        logger.warn("submitHealthChecks - No Tests found for cluster type: " + cluster.getType().getValue() +
                                " and cluster role: " + cluster.getSegment());
                        continue;
                    }

                    clusterHealthCheckDao.createHealthCheckTestCases(testCases,
                            cluster.getClusterId(),
                            cluster.getClusterName(),
                            "scheduler");
                } catch (Exception ex) {
                    logger.error("Error while inserting test cases for cluster id:" + clusterId, ex);
                }
            }
        } catch (Exception ex) {
            logger.error("error while doing submitHealthChecks", ex);
        }
    }
}
