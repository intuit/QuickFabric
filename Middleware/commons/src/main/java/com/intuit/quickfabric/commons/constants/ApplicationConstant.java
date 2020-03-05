package com.intuit.quickfabric.commons.constants;

import java.util.HashMap;
import java.util.Map;

public class ApplicationConstant {
    public static final String CLUSTER_METRICS = "clusterMetrics";
    public static final String EMR_STATUS = "emrStatus";
    public static final String REFRESH_DATE = "metricsFetchDate";
    public static final String CLUSTER_CREATE_DATE = "clusterCreateDate";
    public static final String CLUSTER_TYPE = "clusterTtype";
    public static final String RM_URL = "rmUrl";
    public static final String EMR_NAME = "emrName";
    public static final String EMR_ID = "emrId";
    public static final String JSON_EXTN = ".json";
    public static final String UNDERSCORE = "_";
    public static final String METRICS = "metrics_";
    public static final String TOTAL_VIRTUAL_CORES = "totalVirtualCores";
    public static final String AVAILABLE_VIRTUAL_CORES = "availableVirtualCores";
    public static final String TOTAL_MB = "totalMB";
    public static final String AVAILABLE_MB = "availableMB";
    public static final String ACTIVE_NODES = "activeNodes";
    public static final String FSLASH = "/";
    public static final String EMR_COST_DATA = "costsummary";
    public static final String CSV = ".csv000";
    public static final String ADHOC_CARE_EMR = "Adhoc-care-EMR";
    public static final String USER_ATTRIBUTE = "user";
    public static final String USER_ID_KEY = "userid";
    public static final String IV = "IV";
    public static final String RETURN_VALUES = "ReturnValues";
    public static final String FNAME = "givenname";
    public static final String LNAME = "familyname";
    public static final String USER_ID = "userid";
    public static final String COSTSUMMARY_MONTHLY_DETAIL = "costsummary_monthly_detail";
    public static final String CONTAINER_PENDING = "containersPending";
    public static final String APPS_PENDING = "appsPending";
    public static final String APPS_RUNNING = "appsRunning";
    public static final String ACCOUNT_ID = "accountId";




    public static final String REGION = "us-west-2";

    //Metrics collector configs
    public static final String METRICS_TIMESTAMP_FORMAT = "MM-dd-yyyy HH:mm:ss";

    public static final Map<String, String> YARN_APP_HEURISTICS = createMap();
    public static final int TERMINATED_CLUSTER_CLEAN_DAYS_AGO = 180;

    private static Map<String, String> createMap() {
        Map<String,String> map = new HashMap<String,String>();

        map.put("Mapper Memory", "The problem indicates you requested large task memory, but the task average used physical memory is low. You should try to decrease hive.tez.container.size/tez.am.resource.memory.mb and hive.tez.java.opts (80% of container size). If you get OutOfMemory error, think about why such error happens before increasing the memory back again.");
        map.put("Reducer Memory", "The problem indicates you requested large task memory (set mapreduce.map(or reduce).memory.mb > 2048), but the task average used physical memory is low. You should try to decrease mapreduce.map(or reduce).memory.mb. If you get OutOfMemory error, think about why such error happens before increase the memory back again. For example, see if there is any mapper/reducer input data skew (some task processes larger input than others) Also check if you are doing memory intensive operation at reducer side for e.g. count distinct. These operations can be rewritten using Secondary Sort concept (sort the values), which will reduce the memory consumption");

        map.put("Tez Mapper Time", "This analysis shows how well the number of input tasks in Tez job is adjusted. This should allow you to better tweak the number of input tasks for your job.You should tune mapper split size to reduce number of mappers and let each mapper process larger data");
        map.put("Tez Mapper Memory", "The problem indicates you requested large task memory, but the task average used physical memory is low. You should try to decrease hive.tez.container.size/tez.am.resource.memory.mb and hive.tez.java.opts (80% of container size). If you get OutOfMemory error, think about why such error happens before increasing the memory back again.");
        map.put("Tez Reducer Time", "This happens when the Hadoop job has a large/small number of reducers and short/long reducer runtime. Set the number of reducers by specifying a better number in your Hadoop job. For Hive jobs this can be done using set mapred.reduce.tasks = 38; For Apache-Pig jobs: Use PARALLEL [num] clause on the operator which caused this job. It is better to let Tez determine the number of reducers. set mapred.reduce.tasks = -1;");
        map.put("Tez Reducer Data Skew", "This analysis shows whether there is a data/time-skew for the data/time entering reducer tasks. This result of the analysis shows two groups of the spectrum, where the first group has significantly less input data compared to the second group. This is often caused by skew in the keyspace (aggregation key for group by, join key for joins). If using Pig, try a skew join. Otherwise, consider what you're doing in the job and if there's a better way to do it.");

        return map;
    }

}
