import { shallow } from 'enzyme';
import toJson from 'enzyme-to-json';
import React from 'react';
import { ToggleSlider } from '../../../../utils/components/ToggleSlider';

const handleToggle = () => {console.log('test toggle')}
const initialValue = {
    id: 0,
    handleToggleChange: handleToggle,
    toggleOn: false,
    isDisabled: false,
    toggleType: 'Encrypt'
};

const component = shallow( <ToggleSlider 
                                id={initialValue.id} 
                                handleToggleChange={initialValue.handleToggleChange} 
                                toggleType={initialValue.toggleType} 
                                toggleOn={initialValue.toggleOn} 
                                isDisabled={initialValue.isDisabled} 
                            />)

describe('Testing Toggle Slider', () => {
    it('Should render the component', () => {
        expect(toJson(component)).toMatchSnapshot();
    })
})