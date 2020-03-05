import EMRManagement from 'Components/EMRManagement';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import React from 'react';

const fetchUIDropdownList = jest.fn();
const data = [{
    metadataId: 3,
    account: "200000000000",
    clusterId: "j-456QWE3JQWEZ3",
    clusterName: "exploratory-sales-test1",
    name: "test1",
    role: "sales",
    status: "HEALTHY",
    type: "exploratory",
    creationTimestamp: "2020-01-03 16:00:17.0",
    doTerminate: true,
    createdBy: "QuickFabric User",
    dnsName: "exploratory-sales-test1.company.com",
    dnsFlip: false,
    isProd: false,
    dnsFlipCompleted: false,
    rotationDaysToGo: "Days Left: 11",
    autoAmiRotation: false,
    autopilotWindowStart: 6,
    autopilotWindowEnd: 22,
    amiRotationSlaDays: 50,
    requestTicket: "DATA-23096"
}]

const uiListArray = {
    roles: [],
    accounts: []
}

const component = shallow(<EMRManagement 
                            uiListData={uiListArray} 
                            superAdmin={true} 
                            data={data} 
                            fetchUIDropdownList={fetchUIDropdownList}
                            />)

describe('Testing EMR Management Table', () => {
    it('Should render the component', () => {
        expect(toJson(component)).toMatchSnapshot();
    })

    it('Should render the table with data', () => {
        const rows = component.find('StatefulGrid');
        expect(rows.length).toBe(1);
    })
});