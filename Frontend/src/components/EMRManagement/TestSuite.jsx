import React from 'react';
import { connect } from 'react-redux';
import {
    postTestSuiteRequest,
    fetchEMRTestSuites,
    fetchEMRTestSuitesStatus,
    fetchClusterHealthCheckHistory
} from '../../actions/emrManagement';
import { Tab, Tabs } from '@blueprintjs/core'
import { Grid, GridDetailRow, GridColumn as Column } from '@progress/kendo-react-grid';
import CustomAlert from './CustomAlert';

const test_status = [ 'NEW', 'INITIATED', 'INPROGRESS' ];
class DetailComponent extends GridDetailRow {
    render() {
        const dataItem = this.props.stepData.filter(x => x.executionId === this.props.dataItem.executionId);
        return (
            <section>
                <p><strong>Executed By:</strong> {dataItem[0].executedBy}</p>
                <p><strong>Execution Time:</strong> {dataItem[0].executionStartTime}</p>
                <p><strong>Remark:</strong> {dataItem[0].remark}</p>
            </section>
        )
    }
}

/**
 * Popup window showing the tests available for the given cluster.
 * Run and see the status of the tests in the workflow.
 * If required re-run the tests.
 */
class TestSuite extends React.Component {
    showAlert = true;
    firstTime = true;
    spinner = false;
    constructor(props) {
        super(props);
        this.state = {
            connectivity: true,
            noOfBootstraps: true,
            noOfDefaultSteps: true,
            userList: true,
            autoScaling: true,
            primaryTab: 'currTests',
            alert: true
        }

        this.createTestWorkflow = this.createTestWorkflow.bind(this);
        this.handleTestSelection = this.handleTestSelection.bind(this);
        this.submitTestsToRun = this.submitTestsToRun.bind(this);
        this.renderTabs = this.renderTabs.bind(this);
        this.handlePrimaryTabChange = this.handlePrimaryTabChange.bind(this);
        this.expandChange = this.expandChange.bind(this);
        this.overrideRequest = this.overrideRequest.bind(this);
        this.handleAlert = this.handleAlert.bind(this);
        this.handleClose = this.handleClose.bind(this);
    }

    render() {
        let msg = "Health checks on the cluster are currently pending. Do you want override the pending tests and submit a new request?"
        return (
            <div>
                <div className='popup'>
                    <div className='popup_inner'>
                        <div className='popup_close'>
                            <button className='close_popup' onClick={this.handleClose}>
                                <i className='material-icons'>keyboard_arrow_right</i>
                            </button>
                        </div>
                        <div>
                            <h3 style={{ margin: '20px' }}>Run Tests for <span style={{ color: '#0097E6'}}>{this.props.data.clusterName}</span></h3>
                            <div className='popup_components'>
                                <div className='test-options'>
                                    <span style={{ marginRight: '20px', fontSize: '14px', fontWeight: '600' }}>Tests:</span>
                                    {this.props.emrTestSuitesData !== undefined && this.props.emrTestSuitesData.length > 0 && this.props.emrTestSuitesData.filter(value => (value.testName === "Connectivity")).length > 0 &&
                                    <div className='test-option'><input type="checkbox" value="connectivity" checked={this.state.connectivity} disabled onChange={(e) =>this.handleTestSelection(e)} /><span style={{ marginLeft: '5px' }}>Connectivity</span></div>}

                                    {this.props.emrTestSuitesData !== undefined && this.props.emrTestSuitesData.length > 0 && this.props.emrTestSuitesData.filter(value => (value.testName === "NoOfBootstraps")).length > 0 &&
                                    <div className='test-option'><input type="checkbox" value="noOfBootstraps" checked={this.state.noOfBootstraps} disabled onChange={(e) =>this.handleTestSelection(e)} /><span style={{ marginLeft: '5px' }}>NoOfBootstraps</span></div>}

                                    {this.props.emrTestSuitesData !== undefined && this.props.emrTestSuitesData.length > 0 && this.props.emrTestSuitesData.filter(value => (value.testName === "NoOfDefaultSteps")).length > 0 &&
                                    <div className='test-option'><input type="checkbox" value="noOfDefaultSteps" checked={this.state.noOfDefaultSteps} disabled onChange={(e) =>this.handleTestSelection(e)} /><span style={{ marginLeft: '5px' }}>NoOfDefaultSteps</span></div>}

                                    {this.props.emrTestSuitesData !== undefined && this.props.emrTestSuitesData.length > 0 && this.props.emrTestSuitesData.filter(value => (value.testName === "UserList")).length > 0 &&
                                    <div className='test-option'><input type="checkbox" value="userList" checked={this.state.userList} disabled onChange={(e) =>this.handleTestSelection(e)} /><span style={{ marginLeft: '5px' }}>UserList</span></div>}

                                    {this.props.emrTestSuitesData !== undefined && this.props.emrTestSuitesData.length > 0 && this.props.emrTestSuitesData.filter(value => (value.testName === "AutoScaling")).length > 0 &&
                                    <div className='test-option'><input type="checkbox" value="autoScaling" checked={this.state.autoScaling} disabled onChange={(e) =>this.handleTestSelection(e)} /><span style={{ marginLeft: '5px' }}>AutoScaling</span></div>}
                                </div>

                                <button className='submitBtn' onClick={this.submitTestsToRun}>Run Tests</button>
                                <div className='tab-data'>
                                    <Tabs className='line' id="primary-tab" selectedTabId={this.state.primaryTab} onChange={this.handlePrimaryTabChange}>
                                        <Tab id="currTests" title="Currently Running Tests" />
                                        <Tab id="testHistory" title="Tests History" />
                                    </Tabs>
                                    {
                                        this.renderTabs(this.state.primaryTab)
                                    }
                                </div>
                            </div>
                        </div>
                        {this.firstTime ? null : this.state.alert && 
                        (this.props.runTestsStatusErrorData !== undefined && 
                        this.props.runTestsStatusErrorData.length > 0 && 
                        this.props.runTestsStatusErrorData === "Health checks on the cluster are currently pending") ?
                            <CustomAlert message={msg} onSubmit={this.overrideRequest} onCancel={this.handleAlert}/> : null
                        } 
                    </div>
                </div>
                {this.spinner && !(this.props.runTestsStatusErrorData !== undefined && 
                        this.props.runTestsStatusErrorData.length > 0 && 
                        this.props.runTestsStatusErrorData === "Health checks on the cluster are currently pending") ? <div className='loader-wrapper'>
                    <div className='loader' />
                </div> : null}
            </div>
        )
    }

