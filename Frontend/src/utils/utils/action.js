  /**
   * Re-usable dispatch & fetch calls
   * @param {object} dispatch // passed down dispatch 
   * @param {string} fetchType // type of fetch
   * @param {object} payloadValue // payload if any
   * @param {string} dispatchType // action type, without the _FETCHING, _SUCCESS, _ERROR text.
   * @param {string} apiLink  // fetch api link
   * @param {string} token  // auth token
   * 
   */

export const dispatchAndFetch = (dispatch, fetchType, payloadValue, dispatchType, apiLink, token) => {
    dispatch({ type: `${dispatchType}_FETCHING`, payload: payloadValue })
    fetch(apiLink, {
        method: fetchType,
        headers: {
            'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
        }
    })
    .then(res => res.json())
    .then(res => {
        return dispatch({
        type: `${dispatchType}_SUCCESS`,
        payload: { data: res }
        })
    })
    .catch(error => {
        return dispatch({
            type:  `${dispatchType}_ERROR`,
            payload: { error }
        })
    })
  } 
  
    /**
   * Re-usable dispatch & POST calls
   * 
   * @param {object} dispatch // passed down dispatch 
   * @param {string} fetchType // type of fetch
   * @param {object} payloadValue // payload if any
   * @param {string} dispatchType // action type, without the _FETCHING, _SUCCESS, _ERROR text.
   * @param {string} apiLink  // fetch api link
   * @param {string} dataLogic  // data logic for response, if any is necessary.
   * @param {string} token  // auth token
   * 
   */

  export const dispatchAndPost = (dispatch, fetchType, payloadValue, dispatchType, apiLink, dataLogic, token) => {
    dispatch({ type: `${dispatchType}_FETCHING`, payload: payloadValue })
    fetch(apiLink, {
        method: fetchType,
        headers: {
            'Content-type': 'application/json',
            'Authorization': 'Bearer ' + token
        },
        body: payloadValue
    })
    .then(res => {
        if(res.status !== 200) {
            console.log('res.stat=', res.message)
        } else {
            dataLogic(res)
            return dispatch({
                type: `${dispatchType}_SUCCESS`,
                payload: { data: res }
            })
        }

    })
    .catch(error => {
        return dispatch({
            type:  `${dispatchType}_ERROR`,
            payload: { error }
        })
    })
  } 
  