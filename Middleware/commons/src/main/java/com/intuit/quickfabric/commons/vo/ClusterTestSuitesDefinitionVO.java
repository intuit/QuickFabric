package com.intuit.quickfabric.commons.vo;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

public class ClusterTestSuitesDefinitionVO {

    @JsonIgnore
    private int id;
    private String name;
    private String description;
    private String criteria;
    @JsonProperty("cluster_type")
    private ClusterType clusterType;
    @JsonProperty("cluster_segment")
    private String clusterSegment;
    private int timeout;
    @JsonProperty("expires_minutes")
    private int expiresMinutes;
    private boolean mandatory;
    private boolean disabled;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCriteria() {
        return criteria;
    }

    public void setCriteria(String criteria) {
        this.criteria = criteria;
    }

    public ClusterType getClusterType() {
        return clusterType;
    }

    public void setClusterType(ClusterType clusterType) {
        this.clusterType = clusterType;
    }

    public String getClusterSegment() {
        return clusterSegment;
    }

    public void setClusterSegment(String clusterSegment) {
        this.clusterSegment = clusterSegment;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getExpiresMinutes() {
        return expiresMinutes;
    }

    public void setExpiresMinutes(int expiresMinutes) {
        this.expiresMinutes = expiresMinutes;
    }

    public boolean isMandatory() {
        return mandatory;
    }

    public void setMandatory(boolean mandatory) {
        this.mandatory = mandatory;
    }

    public boolean isDisabled() {
        return disabled;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    @Override
    public String toString() {
        return "ClusterTestSuitesDefinitionVO{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", criteria='" + criteria + '\'' +
                ", clusterType=" + clusterType +
                ", clusterSegment='" + clusterSegment + '\'' +
                ", timeout=" + timeout +
                ", expiresMinutes=" + expiresMinutes +
                ", mandatory=" + mandatory +
                ", disabled=" + disabled +
                '}';
    }
}
