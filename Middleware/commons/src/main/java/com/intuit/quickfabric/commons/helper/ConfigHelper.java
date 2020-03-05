package com.intuit.quickfabric.commons.helper;

import com.intuit.quickfabric.commons.dao.ConfigDao;
import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricServerException;
import com.intuit.quickfabric.commons.security.AccessControl;
import com.intuit.quickfabric.commons.vo.ConfigDataType;
import com.intuit.quickfabric.commons.vo.ConfigDefinitionVO;
import com.intuit.quickfabric.commons.vo.ConfigType;
import com.intuit.quickfabric.commons.vo.ConfigVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jasypt.util.text.AES256TextEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Component
public class ConfigHelper {

    private final Logger logger = LogManager.getLogger(ConfigHelper.class);

    @Autowired
    private ConfigDao configDao;

    @Value("${aesencryptor.secret}")
    private String aesSecret;

    public ConfigVO getConfig(String configName, String accountId) {
        if (StringUtils.isBlank(configName)) {
            throw new QuickFabricClientException("config name or config value is null. configName:" + configName);
        }

        ConfigDefinitionVO configDefinition = configDao.getConfigDefinition(configName);
        if (configDefinition == null) {
            throw new QuickFabricClientException("config name not found. config name:" + configName);
        }

        if (configDefinition.getConfigType() == ConfigType.Account) {
            if (StringUtils.isBlank(accountId)) {
                logger.info("no account id passed for config:" + configName);
                throw new QuickFabricClientException("account id not found for config:" + configName);
            }
        }

        if (!AccessControl.hasConfigAccess(configDefinition)) {
            throw new QuickFabricServerException("User does not have access to config:" + configName);
        }

        ConfigVO configVO;
        if (configDefinition.getConfigType() == ConfigType.Account) {
            configVO = configDao.getAccountConfigs(accountId)
                    .stream().filter(x -> x.getConfigName().equalsIgnoreCase(configName))
                    .findFirst().orElse(null);
        } else {
            configVO = configDao.getApplicationConfigs()
                    .stream().filter(x -> x.getConfigName().equalsIgnoreCase(configName))
                    .findFirst().orElse(null);
        }

        return configVO;
    }

    public <T> T getConfigValue(String configName, String accountId) {
        logger.info("getConfigValue config:" + configName + " accountId:" + accountId);

        ConfigDefinitionVO configDefinition = configDao.getConfigDefinition(configName);
        if (configDefinition == null) {
            throw new QuickFabricClientException("config not found. config name:" + configName);
        }

        String configValue;
        ConfigVO configVO;
        if (configDefinition.getConfigType() == ConfigType.Account) {
            if (StringUtils.isBlank(accountId)) {
                logger.info("no account id passed for config:" + configName);
                throw new QuickFabricClientException("account id required for config:" + configName);
            }

            configVO = configDao.getAccountConfigs(accountId)
                    .stream().filter(x -> x.getConfigName().equalsIgnoreCase(configName))
                    .findFirst().orElse(null);

            if (configVO == null || StringUtils.isBlank(configVO.getConfigValue())) {
                throw new QuickFabricClientException("no config value found for account config:" + configName + " and accountId:" + accountId);
            }
            configValue = configVO.getConfigValue();
        } else {
            configVO = configDao.getApplicationConfigs()
                    .stream().filter(x -> x.getConfigName().equalsIgnoreCase(configName))
                    .findFirst().orElse(null);

            if (configVO == null || StringUtils.isBlank(configVO.getConfigValue())) {
                throw new QuickFabricClientException("no config value found for application config:" + configName);
            }

            configValue = configVO.getConfigValue();
        }

        if (configVO.isEncrypted()) {
            configValue = decryptText(configValue);
        }

        switch (configDefinition.getConfigDataType()) {
            case Boolean:
                return (T) Boolean.valueOf(configValue);

            case Int:
                return (T) Integer.valueOf(configValue);

            case String:
                return (T) configValue;

            case DateTime:
            case Date:
                //VSTODO: localdate vs date
                return (T) LocalDate.parse(configValue);

            case Long:
                return (T) Long.valueOf(configValue);

            case Decimal:
                return (T) Double.valueOf(configValue);
        }

        return null;
    }

    public <T> T getConfigValue(String configName) {
        return getConfigValue(configName, null);
    }

    public List<ConfigDefinitionVO> getAllConfigurationDefinitions() {
        List<ConfigDefinitionVO> configDefinitions = configDao.getAllConfigurationDefinitions();
        return configDefinitions;
    }

    public void addConfig(ConfigVO configVO) {
        if (StringUtils.isBlank(configVO.getConfigName()) || StringUtils.isBlank(configVO.getConfigValue())) {
            throw new QuickFabricClientException("config name or config value is null. configName:" + configVO.getConfigName()
                    + " configValue:" + configVO.getConfigValue());
        }

        // get definition from db
        ConfigDefinitionVO configDefinition = configDao.getConfigDefinition(configVO.getConfigName());
        if (configDefinition == null) {
            throw new QuickFabricClientException("config name not found. config name:" + configVO.getConfigName());
        }

        if (configDefinition.getConfigType() == ConfigType.Account) {
            if (StringUtils.isBlank(configVO.getAccountId())) {
                logger.info("no account id passed for config:" + configVO.getConfigName());
                throw new QuickFabricClientException("account id not found for config:" + configVO.getConfigName());
            }
        }

        // validation
        String value = configVO.getConfigValue();
        if (configDefinition.getConfigDataType() == ConfigDataType.Boolean) {
            if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
                throw new QuickFabricClientException("boolean value is not correct. value:" + configVO.getConfigValue());
            }
        } else if (configDefinition.getConfigDataType() == ConfigDataType.Int) {
            Integer.parseInt(value);
        } else if (configDefinition.getConfigDataType() == ConfigDataType.Decimal) {
            Double.parseDouble(value);
        } else if (configDefinition.getConfigDataType() == ConfigDataType.Long) {
            Long.parseLong(value);
        } else if (configDefinition.getConfigDataType() == ConfigDataType.Date || configDefinition.getConfigDataType() == ConfigDataType.DateTime) {
            Date.valueOf(value);
        }

