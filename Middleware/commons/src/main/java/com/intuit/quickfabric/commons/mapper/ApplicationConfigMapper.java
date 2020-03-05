package com.intuit.quickfabric.commons.mapper;

import com.intuit.quickfabric.commons.vo.ConfigDataType;
import com.intuit.quickfabric.commons.vo.ConfigType;
import com.intuit.quickfabric.commons.vo.ConfigVO;
import org.apache.commons.lang3.EnumUtils;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ApplicationConfigMapper implements ResultSetExtractor<List<ConfigVO>>  {
    @Override
    public List<ConfigVO> extractData(ResultSet rs) throws SQLException, DataAccessException {
        List<ConfigVO> configList = new ArrayList<>();

        while (rs.next()) {
            ConfigVO configVO = new ConfigVO();
            configVO.setConfigId(rs.getInt("config_id"));
            configVO.setConfigName(rs.getString("config_name"));
            configVO.setConfigDescription(rs.getString("config_description"));
            configVO.setConfigValue(rs.getString("config_value"));
            configVO.setEncrypted(rs.getBoolean("is_encrypted"));
            configVO.setConfigType(EnumUtils.getEnumIgnoreCase(ConfigType.class, rs.getString("config_type_name")));
            configVO.setConfigDataType(EnumUtils.getEnumIgnoreCase(ConfigDataType.class, rs.getString("data_type_name")));
            configVO.setEncryptionRequired(rs.getBoolean("encryption_required"));
            configVO.setMandatory(rs.getBoolean("is_mandatory"));
            configVO.setUserAccessible(rs.getBoolean("is_user_accessible"));

            configList.add(configVO);
        }
        return configList;
    }
}
