package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum ClusterType {
	@JsonProperty("exploratory")
	EXPLORATORY("exploratory"),
	@JsonProperty("scheduled")
	SCHEDULED("scheduled"),
	@JsonProperty("transient")
	TRANSIENT("transient");
	
	 // the value which is used for matching
    // the json node value with this enum
	 @JsonProperty("type")
    private final String value;

	 
	 public String getValue() {
	        return value;
	    }
	 
	ClusterType(final String type) 
	{
        value = type;
    }

    @Override
    public String toString() {
        return value;
    }
}
