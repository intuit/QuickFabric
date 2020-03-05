import CreateCluster from 'Components/EMRManagement/CreateCluster';
import { shallow, mount } from 'enzyme';
import toJson from 'enzyme-to-json';
import { Provider } from 'react-redux';
import configureMockStore from 'redux-mock-store';
import React from 'react';

const mockStore = configureMockStore();
const initialState = {
    emrMetadataData: {
        clusterCloneData: []
    }
}
const uiListArray = {
    segments: ["sales","marketing","care"],
    accounts: ["100000000", "300000002", "400000011"]
}
const superAdmin = true;
const store = mockStore(initialState);

describe('Testing Create cluster form', () => {
    it('Should render the component', () => {
        const uiListArray = {
            segments: [],
            accounts: []
        }
        const component = shallow(
            <Provider store={store}>
                <CreateCluster uiListArray={uiListArray} />
            </Provider>
        );
        expect(toJson(component)).toMatchSnapshot();
    })

    it('Should have an initial state', () => {
        const component = mount(
            <Provider store={store}>
                <CreateCluster uiListData={uiListArray} superAdmin={superAdmin} />
            </Provider>
        );
        let startTime = new Date();
        startTime.setHours(0,0,0,0);
        let endTime = new Date();
        endTime.setHours(23,0,0,0);
        const componentInstance = component.find('CreateCluster').instance();
        expect(componentInstance.state).toEqual({
            clusterName: '',
            type: 'exploratory',
            subType: 'non-kerberos',
            clusterSegment: 'sales',
            account: '100000000',
            masterInstanceType: ['m5.xlarge'],
            coreInstanceCount: '0',
            coreInstanceType: ['m5.xlarge'],
            taskInstanceCount: '0',
            taskInstanceType: ['m5.xlarge'],
            coreEbsVolSize: '10',
            masterEbsVolSize: '10',
            taskEbsVolSize: '10',
            customAmiId: '',
            doTerminate: false,
            toggleStatusTerminate: false,
            headlessUsers: '',
            visible: false,
            steps: [],
            bootstrapActions: [],
            primaryButton: 'cluster',
            clusterBtnDisabled: false,
            hardwareBtnDisabled: true,
            configBtnDisabled: true,
            addBootstrapBtnDisabled: true,
            addStepBtnDisabled: true,
            reviewBtnDisabled: true,
            clusterEnabled: true,
            hardwareEnabled: false,
            configEnabled: false,
            addStepEnabled: false,
            reviewEnabled: false,
            coreInstanceCountError: false,
            taskInstanceCountError: false,
            amiIdError: false,
            headlessUsersError: false,
            stepNameError: false,
            jarLocationError: false,
            argsError: false,
            bootstrapNameError: false,
            bootstrapScriptError: false,
            addStepNextBtnDisabled: false,
            spinnerActive: true,
            stepErrors: [],
            bootstrapActionsErrors: [],
            isProd: false,
            showClusterName: true,
            servicenow: '',
            showConfirmation: false,
            autoAmiRotation: 'False',
            autoAMIRotation: false,
            toggleStatus: false,
            autopilotWindowStart: startTime,
            autopilotWindowEnd: endTime,
            autopilotWindowError: false,
            amiRotationSla: '30',
            autoScaling: false,
            autoScalingMin: 0,
            autoScalingMax: 0,
            instanceGroup: '',
            instanceMinCountError: false,
            instanceMaxCountError: false
        })
    })

    it('Should capture cluster name correctly onChange', () => {
        const component = mount(
            <Provider store={store}>
                <CreateCluster uiListData={uiListArray} superAdmin={superAdmin} />
            </Provider>
        );
        const componentInstance = component.find('CreateCluster');
        componentInstance.find('input').at(1).simulate("change", {
            target: { value: "testcluster" }
        });
        expect(componentInstance.state().clusterName).toEqual("testcluster")
    })

    it('Should capture cluster type onChange', () => {
        const component = mount(
            <Provider store={store}>
                <CreateCluster uiListData={uiListArray} superAdmin={superAdmin} />
            </Provider>
        );
        const componentInstance = component.find('CreateCluster');
        componentInstance.find('select').at(0).simulate('change', { target: { value: 'scheduled' } })
        expect(componentInstance.state().type).toEqual("scheduled")
    })

    it('Should capture cluster subtype onChange', () => {
        const component = mount(
            <Provider store={store}>
                <CreateCluster uiListData={uiListArray} superAdmin={superAdmin} />
            </Provider>
        );
        const componentInstance = component.find('CreateCluster');
        componentInstance.find('select').at(1).simulate('change', { target: { value: 'non-kerberos' } })
        expect(componentInstance.state().subType).toEqual("non-kerberos")
    })

    it('Should capture cluster Role onChange', () => {
        const component = mount(
            <Provider store={store}>
                <CreateCluster uiListData={uiListArray} superAdmin={superAdmin} />
            </Provider>
        );
        const componentInstance = component.find('CreateCluster');
        componentInstance.find('select').at(2).simulate('change', { target: { value: 'care' } })
        expect(componentInstance.state().clusterSegment).toEqual("care")
    })

    it('Should capture cluster Account onChange', () => {
        const component = mount(
            <Provider store={store}>
                <CreateCluster uiListData={uiListArray} superAdmin={superAdmin} />
            </Provider>
        );
        const componentInstance = component.find('CreateCluster');
        componentInstance.find('select').at(3).simulate('change', { target: { value: '400000011' } })
        expect(componentInstance.state().account).toEqual("400000011")
    })

    it('Should capture Is Production field correctly onChange', () => {
        const component = mount(
            <Provider store={store}>
                <CreateCluster uiListData={uiListArray} superAdmin={superAdmin} />
            </Provider>
        );
        const componentInstance = component.find('CreateCluster');
        componentInstance.find('input').at(0).simulate("change", {
            target: { checked: true }
        });
        expect(componentInstance.state().isProd).toEqual(true)
    })

    it('Test Next button click by changing the section change to Hardware from Cluster Details', () => {
        const component = mount(
            <Provider store={store}>
                <CreateCluster uiListData={uiListArray} superAdmin={superAdmin} />
            </Provider>
        );
        const componentInstance = component.find('CreateCluster');
        componentInstance.find('input').at(0).simulate("change", {
            target: { checked: true }
        });
        expect(componentInstance.state().isProd).toEqual(true)
    })
});