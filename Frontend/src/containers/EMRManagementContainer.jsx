import React, { Component } from 'react'
import { EMRManagement } from '../components'
import { connect } from 'react-redux'
import { Intent,Toaster,Toast, Position } from '@blueprintjs/core'
import { 
  fetchAllClusterMetaData, 
  fetchClusterWiseMetaData, 
  fetchAccountWiseMetaData, 
  fetchStepsStatusData, 
  postAddStepsData, 
  postTerminateClusterData, 
  postRotateAMI, 
  postAutoRotateAMI,
  postCreateClusterData,
  postDNSFlip,
  clearErrors,
  postDoTerminate,
  fetchUIDropdownList,
  fetchGlobalJiraEnabled,
  fetchAccountJiraEnabled
} from '../actions/emrManagement'
import '../components/EMRHealth/emrHealth.css'

/**
 * Container for EMR Management component.
 */
class EMRManagementContainer extends Component {
  constructor(props) {
    super(props)
    this.toaster = Toaster
    this.refHandlers = {
        toaster: (ref) => this.toaster = ref
    }

    this.state = {
        toasts: [],
        spinnerActive: false
    }

    this.handleRangeChange = this.handleRangeChange.bind(this)
    this.handleStepsStatus = this.handleStepsStatus.bind(this)
    this.SuccessToast = this.SuccessToast.bind(this)
    this.ErrorToast = this.ErrorToast.bind(this)
    this.handleSpinnerActivity = this.handleSpinnerActivity.bind(this)
    this.handleAutoRotateAMI = this.handleAutoRotateAMI.bind(this)
    this.handleDoTerminate = this.handleDoTerminate.bind(this);
  }

  render() {
    return (
      <div className='emr-management-container'>
        <Toaster position={Position.TOP} ref={this.refHandlers.toaster}>
              {/* "Toasted!" will appear here after clicking button. */}
              {this.state.toasts.map(toast => <Toast {...toast} />)}
            </Toaster>
        
        <span id='displayTimer' className="displayTimer"/>
        
        <div id='displayDiv' >
          {
            (!this.props.uiListFetching && 
              this.props.uiListSuccess || 
              this.props.uiListData) && 
              !this.props.globalJiraFetching ?
            <EMRManagement 
              fetching={this.props.emrMetadataDataFetching} 
              handleRangeChange={this.handleRangeChange} 
              handleStepsStatus={this.handleStepsStatus}
              handleAutoRotateAMI={this.handleAutoRotateAMI}
              data={this.props.emrMetadataData} 
              fullName={this.props.fullName}
              role={this.props.role}
              token={this.props.token}
              dccRoles={this.props.dccRoles}
              superAdmin={this.props.superAdmin}
              postAddSteps={this.props.postAddStepsData}
              postTerminateCluster={this.props.postTerminateClusterData}
              postRotateAMI={this.props.postRotateAMI}
              postCreateCluster={this.props.postCreateClusterData}
              stepsStatusData={this.props.stepsStatusData}
              activeSpinner={this.state.spinnerActive}
              fetchAllClusterMetaData={this.props.fetchAllClusterMetaData}
              fetchClusterWiseMetaData={this.props.fetchClusterWiseMetaData}
              handleSpinnerActivity={this.handleSpinnerActivity}
              handleDoTerminate={this.handleDoTerminate}
              fetchUIDropdownList={this.props.fetchUIDropdownList}
              uiListSuccess={this.props.uiListSuccess}
              uiListData={this.props.uiListData}
              globalJiraSuccess={false}
              globalJiraFetching={this.props.globalJiraFetching}
              globalJiraData={this.props.globalJiraData}
           /> : null
          }

        </div>
      </div>
    )
  }

  componentDidMount() {
    this.handleRangeChange('all');
    this.props.fetchGlobalJiraEnabled(this.props.token);
  }

  handleRangeChange(type, range) {
    if (type === 'all') {
      this.props.fetchAllClusterMetaData(this.props.token);
    } else if (type === 'exploratory') {
      this.props.fetchClusterWiseMetaData(type,this.props.token);
    } else if (type === 'scheduled') {
      this.props.fetchClusterWiseMetaData(type,this.props.token);
    } else if (type === 'transient'){
      this.props.fetchClusterWiseMetaData(type,this.props.token);
    } else if (type === 'accounts') {
      this.props.fetchAccountWiseMetaData(range,this.props.token);
    }
  }

