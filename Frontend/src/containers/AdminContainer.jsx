import React from 'react';
import { Admin } from '../components';
import { connect } from 'react-redux';

import {
  fetchUIDropdownList
} from '../actions/emrManagement';

/**
 * Container for Admin Component.
 */
class AdminContainer extends React.Component {
    render() {
        if(!this.props.uiListFetching && this.props.uiListSuccess) {
            return (
                <Admin {...this.props} />
            )
        } else {
            this.props.fetchUIDropdownList(this.props.token)
            return (
                <div></div>
            )
        }

    }
}

const mapStateToProps = state => {
    return {
      uiListData: state.emrMetadataData.uiListData,
      uiListFetching: state.emrMetadataData.uiListFetching,
      uiListSuccess: state.emrMetadataData.uiListSuccess
    }
  }
  
const mapDispatchToProps = dispatch => {
  return {
    fetchUIDropdownList: (token) => {return dispatch(fetchUIDropdownList(token))}
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(AdminContainer);