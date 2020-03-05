package com.intuit.quickfabric.emr.mapper;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.intuit.quickfabric.commons.vo.Workflow;
import com.intuit.quickfabric.commons.vo.WorkflowStep;
import com.intuit.quickfabric.commons.vo.WorkflowType;

import java.sql.ResultSet;
import java.sql.SQLException;


public class WorkflowMapper implements ResultSetExtractor<Workflow> {

    @Override
    public Workflow extractData(ResultSet resultSet) throws SQLException, DataAccessException {
        Workflow workflow = new Workflow();

        while (resultSet.next()) {
            workflow.setName(resultSet.getString("workflow_name"));
            WorkflowStep workflowStep = new WorkflowStep();
            workflowStep.setWorkflowType(WorkflowType.valueOf(resultSet.getString("workflow_step").toUpperCase()));
            workflowStep.setTableName(resultSet.getString("lookup_table"));
            workflow.addStep(workflowStep);
        }

        return workflow;
    }
}
