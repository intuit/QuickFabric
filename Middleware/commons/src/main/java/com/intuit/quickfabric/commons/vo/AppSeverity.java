package com.intuit.quickfabric.commons.vo;

public enum AppSeverity {
    NONE("NONE"),
    LOW("LOW"),
    MODERATE("MODERATE"),
    SEVERE("SEVERE"),
    CRITICAL("CRITICAL");
    
    private final String value;
    
    AppSeverity(String severity) {
        this.value = severity;
    }
    
    public String toString() {
        return this.value;
    }

}
