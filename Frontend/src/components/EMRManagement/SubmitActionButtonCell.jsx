import React from 'react'
import { Dialog, Classes } from '@blueprintjs/core'
import { GridCell } from '@progress/kendo-react-grid'
import AddSteps from './AddSteps'
import TerminateCluster from './TerminateCluster'
import RotateAMI from './RotateAMI'
import TestSuite from './TestSuite'
import CreateClusterWorkflow from './CreateClusterWorkflow';
import RotateAMIWorkflow from './RotateAMIWorkflow';
import './emrManagement.css'
import Fab from '@material-ui/core/Fab';
import DeleteIcon from '@material-ui/icons/Delete';
import AddIcon from '@material-ui/icons/Add';
import AutoRenew from '@material-ui/icons/Autorenew';
import CreateIcon from '@material-ui/icons/Create';
import Settings from '@material-ui/icons/Settings';
import FlipToBack from '@material-ui/icons/FlipToBack';
import CheckCircle from '@material-ui/icons/Check';
import FileCopy from '@material-ui/icons/FileCopy';
import DNSFlip from './DNSFlip';
import CustomAlert from './CustomAlert';

import './../../utils/styles/toggle.scss'
import { debounce } from 'lodash';
import { TimePicker } from '@progress/kendo-react-dateinputs'
import { formatDate } from '@telerik/kendo-intl';

function dis_state(props) {
  let cluster_state = props.dataItem.status;
  return cluster_state === 'RUNNING' || cluster_state === 'WAITING' || cluster_state === 'HEALTHY';
}

function rotate_disabled(props) {
  let cluster_state = props.dataItem.status;
  return cluster_state === 'FAILED' || cluster_state === 'BOOTSTRAPPING' || cluster_state === 'INITIATED';
}

/**
 * Component to show the action buttons in the EMR Management table based on the current tab and action.
 */
export default class SubmitActionButtonCell extends GridCell {
  
  constructor(props) {
    super(props)

    var startTime = new Date();
    startTime.setHours(this.props.dataItem.autopilotWindowStart);
    var endTime = new Date();
    endTime.setHours(this.props.dataItem.autopilotWindowEnd);
    this.state = {
      ModalAddSteps: false,
      ModalTerminateCluster: false,
      ModalRotateAMI: false,
      ModalTestSuite: false,
      ModalCreatClusterWorkflow: false,
      ModalRotateAMIWorkflow: false,
      ModalDNSFlip: false,
      modalState: {},
      rotateamialert: false,
      workflowalert: false,
      autoAMIRotation: this.props.dataItem.autoAmiRotation,
      showAlert: false,
      alertMessage: '',
      alertTitle: '',
      alertIcon: '',
      showConfirmButton: false,
      autopilot_window_start: startTime,
      autopilot_window_end: endTime,
      autopilot_error: false,
      doTerminate: this.props.dataItem.doTerminate,
      ami_rotation_sla: this.props.dataItem.amiRotationSlaDays,
      autopilot_sla_alert: false,
      auto_ami_rotation_alert: false
    }

    this.handleAddStepsButtonClick= this.handleAddStepsButtonClick.bind(this)
    this.handleTerminateClusterButtonClick = this.handleTerminateClusterButtonClick.bind(this)
    this.handleRotateAMIButtonClick = this.handleRotateAMIButtonClick.bind(this)
    this.handleModalSubmitAddSteps = this.handleModalSubmitAddSteps.bind(this)
    this.handleModalSubmitTerminateCluster = this.handleModalSubmitTerminateCluster.bind(this)
    this.handleModalSubmitRotateAMI = this.handleModalSubmitRotateAMI.bind(this)
    this.handleModalClose = this.handleModalClose.bind(this)
    this.handleTestSuiteButtonClick = this.handleTestSuiteButtonClick.bind(this);
    this.handleCreateClusterWorkflowClick = this.handleCreateClusterWorkflowClick.bind(this);
    this.handleStepsStatusData = this.handleStepsStatusData.bind(this);
    this.handleRotateAMIWorkflowClick = this.handleRotateAMIWorkflowClick.bind(this);
    this.handleRotateAMIAlert = this.handleRotateAMIAlert.bind(this);
    this.handleDNSFlipButtonClick = this.handleDNSFlipButtonClick.bind(this);
    this.handleModalSubmitDNSFlip = this.handleModalSubmitDNSFlip.bind(this);
    this.handleWorkflowAlert = this.handleWorkflowAlert.bind(this);
    this.handleAutoAMIRotation = debounce(this.handleAutoAMIRotation.bind(this), 1000, false);
    this.toggleAutoAMIRotation = this.toggleAutoAMIRotation.bind(this);
    this.handleAlert = this.handleAlert.bind(this);
    this.handleConfirm = this.handleConfirm.bind(this);
    this.setAlert = this.setAlert.bind(this);
    this.rotateToggle = this.rotateToggle.bind(this);
    this.handleTimeChange = this.handleTimeChange.bind(this);
    this.handleAutoPilotWindowAlert = this.handleAutoPilotWindowAlert.bind(this);
    this.toggleTerminateCluster = this.toggleTerminateCluster.bind(this);
    this.handleDoTerminate = this.handleDoTerminate.bind(this);
    this.handleAMIRotationSLA = this.handleAMIRotationSLA.bind(this);
    this.handleCloneClusterButtonClick = this.handleCloneClusterButtonClick.bind(this);
    this.handleAutoPilotSLAAlert = this.handleAutoPilotSLAAlert.bind(this);
    this.handleAutoAMIRotationAlert = this.handleAutoAMIRotationAlert.bind(this);
    this.handleCustomAlertAutoAMI = this.handleCustomAlertAutoAMI.bind(this);
  }

