import React from 'react';
import './emrManagement.css';
import Fab from '@material-ui/core/Fab';
import DeleteIcon from '@material-ui/icons/Delete';
import AddIcon from '@material-ui/icons/Add';
import { Dialog, Classes } from '@blueprintjs/core';
import { TimePicker } from '@progress/kendo-react-dateinputs'
import { formatDate } from '@telerik/kendo-intl';
import { connect } from 'react-redux';
import { fetchClusterClone, fetchAccountJiraEnabled } from '../../actions/emrManagement'
import { checkJiraEnabled } from '../../utils/components/CheckJiraEnabled';
import { Typeahead } from 'react-bootstrap-typeahead';
import 'react-bootstrap-typeahead/css/Typeahead.css';
import { InstanceTypes } from './CreateClusterConstants';
import 'bootstrap/dist/css/bootstrap.min.css';
import { NumericTextBox } from '@progress/kendo-react-inputs';

/**
 * Combo dropdown for create cluster form.
 */
const ComboBox = ({ data, onChange, defaultValue }) => {
    if (!data || data.length === 0) {
        return <div />;
    }
    return ( 
        <div className="combobox-container">
                <select className="combobox-dropdown" onChange={onChange}>
                    {
                        data.map((v, i) => <option key={i} value={v} selected={defaultValue === v}>{v}</option>)
                    }
                </select>
        </div>
    );
};

var startTime = new Date();
startTime.setHours(0,0,0,0);
var endTime = new Date();
endTime.setHours(23,0,0,0);

/**
 * Create Cluster form component.
 */
class CreateCluster extends React.Component {
    instanceType = ['m4.large','m4.xlarge','m4.2xlarge','m4.4xlarge','m4.10xlarge','m4.16xlarge',
    'm5.xlarge','m5.2xlarge','m5.4xlarge','m5.12xlarge','m5.24xlarge','r4.xlarge','r4.2xlarge','r4.4xlarge','r4.8xlarge','r4.16xlarge',
    'r5.xlarge','r5.2xlarge','r5.4xlarge','r5.12xlarge','r5.24xlarge','c4.large','c4.xlarge','c4.2xlarge','c4.4xlarge','c4.8xlarge',
    'c5.large','c5.xlarge','c5.2xlarge','c5.4xlarge','c5.9xlarge', 'c5.18xlarge'];

    // instanceType = ['m5.xlarge','m5.2xlarge','m5.4xlarge','m5.12xlarge','m5.24xlarge',
    // 'r5.xlarge','r5.2xlarge','r5.4xlarge','r5.12xlarge','r5.24xlarge'];

    ebsVolSize = ['10','20','50','70','100','200','300','400','500','600','750','800','900','1000'];
    actionType = ['CANCEL_AND_WAIT','CONTINUE'];
    clusterType = ['exploratory','scheduled','transient'];
    clusterSubType = ['non-kerberos'];

    clusterSegment = this.props.uiListData.segments;
    accountId = this.props.uiListData.accounts;
    doTerminate = ['False','True'];
    do_autoRotate = ['False', 'True'];
    roleAccounts = {};
    selectedRole = '';
    instanceGrpList = [];

    constructor(props) {
      super(props)
      this.state = {
        clusterName: '',
        type: 'exploratory',
        subType: 'non-kerberos',
        clusterSegment: this.clusterSegment[0],
        account: this.accountId[0],
        masterInstanceType: ['m4.large'],
        coreInstanceCount: 0,
        coreInstanceType: ['m4.large'],
        taskInstanceCount: 0,
        taskInstanceType: ['m4.large'],
        coreEbsVolSize: '10',
        masterEbsVolSize: '10',
        taskEbsVolSize: '10',
        customAmiId: '',
        doTerminate: false,
        toggleStatusTerminate: false,
        headlessUsers: '',
        visible: false,
        steps: [],
        bootstrapActions: [],
        primaryButton: 'cluster',
        clusterBtnDisabled: false,
        hardwareBtnDisabled: true,
        configBtnDisabled: true,
        addBootstrapBtnDisabled: true,
        addStepBtnDisabled: true,
        reviewBtnDisabled: true,
        clusterEnabled: true,
        hardwareEnabled: false,
        configEnabled: false,
        addStepEnabled: false,
        reviewEnabled: false,
        coreInstanceCountError: false,
        taskInstanceCountError: false,
        amiIdError: false,
        headlessUsersError: false,
        stepNameError: false,
        jarLocationError: false,
        argsError: false,
        bootstrapNameError: false,
        bootstrapScriptError: false,
        addStepNextBtnDisabled: false,
        spinnerActive: true,
        stepErrors: [],
        bootstrapActionsErrors: [],
        isProd: false,
        showClusterName: true,
        servicenow: '',
        showConfirmation: false,
        autoAmiRotation: 'False',
        autoAMIRotation: false,
        toggleStatus: false,
        autopilotWindowStart: startTime,
        autopilotWindowEnd: endTime,
        autopilotWindowError: false,
        amiRotationSla: '30',
        autoScaling: false,
        autoScalingMin: 0,
        autoScalingMax: 0,
        instanceGroup: '',
        instanceMinCountError: false,
        instanceMaxCountError: false
      }

      this.handleSubmit = this.handleSubmit.bind(this);
      this.toggleDialog = this.toggleDialog.bind(this);
      this.handleclusterName = this.handleclusterName.bind(this);
      this.handleclusterType = this.handleclusterType.bind(this);
      this.handleclusterSubType = this.handleclusterSubType.bind(this);
      this.handleAccountID = this.handleAccountID.bind(this);
      this.handleclusterSegment = this.handleclusterSegment.bind(this);
      this.handlemasterNodeType = this.handlemasterNodeType.bind(this);
      this.handlecoreNodeCount = this.handlecoreNodeCount.bind(this);
      this.handlecoreNodeType = this.handlecoreNodeType.bind(this);
      this.handletaskNodeCount = this.handletaskNodeCount.bind(this);
      this.handletaskNodeType = this.handletaskNodeType.bind(this);
      this.handlemasterVolSize = this.handlemasterVolSize.bind(this);
      this.handlecoreVolSize = this.handlecoreVolSize.bind(this);
      this.handletaskVolSize = this.handletaskVolSize.bind(this);
      this.handleamiID = this.handleamiID.bind(this);
      this.handledoTerminate = this.handledoTerminate.bind(this);
      this.handleheadlessUsers = this.handleheadlessUsers.bind(this);
      this.handlename = this.handlename.bind(this);
      this.handlemainClass = this.handlemainClass.bind(this);
      this.handlejarLocation = this.handlejarLocation.bind(this);
      this.handlearguments = this.handlearguments.bind(this);
      this.handleactiononFailure = this.handleactiononFailure.bind(this);
      this.handleAddStep = this.handleAddStep.bind(this);
      this.handleRemoveStep = this.handleRemoveStep.bind(this);
      this.formSubmit = this.formSubmit.bind(this);
      this.handleFormChange = this.handleFormChange.bind(this);
      this.handleNext = this.handleNext.bind(this);
      this.handleRoleShortlist = this.handleRoleShortlist.bind(this);
      this.handleIsProd = this.handleIsProd.bind(this);
      this.handleConfirmation = this.handleConfirmation.bind(this);
      this.handleToggle = this.handleToggle.bind(this);
      this.handleTimeChange = this.handleTimeChange.bind(this);
      this.handleAMIRotationSLA = this.handleAMIRotationSLA.bind(this);
      this.handleAutoScaling = this.handleAutoScaling.bind(this);
      this.handleInstanceGroup = this.handleInstanceGroup.bind(this);
      this.handleAutoScalingMin = this.handleAutoScalingMin.bind(this);
      this.handleAutoScalingMax = this.handleAutoScalingMax.bind(this);
    }

    componentDidMount() {
        this.handleRoleShortlist();
        if (this.props.clusterId !== undefined && this.props.clusterId.length > 0) {
            this.props.fetchClusterClone(this.props.clusterId, this.props.token)
        }
    }

