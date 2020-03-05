import EMRManagementActionTypes from '../actions/actionTypes/EMRManagementActionTypes';
  
const initialState = {
  emrMetadataData: [],
  emrMetadataDataFetching: false,
  emrMetadataDataSuccess: false,
  emrMetadataDataError: false,
  addStepsStatusSuccess: false,
  addStepsStatusError: false,
  addStepsStatusErrorData: '',
  terminateClusterStatusSuccess: false,
  terminateClusterStatusError: false,
  terminateClusterStatusErrorData: '',
  rotateAMIStatusSuccess: false,
  rotateAMIStatusError: false,
  rotateAMIStatusErrorData: '',
  createClusterStatusSuccess: false,
  createClusterStatusError: false,
  createClusterStatusErrorData: '',
  createClusterStatusData: '',
  stepsStatusData: [],
  stepsStatusDataFetching: false,
  stepsStatusDataSuccess: false,
  stepsStatusDataError: false,
  runTestsStatusSucess: false,
  runTestsStatusError: false,
  runTestsStatusErrorData: '',
  emrTestSuitesData: [],
  emrTestSuitesFetching: false,
  emrTestSuitesSuccess: false,
  emrTestSuitesError: false,
  emrTestSuitesStatusData: [],
  emrTestSuitesStatusFetching: false,
  emrTestSuitesStatusSuccess: false,
  emrTestSuitesStatusError: false,
  clusterHealthCheckHistoryData: [],
  clusterHealthCheckHistoryFetching: false,
  clusterHealthCheckHistorySucess: false,
  clusterHealthCheckHistoryError: false,
  createClusterWorkflowData: [],
  createClusterWorkflowFetching: false,
  createClusterWorkflowSuccess: false,
  createClusterWorkflowError: false,
  rotateAMIWorkflowData: [],
  rotateAMIWorkflowFetching: false,
  rotateAMIWorkflowSuccess: false,
  rotateAMIWorkflowError: false,
  autoRotateAMIRequest: true,
  autoRotateAMISuccess: false,
  autoRotateAMIError: false,
  autoRotateAMIData: {},
  autoRotateAMIErrorMessage: '',
  dnsFlipStatusSuccess: false,
  dnsFlipStatusError: false,
  dnsFlipStatusErrorData: '',
  requestClusterWorkflowFetching: false,
  requestClusterWorkflowSuccess: false,
  requestClusterWorkflowError: false,
  requestClusterWorkflowData: [],
  requestClusterWorkflowErrorData: '',
  requestWorkflowFetching: false,
  requestWorkflowSuccess: false,
  requestWorkflowError: false,
  requestWorkflowData: [],
  requestWorkflowErrorData: '',
  updateDoTerminateSuccess: false,
  updateDoTerminateError: false,
  updateDoTerminateErrorData: '',
  clusterCloneFetching: false, 
  clusterCloneSuccess: false, 
  clusterCloneData: {}, 
  clusterCloneError: false, 
  clusterCloneErrorMessage: '',
  uiListFetching: false,
  uiListSuccess: false,
  uiListError: false,
  uiListData: {},
  uiListErrorMessage: '',
  globalJiraFetching: false,
  globalJiraSuccess: false,
  globalJiraError: false,
  globalJiraData: {},
  accountJiraFetching: false,
  accountJiraSuccess: false,
  accountJiraError: false,
  accountJiraData: {}
}
  
  export const emrMetadataData = (state = initialState, action) => {
    switch (action.type) {
      case EMRManagementActionTypes.EMRMETADATA_STATUS_FETCHING:
        return {
          ...state,
          emrMetadataDataFetching: true,
          emrMetadataDataSuccess: false,
          emrMetadataDataError: false
        }

      case EMRManagementActionTypes.EMRMETADATA_STATUS_SUCCESS:
        return {
          ...state,
          emrMetadataData: action.payload.data,
          emrMetadataDataFetching: false,
          emrMetadataDataSuccess: true,
          emrMetadataDataError: false
        }

      case EMRManagementActionTypes.EMRMETADATA_STATUS_ERROR:
        return {
          ...state,
          emrMetadataDataFetching: false,
          emrMetadataDataSuccess: false,
          emrMetadataDataError: true
        }
      
      case EMRManagementActionTypes.STEPS_STATUS_FETCHING:
        return {
          ...state,
          stepsStatusDataFetching: true,
          stepsStatusDataSuccess: false,
          stepsStatusDataError: false
        }

      case EMRManagementActionTypes.STEPS_STATUS_SUCCESS:
        return {
          ...state,
          stepsStatusData: {
            ...state.stepsStatusData,
            [action.payload.id]: action.payload.data
          },
          stepsStatusDataFetching: false,
          stepsStatusDataSuccess: true,
          stepsStatusDataError: false
        }

      case EMRManagementActionTypes.STEPS_STATUS_ERROR:
        return {
          ...state,
          stepsStatusDataFetching: false,
          stepsStatusDataSuccess: false,
          stepsStatusDataError: true
        }

      case EMRManagementActionTypes.STEPS_STATUS_MODAL_FETCHING:
        return {
          ...state,
          stepsStatusModalDataFetching: true,
          stepsStatusModalDataSuccess: false,
          stepsStatusModalDataError: false
        }

      case EMRManagementActionTypes.STEPS_STATUS_MODAL_SUCCESS:
        return {
          ...state,
          stepsStatusModalData: {
            ...state.stepsStatusModalData,
            [action.payload.id]: action.payload.data
          },
          stepsStatusModalDataFetching: false,
          stepsStatusModalDataSuccess: true,
          stepsStatusModalDataError: false
        }

      case EMRManagementActionTypes.STEPS_STATUS_MODAL_ERROR:
          return {
            ...state,
            stepsStatusModalDataFetching: false,
            stepsStatusModalDataSuccess: false,
            stepsStatusModalDataError: true
          }

      case EMRManagementActionTypes.ADDSTEPS_STATUS_SUCCESS:
        return {
          ...state,
          addStepsStatusSuccess: true,
          addStepsStatusError: false
        }
  
      case EMRManagementActionTypes.ADDSTEPS_STATUS_ERROR:
        return {
          ...state,
          addStepsStatusSuccess: false,
          addStepsStatusError: true,
          addStepsStatusErrorData: action.payload
        }
  
      case EMRManagementActionTypes.TERMINATECLUSTER_STATUS_SUCCESS:
        return {
          ...state,
          terminateClusterStatusSuccess: true,
          terminateClusterStatusError: false
        }
  
      case EMRManagementActionTypes.TERMINATECLUSTER_STATUS_ERROR:
        return {
          ...state,
          terminateClusterStatusSuccess: false,
          terminateClusterStatusError: true,
          terminateClusterStatusErrorData: action.payload
        }

      case EMRManagementActionTypes.ROTATEAMI_STATUS_SUCCESS:
        return {
          ...state,
          rotateAMIStatusSuccess: true,
          rotateAMIStatusError: false
        }
  
      case EMRManagementActionTypes.ROTATEAMI_STATUS_ERROR:
        return {
          ...state,
          rotateAMIStatusSuccess: false,
          rotateAMIStatusError: true,
          rotateAMIStatusErrorData: action.payload
        }
      
      case EMRManagementActionTypes.DNS_FLIP_SUCCESS:
        return {
          ...state,
          dnsFlipStatusSuccess: true,
          dnsFlipStatusError: false
        }

      case EMRManagementActionTypes.DNS_FLIP_ERROR:
        return {
          ...state,
          dnsFlipStatusSuccess: false,
          dnsFlipStatusError: true,
          dnsFlipStatusErrorData: action.payload
        }
  
      case EMRManagementActionTypes.CREATECLUSTER_STATUS_SUCCESS:
        return {
          ...state,
          createClusterStatusSuccess: true,
          createClusterStatusData: action.payload.data,
          createClusterStatusError: false
        }
  
      case EMRManagementActionTypes.CREATECLUSTER_STATUS_ERROR:
        return {
          ...state,
          createClusterStatusSuccess: false,
          createClusterStatusError: true,
          createClusterStatusErrorData: action.payload
        }
      
      case EMRManagementActionTypes.RUN_TESTS_STATUS_SUCCESS:
        return {
          ...state,
          runTestsStatusSucess: true,
          runTestsStatusError: false
        }
      
      case EMRManagementActionTypes.RUN_TESTS_STATUS_ERROR:
        return {
          ...state,
          runTestsStatusError: true,
          runTestsStatusSucess: false,
          runTestsStatusErrorData: action.payload
        }
      
      case EMRManagementActionTypes.EMRTEST_SUITES_FETCHING:
        return {
          ...state,
          emrTestSuitesFetching: true,
          emrTestSuitesSuccess: false,
          emrTestSuitesError: false
        }

      case EMRManagementActionTypes.EMRTEST_SUITES_SUCCESS:
        return {
          ...state,
          emrTestSuitesData: action.payload.data,
          emrTestSuitesFetching: false,
          emrTestSuitesSuccess: true,
          emrTestSuitesError: false
        }
      
      case EMRManagementActionTypes.EMRTEST_SUITES_ERROR:
        return {
          ...state,
          emrTestSuitesFetching: false,
          emrTestSuitesSuccess: false,
          emrTestSuitesError: true
        }
      
      case EMRManagementActionTypes.EMRTEST_SUITES_STATUS_FETCHING:
        return {
          ...state,
          emrTestSuitesStatusFetching: true,
          emrTestSuitesStatusSuccess: false,
          emrTestSuitesStatusError: false
        }

      case EMRManagementActionTypes.EMRTEST_SUITES_STATUS_SUCCESS:
        return {
          ...state,
          emrTestSuitesStatusData: action.payload.data,
          emrTestSuitesStatusFetching: false,
          emrTestSuitesStatusSuccess: true,
          emrTestSuitesStatusError: false
        }
        
      case EMRManagementActionTypes.EMRTEST_SUITES_STATUS_ERROR:
        return {
          ...state,
          emrTestSuitesStatusFetching: false,
          emrTestSuitesStatusSuccess: false,
          emrTestSuitesStatusError: true
        }
        
      case EMRManagementActionTypes.CLUSTER_HEALTH_CHECK_HISTORY_FETCHING:
        return {
          ...state,
          clusterHealthCheckHistoryFetching: true,
          clusterHealthCheckHistorySucess: false,
          clusterHealthCheckHistoryError: false
        }
          
      case EMRManagementActionTypes.CLUSTER_HEALTH_CHECK_HISTORY_SUCCESS:
        return {
          ...state,
          clusterHealthCheckHistoryData: action.payload.data,
          clusterHealthCheckHistoryFetching: false,
          clusterHealthCheckHistorySucess: true,
          clusterHealthCheckHistoryError: false
        }
          
      case EMRManagementActionTypes.CLUSTER_HEALTH_CHECK_HISTORY_ERROR:
        return {
          ...state,
          clusterHealthCheckHistoryFetching: false,
          clusterHealthCheckHistorySucess: false,
          clusterHealthCheckHistoryError: true
        }
      
      case EMRManagementActionTypes.CREATE_CLUSTER_WORKFLOW_FETCHING:
        return {
          ...state,
          createClusterWorkflowFetching: true,
          createClusterWorkflowSuccess: false,
          createClusterWorkflowError: false
        }
      
      case EMRManagementActionTypes.CREATE_CLUSTER_WORKFLOW_SUCCESS:
        return {
          ...state,
          createClusterWorkflowFetching: false,
          createClusterWorkflowData: action.payload.data,
          createClusterWorkflowSuccess: true,
          createClusterWorkflowError: false
        }
      
      case EMRManagementActionTypes.CREATE_CLUSTER_WORKFLOW_ERROR:
        return {
          ...state,
          createClusterWorkflowFetching: false,
          createClusterWorkflowSuccess: false,
          createClusterWorkflowError: true
        }
      
      case EMRManagementActionTypes.ROTATE_AMI_WORKFLOW_FETCHING:
        return {
          ...state,
          rotateAMIWorkflowFetching: true,
          rotateAMIWorkflowSuccess: false,
          rotateAMIWorkflowError: false
        }
      
      case EMRManagementActionTypes.ROTATE_AMI_WORKFLOW_SUCCESS:
        return {
          ...state,
          rotateAMIWorkflowData: action.payload.data,
          rotateAMIWorkflowFetching: false,
          rotateAMIWorkflowSuccess: true,
          rotateAMIWorkflowError: false
        }
      
      case EMRManagementActionTypes.ROTATE_AMI_WORKFLOW_ERROR:
        return {
          ...state,
          rotateAMIWorkflowFetching: false,
          rotateAMIWorkflowSuccess: false,
          rotateAMIWorkflowError: true
        }

      case EMRManagementActionTypes.AUTOROTATEAMI_STATUS_REQUEST:
          return {
            ...state,
            autoRotateAMIRequest: true,
            autoRotateAMISuccess: false,
            autoRotateAMIError: false,
            autoRotateAMIErrorMessage: '',
          }

      case EMRManagementActionTypes.AUTOROTATEAMI_STATUS_SUCCESS:
        return {
          ...state,
          autoRotateAMIRequest: false,
          autoRotateAMISuccess: true,
          autoRotateAMIError: false,
          autoRotateAMIData: action.payload.data,
          autoRotateAMIErrorMessage: '',
        }

      case EMRManagementActionTypes.AUTOROTATEAMI_STATUS_ERROR:
        return {
          ...state,
          autoRotateAMIRequest: false,
          autoRotateAMISuccess: false,
          autoRotateAMIError: true,
          autoRotateAMIErrorMessage: action.payload
        }

      case EMRManagementActionTypes.REQUEST_CLUSTER_WORKFLOW_FETCHING:
       return {
            ...state,
            requestClusterWorkflowFetching: true,
            requestClusterWorkflowSuccess: false,
            requestClusterWorkflowError: false
        }

      case EMRManagementActionTypes.REQUEST_CLUSTER_WORKFLOW_SUCCESS:
        return {
          ...state,
          requestClusterWorkflowFetching: false,
          requestClusterWorkflowSuccess: true,
          requestClusterWorkflowError: false,
          requestClusterWorkflowData: action.payload.data,
          requestClusterWorkflowErrorData: ''
        }

      case EMRManagementActionTypes.REQUEST_CLUSTER_WORKFLOW_ERROR:
        return {
          ...state,
          requestClusterWorkflowFetching: false,
          requestClusterWorkflowSuccess: false,
          requestClusterWorkflowError: true,
          requestClusterWorkflowErrorData: action.payload.error
        }
      
        case EMRManagementActionTypes.REQUEST_WORKFLOW_FETCHING:
          return {
               ...state,
               requestWorkflowFetching: true,
               requestWorkflowSuccess: false,
               requestWorkflowError: false
           }
   
         case EMRManagementActionTypes.REQUEST_WORKFLOW_SUCCESS:
           return {
             ...state,
             requestWorkflowFetching: false,
             requestWorkflowSuccess: true,
             requestWorkflowError: false,
             requestWorkflowData: action.payload.data,
             requestWorkflowErrorData: ''
           }
   
         case EMRManagementActionTypes.REQUEST_WORKFLOW_ERROR:
           return {
             ...state,
             requestWorkflowFetching: false,
             requestWorkflowSuccess: false,
             requestWorkflowError: true,
             requestWorkflowErrorData: action.payload.error
           }

      case EMRManagementActionTypes.UPDATE_DO_TERMINATE_SUCCESS:
        return {
          ...state,
          updateDoTerminateSuccess: true,
          updateDoTerminateError: false
        }

      case EMRManagementActionTypes.UPDATE_DO_TERMINATE_ERROR:
        return {
          ...state,
          updateDoTerminateError: true,
          updateDoTerminateErrorData: action.payload.error,
          updateDoTerminateSuccess: false
        }

      case EMRManagementActionTypes.CLEAR_ERRORS:
        return {
          ...state,
          emrMetadataDataError: false,
          createClusterStatusError: false,
          createClusterStatusErrorData: '',
          runTestsStatusError: false,
          runTestsStatusErrorData: '',
          emrTestSuitesError: false,
          emrTestSuitesStatusError: false,
          clusterHealthCheckHistoryError: false,
          createClusterWorkflowError: false,
          rotateAMIWorkflowError: false,
          autoRotateAMIError: false,
          autoRotateAMIErrorMessage: '',
          dnsFlipStatusSuccess: false,
          dnsFlipStatusError: false,
          dnsFlipStatusErrorData: '',
          requestClusterWorkflowError: false,
          requestClusterWorkflowErrorData: '',
          updateDoTerminateError: false,
          updateDoTerminateErrorData: '',
          clusterCloneError: false, 
          clusterCloneErrorMessage: '',
          addStepsStatusSuccess: false,
          addStepsStatusError: false,
          addStepsStatusErrorData: '',
          terminateClusterStatusSuccess: false,
          terminateClusterStatusError: false,
          terminateClusterStatusErrorData: '',
          rotateAMIStatusSuccess: false,
          rotateAMIStatusError: false,
          rotateAMIStatusErrorData: '',
        }

      case EMRManagementActionTypes.GET_CLUSTER_CLONE_FETCHING:
        return {
          ...state,
          clusterCloneFetching: true, 
          clusterCloneSuccess: false, 
          clusterCloneData: {}, 
          clusterCloneError: false, 
          clusterCloneErrorMessage: ''
        }

      case EMRManagementActionTypes.GET_CLUSTER_CLONE_SUCCESS:
        return {
          ...state,
          clusterCloneFetching: false, 
          clusterCloneSuccess: true, 
          clusterCloneData: action.payload.data, 
          clusterCloneError: false, 
          clusterCloneErrorMessage: ''
        }

      case EMRManagementActionTypes.GET_CLUSTER_CLONE_ERROR:
        return {
          ...state,
          clusterCloneFetching: false, 
          clusterCloneSuccess: false, 
          clusterCloneError: false, 
          clusterCloneErrorMessage: action.payload.errorMessage
        }

      case EMRManagementActionTypes.GET_UI_LIST_FETCHING:
        return {
          ...state,
          uiListFetching: true, 
          uiListSuccess: false,
          uiListError: false,
          uiListErrorMessage: ''
        }

      case EMRManagementActionTypes.GET_UI_LIST_SUCCESS: 
        return {
          ...state,
          uiListFetching: false,
          uiListSuccess: true,
          uiListError: false,
          uiListData: action.payload.data,
          uiListErrorMessage: ''
        }

      case EMRManagementActionTypes.GET_UI_LIST_ERROR: 
        return {
          ...state,
          uiListFetching: false,
          uiListSuccess: false,
          uiListError: true,
          uiListErrorMessage: action.payload.errorMessage
        }

      case EMRManagementActionTypes.GLOBAL_JIRA_FETCHING:
        return {
          ...state,
          globalJiraFetching: true,
          globalJiraSuccess: false,
          globalJiraError: false
        }
      
      case EMRManagementActionTypes.GLOBAL_JIRA_SUCCESS:
        return {
          ...state,
          globalJiraFetching: false,
          globalJiraSuccess: true,
          globalJiraError: false,
          globalJiraData: action.payload.data
        }
      
      case EMRManagementActionTypes.GLOBAL_JIRA_ERROR:
        return {
          ...state,
          globalJiraFetching: false,
          globalJiraSuccess: false,
          globalJiraError: true
        }

      case EMRManagementActionTypes.ACCOUNT_JIRA_FETCHING:
        return {
          ...state,
          accountJiraFetching: true,
          accountJiraSuccess: false,
          accountJiraError: false,
        }
      
      case EMRManagementActionTypes.ACCOUNT_JIRA_SUCCESS:
        return {
          ...state,
          accountJiraFetching: false,
          accountJiraSuccess: true,
          accountJiraError: false,
          accountJiraData: action.payload.data
        }

      case EMRManagementActionTypes.ACCOUNT_JIRA_ERROR:
        return {
          ...state,
          accountJiraFetching: false,
          accountJiraSuccess: false,
          accountJiraError: true
        }
        
      default:
        return state
    }
    
  }
  
  