import AdminActionTypes from '../actions/actionTypes/AdminActionTypes';


const initialState = {
    adminMetadata: [],
    adminAddRolesSuccess: false,
    adminAddRolesError: false,
    adminAddRolesErrorData: '',
    adminRemoveRolesSuccess: false,
    adminRemoveRolesError: false,
    adminRemoveRolesErrorData: '',
    getUserRolesSuccess: false,
    getUserRolesFetching: false,
    getUserRolesError: false,
    getUserRolesErrorData: '',
    getUserRolesData: {},
    postAccountSetupSuccess: false,
    postAccountSetupFetching: false,
    postAccountSetupError: false,
    postAccountSetupErrorMessage: '',
    getConfigDefinitionsSuccess: false,
    getConfigDefinitionsFetching: false,
    getConfigDefinitionsError: false,
    getConfigDefinitionsErrorMessages: '',
    getConfigDefinitionsData: [],
    getConfigSuccess: false,
    getConfigFetching: false,
    getConfigError: false,
    getConfigErrorMessages: '',
    getConfigData: [],
    getConfigByIdSuccess: false,
    getConfigByIdFetching: false,
    getConfigByIdError: false,
    getConfigByIdErrorMessages: '',
    getConfigByIdData: [],
    putConfigByIdSuccess: false,
    putConfigByIdFetching: false,
    putConfigByIdError: false,
    putConfigByIdErrorMessages: '',
    putConfigByIdData: [],
    decryptConfigSuccess: false,
    decryptConfigFetching: false,
    decryptConfigError: false,
    decryptConfigErrorMessages: '',
    decryptConfigData: {},
    resetPasswordError: false,
    resetPasswordSuccess: false,
    resetPasswordErrorMessage: ''
}