    /**
     * Check whether reached here directly or from clone cluster.
     * If arrived from clone cluster, then copy the values of the cluster to be cloned into the fields in this form.
     */
    componentDidUpdate(prevProps) {
        if (!prevProps.isOpen && this.props.isOpen) {
          this.setState({
              visible: true,
          })
        }
        if (prevProps.clusterCloneData !== this.props.clusterCloneData && this.props.clusterCloneData.clusterName !== undefined) {
            let shour = new Date()
            shour.setHours(this.props.clusterCloneData.autopilotWindowStart)
            let ehour = new Date()
            ehour.setHours(this.props.clusterCloneData.autopilotWindowEnd)
            if (this.props.clusterCloneData.coreInstanceCount > 0) this.instanceGrpList.push('CORE');
            if (this.props.clusterCloneData.taskInstanceCount > 0) this.instanceGrpList.push('TASK');
            let bootstrapData = [];
            let bootstrapDataError = []
            if (this.props.clusterCloneData.bootstrapActions !== undefined && 
                this.props.clusterCloneData.bootstrapActions.length > 0) {
                    this.props.clusterCloneData.bootstrapActions.map((action, index) => {
                        let newBootstrap = {
                            bootstrapName: action.bootstrapName,
                            bootstrapScript: action.bootstrapScript
                        }
                        let newBootstrapError = {
                            bootstrapName: false,
                            bootstrapScript: false
                        }
                        bootstrapData.push(newBootstrap);
                        bootstrapDataError.push(newBootstrapError);
                    })
                }
            this.setState({
                ...this.state,
                type: this.props.clusterCloneData.type,
                clusterSegment: this.props.clusterCloneData.segment,
                account: this.props.clusterCloneData.account,
                isProd: this.props.clusterCloneData.isProd,
                masterInstanceType: [this.props.clusterCloneData.masterInstanceType],
                coreInstanceType: this.props.clusterCloneData.coreInstanceType !== undefined ? [this.props.clusterCloneData.coreInstanceType] : ['m5.xlarge'],
                coreInstanceCount: this.props.clusterCloneData.coreInstanceCount !== undefined ? this.props.clusterCloneData.coreInstanceCount : '0',
                taskInstanceCount: this.props.clusterCloneData.taskInstanceCount !== undefined ? this.props.clusterCloneData.taskInstanceCount : '0',
                taskInstanceType: this.props.clusterCloneData.taskInstanceType !== undefined ? [this.props.clusterCloneData.taskInstanceType] : ['m5.xlarge'],
                masterEbsVolSize: this.props.clusterCloneData.masterEbsVolSize,
                coreEbsVolSize: this.props.clusterCloneData.coreEbsVolSize,
                taskEbsVolSize: this.props.clusterCloneData.taskEbsVolSize,
                doTerminate: this.props.clusterCloneData.doTerminate,
                toggleStatusTerminate: this.props.clusterCloneData.doTerminate,
                toggleStatus: this.props.clusterCloneData.autoAmiRotation,
                autoAmiRotation: this.props.clusterCloneData.autoAmiRotation,
                autopilotWindowStart: shour,
                autopilotWindowEnd: ehour,
                subType: this.props.clusterCloneData.subType,
                amiRotationSla: this.props.clusterCloneData.amiRotationSlaDays,
                autoScaling: this.props.clusterCloneData.instanceGroup !== undefined && (this.props.clusterCloneData.instanceGroup === 'CORE' || this.props.clusterCloneData.instanceGroup === 'TASK'),
                autoScalingMin: this.props.clusterCloneData.min,
                autoScalingMax: this.props.clusterCloneData.max,
                instanceGroup: this.props.clusterCloneData.instanceGroup !== undefined ? this.props.clusterCloneData.instanceGroup : '',
                bootstrapActions: bootstrapData,
                bootstrapActionsErrors: bootstrapDataError
            })
        }
      }

    render() {
        return (
                <div>
                    <form className="k-form">
                        <div>
                            <button disabled={this.state.clusterBtnDisabled} className={`breadcrumbBtn ${this.state.clusterBtnDisabled ? 'disabledBtn' : ''} ${this.state.primaryButton == 'cluster' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'cluster')}>1. Cluster Details ></button>
                            <button disabled={this.state.hardwareBtnDisabled ||this.state.clusterName.length === 0 } className={`breadcrumbBtn ${this.state.hardwareBtnDisabled ? 'disabledBtn' : ''} ${this.state.primaryButton == 'hardware' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'hardware')}>2. Hardware ></button>
                            <button disabled={this.state.configBtnDisabled || this.state.clusterName.length === 0 || this.hardwareErrors()} className={`breadcrumbBtn ${this.state.configBtnDisabled ? 'disabledBtn' : ''} ${this.state.primaryButton == 'config' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'config')}>3. Config ></button>
                            <button disabled={this.state.addBootstrapBtnDisabled || this.state.clusterName.length === 0 || this.hardwareErrors()} className={`breadcrumbBtn ${this.state.addStepBtnDisabled ? 'disabledBtn' : ''} ${this.state.primaryButton == 'addBootstrap' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'addBootstrap')}>4. Add Bootstrap Actions ></button>
                            <button disabled={this.state.addStepBtnDisabled || this.state.clusterName.length === 0 || this.state.coreInstanceCountError || this.hardwareErrors()} className={`breadcrumbBtn ${this.state.addStepBtnDisabled ? 'disabledBtn' : ''} ${this.state.primaryButton == 'addStep' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'addStep')}>5. Add Step ></button>
                            <button disabled={this.state.reviewBtnDisabled || this.state.clusterName.length === 0 || this.state.stepNameError || this.state.jarLocationError || this.state.argsError || this.hardwareErrors()} className={`breadcrumbBtn ${this.state.reviewBtnDisabled ? 'disabledBtn' : ''} ${this.state.primaryButton == 'review' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'review')}>6. Review</button>
                        </div>
                        <div className='formStyling'>
                            <hr />
                            {this.renderForm(this.state.primaryButton)}
                        </div>
                        </form>
                </div>
        );
    }

    hardwareErrors = () => {
        return this.state.coreInstanceCountError || 
        this.state.taskInstanceCountError || 
        (this.state.autoScaling && 
        (this.state.instanceMinCountError || 
        this.state.instanceMaxCountError)) ||
        (this.state.coreInstanceCount == 0 &&
        this.state.taskInstanceCount > 0)
    }

    handleRoleShortlist() {
        if (this.props.superAdmin) return;
        let dccServices = this.props.dccRoles;
        let emrRoles = dccServices[0];
        let roleSegments = [];
        emrRoles.roles.forEach((value) => {
            if (value.name === 'createcluster') {
                roleSegments = value.segments;
            }
        });
        let roles = [];
        roleSegments.forEach(value => {
            let role = value.segmentName;
            let acts = [];
            roles.push(role);
            value.accounts.forEach(act => {
                acts.push(act.accountId);
            })
            this.roleAccounts[role] = acts;
        })
        this.clusterSegment = roles;
        this.selectedRole = roles[0];
        this.setState({
            ...this.state,
            clusterSegment: roles.length > 0 ? roles[0] : '',
            account: roles.length > 0 ? this.roleAccounts[roles[0]][0] : ''
        })
    }

    handleFormChange(e, stepName) {
        e.preventDefault();
        this.setState({
            ...this.state,
            primaryButton: stepName
        })
    }

    handleStepValidation(e) {
        e.preventDefault();
        for (var i = 0; i < this.state.steps.length; i++) {
            if (this.state.steps[i].name.length === 0 || this.state.steps[i].jar.length === 0 || this.state.steps[i].args.length === 0)
                return false;
        }
        return true
    }

    /**
     * Handle shifting between tabs in the form.
     * A tab is enabled only if the previous tab is active and there are no errors in the form in the earlier fields.
     */
    handleNext(e) {
        e.preventDefault();
        switch(this.state.primaryButton) {
            case 'cluster':
                this.setState({
                    ...this.state,
                    hardwareBtnDisabled: false,
                    primaryButton: 'hardware'
                })
                break;
            case 'hardware':
                this.setState({
                    ...this.state,
                    configBtnDisabled: false,
                    primaryButton: 'config'
                })
                break;
            case 'config':
                this.setState({
                    ...this.state,
                    addBootstrapBtnDisabled: false,
                    primaryButton: 'addBootstrap'
                })
                break;
            case 'addBootstrap':
                this.setState({
                    ...this.state,
                    addStepBtnDisabled: false,
                    primaryButton: 'addStep'
                })
                break;
            case 'addStep':
                this.setState({
                        ...this.state,
                        primaryButton: 'review',
                        reviewBtnDisabled: false
                    })
        }
    }

