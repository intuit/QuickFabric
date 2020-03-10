import { user } from 'Reducers/User';
import UserActionTypes from '../../actions/actionTypes/AdminActionTypes';

describe('User Reducer testing', () => {
    const initialState = {
        signedIn: false,
        signInFetching: false,
        signInError: false,
        signInErrorData: {},
        authenticateUserFetching: false, 
        authenticateUserSuccess: false, 
        authenticateUserError: false
      }
    
    it('should return the initial stage', () => {
        expect(user(undefined, {})).toEqual(initialState);
    })

    it('should handle SIGN_IN_FETCHING', () => {
        const dataToInsert = {
            signedIn: false,
            signInFetching: false,
            signInError: false,
            signInErrorData: {},
            authenticateUserFetching: false, 
            authenticateUserSuccess: false, 
            authenticateUserError: false
          }
        const action = {
            type: UserActionTypes.SIGN_IN_FETCHING,
            payload: {
                data: {}
            }
        };
        expect(user(undefined, action)).toEqual(dataToInsert)
    })

    it('should handle SIGN_IN_SUCCESS', () => {
        const dataToInsert = {
            signedIn: false,
            signInFetching: false,
            signInError: false,
            signInErrorData: {},
            authenticateUserFetching: false, 
            authenticateUserSuccess: false, 
            authenticateUserError: false
          }
        const action = {
            type: UserActionTypes.SIGN_IN_SUCCESS,
            payload: {
                data: {}
            }
        };
        expect(user(undefined, action)).toEqual(dataToInsert)
    })

    it('should handle SIGN_IN_ERROR', () => {
        const dataToInsert = {
            signedIn: false,
            signInFetching: false,
            signInError: false,
            signInErrorData: {},
            authenticateUserFetching: false, 
            authenticateUserSuccess: false, 
            authenticateUserError: false
          }
        const action = {
            type: UserActionTypes.SIGN_IN_ERROR,
            payload: {
                data: {}
            }
        };
        expect(user(undefined, action)).toEqual(dataToInsert)
    })

    it('should handle SIGN_OUT', () => {
        const action = {
            type: UserActionTypes.SIGN_OUT,
            payload: {
                data: {}
            }
        };
        expect(user(undefined, action)).toEqual(initialState)
    })
})