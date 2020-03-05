import { emrCostData } from 'Reducers/EMRCost';
import EMRCostActionTypes from '../../actions/actionTypes/AdminActionTypes';

describe('EMR Cost Reducer testing', () => {
    const initialState = {
        clusterCostCustomData: {},
        clusterCostCustomError: false,
        clusterCostCustomErrorMessage: "",
        clusterCostCustomFetching: true,
        clusterCostCustomSuccess: false,
        clusterCostMonthlyData: {},
        clusterCostMonthlyError: false,
        clusterCostMonthlyErrorMessage: "",
        clusterCostMonthlyFetching: true,
        clusterCostMonthlySuccess: false,
        clusterCostWeeklyData: {},
        clusterCostWeeklyError: false,
        clusterCostWeeklyErrorMessage: "",
        clusterCostWeeklyFetching: true,
        clusterCostWeeklySuccess: false,
        emrCostData: [],
        emrCostError: false,
        emrCostFetching: false,
        emrCostSuccess: false
    }

    it('should return the initial stage', () => {
        expect(emrCostData(undefined, {})).toEqual(initialState);
    })

    it('should handle EMRCOST_STATUS_FETCHING', () => {
        const dataToInsert = {
            emrCostData: [],
            emrCostFetching: false,
            emrCostSuccess: false,
            emrCostError: false,
            clusterCostCustomData: {},
            clusterCostCustomError: false,
            clusterCostCustomErrorMessage: "",
            clusterCostCustomFetching: true,
            clusterCostCustomSuccess: false,
            clusterCostMonthlyData: {},
            clusterCostMonthlyError: false,
            clusterCostMonthlyErrorMessage: "",
            clusterCostMonthlyFetching: true,
            clusterCostMonthlySuccess: false,
            clusterCostWeeklyData: {},
            clusterCostWeeklyError: false,
            clusterCostWeeklyErrorMessage: "",
            clusterCostWeeklyFetching: true,
            clusterCostWeeklySuccess: false,
        }
        const action = {
            type: EMRCostActionTypes.EMRCOST_STATUS_FETCHING,
            payload: {
                data: {}
            }
        };
        expect(emrCostData(undefined, action)).toEqual(dataToInsert)
    })

    it('should handle EMRCOST_STATUS_SUCCESS', () => {
        const dataToInsert = {
            emrCostData: [],
            emrCostFetching: false,
            emrCostSuccess: false,
            emrCostError: false,
            clusterCostWeeklyFetching: true,
            clusterCostWeeklySuccess: false,
            clusterCostWeeklyError: false,
            clusterCostWeeklyErrorMessage: '',
            clusterCostWeeklyData: {},
            clusterCostMonthlyFetching: true,
            clusterCostMonthlySuccess: false,
            clusterCostMonthlyError: false,
            clusterCostMonthlyErrorMessage: '',
            clusterCostMonthlyData: {},
            clusterCostCustomFetching: true,
            clusterCostCustomSuccess: false,
            clusterCostCustomError: false,
            clusterCostCustomErrorMessage: '',
            clusterCostCustomData: {}
        }
        const action = {
            type: EMRCostActionTypes.EMRCOST_STATUS_SUCCESS,
            payload: {
                data: {}
            }
        };
        expect(emrCostData(undefined, action)).toEqual(dataToInsert)
    })

    it('should handle EMRCOST_STATUS_ERROR', () => {
        const dataToInsert = {
            emrCostData: [],
            emrCostFetching: false,
            emrCostSuccess: false,
            emrCostError: false,
            clusterCostWeeklyFetching: true,
            clusterCostWeeklySuccess: false,
            clusterCostWeeklyError: false,
            clusterCostWeeklyErrorMessage: '',
            clusterCostWeeklyData: {},

            clusterCostMonthlyFetching: true,
            clusterCostMonthlySuccess: false,
            clusterCostMonthlyError: false,
            clusterCostMonthlyErrorMessage: '',
            clusterCostMonthlyData: {},

            clusterCostCustomFetching: true,
            clusterCostCustomSuccess: false,
            clusterCostCustomError: false,
            clusterCostCustomErrorMessage: '',
            clusterCostCustomData: {}
        }
        const action = {
            type: EMRCostActionTypes.EMRCOST_STATUS_ERROR,
            payload: {
                data: {}
            }
        };
        expect(emrCostData(undefined, action)).toEqual(dataToInsert)
    })
});
