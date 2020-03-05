import EMRManagementActionTypes from './actionTypes/EMRManagementActionTypes'
import baseURL from '../api-config';
import Cookies from 'js-cookie';
/**
 * Get Metadata information for all clusters.
 * @param {string} token jwt token
 */
export const fetchAllClusterMetaData = (token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.EMRMETADATA_STATUS_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metadata`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRManagementActionTypes.EMRMETADATA_STATUS_SUCCESS,
          payload: { data: res.emrClusterMetadataReport }

        })
      })
      .catch(error => {
        return dispatch({
          type: EMRManagementActionTypes.EMRMETADATA_STATUS_ERROR,
          payload: { error }
        })
      })
  } 
}

/**
 * Get execution step information for an EMR cluster.
 * @param {string} name EMR name
 * @param {string} id EMR ID
 * @param {string} token jwt token
 */
export const fetchStepsStatusData = (name,id,token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.STEPS_STATUS_FETCHING, payload: {} })
    let steps_url = "";
    
    if(id === undefined){
      steps_url = name
    } else {
      steps_url = id
    }
    fetch(baseURL + `/emr/steps/${steps_url}`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRManagementActionTypes.STEPS_STATUS_SUCCESS,
          payload: { id, data: res.steps }
        })
      })
      .catch(error => {
        return dispatch({
          type: EMRManagementActionTypes.STEPS_STATUS_ERROR,
          payload: { error }
        })
      })
  } 
}

/**
 * Get execution step information for an EMR cluster for the popup modal for an EMR cluster.
 * @param {string} name EMR Name
 * @param {string} id EMR ID
 * @param {string} token jwt token
 */
export const fetchStepsStatusModalData = (name, id, fromWorkflow, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.STEPS_STATUS_MODAL_FETCHING, payload: {} })
    let steps_url = "";
    
    if(id === undefined){
      steps_url = name
    } else {
      steps_url = id
    }

    let url = baseURL + '/emr/steps/' + steps_url;
    if (fromWorkflow) {
      url = url + '?request_from=workflow'
    }

    fetch(url, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRManagementActionTypes.STEPS_STATUS_MODAL_SUCCESS,
          payload: { id, data: res.steps }
        })
      })
      .catch(error => {
        return dispatch({
          type: EMRManagementActionTypes.STEPS_STATUS_MODAL_ERROR,
          payload: { error }
        })
      })
  } 
}
  
/**
 * Get Metadata information for the specified type of EMR.
 * @param {string} type e.g. scheduled, exploratory, transient etc.
 * @param {string} token jwt token
 */
export const fetchClusterWiseMetaData = (type,token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.EMRMETADATA_STATUS_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metadata?cluster_type=${type}`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRManagementActionTypes.EMRMETADATA_STATUS_SUCCESS,
          payload: { data: res.emrClusterMetadataReport }

        })
      })
      .catch(error => {
        return dispatch({
          type: EMRManagementActionTypes.EMRMETADATA_STATUS_ERROR,
          payload: { error }
        })
      })
  } 
}
  
/**
 * Get cluster metadata for the specified AWS account.
 * @param {string} range AWS account number
 * @param {string} token jwt token
 */
