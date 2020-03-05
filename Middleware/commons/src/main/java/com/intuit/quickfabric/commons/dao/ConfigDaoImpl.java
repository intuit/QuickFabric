package com.intuit.quickfabric.commons.dao;

import com.intuit.quickfabric.commons.mapper.AccountConfigMapper;
import com.intuit.quickfabric.commons.mapper.ApplicationConfigMapper;
import com.intuit.quickfabric.commons.mapper.ConfigDefinitionMapper;
import com.intuit.quickfabric.commons.vo.ConfigDefinitionVO;
import com.intuit.quickfabric.commons.vo.ConfigVO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class ConfigDaoImpl implements ConfigDao {

    private static final Logger logger = LogManager.getLogger(ConfigDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;

    @Override
    public List<ConfigDefinitionVO> getAllConfigurationDefinitions() {
        String SQL = "SELECT qcd.id, qcd.config_name, qct.config_type as config_type_name, qdt.data_type_name, " +
                "qcd.encryption_required, qcd.is_mandatory, qcd.is_user_accessible, qcd.config_description " +
                "FROM configuration_definitions qcd " +
                "join configuration_types qct on qct.id = qcd.config_type " +
                "join configuration_data_types qdt on qdt.id = qcd.data_type";

        logger.info("Retrieving all configs from database");
        List<ConfigDefinitionVO> configs = namedJdbcTemplateObject.query(SQL, new ConfigDefinitionMapper());

        return configs;
    }

    @Override
    public ConfigDefinitionVO getConfigDefinition(String configName) {
        String sql = "SELECT cd.id, cd.config_name, qct.config_type as config_type_name, cdt.data_type_name," +
                " cd.encryption_required, cd.is_mandatory, cd.is_user_accessible, cd.config_description  " +
                " FROM configuration_definitions cd " +
                " join configuration_types qct on qct.id = cd.config_type " +
                " join configuration_data_types cdt on cdt.id = cd.data_type" +
                " where cd.config_name = :config_name ";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("config_name", configName);

        List<ConfigDefinitionVO> configDefinitionVOList = namedJdbcTemplateObject.query(sql, namedParams, new ConfigDefinitionMapper());
        ConfigDefinitionVO configDefinition = configDefinitionVOList.stream().findFirst().orElse(null);

        return configDefinition;
    }

    @Override
    public List<ConfigVO> getAccountConfigs(String accountId) {
        String sql = "select ac.config_id, ac.account_id, ac.config_value, ac.is_encrypted, " +
                " cd.config_name, cd.is_mandatory, cd.encryption_required, cd.config_description, cd.is_user_accessible, " +
                " ct.config_type as config_type_name, cdt.data_type_name" +
                " from account_configurations ac" +
                " join configuration_definitions cd on cd.id = ac.config_id" +
                " join configuration_types ct on ct.id = cd.config_type " +
                " join configuration_data_types cdt on cdt.id = cd.data_type" +
                " where ac.account_id = :accountId";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("accountId", accountId);

        List<ConfigVO> accountConfigs = namedJdbcTemplateObject.query(sql, namedParams, new AccountConfigMapper());

        return accountConfigs;
    }

    @Override
    public void addAccountConfig(int configId, String accountId, String value, boolean isEncrypted) {
        String sql = "insert into account_configurations (config_id, account_id, config_value, is_encrypted) " +
                " values (:configId, :accountId, :configValue, :isEncrypted)";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("configId", configId);
        namedParams.addValue("accountId", accountId);
        namedParams.addValue("configValue", value);
        namedParams.addValue("isEncrypted", isEncrypted);

        int rowsInserted = namedJdbcTemplateObject.update(sql, namedParams);
        logger.info("inserted new account config. Config id:" + configId + " Rows inserted:" + rowsInserted);
    }

    @Override
    public void updateAccountConfig(int configId, String accountId, String value, boolean isEncrypted) {
        String sql = "update account_configurations set config_value =:configValue, is_encrypted =:isEncrypted" +
                " where config_id =:configId and account_id= :accountId";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("configId", configId);
        namedParams.addValue("accountId", accountId);
        namedParams.addValue("configValue", value);
        namedParams.addValue("isEncrypted", isEncrypted);

        int rowsUpdated = namedJdbcTemplateObject.update(sql, namedParams);
        logger.info("updated account config. Config id:" + configId + " Rows updated:" + rowsUpdated);
    }

    @Override
    public List<ConfigVO> getApplicationConfigs() {
        String sql = "select ac.config_id, ac.config_value, ac.is_encrypted," +
                " cd.config_name, cd.is_mandatory, cd.encryption_required, cd.config_description, cd.is_user_accessible, " +
                " ct.config_type as config_type_name, cdt.data_type_name" +
                " from application_configurations ac" +
                " join configuration_definitions cd on cd.id = ac.config_id" +
                " join configuration_types ct on ct.id = cd.config_type " +
                " join configuration_data_types cdt on cdt.id = cd.data_type";

        List<ConfigVO> applicationConfigs = namedJdbcTemplateObject.query(sql, new ApplicationConfigMapper());

        return applicationConfigs;
    }

    @Override
    public void addApplicationConfig(int configId, String value, boolean isEncrypted) {
        String sql = "insert into application_configurations (config_id, config_value, is_encrypted) " +
                " values (:configId, :configValue, :isEncrypted)";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("configId", configId);
        namedParams.addValue("configValue", value);
        namedParams.addValue("isEncrypted", isEncrypted);

        int rowsInserted = namedJdbcTemplateObject.update(sql, namedParams);
        logger.info("inserted new application config. Config id:" + configId + " Rows inserted:" + rowsInserted);
    }

    @Override
    public void updateApplicationConfig(int configId, String value, boolean isEncrypted) {
        String sql = "update application_configurations set config_value =:configValue, is_encrypted =:isEncrypted" +
                " where config_id =:configId";

        MapSqlParameterSource namedParams = new MapSqlParameterSource();
        namedParams.addValue("configId", configId);
        namedParams.addValue("configValue", value);
        namedParams.addValue("isEncrypted", isEncrypted);

        int rowsUpdated = namedJdbcTemplateObject.update(sql, namedParams);
        logger.info("updated application config. Config id:" + configId + " Rows updated:" + rowsUpdated);
    }
}
