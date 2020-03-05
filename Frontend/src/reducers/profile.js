import ProfileActionTypes from '../actions/actionTypes/ProfileActionTypes';

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

export const profileMetadata = (state = initialState, action) => {
    switch(action.type) {
        case ProfileActionTypes.GET_SUBSCRIPTIONS_FETCHING:
            return {
                ...state,
                getSubscriptionFetching: true,
                getSubscriptionError: false,
                getSubscriptionSuccess: false
            }
        
        case ProfileActionTypes.GET_SUBSCRIPTIONS_SUCCESS:
            return {
                ...state,
                getSubscriptions: action.payload.data,
                getSubscriptionError: false,
                getSubscriptionFetching: false,
                getSubscriptionSuccess: true 
            }
        
        case ProfileActionTypes.GET_SUBSCRIPTIONS_ERROR:
            return {
                ...state,
                getSubscriptionError: true,
                getSubscriptionFetching: false,
                getSubscriptionSuccess: false
            }
        
        case ProfileActionTypes.UPDATE_SUBSCRIPTIONS_SUCCESS:
            return {
                ...state,
                updateSubscriptionsSuccess: true,
                updateSubscriptionsError: false
            }
        
        case ProfileActionTypes.UPDATE_SUBSCRIPTIONS_ERROR:
            return {
                ...state,
                updateSubscriptionsError: true,
                updateSubscriptionsSuccess: false,
                updateSubscriptionsErrorData: action.payload
            }

        case ProfileActionTypes.CLEAR_UPDATES:
            return {
                ...state,
                updateSubscriptionsError: false,
                updateSubscriptionsErrorData: '',
                updateSubscriptionsSuccess: false
            }
        
        default:
            return state
    }
}