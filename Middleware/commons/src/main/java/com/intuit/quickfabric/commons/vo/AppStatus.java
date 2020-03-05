package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum AppStatus {
    RUNNING("RUNNING"),
    SUCCEEDED("SUCCEEDED"),
    FAILED("FAILED");
    
 // the value used to match json node string value with an enum constant
    @JsonProperty("status")
   private final String value;

   AppStatus(final String type) {
       value = type;
   }

   @Override
   public String toString() {
       return value;
   }
}