    handleClose() {
        this.setState({
            ...this.state,
            alert: false
        })
        this.props.onClose();
    }

    handleAlert() {
        this.setState({
            ...this.state,
            alert: false
        })
    }

    overrideRequest() {
        let data = {
            "clusterId": this.props.data.clusterId,
            "clusterSegment": this.props.data.segment,
            "clusterType": this.props.data.type,
            "clusterName": this.props.data.clusterName,
            "overrideTimeout": true
        }
        this.props.postTestSuiteRequest(data, this.props.token);
        this.spinner = true;
        this.setState({
            ...this.state,
            alert: true
        })
    }


    expandChange = (event) => {
        event.dataItem.expanded = !event.dataItem.expanded;
        this.forceUpdate();
    }

    renderTabs(primaryTab) {
        switch(primaryTab) {
            case 'currTests':
                let testName = <span style={{ fontSize: '15px', fontWeight: '600' }}>Test Name</span>
                let testStatus = <span style={{ fontSize: '15px', fontWeight: '600' }}>Test Status</span>
                return (
                    <div>
                        {this.props.emrTestSuitesStatusData !== undefined && 
                        <Grid
                            data={this.props.emrTestSuitesStatusData}
                            detail={(props) => <DetailComponent {...props} stepData={this.props.emrTestSuitesStatusData} />}
                            expandField="expanded"
                            onExpandChange={this.expandChange}
                            resizable={true}
                            reorderable={true}
                            sortable={true}
                        >
                            <Column field="testName" title={testName} width="150px" />
                            <Column field="testStatus" title={testStatus} width="auto" cell={ (props) =>
                                <td>{this.createTestWorkflow(props.dataItem)}</td>
                            } />
                        </Grid> }
                    </div>
                    
                )
            case 'testHistory':
                let history_data = this.props.clusterHealthCheckHistoryData.filter((data, key) => {
                    if (!(this.props.emrTestSuitesStatusData.filter(x => x.executionId === data.executionId).length > 0)) {
                        return data;
                    }
                })
                return (
                    <div>
                        {this.props.clusterHealthCheckHistoryData !== undefined &&
                        <Grid
                            data={history_data}
                            resizable={true}
                            reorderable={true}
                            sortable={true}
                        >
                            <Column field="testName" title="Test Name" />
                            <Column field="status" title="Test Status" />
                            <Column field="executedBy" title="Executed By" />
                            <Column field="executionStartTime" title="Execution Start Time" />
                        </Grid> }
                    </div>
                )
            default: 
                return (
                    <div></div>
                )
        }
    }

    handlePrimaryTabChange(newTabId) {
        this.setState({
          ...this.state,
          primaryTab: newTabId
        })
      }

    handleTestSelection = (e) => {
        let val = e.target.value;
        if (val === "connectivity") {
            this.setState({
                ...this.state,
                connectivity: !this.state.connectivity
            })
        } else if (val === "noOfBootstraps") {
            this.setState({
                ...this.state,
                noOfBootstraps: !this.state.noOfBootstraps
            })
        } else if (val === "noOfDefaultSteps") {
            this.setState({
                ...this.state,
                noOfDefaultSteps: !this.state.noOfDefaultSteps
            })
        } else if (val === "userList") {
            this.setState({
                ...this.state,
                userList: !this.state.userList
            })
        } else if (val === "autoScaling") {
            this.setState({
                ...this.state,
                autoScaling: !this.state.autoScaling
            })
        }
    }

