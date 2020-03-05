import AdminActionTypes from './actionTypes/AdminActionTypes'
import Cookies from 'js-cookie';
import {dispatchAndFetch, dispatchAndPost} from './../utils/utils/action'
import baseURL from '../api-config';

/**
 * Post call to add new roles to a user.
 * @param {json} data contains user email Id and new roles to be added in json format
 * @param {string} token jwt token
 */
export const postAddRoles = (data, token) => {
  token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/admin/roles/add', {
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data => {
        if (res.status !== 200) {
          throw data.errorMessage
        } else {
          return dispatch({
            type: AdminActionTypes.ADMIN_ADD_ROLES_SUCCESS,
            payload: {}
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: AdminActionTypes.ADMIN_ADD_ROLES_ERROR,
          payload: { message: m }
        })
      })
    }
  }
  
  /**
   * Post call to remove roles for a user.
   * @param {json} data contains user email Id and new roles to be removed in json format
   * @param {string} token jwt token
   */
  export const postRemoveRoles = (data, token) => {
    token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/admin/roles/remove', {
        method: 'POST',
        headers: {
          'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(res => res.json().then (data => {
        if (res.status !== 200) {
          throw data.errorMessage
        } else {
          return dispatch({
            type: AdminActionTypes.ADMIN_REMOVE_ROLES_SUCCESS,
            payload: {}
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: AdminActionTypes.ADMIN_REMOVE_ROLES_ERROR,
          payload: { message: m }
        })
      })
    }
  }

  /**
   * Get existing roles for a user.
   * @param {string} email user email Id
   * @param {string} token jwt token
   */
  export const fetchUserRoles = (email, token) => {
    token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + '/user/roles/' + email, {
        method: 'GET',
        headers: {
          'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
      })
      .then(res => res.json().then(res => {
        if(res.status) {
          return dispatch({
            type: AdminActionTypes.GET_USER_ROLES_ERROR,
            payload: { message: res }
          })
        } else {
          return dispatch({
            type: AdminActionTypes.GET_USER_ROLES_SUCCESS,
            payload: {data: res}
          })
        }
      }))
      .catch(m => {
        return dispatch({
          type: AdminActionTypes.GET_USER_ROLES_ERROR,
          payload: { message: m }
        })
      })
    }
  }

  export const resetUserPassword = (data, token) => {
    token = Cookies.get('jwt');
    return dispatch => {
      fetch(baseURL + `/admin/reset-password`, {
        method: 'PUT',
        headers: {
          'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: JSON.stringify(data)
      })
      .then(data => {
        if (data.status !== 200) {
          throw data
        } else {
          return dispatch({
            type: AdminActionTypes.RESET_PASSWORD_SUCCESS,
            payload: {}
          })
        }
      })
      .catch(m => {
        return dispatch({
          type: AdminActionTypes.RESET_PASSWORD_ERROR,
          payload: m
        })
      })
    }
  }
  

  ////////////////////////////////////////////////////////////
  //                    Account-Setup                       //
  ////////////////////////////////////////////////////////////



  /**
   * Post details for Account Set-up.
   * @param {array} accountDetails 
   */
  export const postAccountSetup = (data, token) => {
    token = Cookies.get('jwt');
    let dataLogic = (res) => {
      console.log('Post Account *** response:', res)
    }
    return dispatch => {
      let apiUrl = baseURL + '/admin/account-setup'
      dispatchAndPost(dispatch, 'POST', JSON.stringify(data), 'POST_ACCOUNT', apiUrl, dataLogic, token) 
    }
  }

  /**
   * Get function for Config Definitions
   */

  export const fetchConfigDefinitions = (token) => {
    token = Cookies.get('jwt');
    let dataLogic = (res) => {
      console.log('Fetch Config Definitions *** response:', res)
    }
    return dispatch => {
      let apiURL = baseURL + '/configurations/definitions'
      dispatchAndFetch(dispatch, 'GET', {}, 'GET_CONFIG_DEFINITIONS', apiURL, token) 
    }
  }
  
  /**
   * Put function for Config Definitions
   * Data params
   * @param {string} configName
   * @param {string} configValue
   * @param {boolean} isEncrypted
   * @param {string} configDataType
   * @param {string} configType
   * @param {string} isMandatory
   * @param {boolean} isEncryptionRequired
   * @param {accountId} accountId
   */

  export const putConfigDefinitions = (id, data, token) => {
    token = Cookies.get('jwt');
    return dispatch => {
      let apiUrl = baseURL + '/configurations'
      let dataLogic = (res) => {
        console.log('res', res)
      }
      dispatchAndPost(dispatch, 'POST', JSON.stringify(data), 'PUT_CONFIG_BY_ID', apiUrl, dataLogic, token)
    }
  }
  /**
   * Get function for Config Definitions
   */

  export const fetchConfigDefinitionList = (token) => {
    token = Cookies.get('jwt');
    return dispatch => {
      dispatch({type: AdminActionTypes.GET_CONFIG_FETCHING})
      fetch(baseURL + `/configurations/list/`, {
        method: 'GET',
        headers: {
          'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
      })
      .then(res => res.json().then(res => {
        let response = res.map((c, i) => {
          if(!c.configId) {
            c.configId = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
          }
          if(c.configDataType === 'Boolean' &&  c.configValue == null || c.configDataType === 'Boolean' && c.configValue == undefined)  { 
            c.isNull = true 
          } 
          if(c.configDataType === 'String' &&  c.configValue == null || c.configDataType === 'String' && c.configValue == undefined) {
            c.isNull = true 
          } 
          if(c.configDataType === 'Int' &&  c.configValue == null || c.configDataType === 'Int' &&  c.configValue == undefined || c.configDataType === 'Int' &&  c.configValue === 'False' || c.configDataType === 'Int' &&  c.configValue === 'false') {
            c.isNull = true 
          }
          c.encryptValue = c.encrypted ? c.configValue : '';
          return c;
        })

          return dispatch({
            type: AdminActionTypes.GET_CONFIG_SUCCESS,
            payload: {data: response}
          })
      }))
      .catch(e => {
        return dispatch({
          type: AdminActionTypes.GET_CONFIG_ERROR,
          payload: { err: e }
        })
      })
    }
  }
    
  /**
   * Get function for Config Definitions By Id
   */

  export const fetchConfigDefinitionsById = (id, token) => {
    token = Cookies.get('jwt');
    return dispatch => {
      dispatch({type: AdminActionTypes.GET_CONFIG_BY_ID_FETCHING})
      fetch(baseURL + `/configurations/list/${id}`, {
        method: 'GET',
        headers: {
          'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
      })
      .then(res => res.json().then(res => {
        let response = res.map((c, i) => {
          if(!c.configId) {
            c.configId = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
          }
          if(c.configDataType === 'Boolean' &&  c.configValue == null || c.configDataType === 'Boolean' && c.configValue == undefined)  { 
            c.isNull = true 
          } 
          if(c.configDataType === 'String' &&  c.configValue == null || c.configDataType === 'String' && c.configValue == undefined) {
            c.isNull = true 
          } 
          if(c.configDataType === 'Int' &&  c.configValue == null || c.configDataType === 'Int' &&  c.configValue == undefined || c.configDataType === 'Int' &&  c.configValue === 'False' || c.configDataType === 'Int' &&  c.configValue === 'false') {
            c.isNull = true 
          }
          c.encryptValue = c.isEncrypted ? c.configValue : '';

          return c;
        })

          return dispatch({
            type: AdminActionTypes.GET_CONFIG_BY_ID_SUCCESS,
            payload: {data: response}
          })
      }))
      .catch(e => {
        return dispatch({
          type: AdminActionTypes.GET_CONFIG_BY_ID_ERROR,
          payload: { err: e }
        })
      })
    }
  }
  
  /**
   * Decrypt function for Config, from 
   */

  export const decryptConfigByName = (name, accountId, token) => {
    token = Cookies.get('jwt');
    return dispatch => {
      let url = accountId === '' ? (baseURL + `/configurations/decrypt/${name}`) : (baseURL + `/configurations/decrypt/${name}/${accountId}`)
      console.log('curr Url', url)
      dispatch({type: AdminActionTypes.DECRYPT_CONFIG_FETCHING})
      fetch(url, {
        method: 'GET',
        headers: {
          'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
      })
      .then(res => res.json().then(res => {
          if(res.configDataType === 'Boolean' &&  typeof res.configValue === 'string') {
            res.configValue = converStrToBoolean(res.configValue)
            console.log('hi res', res.configValue)
          } if(res.configDataType === 'Boolean' &&  res.configValue == null || res.configDataType === 'Boolean' && res.configValue == undefined)  { 
            res.isNull = true 
          } if(res.configDataType === 'String' &&  res.configValue == null ||  res.configDataType === 'Boolean' && res.configValue == undefined) {
            res.isNull = true 
          } if(res.configDataType === 'Int' &&  res.configValue == null || res.configDataType === 'Boolean' &&  res.configValue == undefined || res.configDataType === 'Boolean' && res.configValue == 'False' || res.configDataType === 'Boolean' && res.configValue == 'false') {
            res.isNull = true 
          }

          return dispatch({
            type: AdminActionTypes.DECRYPT_CONFIG_SUCCESS,
            payload: {data: res}
          })
      }))
      .catch(e => {
        return dispatch({
          type: AdminActionTypes.DECRYPT_CONFIG_ERROR,
          payload: e
        })
      })
    }
  }
/**
 * Clear any eror updates.
 */
export const clearStatus = () => {
  return {
    type: AdminActionTypes.CLEAR_STATUS,
    payload: {}
  }
}
export const clearConfigs = () => {
  return {
    type: AdminActionTypes.CLEAR_CONFIG,
    payload: {}
  }
}
export const converStrToBoolean = (val) => {
  if(typeof val === 'string') {
      if(val === 'False' || val === 'false') {
          return false
      } else if(val === 'True' || val === 'true') {
          return true
      }
  } 
} 