export const fetchAccountWiseMetaData = (range,token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.EMRMETADATA_STATUS_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metadata?account=${range}`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRManagementActionTypes.EMRMETADATA_STATUS_SUCCESS,
          payload: { data: res.emrClusterMetadataReport }

        })
      })
      .catch(error => {
        return dispatch({
          type: EMRManagementActionTypes.EMRMETADATA_STATUS_ERROR,
          payload: { error }
        })
      })
  } 
}
  
/**
 * Add steps(jobs) to the cluster.
 * @param {json} data contains cluster and one or more job details
 * @param {string} token jwt token
 */
export const postAddStepsData = (data,token) => {
  token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/emr/management/add-custom-steps', {
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data =>{
        if (res.status !== 200) {
          throw data
        } else {
          return dispatch({
            type: EMRManagementActionTypes.ADDSTEPS_STATUS_SUCCESS,
            payload: {}
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: EMRManagementActionTypes.ADDSTEPS_STATUS_ERROR,
          payload: m
        })
      })
    }
}
  
/**
 * Terminate a cluster.
 * @param {json} data cluster data for termination
 * @param {string} token jwt token
 */
export const postTerminateClusterData = (data,token) => {
  token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/emr/management/terminate', {
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data =>{
        if (res.status !== 200) {
          throw data
        } else {
          return dispatch({
            type: EMRManagementActionTypes.TERMINATECLUSTER_STATUS_SUCCESS,
            payload: {}
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: EMRManagementActionTypes.TERMINATECLUSTER_STATUS_ERROR,
          payload: m
        })
      })
    }
}

/**
 * Rotate AMI for a cluster.
 * @param {json} data cluster and rotation data
 * @param {string} token jwt token
 */
export const postRotateAMI = (data,token) => {
  token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/emr/management/rotate-ami', {
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data =>{
        if (res.status !== 200) {
          throw data
        } else {
          return dispatch({
            type: EMRManagementActionTypes.ROTATEAMI_STATUS_SUCCESS,
            payload: {}
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: EMRManagementActionTypes.ROTATEAMI_STATUS_ERROR,
          payload: m
        })
      })
    }
}

/**
 * Auto rotate AMI for a cluster.
 * @param {json} data cluster and auto rotate ami data
 * @param {string} token jwt token 
 */
export const postAutoRotateAMI = (data,token) => {
  token = Cookies.get('jwt');
    return dispatch => {
      dispatch({ type: EMRManagementActionTypes.AUTOROTATEAMI_STATUS_REQUEST, payload: {} })
      fetch(baseURL + '/emr/autopilot/update-config', {
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data =>{
        if (res.status !== 200) {
          throw data
        } else {
          return dispatch({
            type: EMRManagementActionTypes.AUTOROTATEAMI_STATUS_SUCCESS,
            payload: data
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: EMRManagementActionTypes.AUTOROTATEAMI_STATUS_ERROR,
          payload: m
        })
      })
    }
} 

/**
 * Create new cluster.
 * @param {json} data all cluster data for creation
 * @param {string} token jwt token
 */
export const postCreateClusterData = (data,token) => {
  token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/emr/management/create', { 
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data =>{
        if (res.status !== 200) { 
          throw data
        } else {
          return dispatch({
            type: EMRManagementActionTypes.CREATECLUSTER_STATUS_SUCCESS,
            payload: { data: res }
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: EMRManagementActionTypes.CREATECLUSTER_STATUS_ERROR,
          payload: m
        })
      })
    }
}
  
/**
 * Run test suites for a cluster.
 * @param {json} data cluster and test suite data
 * @param {string} token jwt token
 */
export const postTestSuiteRequest = (data, token) => {
  token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/emr/health/run-check', {
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data => {
        if (res.status !== 200) {
          throw data
        } else {
          return dispatch({
            type: EMRManagementActionTypes.RUN_TESTS_STATUS_SUCCESS,
            payload: {}
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: EMRManagementActionTypes.RUN_TESTS_STATUS_ERROR,
          payload: m
        })
      })
    }
}

/**
 * Get the tests that can be run for the cluster.
 * @param {string} type cluster type
 * @param {string} role cluster role
 * @param {string} token jwt token
 */
export const fetchEMRTestSuites = (type, role, token) => {
  token = Cookies.get('jwt');
  return dispatch => {
    dispatch({ type: EMRManagementActionTypes.EMRTEST_SUITES_FETCHING, payload: {} })
    fetch(baseURL + '/emr/health/test-suites?clusterType=' + type + '&clusterSegment=' + role, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRManagementActionTypes.EMRTEST_SUITES_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRManagementActionTypes.EMRTEST_SUITES_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get status of the tests run on the cluster.
 * @param {string} cluster_id cluster ID
 * @param {string} from_workflow whether the request is coming from a workflow
 * @param {string} token jwt token
 */
export const fetchEMRTestSuitesStatus = (cluster_id, from_workflow, token) => {
  token = Cookies.get('jwt');
  let url = baseURL + '/emr/health/status/' + cluster_id;
  if (from_workflow) {
    url = url + '?request_from=workflow'
  }
  return dispatch => {
    dispatch({ type: EMRManagementActionTypes.EMRTEST_SUITES_STATUS_FETCHING, payload: {} })
    fetch(url, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRManagementActionTypes.EMRTEST_SUITES_STATUS_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRManagementActionTypes.EMRTEST_SUITES_STATUS_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get history of the test suites run on the cluster.
 * @param {string} cluster_id Cluster ID
 * @param {string} token jwt token
 */
export const fetchClusterHealthCheckHistory = (cluster_id, token) => {
  token = Cookies.get('jwt');
  return dispatch => {
    dispatch({ type: EMRManagementActionTypes.CLUSTER_APPS_RUNNING_FETCHING, payload: {} })
    fetch(baseURL + '/emr/health/history/' + cluster_id, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRManagementActionTypes.CLUSTER_HEALTH_CHECK_HISTORY_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRManagementActionTypes.CLUSTER_HEALTH_CHECK_HISTORY_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get the status of the create cluster process.
 * @param {string} record_id ID of the newly initiated cluster
 * @param {string} token jwt token
 */
export const fetchCreateClusterWorkflow = (record_id, token) => {
  token = Cookies.get('jwt');
  return dispatch => {
    dispatch({ type: EMRManagementActionTypes.CREATE_CLUSTER_WORKFLOW_FETCHING, payload: {} })
    fetch(baseURL + `/emr/workflow/CreateCluster/${record_id}`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRManagementActionTypes.CREATE_CLUSTER_WORKFLOW_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRManagementActionTypes.CREATE_CLUSTER_WORKFLOW_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get workflow data for a cluster.
 * @param {string} id Cluster ID
 * @param {string} token jwt token
 */
export const fetchClusterWorkflowData = (id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.REQUEST_CLUSTER_WORKFLOW_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metadata/${id}?request_from=workflow`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRManagementActionTypes.REQUEST_CLUSTER_WORKFLOW_SUCCESS,
          payload: { data: res }

        })
      })
      .catch(error => {
        return dispatch({
          type: EMRManagementActionTypes.REQUEST_CLUSTER_WORKFLOW_ERROR,
          payload: { error }
        })
      })
  } 
}

