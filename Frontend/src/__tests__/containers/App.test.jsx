import React from 'react';
import { shallow } from 'enzyme';
import AppContainer from 'Containers/AppContainer';

describe('App', () => {
    describe('component', () => {
      let element;
      beforeEach(() => {
        element = <AppContainer />;
      });
  
      it('renders as expected', () => {
        const component = shallow(element);
        expect(component).toMatchSnapshot();
      });
    });
  });