        boolean isEncrypted = configDefinition.isEncryptionRequired() || configVO.isEncrypted();
        if (isEncrypted) {
            value = encryptText(value);
        }

        // save or update
        if (configDefinition.getConfigType() == ConfigType.Account) {
            ConfigVO existingConfig = configDao.getAccountConfigs(configVO.getAccountId())
                    .stream().filter(x -> x.getConfigName().equalsIgnoreCase(configVO.getConfigName()))
                    .findFirst().orElse(null);
            if (existingConfig == null) {
                logger.info("adding new account config. configName:{} accountId:{}", configVO.getConfigName(), configVO.getAccountId());
                configDao.addAccountConfig(configDefinition.getConfigId(), configVO.getAccountId(), value, isEncrypted);
            } else {
                logger.info("update existing account config. configName:{} accountId:{}", configVO.getConfigName(), configVO.getAccountId());
                configDao.updateAccountConfig(configDefinition.getConfigId(), configVO.getAccountId(), value, isEncrypted);
            }
        } else {
            ConfigVO existingConfig = configDao.getApplicationConfigs()
                    .stream().filter(x -> x.getConfigName().equalsIgnoreCase(configVO.getConfigName()))
                    .findFirst().orElse(null);
            if (existingConfig == null) {
                logger.info("adding new application config. configName:{} accountId:{}", configVO.getConfigName(), configVO.getAccountId());
                configDao.addApplicationConfig(configDefinition.getConfigId(), value, isEncrypted);
            } else {
                logger.info("update existing application config. configName:{} accountId:{}", configVO.getConfigName(), configVO.getAccountId());
                configDao.updateApplicationConfig(configDefinition.getConfigId(), value, isEncrypted);
            }
        }
    }

    public ConfigVO decryptConfig(String configName, String accountId) {
        logger.info("decrypt config called for config:" + configName + " accountId:" + accountId);

        ConfigVO config = getConfig(configName, accountId);
        if (config.isEncrypted()) {
            logger.info("decrypting config:" + configName + " accountId:" + accountId);
            config.setConfigValue(decryptText(config.getConfigValue()));
        }

        return config;
    }

    private String encryptText(String plainText) {
        AES256TextEncryptor e = new AES256TextEncryptor();
        e.setPassword(aesSecret);
        String encryptedText = e.encrypt(plainText);
        return encryptedText;
    }

    private String decryptText(String encryptedText) {
        AES256TextEncryptor e = new AES256TextEncryptor();
        e.setPassword(aesSecret);
        String decryptedText = e.decrypt(encryptedText);
        return decryptedText;
    }


    public String getAccountSpecificUrl(String accountId, String path) {
        String apiGateway = this.getConfigValue("gateway_api_url", accountId);
        return apiGateway + path;
    }


    public List<ConfigVO> getAllConfigurations(String accountId, boolean appendMissingConfigurations) {
        List<ConfigVO> result;
        if (StringUtils.isBlank(accountId)) {
            result = configDao.getApplicationConfigs();
            if (appendMissingConfigurations) {
                addPendingConfigurations(result, null);
            }
        } else {
            result = configDao.getAccountConfigs(accountId);
            if (appendMissingConfigurations) {
                addPendingConfigurations(result, accountId);
            }
        }

        return result;
    }

    private void addPendingConfigurations(List<ConfigVO> existingConfigs, String accountId) {
        List<ConfigDefinitionVO> configDefinitions = configDao.getAllConfigurationDefinitions();
        List<Integer> existingConfigIds = existingConfigs.stream().map(x -> x.getConfigId()).collect(Collectors.toList());
        ConfigType configType = StringUtils.isBlank(accountId) ? ConfigType.Application : ConfigType.Account;
        Stream<ConfigDefinitionVO> missingConfigs = configDefinitions.stream().filter(x -> !existingConfigIds.contains(x.getConfigId())
                && x.getConfigType() == configType);
        existingConfigs.addAll(missingConfigs.map(x -> getConfigVo(x, accountId)).collect(Collectors.toList()));
    }

    private ConfigVO getConfigVo(ConfigDefinitionVO definition, String accountId) {
        ConfigVO configVO = new ConfigVO();
        configVO.setConfigId(definition.getConfigId());
        configVO.setConfigName(definition.getConfigName());
        configVO.setConfigDescription(definition.getConfigDescription());
        configVO.setAccountId(accountId);
        configVO.setConfigDataType(definition.getConfigDataType());
        configVO.setConfigType(definition.getConfigType());
        configVO.setMandatory(definition.isMandatory());
        configVO.setEncryptionRequired(definition.isEncryptionRequired());
        configVO.setUserAccessible(definition.isUserAccessible());

        return configVO;
    }
}