  render() {
    const showButton = 'True'
    return (
      <td>
        {
          showButton ?
            <div >
            {this.renderButton(this.props.action)}
            </div>
            : <div />
        }
        <AddSteps 
          isOpen={this.state.ModalAddSteps} 
          onSubmit={this.handleModalSubmitAddSteps}
          modalState={this.state.modalState}
          data={this.props.dataItem}
          onClose={this.handleModalClose}
          fullName={GridCell.fullName}
          role={GridCell.role}
          token={this.props.token}
          globalJiraData={this.props.globalJiraData}
        />
        
        <TerminateCluster
          isOpen={this.state.ModalTerminateCluster} 
          onSubmit={this.handleModalSubmitTerminateCluster}
          modalState={this.state.modalState}
          data={this.props.dataItem}
          onClose={this.handleModalClose}
          fullName={GridCell.fullName}
          role={GridCell.role}
          token={this.props.token}
          globalJiraData={this.props.globalJiraData}
        />

        {this.state.ModalRotateAMI && 
        <RotateAMI
          handleAutoRotateAMI = {this.props.handleAutoRotateAMI}
          isOpen={this.state.ModalRotateAMI} 
          onSubmit={this.handleModalSubmitRotateAMI}
          modalState={this.state.modalState}
          data={this.props.dataItem}
          onClose={this.handleModalClose}
          fullName={GridCell.fullName}
          role={GridCell.role}
          token={this.props.token}
          globalJiraData={this.props.globalJiraData}
        /> }

        <DNSFlip
          isOpen={this.state.ModalDNSFlip}
          onSubmit={this.handleModalSubmitDNSFlip}
          modalState={this.state.modalState}
          onClose={this.handleModalClose}
          data={this.props.dataItem}
          token={this.props.token}
          globalJiraData={this.props.globalJiraData}
        />

        {this.state.ModalTestSuite && 
        <TestSuite
          data={this.props.dataItem}
          onClose={this.handleModalClose}
          token={this.props.token}
        /> }

        {this.state.ModalCreatClusterWorkflow && 
        <CreateClusterWorkflow
          data={this.props.dataItem}
          isOpen={this.state.ModalCreatClusterWorkflow} 
          onClose={this.handleModalClose}
          token={this.props.token}
        /> }

        {this.state.ModalRotateAMIWorkflow && 
        <RotateAMIWorkflow
          data={this.props.dataItem}
          isOpen={this.state.ModalRotateAMIWorkflow}
          onClose={this.handleModalClose}
          token={this.props.token}
        /> }

        {this.state.autopilot_error && 
            <Dialog
              isOpen={this.state.autopilot_error}
              onClose={this.handleAutoPilotWindowAlert}
              title='Auto-Pilot Window Error'
              icon='refresh'
            >
              <div className={Classes.DIALOG_BODY}><span style={{ fontSize: '15px', margin: '10px' }}>End Hour cannot be smaller than the Start Hour.</span></div>
              <button
              className='cancelBtn'
              onClick={this.handleAutoPilotWindowAlert}
              >
                Cancel
              </button>
            </Dialog> }

        {this.state.rotateamialert && 
          <Dialog
            isOpen={this.state.rotateamialert}
            onClose={this.handleRotateAMIAlert}
            title='Cluster Rotation'
            icon='refresh'
          >
            <div className={Classes.DIALOG_BODY}><span style={{ fontSize: '15px', margin: '10px' }}>AMI can be rotated only for clusters created from DCC.</span></div>
            <button
            className='cancelBtn'
            onClick={this.handleRotateAMIAlert}
            >
              Cancel
            </button>
          </Dialog> }

          {this.state.showAlert && 
          <Dialog
            isOpen={this.state.showAlert}
            onClose={this.handleAlert}
            title={this.state.alertTitle}
            icon={this.state.alertIcon}
          >
            <div className={Classes.DIALOG_BODY}>
              <span style={{ fontSize: '15px', marginTop: '10px' }}>{this.state.alertMessage}</span>
            </div>
            <div className='confirm-button-container'>
              <button
              className='cancelBtn'
              onClick={this.handleAlert}
              >
                Cancel
              </button> 
              {
                this.state.showConfirmButton &&
                <button 
                className='cancelBtn'
                onClick={this.handleConfirm}>
                Confirm
                </button>
              }
            </div>
          </Dialog> }

          {this.state.auto_ami_rotation_alert && 
            <CustomAlert 
              message="Toggling Auto-pilot settings for AMI Rotation." 
              type="auto-pilot" 
              dataItem={this.props.dataItem}
              onCancel={this.handleAutoAMIRotationAlert} 
              onSubmit={this.handleCustomAlertAutoAMI} 
              token={this.props.token}
              />
          } 

          {this.state.autopilot_sla_alert &&
            <Dialog
              isOpen={this.state.autopilot_sla_alert}
              onClose={this.handleAutoPilotSLAAlert}
              title="Auto Pilot Window and SLA"
              icon='refresh'
              >
                <div className={Classes.DIALOG_BODY}>
                  <span style={{ fontSize: '15px' }}>Proceed with below information?</span>
                  {this.state.autoAMIRotation &&
                    <div>
                      <div style={{ fontSize: '15px', marginTop: '20px' }}><h5>Auto Pilot Window:</h5></div>
                      <span style={{ fontSize: '15px', margin: '10px', marginRight: '0px' }}>From: </span>
                      <span style={{ fontSize: '15px', margin: '10px', marginLeft: '0px' }}>{formatDate(this.state.autopilot_window_start, "HH")}</span>
                      <span style={{ fontSize: '15px', margin: '10px', marginRight: '0px' }}>To: </span>
                      <span style={{ fontSize: '15px', margin: '10px', marginLeft: '0px' }}>{formatDate(this.state.autopilot_window_end, "HH")}</span>
                    </div>
                  }
                  <div style={{ fontSize: '15px', marginTop: '20px' }}><h5>AMI Rotation SLA Days:</h5></div>
                  <span style={{ fontSize: '15px', marginTop: '10px' }}>{this.state.ami_rotation_sla}</span>
                </div>
                <div className='confirm-button-container'>
              <button
              className='cancelBtn'
              onClick={this.handleAutoPilotSLAAlert}
              >
                Cancel
              </button> 
                <button 
                className='cancelBtn'
                onClick={this.handleAutoAMIRotation}>
                Confirm
                </button>
            </div>
              </Dialog>
          }
      </td>
    )
  }

