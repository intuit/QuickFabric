import React from 'react';
import './admin.scss';
import { Tab, Tabs, Intent,Toaster,Toast, Position } from '@blueprintjs/core'
import Select from 'react-select';
import { connect } from 'react-redux';
import Fab from '@material-ui/core/Fab';
import DeleteIcon from '@material-ui/icons/Delete';
import AddIcon from '@material-ui/icons/Add';
import { Dialog, Classes, Icon } from '@blueprintjs/core';
import { debounce } from 'lodash';
import { Grid, GridColumn as Column } from '@progress/kendo-react-grid'
import { NumericTextBox } from '@progress/kendo-react-inputs'
import { 
    postAddRoles,
    postRemoveRoles,
    fetchUserRoles,
    postAccountSetup,
    fetchConfigDefinitions,
    fetchConfigDefinitionList,
    fetchConfigDefinitionsById,
    putConfigDefinitions,
    decryptConfigByName,
    resetUserPassword,
    clearStatus,
    clearConfigs
} from '../../actions/admin'
import { fetchUIDropdownList } from '../../actions/emrManagement'
import {ToggleSlider} from '../../utils/components/ToggleSlider.jsx'
import ConfigManagement from './ConfigManagement'
import ConfigDialogContainer from './ConfigDialogContainer'
import { withState } from '../../utils/components/with-state.jsx';
import { DCCSegmentGrid} from './detailGrids'
const StatefulGrid = withState(Grid);

const services =[{
    label: 'EMR',
    value: 'emr'
}]