export const adminMetadata = (state = initialState, action) => {
    switch (action.type) {
        case AdminActionTypes.ADMIN_ADD_ROLES_SUCCESS:
            return {
            ...state,
            adminAddRolesSuccess: true,
            adminAddRolesError: false
            }
      
        case AdminActionTypes.ADMIN_ADD_ROLES_ERROR:
            return {
            ...state,
            adminAddRolesError: true,
            adminAddRolesErrorData: action.payload.message,
            adminAddRolesSuccess: false
            }

        case AdminActionTypes.ADMIN_REMOVE_ROLES_SUCCESS:
            return {
            ...state,
            adminRemoveRolesSuccess: true,
            adminRemoveRolesError: false
            }
      
        case AdminActionTypes.ADMIN_REMOVE_ROLES_ERROR:
            return {
            ...state,
            adminRemoveRolesError: true,
            adminRemoveRolesErrorData: action.payload.message,
            adminRemoveRolesSuccess: false
            }

        case AdminActionTypes.GET_USER_ROLES_FETCH:
            return {
                ...state,
                getUserRolesSuccess: false,
                getUserRolesFetching: true,
                getUserRolesError: false,
            }

        case AdminActionTypes.GET_USER_ROLES_SUCCESS:
            return {
                ...state,
                getUserRolesSuccess: true,
                getUserRolesFetching: false,
                getUserRolesError: false,
                getUserRolesErrorData: '',
                getUserRolesData: action.payload.data
            }

        case AdminActionTypes.GET_USER_ROLES_ERROR:
            return {
                ...state,
                getUserRolesSuccess: false,
                getUserRolesFetching: false,
                getUserRolesError: true,
                getUserRolesErrorData: action.payload.message,
            }   
        case AdminActionTypes.POST_ACCOUNT_FETCHING:
            return {
                ...state,
                postAccountSetupSuccess: false,
                postAccountSetupFetching: true,
                postAccountSetupError: false,
                postAccountSetupErrorMessage: '',
            } 
        case AdminActionTypes.POST_ACCOUNT_SUCCESS:
            return {
                ...state,
                postAccountSetupSuccess: true,
                postAccountSetupFetching: false,
                postAccountSetupError: false,
                postAccountSetupErrorMessage: '',
            } 
        case AdminActionTypes.POST_ACCOUNT_ERROR:
            return {
                ...state,
                postAccountSetupSuccess: false,
                postAccountSetupFetching: false,
                postAccountSetupError: true,
                postAccountSetupErrorMessage: action.payload
            }  
        case AdminActionTypes.GET_CONFIG_DEFINITIONS_ERROR:
            return {
                ...state,
                getConfigDefinitionsSuccess: false,
                getConfigDefinitionsFetching: false,
                getConfigDefinitionsError: true,
                getConfigDefinitionsErrorMessages: action.payload.error
            }  
        case AdminActionTypes.GET_CONFIG_DEFINITIONS_FETCHING:
            return {
                ...state,
                getConfigDefinitionsSuccess: false,
                getConfigDefinitionsFetching: true,
                getConfigDefinitionsError: false,
                getConfigDefinitionsErrorMessages: ''
            }
        case AdminActionTypes.GET_CONFIG_DEFINITIONS_SUCCESS:
            return {
                ...state,
                getConfigDefinitionsSuccess: true,
                getConfigDefinitionsFetching: false,
                getConfigDefinitionsError: false,
                getConfigDefinitionsErrorMessages: '',
                getConfigDefinitionsData: action.payload.data
            }
        case AdminActionTypes.GET_CONFIG_ERROR:
            return {
                ...state,
                getConfigSuccess: false,
                getConfigFetching: false,
                getConfigError: true,
                getConfigErrorMessages: action.payload
            }  
        case AdminActionTypes.GET_CONFIG_FETCHING:
            return {
                ...state,
                getConfigSuccess: false,
                getConfigFetching: true,
                getConfigError: false,
                getConfigErrorMessages: ''
            }
        case AdminActionTypes.GET_CONFIG_SUCCESS:
            return {
                ...state,
                getConfigSuccess: true,
                getConfigFetching: false,
                getConfigError: false,
                getConfigErrorMessages: '',
                getConfigData: action.payload.data
            }
        case AdminActionTypes.GET_CONFIG_BY_ID_ERROR:
            return {
                ...state,
                getConfigByIdSuccess: false,
                getConfigByIdFetching: false,
                getConfigByIdError: true,
                getConfigByIdErrorMessages: action.payload.error
            }  
        case AdminActionTypes.GET_CONFIG_BY_ID_FETCHING:
            return {
                ...state,
                getConfigByIdSuccess: false,
                getConfigByIdFetching: true,
                getConfigByIdError: false,
                getConfigByIdErrorMessages: ''
            }
        case AdminActionTypes.GET_CONFIG_BY_ID_SUCCESS:
            return {
                ...state,
                getConfigByIdSuccess: true,
                getConfigByIdFetching: false,
                getConfigByIdError: false,
                getConfigByIdErrorMessages: '',
                getConfigByIdData: action.payload.data
            }
        case AdminActionTypes.PUT_CONFIG_BY_ID_ERROR:
            return {
                ...state,
                putConfigByIdSuccess: false,
                putConfigByIdFetching: false,
                putConfigByIdError: true,
                putConfigByIdErrorMessages: action.payload
            }  
        case AdminActionTypes.PUT_CONFIG_BY_ID_FETCHING:
            return {
                ...state,
                putConfigByIdSuccess: false,
                putConfigByIdFetching: true,
                putConfigByIdError: false,
                putConfigByIdErrorMessages: ''
            }
        case AdminActionTypes.PUT_CONFIG_BY_ID_SUCCESS:
            return {
                ...state,
                putConfigByIdSuccess: true,
                putConfigByIdFetching: false,
                putConfigByIdError: false,
                putConfigByIdErrorMessages: '',
                putConfigByIdData: action.payload.data
            }
        case AdminActionTypes.DECRYPT_CONFIG_ERROR:
            return {
                ...state,
                decryptConfigSuccess: false,
                decryptConfigFetching: false,
                decryptConfigError: true,
                decryptConfigErrorMessages: action.payload
            }  
        case AdminActionTypes.DECRYPT_CONFIG_FETCHING:
            return {
                ...state,
                decryptConfigSuccess: false,
                decryptConfigFetching: true,
                decryptConfigError: false,
                decryptConfigErrorMessages: ''
            }
        case AdminActionTypes.DECRYPT_CONFIG_SUCCESS:
            return {
                ...state,
                decryptConfigSuccess: true,
                decryptConfigFetching: false,
                decryptConfigError: false,
                decryptConfigErrorMessages: '',
                decryptConfigData: action.payload.data
            } 
        case AdminActionTypes.CLEAR_STATUS:
            return {
                ...state,
                resetPasswordError: false,
                resetPasswordSuccess: false,
                resetPasswordErrorMessage: '',
                putConfigByIdSuccess: false
            }   
        case AdminActionTypes.RESET_PASSWORD_ERROR:
            return {
                ...state,
                resetPasswordError: true,
                resetPasswordErrorMessage: action.payload,
                resetPasswordSuccess: false
            }    
        case AdminActionTypes.RESET_PASSWORD_SUCCESS:
            return {
                ...state,
                resetPasswordError: false,
                resetPasswordSuccess: true
            }
        case AdminActionTypes.CLEAR_CONFIG:
            return {
                ...state,
                getConfigByIdSuccess: false,
                getConfigByIdFetching: false,
                getConfigByIdError: false,
                getConfigByIdErrorMessages: '',
                getConfigByIdData: [],
            }                                     
        default:
            return state
    }
}