import React from 'react';
import { connect } from 'react-redux';
import { fetchCreateClusterWorkflow, fetchClusterWorkflowData, fetchStepsStatusModalData } from '../../actions/emrManagement';
import ModalTestSuite from './TestSuite';
import ModalCreateClusterDetail from './Modal/ModalCreateClusterDetail';
import StepsStatusDetail from './Modal/ModalStepsStatusDetail'

/**
 * Component to show Create Cluster Workflow visualization in a popup window.
 */
class CreateClusterWorkflow extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            showTestSuite: false,
            showSteps: false,
            showCreateClusters: false,
            stepType: ''
        }

        this.createTestWorkflow = this.createTestWorkflow.bind(this);
        this.testSuiteToggle = this.testSuiteToggle.bind(this);
        this.stepsToggle = this.stepsToggle.bind(this);
        this.createClustersToggle = this.createClustersToggle.bind(this);
    }

    render() {
        return (
            <div>
               <div className="popup">
                    <div className="workflow-content">
                        <h3 style={{ margin: '20px', marginBottom: '30px' }}>Create Cluster Workflow: <span style={{ color: '#0097E4' }}>{this.props.data.cluster_name}</span></h3>
                        <div cla>
                            {(this.props.createClusterWorkflowData !== undefined &&
                                this.props.createClusterWorkflowData["steps"] !== undefined) &&
                                <div style={{ margin: '20px' }}>{this.createTestWorkflow(this.props.createClusterWorkflowData)}</div>}
                        </div>
                        <button type="button" className="nextBtn" onClick={this.props.onClose}>Cancel</button>
                        {this.state.showTestSuite &&
                        <ModalTestSuite
                            data={this.props.data}
                            onClose={this.testSuiteToggle}
                            token={this.props.token}
                        />}
                        {this.state.showCreateClusters ?
                        <ModalCreateClusterDetail
                            key={this.props.data.clusterId}
                            data={this.props.data}
                            onClose={this.createClustersToggle}
                            token={this.props.token}
                        /> : ''}
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
            </div>
        )
    }

    /**
     * Creating the workflow visualization.
     */
    createTestWorkflow = (data) => {
        const completed_state = <i className='material-icons workflow'>check_circle</i>
        const green_line = <div className='workflow-line-tabular' />
        const incomplete_state = <i className='material-icons workflow gray'>album</i>
        const running_state = <i className='material-icons workflow blink'>album</i>
        const gray_line = <div className='workflow-line-tabular gray' />
        const failed_state = <i className='material-icons workflow red'>error</i>

        if (data['steps'] === null) return;
        const createNewClusterBtn = <a style={{ color: 'blue', marginRight: '180px' }} onClick={this.createClustersToggle}>Click for Details</a>
        const customStepsBtn = <a style={{ color: 'blue', marginRight: '170px' }} onClick={() => this.stepsToggle("custom")}>Click for Details</a>
        const bootstrapsBtn = <a style={{ color: 'blue', marginRight: '170px' }} onClick={() => this.stepsToggle("bootstraps")}>Click for Details</a>
        const testSuitesBtn = <a style={{ color: 'blue' }} onClick={this.testSuiteToggle}>Click for Details</a>
        let workflow_elements = [];
        let workflow_labels = [];
        let workflow_buttons = [];

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
            let label = step.workflowType.split('_').join(' ');
            let step_label = <span className='label createnewcluster'>{label}</span>
            workflow_labels.push(step_label)
        })

        workflow_elements.pop();
        workflow_buttons.push(createNewClusterBtn);
        workflow_buttons.push(bootstrapsBtn);
        workflow_buttons.push(customStepsBtn);
        workflow_buttons.push(testSuitesBtn);

        return (
            <div>
                <div className='workflow-component'>{workflow_labels}</div>
                <div className='workflow-component create-cluster'>{workflow_elements}</div>
                <div className='workflow-component'>{workflow_buttons}</div>
            </div>
        )
    }

    stepsToggle(type) {
        this.setState({
            ...this.state,
            showSteps: !this.state.showSteps,
            stepType: type
        })
    }

    createClustersToggle() {
        this.setState({
            ...this.state,
            showCreateClusters: !this.state.showCreateClusters
        })
    }

    testSuiteToggle() {
        this.setState({
            ...this.state,
            showTestSuite: !this.state.showTestSuite
        })
    }

    componentDidMount() {
        this.props.fetchStepsStatusModalData(this.props.data.clusterName, this.props.data.clusterId, false, this.props.token)
        this.props.fetchCreateClusterWorkflow(this.props.data.metadataId, this.props.token)
        this.intervalId = setInterval(() => this.props.fetchCreateClusterWorkflow(this.props.data.metadataId, this.props.token), 30000)
    }

    componentWillUnmount() {
        clearInterval(this.intervalId)
    }
}

const mapStateToProps = state => {
    return {
        stepsStatusModalData: state.emrMetadataData.stepsStatusModalData,
        createClusterWorkflowData: state.emrMetadataData.createClusterWorkflowData
    }
}

const mapDispatchToProps = dispatch => {
    return {
        fetchStepsStatusModalData: (name, record_id, fromWorkflow, token) => dispatch(fetchStepsStatusModalData(name, record_id, fromWorkflow, token)),
        fetchCreateClusterWorkflow: (record_id, token) => dispatch(fetchCreateClusterWorkflow(record_id, token)),
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CreateClusterWorkflow)