    /**
     * Handle shifting between tabs in the form.
     */
    handleBack(e) {
        e.preventDefault();
        switch(this.state.primaryButton) {
            case 'review':
                this.setState({
                    ...this.state,
                    primaryButton: 'addStep'
                })
                break;
            case 'hardware':
                this.setState({
                    ...this.state,
                    primaryButton: 'cluster'
                })
                break;
            case 'config':
                this.setState({
                    ...this.state,
                    primaryButton: 'hardware'
                })
                break;
            case 'addBootstrap':
                this.setState({
                    ...this.state,
                    primaryButton: 'config'
                })
                break;
            case 'addStep':
                this.setState({
                    ...this.state,
                    primaryButton: 'addBootstrap'
                })
        }
    }

    renderForm(stepName) {
        switch(stepName) {
            case 'cluster':
                return (
                    <div className='clusterField'>
                        <div>
                            <label className="formField">
                                <span className="required" className='labelField'>Cluster Type:</span>
                                <i className='m-icons'>help</i>
                                <ComboBox className='comboField' data={this.clusterType} defaultValue={this.state.type} onChange={this.handleclusterType} />
                                <div className='help-content'> Type of the cluster to be created: Exploratory (adhoc jobs), Scehduled (scheduled production jobs) or Transient (non-persistent cluster). </div>
                            </label>
                            <label className="formField">
                                <span className='labelField'>Cluster Sub Type:</span>
                                <i className='m-icons'>help</i>
                                <ComboBox className='comboField' data={this.clusterSubType} defaultValue={this.state.subType} onChange={this.handleclusterSubType} />
                                <div className='help-content1'> Sub type of the cluster: Kerberos or Non-kerberos. </div>
                            </label>
                            <label className="formField">
                                <span className="required" className='labelField'>Cluster Segment:</span>
                                <i className='m-icons'>help</i>
                                <ComboBox className='comboField' data={this.clusterSegment} defaultValue={this.state.clusterSegment} onChange={this.handleclusterSegment} />
                                <div className='help-content'> Based on the group the cluster is going to be used by, roles are defined accordingly. </div>
                            </label>
                        </div>
                        <label className="formField">
                            <span className="required" className='labelField'>Account ID:</span>
                            <i className='m-icons'>help</i>
                            <ComboBox className='comboField' data={this.props.superAdmin ? this.accountId : this.roleAccounts[this.selectedRole]} defaultValue={this.state.account} onChange={this.handleAccountID} />
                            <div className='help-content'>Select one of the two AWS production accounts. </div>
                        </label>
                        <label className="formField" >
                            <span>Is Production Cluster:</span>
                            <i className='m-icons'>help</i>
                            <div className="create-cluster-toggle">
                                <input
                                className="react-switch-checkbox"
                                id={this.state.account + '_terminate_' + this.state.clusterType}
                                type="checkbox"
                                onChange={this.handleIsProd}
                                checked={this.state.isProd}
                                />
                                <label
                                    style={{ background: this.state.isProd ? '#53b700' : '#ff0100' }}
                                    className="react-switch-label"
                                    htmlFor={this.state.account + '_terminate_' + this.state.clusterType}
                                >
                                <span className={`react-switch-button`} />
                                    {this.state.isProd ? <p className="create-toggle-on">Yes</p> : <p className="create-toggle-off">No</p>}
                                </label>
                            </div>
                            <div className='help-content3'>Select if it this is a production cluster.</div>
                        </label>
                        <label className='formField'>
                            <span className="required">Cluster Name:</span>
                            <i className='m-icons'>help</i>
                            <input ref="name" className="textField" placeholder="Final Name will be 'type-role-clustername'" value={this.state.clusterName} onChange={this.handleclusterName} />
                            <span className='finalName'>{this.state.type}-{this.state.clusterSegment.toLowerCase()}-{this.state.clusterName}</span>
                            <div>{this.state.clusterName.length === 0 ? <span className='errorField'>This is a required field</span> : null }</div>
                            <div className='help-content'>Name of the cluster to be created. This name will be prepended by the cluster type and role. Eg, if the cluster name provided here is 'test', cluster type is 'exploratory' and cluster role is 'testing', then the final name will be 'exploratory-testing-test'. </div>
                        </label>
                        <button disabled={this.state.showClusterName && this.state.clusterName.length === 0} className='nextBtn' onClick={(e) => this.handleNext(e)}>Next</button>
                    </div>
                )
            case 'hardware':
                return (
                    <div className='hardware'>
                            <label className='formField'>
                                <span>Master Node Instance Type:</span>
                                <i className='m-icons'>help</i>
                                <div className='combobox-container'>
                                    <Typeahead
                                        options={InstanceTypes}
                                        onChange={selected => this.handlemasterNodeType(selected)}
                                        selected={this.state.masterInstanceType}
                                    />
                                </div>
                                <div className='help-content2'>EC2 instance type for Master Node. Default is M5.xlarge.</div>
                            </label>
                            <label className='formField'>
                                <span className="required">Core Instance Count:</span>
                                <i className='m-icons'>help</i>
                                <div className="numericField"><NumericTextBox placeholder="Number of Core Instances (Required)" value={this.state.coreInstanceCount} onChange={this.handlecoreNodeCount} /></div>
                                <div>{this.state.coreInstanceCountError && <span className='errorField'>This is a required field</span>}</div>
                                <div>{this.state.taskInstanceCount > 0 && this.state.coreInstanceCount == 0 ? <span className='errorField'>Cannot be 0 if Task Instance Count is greater than 0.</span> : null }</div>
                                <div className='help-content2'>Number of Core Node instances.</div>
                            </label>
                            {this.state.coreInstanceCount > 0 && <label className='formField'>
                                <span>Core Node Instance Type:</span>
                                <i className='m-icons'>help</i>
                                <div className='combobox-container'>
                                    <Typeahead
                                        options={InstanceTypes}
                                        onChange={selected => this.handlecoreNodeType(selected)}
                                        selected={this.state.coreInstanceType}
                                    />
                                </div>
                                <div className='help-content2'>EC2 instance type for Core Node. Default is M5.xlarge.</div>
                            </label>}
                            <label className='formField'>
                                <span className="required">Task Instance Count:</span>
                                <i className='m-icons'>help</i>
                                <div className="numericField"><NumericTextBox className="textFieldHardware" placeholder="Number of Task Instances (Required)" value={this.state.taskInstanceCount} onChange={this.handletaskNodeCount} /></div>
                                <div>{this.state.taskInstanceCountError ? <span className='errorField'>This is a required field</span> : null }</div>
                                <div className='help-content2'>Number of Task Node instances.</div>
                            </label>
                            {this.state.taskInstanceCount > 0 && <label className='formField'>
                                <span>Task Node Instance Type:</span>
                                <i className='m-icons'>help</i>
                                <div className='combobox-container'>
                                    <Typeahead
                                        options={InstanceTypes}
                                        onChange={selected => this.handletaskNodeType(selected)}
                                        selected={this.state.taskInstanceType}
                                    />
                                </div>
                                <div className='help-content2'>EC2 insrance type for Task Node. Default is M5.xlarge.</div>
                            </label>}
                            {(this.state.coreInstanceCount > 0 || this.state.taskInstanceCount > 0) && <label className="formField" >
                                <span>Attach Auto-Scaling:</span>
                                <i className='m-icons'>help</i>
                                <div className="create-cluster-toggle">
                                    <input
                                    className="react-switch-checkbox"
                                    id={this.state.account + '_autoscaling_' + this.state.clusterType}
                                    type="checkbox"
                                    onChange={this.handleAutoScaling}
                                    checked={this.state.autoScaling}
                                    />
                                    <label
                                        style={{ background: this.state.autoScaling ? '#53b700' : '#ff0100' }}
                                        className="react-switch-label"
                                        htmlFor={this.state.account + '_autoscaling_' + this.state.clusterType}
                                    >
                                    <span className={`react-switch-button`} />
                                        {this.state.autoScaling ? <p className="create-toggle-on">Yes</p> : <p className="create-toggle-off">No</p>}
                                    </label>
                                </div>
                                <div className='help-content3'>Toggle button to attach auto-scaling to EMR.</div>
                            </label>}
                            {(this.state.coreInstanceCount > 0 || this.state.taskInstanceCount > 0) && this.state.autoScaling && 
                            <div>
                            <label className='formField'>
                                <span>Instance Group:</span>
                                <i className='m-icons'>help</i>
                                <ComboBox className='comboField' data={this.instanceGrpList} onChange={this.handleInstanceGroup} />
                                <div className='help-content2'>The instances to which the auto-scaling will be attached to: CORE or TASK.</div>
                            </label>
                            <label className='formField'>
                                <span className="required">Instance Min Count:</span>
                                <i className='m-icons'>help</i>
                                <input className="textFieldHardware" defaultValue="0" placeholder="Minimum Instance Count for Auto-Scaling (Required)" value={this.state.autoScalingMin} onChange={this.handleAutoScalingMin} />
                                <div>{this.state.instanceMinCountError ? <span className='errorField'>This is a required field</span> : null }</div>
                                <div className='help-content2'>Minimum Instance Count for Auto-Scaling.</div>
                            </label>
                            <label className='formField'>
                                <span className="required">Instance Max Count:</span>
                                <i className='m-icons'>help</i>
                                <input className="textFieldHardware" defaultValue="0" placeholder="Maximum Instance Count for Auto-Scaling (Required)" value={this.state.autoScalingMax} onChange={this.handleAutoScalingMax} />
                                <div>{this.state.instanceMaxCountError ? <span className='errorField'>This is a required field</span> : null }</div>
                                <div className='help-content2'>Maximum Instance Count for Auto-Scaling.</div>
                            </label>
                            </div>
                            }
                            <button disabled={this.hardwareErrors()} className='nextBtn' onClick={(e) => this.handleNext(e)}>Next</button>
                            <button className='nextBtn' onClick={(e) => this.handleBack(e)}>Back</button>
                        </div>
                )
            case 'config':
                return (
                    <div className='config'>
                        <label className="formField">
                            <span>Master Node EBS Volume Size (in GB):</span>
                            <i className='m-icons'>help</i>
                            <ComboBox data={this.ebsVolSize} defaultValue={this.state.masterEbsVolSize} onChange={this.handlemasterVolSize} />
                            <div className='help-content3'>EBS Volume size for Master Node in Gigabytes. Default is 10 GB.</div>
                        </label>
                        <label className="formField">
                            <span>Core Node EBS Volume Size (in GB):</span>
                            <i className='m-icons'>help</i>
                            <ComboBox data={this.ebsVolSize} defaultValue={this.state.coreEbsVolSize} onChange={this.handlecoreVolSize} />
                            <div className='help-content3'>EBS Volume size per instance for Core Node in Gigbytes. Default is 10 GB. If Core Instance Count is 50, the total volume size is 500.</div>
                        </label>
                            <label className="formField">
                            <span>Task Node EBS Volume Size (in GB):</span>
                            <i className='m-icons'>help</i>
                            <ComboBox data={this.ebsVolSize} defaultValue={this.state.taskEbsVolSize} onChange={this.handletaskVolSize} />
                            <div className='help-content3'>EBS volume size for Task Node in Gigabytes.</div>
                        </label>
                        <label className="formField">
                            <span>AMI ID:</span>
                            <i className='m-icons'>help</i>
                            <input className="textFieldHardware" placeholder="Enter AMI ID (Optional)" value={this.state.customAmiId} onChange={this.handleamiID} />
                            <div className='help-content'>EC2 AMI ID. If blank, the latest AMI ID will be used.</div>
                        </label>
                        <label className="formField" >
                            <span>Terminate Cluster after Completion:</span>
                            <i className='m-icons'>help</i>
                            <div className="create-cluster-toggle">
                                <input
                                className="react-switch-checkbox"
                                id={this.state.account + '_terminate_' + this.state.clusterType}
                                type="checkbox"
                                onChange={() => this.handleToggle('terminateCluster')}
                                checked={this.state.toggleStatusTerminate}
                                />
                                <label
                                    style={{ background: this.state.toggleStatusTerminate ? '#53b700' : '#ff0100', width: '62px' }}
                                    className="react-switch-label"
                                    htmlFor={this.state.account + '_terminate_' + this.state.clusterType}
                                >
                                <span className={`react-switch-button`} />
                                    {this.state.toggleStatusTerminate ? <p className="create-toggle-on">ON</p> : <p className="create-toggle-off">OFF</p>}
                                </label>
                            </div>
                            <div className='help-content3'>Non-persistent if true, i.e. the cluster will be terminated automatically after creation.</div>
                        </label>
                        <label className="formField" >
                            <span>Auto-Pilot for Rotation AMI:</span>
                            <i className='m-icons'>help</i>
                            <div className="create-cluster-toggle">
                                <input
                                className="react-switch-checkbox"
                                id={this.state.account + '_autorotate_' + this.state.clusterType}
                                type="checkbox"
                                onChange={() => this.handleToggle('autoPilot')}
                                checked={this.state.toggleStatus}
                            />
                                <label
                                    style={{ background: this.state.toggleStatus ? '#53b700' : '#ff0100', width: '62px' }}
                                    className="react-switch-label"
                                    htmlFor={this.state.account + '_autorotate_' + this.state.clusterType}
                                >
                                <span className={`react-switch-button`} />
                                    {this.state.toggleStatus ? <p className="create-toggle-on">ON</p> : <p className="create-toggle-off">OFF</p>}
                                </label>
                            </div>
                            <div className='help-content3'>Auto-Pilot mode rotates the cluster every 30 days.</div>
                        </label>
                        {this.state.toggleStatus &&
                        <label className="formField">
                            <span>Auto-Pilot Window:</span>
                            <i className='m-icons'>help</i>
                            <div>{this.state.autopilotWindowError && <span className='errorField'>End Hour cannot be smaller than Start Hour</span> }</div>
                            <div className='timeField'>
                                <span style={{ position: 'relative', top: '10px' }}>From</span>
                                <TimePicker
                                    className='customTimePicker'
                                    onChange={(e) => this.handleTimeChange(e, "start")}
                                    format={"HH"}
                                    value={this.state.autopilotWindowStart}
                                />
                                <span style={{ position: 'relative', top: '10px', marginLeft: '20px' }}>To</span>
                                <TimePicker
                                    className='customTimePicker'
                                    onChange={(e) => this.handleTimeChange(e, "end")}
                                    format={"HH"}
                                    value={this.state.autopilotWindowEnd}
                                />
                            </div>
                            <div className='help-content'>Window in day to run the auto-pilot ami rotation. Provide value between 00 and 24 hours.</div>
                        </label>
                        }
                        <label className="formField">
                            <span>AMI Rotation Custom SLA: </span>
                            <i className='m-icons'>help</i>
                            <input className="textFieldHardware" defaultValue="30" placeholder="Custom SLA for AMI Rotation" value={this.state.amiRotationSla} onChange={this.handleAMIRotationSLA} />
                            <div className='help-content2'>Number of SLA days for AMI Rotation. Default is 30 days.</div>
                        </label>
                        <label className="formField" >
                            <span>Headless Users:</span>
                            <i className='m-icons'>help</i>
                            <input className="textFieldHardware" placeholder="Comma Separated List for headless users. Required for Transient Clusters." value={this.state.headlessUsers} onChange={this.handleheadlessUsers} />
                            <div>{this.state.headlessUsersError ? <span className='errorField'>This is a required field for Transient Clusters</span> : null }</div>
                            <div className='help-content1'>Required for Transient. Creation of headless users to be associated with the cluster.</div>
                        </label>
                        <button disabled={this.state.autopilotWindowError} className='nextBtn' onClick={(e) => this.handleNext(e)}>Next</button>
                        <button className='nextBtn' onClick={(e) => this.handleBack(e)}>Back</button>
                    </div>
                )
            case 'addBootstrap':
                return (
                    <div className='addStep'>
                        <h2>Add Bootstrap Actions</h2>
                        {this.state.bootstrapActions.map((values, index) => (
                            <div key={index} style={{marginTop: '10px'}}>
                                <h4>Action #{index + 1}</h4>
                                <label className="formField">
                                    <span className="required">Name:</span>
                                    <i className="m-icons">help</i>
                                    <input className="textField" placeholder="Action Name (Required)" value={this.state.bootstrapActions[index].bootstrapName} onChange={e => this.handleBootstrapName(e, index)} />
                                    <div>{this.state.bootstrapActionsErrors[index].bootstrapName ? <span className='errorField'>This is a required field</span> : null }</div>
                                    <div className='help-content'>Name of the bootstrap action.</div>
                                </label>
                                <label className="formField">
                                    <span className="required" >Script:</span>
                                    <i className='m-icons'>help</i>
                                    <input className="textField" placeholder="Eg: s3://my_dir/run_my_script.sh param1" value={this.state.bootstrapActions[index].bootstrapScript} onChange={e => this.handleBootstrapScript(e, index)} />
                                    <div>{this.state.bootstrapActionsErrors[index].bootstrapScript ? <span className='errorField'>This is a required field</span> : null }</div>
                                    <div className='help-content'>Script to be run in the bootstrap action.</div>
                                </label>
                            </div>
                        ))}

                        <Fab color='primary' onClick={this.handleAddBootstrapAction}>
                            <AddIcon />
                        </Fab>
                        {this.state.bootstrapActions.length > 0 &&
                            <Fab style={{ marginLeft: '20px' }} color='secondary' onClick={this.handleRemoveBootstrapAction}>
                                <DeleteIcon />
                            </Fab>
                        }
                        <button className='nextBtn' disabled={this.state.bootstrapNameError || this.state.bootstrapScriptError} onClick={(e) => this.handleNext(e)}>Next</button>
                        <button className='nextBtn' onClick={(e) => this.handleBack(e)}>Back</button>
                    </div>
                )
            case 'addStep':
                return (
                    <div className='addStep'>
                        <h2>Add Steps</h2>
                        {this.state.steps.map((values, index) => (
                            <div key={index} style={{marginTop: '10px'}}>
                            <h4>Step #{index + 1}</h4>
                            <label className="formField" >
                                    <span className="required" >Name:</span>
                                    <i className='m-icons'>help</i>
                                    <input className="textField" placeholder="Step Name (Required)" value={this.state.steps[index].name} onChange={e => this.handlename(e, index)} />
                                    <div>{this.state.stepErrors[index].name ? <span className='errorField'>This is a required field</span> : null }</div>
                                    <div className='help-content'>Name to be given for the step.</div>
                                </label>
                                <label className="formField" >
                                    <span className="required" >Action on Failure:</span>
                                    <i className='m-icons'>help</i>
                                    <ComboBox className="dropdown-content" data={this.actionType} defaultValue={this.state.steps[index].actionOnFailure} onChange={e => this.handleactiononFailure(e, index)} />
                                    <div className='help-content1'>What action to take if the step fails.</div>
                                </label>
                                <label className="formField" >
                                    <span className="required" >Arguments:</span>
                                    <i className='m-icons'>help</i>
                                    <input className="textField" placeholder="Eg: s3://idl-sched-uw2-processing-sbgayt-prd/artifacts/emr/emr-1.8.2/scripts/resources/processing-sbgayt-prd/kerberos/adhoc_create_hdp_home_dirs.sh av1" value={this.state.steps[index].args} onChange={e => this.handlearguments(e, index)} />
                                    <div>{this.state.stepErrors[index].args ? <span className='errorField'>This is a required field</span> : null }</div>
                                    <div className='help-content'>Script to be run in the step.</div>
                                </label>
                                <label className="formField" >
                                    <span className="required" >JAR location:</span>
                                    <i className='m-icons'>help</i>
                                    <input className="textField" defaultValue="s3://us-west-2.elasticmapreduce/libs/script-runner/script-runner.jar" value={this.state.steps[index].jar} onChange={e => this.handlejarLocation(e, index)} />
                                    <div>{this.state.stepErrors[index].jar ? <span className='errorField'>This is a required field</span> : null }</div>
                                    <div className='help-content'>Location of the jar that the EMR will use to run the script.</div>
                                </label>
                                <label className="formField" >
                                    <span>Main Class:</span>
                                    <i className='m-icons'>help</i>
                                    <input className="textField" placeholder="Main Class (Optional)" onChange={e => this.handlemainClass(e, index)}/>
                                    <div className='help-content'>Optional field.</div>
                                </label>
                            </div>
                            ))}

                            <Fab color='primary' onClick={this.handleAddStep}>
                                <AddIcon />
                            </Fab>
                            {this.state.steps.length > 0 &&
                                <Fab style={{ marginLeft: '20px' }} color='secondary' onClick={this.handleRemoveStep}>
                                    <DeleteIcon />
                                </Fab>
                            }
                            <button className='nextBtn' disabled={this.state.stepNameError || this.state.jarLocationError || this.state.argsError} onClick={(e) => this.handleNext(e)}>Next</button>
                            <button className='nextBtn' onClick={(e) => this.handleBack(e)}>Back</button>
                        </div>
                )
            case 'review':
                return (
                    <div className='review'>
                        <hr/>
                        <div className='reviewTitle'>Cluster Details</div>
                        <div className='reviewCluster'>
                            <div className='grey'><span className='reviewTitles'>Cluster Name: </span><span className='reviewValue'>{this.state.clusterName}</span></div>
                            <div className='grey'><span className='reviewTitles'>Cluster Type: </span><span className='reviewValue'>{this.state.type}</span></div>
                            <div className='grey'><span className='reviewTitles'>Cluster Sub type: </span><span className='reviewValue'>{this.state.subType}</span></div>
                            <div className='grey'><span className='reviewTitles'>Cluster Role: </span><span className='reviewValue'>{this.state.clusterSegment}</span></div>
                            <div className='grey'><span className='reviewTitles'>Account ID: </span><span className='reviewValue'>{this.state.account}</span></div>
                            <div className='grey'><span className='reviewTitles'>Is Production Cluster </span><span className='reviewValue'>{`${this.state.isProd}` + ' '}</span></div>
                        </div>
                        <div className='reviewTitle'>Hardware</div>
                        <div className='reviewCluster'>
                            <div className='grey'><span className='reviewTitles'>Master Node Instance Type: </span><span className='reviewValue'>{this.state.masterInstanceType}</span></div>
                            <div className='grey'><span className='reviewTitles'>Core Instance Count: </span><span className='reviewValue'>{this.state.coreInstanceCount}</span></div>
                            {this.state.coreInstanceCount > 0 &&<div className='grey'><span className='reviewTitles'>Core Node Instance Type: </span><span className='reviewValue'>{this.state.coreInstanceType}</span></div>}
                            <div className='grey'><span className='reviewTitles'>Task Instance Count: </span><span className='reviewValue'>{this.state.taskInstanceCount}</span></div>
                            {this.state.taskInstanceCount > 0 && <div className='grey'><span className='reviewTitles'>Task Node Instance Type: </span><span className='reviewValue'>{this.state.taskInstanceType}</span></div>}
                            {(this.state.coreInstanceCount > 0 || this.state.taskInstanceCount > 0) && <div className='grey'><span className='reviewTitles'>Auto-Scaling: </span><span className='reviewValue'>{`${this.state.autoScaling}` + ' '}</span></div>}
                            {this.state.autoScaling && <div><div className='grey'><span className='reviewTitles'>Instance Group: </span><span className='reviewValue'>{this.state.instanceGroup}</span></div>
                            {this.state.autoScaling && <div className='grey'><span className='reviewTitles'>Instance Min Count: </span><span className='reviewValue'>{this.state.autoScalingMin}</span></div>}
                            {this.state.autoScaling && <div className='grey'><span className='reviewTitles'>Instance Max Count: </span><span className='reviewValue'>{this.state.autoScalingMax}</span></div>}
                            </div> }
                        </div>
                        <div className='reviewTitle'>Config</div>
                        <div className='reviewCluster'>
                            <div className='grey'><span className='reviewTitles'>Master Node EBS Volume Size: </span><span className='reviewValue'>{this.state.masterEbsVolSize}</span></div>
                            <div className='grey'><span className='reviewTitles'>Core Node ENS Volume Size: </span><span className='reviewValue'>{this.state.coreEbsVolSize}</span></div>
                            <div className='grey'><span className='reviewTitles'>Task Node EBS Volume Size: </span><span className='reviewValue'>{this.state.taskEbsVolSize}</span></div>
                            <div className='grey'><span className='reviewTitles'>AMI ID: </span>{this.state.customAmiId.length === 0 ? <span className='reviewValue'>Default AWS AMI ID</span> : <span className='reviewValue'>{this.state.customAmiId}</span>}</div>
                            <div className='grey'><span className='reviewTitles'>Terminate Cluster After Completion: </span><span className='reviewValue'>{`${this.state.doTerminate}` + ' '}</span></div>
                            <div className='grey'><span className='reviewTitles'>Auto-Rotate Cluster: </span><span className='reviewValue'>{`${this.state.toggleStatus}` + ' '}</span></div>
                            {this.state.toggleStatus && <div className='grey'><span className='reviewTitles'>Auto-Pilot Window Start: </span><span className='reviewValue'>{formatDate(this.state.autopilotWindowStart, "HH")} Hours</span></div>}
                            {this.state.toggleStatus && <div className='grey'><span className='reviewTitles'>Auto-Pilot Window End: </span><span className='reviewValue'>{formatDate(this.state.autopilotWindowEnd, "HH")} Hours</span></div>}
                            <div className='grey'><span className='reviewTitles'>AMI Rotation SLA Days:</span><span className='reviewValue'>{this.state.amiRotationSla}</span></div>
                            <div className='grey'><span className='reviewTitles'>Headless Users: </span>{this.state.headlessUsers.length === 0 ? <span className='reviewValue'>none</span> : <span className='reviewValue'>{this.state.headlessUsers}</span>}</div>
                        </div>
                        {this.state.bootstrapActions.length > 0 ?
                            <div>
                                <div className='reviewTitle'>Bootstrap Actions</div>
                                <div className='reviewCluster'>
                                    {this.state.bootstrapActions.map((action, index) => (
                                        <div>
                                            <div className='grey'><span className='stepTitle'>Action #{index + 1}</span></div>
                                            <div className='grey'><span className='reviewSubtitles'>Name:</span><span className='reviewValue'>{{action}.action.bootstrapName}</span></div>
                                            <div className='grey'><span className='reviewSubtitles'>Arguments:</span><span className='reviewValue'>{{action}.action.bootstrapScript}</span></div>
                                        </div>
                                    ))}
                                </div>
                            </div> : null
                        }
                        {this.state.steps.length > 0 ?
                            <div>
                                <div className='reviewTitle'>Steps Information</div>
                                <div className='reviewCluster'>
                                    {this.state.steps.map((step, index) => (
                                        <div>
                                            <div className='grey'><span className='stepTitle'>Step #{index + 1}</span></div>
                                            <div className='grey'><span className='reviewSubtitles'>Name:</span><span className='reviewValue'>{{step}.step.name}</span></div>
                                            <div className='grey'><span className='reviewSubtitles'>Action on Failure:</span><span className='reviewValue'>{{step}.step.actionOnFailure}</span></div>
                                            <div className='grey'><span className='reviewSubtitles'>JAR Location:</span><span className='reviewValue'>{{step}.step.jar}</span></div>
                                            <div className='grey'><span className='reviewSubtitles'>Arguments:</span><span className='reviewValue'>{{step}.step.args}</span></div>
                                            <div className='grey'><span className='reviewSubtitles'>Main Class:</span><span className='reviewValue'>{{step}.step.mainClass}</span></div>
                                        </div>
                                    ))}
                                </div>
                            </div> : null
                        }
                        <button type="button" className="nextBtn" onClick={this.handleConfirmation} disabled={!this.formSubmit()}>Create</button>
                        <button className='nextBtn' onClick={(e) => this.handleBack(e)}>Back</button>
                        <Dialog
                            isOpen={this.state.showConfirmation}
                            onClose={this.handleConfirmation}
                            title="Confirm"
                        >
                            <div className={Classes.DIALOG_BODY}>
                                { checkJiraEnabled(this.props.globalJiraData, this.props.accountJiraData) &&
                                    <label className="servicenow-field">
                                    <span className="required">JIRA Ticket:</span>
                                    <input className="textField-servicenow" placeholder="Jira ticket" value={this.state.servicenow} onChange={e => this.handleServicenow(e)} />
                                    <div>{this.state.servicenow.length === 0 ? <span className='errorField'>This is a required field</span> : null }</div>
                                </label> }
                                <div><span style={{ fontSize: '15px' }}>New cluster <strong>{this.state.type}-{this.state.clusterSegment.toLowerCase()}-{this.state.clusterName}</strong> of type <strong>{this.state.type}</strong> and segment <strong>{this.state.clusterSegment}</strong> will be created. Continue?</span></div>
                                <button
                                className='nextBtn'
                                onClick={this.handleConfirmation}
                                >
                                Cancel
                                </button>
                                <button type="button" className="nextBtn" disabled={checkJiraEnabled(this.props.globalJiraData, this.props.accountJiraData) && this.state.servicenow.length === 0} onClick={this.handleSubmit}>Submit</button>

                            </div>
                        </Dialog>
                    </div>
                )
        }
    }

