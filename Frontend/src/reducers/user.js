import UserActionTypes from '../actions/actionTypes/UserActionTypes';

const initialState = {
  signedIn: false,
  signInFetching: false,
  signInError: false,
  signInErrorData: {},
  authenticateUserFetching: false, 
  authenticateUserSuccess: false, 
  authenticateUserError: false
}

export const user = (state = initialState, action) => {
  switch (action.type) {
  case UserActionTypes.AUTHENTICATE_USER_SUCCESS:
  {    
    console.log(action.payload)
    const { username, passcode, role, jwtToken, name, services, superAdmin } = action.payload
    return {
      ...state,
      jwtToken
    }
  }

  case UserActionTypes.AUTHENTICATE_USER_ERROR:
    return { 
      ...state, 
      signInFetching: false,
      signedIn: false,
      signInError: true
    }
  case UserActionTypes.SIGN_IN_FETCHING:
    return { 
      ...state, 
      signInFetching: true,
      signedIn: false,
      signInError: false
    }

  case UserActionTypes.SIGN_IN_SUCCESS:
  {
    const { username, passcode, role, jwtToken, name, services, superAdmin } = action.payload
    return {
      ...state,
      signedIn: true,
      signInFetching: false,
      signInError: false,
      username,
      passcode,
      role,
      jwtToken,
      name,
      services,
      superAdmin
    }
  }

  case UserActionTypes.SIGN_IN_ERROR:
    return { 
      ...state, 
      signInFetching: false,
      signedIn: false,
      signInError: true,
      signInErrorData: action.payload
    }
      
  case UserActionTypes.SIGN_OUT:
    
  case UserActionTypes.CLEAR_SIGN_IN_ERROR:
    return initialState

  default:
    return state
  } 
}
