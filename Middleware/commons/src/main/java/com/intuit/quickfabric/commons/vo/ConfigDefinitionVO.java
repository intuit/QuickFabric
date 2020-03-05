package com.intuit.quickfabric.commons.vo;

public class ConfigDefinitionVO {

    private ConfigType configType;
    private int configId;
    private String configName;
    private String configDescription;
    private ConfigDataType configDataType;
    private boolean isMandatory;
    private boolean isEncryptionRequired;
    private boolean isUserAccessible;

    public void setConfigId(int configId) {
        this.configId = configId;
    }

    public int getConfigId() {
        return configId;
    }

    public void setConfigType(ConfigType configType) {
        this.configType = configType;
    }

    public ConfigType getConfigType() {
        return configType;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigDataType(ConfigDataType configDataType) {
        this.configDataType = configDataType;
    }

    public ConfigDataType getConfigDataType() {
        return configDataType;
    }

    public boolean isEncryptionRequired() {
        return isEncryptionRequired;
    }

    public void setEncryptionRequired(boolean encryptionRequired) {
        isEncryptionRequired = encryptionRequired;
    }

    public boolean isMandatory() {
        return isMandatory;
    }

    public void setMandatory(boolean mandatory) {
        isMandatory = mandatory;
    }

    public boolean isUserAccessible() {
        return isUserAccessible;
    }

    public void setUserAccessible(boolean userAccessible) {
        isUserAccessible = userAccessible;
    }

    public String getConfigDescription() {
        return configDescription;
    }

    public void setConfigDescription(String configDescription) {
        this.configDescription = configDescription;
    }

    @Override
    public String toString() {
        return "ConfigDefinitionVO{" +
                "configType=" + configType +
                ", configId=" + configId +
                ", configName='" + configName + '\'' +
                ", configDescription='" + configDescription + '\'' +
                ", configDataType=" + configDataType +
                ", isMandatory=" + isMandatory +
                ", isEncryptionRequired=" + isEncryptionRequired +
                ", isUserAccessible=" + isUserAccessible +
                '}';
    }
}