    /**
     * Update the instance group list based on the core and task instance values.
     */
    handleInstanceGroupList = () => {
        let instanceGrp = [];
        if (this.state.coreInstanceCount > 0) {
            instanceGrp.push('CORE')
        }

        if(this.state.taskInstanceCount > 0) {
            instanceGrp.push('TASK');
        }
        return instanceGrp
    }

    /**
     * Update the times provided for the auto-pilot window.
     */
    handleTimeChange(e, type) {
        if (type === 'start') {
            this.setState({
                ...this.state,
                autopilotWindowStart: e.target.value,
                autopilotWindowError: false
            })
        } else {
            let startHour = formatDate(this.state.autopilotWindowStart, "HH")
            let endHour = formatDate(e.target.value, "HH")
            if (endHour < startHour) {
                this.setState({
                    ...this.state,
                    autopilotWindowError: true,
                    autopilotWindowEnd: e.target.value
                })
            } else {
                this.setState({
                    ...this.state,
                    autopilotWindowEnd: e.target.value,
                    autopilotWindowError: false
                })
            }
        }

    }

    /**
     * Check the JIRA ticket.
     */
    handleServicenow(e) {
        if(e.target.value.match("^[a-zA-Z0-9-]*$")!=null)
            this.setState({
                ...this.state,
                servicenow: e.target.value
            })
    }

