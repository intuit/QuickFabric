package com.intuit.quickfabric.emr.mapper;

import com.intuit.quickfabric.commons.vo.ClusterStep;
import com.intuit.quickfabric.commons.vo.StepResponseVO;
import com.intuit.quickfabric.commons.vo.StepStatus;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ClusterStepResponseMapper implements ResultSetExtractor<StepResponseVO> {


    public StepResponseVO extractData(ResultSet rs) throws SQLException,
            DataAccessException {


        StepResponseVO vo = new StepResponseVO();
        while (rs.next()) {

            ClusterStep stepVo = new ClusterStep();
            if (null == vo.getClusterId()) {
                vo.setClusterId(rs.getString("cluster_id"));
                vo.setClusterName(rs.getString("cluster_name"));
            }

            stepVo.setName(rs.getString("name"));
            
            stepVo.setStatus(StepStatus.valueOf(rs.getString("status")));
            stepVo.setStepType(rs.getString("step_type"));
            // Setting Step ID to Not Applicable for Bootstrap
            if(stepVo.getStepType().equalsIgnoreCase("Bootstrap")) {
                stepVo.setStepId("N/A");
            } else {
                stepVo.setStepId(rs.getString("step_id"));
            }
            stepVo.setCreationTimestamp(rs.getTimestamp("created_ts").toString());
            vo.getSteps().add(stepVo);
        }

        return vo;
    }
}