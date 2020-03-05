import EMRHealthActionTypes from '../actions/actionTypes/EMRHealthActionTypes';

const initialState = {
  allEmrHealthData: [],
  allEmrHealthFetching: false,
  allEmrHealthSuccess: false,
  allEmrHealthError: false,

  clusterMetricsHourlyData: [],
  clusterMetricsHourlyFetching: false,
  clusterMetricsHourlySuccess: false,
  clusterMetricsHourlyError: false,

  clusterMetricsDailyFetching: false,
  clusterMetricsDailySuccess: false,
  clusterMetricsDailyError: false,

  clusterMetricsWeeklyData: [],
  clusterMetricsWeeklyFetching: false,
  clusterMetricsWeeklySuccess: false,
  clusterMetricsWeeklyError: false,

  clusterMetricsMonthlyFetching: false,
  clusterMetricsMonthlySuccess: false,
  clusterMetricsMonthlyError: false,

  clusterMetricsAdviceDailyFetching: false,
  clusterMetricsAdviceDailySuccess: false,
  clusterMetricsAdviceDailyError: false,

  clusterMetricsAdviceWeeklyData: [],
  clusterMetricsAdviceWeeklyFetching: false,
  clusterMetricsAdviceWeeklySuccess: false,
  clusterMetricsAdviceWeeklyError: false,

  clusterMetricsAdviceMonthlyFetching: false,
  clusterMetricsAdviceMonthlySuccess: false,
  clusterMetricsAdviceMonthlyError: false,

  clusterAppsRunningData: [],
  clusterAppsRunningFetching: false,
  clusterAppsRunningSuccess: false,
  clusterAppsRunningError: false,

  allClusterMetricsDailyError: false,
  allClusterMetricsDailySuccess: false,
  allClusterMetricsDailyFetching: false,
  allClusterMetricsDailyData: {},
  allClusterMetricsDailyErrorMessage: '',

  allClusterMetricsMonthlyError: false,
  allClusterMetricsMonthlySuccess: false,
  allClusterMetricsMonthlyFetching: false,
  allClusterMetricsMonthlyData: {},
  allClusterMetricsMonthlyErrorMessage: '',

  allClusterMetricsWeeklyError: false,
  allClusterMetricsWeeklySuccess: false,
  allClusterMetricsWeeklyFetching: false,
  allClusterMetricsWeeklyData: {},
  allClusterMetricsWeeklyErrorMessage: '',

  clusterMetricsGetCustomFetching: false,
  clusterMetricsGetCustomSuccess: false,
  clusterMetricsGetCustomError: false, 
  clusterMetricsGetCustomData: {},
  clusterMetricsGetCustomErrorMessage: '',

  uiListFetching: false,
  uiListSuccess: false,
  uiListError: false,
  uiListData: {},
  uiListErrorMessage: '',
}

