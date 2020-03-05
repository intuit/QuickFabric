package com.intuit.quickfabric.commons.vo;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ClusterHealthCheckRequest {

    @NotNull(message = "Cluster Id cannot be empty")
    @Size(min = 1)
    private String clusterId;

    @NotNull(message = "Cluster name cannot be empty")
    @Size(min = 1)
    private String clusterName;

    @NotNull(message = "Cluster type cannot be empty")
    @Size(min = 1)
    private String clusterType;

    @NotNull(message = "Cluster segment cannot be empty")
    @Size(min = 1)
    private String clusterSegment;

    private boolean overrideTimeout;

    public boolean isOverrideTimeout() {
        return overrideTimeout;
    }

    public void setOverrideTimeout(boolean overrideTimeout) {
        this.overrideTimeout = overrideTimeout;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterType() {
        return clusterType;
    }

    public void setClusterType(String clusterType) {
        this.clusterType = clusterType;
    }

    public String getClusterSegment() {
        return clusterSegment;
    }

    public void setClusterSegment(String clusterSegment) {
        this.clusterSegment = clusterSegment;
    }

    @Override
    public String toString() {
        return "ClusterHealthCheckRequest{" +
                "clusterId='" + clusterId + '\'' +
                ", clusterName='" + clusterName + '\'' +
                ", clusterType='" + clusterType + '\'' +
                ", clusterSegment='" + clusterSegment + '\'' +
                ", overrideTimeout=" + overrideTimeout +
                '}';
    }
}
