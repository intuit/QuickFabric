import React from 'react';
import { Dialog, Classes } from '@blueprintjs/core'
import { connect } from 'react-redux';
import { fetchAccountJiraEnabled } from '../../actions/emrManagement';
import { checkJiraEnabled } from '../../utils/components/CheckJiraEnabled';

/**
 * Component to show popup window to flip cluster to production.
 */
class DNSFlip extends React.Component {
    constructor(props) {
        super(props);

        this.state = {
            clusterName: '',
            showConfirm: false,
            confirmFail: false,
            confirmErrMessage: '',
            jiraticket: '',
            fullDNSName: ''
        }

        this.formSubmit = this.formSubmit.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.handleCancel = this.handleCancel.bind(this);
        this.handleClusterCheck = this.handleClusterCheck.bind(this);
        this.handleConfirmation = this.handleConfirmation.bind(this);
        this.handleJiraTicket = this.handleJiraTicket.bind(this);
        this.handleFullDNSName = this.handleFullDNSName.bind(this);
    }

    render() {
        return (
            <div>
                <Dialog
                    isOpen={this.props.isOpen}
                    onClose={this.props.onClose}
                    title='Confirm DNS Flip'
                    icon='refresh'
                >
                    <div className={Classes.DIALOG_BODY}>
                        <div>
                          {this.state.confirmFail && <p className="confirm-cluster-rotation-err">{this.state.confirmErrMessage}</p>}
                            <span style={{ fontSize: '15px', marginRight: '15px' }}>Cluster Name</span>
                            <input className="textField-1" type="text" placeholder='Please type in exact Cluster Name' onChange={this.handleClusterCheck} />
                        </div>
                        <div style={{ marginTop: '10px' }}>
                          <span style={{ fontSize: '15px', marginRight: '30px' }}>DNS Name</span>
                          <input className="textField-1" placeholder='Provide a DNS Name to switch to' value={this.state.fullDNSName} onChange={this.handleFullDNSName} type="text" />
                        </div>
                    </div>
                    <div className={Classes.DIALOG_FOOTER}>
                    <button
                        className='terminateRotateSubmitBtn'
                        onClick={this.handleConfirmation}
                        disabled={!this.formSubmit}
                    >
                        Confirm
                    </button>
                    <button
                        className='cancelBtn'
                        onClick={this.handleCancel}
                    >
                        Cancel
                    </button>
                    </div>
                </Dialog>
                <Dialog
                  isOpen={this.state.showConfirm}
                  onClose={this.handleConfirmation}
                  title='Confirm'
                  icon='refresh'
                >
                  <div className={Classes.DIALOG_BODY}>
                  {checkJiraEnabled(this.props.globalJiraData, this.props.accountJiraData) && <label className="servicenow-field">
                      <span className="required">JIRA Ticket:</span>
                      <input className="textField-servicenow" placeholder="Jira ticket" value={this.state.jiraticket} onChange={e => this.handleJiraTicket(e)} />
                      <div>{this.state.jiraticket.length === 0 ? <span className='errorField'>This is a required field</span> : null }</div>
                    </label>}
                    <span style={{ fontSize: '15px' }}>Cluster: <strong>{this.props.data.clusterName}</strong> will be flipped to DNS: <strong>{this.state.fullDNSName}</strong> and original old cluster used for Rotating this cluster will be terminated after 1 day.</span>
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
                </Dialog>
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
      if(!this.state.showConfirm) {
        if(!this.formSubmit()) {
          return;
        }
        this.props.fetchAccountJiraEnabled(this.props.data.account, this.props.token)
      }
      this.setState({
        ...this.state,
        showConfirm: !this.state.showConfirm
      })
      this.setState({confirmFail: false, confirmErrMessage: ''})
    }


    handleCancel() {
      this.setState({confirmFail: false, confirmErrMessage: ''})
        this.props.onClose();
    }

    formSubmit() {
      if (this.state.clusterName === '') {
        this.setState({confirmErrMessage: 'Please fill in text field!', confirmFail: true})
        return false
      }
      else if (this.state.clusterName !== this.props.data.clusterName) {
        this.setState({confirmErrMessage: 'Text field does not match Cluster Name', confirmFail: true})
        return false
      }
      else {
        return true
      }
    }
    
    handleSubmit() {
      if (this.formSubmit()) {
        this.props.onSubmit({
          "account": this.props.data.account,
          "clusterName": this.props.data.clusterName,
          "clusterId": this.props.data.clusterId,
          "type": this.props.data.type,
          "role": this.props.data.role,
          "jiraTicket": this.state.jiraticket,
          "dnsName": this.state.fullDNSName
        })
      }
    }
    handleClusterCheck(e) {
      this.setState({
        ...this.state,
        clusterName: e.target.value
      })
    }
    handleFullDNSName(e) {
      this.setState({
        ...this.state,
        fullDNSName: e.target.value
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

export default connect(mapStateToProps, mapDispatchToProps)(DNSFlip)