import React, { Component } from 'react'
import { Dialog, Classes } from '@blueprintjs/core';
import { connect } from 'react-redux';
import { fetchAccountJiraEnabled } from '../../actions/emrManagement';
import { checkJiraEnabled } from '../../utils/components/CheckJiraEnabled';

class ModalTerminateCluster extends Component {
  constructor(props) {
    super(props)
    this.state = {
      clusterName: '',
      showConfirm: false,
      confirmFail: false,
      confirmErrMessage: '',
      jiraticket: ''
    }

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleClusterCheck = this.handleClusterCheck.bind(this);
    this.formSubmit = this.formSubmit.bind(this);
    this.handleConfirmation = this.handleConfirmation.bind(this);
    this.handleJiraTicket = this.handleJiraTicket.bind(this);
  }

  render() {
    return (
      <div>
      <Dialog
        isOpen={this.props.isOpen}
        onClose={this.handleCancel}
        title='Confirm Cluster Termination'
        icon='delete'
      >
        <div className={Classes.DIALOG_BODY}>
            <div>
              {this.state.confirmFail && <p className="confirm-cluster-rotation-err">{this.state.confirmErrMessage}</p>}
              <textarea
                className={Classes.INPUT}
                placeholder='Please type in exact Cluster Name'
                style={{ width: '100%', height: '100px', marginTop: '10px' }}
                onChange={this.handleClusterCheck}
              />
            </div>

        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <button
            className='terminateRotateSubmitBtn'
            onClick={this.handleConfirmation}
            disabled={!this.formSubmit}
          >
            Submit
          </button>
          <button
            className='cancelBtn'
            onClick={this.handleCancel}
          >
            Cancel
          </button>
        </div>
      </Dialog>
      {this.state.showConfirm &&
      <Dialog
        isOpen={this.state.showConfirm}
        onClose={this.handleConfirmation}
        title='Confirm Terminate'
        icon='refresh'
      >
        <div className={Classes.DIALOG_BODY}>
          {checkJiraEnabled(this.props.globalJiraData, this.props.accountJiraData) && <label className="servicenow-field">
            <span className="required">JIRA Ticket:</span>
            <input className="textField-servicenow" placeholder="Jira ticket" value={this.state.jiraticket} onChange={e => this.handleJiraTicket(e)} />
            <div>{this.state.jiraticket.length === 0 ? <span className='errorField'>This is a required field</span> : null }</div>
          </label>}
          <span style={{ fontSize: '15px', marginTop: '5px' }}>Terminating cluster <strong>{this.props.data.clusterName}</strong>. Continue?</span>
        </div>
        <div className={Classes.DIALOG_FOOTER}>
          <button
            className='terminateRotateSubmitBtn'
            onClick={this.handleSubmit}
            disabled={checkJiraEnabled(this.props.globalJiraData, this.props.accountJiraData) && this.state.jiraticket.length === 0}
          >
            Submit
          </button>
          <button
            className='cancelBtn'
            onClick={this.handleConfirmation}
          >
            Cancel
          </button>
        </div>
      </Dialog> }
      </div>
    )
  }

  handleJiraTicket(e) {
    if(e.target.value.match("^[a-zA-Z0-9-]*$")!=null) 
      this.setState({
          ...this.state,
          jiraticket: e.target.value
      })
  }

  handleConfirmation() {
    if (!this.formSubmit()) return;
    if (!this.state.showConfirm) this.props.fetchAccountJiraEnabled(this.props.data.account, this.props.token)
    this.setState({
      ...this.state,
      showConfirm: !this.state.showConfirm,
      confirmFail: false,
      confirmErrMessage: ''
    })
  }

  formSubmit() {
      if(this.state.clusterName === ''){
        this.setState({confirmErrMessage: 'Please fill in text field!', confirmFail: true})
        return false
      }
      else if(this.state.clusterName !== this.props.data.clusterName){
        this.setState({confirmErrMessage: 'Text field does not match Cluster Name', confirmFail: true})
        return false
      }
      else {
        return true
      }
    }


  handleSubmit() {
    this.props.onSubmit({
      "account": this.props.data.account,
      "clusterName": this.props.data.clusterName,
      "clusterId": this.props.data.clusterId,
      "lastUpdatedBy": this.props.fullName,
      "role": this.props.data.role,
      "type": this.props.data.type,
      "jiraTicket": this.state.jiraticket
    })
  }

  handleCancel() {
      this.props.onClose()
      this.setState({confirmFail: false, confirmErrMessage: ''})

  }
  handleClusterCheck(e) {
    this.setState({
      ...this.state,
      clusterName: e.target.value
  })
  }
}

const mapStateToProps = state => {
  return {
      accountJiraFetching: state.emrMetadataData.accountJiraFetching,
      accountJiraData: state.emrMetadataData.accountJiraData
  }
}

const mapDispatchToProps = dispatch => {
  return {
      fetchAccountJiraEnabled: (account, token) => dispatch(fetchAccountJiraEnabled(account, token))
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(ModalTerminateCluster)