class Admin extends React.Component {
    spinner = false;
    constructor(props) {
        super(props);
        this.toaster = Toaster
        this.refHandlers = {
            toaster: (ref) => this.toaster = ref
        }

        this.state = {
            primaryAction: 'accountSetUp',
            primaryTab: 'addRoles',
            primaryConfigTab: 'accountConfig',
            addemailID: '',
            addfname: '',
            addlname: '',
            addservices: [services[0].value],
            addsegments: [this.props.uiListData.segments_valFormat[0].value],
            addactions: [],
            addaccounts: [this.props.uiListData.accounts_valFormat[0].value],
            removeemailID: '',
            removefname: '',
            removelname: '',
            segments: this.props.uiListData.segments_valFormat,
            accounts: this.props.uiListData.accounts_valFormat,
            actions: this.props.uiListData.actions_valFormat,
            removeservices: [services[0].value],
            removesegments: [this.props.uiListData.segments_valFormat[0].value],
            removeactions: [],
            removeaccounts: [this.props.uiListData.accounts_valFormat[0].value],
            toasts: [],
            spinner: false,
            showAddConfirmation: false,
            showRemoveConfirmation: false,
            resetPwdEmailID: '',
            resetPwd: '',
            showResetPwdConfirmation: false,
            errValidation: {
                addEmailErr: false,
                addEmailErrMessage: '',
                removeEmailErr: false,
                removeEmailErrMessage: '',
                segmentNameErr: false,
                segmentNameErrMessage: '',
                segmentOwnerNameErr: false,
                segmentOwnerNameErrMessage: '',
                ownerEmailIdErr: false,
                ownerEmailIdErrMessage: '',
                accountOwnerNameErr: false,
                accountOwnerNameErrMessage: '',
                resetPwdEmailErr: false,
                resetPwdEmailErrMessage: ''
            },   
            uiListData: {roles: [], accounts: []},
            uiListLoaded: false,
            currentStep: 'account',
            accountTypeOptions: ['prod', 'preprod', 'dev'],
            accountDetails: {
                accountId: 0,
                accountType: 'prod',
                accountOwnerName: '',
                accountIdErrorMessage: '',
                accountTypeErrorMessage: '',
                accountOwnerNameErrorMessage: '',
                error: true
            },
            segmentsList: this.props.uiListData.segments,
            segmentsListUser: this.props.uiListData.segments_valFormat,
            segmentsListLowerCase: this.props.uiListData.segments,
            segmentDetails: {

            },
            clusterSegments: [{
                id: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15),
                segmentName: '',
                segmentOwnerName: '',
                ownerEmailId: '',
                segmentNameErrorMessage: '',
                segmentOwnerNameErrorMessage: '',
                ownerEmailIdErrorMessage: '',
                isExist: false,
                error: true
            }],
            segmentsListPlaceholder: 'Existing Segments',
            currSegmentObj: {
                segmentName: '',
                segmentOwnerName: '',
                ownerEmailId: ''
            },
            testSuiteData: [
                {
                    id: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15),
                    selected: false,
                    testSuiteName: 'AutoScaling',
                    testSuiteDescription: 'This is AutoScaling',
                    clusterType: ['Exploratory', 'Scheduled', 'Transient'],
                    clusterSegment: [],
                    criteria: '',
                    type: 'min/max',
                    bootstrapNo: 0,
                    min: 0,
                    max: 1,

                    error: false,
                    criteriaBootstrapErrorMessage: '',
                    criteriaMinMaxErrorMessage: ''
                    
                },
                {
                    id: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15),
                    selected: false,
                    testSuiteName: 'NoOfBootstraps',
                    testSuiteDescription: 'No. of Bootstraps',
                    clusterType: ['Exploratory', 'Scheduled', 'Transient'],
                    clusterSegment: [],
                    criteria: '',
                    type: 'No. of Bootstraps',
                    min: 0,
                    max: 1,
                    bootstrapNo: 0,
                    defaultSteps: 0,
                    error: false,
                    errorMessage: '',
                    bootstrapNoErrorMessage: '',
                    criteriaMinMaxErrorMessage: ''
                }
            ],
            configDetailData: [
                {
                    "configType": "Account",
                    "configId": 17,
                    "configName": "gateway_api_key",
                    "configDataType": "String",
                    "encryptionRequired": false,
                    "apiEncryption": false,
                    'configValueError': false,
                    'configValueErrorMessage': ''
                },
                {
                    "configType": "Account",
                    "configId": 18,
                    "configName": "Jira",
                    "configDataType": "String",
                    "encryptionRequired": false,
                    "apiEncryption": false,
                    'configValueError': false,
                    'configValueErrorMessage': ''
                },
                {
                    "configType": "Account",
                    "configId": 36,
                    "configName": "TestSuites",
                    "configDataType": "String",
                    "encryptionRequired": false,
                    "apiEncryption": false,
                    'configValueError': false,
                    'configValueErrorMessage': ''
                },
                {
                    "configType": "Account",
                    "configId": 45,
                    "configName": "cluster_auto_termination",
                    "configDataType": "String",
                    "encryptionRequired": false,
                    "apiEncryption": false,
                    'configValueError': false,
                    'configValueErrorMessage': ''
                },
                {
                    "configType": "Account",
                    "configId": 66,
                    "configName": "backend_api_url",
                    "configDataType": "String",
                    "encryptionRequired": false,
                    "apiEncryption": false,
                    'configValueError': false,
                    'configValueErrorMessage': ''
                },
            ],
            jiraUser: '',
            jiraPassword: '',
            jiraProjects: '',
            servicenowUser: '',
            servicenowPassword: '',
            jiraUrl: '',
            jiraUrlErrorMessage: '',
            serviceNowEnabled: false,
            jiraEnabled: false,
            serviceNowEnabled: false,
            jiraEnabledErrorMessage: '',
            jiraUserErrorMessage: '',
            jiraPasswordErrorMessage: '',
            jiraProjectsErrorMessage: '',
            servicenowUrl: '',
            servicenowUserErrorMessage: '',
            servicenowPasswordErrorMessage: '',
            servicenowUrlErrorMessage: '',
            newUserData: [],
            handleStepsDisabled: true,
            accountDetailsBtnDisabled: false,
            segmentsBtnDisabled: true,
            testSuitesBtnDisabled: true,
            applicationSetUpBtnDisabled: true,
            newUserBtnDisabled: true,
            reviewBtnDisabled: true,
            showUserForm: false,
            showAccConfirmation: false,
            accData: {},
            objectInEdit: {},
            currType: '',
            isPwdType: true,
            isPwdType_JIRA: true,
            isPwdType_serviceNow: true
        }

        this.renderTab = this.renderTab.bind(this);
        this.renderConfigTab = this.renderConfigTab.bind(this);
        this.handlePrimaryTabChange = this.handlePrimaryTabChange.bind(this);
        this.handlePrimaryConfigTabChange = this.handlePrimaryConfigTabChange.bind(this);
        this.selectedSegment = this.selectedSegment.bind(this);
        this.selectedActions = this.selectedActions.bind(this);
        this.selectedServices = this.selectedServices.bind(this);
        this.selectedAccounts = this.selectedAccounts.bind(this);
        this.handleEmailID = this.handleEmailID.bind(this);
        this.handleFirstName = this.handleFirstName.bind(this);
        this.handleLastName = this.handleLastName.bind(this);
        this.handleAddRoles = this.handleAddRoles.bind(this);
        this.handleRemoveRoles = this.handleRemoveRoles.bind(this);
        this.handleAddConfirmation = this.handleAddConfirmation.bind(this);
        this.handleRemoveConfirmation = this.handleRemoveConfirmation.bind(this);
        this.handleAddSubmit = this.handleAddSubmit.bind(this);
        this.renderErrCheck = this.renderErrCheck.bind(this);
        this.handleRemoveSubmit = this.handleRemoveSubmit.bind(this);
        this.SuccessToast = this.SuccessToast.bind(this);
        this.ErrorToast = this.ErrorToast.bind(this);
        this.retrieveRoles = debounce(this.retrieveRoles.bind(this), 2500, false);
        this.validateText = this.validateText.bind(this);
        this.expandChange = this.expandChange.bind(this);
        this.handleChange = this.handleChange.bind(this);
        this.handleFormChange = this.handleFormChange.bind(this);
        this.handleStepsChange = this.handleStepsChange.bind(this);
        this.validateSegments = this.validateSegments.bind(this);
        this.errCheck = this.errCheck.bind(this);
        this.handleErrorResponse = this.handleErrorResponse.bind(this);
        this.checkError = this.checkError.bind(this);
        this.selectionChange = this.selectionChange.bind(this);
        this.rowClick = this.rowClick.bind(this);
        this.headerSelectionChange = this.headerSelectionChange.bind(this);
        this.handleAddSegment = this.handleAddSegment.bind(this);
        this.renderSegmentForm = this.renderSegmentForm.bind(this);
        this.handleToggleEncrypt = this.handleToggleEncrypt.bind(this);
        this.handleRemoveSegment = this.handleRemoveSegment.bind(this);
        this.handleRemoveNewUser = this.handleRemoveNewUser.bind(this);
        this.handleAddNewUser = this.handleAddNewUser.bind(this);
        this.handleSegmentChange = this.handleSegmentChange.bind(this);
        this.setUpForms = debounce(this.setUpForms.bind(this), 1500, false);
        this.handleChangeKendo = this.handleChangeKendo.bind(this);
        this.onItemChangeConfig = this.onItemChangeConfig.bind(this);
        this.renderAccountOnboarding = this.renderAccountOnboarding.bind(this);
        this.handleSubmit = debounce(this.handleSubmit.bind(this), 1500, false);
        this.formatData = this.formatData.bind(this);
        this.itemChangeTestSuite = this.itemChangeTestSuite.bind(this);
        this.handleUserChange = this.handleUserChange.bind(this);
        this.renderUserForm = this.renderUserForm.bind(this);
        this.formatUserData = this.formatUserData.bind(this);
        this.setError = this.setError.bind(this);
        this.handleAccConfirmation = this.handleAccConfirmation.bind(this);
        this.configCheck = this.configCheck.bind(this);
        this.handlePrimaryConfigTabChange = this.handlePrimaryConfigTabChange.bind(this);
        this.handlePassword = this.handlePassword.bind(this);
    }

    render() {
        return (
            <div className='admin-component'>
                <Toaster position={Position.TOP} ref={this.refHandlers.toaster}>
                {this.state.toasts.map(toast => <Toast {...toast} />)}
                </Toaster>
                {this.state.spinner ? 
                    <div className='loader-wrapper'>
                    <div className='loader' />
                  </div>  :
                    null
                }
                <h2>Administration</h2>
                <div className='actions'>
                    <ul>
                    <li><button className={`actionBtn ${this.state.primaryAction == 'accountSetUp' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('accountSetUp')}><span>Account Onboarding</span></button></li>
                    <li><button className={`actionBtn ${this.state.primaryAction == 'configManagement' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('configManagement')}><span>Config Management</span></button></li>
                    <li><button className={`actionBtn ${this.state.primaryAction == 'userManagement' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('userManagement')}><span>User Management</span></button></li>
                    </ul>
                </div>
                {this.renderActions(this.state.primaryAction)}
            </div>
        )
    }

    selectedSegment(params, type) {
        let segments = [];
        params !== null && params.forEach(x => segments.push(x.value))
        if (type === 'add') {
            this.setState({
                ...this.state,
                addsegments: segments
            })
        } else {
            this.setState({
                ...this.state,
                removesegments: segments
            })
        }
        
    }

    selectedActions(param, type) {
        let actions = [];
        param != null && param.forEach(x => actions.push(x.value))
        if (type === 'add') {
            this.setState({
                ...this.state,
                addactions: actions
            })
        } else {
            this.setState({
                ...this.state,
                removeactions: actions
            })
        }
        
    }

    selectedAccounts(param, type) {
        let accounts = [];
        param !== null && param.forEach(x => accounts.push(x.value))
        if (type === 'add') {
            this.setState({
                ...this.state,
                addaccounts: accounts
            })
        } else {
            this.setState({
                ...this.state,
                removeaccounts: accounts
            })
        }
        
    }

    selectedServices(param, type) {
        let services = [];
        param !== null && param.forEach(x => services.push(x.value))
        if (type === 'add') {
            this.setState({
                ...this.state,
                addservices: services
            })
        } else {
            this.setState({
                ...this.state,
                removeservices: services
            })
        }
        
    }
   // Render Actions according to currTab

   renderActions(currTab) {
    switch(currTab) {
        case 'userManagement' : 
            return (
                <div className={`actionContent actionContentCluster`}>
                <div className='tabContent'>
                <Tabs className='line' id="primary-tab" selectedTabId={this.state.primaryTab} onChange={this.handlePrimaryTabChange}>
                    <Tab id="addRoles" title="Add Roles" />
                    <Tab id="removeRoles" title="Remove Roles" />
                    <Tab id="resetPwd" title="Reset Password" />
                </Tabs>
                    {
                        this.renderTab(this.state.primaryTab)
                    }
                </div>
            </div>
            )
    case 'accountSetUp': 
        return (
            <div class='actionContentCluster'>
            <form className="k-form">
            <div>
                <button disabled={this.state.accountDetailsBtnDisabled} className={`breadcrumbBtn ${this.state.accountDetailsBtnDisabled ? 'disabledBtn' : ''} ${this.state.currentStep == 'account' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'account')}>1. Account Details ></button>
                <button disabled={this.state.segmentsBtnDisabled } className={`breadcrumbBtn ${this.state.segmentsBtnDisabled ? 'disabledBtn' : ''} ${this.state.currentStep == 'segment' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'segment')}>2. Segments ></button>
                <button disabled={this.state.testSuitesBtnDisabled} className={`breadcrumbBtn ${this.state.testSuitesBtnDisabled ? 'disabledBtn' : ''} ${this.state.currentStep == 'testSuites' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'testSuites')}>3. Test Suite ></button>
                <button disabled={this.state.applicationSetUpBtnDisabled} className={`breadcrumbBtn ${this.state.applicationSetUpBtnDisabled ? 'disabledBtn' : ''} ${this.state.currentStep == 'a' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'application')}>4. Config ></button>
                <button disabled={this.state.newUserBtnDisabled} className={`breadcrumbBtn ${this.state.newUserBtnDisabled ? 'disabledBtn' : ''} ${this.state.currentStep == 'newUser' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'newUser')}>5. New User ></button>
                <button disabled={this.state.reviewBtnDisabled} className={`breadcrumbBtn ${this.state.reviewBtnDisabled ? 'disabledBtn' : ''} ${this.state.currentStep == 'review' ? 'activeBtn' : ''}`} onClick={(e) => this.handleFormChange(e, 'review')}>6. Review ></button>
            </div>
            <div className='formStyling'>
                <hr />
                {this.renderAccountOnboarding(this.state.currentStep)}
            </div>
            </form>
            </div>
        )
        case 'configManagement': 
        return (
            <div class='actionContentCluster'>
                <div className='tabContent'>
                <Tabs className='line' id="primary-tab" selectedTabId={this.state.primaryConfigTab} onChange={this.handlePrimaryConfigTabChange}>
                    <Tab id="accountConfig" title="Account Configs" />
                    <Tab id="applicationConfig" title="Application Configs" />
                </Tabs>
                    {
                        this.renderConfigTab(this.state.primaryConfigTab)
                    }
                </div>              
            </div>
        )
    }
}
    renderConfigTab = (primaryTab) => {
        
        switch(primaryTab) {
            case 'accountConfig':
                let randomStr = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15)
                return (
                    <form className="k-form">
                    <div>
                        <h4>Account Config Management</h4>
                    </div>
                    <hr />
                    <div className='account-config-container'>

                        {primaryTab === 'accountConfig' ? <ConfigManagement id={randomStr} type={primaryTab} {...this.props} /> : null}
                    </div>
                    </form>
                )
            case 'applicationConfig':
                let randomAppStr = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15)

                this.props.fetchConfigDefinitionList(this.props.token)
                return (
                    <form className="k-form">
                    <div><h4>Application Config Management</h4></div>
                    <hr />
                    <div className='account-config-container'>

                        {primaryTab === 'applicationConfig' && <ConfigManagement id={randomAppStr} type={primaryTab} {...this.props} />}
                    </div>
                    </form>
                )
        }
    }
    renderTab = (primaryTab) => {
        switch(primaryTab) {
            case 'addRoles' :
                return (
                    <div className='tab-content'>
                        <h3>Add Roles</h3>
                        <table>
                            <tbody>
                                <tr>
                                    <td><span className='required labelTxt'>User Email ID:</span></td>
                                    <td>
                                        <input type="text" className="txtfield" value={this.state.addemailID} onChange={(e) => this.handleEmailID(e, 'add')} />
                                        {this.props.getUserRolesSuccess && !this.props.getUserRolesData.loginRoles ? 
                                            <div><span className='errorField'>E-mail ID does not exist. Proceed to create a new User</span></div> : this.state.errValidation.addEmailErr ? 
                                            <div><span className='errorField'>{this.state.errValidation.addEmailErrMessage}</span></div> : null}
                                    </td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>First Name:</span></td>
                                    <td><input type="text" className="txtfield" value={this.state.addfname} onChange={(e) => this.handleFirstName(e, 'add')} /></td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>Last Name:</span></td>
                                    <td><input type="text" className="txtfield" value={this.state.addlname} onChange={(e) => this.handleLastName(e, 'add')} /></td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>Service</span></td>
                                    <td>
                                        <Select defaultValue={[services[0]]} isMulti name="services" options={services} className="basic-multi-select" classNamePrefix="select" onChange={(e) => this.selectedServices(e, 'add')} />
                                        {this.state.addservices.length === 0 ? <div><span className='errorField'>This is a required field</span></div> : null}
                                    </td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>Segments</span></td>
                                    <td>
                                        <Select defaultValue={[this.state.segments[0]]} isMulti name="segments" options={this.state.segments} className="basic-multi-select" classNamePrefix="select" onChange={(e) => this.selectedSegment(e, 'add')} />
                                        {this.state.addsegments.length === 0 ? <div><span className='errorField'>This is a required field</span></div> : null}
                                    </td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>Actions</span></td>
                                    <td>
                                        <Select isMulti name="actions" options={this.state.actions} className="basic-multi-select" classNamePrefix="select" onChange={(e) => this.selectedActions(e, 'add')} />
                                        {this.state.addactions.length === 0 ? <div><span className='errorField'>This is a required field</span></div> : null}
                                    </td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>AWS Account</span></td>
                                    <td>
                                        <Select defaultValue={[this.state.accounts[0]]} isMulti name="accounts" options={this.state.accounts} className="basic-multi-select" classNamePrefix="select" onChange={(e) => this.selectedAccounts(e, 'add')} />
                                        {this.state.addaccounts.length === 0 ? <div><span className='errorField'>This is a required field</span></div> : null}
                                    </td>
                                </tr>
                            </tbody>
                        </table>

                        <button type="submit" className='addBtn' disabled={this.state.errValidation.addEmailErr || this.state.addemailID.length === 0 || 
                            this.state.addservices.length === 0 || 
                            this.state.addsegments.length === 0 ||
                            this.state.addactions.length === 0 ||
                            this.state.addaccounts.length ===0} onClick={this.handleAddRoles}>Add Roles
                        </button>
                        
                        {this.props.getUserRolesSuccess && this.props.getUserRolesData.loginRoles && this.props.getUserRolesData.loginRoles.services !== undefined && this.renderUserRoles()}

                        <Dialog
                            isOpen={this.state.showAddConfirmation}
                            onClose={this.handleAddConfirmation}
                            title="Confirm"
                        >
                            <div className={Classes.DIALOG_BODY}>
                                {this.props.getUserRolesSuccess && !this.props.getUserRolesData.loginRoles ? <span style={{ fontSize: '15px' }}><strong>{this.state.addfname}</strong> <strong>{this.state.addlname}</strong> did not exist. Do you wish to submit a new user with the granted access?</span> : <span style={{ fontSize: '15px' }}>Adding access roles for <strong>{this.state.addfname}</strong> <strong>{this.state.addlname}</strong>. Continue?</span>}
                                
                            </div>
                            <div className={Classes.DIALOG_FOOTER}>
                                <button
                                className='nextBtn'
                                onClick={this.handleAddConfirmation}
                                >
                                Cancel
                                </button>
                                <button type="button" className="nextBtn" onClick={this.handleAddSubmit}>Submit</button>
                            </div>
                        </Dialog> 
                    </div>
                )
            
            case 'removeRoles': 
                return (
                    <div className='tab-content'>
                        <h3>Remove Roles</h3>
                        <table>
                            <tbody>
                                <tr>
                                    <td><span className='required labelTxt'>User Email ID:</span></td>
                                    <td>
                                        <input type="text" className="txtfield" value={this.state.removeemailID} onChange={(e) => this.handleEmailID(e, 'remove')} />
                                        {this.props.getUserRolesSuccess && !this.props.getUserRolesData.loginRoles ? 
                                            <div><span className='errorField'>E-mail ID does not exist. Proceed to creating new user under Add Roles.</span></div> : this.state.errValidation.removeEmailErr ? 
                                            <div><span className='errorField'>{this.state.errValidation.removeEmailErrMessage}</span></div> : null}
                                    </td>
                                </tr>
                                <tr>
                                    <td><span className='labelTxt'>First Name:</span></td>
                                    <td><input type="text" className="txtfield" value={this.state.removefname} onChange={(e) => this.handleFirstName(e, 'remove')} /></td>
                                </tr>
                                <tr>
                                    <td><span className='labelTxt'>Last Name:</span></td>
                                    <td><input type="text" className="txtfield" value={this.state.removelname} onChange={(e) => this.handleLastName(e, 'remove')} /></td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>Service</span></td>
                                    <td>
                                        <Select defaultValue={[services[0]]} isMulti name="services" options={services} className="basic-multi-select" classNamePrefix="select" onChange={(e) => this.selectedServices(e, 'remove')} />
                                        {this.state.removeservices.length === 0 ? <div><span className='errorField'>This is a required field</span></div> : null}
                                    </td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>Segments</span></td>
                                    <td>
                                        <Select defaultValue={[this.state.segments[0]]} isMulti name="segments" options={this.state.segments} className="basic-multi-select" classNamePrefix="select" onChange={(e) => this.selectedSegment(e, 'remove')} />
                                        {this.state.removesegments.length === 0 ? <div><span className='errorField'>This is a required field</span></div> : null}
                                    </td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>Actions</span></td>
                                    <td>
                                        <Select isMulti name="actions" options={this.state.actions} className="basic-multi-select" classNamePrefix="select" onChange={(e) => this.selectedActions(e, 'remove')} />
                                        {this.state.removeactions.length === 0 ? <div><span className='errorField'>This is a required field</span></div> : null}
                                    </td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>AWS Account</span></td>
                                    <td>
                                        <Select defaultValue={[this.state.accounts[0]]} isMulti name="accounts" options={this.state.accounts} className="basic-multi-select" classNamePrefix="select" onChange={(e) => this.selectedAccounts(e, 'remove')} />
                                        {this.state.addaccounts.length === 0 ? <div><span className='errorField'>This is a required field</span></div> : null}
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <button type="submit" className='addBtn' onClick={this.handleRemoveRoles}>Remove Roles</button>
                        {this.props.getUserRolesSuccess && this.props.getUserRolesData.loginRoles && this.renderUserRoles()}
                        <Dialog
                            isOpen={this.state.showRemoveConfirmation}
                            onClose={this.handleRemoveConfirmation}
                            title="Confirm"
                        >
                            <div className={Classes.DIALOG_BODY}>
                                <span style={{ fontSize: '15px' }}>Removing access roles for <strong>{this.state.removefname}</strong> <strong>{this.state.removelname}</strong>. Continue?</span>
                            </div>
                            <div className={Classes.DIALOG_FOOTER}>
                                <button
                                className='nextBtn'
                                onClick={this.handleRemoveConfirmation}
                                >
                                Cancel
                                </button>
                                <button type="button" className="nextBtn" onClick={this.handleRemoveSubmit}>Submit</button>
                            </div>
                        </Dialog> 
                    </div>
                )
            case 'resetPwd':
                return (
                    <div className='tab-content'>
                        <h3>Reset Password</h3>
                        <table>
                            <tbody>
                                <tr>
                                    <td><span className='required labelTxt'>User Email ID:</span></td>
                                    <td>
                                        <input type="text" className="txtfield" value={this.state.resetPwdEmailID} onChange={(e) => this.handleEmailID(e, 'reset')} />
                                        {this.props.getUserRolesSuccess && !this.props.getUserRolesData.loginRoles ? 
                                            <div><span className='errorField'>E-mail ID does not exist.</span></div> : this.state.errValidation.resetPwdEmailErr ? 
                                            <div><span className='errorField'>{this.state.errValidation.resetPwdEmailErrMessage}</span></div> : null}
                                    </td>
                                </tr>
                                <tr>
                                    <td><span className='required labelTxt'>New Password:</span></td>
                                    <td>
                                        <input type={this.state.isPwdType ? "password" : "text"} className="pwd-input" value={this.state.resetPwd} onChange={(e) => this.handlePassword(e)} />

                                        {this.state.isPwdType ?
                                            <div className="btn-pwd-input">
                                                <button style={{marginLeft: '5px'}} onClick={this.handlePasswordTextType}>
                                                    <Icon icon={'eye-on'} iconSize={24}/>
                                                </button>
                                            </div> : <div className="btn-pwd-input">
                                                <button style={{marginLeft: '5px'}} onClick={this.handlePasswordTextType}>
                                                    <Icon icon={'eye-off'} iconSize={24}/>
                                                </button>
                                            </div>  
                                        }                                        
                                        {this.state.resetPwd.length === 0 && <div><span className='errorField'>Password is required.</span></div>}
                                        
                                    </td>
                                </tr>
                            </tbody>
                        </table>
                        <button type="submit" className='addBtn' onClick={this.handleResetPwdConfirmation}>Reset</button>
                        <Dialog
                            isOpen={this.state.showResetPwdConfirmation}
                            onClose={this.handleResetPwdConfirmation}
                            title="Confirm"
                        >
                            <div className={Classes.DIALOG_BODY}>
                                <span style={{ fontSize: '15px' }}>Resetting Password for <strong>{this.state.resetPwdEmailID}</strong>. Continue?</span>
                            </div>
                            <div className={Classes.DIALOG_FOOTER}>
                                <button
                                className='nextBtn'
                                onClick={this.handleResetPwdConfirmation}
                                >
                                Cancel
                                </button>
                                <button type="button" className="nextBtn" onClick={this.handleResetPassword}>Submit</button>
                            </div>
                        </Dialog> 
                    </div>
                )
            default:
                return (
                    <div></div>
                )
        }
    }

    handlePasswordTextType = () => {
        this.setState({
            ...this.state,
            isPwdType: !this.state.isPwdType
        })
    }
    handleJiraPasswordTextType = (e) => {
        e.preventDefault()
        this.setState({
            ...this.state,
            isPwdType_JIRA: !this.state.isPwdType_JIRA
        })
    }
    handleServiceNowPasswordTextType = (e) => {
        e.preventDefault()
        this.setState({
            ...this.state,
            isPwdType_serviceNow: !this.state.isPwdType_serviceNow
        })
    }
    handleAddConfirmation() {
        this.setState({
            ...this.state,
            showAddConfirmation: !this.state.showAddConfirmation
        })
    }

    handleResetPwdConfirmation = () => {
        this.setState({
            ...this.state,
            showResetPwdConfirmation: !this.state.showResetPwdConfirmation
        })
    }
    
    handleFormChange(e, stepName) {
        e.preventDefault();
        this.setState({
            ...this.state,
            currentStep: stepName
        })
    }

    handleRemoveConfirmation() {
        this.setState({
            ...this.state,
            showRemoveConfirmation: !this.state.showRemoveConfirmation
        })
    }

    handlePrimaryTabChange(newTabId) {
        this.setState({
          ...this.state,
          primaryTab: newTabId
        })
    }
    handlePrimaryConfigTabChange(newTabId) {
        if(newTabId === 'applicationConfig') {}
        this.setState({
          ...this.state,
          primaryConfigTab: newTabId
        })
    }
    validateNumber = (e) => {

        let a = e.target.name.split('.')[0]
        let b = e.target.name.split('.')[1]
        let updatedInfo = this.state[a]      
        updatedInfo[b] = e.target.value
        
        if (e.target.value.length === 0) {
            this.setState({ 
                ...this.state, 
                [a]: updatedInfo
            });
            
        }
        else if (e.target.value.match("^([0-9]*)$")) {
            this.setState({ 
                ...this.state, 
                [a]: updatedInfo
            });
        }
    }
    handleChange(e) {

        //  Using e.target.name to determine which category & sub-category of state to be updated.

        let a = e.target.name.split('.')[0]
        let b = e.target.name.split('.')[1]

        let updatedInfo = this.state[a]      
        updatedInfo[b] = e.target.value

        this.setState({ 
            ...this.state, 
            [a]: updatedInfo
        });
    }
    handleChangeConfig = (e) => {

        let a = e.target.name
        this.setState({ 
            ...this.state, 
            [a]: e.target.value
        });
    }
    validateText(e, format) {
        
        let validateResponse = 
            e === null  || e === undefined ?  'This is a required field.' :
            e.length === 0 ? 'This is a required field.' :
            format === 'int' && e === 0 ? 'Text field cannot be of value 0' :
            format === 'email' && !e.includes('@') || format === 'email' && !e.includes('.') ?  'Please enter a valid e-mail.' :
            format === 'roles' && this.state.segmentsListLowerCase.includes(e.toLowerCase()) ? 'Segment name already exists.'
            : '';   
        return validateResponse;          
    }
    setError(d, type) {
        let validateResponse = this.validateText(d.value, type)
        let errorMessageKey = d.key + 'ErrorMessage'
        if(d.multiForm) {
            let i = d.index
            let errStatus = d.updateObj[i]['error']
            
            d.updateObj[i][errorMessageKey] = validateResponse
            d.updateObj[i]['error'] = validateResponse === '' ? false : true
            
            this.setState({
                ...this.state,
                handleStepsDisabled: validateResponse !== '' ? true : false,
                [d.category]: d.updateObj
            })
        } else {
            let errStatus = d.updateObj['error']

            d.updateObj[errorMessageKey] = validateResponse
            d.updateObj['error'] = validateResponse === '' ? false : true
            this.setState({
                ...this.state,
                handleStepsDisabled: validateResponse !== '' ? true : false,
                [d.updateCategory]: d.updateObj
            })
        }



    }
    errCheck() {
        let category = this.state.currentStep;

        if(category === 'account') {
            Object.entries(this.state.accountDetails).map((c, i) => {
                let details = {
                    id: 0,
                    key: c[0],
                    value: c[1],
                    multiForm: false,
                    category: 'accountDetails',
                    updateObj: this.state.accountDetails,
                }

                if(details.key === 'accountId') {this.setError(details, 'int')}
                if(details.key === 'accountOwnerName') {this.setError(details, '')}   
                if(details.key === 'accountType') {this.setError(details, '')}   

            })

// checks whether an element is even



        }
        if(category === 'segment') {
            this.state.clusterSegments.map((s, index) => {
                Object.entries(s).map((c, i) => {
                    let details = {
                        index: index,
                        key: c[0],
                        value: c[1],
                        multiForm: true,
                        category: 'clusterSegments',
                        updateObj: this.state.clusterSegments,
                    }
    
                    if(details.key === 'segmentName') {this.setError(details, '')}
                    if(details.key === 'segmentOwnerName')  {this.setError(details, '')}
                    if(details.key === 'ownerEmailId') {this.setError(details, 'email')}    
    
                    if(this.state.clusterSegments[index].segmentNameErrorMessage === '' && 
                    this.state.clusterSegments[index].segmentOwnerNameErrorMessage === '' && 
                    this.state.clusterSegments[index].ownerEmailIdErrorMessage === '' &&
                    !this.state.clusterSegments[index].error) {
                        details.updateObj[index].error = false
                        this.setState({
                            handleStepsDisabled: false,
                            clusterSegments: details.updateObj
                        })
                    }
                    
                    // this.handleErrorResponse('accountDetails', c, this.state.accountDetails[c], '')
                })
            })

        }
        if(category === 'testSuites') {

            if(this.state.testSuiteData[0].selected){
                let t = this.state.testSuiteData[0]
                if(t.type === 'min/max') {
                    let errMinMax = t.min > t.max ? true : false
                    let updatedObj = this.state.testSuiteData
                    updatedObj[0].criteriaMinMaxErrorMessage = errMinMax ? "'Min' value cannot be greater than 'Max' Value." : ''
                    updatedObj[0].error = errMinMax
                    this.setState({
                        handleStepsDisabled: errMinMax,
                        testSuiteData: updatedObj
                    }, () => {console.log(this.state)})
                } 
            }
            if(this.state.testSuiteData[1].selected){
                let t = this.state.testSuiteData[1]
                if(t.type === 'No. of Bootstraps') {
                    let details = {
                        index: 1,
                        key: 'bootstrapNo',
                        value: t.bootstrapNo,
                        multiForm: true,
                        category: 'testSuiteData',
                        updateObj: this.state.testSuiteData,
                    }
                    
                    this.setError(details, '')

                }
            }
                if(this.state.testSuiteData[0].criteriaMinMaxErrorMessage === '' && 
                this.state.testSuiteData[1].bootstrapNoErrorMessage === '' && 
                !this.state.testSuiteData[0].error && !this.state.testSuiteData[1].error) {
                    let updateObj = this.state.testSuiteData
    
                    this.setState({
                        handleStepsDisabled: false,
                        testSuiteData: updateObj
                    })
                }


        }
        if(category === 'application') {
            let validJira = false
            let validServiceNow = false

            if(this.state.jiraEnabled) {
                validJira = this.state.jiraEnabled && this.state.jiraPasswordErrorMessage === '' && this.state.jiraProjectsErrorMessage === '' && this.state.jiraUserErrorMessage === '' && this.state.jiraUrlErrorMessage === '' ? true : false
                validServiceNow = true
            } if(this.state.serviceNowEnabled) {
                validServiceNow = this.state.servicenowPasswordErrorMessage === '' && this.state.servicenowUserErrorMessage === '' && this.state.servicenowUrlErrorMessage === '' ? true : false
                validJira = true
            } if(!this.state.jiraEnabled && !this.state.serviceNowEnabled) {
                validJira = true
                validServiceNow = true
            }
            this.state.configDetailData.map((d, idx) => {
                Object.entries(d).map((c, i) => {
                    let details = {
                        index: idx,
                        key: 'configValue',
                        value: c[1],
                        multiForm: true,
                        category: 'configDetailData',
                        updateObj: this.state.configDetailData,
                    }
                    if(c[0] === 'configValue' && d.configDataType === 'String') {this.setError(details, '')} 
                    // if(c[0] === 'configValue' && d.configDataType === 'form' && d.configName )
                    if(this.state.configDetailData[idx].configValueErrorMessage !== '' && !validJira && !validServiceNow ) {
                            let updtObj = this.state.configDetailData
                            this.setState({
                                handleStepsDisabled: true,
                                configDetailData: updtObj
                            })
                    }
                })
            })
            if(this.state.jiraEnabled) {
                console.log('hi jira enable', this.state)
                this.setState({
                    jiraUserErrorMessage: this.state.jiraUser == '' ? 'Please fill out Jira User.' : '',
                    jiraPasswordErrorMessage: this.state.jiraPassword == '' ? 'Please fill out Jira Password.' : '',
                    jiraProjectsErrorMessage: this.state.jiraProjects == '' ? 'Please fill out Jira Projects.' : '',
                    jiraUrlErrorMessage: this.state.jiraUrl == '' ? 'Please fill out Jira Url.' : '',
                })                   
                if(this.state.jiraUrl !== '' && this.state.jiraUser !== '' && this.state.jiraPassword !== '' && this.state.jiraProjects !== '') {
                    console.log('hi pass jira', this.state)
                    this.setState({
                        jiraUrlErrorMessage: '',
                        jiraProjectsErrorMessage: '',
                        jiraPasswordErrorMessage: '',
                        jiraUserErrorMessage: ''
                    })
                }
            } if(this.state.serviceNowEnabled) {
                console.log('serv enabled', this.state)
                this.setState({
                    servicenowUserErrorMessage: this.state.servicenowUser === '' ? 'Please fill out ServiceNow User.' : '',
                    servicenowPasswordErrorMessage: this.state.servicenowPassword === '' ? 'Please fill out ServiceNow Password.' : '',
                    servicenowUrlErrorMessage: this.state.servicenowUrl === '' ? 'Please fill out ServiceNow Url.' : '',
                })      
                if(this.state.servicenowPassword !== '' && this.state.servicenowUser !== '' && this.state.servicenowUrl !== '') {
                    console.log('serv pass', this.state)
                    this.setState({
                        servicenowUrlErrorMessage: '',
                        servicenowPasswordErrorMessage: '',
                        servicenowUserErrorMessage: ''
                    })
                }
            }


        }
        if(category === 'newUser') {
            this.state.newUserData.map((s, index) => {
                Object.entries(s).map((c, i) => {
                    let details = {
                        index: index,
                        key: c[0],
                        value: c[1],
                        multiForm: true,
                        category: 'newUserData',
                        updateObj: this.state.newUserData,
                    }
    
                    if(details.key === 'newUserEmail') {this.setError(details, 'email')}
                    if(details.key === 'newUserFirstName')  {this.setError(details, '')}
                    if(details.key === 'newUserLastName') {this.setError(details, '')}    
    
                    if(this.state.newUserData[index].newUserEmailErrorMessage === '' && 
                    this.state.newUserData[index].newUserFirstNameErrorMessage === '' && 
                    this.state.newUserData[index].newUserLastNameErrorMessage === '' &&
                    !this.state.newUserData[index].error) {
                        details.updateObj[index].error = false
                        this.setState({
                            handleStepsDisabled: false,
                            newUserData: details.updateObj
                        })
                    }

                    if(this.state.newUserData[index].newUserEmailErrorMessage !== '' || 
                    this.state.newUserData[index].newUserFirstNameErrorMessage !== '' ||
                    this.state.newUserData[index].newUserLastNameErrorMessage !== '') {
                        details.updateObj[index].error = true
                        this.setState({
                            handleStepsDisabled: true,
                            newUserData: details.updateObj
                        })
                    }                   
                    
                    // this.handleErrorResponse('accountDetails', c, this.state.accountDetails[c], '')
                })
            })
        }
        if(category === 'review') {}
        if(category === 'currSegmentObj') {
            let checkList = ['configValue']
            checkList.map((c, i) => {
                
            })
        }
    }
    configCheck = () => {
        let err
        this.state.configDetailData.map((t,i) => {
            if(t.error) {
                return (
                    err = 'yes'
                )
            }
        })
        if(err = 'yes') {
            return (<div><span className='errorField'>Please fill in all fields.</span></div>)
        } else {
            return null
        }
    }
    renderDropdownCell = (param, props) => {
        let objUpdate = {}
        let idx = 0
        this.state.testSuiteData.map((p, i) => {
            if(p.id === props.dataItem.id) {
                objUpdate = p;
                idx = i;
            }
        })
        if(objUpdate.type === 'min/max') {
            if(param === 'min' || param === 'max') {
                let obj = this.state.testSuiteData[idx]

                return (
                    <td className="numeric-txt-kndo">
                        <NumericTextBox
                            min={0}
                            placeholder=""
                            value={obj[param]}
                            onChange={(e) => {this.handleChangeKendoTestSuites(e, props, param)}}
                        /> 
                    </td>

                )
            } else {
                return (
                    <td className="numeric-txt-kndo">
                        <NumericTextBox
                            disabled
                            min={0}
                            placeholder=""
                            onChange={(e) => {this.setState({})}}
                        /> 
                    </td>

                )
            }
            
        }
        if(props.dataItem.type === 'No. of Bootstraps') {
            let obj = this.state.testSuiteData[idx]
            if(param === 'bootstrapNo') {
                return (
                    <td className="numeric-txt-kndo">
                        <NumericTextBox
                            placeholder=""
                            min={0}
                            value={obj[param]}
                            onChange={(e) => {this.handleChangeKendoTestSuites(e, props, param)}}
                        /> 
                    </td>
                )
            } else {
                return (
                    <td className="numeric-txt-kndo">
                        <NumericTextBox
                            disabled
                            placeholder=""
                            onChange={(e) => {this.setState({})}}
                        /> 
                    </td>

                )
            }
        }
    }
    renderErrCheck(field, message){
        // if(field === 'addEmail') {
        //     return(
        //         {this.state.errValidation.addEmailErr ? <div><span className='errorField'>{this.state.errValidation.addEmailErrMessage}</span></div> : null}
        //         {this.props.getUserRolesSuccess && !this.props.getUserRolesData.loginRoles ? 
        //             <div><span className='errorField'>{message}</span></div> : null
        //         }
        //     )            
        // }
    }
    handleErrorResponse(category, subCategory, value, type) {

        let validateResponse = this.validateText(value, type)

        let errStateParam = subCategory + 'Error'
        let errMessageStateParam = subCategory + 'ErrorMessage'

  
        let updatedErrObj = this.state[category];
      
      // example: 
      //    this.state.errValidation[segmentOwnerNameErr] = false/true
      //    this.state.errValidation[segmentOwnerNameErrMessage] = 'This field is required.'
      //    this.state.errValidation = {segmentOwnerNameErr: false, segmentOwnerNameErrMessage: 'This field is required.'}

        updatedErrObj[errStateParam] = validateResponse === '' ? false : true
        updatedErrObj[errMessageStateParam] = validateResponse

        let catSub = category[subCategory]
        console.log('Category & subcategory', catSub)
        
        this.setState({
            ...this.state,
            category: updatedErrObj
        })
    }
    capitalize(s){
        if (typeof s !== 'string') return ''
        return s.charAt(0).toUpperCase() + s.slice(1)
    }

    validateSegments(e, id) {
        let param = 'this.state.clusterSegments[id]'
        let stateKey = this.state.clusterSegments[id]
        let stateArray = this.state.clusterSegments
        let type = 'segmentName'

        stateKey[type] = e;

        let lowerE = e.toLowerCase()
        let validateResponse = this.validateText(lowerE, 'roles')
        let isError = validateResponse === '' ? false : true
        let isExist = validateResponse === '' ? false : true
        stateKey.error = isError
        stateKey['segmentNameErrorMessage'] = validateResponse

        this.setState({
            ...this.state,
            handleStepsDisabled: validateResponse !== '' ? true : false,
            [param]: stateKey
        })

    }
    selectedSegement = (e, id) => {
        let param = 'this.state.clusterSegments[id]'
        let stateKey = this.state.clusterSegments[id]
        let stateArray = this.state.clusterSegments
        let type = 'segmentName'

        stateKey[type] = e;

        let lowerE = e.toLowerCase()
        let validateResponse = this.validateText(lowerE, 'roles')
        let isExist = validateResponse === '' ? false : true
        const found = this.state.segmentsListUser.find((u, i) => u.label.toLowerCase() == lowerE)
        stateKey.isExist = isExist
        stateKey.ownerEmailId = isExist === true ? found.businessOwnerEmail : ''
        stateKey.segmentOwnerName = isExist === true ? found.businessOwner : ''
        this.setState({
            ...this.state,
            [param]: stateKey
        })
    }
    retrieveRoles(type) {
        if(type === 'add') {
            let validateResponse = this.validateText(this.state.addemailID, 'email')
            this.setState({
                ...this.state,
                errValidation: {
                    addEmailErr: validateResponse === '' ? false : true,
                    addEmailErrMessage: validateResponse
                }
            })
            if(validateResponse === '') {
                this.props.fetchUserRoles(this.state.addemailID, this.props.token)
            }
        } else if(type === 'remove') {
            let validateResponse = this.validateText(this.state.removeemailID, 'email')
            this.setState({
                ...this.state,
                errValidation: {
                    removeEmailErr: validateResponse === '' ? false : true,
                    removeEmailErrMessage: validateResponse
                }
            })
            if(validateResponse === '') {
                this.props.fetchUserRoles(this.state.removeemailID, this.props.token)
            }
        } else if (type === 'reset') {
            let validateResponse = this.validateText(this.state.resetPwdEmailID, 'email')
            this.setState({
                ...this.state,
                errValidation: {
                    resetPwdEmailErr: validateResponse === '' ? false : true,
                    resetPwdEmailErrMessage: validateResponse
                }
            })
            if(validateResponse === '') {
                this.props.fetchUserRoles(this.state.resetPwdEmailID, this.props.token)
            }
        }
    }

    handlePassword(e) {
        this.setState({
            ...this.state,
            resetPwd: e.target.value
        })
    }

    handleEmailID = (e, type) => {
        if(type === 'add') {
            this.setState({
                ...this.state,
                addemailID: e.target.value
            })
            this.retrieveRoles('add')
        } else if (type === 'remove') {
            this.setState({
                ...this.state,
                removeemailID: e.target.value
            })
            this.retrieveRoles('remove')
        } else {
            this.setState({
                ...this.state,
                resetPwdEmailID: e.target.value
            })
            this.retrieveRoles('reset')
        }
    }
    handleActionBtnClick(action) {
        this.setState({
          ...this.state,
          primaryAction: action
        })
    }
    
    
    handleFirstName(e, type) {
        if (type === 'add') {
            this.setState({
                ...this.state,
                addfname: e.target.value
            })
        } else {
            this.setState({
                ...this.state,
                removefname: e.target.value
            })
        }
        
    }

    handleLastName(e, type) {
        if (type === 'add') {
            this.setState({
                ...this.state,
                addlname: e.target.value
            })
        } else {
            this.setState({
                ...this.state,
                removelname: e.target.value
            })
        }
        
    }


    handleAddRoles() {
        this.setState({
            ...this.state,
            showAddConfirmation: !this.state.showAddConfirmation
        })
    }

    handleRolesData = (array) => {
        let newRolesArray = [];
        if (this.props.getUserRolesData.loginRoles.services !== undefined) {
            this.props.getUserRolesData.loginRoles.services.map((a, i) => {
                if(a.roles.length) { 
                    a.roles.map((r, i) => {
                        r.serviceType = a.serviceType
                        newRolesArray.push(r);
                    })
                    
                }
                return a;
            })
        }
        
        return newRolesArray
    }
    
    handleAddSubmit = (e) => {
        let apiRoles = []
        this.state.addactions.push("Read")
        this.state.addservices.forEach((service, index) => {
            this.state.addsegments.forEach((segment, index1) => {
                this.state.addactions.forEach((action, index2) => {
                    this.state.addaccounts.forEach((act, index3) => {
                    let roles = {
                        "serviceName": service,
                        "awsAccountName": act,
                        "segmentName": segment,
                        "roleName": action
                    }
                    apiRoles.push(roles)
                })
            })
        })
        this.props.postAddRoles({
            "email": this.state.addemailID,
            "roles": apiRoles,
            "firstName": this.state.addfname,
            "lastName": this.state.addlname
        }, this.props.token)
    })
        this.setState({
            ...this.state,
            spinner: true,
            showAddConfirmation: false
        })
    }

    handleResetPassword = () => {
        let data = {
            email: this.state.resetPwdEmailID,
            newPassword: this.state.resetPwd
        }
        this.props.resetUserPassword(data, this.props.token)
        this.setState({
            ...this.state,
            showResetPwdConfirmation: false,
            resetPwdEmailID: '',
            resetPwd: ''
        })
    }

    handleRemoveRoles = () => {
        this.setState({
            ...this.state,
            showRemoveConfirmation: !this.state.showRemoveConfirmation
        })
    }

    handleRemoveSubmit = (e) => {
        let apiRoles = []
        this.state.removeservices.forEach((service, index) => {
            this.state.removesegments.forEach((segment, index1) => {
                this.state.removeactions.forEach((action, index2) => {
                    this.state.removeaccounts.forEach((act, index3) => {
                    let roles = {
                        "serviceName": service,
                        "awsAccountName": act,
                        "segmentName": segment,
                        "roleName": action
                    }
                    apiRoles.push(roles)
                  })
              })
          })
          this.props.postRemoveRoles({
              "email": this.state.removeemailID,
              "roles": apiRoles
          }, this.props.token)
        })
        this.setState({
            ...this.state,
            spinner: true,
            showRemoveConfirmation: false
        })
    }
    expandChange = (event) => {
        event.dataItem.expanded = event.value;
        this.setState({ ...this.state });
  
  
        if (!event.value || event.dataItem.details) {
          return;
        }
      }
    renderUserRoles = () => {
        return(
            <div style={{display: 'inline-block'}}>
                <h3>User Access</h3>
                <StatefulGrid
                data={this.handleRolesData(this.props.getUserRolesData.loginRoles.services)}
                resizable
                reorderable={true}
                pageable={this.props.getUserRolesData.loginRoles.services.length > 10 ? true : false}
                detail={(props) => <DCCSegmentGrid {...props} dccDetails={this.props.getUserRolesData.loginRoles.services} />}
                expandField="expanded"
                onExpandChange={this.expandChange}
                {...this.state}
                >
                    <Column field="serviceType" title={'Service Type'}  />
                    <Column field="name" title={'Role'} cell={(props) => <td><span>{props.dataItem.name}</span></td>}/>
                </StatefulGrid>
            </div>           
        )
    }

  ////////////////////////////////////////////////////////////
  //              #Account-Setup Functions                  //
  ////////////////////////////////////////////////////////////



    handleRemoveSegment = () => {
        let nameError = false;
        let jarError = false;
        let argsError = false;
        if (this.state.segments.length === 1) {
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
    handleRemoveNewUser = () => {
        this.setState({
            ...this.state,
            newUserData: this.state.newUserData.slice(0,-1),
        })
    }

    handleToggleEncrypt = (id, val) => {
        var newObj = this.state.configDetailData;
        let i = this.state.configDetailData.findIndex(c => c.configId == id);
        newObj[i].isEncrypted = !val

        this.setState({
            ...this.state,
            configDetailData:  newObj
        }, () => console.log('togled', this.state))

    }
    handleToggleConfigSwitch = (id, val) => {
        var newObj = this.state.configDetailData;
        let i = this.state.configDetailData.findIndex(c => {
            let idval = c.configId + '_config'
            return idval === id
        });

        let jira_index = this.state.configDetailData.findIndex(c => {
            return c.configName === 'jira_enabled_account'
        });
        let service_index = this.state.configDetailData.findIndex(c => {
            return c.configName === 'servicenow_enabled_account'
        });


        if(newObj[i].configName === 'jira_enabled_account') {
            newObj[i].configValue = !val.configValue
            newObj[service_index].configValue = false
            
            this.setState({
                ...this.state,
                configDetailData: newObj,
                currType:  newObj[i].configValue == false ? '' : 'jira',
                jiraEnabled: newObj[i].configValue,
                jiraPassword: '',
                jiraPasswordErrorMessage: '',
                jiraUrl: '',
                jiraUrlErrorMessage: '',
                jiraUser: '', 
                jiraUserErrorMessage: '',
                jiraProjects: '',
                jiraProjectsErrorMessage: '',
                servicenowPassword: '',
                servicenowPasswordErrorMessage: '',
                servicenowUrl: '',
                servicenowUrlErrorMessage: '',
                servicenowUser: '', 
                servicenowUserErrorMessage: '',
                serviceNowEnabled: newObj[service_index].configValue 
            }, () => console.log('togled config', this.state))
        } else if(newObj[i].configName === 'servicenow_enabled_account') {
            newObj[i].configValue = !val.configValue
            newObj[jira_index].configValue = false
            this.setState({
                ...this.state,
                configDetailData:  newObj,
                currType:  newObj[i].configValue == false ? '' : 'serviceNow',
                jiraPassword: '',
                jiraPasswordErrorMessage: '',
                jiraUrl: '',
                jiraUrlErrorMessage: '',
                jiraUser: '', 
                jiraUserErrorMessage: '',
                jiraProjects: '',
                jiraProjectsErrorMessage: '',
                servicenowPassword: '',
                servicenowPasswordErrorMessage: '',
                servicenowUrl: '',
                servicenowUrlErrorMessage: '',
                servicenowUser: '', 
                servicenowUserErrorMessage: '',
                serviceNowEnabled: newObj[i].configValue,
                jiraEnabled: newObj[jira_index].configValue 
            }, () => console.log('togled config', this.state))
        }


    }
    handleToggleConfigSwitchy = (id, val) => {
        console.log('jio', id, val, this.state)
        var newObj = this.state.configDetailData;
        let i = this.state.configDetailData.findIndex(c => {
            let idval = c.configId + '_config'
            return idval === id
        });
        newObj[i].configValue = !val.configValue
        this.setState( {
            ...this.state,
            configDetailData:  newObj
        }, () => console.log('togled config', this.state))

    }
   selectionChange = (event) => {
        const data = this.state.testSuiteData.map(item=>{
            if(item.id === event.dataItem.id){
                item.selected = !event.dataItem.selected;
            }
            return item;
        });
        this.setState({ 
            ...this.state,
            testSuiteData: data
         });
    }
    
    rowClick = (event) => {};

    headerSelectionChange = (event) => {
        const checked = event.syntheticEvent.target.checked;
        const data = this.state.testSuiteData.map(item=>{
            item.selected = checked;
            return item;
        });
        this.setState({ testSuiteData: data });
    }
    expandChanges = (event) => {
        event.dataItem.expanded = event.value;
        this.setState({ ...this.state });
  
  
        if (!event.value || event.dataItem.details) {
          return;
        }
    }
    expandChange = (event) => {
        console.log('event', event)
        event.dataItem.expanded = event.value;
        this.setState({ ...this.state });
  
  
        if (!event.value || event.dataItem.details) {
          return;
        }
    }
    handleAddSegment = () => {
        const newSegment = {
            id: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15),
            segmentName: '',
            segmentOwnerName: '',
            ownerEmailId: '',
            segmentNameErrorMessage: '',
            segmentOwnerNameErrorMessage: '',
            ownerEmailIdErrorMessage: '',
            isExist: false,
            error: true
        };
        const segmentDetailArray = this.state.clusterSegments;
        const newArray = segmentDetailArray.concat(newSegment)
        this.setState({
              clusterSegments: newArray
        })
        this.updateUserSegmentList()
    }
    handleAddNewUser = () => {
        const newUser = {
            id: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15),
            newUserEmail: '',
            newUserFirstName: '',
            newUserLastName: '',
            newUserAccountId: '',
            newUserSegments: [],
            newUserActions: [this.state.actions[0]],
            newUserEmailErrorMessage: '',
            newUserFirstNameErrorMessage: '',
            newUserLastNameErrorMessage: '',
            newUserAccountIdErrorMessage: '',
            newUserSegmentsErrorMessage: '',
            newUserActionsErrorMessage: '',
            error: false
        };

        const userArray = this.state.newUserData;
    
        const newArray = userArray.concat(newUser)


        this.setState({
              newUserData: newArray,
              showUserForm: true
        })
    }
    checkError = (section) => {
        Object.keys(this.state.errValidation).map(x => {

            if(x.includes('Message')) {} 
            else {
                console.log('err VAl:', this.state.errValidation[x])
                if(this.state.errValidation[x]) {
                }
            }
        })
    }
    handleUserChange = (e, id, type, eventType) => {

        let param = 'this.state.newUserData[id]'
        let stateKey = this.state.newUserData[id]
        let stateArray = this.state.newUserData
        if(eventType === 'add') {
            if(type === 'newUserActions' || type === 'newUserSegments') { 
                let actions = [];
                if(e !== null) {
                    let filteredArr = e.filter((f) => f !== undefined && f !== null)
                    filteredArr.forEach(x => {actions.push(x)})
                }
                stateKey[type] = actions
    
                this.setState({
                    [param]: stateKey
                })
            } else {
                e.preventDefault();

                stateKey[type] = e.target.value;
    
                this.setState({
                    [param]: stateKey
                })
            }

        } if(eventType === 'remove') { 
            let a1 = this.state.newUserData
            let a2 = a1.pop()
            this.setState({
                newUserData: a1
            })          
        }

    }
    handleSegmentChange = (e, id, type, event) => {
        e.preventDefault();

        let param = 'this.state.clusterSegments[id]'
        let stateKey = this.state.clusterSegments[id]
        let stateArray = this.state.clusterSegments
        if(event === 'add') {
            if(type === 'segmentName') {
                stateKey[type] = e.target.value;
                this.setState({
                    [param]: stateKey
                })
                
                this.selectedSegement(e.target.value, id)
            } else {
                stateKey[type] = e.target.value;
                this.setState({
                    [param]: stateKey
                })
                this.updateUserSegmentList()
            }

        } if(event === 'remove') { 
            let a1 = this.state.clusterSegments
            let a2 = a1.pop()
            this.setState({
                clusterSegments: a1
            })          
        }

    }

    handleChangeKendo = (e, params) => {
        params.dataItem.configValue = e.target.value
        // [configDetailData[i].configValue]: e.target.value
        this.setState({
            configDetailData: this.state.configDetailData.map(item =>
                item.configId === params.dataItem.configId ?
                { ...item, inEdit: true } : item
            )
        });
    }
    handleChangeKendoTestSuites = (e, params, type) => {
        params.dataItem[type] = e.target.value
        // [configDetailData[i].configValue]: e.target.value
        this.setState({
            testSuitesData: this.state.configDetailData.map(item =>
                item.id === params.dataItem.id ?
                { ...item, inEdit: true } : item
            )
        });

    }
    formatUserData = (data) => {
        const servicesArr =[{
        label: 'EMR',
        value: 'emr'
    }]
    
        let apiRoles = []
        servicesArr.forEach((service, index) => {
            data.newUserSegments.forEach((segment, index1) => {
                data.newUserActions.forEach((action, index2) => {
                    data.newUserAccountId.forEach((act, index3) => {
                    let roles = {
                        "serviceName": `${service.value}`,
                        "awsAccountName": act,
                        "segmentName": `${segment}`,
                        "roleName": action
                    }
                    apiRoles.push(roles)
                })
            })
        })
    })
    let dataObj = {
        "email": data.newUserEmail,
        "roles": apiRoles,
        "firstName": data.newUserFirstName,
        "lastName": data.newUserLastName
    }
    return dataObj;
    }
    formatData(e) {
        e.preventDefault();

        // Formatting Account 
        let accountFin = {
            accountId: this.state.accountDetails['accountId'],
            accountEnv: this.state.accountDetails['accountType'],
            accountOwner: this.state.accountDetails['accountOwnerName']
        }            
        // Formatting Segments

        let segmentsFin = this.state.clusterSegments.map((s, i) => {
            return {
                segmentName: s['segmentName'],
                businessOwner: s['segmentOwnerName'],
                businessOwnerEmail: s['ownerEmailId']
            }
        })
    // Formatting new User obj

        let newUserObjFin = this.state.newUserData.map((u, i) => {
            let actionFin = [];
            let segmentFin = []
            u.newUserActions.forEach(n => actionFin.push(n.value))
            u.newUserSegments.forEach(n => segmentFin.push(n.value))
            return {
                newUserEmail: u['newUserEmail'],
                newUserFirstName: u['newUserFirstName'],
                newUserLastName: u['newUserLastName'],
                newUserAccountId: [this.state.accountDetails.accountId],
                newUserSegments: segmentFin,
                newUserActions: actionFin
            }
        })


        let userFin = newUserObjFin.map((u, i) => {
            let userObj = this.formatUserData(u);
            return userObj
        })
        let jiraArray = []
        if(this.state.jiraEnabled) {
            jiraArray = [
                {configName: 'jira_user', configValue: this.state.jiraUser, accountId: this.state.accountDetails.accountId, isEncrypted: false},
                {configName: 'jira_password', configValue: this.state.jiraPassword, accountId: this.state.accountDetails.accountId, isEncrypted: false},
                {configName: 'jira_url', configValue: this.state.jiraUrl, accountId: this.state.accountDetails.accountId, isEncrypted: false},
                {configName: 'jira_projects', configValue: this.state.jiraProjects, accountId: this.state.accountDetails.accountId, isEncrypted: false}
            ]
        }

        let servicenowArray = []
        if(this.state.serviceNowEnabled) {
            servicenowArray = [
                {configName: 'servicenow_user', configValue: this.state.servicenowUser, accountId: this.state.accountDetails.accountId, isEncrypted: false},
                {configName: 'servicenow_password', configValue: this.state.servicenowPassword, accountId: this.state.accountDetails.accountId, isEncrypted: false},
                {configName: 'servicenow_url', configValue: this.state.servicenowUrl, accountId: this.state.accountDetails.accountId, isEncrypted: false}
            ]
        }

        let configArray = this.state.configDetailData.map((c, i) => {
            let value = c.configValue
            if(this.state.serviceNowEnabled) {
                if(c.configName === 'jira_user' || c.configName === 'jira_password' || c.configName === 'jira_url' || c.configName === 'jira_projects') {
                    return null
                } else {
                    if(c.configName === 'servicenow_user') {value = this.state.servicenowUser}
                    if(c.configName === 'servicenow_url') {value = this.state.servicenowUrl}
                    if(c.configName === 'servicenow_password') {value = this.state.servicenowPassword}
                    let isThisAllowed = c.configName === 'jira_enabled_account' || c.configName === 'servicenow_enabled_account' || c.configName === 'jira_user' || c.configName === 'jira_password' || c.configName === 'jira_url' || c.configName === 'jira_projects' || c.configName === 'servicenow_user' || c.configName === 'servicenow_url' || c.configName === 'servicenow_password' ? false : true
                    return {
                        configName: c.configName,
                        isEncrypted: isThisAllowed,
                        accountId: this.state.accountDetails.accountId,
                        configValue: value
                    }
                }
            } if(this.state.jiraEnabled) {
                if(c.configName === 'servicenow_user' || c.configName === 'servicenow_url' || c.configName === 'servicenow_password') {
                    return null
                } else {
                    if(c.configName === 'jira_user') {value = this.state.jiraUser}
                    if(c.configName === 'jira_password') {value = this.state.jiraPassword}
                    if(c.configName === 'jira_url') {value = this.state.jiraUrl}
                    if(c.configName === 'jira_projects') {value = this.state.jiraProjects}
                    let isThisAllowed = c.configName === 'jira_enabled_account' || c.configName === 'servicenow_enabled_account' || c.configName === 'jira_user' || c.configName === 'jira_password' || c.configName === 'jira_url' || c.configName === 'jira_projects' || c.configName === 'servicenow_user' || c.configName === 'servicenow_url' || c.configName === 'servicenow_password' ? false : true
                    return {
                        configName: c.configName,
                        isEncrypted: isThisAllowed,
                        accountId: this.state.accountDetails.accountId,
                        configValue: value
                    }
                }

            } if(!this.state.jiraEnabled && !this.state.serviceNowEnabled) {
                if(c.configName === 'servicenow_user' || c.configName === 'servicenow_url' || c.configName === 'servicenow_password' || c.configName === 'jira_user' || c.configName === 'jira_password' || c.configName === 'jira_url' || c.configName === 'jira_projects') {
                    return null
                } else {
                    if(c.configName === 'jira_user') {value = this.state.jiraUser}
                    if(c.configName === 'jira_password') {value = this.state.jiraPassword}
                    if(c.configName === 'jira_url') {value = this.state.jiraUrl}
                    if(c.configName === 'jira_projects') {value = this.state.jiraProjects}
                    let isThisAllowed = c.configName === 'jira_enabled_account' || c.configName === 'servicenow_enabled_account' || c.configName === 'jira_user' || c.configName === 'jira_password' || c.configName === 'jira_url' || c.configName === 'jira_projects' || c.configName === 'servicenow_user' || c.configName === 'servicenow_url' || c.configName === 'servicenow_password' ? false : true
                    return {
                        configName: c.configName,
                        isEncrypted: isThisAllowed,
                        accountId: this.state.accountDetails.accountId,
                        configValue: value
                    }
                }
            }

        })
        let configFin = configArray.filter((f) => f !== undefined && f !== null)

        // Formatting Test Suites
        let testSuitesObj = this.state.testSuiteData.filter((t,i) => t.selected)
        let testSuitesFin = testSuitesObj.map((t, i) => {
            if(t.type === 'min/max'){
                let minMaxObj = `${t.min},${t.max}`;
                return {
                    name: t['testSuiteName'],
                    description: t['testSuiteDescription'],
                    criteria: minMaxObj
                }
            } else {
                return {
                    name: t['testSuiteName'],
                    description: t['testSuiteDescription'],
                    criteria: `${t['bootstrapNo']}`
                }
            }
        })
        let dataObj = {
            accountDetails: [accountFin],
            segmentDetails: segmentsFin,
            testSuitesDetails: testSuitesFin,
            configDetails: configFin,
            userDetails: userFin
        }
        
        console.log('Account Set-up Output', dataObj)
        this.setState({
            accData: dataObj
        })
        this.handleAccConfirmation()
    }
    handleAccConfirmation = () => {
        this.setState({
            showAccConfirmation: !this.state.showAccConfirmation
        })
    }
    handleSubmit = () => {
        this.props.postAccountSetup(this.state.accData, this.props.token)
        this.setState({showAccConfirmation: false, spinner: true})
    }
    handleStepsChange = (e, type) => {
        e.preventDefault();
        this.updateUserSegmentList()
        this.errCheck()
        switch(this.state.currentStep) {
            case 'account':
                if(this.state.accountDetails.accountIdErrorMessage === '' && this.state.accountDetails.accountOwnerNameErrorMessage === '' && this.state.accountDetails.accountTypeErrorMessage === ''){
                    this.setState({
                        ...this.state,
                        segmentsBtnDisabled: false,
                        currentStep: 'segment'
                    })
                } else {
                    console.log('ERR - Accounts - handleStepsChange() -->', this.state.accountDetails)
                }
                
                break;
            case 'segment':
                let valid = false;
                let errExist = false;
                this.state.clusterSegments.map((c, i) => {
                    if(!c.error && c.segmentNameErrorMessage === '') {
                        valid = true;
                        console.log('no err')
                    } else {
                        console.log('c')
                        errExist = true
                    }
                })
                if(valid && !errExist) {
                    this.setState({
                        ...this.state,
                        testSuitesBtnDisabled: type === 'next' ? false : this.state.testSuitesBtnDisabled,
                        currentStep: type === 'next' ? 'testSuites' : 'account'
                    })
                }
                console.log('ERR - Segments - handleStepsChange()')
                break;
            case 'testSuites':

                if(this.state.testSuiteData[0].criteriaMinMaxErrorMessage === '' && 
                this.state.testSuiteData[1].bootstrapNoErrorMessage === '' && 
                !this.state.testSuiteData[0].error && !this.state.testSuiteData[1].error) {
                    this.setState({
                        ...this.state,
                        applicationSetUpBtnDisabled: type === 'next' ? false : this.state.applicationSetUpBtnDisabled,
                        currentStep: type === 'next' ? 'application' : 'segment'
                    })
                }
                break;
             case 'application':
                let validJira = false
                let validServiceNow = false
                valid = [];
                this.state.configDetailData.map((d, i) => {
                    if(!d.error) {
                        
                    } else {
                        valid.push(d)
                    }
                })
                
                if(this.state.jiraEnabled) {
                    validJira = this.state.jiraEnabled && this.state.jiraUser !== '' && this.state.jiraPassword !== '' && this.state.jiraProjects !== '' && this.state.jiraUrl !== '' && this.state.jiraPasswordErrorMessage === '' && this.state.jiraProjectsErrorMessage === '' && this.state.jiraUserErrorMessage === '' && this.state.jiraUrlErrorMessage === '' ? true : false
                    validServiceNow = true
                } if(this.state.serviceNowEnabled) {
                    validServiceNow = this.state.serviceNowEnabled && this.state.servicenowPassword !== ''  && this.state.servicenowUrl !== '' && this.state.servicenowUser !== '' && this.state.servicenowPasswordErrorMessage === '' && this.state.servicenowUserErrorMessage === '' && this.state.servicenowUrlErrorMessage === '' ? true : false
                    validJira = true
                } if(!this.state.jiraEnabled && !this.state.serviceNowEnabled) {
                    validJira = true
                    validServiceNow = true
                }
                if(valid.length === 0 && validJira && validServiceNow) {
                    this.setState({
                        ...this.state,
                        newUserBtnDisabled: type === 'next' ? false : this.state.newUserSetUpBtnDisabled,
                        currentStep: type === 'next' ? 'newUser' : 'testSuites'
                    })
                }

                break;           
            case 'newUser':
                

                if(this.state.newUserData.length > 0) {
                    let valid = false;
                    let errExist = false;
                    this.state.newUserData.map((c, index) => {
                        if(this.state.newUserData[index].newUserEmailErrorMessage === '' && 
                        this.state.newUserData[index].newUserFirstNameErrorMessage === '' && 
                        this.state.newUserData[index].newUserLastNameErrorMessage === '' &&
                        !this.state.newUserData[index].error) {
                            valid = true;
                        }
                        if(!c.error) {
                            valid = true;
                        } else {
                            valid = false;
                            errExist = true;
                        }
                    })
                    if(valid && !errExist) {
                        this.setState({
                            ...this.state,
                            currentStep: type === 'next' ? 'review' : 'application',
                            reviewBtnDisabled: type === 'next' ? false : this.state.reviewBtnDisabled
                        })  
                    }
                }
                if(this.state.newUserData.length === 0) {
                    this.setState({
                        ...this.state,
                        currentStep: type === 'next' ? 'review' : 'application',
                        reviewBtnDisabled: type === 'next' ? false : this.state.reviewBtnDisabled
                    })  
                }
                break;
            case 'review': 
                this.setState({
                    ...this.state,
                    currentStep: type === 'back' ? 'newUser' : this.state.currentStep
                })
                break;
        }
    }
    renderJiraService = (type) => {
        if(this.state.currType === 'serviceNow') {
            return (
                <div className='clusterField'>
                 <div style={{display: "inline-block", fontWeight: '600', fontSize: '15px'}}>Servicenow Config Details</div>
                <div>
                <label className='formField-2'>
                    <span className="required">Servicenow User:</span>
                    <i className='m-icons'>help</i>
                    <div className="txt-field-container">
                    <input type="text" min="0" step="1" name="servicenowUser" className="txt-field-100-f" placeholder="ServiceNow User" value={this.state.servicenowUser} onChange={this.handleChangeConfig} />
                    {this.state.servicenowUserErrorMessage !== '' ? <div><span className='errorField'>{this.state.servicenowUserErrorMessage}</span></div> : null}
                    </div>

                </label>
                
                <label className='formField-2'>
                    <span className="required">Servicenow Password:</span>
                    <i className='m-icons'>help</i>
                    <div className="txt-field-container">
                    <input style={{display: 'inline-block'}} type={this.state.isPwdType_serviceNow ? "password" : "text"} className="pwd-input-conf" name="servicenowPassword" placeholder="ServiceNow Password" value={this.state.servicenowPassword} onChange={this.handleChangeConfig} />
                    {this.state.isPwdType_serviceNow ?
                        <div className="btn-pwd-input">
                            <button style={{marginLeft: '5px'}} onClick={this.handleServiceNowPasswordTextType}>
                                <Icon icon={'eye-on'} iconSize={24}/>
                            </button>
                        </div> : <div className="btn-pwd-input">
                            <button style={{marginLeft: '5px'}} onClick={this.handleServiceNowPasswordTextType}>
                                <Icon icon={'eye-off'} iconSize={24}/>
                            </button>
                        </div>  
                    } 
                    {this.state.servicenowPasswordErrorMessage !== '' ? <div><span className='errorField'>{this.state.servicenowPasswordErrorMessage}</span></div> : null}
                    </div>


                </label>
                <label className='formField-2'>
                    <span className="required">Servicenow URL:</span>
                    <i className='m-icons'>help</i>
                    <div className="txt-field-container">
                    <input type="text" name="servicenowUrl" className="txt-field-100-f" placeholder="ServiceNow URL" value={this.state.servicenowUrl} onChange={this.handleChangeConfig} />
                    {this.state.servicenowUrlErrorMessage !== '' ? <div><span className='errorField'>{this.state.servicenowUrlErrorMessage}</span></div> : null}
                    </div>
                </label>
                </div>
            </div>
            
            )
        }
        if(this.state.currType === 'jira') {
            let list = {

            }
            return (
                <div className='clusterField'>
                    <div style={{display: "inline-block", fontWeight: '600', fontSize: '15px'}}>Jira Config Details</div>
                <div>
                <label className='formField'>
                    <span className="required">Jira Projects:</span>
                    <i className='m-icons'>help</i>
                    <div className="txt-field-container">
                    <input type="text" min="0" step="1" name="jiraProjects" className="txt-field-100-f" placeholder="Jira Projects" value={this.state.jiraProjects} onChange={this.handleChangeConfig} />
                    {this.state.jiraProjectsErrorMessage !== '' ? <div><span className='errorField'>{this.state.jiraProjectsErrorMessage}</span></div> : null}
                    </div>

                </label>
                
                <label className='formField-0'>
                    <span className="required">Jira User:</span>
                    <i className='m-icons'>help</i>
                    <div className="txt-field-container">
                    <input type="text" name="jiraUser" className="txt-field-100-f" placeholder="Owner Name" value={this.state.jiraUser} onChange={this.handleChangeConfig} />
                    {this.state.accountDetails.jiraUserErrorMessage !== '' ? <div><span className='errorField'>{this.state.jiraUserErrorMessage}</span></div> : null}
                    </div>


                </label>
                <label className='formField-0'>
                    <span className="required">Jira Password:</span>
                    <i className='m-icons'>help</i>
                    <div className="txt-field-container">
                    <input style={{display: 'inline-block'}} type={this.state.isPwdType_JIRA ? "password" : "text"} className="pwd-input-conf" name="jiraPassword" placeholder="Jira Password" value={this.state.jiraPassword} onChange={this.handleChangeConfig} />
                    {this.state.isPwdType_JIRA ?
                        <div className="btn-pwd-input">
                            <button style={{marginLeft: '5px'}} onClick={this.handleJiraPasswordTextType}>
                                <Icon icon={'eye-on'} iconSize={24}/>
                            </button>
                        </div> : <div className="btn-pwd-input">
                            <button style={{marginLeft: '5px'}} onClick={this.handleJiraPasswordTextType}>
                                <Icon icon={'eye-off'} iconSize={24}/>
                            </button>
                        </div>  
                    } 
                    {this.state.jiraPasswordErrorMessage !== '' ? <div><span className='errorField'>{this.state.jiraPasswordErrorMessage}</span></div> : null}
                    </div>


                </label>
                <label className='formField-0'>
                    <span className="required">Jira Url:</span>
                    <i className='m-icons'>help</i>
                    <div className="txt-field-container">
                    <input type="text" name="jiraUrl" className="txt-field-100-f" placeholder="Jira Url" value={this.state.jiraUrl} onChange={this.handleChangeConfig} />
                    {this.state.jiraUrlErrorMessage !== '' ? <div><span className='errorField'>{this.state.jiraUrlErrorMessage}</span></div> : null}
                    </div>


                </label>
            <br/>

                </div>
            </div>
            )
        }
    }

    renderAccountOnboarding = (stepName) => {
        switch(stepName) {
            case 'account':
                return (
                    <div className='clusterField'>
                        <div>
                        <label className='formField'>
                            <span className="required">Account ID:</span>
                            <i className='m-icons'>help</i>
                            <div className="txt-field-container">

                            </div>
                            <input type="text" min="0" step="1" name="accountDetails.accountId" className="txt-field-100" placeholder="Account ID" value={this.state.accountDetails.accountId} onChange={this.validateNumber} />
                            {this.state.accountDetails.accountIdErrorMessage !== '' ? <div><span className='errorField'>{this.state.accountDetails.accountIdErrorMessage}</span></div> : null}
                        </label>
                        
                        <label className='formField-0'>
                            <span className="required">Owner Name:</span>
                            <i className='m-icons'>help</i>
                            <div className="txt-field-container">
                            <input type="text" name="accountDetails.accountOwnerName" className="txt-field-100-e" placeholder="Owner Name" value={this.state.accountDetails.accountOwnerName} onChange={this.handleChange} />
                            {this.state.accountDetails.accountOwnerNameErrorMessage !== '' ? <div><span className='errorField'>{this.state.accountDetails.accountOwnerNameErrorMessage}</span></div> : null}
                            </div>


                        </label>
                    <br/>
                        <label className='formField-1'>
                        <span className="required">Account Environment:</span>
                        <i className='m-icons'>help</i>
                                                    <div className="txt-field-container">
                        <select className="combobox-dropdown-admin-env" name="accountDetails.accountType" onChange={this.handleChange}>
                                {
                                    this.state.accountTypeOptions.map((v, i) => <option key={i} value={v} selected={this.state.accountDetails.accountType === v}>{v}</option>)
                                }
                        </select> 
                        {this.state.accountDetails.accountTypeErrorMessage !== '' ? <div><span className='errorField'>{this.state.accountDetails.accountTypeErrorMessage}</span></div> : null}
                       </div>
                        </label>
                        <button disabled={this.handleStepsDisabled} className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'next')}>Next</button>
                        </div>
                    </div>
                    
                )
            case 'segment':
                return (
                    <div className='clusterField'>
                        <div>
                        {this.renderSegmentForm()}
                    <br/>
                    <Fab color='primary' onClick={this.handleAddSegment}>
                                <AddIcon />
                    </Fab>
                            {this.state.clusterSegments.length > 1 && 
                                <Fab style={{ marginLeft: '20px' }} color='secondary' onClick={(e) => this.handleSegmentChange(e, '', 'segmentName', 'remove')}>
                                    <DeleteIcon />
                                </Fab>
                            }
                           
                        <button disabled={this.handleStepsDisabled} className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'next')}>Next</button>
                        <button className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'back')}>Back</button>
                        </div>
                    </div>
                    
                )
            case 'testSuites':
                    let TITLE_NAME_T = <span style={{ fontSize: '15px', fontWeight: '600' }}>Test Suite Name</span>
                    let TITLE_DESCRIPTION_T = <span style={{ fontSize: '15px', fontWeight: '600' }}>Test Suite Description</span>
                    let TITLE_TYPE_T = <span style={{ fontSize: '15px', fontWeight: '600' }}>Cluster Type</span>
                    let TITLE_SEGMENT_T = <span style={{ fontSize: '15px', fontWeight: '600' }}>Segments</span>
                    let TITLE_CRITERIA_T = <span style={{ fontSize: '15px', fontWeight: '600' }}>Criteria</span>
                    let TITLE_NO_BOOTSTRAP_T = <span style={{ fontSize: '15px', fontWeight: '600' }}>No. of Bootstraps</span>

                    let TITLE_MIN_T = <span style={{ fontSize: '15px', fontWeight: '600' }}>Min</span>
                    let TITLE_MAX_T = <span style={{ fontSize: '15px', fontWeight: '600' }}>Max</span>
                    let TITLE_BOOT_T = <span style={{ fontSize: '15px', fontWeight: '600' }}>No. of Bootstraps</span>
                return (
                    <div className='clusterField' style={{marginLeft: '0%', marginRight: "0%"}}>
                        <div >
                        <StatefulGrid
                            data={this.state.testSuiteData}
                            resizable
                            reorderable={true}
                            filterable={false}
                            editField="inEdit"
                            pageable={this.state.segmentsList.length > 10 ? true : false}
                            selectedField="selected"
                            onSelectionChange={this.selectionChange}
                            onHeaderSelectionChange={this.headerSelectionChange}
                            onRowClick={this.rowClick}
                            onItemChange={this.itemChangeTestSuite}
                            {...this.state}
                        >
                            <Column field="selected" width="50px" headerSelectionValue={this.state.testSuiteData.findIndex(dataItem => dataItem.selected === false) === -1} />
                            <Column field="testSuiteName" width="200px" title={TITLE_NAME_T} cell={(props) => <td><span>{props.dataItem.testSuiteName}</span></td>}  />
                            <Column field="testSuiteDescription" width="300px" title={TITLE_DESCRIPTION_T} cell={(props) => <td><span>{props.dataItem.testSuiteDescription}</span></td>}/>
                            <Column field="type" title={TITLE_CRITERIA_T} width="115px" cell={(props) => <td><span>{props.dataItem.type}</span></td>}/>
                            <Column field="min" editor="numeric" width="110px" title={TITLE_MIN_T}  cell={(props) => this.renderDropdownCell('min', props)} />
                            <Column field="max" editor="numeric" width="110px" title={TITLE_MAX_T} cell={(props) => this.renderDropdownCell('max', props)}  />
                            <Column field="bootstrapNo" editor="numeric"  width="205px" cell={(props) => this.renderDropdownCell('bootstrapNo', props)}  title={TITLE_BOOT_T} />
                        </StatefulGrid>
                        {this.state.testSuiteData[0].criteriaMinMaxErrorMessage !== '' ? <div className="err-container"><span className='errorField'>{this.state.testSuiteData[0].criteriaMinMaxErrorMessage}</span></div> : null}
                        {this.state.testSuiteData[1].bootstrapNoErrorMessage !== '' ? <div className="err-container"><span className='errorField'>'Bootstrap No.' value cannot be null.</span></div> : null}
                    <br/>
                    {/* <Fab color='primary' onClick={this.handleAddSegment}>
                                <AddIcon />
                            </Fab>
                            {this.state.steps.length > 0 && 
                                <Fab style={{ marginLeft: '20px' }} color='secondary' onClick={this.handleRemoveSegment}>
                                    <DeleteIcon />
                                </Fab>
                            } */}
                           
                        <button disabled={this.handleStepsDisabled} className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'next')}>Next</button>
                        <button className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'back')}>Back</button>
                        </div>
                    </div>
                    
                )
            case 'application':
                let TITLE_NAME_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Name</span>
                let TITLE_VALUE_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Value</span>
                let TITLE_DESCRIPTION_A = <span style={{ fontStize: '15px', fontWeight: '600'}}>Description</span>
                let TITLE_ENCRYPT_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Encrypt API?</span> 
                let array = [{object: '1'}, {object: '2'}] 
                let arr2 = [] 
                if(this.props.getConfigDefinitionsSuccess) {
                    arr2 = this.state.configDetailData.filter((f,i) => f.configDataType !== 'Boolean' && f.configDataType !== 'form') 
                    arr2.map((l,i) => {
                        if(l.configName === 'gateway_api_key') {
                            array[1] = l
                        } else if(l.configName === 'gateway_api_url') {
                            array[0] = l
                         }
                    })
                }
                            
                return (
                    
                    <div className='clusterField' style={{marginLeft: '0%', marginRight: "0%", width: "100%"}}>

                        {
                            this.props.getConfigDefinitionsSuccess ? <div>
                            <StatefulGrid
                                data={array}
                                resizable
                                reorderable={true}
                                filterable={false}
                                onItemChange= {this.onItemChangeConfig}
                                editField="inEdit"
                                pageable={this.state.configDetailData.length > 10 ? true : false}
                                {...this.state}
                            >
                            <Column width={'300px'} height={'20px'} field="configName"  title={TITLE_NAME_A} cell={(props) => <td><span style={{wordBreak: 'break-all', fontWeight: '600'}}>{props.dataItem.configName}<span style={{color: 'red'}}>*</span></span></td>} />
                            <Column width={'374px'} height={'20px'} field="configDescription"  title={TITLE_DESCRIPTION_A} cell={(props) => <td><span style={{wordBreak: 'break-all'}}>{props.dataItem.configDescription}</span></td>} />
                            <Column width={'294px'} height={'20px'} field="configValue" editor="text" title={TITLE_VALUE_A}/>    
                            <Column width={'136px'} height={'20px'} field="encryptionRequired" title={TITLE_ENCRYPT_A} cell={(props) => <td>
                    <div className="encrypt-toggle">
                        <ToggleSlider id={props.dataItem.configId} handleToggleChange={this.handleToggleEncrypt} toggleType='encrypt' toggleOn={true} isDisabled={!props.dataItem.encryptionRequired} />
                    </div></td>} />
                            />
                        </StatefulGrid>
                        {this.state.configDetailData.map((d, i) => {
                            if(this.state.configDetailData[i].configValueErrorMessage !== '' ) {
                                return (
                                    <div><span className='errorField'>'{this.state.configDetailData[i].configName}' is a required field.</span></div>
                                )
                            }
                            else {}
                        })}
                        <div style={{width:'100%', marginTop: '10px'}}>
                        <div style={{display: "inline-block",width: "25%", height: '70px'}}>
                            <div style={{display: "inline-block", fontWeight: '600', fontSize: '11px'}}>Please select one of the following options:</div>
                        </div>                            
                        {this.state.configDetailData.map((c, i) => {
                            if(c.configDataType === 'Boolean') {
                                return (
                                        <div style={{display: "inline-block",width: "30%", height: '70px'}}>
                                            <div style={{display: "inline-block", fontWeight: '600', fontSize: '1rem'}}>{c.configName}:</div>
                                                <div style={{display: "inline-block"}}>
                                                    <ToggleSlider id={c.configId + '_config'} handleToggleChange={e => {this.handleToggleConfigSwitch(`${c.configId}_config`, c)}} toggleType='apiConfigEncrypt' toggleOn={c.configValue} onChange={e => {this.handleToggleConfigSwitch(`${c.configId}_config`, c)}} isDisabled={false} />

                                                </div>
                                        </div> 
                                )
                            }
                        }) }   
                        </div>
                        {
                            this.state.currType !== '' ? <div style={{width: '100%', height: '335px', marginTop: '10px', background: 'aliceblue', padding: '20px'}}> {this.renderJiraService('jira')}</div> : null
                        }

                    <br/>
                        <button disabled={this.handleStepsDisabled} className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'next')}>Next</button>
                        <button className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'back')}>Back</button>
                        </div> : 
                         !this.props.getConfigDefinitionsSuccess && this.props.getConfigDefinitionsError || this.props.getConfigDefinitionsFetching && this.props.getConfigDefinitionsData == undefined ? <p style={{color: 'black', fontSize: '12px'}}>There was an error loading Config Definitions. Please refresh the page.</p> :
                         !this.props.getConfigDefinitionsSuccess && this.props.getConfigDefinitionsFetching ? <div className='loader-wrapper'><div className='loader' /></div> : null
                        }

                    </div>
                    
                )
            case 'newUser':
                return (
                    <div className='clusterField'>
                        <div>
                        {this.state.newUserData.length > 0  && this.state.showUserForm && this.renderUserForm()}                       
       
                    <br/>
                    <Fab color='primary' onClick={this.handleAddNewUser}>
                                <AddIcon />
                            </Fab>
                            {this.state.newUserData.length > 0 && 
                                <Fab style={{ marginLeft: '20px' }} color='secondary' onClick={(e) => this.handleUserChange(e, '', '', 'remove')}>
                                    <DeleteIcon />
                                </Fab>
                            }
                        
                        <button disabled={false} className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'next')}>Next</button>
                        <button className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'back')}>Back</button>
                        </div>
                    </div>
                    
                )
            case 'review': 
                return (
                     <div className='review'>
                        <div className='reviewTitle'>Account Details</div>
                        <hr />
                        <div className='reviewCluster'>
                            <div className='grey'><span className='reviewTitles'>Account Id: </span><span className='reviewValue'>{this.state.accountDetails.accountId}</span></div>
                            <div className='grey'><span className='reviewTitles'>Account Type: </span><span className='reviewValue'>{this.state.accountDetails.accountType}</span></div>
                            <div className='grey'><span className='reviewTitles'>Account Owner Name: </span><span className='reviewValue'>{this.state.accountDetails.accountOwnerName}</span></div>
                        </div>
                        <div className='reviewTitle'>Segment Details</div>
                        <hr />
                        {this.state.clusterSegments.map((s, i) => {
                            return (
                                <div className='reviewCluster'>
                                    <h4>Segment #{i + 1}</h4>
                                    <div className='grey'><span className='reviewTitles'>Segment Name: </span><span className='reviewValue'>{s.segmentName}</span></div>
                                    <div className='grey'><span className='reviewTitles'>Segment Owner Name: </span><span className='reviewValue'>{s.segmentOwnerName}</span></div>
                                    <div className='grey'><span className='reviewTitles'>Business Owner Email Id: </span><span className='reviewValue'>{s.ownerEmailId}</span></div>
        
                                </div>
                            )
                        })}
                        <div className='reviewTitle'>Test Suites Details</div>
                        <hr />
                        {this.state.testSuiteData.map((t, i) => {
                            if(t.selected) {
                                return (
                                    <div className='reviewCluster'>
                                        <h4>Test Suite #{i + 1}</h4>
                                        <div className='grey'><span className='reviewTitles'>Name: </span><span className='reviewValue'>{t.testSuiteName}</span></div>
                                        <div className='grey'><span className='reviewTitles'>Description: </span><span className='reviewValue'>{t.testSuiteDescription}</span></div>
                                        <div className='grey'><span className='reviewTitles'>Type: </span><span className='reviewValue'>{t.type === 'min/max' ? 'Number' : t.type === 'No. of Bootstraps' ? 'Number' : t.type}</span></div>
                                        <div className='grey'><span className='reviewTitles'>Criteria: </span><span className='reviewValue'>{t.type}</span></div>
                                        <div className='grey'><span className='reviewTitles'>Value: </span><span className='reviewValue'>{t.type === 'min/max' ? `Min: ${t.min}, Max: ${t.max}` : t.type === 'No. of Bootstraps' ? t.bootstrapNo : ''}</span></div>
                                    </div>
                                )
                            }

                        })}

                        <div className='reviewTitle'>Config Details</div>
                        <hr />
                        {this.state.configDetailData.map((c, i) => {
                            let configObj = this.state.jiraEnabled && c.configName === 'jira_user' ? this.state.jiraUser : 
                            this.state.jiraEnabled && c.configName === 'jira_password' ?  this.state.jiraPassword :
                            this.state.jiraEnabled && c.configName === 'jira_url' ? this.state.jiraUrl :
                            this.state.jiraEnabled && c.configName === 'jira_projects' ? this.state.jiraProjects :
                            this.state.serviceNowEnabled && c.configName === 'servicenow_user' ? this.state.servicenowUser : 
                            this.state.serviceNowEnabled && c.configName === 'servicenow_url' ? this.state.servicenowUrl :
                            this.state.serviceNowEnabled && c.configName === 'servicenow_password' ? this.state.servicenowPassword :
                            this.capitalize(c.configValue.toString())
                            
                            if(!this.state.jiraEnabled && c.configName === 'jira_user' || !this.state.jiraEnabled && c.configName === 'jira_password' || !this.state.jiraEnabled && c.configName === 'jira_url' || !this.state.jiraEnabled && c.configName === 'jira_projects') {
                                return null
                            }
                            else if(!this.state.serviceNowEnabled && c.configName === 'servicenow_user' || !this.state.serviceNowEnabled && c.configName === 'servicenow_url' || !this.state.serviceNowEnabled && c.configName === 'servicenow_password') {
                                return null
                            } else {
                                return (
                                    <div className='reviewCluster'>
                                        <h4>Config #{i + 1}</h4>
                                        <div className='grey'><span className='reviewTitles'>Config Name: </span><span className='reviewValue'>{c.configName}</span></div>
                                        <div className='grey'><span className='reviewTitles'>Config Type: </span><span className='reviewValue'>{c.configValue !== configObj ? 'Account' : c.configType}</span></div>
                                        <div className='grey'><span className='reviewTitles'>Config Value: </span><span className='reviewValue'>{c.configName === 'servicenow_password'  || c.configName === 'jira_password' ? `**************${configObj.substr(configObj.length - 1)}` : configObj}</span></div>
                                        <div className='grey'><span className='reviewTitles'>Encryption Enabled: </span><span className='reviewValue'>{c.configDataType !== 'Boolean' && 'True'}</span></div>
                                    </div>
                                )
                            }

                        })}
                        <div className='reviewTitle'>New User Details</div>
                        <hr />
                        {this.state.newUserData.map((u, i) => {
                            return (
                                <div className='reviewCluster'>
                                <h4>New User #{i + 1}</h4>
                                <div className='grey'><span className='reviewTitles'>Email: </span><span className='reviewValue'>{u.newUserEmail}</span></div>
                                <div className='grey'><span className='reviewTitles'>First Name: </span><span className='reviewValue'>{u.newUserFirstName}</span></div>
                                <div className='grey'><span className='reviewTitles'>Last Name: </span><span className='reviewValue'>{u.newUserLastName}</span></div>
                                <div className='grey'><span className='reviewTitles'>Actions: </span>{u.newUserActions.length >= 7 ? <span className='reviewValue-action'>{u.newUserActions.map((n, i) => {
                                    if(i + 1 === u.newUserActions.length) {
                                        return (
                                            <span>{n.value}</span>
                                        )
                                    } else {
                                        return (
                                            <span>{n.value}, </span>
                                        )
                                    }
                                })}
                                </span> : <span className='reviewValue'>{u.newUserActions.map((n, i) => {
                                    if(i + 1 === u.newUserActions.length) {
                                        return (
                                            <span>{n.value}</span>
                                        )
                                    } else {
                                        return (
                                            <span>{n.value}, </span>
                                        )
                                    }
                                })}
                                </span>}</div>
                                <div className='grey'><span className='reviewTitles'>Segments: </span><span className='reviewValue'>{u.newUserSegments.map((n, i) => {
                                    if(i + 1 === u.newUserSegments.length) {
                                        return (
                                            <span>{n.value}</span>
                                        )
                                    } else {
                                        return (
                                            <span>{n.value}, </span>
                                        )
                                    }
                                })}
                                </span></div>
                            </div>
                            )
                        })}
                        <button className='nextBtn' onClick={(e) => this.formatData(e)}>Submit</button>
                        <button className='nextBtn' onClick={(e) => this.handleStepsChange(e, 'back')}>Back</button>
                        <Dialog
                        isOpen={this.state.showAccConfirmation}
                        onClose={this.handleAccConfirmation}
                        title="Confirm"
                    >
                        <div className={Classes.DIALOG_BODY}>
                            <span style={{ fontSize: '15px' }}>Add new Account Set-up?</span>
                            
                        </div>
                        <div className={Classes.DIALOG_FOOTER}>
                            <button
                            className='nextBtn'
                            onClick={this.handleAccConfirmation}
                            >
                            Cancel
                            </button>
                            <button type="button" className="nextBtn" onClick={this.handleSubmit}>Submit</button>
                        </div>
                    </Dialog>  
                    </div>
                   
                )   
        }
    }
    renderApplicationTable = () => {
        let TITLE_NAME_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Name</span>
        let TITLE_VALUE_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Value</span>
        let TITLE_DESCRIPTION_A = <span style={{fontSize: '15px', fontWeight: '600'}}>Description</span>
        let TITLE_ENCRYPT_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Encrypt API?</span>
        // const hasEditedItem = this.state.data.some(p => p.inEdit);

         return (
            <div className='clusterField' style={{marginLeft: '0%', marginRight: "0%"}}>
            <div className='k-grid-conf'  >
            <StatefulGrid
                data={this.state.data}
                resizable
                reorderable={true}
                filterable={false}
                onItemChange= {this.itemChange}
                editField="inEdit"
                pageable={this.state.data.length > 10 ? true : false}
                {...this.state}
            >
                <Column width={'214px'} height={'20px'} field="configName"  title={TITLE_NAME_A} cell={(props) => <td><span>{props.dataItem.configName}</span></td>} />
                <Column width={'264px'} height={'20px'} field="configDescription" editor="text" title={TITLE_DESCRIPTION_A} /> 
                <Column width={'200px'} height={'20px'} field="configValue" editor="text" title={TITLE_VALUE_A} /> 
                <Column width={'124px'} height={'20px'} field="isEncrypted" title={TITLE_ENCRYPT_A} cell={(props) =>  <td>
                    <div className="app-config-toggle">
                        <ToggleSlider id={props.dataItem.configId + '1'} handleToggleChange={this.handleToggleEncrypt} toggleType='apiConfigEncrypt' toggleOn={props.dataItem.isEncrypted} isDisabled={!props.dataItem.encryptionRequired} />
                </div></td>} />
                <Column cell={this.CommandCell} width="285px" />
            </StatefulGrid>
            {this.state.objectInEdit && <ConfigDialogContainer dataItem={this.state.objectInEdit} save={this.save} cancel={this.cancel}/>}

        <br/>
        {/* <Fab color='primary' onClick={this.handleAddSegment}>
                    <AddIcon />
                </Fab>
                {this.state.steps.length > 0 && 
                    <Fab style={{ marginLeft: '20px' }} color='secondary' onClick={this.handleRemoveSegment}>
                        <DeleteIcon />
                    </Fab>
                } */}
               
            </div>
        </div>
        )
    }
    renderUserForm = () => {

        return (
            this.state.newUserData.map((u, i) => {
                return (
                    <div className="segment-detail-container">
                        <h3>New User #{i + 1}</h3>
                        <label className='formField'>
                            <span className="required">E-mail:</span>
                            <i className='m-icons'>help</i>
                            <div className="txt-field-container">
                            <input type="text" name='' className="txt-field-100-e" placeholder="E-mail" value={this.state.newUserData[i].newUserEmail} onChange={(e) => this.handleUserChange(e, i, 'newUserEmail', 'add')} />
                            {this.state.newUserData[i].newUserEmailErrorMessage !== '' ? <div><span className='errorField'>{this.state.newUserData[i].newUserEmailErrorMessage}</span></div> : null}
                            </div>
                        </label>
                        <label className='formField'>
                            <span className="required">First Name:</span>
                            <i className='m-icons'>help</i>
                            <div className="txt-field-container">
                            <input type="text" name="newUserFirstName" className="txt-field-100-e" placeholder="First Name" value={this.state.newUserData[i].newUserFirstName} onChange={(e) => this.handleUserChange(e, i, 'newUserFirstName', 'add')} />
                            {this.state.newUserData[i].newUserFirstNameErrorMessage !== '' ? <div><span className='errorField'>{this.state.newUserData[i].newUserFirstNameErrorMessage}</span></div> : null}
                            </div>

                        </label>
                        <label className='formField'>
                            <span className="required">Last Name:</span>
                            <i className='m-icons'>help</i>
                            <div className="txt-field-container">
                            <input type="text" name="newUserLastName" className="txt-field-100-e" placeholder="Last Name" value={this.state.newUserData[i].newUserLastName} onChange={(e) => this.handleUserChange(e, i, 'newUserLastName', 'add')} />
                            {this.state.newUserData[i].newUserLastNameErrorMessage !== '' ? <div><span className='errorField'>{this.state.newUserData[i].newUserLastNameErrorMessage}</span></div> : null}
                            </div>
                        </label> 
                        <label className='formField formField-1'>
                        <span className="required">Actions:</span>
                        <i className='m-icons'>help</i>
                        <Select isMulti name="actions"  value={this.state.newUserData[i].newUserActions} options={this.state.actions} className="basic-multi-select txt-field-container-multi" classNamePrefix="select" onChange={(e) => this.handleUserChange(e, i, 'newUserActions', 'add')} />

                        </label>
                        <label className='formField formField-1'>
                        <span className="required">Segments:</span>
                        <i className='m-icons'>help</i>
                        <Select isMulti name="actions" options={this.state.segmentsListUser} className="basic-multi-select txt-field-container-multi" classNamePrefix="select" onChange={(e) => this.handleUserChange(e, i, 'newUserSegments', 'add')} />

                        </label>                          
                    </div>
                )
            })
        )
    }
    renderSegmentForm = () => {
        return (
            this.state.clusterSegments.map((s, i) => {
                return (
                    <div className="segment-detail-container">
                        <h3>Segment #{i + 1}</h3>
                    <label className='formField-inline'>
                    <span className="required">Segment Name:</span>
                    <i className='m-icons'>help</i>
                    <div className="txt-field-container">
                        <input type="text" name={this.state.clusterSegments[i].segmentName} className="txt-field-50" placeholder="Segment Name" value={this.state.clusterSegments[i].segmentName} onChange={(e) => this.handleSegmentChange(e, i, 'segmentName', 'add')} />
                        <select className="combobox-dropdown-admin-50" name="clusterSegments.segmentName" onChange={(e) => this.handleSegmentChange(e, i, 'segmentName', 'add')}>
                            <option value="" disabled selected hidden>Existing Segments</option>
                            {
                                this.state.segmentsList.map((v, index) => <option key={index} disabled={false} value={v} selected={this.state.segmentsListPlaceholder === v}>{this.capitalize(v)}</option>)
                            }
                        </select> 
                        {!this.state.clusterSegments[i].isExist ? <div><span className='errorField' style={{color: 'black'}}></span></div> : null}
                        {/* <div className='help-content'>Create your own Segment or select an existing from the dropdown. </div> */}
                    </div>

                    </label>                           
                    
                    <label className='formField'>
                        <span className="required">Owner Name:</span>
                       
                        <i className='m-icons'>help</i>
                        <div className="txt-field-container">
                        <input type="text" name="clusterSegments.segmentOwnerName"  className="txt-field-100-e" placeholder="Business Owner Name" value={this.state.clusterSegments[i].segmentOwnerName} onChange={(e) => this.handleSegmentChange(e, i, 'segmentOwnerName', 'add')} />
                        {this.state.clusterSegments[i].segmentOwnerNameErrorMessage !== '' ? <div><span className='errorField'>{this.state.clusterSegments[i].segmentOwnerNameErrorMessage}</span></div> : null}
                        
                        
                        </div>
                    </label>
                    <label className='formField'>
                    <span className="required">Owner E-mail:</span>
                        <i className='m-icons'>help</i>
                        <div className="txt-field-container">
                        <input type="text" name="clusterSegments.ownerEmailId" className="txt-field-100-e" placeholder="Business Owner E-mail"  value={this.state.clusterSegments[i].ownerEmailId} onChange={(e) => this.handleSegmentChange(e, i, 'ownerEmailId', 'add')} />
                        {this.state.clusterSegments[i].ownerEmailIdErrorMessage !== '' ? <div><span className='errorField'>{this.state.clusterSegments[i].ownerEmailIdErrorMessage}</span></div> : null}
                        </div>
    
                    </label>
                </div>
                )
            })
        )
    }
    renderGridCellDetails = (params) => {
        if(params.dataItem.configDataType === 'String' && params.field === 'configValue') {
            return (
                <div>
                    <input type="text" name={params.field} className="app-config-txtField" placeholder={'PLEASE FILL IN'} value={params.dataItem.configValue} onChange={(e) => this.handleChangeKendo(e, params)} />
                </div>
            )
        } else if(params.dataItem.configDataType === 'boolean' && params.field === 'configValue') {
            return (
                <td>
                    <div className="app-config-toggle">
                        <ToggleSlider id={params.dataItem.configId} handleToggleChange={this.handleToggleEncrypt} toggleType='apiConfigEncrypt' toggleOn={params.dataItem.configValue} />
                    </div>
                </td>
            )
        } else if(params.field === 'encryptionRequired') {
            if(params.dataItem.encryptionRequired) {
                return (
                    <td>
                        <div className="app-config-toggle">
                            <ToggleSlider id={params.dataItem.configId} handleToggleChange={this.handleToggleEncrypt} toggleType='apiConfigEncrypt' toggleOn={params.dataItem.encryptionRequired} />
                        </div>
                    </td>
                )
            } else {
                return (
                    <td>
                    </td>
                )
            }

        }

    }
    setUpForms() {
        if(this.props.getConfigDefinitionsSuccess) {
            let StrArray = []
            let BooleanArray = []
            let jiraArray = [
                {configId: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15), configName: 'jira_projects', configValue: '',configDataType: 'form', isEncrypted: false, inEdit: false, error: false, configValueErrorMessage: ''}, 
                {configId: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15), configName: 'jira_user', configValue: '',configDataType: 'form', isEncrypted: false, inEdit: false, error: false, configValueErrorMessage: ''},
                {configId: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15), configName: 'jira_password', configValue: '',configDataType: 'form', isEncrypted: false, inEdit: false, error: false, configValueErrorMessage: ''},
                {configId: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15), configName: 'jira_url', configValue: '', configDataType: 'form', isEncrypted: false, inEdit: false, error: false, configValueErrorMessage: ''}
            ]
            let serviceArray = [
                {configId: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15), configName: 'servicenow_user', configValue: '', configDataType: 'form', isEncrypted: false, inEdit: false, error: false, configValueErrorMessage: ''}, 
                {configId: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15), configName: 'servicenow_password', configValue: '',configDataType: 'form', isEncrypted: false, inEdit: false, error: false, configValueErrorMessage: ''},
                {configId: Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15), configName: 'servicenow_url', configValue: '',configDataType: 'form', isEncrypted: false, inEdit: false, error: false, configValueErrorMessage: ''},
            ]
            let configArray = this.props.getConfigDefinitionsData.map((c, i) => {
                if(c.configType === 'Account' && c.mandatory) {
                    if(c.configDataType === 'String') {
                        c.configValue = ''
                        c.isEncrypted = false
                        c.inEdit = true
                        c.error = false
                        c.configValueErrorMessage = ''
                        StrArray.push(c)
                        return c
                    } else if(c.configDataType === 'Boolean') {
                        c.configValue = false
                        c.isEncrypted = false
                        c.error = false
                        c.configValueErrorMessage = ''
                        BooleanArray.push(c)
                        return c
                    }
                } else {
                }
            })
            configArray = configArray.filter((f,i) => f !== undefined && f !== null)
            let finConfigArray = [...configArray, ...jiraArray, ...serviceArray]
            this.setState({
                configDetailData: finConfigArray
            })
        }


        this.state.testSuiteData.map((t, i) => {
            t.inEdit = true
            return t
        })
        let segmentList = this.state.segmentsListLowerCase.map((s, i) => {
            return s.toLowerCase()
        })
        this.setState({
            segmentsListLowerCase: segmentList
        })
    }
    onItemChangeConfig = (event) => {
        const editedItemID = event.dataItem.configId;
        const configDetailData = this.state.configDetailData.map(item =>
            item.configId === editedItemID ? {...item, [event.field]: event.value} : item
        );
        this.setState({ configDetailData });
    };
    itemChangeTestSuite = (event) => {
        const editedItemID = event.dataItem.id;
        const testSuiteData = this.state.testSuiteData.map(item =>
            item.id === editedItemID ? {...item, [event.field]: event.value} : item
        );
        this.setState({ testSuiteData });
    };
    updateUserSegmentList = () => {
        let fin = []
        var newUserSegmentArray = this.state.segments
        var hello = this.state.clusterSegments.map((c, i) => {
            if(!this.state.segmentsListLowerCase.includes(c.segmentName.toLowerCase()) || !this.state.segmentsListUser.includes(c.segmentName.toLowerCase())) {
                return ( {'label': c.segmentName, 'value': c.segmentName} )
            }
        })
        var object = hello.filter((f,i) => f !== undefined && f !== null)
        fin = [...this.state.segments, ...hello]
        this.setState({segmentsListUser: fin})
    }
    componentDidMount = () => {
        this.props.fetchConfigDefinitions(this.props.token)
        this.setUpForms()
    }
    
    componentDidUpdate = (prevProps) => {
        if (prevProps != this.props) {
            if (this.props.adminAddRolesSuccess || this.props.postAccountSetupSuccess || this.props.adminAddRolesSuccess || this.props.adminRemoveRolesSuccess) {
                this.SuccessToast('Successfully Posted')
                setTimeout(function () {
                window.location.reload()
                }.bind(this), 3000)
            } else if (this.props.adminAddRolesError || this.props.adminRemoveRolesError || this.props.putConfigByIdError) {
                this.ErrorToast(this.props.adminAddRolesErrorData)
            } else if (this.props.postAccountSetupError) {
                this.ErrorToast(this.props.postAccountSetupErrorMessage)
            } else if (this.props.resetPasswordSuccess) {
                this.SuccessToast('Successfully Posted')
                this.props.clearStatus()
            } else if (this.props.resetPasswordError) {
                this.ErrorToast(this.props.resetPasswordErrorMessage)
                this.props.clearStatus()
            } else if(this.props.putConfigByIdSuccess) {
                this.SuccessToast('Successfully Updated')
            }
        }
    }

      SuccessToast = (msg) => {
        this.toaster.show({ 
          intent: Intent.SUCCESS,
          icon: 'tick',
          message: msg
        })
        this.setState({
          ...this.state,
          spinner: false
        })
      }
     
      ErrorToast = (m) => {
        let errorMsg = 
        <div>
          <b>Error Mesage</b>: {m.errorMessage}
          <br/>
          <b>Error Details</b>: {m.messageDetails}
          <br/>
          <b>Error ID</b>: {m.errorId}
        </div>
        this.toaster.show({ 
          intent: Intent.DANGER,
          icon: 'cross',
          message: errorMsg
        })
    }
}


