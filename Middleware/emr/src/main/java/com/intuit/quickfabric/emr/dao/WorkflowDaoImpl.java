package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.vo.Workflow;
import com.intuit.quickfabric.emr.mapper.WorkflowMapper;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class WorkflowDaoImpl implements  WorkflowDao {

    private final Logger logger = LogManager.getLogger(WorkflowDaoImpl.class);

    @Autowired
    NamedParameterJdbcTemplate namedJdbcTemplateObject;

    @Override
    public Workflow getWorkflow(String name) {
         logger.info("Getting workflow steps for " + name);

        String sql = "select * from workflow" +
                " where workflow_name = :workflowName" +
                " order by id";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("workflowName", name);

        Workflow workflow = namedJdbcTemplateObject.query(sql, parameters, new WorkflowMapper());
        return workflow;
    }
}
