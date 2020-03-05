package com.intuit.quickfabric.commons.dao;

import java.util.List;

import com.intuit.quickfabric.commons.vo.ConfigVO;
import com.intuit.quickfabric.commons.vo.ConfigDefinitionVO;

public interface ConfigDao {

    List<ConfigDefinitionVO> getAllConfigurationDefinitions();

    ConfigDefinitionVO getConfigDefinition(String configName);

    List<ConfigVO> getAccountConfigs(String accountId);

    void addAccountConfig(int configId, String accountId, String value, boolean isEncrypted);

    void updateAccountConfig(int configId, String accountId, String value, boolean isEncrypted);

    List<ConfigVO> getApplicationConfigs();

    void addApplicationConfig(int configId, String value, boolean isEncrypted);

    void updateApplicationConfig(int configId, String value, boolean isEncrypted);
}
