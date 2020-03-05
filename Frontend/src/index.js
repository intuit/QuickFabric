import React from 'react'
import ReactDOM from 'react-dom'
import { Provider } from 'react-redux'
import { BrowserRouter } from 'react-router-dom'
import store from './store/index'
import AppContainer from './containers/AppContainer'
import './main.css';

ReactDOM.render(
  
  <Provider store={store}>
  
    <BrowserRouter>
      <AppContainer />
    </BrowserRouter>
    
  </Provider>,
  document.getElementById('root')
)