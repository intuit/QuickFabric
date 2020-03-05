package com.intuit.quickfabric.emr.helper;

import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.intuit.quickfabric.commons.domain.QuickFabricAuthenticationToken;
import com.intuit.quickfabric.commons.exceptions.QuickFabricClientException;
import com.intuit.quickfabric.commons.exceptions.QuickFabricJsonException;
import com.intuit.quickfabric.commons.helper.ConfigHelper;
import com.intuit.quickfabric.commons.vo.ClusterRequest;

@Component
public class TicketValidationHelper {

    private static final Logger logger = LogManager.getLogger(TicketValidationHelper.class);

    
    @Autowired
    private ConfigHelper configHelper;

    @Autowired
    EMRAWSServiceCallerHelper caller;

    public void validRequestTicket(ClusterRequest clusterDetails) {
        boolean validateJira;
        boolean validateSnow;
       
            boolean globalJira = configHelper.getConfigValue("jira_enabled_global");
            boolean accountJira = configHelper.getConfigValue("jira_enabled_account", clusterDetails.getAccount());
            validateJira = globalJira && accountJira;

            boolean globalSnow = configHelper.getConfigValue("servicenow_enabled_global");
            boolean accountSnow = configHelper.getConfigValue("servicenow_enabled_account", clusterDetails.getAccount());
            validateSnow = globalSnow && accountSnow;
        if(validateJira) {
        	jiraTicketApproved(clusterDetails.getJiraTicket(), clusterDetails.getAccount());
        }else if(validateSnow) {
        	validServiceNowTicket(clusterDetails.getSnowTicket(), clusterDetails.getAccount());
        }
       
    }

    private void jiraTicketApproved(String ticketId, String account) {
        logger.info("Validating JIRA ticket {} for cluster action...", ticketId);

        if (StringUtils.isBlank(ticketId)) {
            String msg = "Missing JIRA ticketId";
            logger.error(msg);
            throw new QuickFabricClientException(msg);
         }
        String projectsString = configHelper.getConfigValue("jira_projects", account);

        //This ticket is not in a valid project, don't even bother making the call here.
        if (!Arrays.asList(projectsString.split(",")).stream()
                .anyMatch(project -> ticketId.contains(project))) {
            String msg = "Ticket must be in one of following projects: " + projectsString;
            logger.error(msg);
            throw new QuickFabricClientException(msg);
        }
        String response = caller.invokeGetJiraTicketService(ticketId, account);
        try {
            JSONObject ticket = new JSONObject(response).getJSONObject("fields");

            String assigneeEmail = ticket.getJSONObject("assignee").getString("emailAddress");

            QuickFabricAuthenticationToken auth =
                    (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            logger.info("Assignee of {}: {}. Current User: {}", ticketId, assigneeEmail, auth.getEmail());

            boolean correctUser = assigneeEmail.equalsIgnoreCase(auth.getEmail());
            if (!correctUser) {
            	logger.error("You must be assigned the ticket associated with this action");
                throw new QuickFabricClientException("You must be assigned the ticket associated with this action");
            }

            boolean ticketApproved = ticketIsApproved(ticket);
            logger.info("Has someone approved this action? Answer: {}", ticketApproved);
            if (!ticketApproved) {
            	logger.error("Ticket must be approved before action can be completed. \"\n" + 
            			"                        + \"Please reach out to this request's approver(s) for approval.");
                throw new QuickFabricClientException("Ticket must be approved before action can be completed. \"\n" + 
                		                             "Please reach out to this request's approver(s) for approval.");

               
            }


        } catch (JSONException e) {
            logger.info("Response JSON: " + response);
            logger.error("Failed to extract state information for ticket {}. "
                    + "Reason: {}", ticketId, e.getMessage());
            throw new QuickFabricJsonException("Failed to extract state information for ticket:"+ticketId+", Reason:"+e.getMessage(), e);
        }

        logger.info("Is {} a valid ticket? Answer: {}", ticketId, true);

        
    }

    private boolean ticketIsApproved(JSONObject ticket) throws JSONException {
        JSONArray comments = ticket.getJSONObject("comment").getJSONArray("comments");

        //Iterate through whole list to log all approvers.
        boolean approved = false;
        for (int i = 0; i < comments.length(); i++) {
            JSONObject comment = comments.getJSONObject(i);
            if (comment.getString("body").toLowerCase().contains("approved")) {
                String approver = comment.getJSONObject("author").getString("emailAddress");
                logger.info("Ticket has been approved by {}", approver);
                approved = true;
            }
        }

        return approved;
    }

    private void validServiceNowTicket(String ticketId, String account) {
        logger.info("Validating SNOW ticket for cluster action...");

        if (StringUtils.isBlank(ticketId)) {
            logger.error("Missing SNOW ticketId, returning `false` for is valid Ticket");
            throw new QuickFabricClientException("Missing SNOW ticketId, returning `false` for is valid Ticket");
        }

        String response = caller.invokeGetServiceNowTicketService(ticketId, account).getBody();
       
        try {
            JSONObject ticket = new JSONObject(response).getJSONArray("result").getJSONObject(0);

            logger.info("Value of 'state' for {}: {}", ticketId, ticket.getString("state"));

            boolean correctState = ticket.getString("state").equals("Closed Complete") ||
                    ticket.getString("state").equals("Accepted") ||
                    ticket.getString("state").equals("Work In Progress");

            if (!correctState) {
                throw new QuickFabricClientException("Ticket state must be one of: Accepted, Work In Progress, Closed Complete");      
            }

            QuickFabricAuthenticationToken auth = (QuickFabricAuthenticationToken) SecurityContextHolder.getContext().getAuthentication();

            boolean correctUser = auth.getEmail().equalsIgnoreCase(ticket.getString("assigned_to.email"));

            logger.info("Assigneee of {}: {}. Current User: {}",
                    ticketId, ticket.getString("assigned_to.email"), auth.getEmail());

            if (!correctUser) {
                throw new QuickFabricClientException("You must be assigned the ticket associated with this action");      
            }

        } catch (JSONException e) {
            logger.info("Response JSON: " + response);
            logger.error("Failed to extract state information for ticket {}. Reason: {}",
                    ticketId, e.getMessage());
            throw new QuickFabricJsonException("Failed to extract state information for ticket:"+ticketId+" , Reason:"+e.getMessage(),e);      

        }

        logger.info("Is {} a real ticket in an approved state? Answer: {}", ticketId, true);

    }

   

}
