import { profileMetadata } from 'Reducers/Profile'
import ProfileActionTypes from '../../actions/actionTypes/AdminActionTypes';

describe('Profile Reducer testing', () => {
    const initialState = {
        profileMetadata: [],
        getSubscriptionFetching: false,
        getSubscriptionSuccess: false,
        getSubscriptionError: false,
        getSubscriptions: {},
        updateSubscriptionsSuccess: false,
        updateSubscriptionsError: false,
        updateSubscriptionsErrorData: {}
    }

    it('should return the initial stage', () => {
        expect(profileMetadata(undefined, {})).toEqual(initialState);
    })

    it('should handle GET_SUBSCRIPTIONS_FETCHING', () => {
        let dataToInsert = initialState;
        dataToInsert.updateSubscriptionsErrorData = {}
        const action = {
            type: ProfileActionTypes.GET_SUBSCRIPTIONS_FETCHING,
            payload: {
                data: {}
            }
        };
        expect(profileMetadata(undefined, action)).toEqual(dataToInsert)
    })

    it('should handle GET_SUBSCRIPTIONS_SUCCESS', () => {
        const dataToInsert = {
            profileMetadata: [],
            getSubscriptionFetching: false,
            getSubscriptionSuccess: false,
            getSubscriptionError: false,
            getSubscriptions: {},
            updateSubscriptionsSuccess: false,
            updateSubscriptionsError: false,
            updateSubscriptionsErrorData: {}
        }
        const action = {
            type: ProfileActionTypes.GET_SUBSCRIPTIONS_SUCCESS,
            payload: {
                data: {}
            }
        };
        expect(profileMetadata(undefined, action)).toEqual(dataToInsert)
    })
})