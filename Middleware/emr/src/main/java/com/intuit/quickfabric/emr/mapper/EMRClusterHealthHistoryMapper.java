package com.intuit.quickfabric.emr.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.ClusterHealthCheckStatusType;
import com.intuit.quickfabric.commons.vo.ClusterHealthStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EMRClusterHealthHistoryMapper implements ResultSetExtractor<List<ClusterHealthStatus>> {

    @Override
    public List<ClusterHealthStatus> extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        List<ClusterHealthStatus> healthStatusList = new ArrayList<>();

        while  (resultSet.next()){
            ClusterHealthStatus healthStatusVO = new ClusterHealthStatus();

            healthStatusVO.setClusterId(resultSet.getString("cluster_id"));
            healthStatusVO.setExecutionId(resultSet.getInt("execution_id"));
            healthStatusVO.setStatus(ClusterHealthCheckStatusType.valueOf(resultSet.getString("status").toUpperCase()));
            healthStatusVO.setTestName(resultSet.getString("name"));
            healthStatusVO.setClusterName(resultSet.getString("cluster_name"));
            healthStatusVO.setClusterType(resultSet.getString("cluster_type"));
            healthStatusVO.setClusterSegment(resultSet.getString("cluster_segment"));
            healthStatusVO.setExecutionStartTime(resultSet.getTimestamp("execution_start_time").toString());
            healthStatusVO.setExecutionEndTime(resultSet.getTimestamp("execution_end_time").toString());
            healthStatusVO.setExecutedBy(resultSet.getString("executed_by"));
            healthStatusVO.setRemark(resultSet.getString("remark"));
            healthStatusVO.setIsMandatory(resultSet.getBoolean("mandatory"));
            healthStatusVO.setExpiresInMinutes(resultSet.getInt("expires_minutes"));
            healthStatusVO.setIsDisabled(resultSet.getBoolean("disabled"));
            healthStatusList.add(healthStatusVO);
        }

        return  healthStatusList;
    }
}