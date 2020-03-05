import EMRHealthActionTypes from './actionTypes/EMRHealthActionTypes';
import baseURL from '../api-config'
import {dispatchAndFetch} from './../utils/utils/action'
import Cookies from 'js-cookie';

/**
 * Get EMR Metrics Data for the type specified.
 * @param {string} type e.g. scheduled, exploratory, transient etc.
 * @param {string} token jwt token
 */
export const fetchEMRHealthData = (type,token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.ALLEMRHEALTHDATA_STATUS_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metrics?cluster_type=${type}`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRHealthActionTypes.ALLEMRHEALTHDATA_STATUS_SUCCESS,
          payload: { data: res.emrClusterMetricsReport }
        })
      })
      .catch(error => {
        return dispatch({
          type: EMRHealthActionTypes.ALLEMRHEALTHDATA_STATUS_ERROR,
          payload: { error }
        })
      })
  }
}

/**
 * Get EMR Metrics Data for a specific AWS account.
 * @param {string} account AWS Account
 * @param {string} token jwt token
 */
export const fetchAccountEMRHealthData = (account,token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.ALLEMRHEALTHDATA_STATUS_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metrics?account=${account}`,{
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRHealthActionTypes.ALLEMRHEALTHDATA_STATUS_SUCCESS,
          //ayload: { data: res.EMRClusterMetricsReport }
          payload: { data: res.emrClusterMetricsReport }

        })
      })
      .catch(error => {
        return dispatch({
          type: EMRHealthActionTypes.ALLEMRHEALTHDATA_STATUS_ERROR,
          payload: { error }
        })
      })
  }
}

/**
 * Get Metrics Data for a cluster for hourly duration.
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} token jwt token
 */
export const fetchClusterMetricsHourly = (emr_id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_HOURLY_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metrics/${emr_id}/hour`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_HOURLY_SUCCESS,
        payload: { data: res.emrClusterMetricsReport }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_HOURLY_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get Metrics Data for a cluster for daily duration
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} token jwt token
 */
export const fetchClusterMetricsDaily = (emr_id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_DAILY_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metrics/${emr_id}/day`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_DAILY_SUCCESS,
        payload: { data: res.emrClusterMetricsReport }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_DAILY_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get Metrics Data for a cluster for weekly duration
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} token jwt token
 */
export const fetchClusterMetricsWeekly = (emr_id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_WEEKLY_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metrics/${emr_id}/week`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_WEEKLY_SUCCESS,
        payload: { data: res.emrClusterMetricsReport }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_WEEKLY_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get Metrics Data for a cluster for monthly duration
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} token jwt token
 */
export const fetchClusterMetricsMonthly = (emr_id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_MONTHLY_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metrics/${emr_id}/month`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_MONTHLY_SUCCESS,
        payload: { data: res.emrClusterMetricsReport }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_MONTHLY_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get Metrics Advice for the cluster for daily duration
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} token jwt token
 */
export const fetchClusterMetricsAdviceDaily = (emr_id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_DAILY_FETCHING, payload: {} })
    fetch(baseURL + `/emr/advice/scheduling/${emr_id}/day`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_DAILY_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_DAILY_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get Metrics Advice for the cluster for all durations
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} time time duration
 * @param {string} token jwt token
 */
export const fetchAllAdvice = (emr_id, time, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    if(time === 'day') {
      dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_GET_DAILY_FETCHING, payload: {} })
      fetch(baseURL + `/emr/advice/all/${emr_id}/${time}`, {
        method: 'GET',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRHealthActionTypes.CLUSTER_METRICS_GET_DAILY_SUCCESS,
          payload: { data: res }
        })
      })
      .catch(error => {
        return dispatch({
          type: EMRHealthActionTypes.CLUSTER_METRICS_GET_DAILY_ERROR,
          payload: { error: error}
        })
      })
    } else if(time === 'week') {
      dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_GET_WEEKLY_FETCHING, payload: {} })
      fetch(baseURL + `/emr/advice/all/${emr_id}/${time}`, {
        method: 'GET',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRHealthActionTypes.CLUSTER_METRICS_GET_WEEKLY_SUCCESS,
          payload: { data: res}
        })
      })
      .catch(error => {
        return dispatch({
          type: EMRHealthActionTypes.CLUSTER_METRICS_GET_WEEKLY_ERROR,
          payload: { error: error}
        })
      })
    } else if(time === 'month') {
      dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_GET_MONTHLY_FETCHING, payload: {} })
      fetch(baseURL + `/emr/advice/all/${emr_id}/${time}`, {
        method: 'GET',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        }
      })
      .then(res => res.json())
      .then(res => {
        return dispatch({
          type: EMRHealthActionTypes.CLUSTER_METRICS_GET_MONTHLY_SUCCESS,
          payload: { data: res }
        })
      })
      .catch(error => {
        return dispatch({
          type: EMRHealthActionTypes.CLUSTER_METRICS_GET_MONTHLY_ERROR,
          payload: { error: error }
        })
      })
    }

  }
}

/**
 * Get Metrics Advice for the cluster for weekly duration
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} token jwt token
 */
export const fetchClusterMetricsAdviceWeekly = (emr_id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_WEEKLY_FETCHING, payload: {} })
    fetch(baseURL + `/emr/advice/scheduling/${emr_id}/week`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_WEEKLY_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_WEEKLY_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get Metrics Advice for the cluster for monthly duration
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} token jwt token
 */
export const fetchClusterMetricsAdviceMonthly = (emr_id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_MONTHLY_FETCHING, payload: {} })
    fetch(baseURL + `/emr/advice/scheduling/${emr_id}/month`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_MONTHLY_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_ADVICE_MONTHLY_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get Running jobs for a cluster
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} token jwt token
 */
export const fetchClusterAppsRunning = (emr_id, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.CLUSTER_APPS_RUNNING_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metrics/${emr_id}/apps/running`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res =>{
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_APPS_RUNNING_SUCCESS,
        payload: { data: res }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_APPS_RUNNING_ERROR,
        payload: { error }
      })
    })
  }
}

/**
 * Get Metrics Data for a cluster for custom range
 * @param {string} emr_id EMR ID of the cluster
 * @param {string} from start date and time
 * @param {string} to end date and time
 * @param {stirng} token jwt token
 */
export const fetchClusterCustomRange = (emr_id, from, to, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({ type: EMRHealthActionTypes.CLUSTER_METRICS_GET_CUSTOM_FETCHING, payload: {} })
    fetch(baseURL + `/emr/metrics/${emr_id}/custom_range?from=${from}&to=${to}`, {
      method: 'GET',
      headers: {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + token
      }
    })
    .then(res => res.json())
    .then(res =>{
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_GET_CUSTOM_SUCCESS,
        payload: { data: res.emrClusterMetricsReport }
      })
    })
    .catch(error => {
      return dispatch({
        type: EMRHealthActionTypes.CLUSTER_METRICS_GET_CUSTOM_ERROR,
        payload: { error }
      })
    })
  }
}