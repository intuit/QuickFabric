import React,  { Component } from 'react'
import './emrManagement.css'
import Fab from '@material-ui/core/Fab';
import DeleteIcon from '@material-ui/icons/Delete';
import AddIcon from '@material-ui/icons/Add';
import CustomAlert from './CustomAlert';

/**
 * Combox component in the create cluster form.
 */
const ComboBox = ({ data, onChange, defaultValue }) => {
    if (!data) {
        return <div />;
    }
    return ( 
        <div className="combobox-container">
            <select className="combobox-dropdown" onChange={onChange}>
                {
                    data.map((v, i) => <option key={i} value={v} defaultValue={defaultValue === v}>{v}</option>)
                }
            </select>
        </div>
    );
}
 
/**
 * Component to add new steps (jobs) to a cluster.
 */
export default class AddSteps extends Component {
    action_type = ['CANCEL_AND_WAIT', 'CONTINUE'];
    constructor(props){
        super(props);
        this.state = {
            visible: false,
            steps: [
                {
                    name: '',
                    jar: 's3://us-west-2.elasticmapreduce/libs/script-runner/script-runner.jar',
                    args: '',
                    actionOnFailure: 'CANCEL_AND_WAIT',
                    mainClass: '',
                    stepCreatedBy: this.props.fullName
                }
            ],
            stepNameError: true,
            jarLocationError: false,
            argsError: true,
            addStepNextBtnDisabled: false,
            stepErrors: [
                {
                    name: true,
                    jar: false,
                    args: true,
                }
            ],
            showConfirmation: false
        }
        this.handleChange = this.handleChange.bind(this);
        this.handleSubmit = this.handleSubmit.bind(this);
        this.formSubmit = this.formSubmit.bind(this);
        this.toggleDialog = this.toggleDialog.bind(this);
        this.handlename = this.handlename.bind(this);
        this.handlemainClass = this.handlemainClass.bind(this);
        this.handlejarLocation = this.handlejarLocation.bind(this);
        this.handlearguments = this.handlearguments.bind(this);
        this.handleactiononFailure = this.handleactiononFailure.bind(this);
        this.handleAddStep = this.handleAddStep.bind(this);
        this.handleRemoveStep = this.handleRemoveStep.bind(this);
        this.handleConfirmation = this.handleConfirmation.bind(this);
    }