/**
 * Get workflow data for a cluster.
 * @param {string} id Cluster ID
 * @param {string} token jwt token
 */
export const fetchWorkflowData = (id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.REQUEST_WORKFLOW_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metadata/${id}`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRManagementActionTypes.REQUEST_WORKFLOW_SUCCESS,
          payload: { data: res }

        })
      })
      .catch(error => {
        return dispatch({
          type: EMRManagementActionTypes.REQUEST_WORKFLOW_ERROR,
          payload: { error }
        })
      })
  } 
}
  
/**
 * Get Rotate AMI Workflow information for a cluster.
 * @param {string} cluster_id Cluster ID
 * @param {string} token jwt token
 */
export const fetchRotateAMIWorkflow = (cluster_id, token) => {
  token = Cookies.get('jwt');
  return dispatch => {
    dispatch({ type: EMRManagementActionTypes.ROTATE_AMI_WORKFLOW_FETCHING, payload: {} })
    fetch(baseURL + `/emr/workflow/ROTATEAMI/${cluster_id}`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRManagementActionTypes.ROTATE_AMI_WORKFLOW_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRManagementActionTypes.ROTATE_AMI_WORKFLOW_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Clear errors from the store.
 */
export const clearErrors = () => {
  return {
    type: EMRManagementActionTypes.CLEAR_ERRORS,
    payload: {}
  }
}

/**
 * Flip cluster to production.
 * @param {json} data DNS Flip data
 * @param {string} token jwt token
 */
export const postDNSFlip = (data,token) => {
  token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/emr/management/dns-flip', {
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data =>{
        if (res.status !== 200) {
          throw data
        } else {
          return dispatch({
            type: EMRManagementActionTypes.DNS_FLIP_SUCCESS,
            payload: {}
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: EMRManagementActionTypes.DNS_FLIP_ERROR,
          payload: m
        })
      })
    }
}

/**
 * Auto-terminate on/off.
 * @param {json} data auto-terminate information
 * @param {string} token jwt token
 */
export const postDoTerminate = (data, token) => {
  token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/emr/auto-terminate/update', {
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data => {
        if (res.status !== 200) {
          throw data
        } else {
          return dispatch({
            type: EMRManagementActionTypes.UPDATE_DO_TERMINATE_SUCCESS,
            payload: {}
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: EMRManagementActionTypes.UPDATE_DO_TERMINATE_ERROR,
          payload: m
        })
      })
    }
  }

/**
 * Get cluster data for cloning.
 * @param {string} id Cluster ID
 * @param {string} token jwt token
 */
export const fetchClusterClone = (id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.GET_CLUSTER_CLONE_FETCHING, payload: {} })
    fetch(baseURL + `/emr/clone/${id}`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRManagementActionTypes.GET_CLUSTER_CLONE_SUCCESS,
          payload: { data: res }

        })
      })
      .catch(error => {
        return dispatch({
          type: EMRManagementActionTypes.GET_CLUSTER_CLONE_ERROR,
          payload: { error }
        })
      })
  } 
}

/**
 * Get list of accounts, roles, business owners.
 * @param {string} token jwt token
 */
export const fetchUIDropdownList = (token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.GET_UI_LIST_FETCHING, payload: {} })
    fetch(baseURL + `/populate-ui-list/`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        let listSegmentsData = res.segments.map((u, i) => {
          return u.segmentName
        })
        let listAccountData = res.accounts.map((a, i) => {
          return a.accountId
        })   
        let listActionData = res.actions.map((ac, i) => {
          return ac
        })
        let listSegmentsDataValFormat = res.segments.map((u, i) => {
          return {label: u.segmentName, value: u.segmentName, businessOwner: u.businessOwner, businessOwnerEmail: u.businessOwnerEmail}
        })
        let listAccountDataValFormat = res.accounts.map((a, i) => {
          return {label: a.accountId, value: a.accountId}
        })   
        let listActionDataValFormat = res.actions.map((ac, i) => {
          return {label: ac, value: ac}
        })
        
        let dataResponse = {
          segments: listSegmentsData,
          accounts: listAccountData,
          actions: listActionData,  
          segments_valFormat: listSegmentsDataValFormat,
          accounts_valFormat: listAccountDataValFormat,
          actions_valFormat: listActionDataValFormat,
          response: res
        }
        console.log('GOT UI LIST', dataResponse)
        return dispatch({
          type: EMRManagementActionTypes.GET_UI_LIST_SUCCESS,
          payload: { data: dataResponse }

        })
      })
      .catch(error => {
        return dispatch({
          type: EMRManagementActionTypes.GET_UI_LIST_ERROR,
          payload: { error }
        })
      })
  } 
}

/**
 * Get information about global JIRA enabled or not
 * @param {*} token jwt token
 */
export const fetchGlobalJiraEnabled = (token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.GLOBAL_JIRA_FETCHING, payload:{} })
    fetch(baseURL + `/configurations/decrypt/jira_enabled_global`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRManagementActionTypes.GLOBAL_JIRA_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRManagementActionTypes.GLOBAL_JIRA_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get information about whether JIRA enabled for the account or not
 * @param {*} token jwt token
 */
export const fetchAccountJiraEnabled = (account, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRManagementActionTypes.ACCOUNT_JIRA_FETCHING, payload:{} })
    fetch(baseURL + `/configurations/decrypt/jira_enabled_account/${account}`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRManagementActionTypes.ACCOUNT_JIRA_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRManagementActionTypes.ACCOUNT_JIRA_ERROR,
        payload: { error }
      })
    })
  }
}