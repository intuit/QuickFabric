import React, { Component } from 'react'
import { connect } from 'react-redux'
import { Switch, Route, withRouter, Redirect } from 'react-router-dom'
import { Position, Toast, Toaster, Intent, Navbar, Button, Alignment } from '@blueprintjs/core'
import { NavBar } from '../components'
import Cookies from 'js-cookie'
import SignInContainer from './SignInContainer'
import EMRHealthContainer from './EMRHealthContainer';
import EMRCostContainer from './EMRCostContainer'
import EMRManagementContainer from './EMRManagementContainer'
import { signInUser, signOutUser, clearSignInError } from '../actions/user';
import { fetchUIDropdownList } from '../actions/emrManagement'
import '../main.css';
import ProfileContainer from './ProfileContainer';
import AdminContainer from './AdminContainer';
import HelpContainer from './HelpContainer';

/**
 * Main Container containing routes and all other UI components.
 */
class AppContainer extends Component {
  constructor(props) {
    super(props)

    this.state = {
      signedIn: false,
      fullName: '',
      toasts: [],
      superAdmin: false,
      view: this.props.location.pathname,
      role:'',
      spinner: false,
      tokenjwt: Cookies.get('jwt')
    }

    this.toaster = Toaster
    this.refHandlers = {
      toaster: (ref) => this.toaster = ref
    }

    this.handleSignOut = this.handleSignOut.bind(this)
    this.handleNavSelection = this.handleNavSelection.bind(this)
  }
  checkJWT = () => {
    if(Cookies.get('jwt') !== this.state.tokenjwt) {
      this.setState({
        tokenjwt: Cookies.get('jwt')
      })
      console.log('new!', this.state.tokenjwt)
    }
  }
  render() {
    this.checkJWT()
    const displaySignIn = this.props.location.pathname === '/'
    return (
      <div>
        {this.state.spinner && <div active={this.props} className='loader-wrapper'>
          <div className='loader' />
        </div> }
        <Toaster
          position={Position.TOP}
          ref={this.refHandlers.toaster}
        >
          {this.state.toasts.map(toast => <Toast {...toast} />)}
        </Toaster>
        <div className='body-elements'>
        {displaySignIn ? <div></div> : 
          <div>
            <NavBar fullName={this.props.fullName} superAdmin={this.props.superAdmin} />
              <Navbar.Group align={Alignment.RIGHT}>
                <span style={{ fontSize: '15px' }}>{this.state.fullName}</span>
                <Navbar.Divider />
                <Button
                  onClick={this.handleSignOut}
                  className='pt-minimal'
                  icon='log-out'
                >
                  <span style= {{ color: 'black' }}>Logout</span>
                </Button>
              </Navbar.Group>
          </div>
        }
        <Switch>
           <Route exact path="/" render={() => (
            this.state.signedIn   ? (
              <Redirect to={
                this.state.view === '/' ? '/emrHealth' : this.state.view
              }/>
            ) : (
              <SignInContainer />
            )
          )}/>
          <Route exact path="/emrHealth" render={() => (
            !this.state.signedIn ? (
              <Redirect to="/"/>
            ) : (
              <div className='app-container-view-gov'>
                <EMRHealthContainer signedIn={this.props.isSignedIn} location={this.props.location} role={this.props.role} token={this.state.tokenjwt} dccRoles={this.props.dccRoles} superAdmin={this.props.superAdmin} />
              </div>
            )
          )}/>

          <Route exact path="/emrCost" render={() => (
            !this.state.signedIn ? (
              <Redirect to="/"/>
            ) : (
              <div className='app-container-view-gov'>
                <EMRCostContainer role={this.props.role} token={this.state.tokenjwt} dccRoles={this.props.dccRoles} superAdmin={this.props.superAdmin} />
              </div>
            )
          )}/>

          <Route exact path="/emrManagement" render={() => (
            !this.state.signedIn ? (
              <Redirect to="/"/>
            ) : (
              <div className='app-container-view-gov'>
                <EMRManagementContainer role={this.props.role} fullName={this.props.fullName} token={this.state.tokenjwt} dccRoles={this.props.dccRoles} superAdmin={this.props.superAdmin} />
              </div>
            )
          )}/>

          <Route exact path="/profile" render={() => (
            !this.state.signedIn ? (
              <Redirect to="/"/>
            ) : (
              <div className='app-container-view-gov'>
                <ProfileContainer role={this.props.role} fullName={this.props.fullName} token={this.state.tokenjwt} dccRoles={this.props.dccRoles} superAdmin={this.props.superAdmin} />
              </div>
            )
          )}/>

          <Route exact path="/admin" render={() => (
            !this.state.signedIn ? (
              <Redirect to="/"/>
            ) : (
              this.state.superAdmin ?
              <div className='app-container-view-gov'>
               <AdminContainer role={this.props.role} fullName={this.props.fullName} token={this.state.tokenjwt} dccRoles={this.props.dccRoles} superAdmin={this.props.superAdmin} />
              </div> :
              <Redirect to="/emrHealth" />
            )
          )}/>    
          <Route exact path="/help" render={() => (
            !this.state.signedIn ? (
              <Redirect to="/"/>
            ) : (
              <div className='app-container-view-gov'>
                <HelpContainer/>
              </div>
            )
          )}/>         
        </Switch>
        </div>
      </div>
    )
  }