    /**
     * open/close confirmation popup.
     */
    handleConfirmation() {
        this.props.fetchAccountJiraEnabled(this.state.account, this.props.token)
        this.setState({
            ...this.state,
            showConfirmation: !this.state.showConfirmation
        })
    }

    /**
     * Save is Prod field value.
     */
    handleIsProd = event => {
        this.setState({
          ...this.state,
          isProd: !this.state.isProd
        })
      }
    
      handleAutoScaling = event => {
        this.setState({
          ...this.state,
          autoScaling: !this.state.autoScaling
        })
      }

    formSubmit() {
        return !(this.state.type === '' || this.state.clusterSegment === '' )
    }

    /**
     * Add bootstrap action information to the object array.
     */
    bootstrapModifier = (bootstrapActions, value, index, key) => {
        return bootstrapActions.map((v, i) => {
            if (index === i) {
                v[key] = value;
            }
            return v
        })
    }

    /**
     * Update any errors in the bootstrap actions object array. 
     */
    bootstrapErrorModifier = (bootstrapActionsErrors, value, index, key) => {
        return bootstrapActionsErrors.map((v, i) => {
            if (index === i) {
                v[key] = value
            }
            return v;
        })
    }

    /**
     * Add new bootstrap action to the array.
     */
    handleAddBootstrapAction = () => {
        const bootstrapAction_template = {
            bootstrapName: '',
            bootstrapScript: ''
        }

        const bootstrapActionError = {
            bootstrapName: true,
            bootstrapScript: true
        }

        this.setState({
            bootstrapActions: this.state.bootstrapActions.concat(bootstrapAction_template),
            bootstrapActionsErrors: this.state.bootstrapActionsErrors.concat(bootstrapActionError),
            bootstrapNameError: true,
            bootstrapScriptError: true
        })
    }