  renderButton(act) {
    switch(act) {
      case 'addStep':
        return (
          <div style={{display: 'flex'}}>
            <div style={{marginRight: '10px'}}>
              <Fab color='primary' title='Click to add new step(s) to the cluster' disabled={!dis_state(this.props)} onClick={this.handleAddStepsButtonClick}>
                <AddIcon />
            </Fab>
            </div>
          </div>

        );
      case 'terminateCluster':
        return (
          <div style={{ display: 'flex' }}>
            <div style={{marginTop: '6px', marginRight: '10px'}}>
              <Fab color='secondary' title='Click to terminate the cluster' disabled={!dis_state(this.props)} onClick={this.handleTerminateClusterButtonClick}>
                <DeleteIcon />
              </Fab>
            </div>
            {this.checkMatch(this.props.dataItem.createdBy, 'Jenkins') || this.props.dataItem.status === 'TERMINATED' || this.props.dataItem.status === 'FAILED' || this.props.dataItem.status === 'TERMINATION_INITIATED' || this.props.dataItem.status === 'BOOTSTRAPPING' ?
              <div className="auto-rotation-actions">
                  <input
                  className="react-switch-checkbox"
                  id={this.props.dataItem.clusterId}
                  type="checkbox"
                  onChange={() => console.log('False') }
                  checked={this.state.doTerminate}
                />
                <label
                    className="terminate-cluster-switch-label"
                    htmlFor={this.props.dataItem.clusterId}
                >
                    <span className={`react-switch-button`} />
                    {this.state.doTerminate ? <p className="subscribe-toggle-on">Auto-Terminate: On</p> : <p className="subscribe-toggle-off">Auto-Terminate: Off</p>}
                </label>     
              </div> : 
              <div className="auto-rotation-actions">
                <input
                className="react-switch-checkbox"
                id={this.props.dataItem.clusterId}
                type="checkbox"
                onChange={() => this.setAlert(true, 'confirm', 'Are you sure you want to toggle Terminate Cluster?', 'Terminate Cluster', 'refresh')  }
                checked={this.state.doTerminate}
                />
                <label
                    style={{ background: this.state.doTerminate ? '#53b700' : '#ff0100', width: '175px' }}
                    className="terminate-cluster-switch-label"
                    htmlFor={this.props.dataItem.clusterId}
                >
                    <span className={`react-switch-button`} />
                    {this.state.doTerminate ? <p className="subscribe-toggle-on">Auto-Terminate: On</p> : <p className="subscribe-toggle-off">Auto-Terminate: Off</p>}
                </label>    
            </div>
            }
            
          </div>
        );
      case 'autopilot' :
        return (
          <div>
            {this.props.dataItem.status === 'TERMINATED' || this.props.dataItem.status === 'FAILED' || this.props.dataItem.status === 'TERMINATION_INITIATED' || this.props.dataItem.status === 'BOOTSTRAPPING' ? 
              this.state.autoAMIRotation ?
              <div>
                <div style={{ display: 'flex' }}>
                  <span style={{ position: 'relative', marginLeft: '10px' }}>From</span>
                  <span style={{ position: 'relative', marginLeft: '50px' }}>To</span>
                  <span style={{ position: 'relative', marginLeft: '65px' }}>SLA </span>
                </div>
                <div style={{ display: 'flex' }}>
                  <TimePicker 
                      disabled='true'
                      className='customTimePicker'
                      onChange={() => console.log('False') }
                      format={"HH"}
                      value={this.state.autopilot_window_start}
                  />
                  <TimePicker 
                    disabled='true'
                    className='customTimePicker'
                    onChange={() => console.log('False') }
                    format={"HH"}
                    value={this.state.autopilot_window_end}
                  />
                  <input className="customslafield" disabled defaultValue="30" placeholder="Custom SLA for AMI Rotation" value={this.state.ami_rotation_sla} onChange={this.handleAMIRotationSLA} />
                </div> 
                <div style={{ margin: '0 auto', width: '10%' }}>
                  <Fab className='autopilotbtn' disabled color='primary' title='Click to set Auto-pilot window' onClick={this.handleAutoAMIRotation}>
                    <CheckCircle />
                  </Fab>
                </div>
              </div> : 
              <div>
                <div style={{ textAlign: 'center' }}>
                  <div>
                    <span style={{ position: 'relative', marginRight: '10px', marginTop: '10px' }}>SLA Days</span>
                  </div>
                  <input className="customslafield1" disabled defaultValue="30" placeholder="Custom SLA for AMI Rotation" value={this.state.ami_rotation_sla} onChange={this.handleAMIRotationSLA} />
                  <Fab className='autopilotbtn' color='primary' disabled title='Click to set Auto-pilot window' onClick={this.handleAutoAMIRotation}>
                    <CheckCircle />
                  </Fab>
                </div>
              </div>
               : null
            }
            {!(this.props.dataItem.status === 'TERMINATED' || this.props.dataItem.status === 'FAILED' || this.props.dataItem.status === 'TERMINATION_INITIATED' || this.props.dataItem.status === 'BOOTSTRAPPING') ?
              this.state.autoAMIRotation ?
              <div>
                <div style={{ display: 'flex' }}>
                  <span style={{ position: 'relative', marginLeft: '10px' }}>From</span>
                  <span style={{ position: 'relative', marginLeft: '50px' }}>To</span>
                  <span style={{ position: 'relative', marginLeft: '65px' }}>SLA </span>
                </div>
                <div style={{ display: 'flex' }}>
                  <TimePicker 
                      className='customTimePicker'
                      onChange={(e) => this.handleTimeChange(e, "start")}
                      format={"HH"}
                      value={this.state.autopilot_window_start}
                  />
                  <TimePicker 
                    className='customTimePicker'
                    onChange={(e) => this.handleTimeChange(e, "end")}
                    format={"HH"}
                    value={this.state.autopilot_window_end}
                  />
                  <input className="customslafield" defaultValue="30" placeholder="Custom SLA for AMI Rotation" value={this.state.ami_rotation_sla} onChange={this.handleAMIRotationSLA} />
                </div> 
                <div style={{ margin: '0 auto', width: '10%' }}>
                  <Fab className='autopilotbtn' color='primary' title='Click to set Auto-pilot window' onClick={this.handleAutoPilotSLAAlert}>
                    <CheckCircle />
                  </Fab>
                </div>
              </div> : 
              <div>
                <div style={{ textAlign: 'center' }}>
                  <div>
                    <span style={{ position: 'relative', marginRight: '10px', marginTop: '10px' }}>SLA Days</span>
                  </div>
                  <input className="customslafield1" defaultValue="30" placeholder="Custom SLA for AMI Rotation" value={this.state.ami_rotation_sla} onChange={this.handleAMIRotationSLA} />
                  <Fab className='autopilotbtn' color='primary' title='Click to set Auto-pilot window' onClick={this.handleAutoAMIRotation}>
                    <CheckCircle />
                  </Fab>
                </div>
              </div>
               : null
            } 
          </div> 
          
        )
      case 'rotateAMI' :
        return (
          <div>
          <div style={{display: 'flex'}}>
            <div style={{marginTop: '6px', marginRight: '10px'}}>
              <div><span>Rotate</span></div>
              <Fab color='primary' title='Click to rotate ami' disabled={rotate_disabled(this.props)} onClick={this.handleRotateAMIButtonClick}>
                <AutoRenew />
              </Fab>
            </div>

            {
               this.props.dataItem.status === 'TERMINATED' || this.props.dataItem.status === 'FAILED' || this.props.dataItem.status === 'TERMINATION_INITIATED' || this.props.dataItem.status === 'BOOTSTRAPPING' ?
              <div style={{ marginTop: '7px' }}>
                <span>Auto-Pilot</span>
                <div className="auto-rotation-actions">
                    <input
                    className="react-switch-checkbox"
                    id={this.props.dataItem.clusterId}
                    type="checkbox"
                    onChange={() => console.log('False') }
                    checked={this.state.autoAMIRotation}
                />
                <label
                    style={{ background: '', width: '70px'}}
                    className="auto-rotate-switch-label"
                    htmlFor={this.props.dataItem.clusterId}
                >
                    <span className={`react-switch-button`} />
                    {this.state.autoAMIRotation ? <p className="toggle-on">ON</p> : <p className="toggle-off">OFF</p>}
                </label> 
              </div>    
            </div>           
            : this.state.autoAMIRotation ?
            <div style={{ marginTop: '7px' }}>
              <span>Auto-Pilot</span>
              <div className="auto-rotation-actions">
                <input
                className="react-switch-checkbox"
                id={this.props.dataItem.clusterId}
                type="checkbox"
                onChange={() => this.setAlert(true, 'confirm', 'Are you sure you want to toggle Auto-Rotation?', 'Auto-Rotate AMI', 'refresh')  }
                checked={this.state.autoAMIRotation}
              />
              <label
                  style={{ background: this.state.autoAMIRotation ? '#53b700' : '#ff0100', width: '70px' }}
                  className="auto-rotate-switch-label"
                  htmlFor={this.props.dataItem.clusterId}
              >
                  <span className={`react-switch-button`} />
                  {this.state.autoAMIRotation ? <p className="toggle-on">ON</p> : <p className="toggle-off">OFF</p>}
              </label> 
              </div>
            </div>
            : !this.state.autoAMIRotation ?
            <div style={{ marginTop: '7px' }}>
              <span>Auto-Pilot</span>
              <div className="auto-rotation-actions">
                <input
                  className="react-switch-checkbox"
                  id={this.props.dataItem.clusterId}
                  type="checkbox"
                  // onChange={() => this.setAlert(true, 'confirm', 'Toggling Auto-pilot settings for AMI Rotation.', 'Auto-Rotate AMI', 'refresh')  }
                  onChange={() => this.handleAutoAMIRotationAlert()}
                  checked={this.state.autoAMIRotation}
              />
              <label
                  style={{ background: this.state.autoAMIRotation ? '#53b700' : '#e40000', width: '70px' }}
                  className="auto-rotate-switch-label"
                  htmlFor={this.props.dataItem.clusterId}
              >
                  <span className={`react-switch-button`} />
                  {this.state.autoAMIRotation ? <p className="toggle-on">ON</p> : <p className="toggle-off">OFF</p>}
              </label>
              </div>
            </div>
            : 
            ''            
            }
          </div>
          </div>
        );

      case 'testSuites' :
        return (
          <Fab  color='secondary' title='Click to view and run tests' disabled={!dis_state(this.props)} onClick={this.handleTestSuiteButtonClick}>
            <Settings />
          </Fab>
        );
      case 'dnsFlip' :
        return (
          <Fab  color='secondary' title='Click to perform dns flip' disabled={!dis_state(this.props)} onClick={this.handleDNSFlipButtonClick}>
            <FlipToBack />
          </Fab>
        );
      case 'cloneCluster':
        return (
          <Fab  color='secondary' title='Click to create a new cluster with the same properties' onClick={this.handleCloneClusterButtonClick}>
            <FileCopy />
          </Fab>
        )
      case 'allClusters':
        return (
          <div style={{ display: 'flex' }}>
            {
              this.props.dataItem.status === 'TERMINATED' || this.props.dataItem.status === 'FAILED' || this.props.dataItem.createdBy === "Jenkins"?               
              <div style={{ marginRight: '10px' }}>
              <Fab color='primary' disabled  title='View create cluster workflow' onClick={this.handleCreateClusterWorkflowClick}>
                <CreateIcon />
              </Fab>
              </div> :
                <div style={{ marginRight: '10px' }}>
                  <Fab color='primary' title='View create cluster workflow' onClick={this.handleCreateClusterWorkflowClick}>
                    <CreateIcon />
                  </Fab>
                </div>
            }
            
            <div style={{ marginRight: '10px' }}>
              <Fab color='primary' title='View rotate AMI workflow' onClick={this.handleRotateAMIWorkflowClick}>
                <AutoRenew />
              </Fab>
            </div>
            {
              this.props.dataItem.status === 'TERMINATED' || this.props.dataItem.status === 'FAILED' || this.props.dataItem.createdBy === "Jenkins" ?               
              <div style={{ marginRight: '10px' }}>
                <Fab  color='primary' disabled title='View running tests and their workflows' onClick={this.handleTestSuiteButtonClick}>
                  <Settings />
                </Fab>
              </div> :
              <div style={{ marginRight: '10px' }}>
                <Fab  color='primary' title='View running tests and their workflows' onClick={this.handleTestSuiteButtonClick}>
                  <Settings />
                </Fab>
              </div>
            }


          </div>
        )
    }
  }