  handleAutoRotateAMI(clusterID, auto_ami_rotation, window_start, window_end, ami_rotation_sla) {
    let data = { 
      clusterId: clusterID, 
      autoAmiRotation: auto_ami_rotation, 
      autopilotWindowStart: window_start, 
      autopilotWindowEnd: window_end, 
      amiRotationSlaDays: ami_rotation_sla 
    }
    this.props.postAutoRotateAMI(data, this.props.token)
  }
  
  handleStepsStatus(name,id) {
    this.props.fetchStepsStatusData(name,id,this.props.token);
  }

  handleDoTerminate(name, id, doTerminate) {
    let data = { clusterId: id, clusterName: name, doTerminate: doTerminate }
    this.props.postDoTerminate(data, this.props.token)
  }

  componentDidUpdate(prevProps) {
    if (prevProps !== this.props) {

      const { addStepsStatusSuccess, addStepsStatusError, terminateClusterStatusSuccess, terminateClusterStatusError,
        createClusterStatusSuccess, createClusterStatusError, rotateAMIStatusSuccess, rotateAMIStatusError, 
        dnsFlipStatusSuccess, dnsFlipStatusError, autoRotateAMISuccess, autoRotateAMIError } = this.props
      if (addStepsStatusSuccess || terminateClusterStatusSuccess || createClusterStatusSuccess || rotateAMIStatusSuccess || dnsFlipStatusSuccess) {
        this.SuccessToast('Successfully Posted')
        setTimeout(function () {
          window.location.reload()
        }.bind(this), 3000)
      }

      if (addStepsStatusError) {
        const message = this.props.addStepsStatusErrorData
        this.ErrorToast(message)
      } else if (terminateClusterStatusError) {
        const message = this.props.terminateClusterStatusErrorData
        this.ErrorToast(message)
      } else if (createClusterStatusError) {
        const message = this.props.createClusterStatusErrorData
        this.ErrorToast(message)
      } else if (rotateAMIStatusError) {
        const message = this.props.rotateAMIStatusErrorData
        this.ErrorToast(message);
      } else if (dnsFlipStatusError) {
        const message = this.props.dnsFlipStatusErrorData
        this.ErrorToast(message);
      } else if (autoRotateAMIError) {
        const message = this.props.autoRotateAMIErrorMessage
        this.ErrorToast(message);
      }
    }
  }

  SuccessToast() {
    this.toaster.show({ 
      intent: Intent.SUCCESS,
      icon: 'tick',
      message: "Successfully Posted!" 
    })
    this.setState({
      ...this.state,
      spinnerActive: false
    })
  }

  ErrorToast(errorData) {
    let errorMsg = <div>
                      <b>Error Mesage</b>: {errorData.errorMessage}
                      <br/>
                      <b>Error Details</b>: {errorData.messageDetails}
                      <br/>
                      <b>Error ID</b>: {errorData.errorId}
                    </div>
    this.toaster.show({ 
      intent: Intent.DANGER,
      icon: 'cross',
      message: errorMsg
    })
    this.props.clearErrors()
    this.setState({
      ...this.state,
      spinnerActive: false
    })

  }

  handleSpinnerActivity(data, token, type) {
    this.setState({
      ...this.state,
      spinnerActive: !this.state.spinnerActive
    });
    if (type === 'create') {
      this.props.postCreateClusterData(data, token);
    } else if (type === 'terminate') {
      this.props.postTerminateClusterData(data, token);
    } else if (type === 'rotate') {
      this.props.postRotateAMI(data, token);
    } else if (type === 'add') {
      this.props.postAddStepsData(data, token);
    } else if (type === 'dnsFlip') {
      this.props.postDNSFlip(data, token)
    }
  }
  
}