  /**
   * Check whether signed in from SSO by checking whether the username and jwt token is present and then redirect to sign in.
   */
  componentWillMount() {
    let username = Cookies.get('username')
    let jwt = Cookies.get('jwt')

    if (username && jwt) {
      this.props.signIn(username, '', jwt)
      this.setState({
        ...this.state,
        spinner: true
      })
    }
  }

  /**
   * Handle any sign in errors, otherwise set the signed in information for later use.
   */
  componentDidUpdate(prevProps, prevState) {
    if (prevProps !== this.props) {
      if (this.props.signInError) {
        let errorMsg = 
        <div>
          <b>Error Mesage</b>: {this.props.signInErrorData.errorMessage}
          <br/>
          <b>Error Details</b>: {this.props.signInErrorData.messageDetails}
          <br/>
          <b>Error ID</b>: {this.props.signInErrorData.errorId}
        </div>
        this.toaster.show({
          message: errorMsg,
          intent: Intent.DANGER,
          icon: 'warning-sign'
        })
        this.handleSignOut()
        this.props.clearSignInError()
      }
      this.setState({
        ...this.state,
        signedIn: this.props.isSignedIn && Cookies.get('username'),
        fullName: this.props.fullName,
        superAdmin: this.props.superAdmin,
        spinner: false
      })

    }
    if (this.state.view !== prevState.view) {
      this.props.history.push(this.state.view)
    }
  }

  /**
   * Sign out the user.
   */
  handleSignOut() {
    this.props.signOut()
    this.setState({
      ...this.state,
      signedIn: false,

    })
    this.props.history.push("/")
  }

  handleNavSelection(path) {
    this.setState({
      ...this.state,
      view: path
    })
    this.props.history.push(path)
  }
}

const mapStateToProps = state => {
  return {
    isSignedIn: state.user.signedIn,
    fullName: state.user.name,
    fullname:Cookies.get('name'),
    signInError: state.user.signInError,
    role: state.user.role,
    dccRoles: state.user.services,
    superAdmin: state.user.superAdmin,
    signInErrorData: state.user.signInErrorData
  }
}

const mapDispatchToProps = dispatch => {
  return {
    signOut: () => dispatch(signOutUser()),
    clearSignInError: () => dispatch(clearSignInError()),
    signIn: (username, passcode, jwt) => {
      return dispatch(signInUser(username, passcode, jwt))
    },
    fetchUIDropdownList: (token) => {return dispatch(fetchUIDropdownList(token))}
  }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(AppContainer))