const mapStateToProps = state => {
    return {
        adminAddRolesSuccess: state.adminMetadata.adminAddRolesSuccess,
        adminAddRolesError: state.adminMetadata.adminAddRolesError,
        adminAddRolesErrorData: state.adminMetadata.adminAddRolesErrorData,
        adminRemoveRolesSuccess: state.adminMetadata.adminRemoveRolesSuccess,
        adminRemoveRolesError: state.adminMetadata.adminRemoveRolesError,
        adminRemoveRolesErrorData: state.adminMetadata.adminRemoveRolesErrorData,
        getUserRolesSuccess: state.adminMetadata.getUserRolesSuccess,
        getUserRolesData: state.adminMetadata.getUserRolesData,
        getConfigDefinitionsData: state.adminMetadata.getConfigDefinitionsData,
        getConfigDefinitionsSuccess: state.adminMetadata.getConfigDefinitionsSuccess,
        getConfigDefinitionsError: state.adminMetadata.getConfigDefinitionsError,
        getConfigDefinitionsFetching: state.adminMetadata.getConfigDefinitionsFetching,
        postAccountSetupSuccess: state.adminMetadata.postAccountSetupSuccess,
        postAccountSetupError: state.adminMetadata.postAccountSetupError,
        postAccountSetupErrorMessage: state.adminMetadata.postAccountSetupErrorMessage, 
        putConfigByIdSuccess: state.adminMetadata.putConfigByIdSuccess,
        putConfigByIdError: state.adminMetadata.putConfigByIdError,
        resetPasswordError: state.adminMetadata.resetPasswordError,
        resetPasswordErrorMessage: state.adminMetadata.resetPasswordErrorMessage,
        resetPasswordSuccess: state.adminMetadata.resetPasswordSuccess
    }
  }

  const mapDispatchToProps = dispatch => {
    return {
        postAddRoles: (data, token) => dispatch(postAddRoles(data, token)),
        postRemoveRoles: (data, token) => dispatch(postRemoveRoles(data, token)),
        fetchUserRoles: (email, token) => dispatch(fetchUserRoles(email, token)),
        fetchUIDropdownList: (token) => dispatch(fetchUIDropdownList(token)),
        postAccountSetup: (data, token) => dispatch(postAccountSetup(data, token)),
        fetchConfigDefinitions: (token) => dispatch(fetchConfigDefinitions(token)),
        fetchConfigDefinitionList: (token) => dispatch(fetchConfigDefinitionList(token)),
        fetchConfigDefinitionsById: (id, token) => dispatch(fetchConfigDefinitionsById(id, token)),
        putConfigDefinitions: (id, data, token) => dispatch(putConfigDefinitions(id, data, token)),
        decryptConfigByName: (name, accId, token) => dispatch(decryptConfigByName(name, accId, token)),
        resetUserPassword: (data, token) => dispatch(resetUserPassword(data, token)),
        clearStatus: () => dispatch(clearStatus()),
        clearConfigs: () => dispatch(clearConfigs()),
        decryptConfigByName: (name, accId, token) => dispatch(decryptConfigByName(name, accId, token))
    }
  }

export default connect(mapStateToProps, mapDispatchToProps)(Admin)