export const allEmrHealthData = (state = initialState, action) => {
  switch (action.type) {
    case EMRHealthActionTypes.ALLEMRHEALTHDATA_STATUS_FETCHING:
      return {
        ...state,
        allEmrHealthFetching: true,
        allEmrHealthSuccess: false,
        allEmrHealthError: false,
      }

    case EMRHealthActionTypes.ALLEMRHEALTHDATA_STATUS_SUCCESS:
      return {
        ...state,
        allEmrHealthData: action.payload.data,
        allEmrHealthFetching: false,
        allEmrHealthSuccess: true,
        allEmrHealthError: false
      }

    case EMRHealthActionTypes.ALLEMRHEALTHDATA_STATUS_ERROR:
      return {
        ...state,
        allEmrHealthFetching: false,
        allEmrHealthSuccess: true,
        allEmrHealthError: true
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_HOURLY_FETCHING:
      return {
        ...state,
        clusterMetricsHourlyFetching: true,
        clusterMetricsHourlySuccess: false,
        clusterMetricsHourlyError: false
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_HOURLY_SUCCESS:
      return {
        ...state,
        clusterMetricsHourlyData: action.payload.data,
        clusterMetricsHourlyFetching: false,
        clusterMetricsHourlySuccess: true,
        clusterMetricsHourlyError: false
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_HOURLY_ERROR:
      return {
        ...state,
        clusterMetricsHourlyFetching: false,
        clusterMetricsHourlyError: true,
        clusterMetricsHourlySuccess: false
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_DAILY_FETCHING:
      return {
        ...state,
        clusterMetricsDailyFetching: true,
        clusterMetricsDailySuccess: false,
        clusterMetricsDailyError: false
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_DAILY_SUCCESS:
      return {
        ...state,
        clusterMetricsDailyData: action.payload.data,
        clusterMetricsDailyFetching: false,
        clusterMetricsDailySuccess: true,
        clusterMetricsDailyError: false
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_DAILY_ERROR:
      return {
        ...state,
        clusterMetricsDailyFetching: false,
        clusterMetricsDailySuccess: false,
        clusterMetricsDailyError: true
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_WEEKLY_FETCHING:
      return {
        ...state,
        clusterMetricsWeeklyFetching: true,
        clusterMetricsWeeklySuccess: false,
        clusterMetricsWeeklyError: false
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_WEEKLY_SUCCESS:
      return {
        ...state,
        clusterMetricsWeeklyData: action.payload.data,
        clusterMetricsWeeklyFetching: false,
        clusterMetricsWeeklySuccess: true,
        clusterMetricsWeeklyError: false
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_WEEKLY_ERROR:
      return {
        ...state,
        clusterMetricsWeeklyFetching: false,
        clusterMetricsWeeklySuccess: false,
        clusterMetricsWeeklyError: true
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_MONTHLY_FETCHING:
      return {
        ...state,
        clusterMetricsMonthlyFetching: true,
        clusterMetricsMonthlySuccess: false,
        clusterMetricsMonthlyError: false
      }

      case EMRHealthActionTypes.CLUSTER_METRICS_MONTHLY_SUCCESS:
        return {
          ...state,
          clusterMetricsMonthlyData: action.payload.data,
          clusterMetricsMonthlyFetching: false,
          clusterMetricsMonthlySuccess: true,
          clusterMetricsMonthlyError: false
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_MONTHLY_ERROR:
        return {
          ...state,
          clusterMetricsMonthlyFetching: false,
          clusterMetricsMonthlySuccess: false,
          clusterMetricsMonthlyError: true
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_DAILY_FETCHING:
        return {
          ...state,
          clusterMetricsAdviceDailyFetching: true,
          clusterMetricsAdviceDailySuccess: false,
          clusterMetricsAdviceDailyError: false
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_DAILY_SUCCESS:
        return {
          ...state,
          clusterMetricsAdviceDailyData: action.payload.data,
          clusterMetricsAdviceDailySuccess: true,
          clusterMetricsAdviceDailyFetching: false,
          clusterMetricsAdviceDailyError: false
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_DAILY_ERROR:
        return {
          ...state,
          clusterMetricsAdviceDailyFetching: false,
          clusterMetricsAdviceDailySuccess: false,
          clusterMetricsAdviceDailyError: true
        }

        case EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_WEEKLY_FETCHING:
          return {
            ...state,
            clusterMetricsAdviceWeeklyFetching: true,
            clusterMetricsAdviceWeeklySuccess: false,
            clusterMetricsAdviceWeeklyError: false
          }

        case EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_WEEKLY_SUCCESS:
          return {
            ...state,
            clusterMetricsAdviceWeeklyData: action.payload.data,
            clusterMetricsAdviceWeeklySuccess: true,
            clusterMetricsAdviceWeeklyFetching: false,
            clusterMetricsAdviceWeeklyError: false
          }

        case EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_WEEKLY_ERROR:
          return {
            ...state,
            clusterMetricsAdviceWeeklyFetching: false,
            clusterMetricsAdviceWeeklySuccess: false,
            clusterMetricsAdviceWeeklyError: true
          }

      case EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_MONTHLY_FETCHING:
        return {
          ...state,
          clusterMetricsAdviceMonthlyFetching: true,
          clusterMetricsAdviceMonthlySuccess: false,
          clusterMetricsAdviceMonthlyError: false
        }

    case EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_MONTHLY_SUCCESS:
      return {
        ...state,
        clusterMetricsAdviceMonthlyData: action.payload.data,
        clusterMetricsAdviceMonthlySuccess: true,
        clusterMetricsAdviceMonthlyFetching: false,
        clusterMetricsAdviceMonthlyError: false
      }

    case EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_MONTHLY_ERROR:
      return {
        ...state,
        clusterMetricsAdviceMonthlyFetching: false,
        clusterMetricsAdviceMonthlySuccess: false,
        clusterMetricsAdviceMonthlyError: true
      }

      case EMRHealthActionTypes.CLUSTER_APPS_RUNNING_FETCHING:
        return {
          ...state,
          clusterAppsRunningFetching: true,
          clusterAppsRunningSuccess: false,
          clusterAppsRunningError: false
        }

      case EMRHealthActionTypes.CLUSTER_APPS_RUNNING_SUCCESS:
        return {
          ...state,
          clusterAppsRunningData: action.payload.data,
          clusterAppsRunningFetching: false,
          clusterAppsRunningSuccess: true,
          clusterAppsRunningError: false
        }

      case EMRHealthActionTypes.CLUSTER_APPS_RUNNING_ERROR:
        return {
          ...state,
          clusterAppsRunningFetching: false,
          clusterAppsRunningSuccess: false,
          clusterAppsRunningError: true
        }
      case EMRHealthActionTypes.CLUSTER_METRICS_GET_DAILY_FETCHING:
        return {
          ...state,
          allClusterMetricsDailyFetching: true,
          allClusterMetricsDailySuccess: false,
          allClusterMetricsDailyError: false
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_GET_DAILY_SUCCESS:
        return {
          ...state,
          allClusterMetricsDailyData: action.payload.data,
          allClusterMetricsDailySuccess: true,
          allClusterMetricsDailyFetching: false,
          allClusterMetricsDailyError: false,
          allClusterMetricsDailyErrorMessage: ''
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_GET_DAILY_ERROR:
        return {
          ...state,
          allClusterMetricsDailyFetching: false,
          allClusterMetricsDailySuccess: false,
          allClusterMetricsDailyError: true,
          allClusterMetricsDailyErrorMessage: action.payload.error
        }

        case EMRHealthActionTypes.CLUSTER_METRICS_GET_WEEKLY_FETCHING:
        return {
          ...state,
          allClusterMetricsWeeklyFetching: true,
          allClusterMetricsWeeklySuccess: false,
          allClusterMetricsWeeklyError: false
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_GET_WEEKLY_SUCCESS:
        return {
          ...state,
          allClusterMetricsWeeklyData: action.payload.data,
          allClusterMetricsWeeklySuccess: true,
          allClusterMetricsWeeklyFetching: false,
          allClusterMetricsWeeklyError: false,
          allClusterMetricsWeeklyErrorMessage: ''
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_GET_WEEKLY_ERROR:
        return {
          ...state,
          allClusterMetricsWeeklyFetching: false,
          allClusterMetricsWeeklySuccess: false,
          allClusterMetricsWeeklyError: true,
          allClusterMetricsWeeklyErrorMessage: action.payload.error
        }

        case EMRHealthActionTypes.CLUSTER_METRICS_GET_MONTHLY_FETCHING:
        return {
          ...state,
          allClusterMetricsMonthlyFetching: true,
          allClusterMetricsMonthlySuccess: false,
          allClusterMetricsMonthlyError: false,
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_GET_MONTHLY_SUCCESS:
        return {
          ...state,
          allClusterMetricsMonthlyData: action.payload.data,
          allClusterMetricsMonthlySuccess: true,
          allClusterMetricsMonthlyFetching: false,
          allClusterMetricsMonthlyError: false,
          allClusterMetricsMonthlyErrorMessage: ''
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_GET_MONTHLY_ERROR:
        return {
          ...state,
          allClusterMetricsMonthlyFetching: false,
          allClusterMetricsMonthlySuccess: false,
          allClusterMetricsMonthlyError: true,
          allClusterMetricsMonthlyErrorMessage: action.payload.error
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_GET_CUSTOM_FETCHING:
        return {
          ...state,
          clusterMetricsGetCustomFetching: true,
          clusterMetricsGetCustomSuccess: false,
          clusterMetricsGetCustomError: false,
          clusterMetricsGetCustomErrorMessage: ''
        }

      case EMRHealthActionTypes.CLUSTER_METRICS_GET_CUSTOM_SUCCESS:
          return {
            ...state,
            clusterMetricsGetCustomFetching: false,
            clusterMetricsGetCustomSuccess: true,
            clusterMetricsGetCustomError: false,
            clusterMetricsGetCustomErrorMessage: '',
            clusterMetricsGetCustomData: action.payload.data
          }

      case EMRHealthActionTypes.CLUSTER_METRICS_GET_CUSTOM_ERROR:
          return {
            ...state,
             clusterMetricsGetCustomFetching: false,
             clusterMetricsGetCustomSuccess: false,
             clusterMetricsGetCustomError: true,
             clusterMetricsGetCustomErrorMessage: action.payload.error
          }

      case EMRHealthActionTypes.GET_UI_LIST_FETCHING:
        return {
          ...state,
          uiListFetching: true, 
          uiListSuccess: false,
          uiListError: false,
          uiListErrorMessage: ''
        }

      case EMRHealthActionTypes.GET_UI_LIST_SUCCESS: 
        return {
          ...state,
          uiListFetching: false,
          uiListSuccess: true,
          uiListError: false,
          uiListData: action.payload.data,
          uiListErrorMessage: ''
        }

      case EMRHealthActionTypes.GET_UI_LIST_ERROR: 
        return {
          ...state,
          uiListFetching: false,
          uiListSuccess: false,
          uiListError: true,
          uiListErrorMessage: action.payload.errorMessage
        }
  
    default:
      return state
  }

}
