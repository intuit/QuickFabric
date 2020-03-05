package com.intuit.quickfabric.schedulers.mappers;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.ClusterRequest;
import com.intuit.quickfabric.commons.vo.ClusterStepRequest;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClusterStepMapper implements ResultSetExtractor<List<ClusterRequest>> {


	public List<ClusterRequest> extractData(ResultSet rs) throws SQLException,
	DataAccessException {


		List<ClusterRequest> reqList= new ArrayList<ClusterRequest>();
		while (rs.next()) { 

			ClusterRequest req= new ClusterRequest();
			req.setAccount(rs.getString("account"));
			req.setClusterId(rs.getString("cluster_id"));
			req.setClusterName(rs.getString("cluster_name"));
			if(!reqList.contains(req)){
				List<ClusterStepRequest> steps = new ArrayList<ClusterStepRequest>();
				ClusterStepRequest stepVo = new ClusterStepRequest();
				stepVo.setArgs(rs.getString("step_arg"));
				stepVo.setJar(rs.getString("jar"));
				stepVo.setMainClass(rs.getString("main_class"));
				stepVo.setActionOnFailure(rs.getString("action_on_failure"));
				stepVo.setName(rs.getString("name"));
				stepVo.setStepId(rs.getString("step_id"));
				steps.add(stepVo);
				req.getSteps().addAll(steps);
				reqList.add(req);
			}else 
			{
				ClusterStepRequest stepVo = new ClusterStepRequest();
				stepVo.setArgs(rs.getString("step_arg"));
				stepVo.setJar(rs.getString("jar"));
				stepVo.setMainClass(rs.getString("main_class"));
				stepVo.setActionOnFailure(rs.getString("action_on_failure"));
				stepVo.setName(rs.getString("name"));
				stepVo.setStepId(rs.getString("step_id"));
				int i=	reqList.indexOf(req);
				reqList.get(i).getSteps().add(stepVo);

			}


		}

		return reqList;
	}


}
