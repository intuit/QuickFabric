package com.intuit.quickfabric.emr.dao;

import com.intuit.quickfabric.commons.vo.Workflow;

public interface WorkflowDao {

    Workflow getWorkflow(String name);
}
