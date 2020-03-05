package com.intuit.quickfabric.commons.vo;

public class AutoScalingTestResponse {

    private Integer min;

    private Integer max;

    private String state;

    private String clusterId;

    private String instanceGroup;
    private Integer rules;

    public Integer getMin() {
        return min;
    }

    public void setMin(Integer min) {
        this.min = min;
    }

    public Integer getMax() {
        return max;
    }

    public void setMax(Integer max) {
        this.max = max;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getRules() {
        return rules;
    }

    public void setRules(Integer rules) {
        this.rules = rules;
    }

    public String getClusterId() {
        return clusterId;
    }

    public void setClusterId(String clusterId) {
        this.clusterId = clusterId;
    }

    public String getInstanceGroup() {
        return instanceGroup;
    }

    public void setInstanceGroup(String instanceGroup) {
        this.instanceGroup = instanceGroup;
    }

    @Override
    public String toString() {
        return "AutoScalingTestResponse{" +
                "min=" + min +
                ", max=" + max +
                ", state='" + state + '\'' +
                ", clusterId='" + clusterId + '\'' +
                ", instanceGroup='" + instanceGroup + '\'' +
                ", rules=" + rules +
                '}';
    }
}
