
package com.intuit.quickfabric.commons.constants;

public class ApiUrls {
    // TODO: Change name to suffix
    public static final String CLUSTER_BOOTSTRAP_TEST_SUFFIX = "/test-service/bootstrap?clusterId={clusterId}";
    public static final String CLUSTER_AUTOSCALING_TEST_SUFFIX = "/test-service/autoscaling?clusterId={clusterId}&instanceGroup={instanceGroup}";
    public static final String CLUSTER_HEALTH_CHECK_SUFFIX = "/v1/emr/test";
    
    public static final String PRECHECK_CLUSTER_URL_SUFFIX = "/emr-service/pre-check?clusterName={clusterName}";
    public static final String CREATE_CLUSTER_URL_SUFFIX = "/emr-service/create";
    public static final String CLUSTER_STATUS_CHECK_URL_SUFFIX = "/emr-service/status?clusterId={clusterId}";
    public static final String CLUSTER_VALIDATE_URL_SUFFIX = "/emr-service/validate?clusterId={clusterId}";
    public static final String TERMINATE_CLUSTER_URL_SUFFIX = "/emr-service/terminate";
    public static final String ADD_CUSTOM_STEP_URL_SUFFIX = "/emr-service/add-custom-steps";
    public static final String VALIDATE_STEP_URL_SUFFIX = "/emr-service/validate-steps";
    public static final String EMR_AUTOSCALING_ADD_URL = "/autoscaling/add";
    public static final String DNS_FLIP_URL = "/dns-service";
    public static final String EMR_COST_URL = "/emr-service/cost-usage?clusterName={clusterName}";
    
    public static final String RM_PROXY_URL_SUFFIX = "/emr-service/rm-proxy";


    
    public static final String LOGIN_SERVICE_PATH = "/login";
    public static final String SSO_REDIRECT_PATH = "/login/sso/redirect";
    public static final String GET_USER_ROLES_SERVICE_PATH = "/user/role";
    public static final String JIRA_ISSUE_PATH = "/rest/api/latest/issue/";
    public static final String SNOW_TABLE_PATH = "/api/now/table/u_incident_task";
	
}

