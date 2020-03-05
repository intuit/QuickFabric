import React, { Component } from 'react'
import { Dialog, Classes } from '@blueprintjs/core'
import ModalRotateAMIWorkflow from './RotateAMIWorkflow';
import { TimePicker } from '@progress/kendo-react-dateinputs'
import { formatDate } from '@telerik/kendo-intl';
import { connect } from 'react-redux';
import { fetchAccountJiraEnabled } from '../../actions/emrManagement';
import { checkJiraEnabled } from '../../utils/components/CheckJiraEnabled';

/**
 * Popup window to perform AMI Rotation for a cluster.
 */
class RotateAMI extends Component {
  constructor(props) {
    super(props)

    var startTime = new Date();
    startTime.setHours(this.props.data.autopilotWindowStart);
    var endTime = new Date();
    endTime.setHours(this.props.data.autopilotWindowEnd);
    this.state = {
      clusterName: '',
      isProd: false,
      showConfirm: false,
      showWorkflow: false,
      confirmFail: false,
      confirmErrMessage: '',
      jiraticket: '',
      autoAMIRotation: this.props.data.autoAmiRotation,
      autopilotWindowStart: startTime,
      autopilotWindowEnd: endTime,
      customAMIId: '',
      ami_rotation_sla: '30'
    }

    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleCancel = this.handleCancel.bind(this);
    this.handleClusterCheck = this.handleClusterCheck.bind(this);
    this.formSubmit = this.formSubmit.bind(this);
    this.handleIsProd = this.handleIsProd.bind(this);
    this.openConfirmation = this.openConfirmation.bind(this);
    this.closeWorkflow = this.closeWorkflow.bind(this);
    this.closeConfirmation = this.closeConfirmation.bind(this);
    this.handleJiraTicket = this.handleJiraTicket.bind(this);
    this.handleAutoAMIRotation = this.handleAutoAMIRotation.bind(this);
    this.toggleAutoAMIRotation = this.toggleAutoAMIRotation.bind(this);
    this.handleTimeChange = this.handleTimeChange.bind(this);
    this.handleCustomAMIId = this.handleCustomAMIId.bind(this);
  }

  render() {
    return (
      <div>
        <div className='popup'>
                <div className='alert-content2'>
                    <div className='alert-text'>
                        <h5>Confirm Cluster Rotation</h5>
                        <span>Cluster Name:</span>
                        <div style={{ position: 'relative', left: '20px' }}>
                          <input className="textFieldHardware" defaultValue="1" placeholder="Please type in exact Cluster Name" value={this.state.clusterName} onChange={this.handleClusterCheck} />
                        </div>
                        {this.state.confirmFail && <p className="confirm-cluster-rotation-err">{this.state.confirmErrMessage}</p>}
                        <span style={{ position: 'relative', top: '10px' }}>Custom AMI ID:</span>
                        <div style={{ position: 'relative', left: '20px', marginTop: '10px' }}>
                          <input className="textFieldHardware" defaultValue="1" placeholder="(Optional) Provide a custom AMI Id" value={this.state.customAMIId} onChange={this.handleCustomAMIId} />
                        </div>
                    </div>
                    <div>
                        <div style={{ position: 'relative', top: '10px', margin: '20px' }}>
                          <div style={{ marginBottom: '20px' }}>
                            <span style={{ fontSize: '14px' }}>Is Production Cluster</span>
                            <div className="cluster-toggle">
                              <input
                              className="react-switch-checkbox"
                              id='isProd'
                              type="checkbox"
                              onChange={this.handleIsProd}
                              checked={this.state.isProd}
                              />
                              <label
                                  style={{ background: this.state.isProd ? '#53b700' : '#ff0100' }}
                                  className="react-switch-label"
                                  htmlFor='isProd'
                              >
                              <span className={`react-switch-button`} />
                                  {this.state.isProd ? <p className="create-toggle-on">Yes</p> : <p className="create-toggle-off">No</p>}
                              </label>    
                            </div>
                          </div>
                          <span style={{ fontSize: '14px' }}>Auto-Rotate every month</span>
                          <div className="cluster-toggle">
                              <input
                              className="react-switch-checkbox"
                              id='autoami'
                              type="checkbox"
                              onChange={this.toggleAutoAMIRotation}
                              checked={this.state.autoAMIRotation}
                              />
                              <label
                                  style={{ background: this.state.autoAMIRotation ? '#53b700' : '#ff0100' }}
                                  className="react-switch-label"
                                  htmlFor='autoami'
                              >
                              <span className={`react-switch-button`} />
                                  {this.state.autoAMIRotation ? <p className="create-toggle-on">Yes</p> : <p className="create-toggle-off">No</p>}
                              </label>    
                          </div>
                        </div>
                        <div style={{ marginLeft: '20px' }}>
                          {this.state.autoAMIRotation && <div className='autopilotwindow'>
                            <span>From</span>
                            <TimePicker 
                                className='customTimePicker'
                                onChange={(e) => this.handleTimeChange(e, "start")}
                                format={"HH"}
                                value={this.state.autopilotWindowStart}
                            /> 
                            <span style={{ marginLeft: '20px' }}>To</span>
                            <TimePicker 
                                className='customTimePicker'
                                onChange={(e) => this.handleTimeChange(e, "end")}
                                format={"HH"}
                                value={this.state.autopilotWindowEnd}
                            />
                            <span style={{ marginLeft: '20px' }}>SLA Days</span>
                            <input className="customslafield1" placeholder="Custom SLA for AMI Rotation" value={this.state.ami_rotation_sla} onChange={this.handleAMIRotationSLA} />
                          </div> }
                        </div>
                    </div>
                    <div className='alert-btns'>
                      <button
                        className='terminateRotateSubmitBtn'
                        onClick={this.openConfirmation}
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
                </div>
            </div>

      {this.state.showConfirm && <Dialog
        isOpen={this.props.isOpen}
        onClose={this.props.onClose}
        title='Confirm'
        icon='refresh'
      >
        <div className={Classes.DIALOG_BODY}>
        {checkJiraEnabled(this.props.globalJiraData, this.props.accountJiraData) && <label className="servicenow-field">
            <span className="required">JIRA Ticket:</span>
            <input className="textField-servicenow" placeholder="Jira ticket" value={this.state.jiraticket} onChange={e => this.handleJiraTicket(e)} />
            <div>{this.state.jiraticket.length === 0 ? <span className='errorField'>This is a required field</span> : null }</div>
        </label> }
          {!this.state.isProd ? <div style={{ fontSize: '15px' }}>We will be terminating <strong>{this.props.data.clusterName}</strong> cluster and creating a new one with same name.</div> : 
            <div style={{ fontSize: '15px' }}>A new cluster with a new name will be created. Current cluster <strong>{this.props.data.clusterName}</strong> will be terminated after 1 day.</div>}
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
            onClick={this.closeConfirmation}
          >
            Cancel
          </button>
        </div>
      </Dialog> }
      {this.state.showWorkflow &&
        <ModalRotateAMIWorkflow
        data={this.props.data}
        isOpen={this.state.showWorkflow}
        onClose={this.closeWorkflow}
        token={this.props.token}
        rotatedami='true'
        />
      }
      </div>
    )
  }

