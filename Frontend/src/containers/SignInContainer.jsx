import React, { Component } from 'react'
import { connect } from 'react-redux'
import { withRouter } from 'react-router-dom'
import { SignIn } from '../components'
import { signInUser } from '../actions/user'

/**
 * Container to sign in a user.
 */
class SignInContainer extends Component {
  constructor(props) {
    super(props)
    this.handleSignIn = this.handleSignIn.bind(this)
  }

  render() {
    return (
      <div className='sign-in-container'>
        <SignIn handleSignIn={this.handleSignIn} />
      </div>
    )
  }

  handleSignIn(username, passcode, jwt) {
    this.props.signIn(username, passcode, jwt)
  }
}


const mapDispatchToProps = dispatch => {
  return {
    signIn: (username, passcode) => {
      return dispatch(signInUser(username, passcode))
    }
  }
}

export default connect(null, mapDispatchToProps)(withRouter(SignInContainer))