  handleAutoAMIRotationAlert() {
    console.log("Reached here");
    this.setState({
      ...this.state,
      auto_ami_rotation_alert: !this.state.auto_ami_rotation_alert
    })
  }

  handleAutoPilotSLAAlert() {
    this.setState({
      ...this.state,
      autopilot_sla_alert: !this.state.autopilot_sla_alert
    })
  }

  handleTimeChange(e, type) {
    if (type === 'start') {
        this.setState({
            ...this.state,
            autopilot_window_start: e.target.value,
            autopilot_error: false
        })
    } else {
        this.setState({
            ...this.state,
            autopilot_window_end: e.target.value,
            autopilot_error: false
        })
    }  
  }

  handleAMIRotationSLA(e) {
    if (e.target.value === '' || e.target.value.match("^[1-9][0-9]*$")) {
      this.setState({
          ...this.state,
          ami_rotation_sla: e.target.value
      })
  }
  }

  handleCloneClusterButtonClick() {
      this.props.handleActionBtnClick('createCluster', 'clone', this.props.dataItem.clusterId);
  }

  handleWorkflowAlert() {
    this.setState({
      ...this.state,
      workflowalert: false
    })
  }
  handleAlert() {
    this.setState({
      ...this.state,
      showAlert: false,
      alertMessage: '',
      alertTitle: '',
      alertIcon: '',
      showConfirmButton: false
    })
    if(this.state.alertTitle === 'Auto-Rotate AMI') {
      this.props.dataItem.autoAmiRotation = this.props.dataItem.autoAmiRotation
    } else if (this.state.alertTitle === 'Terminate Cluster') {
      this.props.dataItem.doTerminate = this.props.dataItem.doTerminate
    } 

  }
  handleConfirm() {
    if(this.state.alertTitle === 'Auto-Rotate AMI') {
      this.toggleAutoAMIRotation()
    } else if (this.state.alertTitle === 'Terminate Cluster') {
      this.toggleTerminateCluster()
    }

  }
  rotateToggle() {
    this.setAlert(true, 'confirm', 'Are you sure you want to toggle Auto-Rotation?', 'Auto-Rotate AMI', 'refresh')  
  }
  setAlert( status, type, message, title, icon) {
    if(type === 'alert') {
      this.setState({
        ...this.state,
        showAlert: status,
        alertMessage: message,
        alertTitle: title,
        alertIcon: icon
      })      
    } else if(type === 'confirm' && (title === 'Auto-Rotate AMI' || title === 'Terminate Cluster')) {
      this.setState({
        ...this.state,
        showAlert: status,
        alertMessage: message,
        alertTitle: title,
        alertIcon: icon,
        showConfirmButton: true
      })  
    }
  }
  handleRotateAMIAlert() {
    this.setState({
      ...this.state,
      rotateamialert: false
    })
  }