  handleTimeChange(e, type) {
    if (type === 'start') {
        this.setState({
            ...this.state,
            autopilotWindowStart: e.target.value,
            confirmFail: false
        })
    } else {
        let startHour = formatDate(this.state.autopilotWindowStart, "HH")
        let endHour = formatDate(e.target.value, "HH")
        if (endHour < startHour) {
            this.setState({
                ...this.state,
                confirmErrMessage: 'Start Hour should be smaller than the End Hour', confirmFail: true,
                autopilotWindowEnd: e.target.value
            })
        } else {
            this.setState({
                ...this.state,
                autopilotWindowEnd: e.target.value,
                confirmFail: false
            })
        }
    }  
  }

  handleAMIRotationSLA = (e) => {
    if (e.target.value === '' || e.target.value.match("^[1-9][0-9]*$")) {
      this.setState({
          ...this.state,
          ami_rotation_sla: e.target.value
      })
  }
  }

  handleJiraTicket(e) {
    if(e.target.value.match("^[a-zA-Z0-9-]*$")!=null) 
      this.setState({
          ...this.state,
          jiraticket: e.target.value
      })
  }

  closeWorkflow() {
    this.setState({
      ...this.state,
      showWorkflow: false
    })
  }

  closeConfirmation() {
    this.setState({
      ...this.state,
      showConfirm: false
    })
  }

  handleIsProd = event => {
    this.setState({
      ...this.state,
      isProd: !this.state.isProd
    })
  }

  handleAutoAMIRotation(event) {
    this.props.handleAutoRotateAMI(this.props.data.clusterId, this.state.data.autoAMIRotation)
  }
  toggleAutoAMIRotation(event) {
    this.setState({
      ...this.state,
      autoAMIRotation : !this.state.autoAMIRotation
    })
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

  openConfirmation() {
    if (!this.formSubmit()) return;
    this.props.fetchAccountJiraEnabled(this.props.data.account, this.props.token)
    this.setState({
      ...this.state,
      showConfirm: true,
      confirmFail: false,
      confirmErrMessage:''
    })
  }

  handleSubmit() {
    let name = this.props.fullName;
    if (this.formSubmit()) {
      this.props.onSubmit({
        "account": this.props.data.account,
        "clusterName": this.props.data.clusterName,
        "clusterId": this.props.data.clusterId,
        "lastUpdatedBy": this.props.fullName,
        "isProd": this.state.isProd,
        "createdBy": name,
        "role": this.props.data.role,
        "jiraTicket": this.state.jiraticket,
        "autoAmiRotation": this.state.autoAMIRotation,
        "autopilotWindowStart": formatDate(this.state.autopilotWindowStart, "HH"),
        "autopilotWindowEnd": formatDate(this.state.autopilotWindowEnd, "HH"),
        "customAMIId": this.state.customAMIId,
        "amiRotationSlaDays": this.state.ami_rotation_sla
      }) 
      this.setState({
        ...this.state,
        showWorkflow: true
      })
    }
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

  handleCustomAMIId(e) {
    this.setState({
      ...this.state,
      customAMIId: e.target.value
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

export default connect(mapStateToProps, mapDispatchToProps)(RotateAMI)