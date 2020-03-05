package com.intuit.quickfabric.emr.mapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.EMRClusterMetricsVO;

public class EMRClusterMetricsRmUrlMapper implements ResultSetExtractor<List<EMRClusterMetricsVO>> {
    
   public List<EMRClusterMetricsVO> extractData(ResultSet rs) throws SQLException,
       DataAccessException {

       List<EMRClusterMetricsVO> eMRClusterMetricsList = new ArrayList<EMRClusterMetricsVO>();
       EMRClusterMetricsVO vo = null;
       while (rs.next()) { 
           vo = new EMRClusterMetricsVO();
           vo.setEmrId(rs.getString("emr_id"));
           vo.setRmUrl(rs.getString("rm_url"));
           vo.setEmrName(rs.getString("emr_name"));
           vo.setAccount(rs.getString("account"));
                                  
           eMRClusterMetricsList.add(vo);
       }

       return eMRClusterMetricsList;
   }

}