  handleAutoPilotWindowAlert() {
    this.setState({
      ...this.state,
      autopilot_error: false
    })
  }

  handleStepsStatusData(name, id) {
    this.props.handleStepsStatus(name, id, this.props.token);
  }

  handleModalSubmitAddSteps(data)  {
    GridCell.postAddSteps(data,this.props.token)
    this.setState({ ModalAddSteps: false })
  }

  handleModalSubmitTerminateCluster(data)  {
    GridCell.postTerminateCluster(data,this.props.token)
    this.setState({ ModalTerminateCluster: false })
  }

  handleModalSubmitRotateAMI(data)  {
    GridCell.postRotateAMI(data,this.props.token)
    this.setState({ ModalRotateAMI: false })
  }

  handleModalSubmitDNSFlip(data) {
    GridCell.postDNSFlip(data, this.props.token)
    this.setState({ ModalDNSFlip: false })
  }
  
  handleAddStepsButtonClick() {
    if(this.checkMatch(this.props.dataItem.createdBy, "Jenkins")){
      this.setAlert(true, 'alert', 'Adding Steps is only available for clusters from DCC', 'Add Steps', 'add')
    } else {
      let modalState ='True'
      if (false) {
        alert('Incomplete form')
      } else {
        this.setState({
          ...this.state,
          ModalAddSteps: true,
          ModalTerminateCluster: false,
          ModalRotateAMI: false,
          ModalTestSuite: false,
          ModalCreatClusterWorkflow: false,
          ModalRotateAMIWorkflow: false,
          ModalDNSFlip: false,
          modalState
        })
      }
    }

  }

