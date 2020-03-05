import ProfileActionTypes from './actionTypes/ProfileActionTypes';
import baseURL from '../api-config';
import Cookies from 'js-cookie';

/**
 * Get report subscription data for the logged in user.
 * @param {string} token jwt token
 */
export const fetchSubscriptions = (token) => {
    token = Cookies.get('jwt');
    return (dispatch) => {
        dispatch({ type: ProfileActionTypes.GET_SUBSCRIPTIONS_FETCHING, payload: {} })
        fetch(baseURL + `/report-subscriptions`, {
            method: 'GET',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + token
              }
            })
            .then(res => res.json())
            .then(res => {
                return dispatch({
                    type: ProfileActionTypes.GET_SUBSCRIPTIONS_SUCCESS,
                    payload: { data: res }
                })
            })
            .catch(error => {
                return dispatch({
                    type: ProfileActionTypes.GET_SUBSCRIPTIONS_ERROR,
                    payload: { error }
                })
            })
    }
}

/**
 * Update the subscriptions for the logged in user.
 * @param {json} data subscriptions
 * @param {string} token jwt token
 */
export const updateSubscriptions = (data, token) => {
    token = Cookies.get('jwt');
    return (dispatch) => {
        fetch(baseURL + `/report-subscriptions/update`, {
            method: 'POST',
            headers: {
                'Content-type': 'application/json',
                'Authorization': 'Bearer ' + token
            },
            body: JSON.stringify(data)
          })
          .then(res => res.json().then(data => {
              if (res.status !== 200) {
                throw data
              } else {
                return dispatch({
                    type: ProfileActionTypes.UPDATE_SUBSCRIPTIONS_SUCCESS,
                    payload: {}
                })
              }
          }))
          .catch(m => {
            return dispatch({
                type: ProfileActionTypes.UPDATE_SUBSCRIPTIONS_ERROR,
                payload: m
            })
          })
    }
}

/**
 * Clear toaster updates.
 */
export const clearUpdates = () => {
    return {
      type: "CLEAR_UPDATES",
      payload: {}
    }
  }