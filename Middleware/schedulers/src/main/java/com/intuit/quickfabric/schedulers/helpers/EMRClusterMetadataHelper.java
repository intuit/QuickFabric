package com.intuit.quickfabric.schedulers.helpers;

import com.intuit.quickfabric.commons.vo.ClusterVO;
import com.intuit.quickfabric.schedulers.dao.EMRClusterMetadataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EMRClusterMetadataHelper {

    @Autowired
    EMRClusterMetadataDao emrClusterMetadataDao;

    public ClusterVO getAllEMRClusterDetailsByClusterID(String clusterId) {
        return emrClusterMetadataDao.getAllEMRClusterDataForAMI(clusterId);
    }
}