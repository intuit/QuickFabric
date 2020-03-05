import EMRCost from 'Components/EMRCost';
import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import React from 'react';

const data = {
                emrGroupCost: [
                    {
                        emrGroup: "exploratory-sales",
                        account: "100000000000",
                        segment: "sales",
                        businessOwner: "The Boss",
                        costPerMonth: [
                            {
                                billMonth: "2019-09",
                                cost: 0
                            }, {
                                billMonth: "2019-10",
                                cost: 0
                            }
                        ]
                    }
                ]
            };

const uiListArray = {
    roles: [],
    accounts: [],
    response: {
        segments: []
    }
}

const component = shallow(<EMRCost  
                            uiListData={uiListArray}
                            superAdmin={true}
                            data={data}
                            />)

describe('Testing EMR Cost Table', () => {
    it('Should render the component', () => {
        expect(toJson(component)).toMatchSnapshot();
    })

    it('Should render the table with data', () => {
        const rows = component.find('StatefulGrid');
        expect(rows.length).toBe(1);
    })
})