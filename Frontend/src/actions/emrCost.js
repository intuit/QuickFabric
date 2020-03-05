import EMRCostActionTypes from './actionTypes/EMRCostActionTypes'
import baseURL from '../api-config'
import Cookies from 'js-cookie';
import {dispatchAndFetch} from './../utils/utils/action'

/**
 * Get EMR Cost for the type of data specified.
 * @param {string} type e.g. scheduled, exploratory, transient etc.
 * @param {*} token jwt token
 */
export const fetchEMRCostData = (type,token) => {
  token = Cookies.get('jwt');
    return (dispatch) => {
      dispatch({ type: EMRCostActionTypes.EMRCOST_STATUS_FETCHING, payload: {} })
      fetch(baseURL + `/emr/metrics/${type}`,{
        method: 'GET',
        headers: {
          'Content-type': 'application/json',
          'Authorization': `Bearer ${token}`
        }
      })
        .then(res => res.json())
        .then(res => {
          return dispatch({
            type: EMRCostActionTypes.EMRCOST_STATUS_SUCCESS,
            payload: { data: res.emrClusterMetricsReport }
  
          })
        })
        .catch(error => {
          return dispatch({
            type: EMRCostActionTypes.EMRCOST_STATUS_ERROR,
            payload: { error }
          })
        })
    } 
}

/**
 * Get EMR Cost for the account specified.
 * @param {string} account AWS account number
 * @param {string} token jwt token
 */
export const fetchEMRCostDataAccountWise = (account,token) => {
  token = Cookies.get('jwt');
    return (dispatch) => {
        dispatch({ type: EMRCostActionTypes.EMRCOST_STATUS_FETCHING, payload: {} })
        fetch(baseURL + `/emr/metrics/${account}`,{
          method: 'GET',
          headers: {
            'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
          }
          })
          .then(res => res.json())
          .then(res => {
            return dispatch({
              type: EMRCostActionTypes.EMRCOST_STATUS_SUCCESS,
              payload: { data: res.emrClusterMetricsReport }
    
            })
          })
          .catch(error => {
            return dispatch({
              type: EMRCostActionTypes.EMRCOST_STATUS_ERROR,
              payload: { error }
            })
          })
      }
}

/**
 * Get EMR Cost information for last six months.
 * @param {string} token jwt token
 */
export const fetchEMRClusterCost = (token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
      dispatch({ type: EMRCostActionTypes.EMRCOST_STATUS_FETCHING, payload: {} })
      fetch(baseURL + `/emr/cost/group/`,{
        method: 'GET',
        headers: {
          'Content-type': 'application/json',
          'Authorization': 'Bearer ' + token
        }
        })
        .then(res => res.json())
        .then(res => {
          return dispatch({
            type: EMRCostActionTypes.EMRCOST_STATUS_SUCCESS,
            payload: { data: res }
  
          })
        })
        .catch(error => {
          return dispatch({
            type: EMRCostActionTypes.EMRCOST_STATUS_ERROR,
            payload: { error }
          })
        })
    }
}

export const fetchClusterCosts = (cluster_id, from, to, type, token) => {
  token = Cookies.get('jwt');
  return (dispatch) => {
    if(type === 'WEEKLY') {
      let apiURL = baseURL + `/emr/cost/${cluster_id}/week`
      dispatchAndFetch(dispatch, 'GET', {}, 'GET_CLUSTER_COST_WEEKLY', apiURL, token) 
    } 
    if(type === 'MONTHLY') {
      let apiURL = baseURL + `/emr/cost/${cluster_id}/month`
      dispatchAndFetch(dispatch, 'GET', {}, 'GET_CLUSTER_COST_MONTHLY', apiURL, token) 
    } 
    if(type === 'CUSTOM') {
      let apiURL = baseURL + `/emr/cost/${cluster_id}/custom_range?from=${from}&to=${to}`
      dispatchAndFetch(dispatch, 'GET', {}, 'GET_CLUSTER_COST_CUSTOM', apiURL, token)
    }
  }
}