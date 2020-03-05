import { adminMetadata } from 'Reducers/Admin';
import AdminActionTypes from '../../actions/actionTypes/AdminActionTypes';

describe('Admin Reducer Testing', () => {
    const initialState = {
        adminMetadata: [],
        adminAddRolesSuccess: false,
        adminAddRolesError: false,
        adminAddRolesErrorData: '',
        adminRemoveRolesSuccess: false,
        adminRemoveRolesError: false,
        adminRemoveRolesErrorData: '',
        getUserRolesSuccess: false,
        getUserRolesFetching: true,
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
        resetPasswordErrorMessage: '',
        getConfigData: [],
        getConfigError: false,
        getConfigErrorMessages: "",
        getConfigFetching: false,
        getConfigSuccess: false
    };

    it('should return the inital stage', () => {
        expect(adminMetadata(undefined, {})).toEqual(initialState);
    })

    it('should handle ADMIN_ADD_ROLES_FETCHING', () => {
        let dataToInsert = initialState;
        const action = {
            type: AdminActionTypes.ADMIN_ADD_ROLES_FETCHING,
            payload: {
                data: {}
            }
        };
        expect(adminMetadata(undefined, action)).toEqual(dataToInsert);
    })

    it('should handle ADMIN_ADD_ROLES_SUCCESS', () => {
        const dataToInsert = {
            "adminAddRolesError": false, 
            "adminAddRolesErrorData": "", 
            "adminAddRolesSuccess": true, 
            "adminMetadata": [], 
            "adminRemoveRolesError": false, 
            "adminRemoveRolesErrorData": "", 
            "adminRemoveRolesSuccess": false, 
            "decryptConfigData": {}, 
            "decryptConfigError": false, 
            "decryptConfigErrorMessages": "", 
            "decryptConfigFetching": false, 
            "decryptConfigSuccess": false, 
            "getConfigByIdData": [], 
            "getConfigByIdError": false, 
            "getConfigByIdErrorMessages": "", 
            "getConfigByIdFetching": false, 
            "getConfigByIdSuccess": false, 
            "getConfigDefinitionsData": [], 
            "getConfigDefinitionsError": false, 
            "getConfigDefinitionsErrorMessages": "", 
            "getConfigDefinitionsFetching": false, 
            "getConfigDefinitionsSuccess": false, 
            "getUserRolesData": {}, 
            "getUserRolesError": false, 
            "getUserRolesErrorData": "", 
            "getUserRolesFetching": false, 
            "getUserRolesSuccess": false, 
            "postAccountSetupError": false, 
            "postAccountSetupErrorMessage": "", 
            "postAccountSetupFetching": false, 
            "postAccountSetupSuccess": false, 
            "putConfigByIdData": [], 
            "putConfigByIdError": false, 
            "putConfigByIdErrorMessages": "", 
            "putConfigByIdFetching": false, 
            "putConfigByIdSuccess": false, 
            "resetPasswordError": false, 
            "resetPasswordErrorMessage": "", 
            "resetPasswordSuccess": false,
            "getConfigData": [],
            "getConfigError": false,
            "getConfigErrorMessages": "",
            "getConfigFetching": false,
            "getConfigSuccess": false,
        }
        const action = {
            type: AdminActionTypes.ADMIN_ADD_ROLES_SUCCESS,
            payload: {
                data: {}
            }
        };
        expect(adminMetadata(undefined, action)).toEqual(dataToInsert);
    })

    it('should handle GET_USER_ROLES_SUCCESS', () => {
        let dataToInsert = initialState;
        dataToInsert.getUserRolesFetching = false;
        dataToInsert.getUserRolesSuccess = true;
        dataToInsert.adminAddRolesSuccess = false;
        const action = {
            type: AdminActionTypes.GET_USER_ROLES_SUCCESS,
            payload: {
                data: {}
            }
        };
        expect(adminMetadata(undefined, action)).toEqual(dataToInsert);
    })
    
});