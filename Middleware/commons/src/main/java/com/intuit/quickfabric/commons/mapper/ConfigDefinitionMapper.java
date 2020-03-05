package com.intuit.quickfabric.commons.mapper;

import com.intuit.quickfabric.commons.vo.ConfigDataType;
import com.intuit.quickfabric.commons.vo.ConfigDefinitionVO;
import com.intuit.quickfabric.commons.vo.ConfigType;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ConfigDefinitionMapper implements ResultSetExtractor<List<ConfigDefinitionVO>> {

    @Override
    public List<ConfigDefinitionVO> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<ConfigDefinitionVO> result = new ArrayList<>();
        while (rs.next()) {
            ConfigDefinitionVO configDefinition = new ConfigDefinitionVO();
            configDefinition.setConfigId(rs.getInt("id"));
            configDefinition.setConfigName(rs.getString("config_name"));
            configDefinition.setConfigDescription(rs.getString("config_description"));
            configDefinition.setConfigType(EnumUtils.getEnumIgnoreCase(ConfigType.class, rs.getString("config_type_name")));
            configDefinition.setConfigDataType(EnumUtils.getEnumIgnoreCase(ConfigDataType.class, rs.getString("data_type_name")));
            configDefinition.setEncryptionRequired(rs.getBoolean("encryption_required"));
            configDefinition.setMandatory(rs.getBoolean("is_mandatory"));
            configDefinition.setUserAccessible(rs.getBoolean("is_user_accessible"));

            result.add(configDefinition);
        }

        return result;
    }
}