    /**
     * Remove a bootstrap action from the form.
     */
    handleRemoveBootstrapAction = () => {
        let nameError = false
        let argsError = false;
        this.state.bootstrapActionsErrors.map((v, i) => {
            if (i < (this.state.bootstrapActionsErrors.length - 1)) {
                nameError = nameError || v['bootstrapName'];
                argsError = argsError || v['bootstrapScript'];
            }
        })
        if (this.state.bootstrapActions.length === 1) {
            this.setState({
                bootstrapActions: this.state.bootstrapActions.slice(0, -1),
                bootstrapActionsErrors: this.state.bootstrapActionsErrors.slice(0, -1),
                bootstrapNameError: false,
                bootstrapScriptError: false
            })
        } else {
            this.setState({
                bootstrapActions: this.state.bootstrapActions.slice(0, -1),
                bootstrapActionsErrors: this.state.bootstrapActionsErrors.slice(0, -1),
                bootstrapNameError: nameError,
                bootstrapScriptError: argsError
            })
        }
    }

    /**
     * Add step information to the steps object array.
     */
    stepsModifier = (steps, value, index, key) => {
        return steps.map((v, i) => {
            if(index === i){
                v[key] = value
            }
            return v
        })
    }

    /**
     * Update any errors in the step objects in the array.
     */
    stepErrorModifier = (stepErrors, value, index, key) => {
        return stepErrors.map((v, i) => {
            if (index === i) {
                v[key] = value
            }
            return v;
        })
    }