    submitTestsToRun = () => {
        let data = {
                "clusterId": this.props.data.clusterId,
                "clusterSegment": this.props.data.segment,
                "clusterType": this.props.data.type,
                "clusterName": this.props.data.clusterName
            }
        this.props.postTestSuiteRequest(data, this.props.token);
        this.firstTime = false
        this.spinner = true;
        this.setState({
            ...this.state,
            alert: true
        })
    }

    createTestWorkflow = data => {
        const completed_state = <i className='material-icons workflow'>check_circle</i>
        const green_line = <div className='workflow-line' />
        const incomplete_state = <i className='material-icons workflow gray'>album</i>
        const running_state = <i className='material-icons workflow blink'>album</i>
        const gray_line = <div className='workflow-line gray' />
        const failed_state = <i className='material-icons workflow red'>error</i>

        const new_label = <span className='wf-label new' new>NEW</span>
        const initiated_label = <span className='wf-label initiated'>INITIATED</span>
        const inprogress_label = <span className='wf-label inprogress'>IN PROGRESS</span>
        const failed_label = <span className='wf-label failed'>FAILED</span>
        const success_label = <span className='wf-label success'>SUCCESS</span>
        const result_label = <span className='wf-label result'>RESULT</span>

        let workflow_elements = [];
        let workflow_labels = []
 
        let status_found = false;
        let counter = 0;
        test_status.forEach(value => {
            if (!status_found) {
                workflow_elements.push(completed_state);
                workflow_elements.push(green_line);
                if (data.status === value) {
                    status_found = true;
                }
            } else {
                if (counter === 0) {
                    workflow_elements.push(running_state);
                } else {
                    workflow_elements.push(incomplete_state);
                }
                counter = counter + 1;
                workflow_elements.push(gray_line);
            }
            if (value === 'NEW') {
                workflow_labels.push(new_label);
            } else if (value === 'INITIATED') {
                workflow_labels.push(initiated_label);
            } else if (value === 'INPROGRESS') {
                workflow_labels.push(inprogress_label);
            }
        })

        if (status_found) {
            workflow_elements.push(incomplete_state);
            workflow_labels.push(result_label)
        } else {
            if (data.status === 'FAILED') {
                workflow_elements.push(failed_state);
                workflow_labels.push(failed_label);
            } else {
                workflow_elements.push(completed_state);
                workflow_labels.push(success_label);
            }
        }
        
        return (
            <div>
                <div className='workflow-component'>{workflow_elements}</div>
                <div className='workflow-component'>{workflow_labels}</div>
            </div>
        )
    }

    componentWillReceiveProps(nextProps) {
        if (this.props.emrTestSuitesStatusData !== nextProps.emrTestSuitesStatusData) {
            this.spinner = false
        }
    }

    componentDidMount() {
        this.props.fetchEMRTestSuites(this.props.data.type, this.props.data.segment, this.props.token)
        this.props.fetchClusterHealthCheckHistory(this.props.data.clusterId, this.props.token)
        if (this.props.from_workflow !== undefined && this.props.from_workflow) {
            this.props.fetchEMRTestSuitesStatus(this.props.data.clusterId, true,  this.props.token)
            this.intervalId = setInterval(() => this.props.fetchEMRTestSuitesStatus(this.props.data.clusterId, true, this.props.token), 30000)
        } else {
            this.props.fetchEMRTestSuitesStatus(this.props.data.clusterId, false, this.props.token)
            this.intervalId = setInterval(() => this.props.fetchEMRTestSuitesStatus(this.props.data.clusterId, false, this.props.token), 30000)
        }
        
    }

    componentWillUnmount() {
        this.setState({
            ...this.state,
            alert: false
        })
        clearInterval(this.intervalId);
    }
}

const mapStateToProps = state => {
    console.log(state)
    return {
        runTestsStatusSucess: state.emrMetadataData.runTestsStatusSucess,
        runTestsStatusError: state.emrMetadataData.runTestsStatusError,
        runTestsStatusErrorData: state.emrMetadataData.runTestsStatusErrorData,
        emrTestSuitesData: state.emrMetadataData.emrTestSuitesData,
        emrTestSuitesStatusData: state.emrMetadataData.emrTestSuitesStatusData,
        clusterHealthCheckHistoryData: state.emrMetadataData.clusterHealthCheckHistoryData
    }
}

const mapDispatchToProps = dispatch => {
    return {
        postTestSuiteRequest: (data, token) => dispatch(postTestSuiteRequest(data, token)),
        fetchEMRTestSuites: (type, segment, token) => dispatch(fetchEMRTestSuites(type, segment, token)),
        fetchEMRTestSuitesStatus: (cluster_id, from_workflow, token) => dispatch(fetchEMRTestSuitesStatus(cluster_id, from_workflow, token)),
        fetchClusterHealthCheckHistory: (cluster_id, token) => dispatch(fetchClusterHealthCheckHistory(cluster_id, token))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(TestSuite);