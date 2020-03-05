package com.intuit.quickfabric.emr.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.ClusterHealthCheckStatusType;
import com.intuit.quickfabric.commons.vo.ClusterHealthStatus;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EMRClusterHealthStatusMapper implements ResultSetExtractor<List<ClusterHealthStatus>> {

    @Override
    public List<ClusterHealthStatus> extractData(ResultSet resultSet) throws SQLException, DataAccessException {

        List<ClusterHealthStatus> healthStatusList = new ArrayList<>();

        while (resultSet.next()) {
            ClusterHealthStatus clusterHealthStatus = new ClusterHealthStatus();
            clusterHealthStatus.setClusterId(resultSet.getString("cluster_id"));
            clusterHealthStatus.setExecutionId(resultSet.getInt("execution_id"));
            clusterHealthStatus.setStatus(ClusterHealthCheckStatusType.valueOf(resultSet.getString("status").toUpperCase()));
            clusterHealthStatus.setTestName(resultSet.getString("name"));
            clusterHealthStatus.setClusterName(resultSet.getString("cluster_name"));
            clusterHealthStatus.setClusterType(resultSet.getString("cluster_type"));
            clusterHealthStatus.setClusterSegment(resultSet.getString("cluster_segment"));
            clusterHealthStatus.setExecutionStartTime(resultSet.getTimestamp("execution_start_time").toString());
            clusterHealthStatus.setExecutionEndTime(resultSet.getTimestamp("execution_end_time").toString());
            clusterHealthStatus.setExecutedBy(resultSet.getString("executed_by"));
            clusterHealthStatus.setRemark(resultSet.getString("remark"));
            clusterHealthStatus.setIsMandatory(resultSet.getBoolean("mandatory"));
            clusterHealthStatus.setExpiresInMinutes(resultSet.getInt("expires_minutes"));
            clusterHealthStatus.setIsDisabled(resultSet.getBoolean("disabled"));

            healthStatusList.add(clusterHealthStatus);
        }

        return healthStatusList;
    }
}