    /**
     * Add a new step to the array.
     */
    handleAddStep() {
        const step_template = {
            name: '',
            jar: 's3://us-west-2.elasticmapreduce/libs/script-runner/script-runner.jar',
            args: '',
            actionOnFailure: 'CANCEL_AND_WAIT',
            mainClass: '',
            stepCreatedBy: this.props.fullName
        };
        const stepError = {
            name: true,
            jar: false,
            args: true,
        }
        this.setState({
            steps: this.state.steps.concat(step_template),
            stepErrors: this.state.stepErrors.concat(stepError),
            stepNameError: true,
            jarLocationError: false,
            argsError: true
        })
    }

    /**
     * Remove a step from the form.
     */
    handleRemoveStep(){
        let nameError = false;
        let jarError = false;
        let argsError = false;
        this.state.stepErrors.map((v, i) => {
            if (i < (this.state.stepErrors.length - 1)) {
                nameError = nameError || v['name'];
                jarError = jarError || v['jar'];
                argsError = argsError || v['args'];
            }
        })
        if (this.state.steps.length === 1) {
            this.setState({
                steps: this.state.steps.slice(0,-1),
                stepErrors: this.state.stepErrors.slice(0, -1),
                stepNameError: false,
                jarLocationError: false,
                argsError: false
            })
        } else {
            this.setState({
                steps: this.state.steps.slice(0,-1),
                stepErrors: this.state.stepErrors.slice(0, -1),
                stepNameError: nameError,
                jarLocationError: jarError,
                argsError: argsError
            })
        }
    }

    handleSubmit() {
        this.props.handleSpinnerActivity({
            "createdBy": this.props.fullName,
            "clusterName": this.state.type + '-' + this.state.clusterSegment.toLowerCase() + '-' + this.state.clusterName,
            "type": this.state.type,
            "subType": 'nonkerb',
            "account": this.state.account,
            "segment": this.state.clusterSegment.toLowerCase(),
            "masterInstanceType": this.state.masterInstanceType[0],
            "coreInstanceCount": this.state.coreInstanceCount+"",
            "coreInstanceType": this.state.coreInstanceType[0],
            "taskInstanceType": this.state.taskInstanceType[0],
            "taskInstanceCount": this.state.taskInstanceCount+"",
            "instanceGroup": this.state.autoScaling ? this.state.instanceGroup : '',
            "min": this.state.autoScalingMin,
            "max": this.state.autoScalingMax,
            "masterEbsVolSize": this.state.masterEbsVolSize,
            "coreEbsVolSize": this.state.coreEbsVolSize,
            "taskEbsVolSize": this.state.taskEbsVolSize,
            "customAmiId": this.state.customAmiId,
            "doTerminate": this.state.doTerminate,
            "headlessUsers": this.state.headlessUsers,
            steps: this.state.steps,
            "bootstrapActions": this.state.bootstrapActions,
            "isProd": this.state.isProd,
            "jiraTicket": this.state.servicenow,
            "autoAmiRotation": this.state.autoAmiRotation,
            "autopilotWindowStart": formatDate(this.state.autopilotWindowStart, "HH"),
            "autopilotWindowEnd": formatDate(this.state.autopilotWindowEnd, "HH"),
            "amiRotationSlaDays": this.state.amiRotationSla
            }, this.props.token, 'create')
        this.showSpinner();
    }

    showSpinner() {
        return (
            <div className='loader' />
        )
    }

    toggleDialog() {
        if(this.state.visible){
            this.props.onClose();
        }
        this.setState({
            visible: !this.state.visible
        });
    }
    
    /**
     * Update the cluster name in the state if it does not contain any character except alphanumerics and -
     */
    handleclusterName(e) {
        if(e.target.value.match("^[a-zA-Z0-9-]*$")!=null)
            this.setState({
                ...this.state,
                clusterName: e.target.value.toLowerCase()
            })
    }

    /**
     * Update the cluster type selected.
     */
    handleclusterType(e) {
        this.setState({
            ...this.state,
            showClusterName: !(e.target.value === 'exploratory' && this.state.isProd),
            type: e.target.value
        })
    }

    /**
     * Update the cluster sub type selected.
     */
    handleclusterSubType(e) {
        this.setState({
            ...this.state,
            subType: e.target.value,

        })
    }

    /**
     * Update the account selected.
     */
    handleAccountID(e) {
        this.setState({
            ...this.state,
            account: e.target.value
        })
    }

    /**
     * Update the role selected.
     * If the user is not a superadmin, update the accounts dropdown based on the role selected.
     */
    handleclusterSegment(e) {
        this.selectedRole = e.target.value
        if (this.props.superAdmin) {
            this.setState({
                ...this.state,
                clusterSegment: e.target.value
            })
        } else {
            this.setState({
                ...this.state,
                clusterSegment: e.target.value,
                account: this.roleAccounts[e.target.value][0]
            })
        }

    }

    /**
     * Update the master node type selected.
     */
    handlemasterNodeType(val) {
        this.setState({
            ...this.state,
            masterInstanceType: val
        })
    }

    /**
     * Update the AMI Rotation SLA Days entered. The value has to be between 1 to 99.
     */
    handleAMIRotationSLA(e) {
        if (e.target.value === '' || e.target.value.match("^(?:[1-9]|(?:[1-9][0-9])|(?:[1-9][0-9])|(?:99))$")) {
            this.setState({
                ...this.state,
                amiRotationSla: e.target.value
            })
        }
    }

    /**
     * Update the node count entered. The value has to be between 1 to 99.
     */
    handlecoreNodeCount(e) {
        if (e.target.value == null) {
            this.setState({
                ...this.state,
                coreInstanceCount: 0,
                coreInstanceCountError: false,
                instanceGroup: ''
            })
        } else if (e.target.value >= 0) {
            if (e.target.value == 0) {
                if (this.instanceGrpList.indexOf('CORE') > -1) {
                    this.instanceGrpList.splice(this.instanceGrpList.indexOf('CORE'), 1);
                }
            } else {
                if (this.instanceGrpList.indexOf('CORE') < 0) {
                    this.instanceGrpList.push('CORE');
                }
            }
            this.setState({
                ...this.state,
                coreInstanceCount: e.target.value,
                coreInstanceCountError: false,
                instanceGroup: this.instanceGrpList.length > 0 ? this.instanceGrpList[0] : ''
            })
        } 
    }

    /**
     * Update the node core type selected.
     */
    handlecoreNodeType(val) {
        this.setState({
            ...this.state,
            coreInstanceType: val
        })
    }

    /**
     * Update the node count entered. The value has to be between 1 and 99.
     */
    handletaskNodeCount(e) {
        if (e.target.value == null) {
            this.setState({
                ...this.state,
                taskInstanceCount: 0,
                taskInstanceCountError : false,
                instanceGroup: ''
            })
        } else if (e.target.value >= 0) {
            if (e.target.value == 0) {
                if (this.instanceGrpList.indexOf('TASK') > -1) {
                    this.instanceGrpList.splice(this.instanceGrpList.indexOf('TASK'), 1);
                }
            } else if (e.target.value > 0) {
                if (this.instanceGrpList.indexOf('TASK') < 0) {
                    this.instanceGrpList.push('TASK');
                }
            }
            this.setState({
                ...this.state,
                taskInstanceCount: e.target.value,
                taskInstanceCountError: false,
                instanceGroup: this.instanceGrpList.length > 0 ? this.instanceGrpList[0] : ''
            })
        }
    }

