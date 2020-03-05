package com.intuit.quickfabric.emr.service;

import com.intuit.quickfabric.commons.utils.CommonUtils;
import com.intuit.quickfabric.commons.vo.Workflow;
import com.intuit.quickfabric.emr.helper.WorkflowHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/emr/workflow")
public class WorkflowService {

    private final Logger logger = LogManager.getLogger(WorkflowService.class);

    @Autowired
    WorkflowHelper workflowHelper;

    /** Get details of the workflow for the given cluster and workflow. 
     * @param workflowName one of: CREATECLUSTER, ROTATEAMI
     * @param metadataId the QuickFabric metadata ID of the cluster to get workflow for
     * @return
     */
    @CrossOrigin(origins = "*", allowedHeaders = "*")
    @GetMapping(value = "/{workflow_name}/{metadata_id}")
    @PreAuthorize("hasAnyAuthority('admin', 'superadmin','read')")
    public @ResponseBody
    ResponseEntity<Workflow> getWorkflow(
            @PathVariable("workflow_name") String workflowName,
            @PathVariable("metadata_id") long metadataId) {
        try {
            Workflow workflow = workflowHelper.selectWorkflow(workflowName, metadataId);
            return ResponseEntity.ok(workflow);
        } catch (Exception ex) {
            logger.error(ex.getMessage(), ex);
            return CommonUtils.createErrorResponse(ex);
        }
    }
}
