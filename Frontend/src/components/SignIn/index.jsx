import React, { Component } from 'react'
import { FormGroup } from '@blueprintjs/core'
import './SignIn.css'
import baseURL from '../../api-config'
import DCCLogo from '../../assets/login/qf_black.svg';
import GithubLogo from '../../assets/login/gitLogo.png';

/**
 * Authenticate and sign in the user.
 */
class SignIn extends Component {
  constructor(props) {
    super(props)

    this.state = {
      username: '',
      passcode: '',
      displayHelperUsername: false,
      displayHelperPasscode: false
    }

    this.handleUsername = this.handleUsername.bind(this)
    this.handlePasscode = this.handlePasscode.bind(this)
    this.handleContinue = this.handleContinue.bind(this)
    this.handleKeyDown = this.handleKeyDown.bind(this)
  }

  render() {
    return (
      <div className="loginBg">
        <img className='dcc-logo' src={DCCLogo} />
        <div className='aws-video'>
          <iframe width="840" height="472.5" src="https://www.youtube.com/embed/TfgNzzaakNs?start=1324" frameBorder="0" allow="accelerometer; autoplay; encrypted-media; gyroscope; picture-in-picture" allowFullScreen></iframe>
        </div>
        <div className='sign-in-cont'>
          <div className='sign-in-component'>
            <div className="sign-in-card" onKeyDown={this.handleKeyDown} tabIndex="0">
              <a href={baseURL+'/login/sso/redirect'}> 
                <button disabled className="sign-in-card-button" onClick={this.handleContinue}> SSO Login </button>
              </a>
              <div className="login__saml-separator">
              <span>
                or
              </span>
              </div>
              <FormGroup 
                labelFor="username" 
                helperText={this.state.displayHelperUsername ? 'Username is required' : ''}
                className="inputForm"
              >
                <input className="inputField" value={this.state.username} placeholder='Username' onChange={this.handleUsername} id="username"/>
              </FormGroup>
              <FormGroup 
                labelFor="passcode" 
                helperText={this.state.displayHelperPasscode ? 'Passcode is required' : ''}
                className="inputForm"
              >
                <input className="inputField" value={this.state.passcode} placeholder='Password' type="password" onChange={this.handlePasscode} id="passcode"/>
              </FormGroup>
              <button className="dev-login-button" onClick={this.handleContinue}>Dev Login</button>
              {/* </a> */}
              <a href='https://github.com/intuit/QuickFabric' target='_blank'><img className='git-logo' src={GithubLogo} /></a>
            </div>
          </div>
        </div>
      </div>
    )
  }
  handleKeyDown(e) {
    if(e.keyCode == 13 && e.shiftKey == false) {
      e.preventDefault()
      this.handleContinue()
    }
  }
  handleUsername(e) {
    this.setState({
      ...this.state,
      username: e.target.value,
      displayHelperUsername: !(e.target.value)
    })
  }

  handlePasscode(e) {
    this.setState({
      ...this.state,
      passcode: e.target.value,
      displayHelperPasscode: !(e.target.value)
    })
  }

  handleContinue() {
    let { username, passcode } = this.state
    if (username && passcode ) {
      this.props.handleSignIn(username, passcode, null)
      this.setState({ username: '', passcode: ''})
      return
    }
    let newState = {}
    if (username === '')
      newState.displayHelperUsername = true
    if (passcode === '')
      newState.displayHelperPasscode = true
    this.setState({ ...this.state, ...newState })
  }
}

export default SignIn