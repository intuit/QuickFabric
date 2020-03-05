import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import React from 'react';
import { Provider } from 'react-redux';
import configureMockStore from "redux-mock-store";

import { Admin } from '../../../components';

const initialState = {
    uiListArray: {
        roles: [],
        accounts: [],
        response: {
            segments: []
        }
    }
}

const mockStore = configureMockStore();
const store = mockStore(initialState);
const component = shallow( 
    <Provider store={store}>
    <Admin {...initialState} />  
    </Provider>)    

describe('Testing for Admin', () => {
    it('Should render the component', () => {
        expect(toJson(component)).toMatchSnapshot();
    })
})