const mapStateToProps = state => {
  return {
    emrMetadataData: state.emrMetadataData.emrMetadataData,
    emrMetadataDataFetching: state.emrMetadataData.emrMetadataDataFetching,
    stepsStatusData: state.emrMetadataData.stepsStatusData,
    stepsStatusDataFetching: state.emrMetadataData.stepsStatusDataFetching,
    addStepsStatusSuccess: state.emrMetadataData.addStepsStatusSuccess,
    addStepsStatusError: state.emrMetadataData.addStepsStatusError,
    addStepsStatusErrorData: state.emrMetadataData.addStepsStatusErrorData,
    terminateClusterStatusSuccess: state.emrMetadataData.terminateClusterStatusSuccess,
    terminateClusterStatusError: state.emrMetadataData.terminateClusterStatusError,
    terminateClusterStatusErrorData: state.emrMetadataData.terminateClusterStatusErrorData,
    rotateAMIStatusSuccess: state.emrMetadataData.rotateAMIStatusSuccess,
    rotateAMIStatusError: state.emrMetadataData.rotateAMIStatusError,
    rotateAMIStatusErrorData: state.emrMetadataData.rotateAMIStatusErrorData,
    createClusterStatusSuccess: state.emrMetadataData.createClusterStatusSuccess,
    createClusterStatusError: state.emrMetadataData.createClusterStatusError,
    createClusterStatusErrorData: state.emrMetadataData.createClusterStatusErrorData,
    dnsFlipStatusSuccess: state.emrMetadataData.dnsFlipStatusSuccess,
    dnsFlipStatusError: state.emrMetadataData.dnsFlipStatusError,
    dnsFlipStatusErrorData: state.emrMetadataData.dnsFlipStatusErrorData,
    autoRotateAMIRequest: state.emrMetadataData.autoRotateAMIRequest,
    autoRotateAMISuccess: state.emrMetadataData.autoRotateAMISuccess,
    autoRotateAMIError: state.emrMetadataData.autoRotateAMIError,
    autoRotateAMIErrorMessage: state.emrMetadataData.autoRotateAMIErrorMessage,
    updateDoTerminateSuccess: state.emrMetadataData.updateDoTerminateSuccess,
    updateDoTerminateError: state.emrMetadataData.updateDoTerminateError,
    updateDoTerminateErrorData: state.emrMetadataData.updateDoTerminateErrorData,
    uiListData: state.emrMetadataData.uiListData,
    uiListFetching: state.emrMetadataData.uiListFetching,
    uiListSuccess: state.emrMetadataData.uiListSuccess,
    globalJiraFetching: state.emrMetadataData.globalJiraFetching,
    globalJiraSuccess: state.emrMetadataData.globalJiraSuccess,
    globalJiraError: state.emrMetadataData.globalJiraError,
    globalJiraData: state.emrMetadataData.globalJiraData
  }
}

const mapDispatchToProps = dispatch => {
  return {
    fetchAllClusterMetaData: (token) => dispatch(fetchAllClusterMetaData(token)),
    fetchClusterWiseMetaData: (type,token) => dispatch(fetchClusterWiseMetaData(type,token)),
    fetchAccountWiseMetaData: (range,token) => dispatch(fetchAccountWiseMetaData(range,token)),
    fetchStepsStatusData: (name,id,token) => dispatch(fetchStepsStatusData(name,id,token)),
    postAddStepsData: (data,token) => dispatch(postAddStepsData(data,token)),
    postTerminateClusterData: (data,token) => dispatch(postTerminateClusterData(data,token)),
    postRotateAMI: (data,token) => dispatch(postRotateAMI(data,token)),
    postAutoRotateAMI: (data, token) => dispatch(postAutoRotateAMI(data, token)),
    postCreateClusterData: (data,token) => dispatch(postCreateClusterData(data,token)),
    postDNSFlip: (data, token) => dispatch(postDNSFlip(data, token)),
    clearErrors: () => dispatch(clearErrors()),
    postDoTerminate: (data, token) => dispatch(postDoTerminate(data, token)),
    fetchUIDropdownList: (token) => dispatch(fetchUIDropdownList(token)),
    fetchGlobalJiraEnabled: (token) => dispatch(fetchGlobalJiraEnabled(token))
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(EMRManagementContainer)