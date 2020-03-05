package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class ConfigVO {

    private int configId;

    @NotBlank
    @NotNull
    private String configName;

    private String configDescription;

    @NotBlank
    @NotNull
    private String configValue;

    private String accountId;

    @JsonProperty
    private boolean isEncrypted;

    private ConfigDataType configDataType;

    private ConfigType configType;

    @JsonProperty
    private boolean isMandatory;

    @JsonProperty
    private boolean isEncryptionRequired;

    @JsonProperty
    private boolean isUserAccessible;

    public ConfigDataType getConfigDataType() {
        return configDataType;
    }

    public void setConfigDataType(ConfigDataType configDataType) {
        this.configDataType = configDataType;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public void setConfigType(ConfigType configType) {
        this.configType = configType;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isEncryptionRequired() {
        return isEncryptionRequired;
    }

    public void setEncryptionRequired(boolean encryptionRequired) {
        isEncryptionRequired = encryptionRequired;
    }

    public String getAccountId() {
        return accountId;
    }

    public void setAccountId(String accountId) {
        this.accountId = accountId;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDescription) {
        this.configDescription = configDescription;
    }

    public boolean isEncrypted() {
        return isEncrypted;
    }

    public void setEncrypted(boolean encrypted) {
        isEncrypted = encrypted;
    }

    public String getConfigValue() {
        return configValue;
    }

    public void setConfigValue(String configValue) {
        this.configValue = configValue;
    }

    public boolean isUserAccessible() {
        return isUserAccessible;
    }

    public void setUserAccessible(boolean userAccessible) {
        isUserAccessible = userAccessible;
    }
}
