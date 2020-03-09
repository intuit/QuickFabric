import React from 'react';
import { connect } from 'react-redux';
import { fetchRotateAMIWorkflow, fetchClusterWorkflowData, fetchStepsStatusModalData, fetchWorkflowData } from '../../actions/emrManagement';
import TestSuite from './TestSuite';
import StepsStatusDetail from './Modal/ModalStepsStatusDetail'

/**
 * Popup window to show the status of the AMI rotation of a cluster in the workflow-based visualization.
 */
class RotateAMIWorkflow extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showTestSuite: false,
            showSteps: false,
            showRotateWorkflowData: false,
            showWorkflowData: false,
            stepType: ''
        }

        this.createTestWorkflow = this.createTestWorkflow.bind(this);
        this.testSuiteToggle = this.testSuiteToggle.bind(this);
        this.toggleRotateWorkflowData = this.toggleRotateWorkflowData.bind(this);
        this.toggleWorkflowData = this.toggleWorkflowData.bind(this);
    }

    render() {
        return (
            <div>
               <div className="popup">
                {this.state.showRotateWorkflowData && this.renderRotateWorkflowData()}
                {this.state.showWorkflowData && this.renderWorkflowData()}
                    <div className="workflow-content">
                        <h3 style={{ margin: '20px', marginBottom: '30px' }}>Rotate AMI Workflow: <span style={{ color: '#0097E4' }}>{this.props.data.clusterName}</span></h3>
                        {this.props.rotateAMIWorkflowData !== undefined && this.props.rotateAMIWorkflowData.errorMessage !== undefined ? 
                            this.props.rotatedami !== undefined ? <div><span style={{ margin: '20px', fontSize: '15px' }}><div className='loading centered'>Workflow is being created. Please wait</div> </span></div> : 
                            <div><span style={{ margin: '20px', fontSize: '15px' }}>{this.props.rotateAMIWorkflowData.errorMessage}</span></div> :
                            <div>
                            {(this.props.rotateAMIWorkflowData !== undefined && 
                                this.props.rotateAMIWorkflowData["steps"] !== undefined) && 
                                <div style={{ margin: '20px' }}>{this.createTestWorkflow(this.props.rotateAMIWorkflowData)}</div>}
                            </div>
                        } 
                        <button type="button" className="workflowbtn" onClick={this.props.onClose}>Cancel</button>
                        {this.state.showTestSuite && 
                        <TestSuite 
                            data={this.props.data}
                            onClose={this.testSuiteToggle}
                            token={this.props.token}
                            from_workflow = {true}
                        />}
                    </div>
                    {this.state.showSteps ?
                        <StepsStatusDetail
                            key={this.props.data.clusterId}
                            name={this.props.data.clusterName}
                            stepsStatusData={this.props.stepsStatusModalData[this.props.data.clusterId]}
                            onClose={this.stepsToggle}
                            token={this.props.token}
                            type={this.state.stepType}
                        /> : ''}
                </div> 
            </div>
        )
    }

    createTestWorkflow = (data) => {
        const completed_state = <i className='material-icons workflow'>check_circle</i>
        const green_line = <div className='workflow-line-rotateami' />
        const incomplete_state = <i className='material-icons workflow gray'>album</i>
        const running_state = <i className='material-icons workflow blink'>album</i>
        const gray_line = <div className='workflow-line-rotateami gray' />
        const failed_state = <i className='material-icons workflow red'>error</i>

        const HAtestSuitesBtn = <a style={{ color: 'blue', marginLeft: '110px' }} onClick={this.testSuiteToggle}>Click for Details</a>
        const HArotateWorkflowClusterBtn  = <a style={{ color: 'blue', marginLeft: '10px' }} onClick={this.toggleRotateWorkflowData}>Click for Details</a>
        const HAworkflowClusterBtn  = <a style={{ color: 'blue', marginLeft: '110px' }} onClick={this.toggleWorkflowData}>Click for Details</a>
        const HAcustomStepsBtn = <a style={{ color: 'blue', marginLeft: '104px' }} onClick={() => this.stepsToggle("custom")}>Click for Details</a>
        const HAbootstrapsBtn = <a style={{ color: 'blue', marginLeft: '104px' }} onClick={() => this.stepsToggle("bootstraps")}>Click for Details</a>

        const workflowClusterBtn  = <a style={{ color: 'blue', marginLeft: '10px' }} onClick={this.toggleWorkflowData}>Click for Details</a>
        const testSuitesBtn = <a style={{ color: 'blue', marginLeft: '110px' }} onClick={this.testSuiteToggle}>Click for Details</a>
        const rotateWorkflowClusterBtn  = <a style={{ color: 'blue', marginLeft: '100px' }} onClick={this.toggleRotateWorkflowData}>Click for Details</a>
        const customStepsBtn = <a style={{ color: 'blue', marginLeft: '110px' }} onClick={() => this.stepsToggle("custom")}>Click for Details</a>
        const bootstrapsBtn = <a style={{ color: 'blue', marginLeft: '110px' }} onClick={() => this.stepsToggle("bootstraps")}>Click for Details</a>
        
        let workflow_elements = [];
        let workflow_labels = [];
        let workflow_buttons = [];
 
        let isHA = false;
        data["steps"].forEach(step => {
            if (step["workflowStatus"] === "SUCCESS") {
                workflow_elements.push(completed_state);
                workflow_elements.push(green_line);
            } else if (step["workflowStatus"] === "FAILED") {
                workflow_elements.push(failed_state);
                workflow_elements.push(gray_line);
            } else if (step["workflowStatus"] === "NEW") {
                workflow_elements.push(incomplete_state);
                workflow_elements.push(gray_line);
            } else if (step["workflowStatus"] === "IN_PROGRESS") {
                workflow_elements.push(running_state);
                workflow_elements.push(gray_line);
            }
            let labelTxt = step.workflowType;
            if (labelTxt === "MARK_CURRENT_CLUSTER_FOR_TERMINATION") {
                isHA = true;
            }
            labelTxt = labelTxt.split('_').join(' ')
            const step_label = <span className='label'>{labelTxt}</span>
            workflow_labels.push(step_label)
        })

        workflow_elements.pop();
        if (isHA) {
            workflow_buttons.push(HArotateWorkflowClusterBtn);
            workflow_buttons.push(HAbootstrapsBtn);
            workflow_buttons.push(HAcustomStepsBtn);
            workflow_buttons.push(HAtestSuitesBtn);
            workflow_buttons.push(HAworkflowClusterBtn);
        } else {
            workflow_buttons.push(workflowClusterBtn);
            workflow_buttons.push(rotateWorkflowClusterBtn);
            workflow_buttons.push(bootstrapsBtn);
            workflow_buttons.push(customStepsBtn);
            workflow_buttons.push(testSuitesBtn);
        }
        

        return (
            <div>
                <div className='workflow-component'>{workflow_labels}</div>
                <div className='workflow-component create-cluster'>{workflow_elements}</div>
                <div className='workflow-component'>{workflow_buttons}</div>
            </div>
        )
    }

    stepsToggle = (type) => {
        this.setState({
            ...this.state,
            showSteps: !this.state.showSteps,
            stepType: type
        })
    }

    testSuiteToggle() {
        this.setState({
            ...this.state,
            showTestSuite: !this.state.showTestSuite
        })
    }
    toggleRotateWorkflowData() {
        this.setState({
            ...this.state,
            showRotateWorkflowData: !this.state.showRotateWorkflowData
        })
    }
    toggleWorkflowData() {
        this.setState({
            ...this.state,
            showWorkflowData: !this.state.showWorkflowData
        })
    }
    renderRotateWorkflowData() {
        return (
                <div className="popup">
                    <div className='popup_inner'>
                        <div className='popup_close'>
                                <button className='close_popup' onClick={this.toggleRotateWorkflowData}>
                                    <i className='material-icons'>keyboard_arrow_right</i>
                                </button>
                        </div>
                        <div className='popup-details'>
                            <h3>Rotate AMI Workflow Data: <span>{this.props.data.clusterName}</span> </h3>
                            <h4>Account:</h4><p>{this.props.requestClusterWorkflowData.account}</p>
                            <h4>Cluster ID:</h4><p>{this.props.requestClusterWorkflowData.clusterId}</p>
                            <h4>Segment:</h4><p>{this.props.requestClusterWorkflowData.segment}</p>
                            <h4>Status:</h4><p>{this.props.requestClusterWorkflowData.status}</p>
                            <h4>Cluster Details:</h4><p>{this.props.requestClusterWorkflowData.clusterDetails}</p>
                            <h4>Auto-Terminate:</h4><p>{`${this.props.requestClusterWorkflowData.doTerminate}` + ''}</p>
                            <h4>Created By:</h4><p>{this.props.requestClusterWorkflowData.createdBy}</p>
                            <h4>DNS Name:</h4><p>{this.props.requestClusterWorkflowData.dnsName}</p>
                            <h4>Flip to Production:</h4><p>{`${this.props.requestClusterWorkflowData.dnsFlip}` + ''}</p>
                            <h4>Is Prod:</h4><p>{`${this.props.requestClusterWorkflowData.isProd}` + ''}</p>
                            <h4>Original Cluster ID:</h4><p>{this.props.requestClusterWorkflowData.originalClusterId}</p>
                            <h4>DNS Flip Completed:</h4><p>{`${this.props.requestClusterWorkflowData.dnsFlipCompleted}` + ''}</p>
                            <h4>Type:</h4><p>{this.props.requestClusterWorkflowData.type}</p>
                            <h4>Auto AMI Rotation:</h4><p>{`${this.props.requestClusterWorkflowData.autoAmiRotation}` + ''}</p>
                            <h4>Auto-Pilot Window Start:</h4><p>{this.props.requestClusterWorkflowData.autopilotWindowStart}</p>
                            <h4>Auto-Pilot Window End:</h4><p>{this.props.requestClusterWorkflowData.autopilotWindowEnd}</p>
                            <h4>AMI Rotation SLA Days:</h4><p>{this.props.requestClusterWorkflowData.amiRotationSlaDays}</p>
                        </div>
                    </div>
            
                </div>        
        )
    }

    renderWorkflowData() {
        return (
                <div className="popup">
                    <div className='popup_inner'>
                        <div className='popup_close'>
                                <button className='close_popup' onClick={this.toggleWorkflowData}>
                                    <i className='material-icons'>keyboard_arrow_right</i>
                                </button>
                        </div>
                        <div className='popup-details'>
                            <h3>Rotate AMI Workflow Data: <span>{this.props.data.clusterName}</span> </h3>
                            <h4>Account:</h4><p>{this.props.requestWorkflowData.account}</p>
                            <h4>Cluster ID:</h4><p>{this.props.requestWorkflowData.clusterId}</p>
                            <h4>Segment:</h4><p>{this.props.requestWorkflowData.segment}</p>
                            <h4>Status:</h4><p>{this.props.requestWorkflowData.status}</p>
                            <h4>Cluster Details:</h4><p>{this.props.requestWorkflowData.clusterDetails}</p>
                            <h4>Auto-Terminate:</h4><p>{`${this.props.requestWorkflowData.doTerminate}` + ''}</p>
                            <h4>Created By:</h4><p>{this.props.requestWorkflowData.createdBy}</p>
                            <h4>DNS Name:</h4><p>{this.props.requestWorkflowData.dnsName}</p>
                            <h4>Flip to Production:</h4><p>{`${this.props.requestWorkflowData.dnsFlip}` + ''}</p>
                            <h4>Is Prod:</h4><p>{`${this.props.requestWorkflowData.isProd}` + ''}</p>
                            <h4>Original Cluster ID:</h4><p>{this.props.requestWorkflowData.originalClusterId}</p>
                            <h4>DNS Flip Completed:</h4><p>{`${this.props.requestWorkflowData.dnsFlipCompleted}` + ''}</p>
                            <h4>Type:</h4><p>{this.props.requestWorkflowData.type}</p>
                            <h4>Auto AMI Rotation:</h4><p>{`${this.props.requestWorkflowData.autoAmiRotation}` + ''}</p>
                            <h4>Auto-Pilot Window Start:</h4><p>{this.props.requestWorkflowData.autopilotWindowStart}</p>
                            <h4>Auto-Pilot Window End:</h4><p>{this.props.requestWorkflowData.autopilotWindowEnd}</p>
                            <h4>AMI Rotation SLA Days:</h4><p>{this.props.requestWorkflowData.amiRotationSlaDays}</p>
                        </div>
                    </div>
            
                </div>        
        )
    }

    componentDidMount() {
        this.props.fetchClusterWorkflowData(this.props.data.clusterId, this.props.token)
        this.props.fetchWorkflowData(this.props.data.clusterId, this.props.token)
        this.props.fetchRotateAMIWorkflow(this.props.data.metadataId, this.props.token)
        this.props.fetchStepsStatusModalData(this.props.data.clusterName, this.props.data.clusterId, true, this.props.token)
        this.interval = setInterval(() => this.props.fetchRotateAMIWorkflow(this.props.data.metadataId, this.props.token), 30000);
    }

    componentWillUnmount() {
        clearInterval(this.interval)
    }
}

const mapStateToProps = state => {
    return {
        stepsStatusModalData: state.emrMetadataData.stepsStatusModalData,
        requestClusterWorkflowData: state.emrMetadataData.requestClusterWorkflowData,
        rotateAMIWorkflowData: state.emrMetadataData.rotateAMIWorkflowData,
        requestWorkflowData: state.emrMetadataData.requestWorkflowData,
    }
}

const mapDispatchToProps = dispatch => {
    return {
        fetchWorkflowData: (id, token) => dispatch(fetchWorkflowData(id, token)),
        fetchClusterWorkflowData: (id, token) => dispatch(fetchClusterWorkflowData(id, token)),
        fetchRotateAMIWorkflow: (record_id, token) => dispatch(fetchRotateAMIWorkflow(record_id, token)),
        fetchStepsStatusModalData: (name, record_id, fromWorkflow, token) => dispatch(fetchStepsStatusModalData(name, record_id, fromWorkflow, token))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(RotateAMIWorkflow)