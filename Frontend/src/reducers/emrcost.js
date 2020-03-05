import EMRCostActionTypes from '../actions/actionTypes/EMRCostActionTypes';

const initialState = {
  emrCostData: [],
  emrCostFetching: false,
  emrCostSuccess: false,
  emrCostError: false,

  clusterCostWeeklyFetching: true,
  clusterCostWeeklySuccess: false,
  clusterCostWeeklyError: false,
  clusterCostWeeklyErrorMessage: '',
  clusterCostWeeklyData: {},

  clusterCostMonthlyFetching: true,
  clusterCostMonthlySuccess: false,
  clusterCostMonthlyError: false,
  clusterCostMonthlyErrorMessage: '',
  clusterCostMonthlyData: {},

  clusterCostCustomFetching: true,
  clusterCostCustomSuccess: false,
  clusterCostCustomError: false,
  clusterCostCustomErrorMessage: '',
  clusterCostCustomData: {}

}

export const emrCostData = (state = initialState, action) => {
    switch (action.type) {
      case EMRCostActionTypes.EMRCOST_STATUS_FETCHING:
        return {
          ...state,
          emrCostFetching: true,
          emrCostSuccess: false,
          emrCostError: false
        }

      case EMRCostActionTypes.EMRCOST_STATUS_SUCCESS:
        return {
          ...state,
          emrCostData: action.payload.data,
          emrCostFetching: false,
          emrCostSuccess: true,
          emrCostError: false
        }

      case EMRCostActionTypes.EMRCOST_STATUS_ERROR:
        return {
          ...state,
          emrCostFetching: false,
          emrCostSuccess: false,
          emrCostError: true
  
        }
// Get Cluster Cost

      case EMRCostActionTypes.GET_CLUSTER_COST_MONTHLY_FETCHING: 
        return {
            ...state,
            clusterCostMonthlyFetching: true,
            clusterCostMonthlySuccess: false,
            clusterCostMonthlyError: false,
            clusterCostMonthlyErrorMessage: '',
            clusterCostMonthlyData: {}
        }
      case EMRCostActionTypes.GET_CLUSTER_COST_MONTHLY_SUCCESS:
        return {
            ...state,
            clusterCostMonthlyFetching: false,
            clusterCostMonthlySuccess: true,
            clusterCostMonthlyError: false,
            clusterCostMonthlyErrorMessage: '',
            clusterCostMonthlyData: action.payload.data.clusterCost
        }
      case EMRCostActionTypes.GET_CLUSTER_COST_MONTHLY_ERROR: 
        return {
            ...state,
            clusterCostMonthlyFetching: false,
            clusterCostMonthlySuccess: false,
            clusterCostMonthlyError: true,
            clusterCostMonthlyErrorMessage: action.payload.error,
            clusterCostMonthlyData: {}
        }
      case EMRCostActionTypes.GET_CLUSTER_COST_WEEKLY_FETCHING:
        return {
            ...state,
            clusterCostWeeklyFetching: true,
            clusterCostWeeklySuccess: false,
            clusterCostWeeklyError: false,
            clusterCostWeeklyErrorMessage: '',
            clusterCostWeeklyData: {}
        }
      case EMRCostActionTypes.GET_CLUSTER_COST_WEEKLY_SUCCESS:
        return {
            ...state,
            clusterCostWeeklyFetching: false,
            clusterCostWeeklySuccess: true,
            clusterCostWeeklyError: false,
            clusterCostWeeklyErrorMessage: '',
            clusterCostWeeklyData: action.payload.data.clusterCost
        }
      case EMRCostActionTypes.GET_CLUSTER_COST_WEEKLY_ERROR:
        return {
            ...state,
            clusterCostWeeklyFetching: false,
            clusterCostWeeklySuccess: false,
            clusterCostWeeklyError: true,
            clusterCostWeeklyErrorMessage: action.payload.error,
            clusterCostWeeklyData: {}
        }        
      case EMRCostActionTypes.GET_CLUSTER_COST_CUSTOM_FETCHING: 
        return {
            ...state,
            clusterCostCustomFetching: true,
            clusterCostCustomSuccess: false,
            clusterCostCustomError: false,
            clusterCostCustomErrorMessage: '',
            clusterCostCustomData: {}
        }
      case EMRCostActionTypes.GET_CLUSTER_COST_CUSTOM_SUCCESS: 
        return {
            ...state,
            clusterCostCustomFetching: false,
            clusterCostCustomSuccess: true,
            clusterCostCustomError: false,
            clusterCostCustomErrorMessage: '',
            clusterCostCustomData: action.payload.data.clusterCost
        }
      case EMRCostActionTypes.GET_CLUSTER_COST_CUSTOM_ERROR:
        return {
            ...state,
            clusterCostCustomFetching: false,
            clusterCostCustomSuccess: false,
            clusterCostCustomError: true,
            clusterCostCustomErrorMessage: action.payload.error
        }           
      default:
        return state
    }
}