    /**
     * Update the task node type selected.
     */ 
    handletaskNodeType(val) {
        this.setState({
            ...this.state,
            taskInstanceType: val
        })
    }

    handleInstanceGroup = (e) => {
        this.setState({
            ...this.state,
            instanceGroup: e.target.value
        })
    }

    handleAutoScalingMin = (e) => {
        if (e.target.value.length === 0) {
            this.setState({
                ...this.state,
                autoScalingMin: e.target.value,
                instanceMinCountError : true,
            })
        } else if (e.target.value.match("^([0-9]*)$"))
            this.setState({
                ...this.state,
                autoScalingMin: e.target.value,
                instanceMinCountError : false,
            })
    }

    handleAutoScalingMax = (e) => {
        if (e.target.value.length === 0) {
            this.setState({
                ...this.state,
                autoScalingMax: e.target.value,
                instanceMaxCountError : true,
            })
        } else if (e.target.value.match("^([0-9]*)$"))
            this.setState({
                ...this.state,
                autoScalingMax: e.target.value,
                instanceMaxCountError : false
            })
    }

    /**
     * Update the master volume size selected.
     */
    handlemasterVolSize(e) {
        this.setState({
            ...this.state,
            masterEbsVolSize: e.target.value
        })
    }

    /**
     * Update the core volumn size selected.
     */
    handlecoreVolSize(e) {
        this.setState({
            ...this.state,
            coreEbsVolSize: e.target.value
        })
    }

    /**
     * Update the task volumn size selected.
     */
    handletaskVolSize(e) {
        this.setState({
            ...this.state,
            taskEbsVolSize: e.target.value
        })
    }

    /**
     * Update the ami ID.
     */
    handleamiID(e) {
        if(e.target.value.match("^[a-zA-Z0-9-]*$")!=null)
            this.setState({
                ...this.state,
                customAmiId: e.target.value
            })
    }

    /**
     * Update the auto-terminate field.
     */
    handledoTerminate(e) {
        this.setState({
            ...this.state,
            doTerminate: e.target.value
        })
    }

    handleToggle(type) {
        if(type === 'terminateCluster') {
            let newToggle_T = !this.state.toggleStatusTerminate
            this.setState({
                ...this.state, 
                toggleStatusTerminate: newToggle_T,
                doTerminate: !!newToggle_T
            })
        } else if(type === 'autoPilot') {
            let newToggle_A = !this.state.toggleStatus
            this.setState({
                ...this.state, 
                toggleStatus: newToggle_A,
                autoAmiRotation: newToggle_A ? 'True' : 'False'
            })
        }
    }

    /**
     * Update headless users field.
     */
    handleheadlessUsers(e) {
        if (e.target.value.match("^[a-zA-Z0-9-_,]*$"))
        this.setState({
            ...this.state,
            headlessUsers: e.target.value,
            headlessUsersError: false
        })
    }

    handlemainClass(e, index) {
        this.setState({
            ...this.state,
            steps: this.stepsModifier(this.state.steps, e.target.value, index, 'mainClass')
        })
    }

    /**
     * Add new bootstrap action name.
     */
    handleBootstrapName(e, index) {
        let error = false;
        this.state.bootstrapActionsErrors.map((v, i) => {
            if (index != i) {
                error = error || v['bootstrapName']
            }
        })
        if (e.target.value.length === 0) {
            this.setState({
                ...this.state,
                bootstrapActions: this.bootstrapModifier(this.state.bootstrapActions, e.target.value, index, 'bootstrapName'),
                bootstrapActionsErrors: this.bootstrapErrorModifier(this.state.bootstrapActionsErrors, true, index, 'bootstrapName'),
                bootstrapNameError: true
            })
        } else if (e.target.value.match("^[a-zA-Z0-9- ]*$") != null) {
            this.setState({
                ...this.state,
                bootstrapActions: this.bootstrapModifier(this.state.bootstrapActions, e.target.value, index, 'bootstrapName'),
                bootstrapActionsErrors: this.bootstrapErrorModifier(this.state.bootstrapActionsErrors, false, index, 'bootstrapName'),
                bootstrapNameError: error
            })
        }
    }

    /**
     * Add bootstrap action args.
     */
    handleBootstrapScript(e, index) {
        let error = false
        this.state.bootstrapActionsErrors.map((v, i) => {
            if (index != i) {
                error = error && v['bootstrapScript']
            }
        })
        if (e.target.value.length === 0) {
            this.setState({
                ...this.state,
                bootstrapActions: this.bootstrapModifier(this.state.bootstrapActions, e.target.value, index, 'bootstrapScript'),
                bootstrapActionsErrors: this.bootstrapErrorModifier(this.state.bootstrapActionsErrors, true, index, 'bootstrapScript'),
                bootstrapScriptError: true
            })
        }
        else
            this.setState({
                ...this.state,
                bootstrapActions: this.bootstrapModifier(this.state.bootstrapActions, e.target.value, index, 'bootstrapScript'),
                bootstrapActionsErrors: this.bootstrapErrorModifier(this.state.bootstrapActionsErrors, false, index, 'bootstrapScript'),
                bootstrapScriptError: error
            })
    }

    /**
     * Add step name.
     */
    handlename(e, index) {
        let error = false
        this.state.stepErrors.map((v, i) => {
            if (index != i) {
                error = error && v['name']
            }
        })
        if (e.target.value.length === 0) {
            this.setState({
                ...this.state,
                steps: this.stepsModifier(this.state.steps, e.target.value, index, 'name'),
                stepErrors: this.stepErrorModifier(this.state.stepErrors, true, index, 'name'),
                stepNameError: true
            })
        }
        else if (e.target.value.match("^[a-zA-Z0-9- ]*$") != null)
            this.setState({
                ...this.state,
                steps: this.stepsModifier(this.state.steps, e.target.value, index, 'name'),
                stepErrors: this.stepErrorModifier(this.state.stepErrors, false, index, 'name'),
                stepNameError: error
            })

    }

    /**
     * Add step jar location.
     */
    handlejarLocation(e, index) {
        let error = false
        this.state.stepErrors.map((v, i) => {
            if (index != i) {
                error = error && v['jar']
            }
        })
        if (e.target.value.length === 0) {
            this.setState({
                ...this.state,
                steps: this.stepsModifier(this.state.steps, e.target.value, index, 'jar'),
                stepErrors: this.stepErrorModifier(this.state.stepErrors, true, index, 'jar'),
                jarLocationError: true
            })
        }
        else
            this.setState({
                ...this.state,
                steps: this.stepsModifier(this.state.steps, e.target.value, index, 'jar'),
                stepErrors: this.stepErrorModifier(this.state.stepErrors, false, index, 'jar'),
                jarLocationError: error
            })
    }

    /**
     * Add step args.
     */
    handlearguments(e, index) {
        let error = false
        this.state.stepErrors.map((v, i) => {
            if (index != i) {
                error = error && v['args']
            }
        })
        if (e.target.value.length === 0) {
            this.setState({
                ...this.state,
                steps: this.stepsModifier(this.state.steps, e.target.value, index, 'args'),
                stepErrors: this.stepErrorModifier(this.state.stepErrors, true, index, 'args'),
                argsError: true
            })
        }
        else
            this.setState({
                ...this.state,
                steps: this.stepsModifier(this.state.steps, e.target.value, index, 'args'),
                stepErrors: this.stepErrorModifier(this.state.stepErrors, false, index, 'args'),
                argsError: error
            })
    }

    /**
     * Add step action on failure field.
     */
    handleactiononFailure(e, index) {
        this.setState({
            ...this.state,
            steps: this.stepsModifier(this.state.steps, e.target.value, index, 'actionOnFailure')
        })
    }
}

const mapStateToProps = state => {
    return {
        clusterCloneData: state.emrMetadataData.clusterCloneData,
        accountJiraFetching: state.emrMetadataData.accountJiraFetching,
        accountJiraData: state.emrMetadataData.accountJiraData
    }
}

const mapDispatchToProps = dispatch => {
    return {
        fetchClusterClone: (id, token) => dispatch(fetchClusterClone(id, token)),
        fetchAccountJiraEnabled: (account, token) => dispatch(fetchAccountJiraEnabled(account, token))
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(CreateCluster)