  handleTestSuiteWorkflowButtonClick() {
    if (this.checkMatch(this.props.dataItem.createdBy, "Jenkins")) {
      this.setAlert(true, 'alert', 'Test Suites Workflow only available for clusters created from DCC', 'Test Suite Workflow', 'cog')
    } else  {
      let modalState ='True'
      this.setState({
        ...this.state,
        ModalAddSteps: false,
        ModalTerminateCluster: false,
        ModalRotateAMI: false,
        ModalTestSuite: true,
        ModalCreatClusterWorkflow: false,
        ModalRotateAMIWorkflow: false,
        ModalDNSFlip: false,
        modalState
      })
    }
  }

  handleTestSuiteButtonClick() {
      if(this.checkMatch(this.props.dataItem.createdBy, "Jenkins")){
        this.setAlert(true, 'alert', 'Test Suites only available for clusters created from DCC', 'Test Suite', 'cog')
      } else {
        let modalState ='True'
        this.setState({
          ...this.state,
          ModalAddSteps: false,
          ModalTerminateCluster: false,
          ModalRotateAMI: false,
          ModalTestSuite: true,
          ModalCreatClusterWorkflow: false,
          ModalRotateAMIWorkflow: false,
          ModalDNSFlip: false,
          modalState
        })       
      }
  }

