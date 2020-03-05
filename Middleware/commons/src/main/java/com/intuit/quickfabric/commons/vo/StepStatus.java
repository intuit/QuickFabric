package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum StepStatus {

	NEW("NEW"),
	INTIATED("INTIATED"),
	VALIDATED("VALIDATED"),
	COMPLETED("COMPLETED"), 
	PENDING("PENDING"),
	FAILED("FAILED"),
	CANCELLED("CANCELLED"),
	RUNNING("RUNNING"),
	TERMINATED("TERMINATED"),
	INTERRUPTED("INTERRUPTED"),
	CANCEL_PENDING("CANCEL_PENDING"),
	COMPLETED_TERMINATEDCLUSTER("COMPLETED_TERMINATEDCLUSTER");
	 @JsonProperty("status")
    private final String value;

	StepStatus(final String type) {
        value = type;
    }

    @Override
    public String toString() {
        return value;
    }

}
