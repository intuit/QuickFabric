package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
@JsonInclude(JsonInclude.Include.NON_EMPTY)

public enum ClusterStatus {
	// Defining constants like this so we can use this enum to convert string 
	// values from JSON to enum constants
	STARTING("STARTING"),
	INITIATED("INITIATED"),
	FAILED("FAILED"),
	RUNNING("RUNNING"),
	BOOTSTRAPPING("BOOTSTRAPPING"),
	TERMINATION_INITIATED("TERMINATION_INITIATED"),
	TERMINATED("TERMINATED"),
	TERMINATING("TERMINATING"),
	ClusterAlreadyExists("ClusterAlreadyExists"),
	ClusterNotPresent("ClusterNotPresent"),
	HEALTHY("HEALTHY"),
	unhealthy("unhealthy"),
	WAITING("WAITING"),
	Completed("Completed"),
	TERMINATED_WITH_ERRORS("TERMINATED_WITH_ERRORS");

	// the value used to match json node string value with an enum constant
	 @JsonProperty("status")
    private final String value;

    ClusterStatus(final String type) {
        value = type;
    }

    @Override
    public String toString() {
        return value;
    }
}