  handleDNSFlipButtonClick() {
      // let modalState = 'True'
      // if (false) {
      //   alert('Incomplete Form')
      // } else {
        this.setState({
          ...this.state,
          ModalAddSteps: false,
          ModalTerminateCluster: false,
          ModalRotateAMI: false,
          ModalTestSuite: false,
          ModalCreatClusterWorkflow: false,
          ModalRotateAMIWorkflow: false,
          ModalDNSFlip: true,
          // modalState
        })
      // }
  }

  toggleTerminateCluster(event) {
      this.setState({
        ...this.state,
        doTerminate : !this.state.doTerminate,
        showAlert: false,
        alertMessage: '',
        alertTitle: '',
        alertIcon: '',
        showConfirmButton: false
      })
      this.handleDoTerminate()
  }
  
  toggleAutoAMIRotation(event) {

    if (this.checkMatch(this.props.dataItem.createdBy, "Jenkins")) {
      this.setAlert(true, 'alert', 'Auto-Rotate AMI can only be set for clusters created from DCC', 'Auto-Rotate AMI', 'refresh')
    } else {
      this.setState({
        ...this.state,
        autoAMIRotation : !this.state.autoAMIRotation,
        showAlert: false,
        alertMessage: '',
        alertTitle: '',
        alertIcon: '',
        showConfirmButton: false
      })
      this.handleAutoAMIRotation()
    }
  }

