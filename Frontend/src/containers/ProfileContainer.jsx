import React from 'react';
import { Profile } from '../components';
import { connect } from 'react-redux';

/**
 * Container for user profile component.
 */
class ProfileContainer extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <Profile {...this.props} />
        )
    }
}

export default connect()(ProfileContainer);