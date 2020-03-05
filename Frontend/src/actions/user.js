import Cookies from 'js-cookie';
import UserActionTypes from './actionTypes/UserActionTypes';
import baseURL from '../api-config';

/**
 * Sign in the user.
 * @param {string} username Email ID
 * @param {string} passcode password
 * @param {string} jwt jwt token
 */
export const signInUser = (username, passcode, jwt) => {
  jwt = Cookies.get('jwt');
  return (dispatch) => {
    console.log("calling login api");
    let signInHeaders = jwt ? {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + jwt
    } 
    : {'Content-type': 'application/json'} 
    fetch(baseURL + '/login', {
      method: 'post',
      headers: signInHeaders,
      body: JSON.stringify({
        emailId: username,
        passcode,
        jwt
      })
    })
      .then(res => res.json().then (data => {
      console.log('res..', res.status, res)
      if(res.status !== 200) {
        console.log('res', data)
        throw data
      } else {
        if (data.loginRoles.firstName === null) 
          return dispatch({
            type: UserActionTypes.SIGN_IN_ERROR,
            payload: {}
          })

        let { emailId, firstName, lastName, roles, services, superAdmin, creationDate } =  data.loginRoles 
        let { jwtToken } = data 
        Cookies.set('username', emailId, { expires: 1/6 })
        Cookies.set('jwt', jwtToken, {expires: 1/6})
        Cookies.set('role', roles, { expires: 1/6 })
        Cookies.set('name', firstName + ' ' + lastName, { expires: 1/6 })
        Cookies.set('services', services, { expires: 1/6 })
        Cookies.set('creationDate', creationDate, { expires: 1/6 })
        return dispatch({
          type: UserActionTypes.SIGN_IN_SUCCESS,
          payload: { 
            username: emailId, 
            role: roles, 
            jwtToken: jwtToken,
            name: firstName + ' ' + lastName ,
            services: services,
            superAdmin: superAdmin
          }
        })        
      }
      }))
      .catch(error => {
        return dispatch({
          type: UserActionTypes.SIGN_IN_ERROR,
          payload: error
        })
      })
  }
}

/**
 * Authenticate the user
 * @param {string} username Email ID
 * @param {string} passcode password
 * @param {string} jwt token
 */
export const authenticateUser = (username, passcode, jwt) => {
  jwt = Cookies.get('jwt');
  return (dispatch) => {
    dispatch({type: UserActionTypes.AUTHENTICATE_USER_FETCHING, payload: {} })
    let authHeaders = jwt ? {
        'Content-type': 'application/json',
        'Authorization': 'Bearer ' + jwt
    } 
    : {'Content-type': 'application/json'} 
    fetch(baseURL + '/login', {
      method: 'post',
      headers: authHeaders,
      body: JSON.stringify({
        emailId: username,
        passcode,
        jwt: null
      })
    })
      .then(res => res.json())
      .then(res => {
        if (res.loginRoles.firstName === null) 
          return dispatch({
            type: UserActionTypes.AUTHENTICATE_USER_ERROR,
            payload: {}
          })
        let { emailId, firstName, lastName, roles, services, superAdmin, creationDate } =  res.loginRoles 
        let { jwtToken } = res   
        console.log("EmailID =",emailId)
        Cookies.set('username', emailId, { expires: 1/6 })
        Cookies.set('jwt', jwtToken, {expires: 1/6})
        Cookies.set('role', roles, { expires: 1/6 })
        Cookies.set('name', firstName + ' ' + lastName, { expires: 1/6 })
        Cookies.set('services', services, { expires: 1/6 })
        Cookies.set('creationDate', creationDate, { expires: 1/6 })

        return dispatch({
          type: UserActionTypes.AUTHENTICATE_USER_SUCCESS,
          payload: { 
            username: emailId, 
            role: roles, 
            jwtToken: jwtToken,
            name: firstName + ' ' + lastName ,
            services: services,
            superAdmin: superAdmin
          }
        })
      })
      .catch(error => {
        return dispatch({
          type: UserActionTypes.AUTHENTICATE_USER_ERROR,
          payload: { error }
        })
      })
  }
}

/**
 * Log out the user.
 */
export const signOutUser = () => {
  Cookies.remove('username')
  Cookies.remove('role')
  Cookies.remove('jwt')
  Cookies.remove('name')
  Cookies.remove('user_email')

  return {
    type: UserActionTypes.SIGN_OUT,
    payload: {}
  }
}

/**
 * Clear any eror updates.
 */
export const clearSignInError = () => {
  return {
    type: UserActionTypes.CLEAR_SIGN_IN_ERROR,
    payload: {}
  }
}