    render() {
        return (
            <div>
            {this.state.visible &&
                <div className='popup'>
                    <div className='popup_inner'>
                        <div className='popup_close'>
                            <button className='close_popup' onClick={this.toggleDialog}>
                                <i className='material-icons'>keyboard_arrow_right</i>
                            </button>
                        </div>
                        <form className="k-form">
                            <div>
                                <h2>Add Steps</h2>
                                {this.state.steps.map((values, index) => (
                                <div key={index} style={{marginTop: '10px'}}>
                                    <hr/>
                                    <h4>Step #{index + 1}</h4>
                            <label className="formField" >
                                    <span className="required" >Name</span>
                                    <input className="textField" placeholder="Step Name (Required)" value={this.state.steps[index].Name} onChange={e => this.handlename(e, index)} />
                                    <div>{this.state.stepErrors[index].name ? <span className='errorField'>This is a required field</span> : null }</div>
                                    
                                </label>
                                <label className="formField" >
                                    <span className="required" >Action on Failure</span>
                                    <ComboBox className="dropdown-content" data={this.action_type} defaultValue="CANCEL_AND_WAIT" onChange={e => this.handleactiononFailure(e, index)} />
                                </label>
                                <label className="formField" >
                                    <span className="required" >JAR location</span>
                                    <input className="textField" defaultValue="s3://us-west-2.elasticmapreduce/libs/script-runner/script-runner.jar" value={this.state.steps[index].jar} onChange={e => this.handlejarLocation(e, index)} />
                                    <div>{this.state.stepErrors[index].jar ? <span className='errorField'>This is a required field</span> : null }</div>
                                </label>
                                <label className="formField" >
                                    <span className="required" >Arguments</span>
                                    <input className="textField" placeholder="Eg: s3://idl-sched-uw2-processing-sbgayt-prd/artifacts/emr/emr-1.8.2/scripts/resources/processing-sbgayt-prd/kerberos/adhoc_create_hdp_home_dirs.sh av1" value={this.state.steps[index].args} onChange={e => this.handlearguments(e, index)} />
                                    <div>{this.state.stepErrors[index].args ? <span className='errorField'>This is a required field</span> : null }</div>
                                </label>
                                <label className="formField" >
                                    <span>Main Class</span>
                                    <input className="textField" placeholder="Main Class (Optional)" onChange={e => this.handlemainClass(e, index)}/>
                                </label>
                                </div>
                                ))}
                                <Fab color='primary' onClick={this.handleAddStep}>
                                    <AddIcon />
                                </Fab>
                                {this.state.steps.length > 1 && 
                                    <Fab style={{ marginLeft: '20px' }} color='secondary' onClick={this.handleRemoveStep}>
                                        <DeleteIcon />
                                    </Fab>
                                }
                            </div>
                            <div>
                                <button type="button" className='nextBtn' onClick={this.handleConfirmation} disabled={this.state.stepNameError || this.state.jarLocationError || this.state.argsError} >Submit</button>
                                <button type="button" className="nextBtn" onClick={this.toggleDialog} >Cancel</button>
                            </div>
                        </form>
                        {this.state.showConfirmation ? <CustomAlert 
                                                            type='addSteps' 
                                                            message={`Steps will be added for ${this.props.data.clusterName}`} 
                                                            account={this.props.data.account} 
                                                            token={this.props.token} 
                                                            onSubmit={this.handleSubmit} 
                                                            onCancel={this.handleConfirmation}
                                                            globalJiraData={this.props.globalJiraData}
                                                            /> : null}
                    </div>
                </div>
            }
        </div>
        );
    }

    componentDidUpdate(prevProps) {
        if (!prevProps.isOpen && this.props.isOpen) {
          this.setState({
              visible: true
          })
        }
      }

      /**
       * Open and close confirmation popup.
       */
    handleConfirmation() {
        this.setState({
            ...this.state,
            showConfirmation: !this.state.showConfirmation
        })
    }
    
    /**
     * Check whether all the required fields in the steps are filled.
     */
    formSubmit() {
        return !(this.state.steps[0].Name === '' || this.state.steps[0].args === '' || this.state.steps[0].jar === '' )
    }

    handleChange(event) {
        this.setState({value: event.target.value});
      }

    /**
    *  Submit the steps added.
    */
    handleSubmit(jira) {
        this.props.onSubmit({
            "clusterName": this.props.data.clusterName,
            "clusterId": this.props.data.clusterId,
            "account": this.props.data.account,
            steps: this.state.steps,
            "role": this.props.data.role,
            "jiraTicket": jira
        })
        this.toggleDialog();
      }
    
    toggleDialog() {
        if(this.state.visible){
            this.props.onClose();
        }
        this.setState({
            visible: false
        });
    }
    
    stepsModifier(steps, value, index, key){
        return steps.map((v, i) => {
            if(index === i){
                v[key] = value
            }
            return v
        })
    }

    stepErrorModifier(stepErrors, value, index, key) {
        return stepErrors.map((v, i) => {
            if (index === i) {
                v[key] = value
            }
            return v;
        })
    }
    
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
        this.setState({
            steps: this.state.steps.slice(0,-1),
            stepErrors: this.state.stepErrors.slice(0, -1),
            stepNameError: nameError,
            jarLocationError: jarError,
            argsError: argsError
        })
    }

    handlemainClass(e, index) {
        this.setState({
            ...this.state,
            steps: this.stepsModifier(this.state.steps, e.target.value, index, 'mainClass')
        })
    }

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

    handleactiononFailure(e, index) {
        this.setState({
            ...this.state,
            steps: this.stepsModifier(this.state.steps, e.target.value, index, 'actionOnFailure')
        })     
    }
}