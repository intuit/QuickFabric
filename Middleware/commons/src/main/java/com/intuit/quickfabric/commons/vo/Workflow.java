package com.intuit.quickfabric.commons.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Workflow {

    private String name;

    @JsonProperty("steps")
    private List<WorkflowStep> workflowSteps;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<WorkflowStep> getWorkflowSteps() {
        return workflowSteps;
    }

    public void setWorkflowSteps(List<WorkflowStep> workflowSteps) {
        this.workflowSteps = workflowSteps;
    }

    public void addStep(WorkflowStep workflowStep) {
        if (workflowSteps == null) {
            workflowSteps = new ArrayList<>();
        }
        workflowSteps.add(workflowStep);
    }

    public WorkflowStep getStep(WorkflowType workflowType) {
        if (workflowSteps == null) {
            return null;
        }

        Optional<WorkflowStep> step = workflowSteps.stream().filter(x -> x.getWorkflowType() == workflowType).findFirst();
        if (step.isPresent())
            return step.get();
        else
            return null;
    }

    public boolean removeStep(WorkflowType workflowType) {
        Optional<WorkflowStep> workflowStep = workflowSteps.stream()
                .filter(x -> x.getWorkflowType() == workflowType).findFirst();
        if (workflowStep.isPresent()) {
            workflowSteps.remove(workflowStep.get());
            return true;
        }

        return false;
    }

    public void mergeSteps(List<WorkflowStep> sourceSteps) {
        if (sourceSteps == null || sourceSteps.size() == 0) {
            return;
        }

        for (WorkflowStep sourceStep : sourceSteps) {
            Optional<WorkflowStep> destinationStep = workflowSteps.stream().filter(x -> x.getWorkflowType() == sourceStep.getWorkflowType()).findFirst();
            if (destinationStep.isPresent()) {
                BeanUtils.copyProperties(sourceStep, destinationStep.get());
            }
        }
    }
}