  handleDoTerminate(event) {
    this.props.handleDoTerminate(this.props.dataItem.clusterName, this.props.dataItem.clusterId, !this.state.doTerminate);
    setTimeout(function () {
      if (this.props.tab === 'all') {
        this.props.fetchAllClusterMetaData(this.props.token);
      } else {
        this.props.fetchClusterWiseMetaData(this.props.tab, this.props.token);
      }
    }.bind(this), 200)
  }

  handleCustomAlertAutoAMI(startHour, endHour, sla) {
    this.props.handleAutoRotateAMI(this.props.dataItem.clusterId, !this.state.autoAMIRotation, formatDate(startHour, "HH"), formatDate(endHour, "HH"), sla)
    setTimeout(function () {
      if (this.props.tab === 'all') {
        this.props.fetchAllClusterMetaData(this.props.token);
      } else {
        this.props.fetchClusterWiseMetaData(this.props.tab, this.props.token);
      }
    }.bind(this), 200)
  }

  handleAutoAMIRotation(event) {
    this.props.handleAutoRotateAMI(this.props.dataItem.clusterId, this.state.autoAMIRotation, formatDate(this.state.autopilot_window_start, "HH"), formatDate(this.state.autopilot_window_end, "HH"), this.state.ami_rotation_sla)
    setTimeout(function () {
      if (this.props.tab === 'all') {
        this.props.fetchAllClusterMetaData(this.props.token);
      } else {
        this.props.fetchClusterWiseMetaData(this.props.tab, this.props.token);
      }
    }.bind(this), 200)
    
  }
  handleTerminateClusterButtonClick() {
    if(this.checkMatch(this.props.dataItem.createdBy, "Jenkins")) {
      this.setAlert(true, 'alert', 'Terminating Clusters are only available for clusters created from DCC', 'Terminate Cluster', 'trash')
    } else {
      let modalState ='True'
      if (false) {
        alert('Incomplete form')
      } else {
        this.setState({
          ...this.state,
          ModalAddSteps: false,
          ModalTerminateCluster: true,
          ModalRotateAMI: false,
          ModalTestSuite: false,
          ModalCreatClusterWorkflow: false,
          ModalRotateAMIWorkflow: false,
          ModalDNSFlip: false,
          modalState
        })
      }
    }
  }
  checkMatch(a, b) {
    return a === b
  }
  handleRotateAMIButtonClick() {
    if (this.checkMatch(this.props.dataItem.createdBy, "Jenkins")) {
      this.setAlert(true, 'alert','AMI can be rotated only for clusters created from DCC.', 'Cluster Rotation', 'refresh')
    } else {
      let modalState ='True'
      if (false) {
        alert('Incomplete form')
      } else {
        this.setState({
          ...this.state,
          ModalAddSteps: false,
          ModalTerminateCluster: false,
          ModalRotateAMI: true,
          ModalTestSuite: false,
          ModalCreatClusterWorkflow: false,
          ModalRotateAMIWorkflow: false,
          ModalDNSFlip: false,
          modalState
        })
      }
    }
    
  }

  handleCreateClusterWorkflowClick() {
    if (this.checkMatch(this.props.dataItem.createdBy, "Jenkins")) {
      this.setAlert(true, 'alert', 'Cluster Workflow can be created only for clusters created from DCC', 'Create Cluster Workflow', 'edit')
    } else {
      let modalState ='True'
      this.setState({
        ...this.state,
        ModalAddSteps: false,
        ModalTerminateCluster: false,
        ModalRotateAMI: false,
        ModalTestSuite: false,
        ModalCreatClusterWorkflow: true,
        ModalRotateAMIWorkflow: false,
        ModalDNSFlip: false,
        modalState
      })
    }
    
  }

  handleRotateAMIWorkflowClick() {
    if (this.checkMatch(this.props.dataItem.createdBy, "Jenkins")) {
      this.setAlert(true, 'alert', 'Rotate AMI Workflow only available for clusters created from DCC.', 'Rotate AMI Workflow', 'refresh')
    } else {
      let modalState ='True'
      this.setState({
        ...this.state,
        ModalAddSteps: false,
        ModalTerminateCluster: false,
        ModalRotateAMI: false,
        ModalTestSuite: false,
        ModalCreatClusterWorkflow: false,
        ModalRotateAMIWorkflow: true,
        ModalDNSFlip: false,
        modalState
      })
    }
  }

  handleModalClose() {
    this.setState({
      ...this.state,
      ModalAddSteps: false,
      ModalTerminateCluster: false,
      ModalRotateAMI: false,
      ModalTestSuite: false,
      ModalCreatClusterWorkflow: false,
      ModalRotateAMIWorkflow: false,
      ModalDNSFlip: false
    })
  }
}