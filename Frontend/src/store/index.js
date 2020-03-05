import { createStore, applyMiddleware } from 'redux'
import thunk from 'redux-thunk'
import Cookies from 'js-cookie'
import rootReducer from '../reducers'
import { authenticateUser, signOutUser, clearSignInError } from '../actions/user.js'
import { composeWithDevTools } from 'redux-devtools-extension'

//window.__REDUX_DEVTOOLS_EXTENSION__ && window.__REDUX_DEVTOOLS_EXTENSION__(),  
let defaultState = {}


/**
 * - Middleware checks per action if user has valid token.
 */

const customMiddleWare = store => next => action => {
  
  if(['SIGN_IN_SUCCESS', 'SIGN_IN_FETCHING', 'SIGN_OUT', 'CLEAR_SIGN_IN_ERROR', 'CLEAR_STATUS'].includes(action.type)) {

  /**
   * - SKIP CHECK: User is signing in or signing out
   */  
    next(action);
  } else if (['AUTHENTICATE_USER_SUCCESS', 'AUTHENTICATE_USER_FETCHING'].includes(action.type)) {
  /**
   * - SKIP CHECK: User is succesfully authenticated or currently authenticating
   */
    next(action);
  } else if (['AUTHENTICATE_USER_ERROR', 'SIGN_IN_ERROR'].includes(action.type)) {

  /**
   *  - SKIP CHECK: Uer has failed to authenticate or sign in.
   *  - User will be redirected to login page if failed to authenticate
   */

    console.log('******** SIGN_IN/AUTH ERR MIDDLEWARE ************************  FAILED TO AUTHENTICATE/SIGN IN', action)

    Cookies.remove('username')
    Cookies.remove('passcode')
    Cookies.remove('role')
    Cookies.remove('jwt')
    Cookies.remove('name')
    Cookies.remove('user_email')

    if(['SIGN_IN_ERROR'].includes(action.type)) {console.log('this is a sign In err', action)}
    if(['AUTHENTICATE_USER_ERROR'].includes(action.type)) {console.log('this is an authentication err', action)}
    
    store.dispatch(signOutUser)
    store.dispatch(clearSignInError)
    next(action);
  } else if (action.type.includes('SUCCESS') || action.type.includes('ERROR')) {
      /**
       * - SKIP CHECK: Action has already been authenticated from 'FETCHING' phase.
       */
      
      next(action);
  } else {
    
  /**
   * - Logic code for authentication middleware
   */
    
    console.log('******** API MIDDLEWARE ************************ CHECKING VALID JWT', action)
    
    let username = Cookies.get('username') ? Cookies.get('username') : ''
    let passcode = Cookies.get('passcode') ? Cookies.get('passcode') : ''
    let jwt = Cookies.get('jwt') ? Cookies.get('jwt') : ''
    
    store.dispatch(authenticateUser(null, null, jwt))
    next(action);
  }

}

const store = createStore(
  rootReducer, 
  defaultState,
  composeWithDevTools(applyMiddleware(thunk, customMiddleWare))
)

export default store