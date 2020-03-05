import React from 'react';
import { TimePicker } from '@progress/kendo-react-dateinputs';
import { connect } from 'react-redux';
import { fetchAccountJiraEnabled } from '../../actions/emrManagement';
import { checkJiraEnabled } from '../../utils/components/CheckJiraEnabled';

/**
 * Shared Custom Popup window component.
 */
class CustomAlert extends React.Component {
    constructor(props) {
        super(props);
        let shour = new Date();
        let ehour = new Date();
        if (this.props.dataItem !== undefined) {
            shour.setHours(this.props.dataItem.autopilotWindowStart)
            ehour.setHours(this.props.dataItem.autopilotWindowEnd)
        }
        this.state = {
            jiraticket: '',
            starthour: shour,
            endhour: ehour,
            sla: this.props.dataItem !== undefined ? this.props.dataItem.amiRotationSlaDays : ''
        }

        this.postSubmit = this.postSubmit.bind(this);
        this.handleTimeChange = this.handleTimeChange.bind(this);
        this.handleJiraTicket = this.handleJiraTicket.bind(this);
        this.handleAMIRotationSLA = this.handleAMIRotationSLA.bind(this);
        this.postAutoPilot = this.postAutoPilot.bind(this);
    }

    render() {
        console.log("Jiras", this.props.accountJiraData, this.props.globalJiraData);
        console.log("Jira token", this.props.token)
        return (
            <div className='popup'>
                {/* <div className='emptydiv'></div> */}
                <div className={`${this.props.type === 'auto-pilot' ? 'alert-content1' : 'alert-content'}`}>
                    <div className='alert-text'>
                        {this.props.type === 'addSteps' && checkJiraEnabled(this.props.globalJiraData, this.props.accountJiraData) ?
                            <div>
                                <span className="required">JIRA Ticket:</span>
                                <input className="textField-servicenow" placeholder="Jira ticket" value={this.state.jiraticket} onChange={e => this.handleJiraTicket(e)} />
                                <div>{this.state.jiraticket.length === 0 ? <span className='errorField'>This is a required field</span> : null }</div>
                            </div> : null
                        }
                        <span>{this.props.message}</span>
                    </div>
                    {this.props.type === 'auto-pilot' && 
                        <div style={{ fontSize: '15px', margin: '20px' }}>
                            <div style={{ marginTop: '20px' }}><h5>Auto Pilot Window:</h5></div>
                            <span style={{ marginTop: '10px' }}>From</span>
                            <TimePicker 
                                className='customTimePicker'
                                onChange={(e) => this.handleTimeChange(e, "start")}
                                format={"HH"}
                                value={this.state.starthour}
                            />
                            <span style={{ fontSize: '15px', marginTop: '10px', marginLeft: '10px' }}>To</span>
                            <TimePicker 
                                className='customTimePicker'
                                onChange={(e) => this.handleTimeChange(e, "end")}
                                format={"HH"}
                                value={this.state.endhour}
                            />
                            <div style={{ fontSize: '15px', marginTop: '20px' }}><h5>AMI Rotation SLA Days:</h5></div>
                            <input className="customslafield" defaultValue="30" placeholder="Custom SLA for AMI Rotation" value={this.state.sla} onChange={this.handleAMIRotationSLA} />
                        </div>
                    }
                    <div className='alert-btns'>
                        {this.props.type === "addSteps" ? 
                        <button
                            className='terminateRotateSubmitBtn'
                            onClick={this.postSubmit}
                            disabled={checkJiraEnabled(this.props.globalJiraData, this.props.accountJiraData) && this.state.jiraticket.length === 0}
                        >
                            Confirm
                        </button> :
                        this.props.type === 'auto-pilot' ?
                        <button
                            className='terminateRotateSubmitBtn'
                            onClick={this.postAutoPilot}
                        >
                            Confirm
                        </button> :
                        <button
                            className='terminateRotateSubmitBtn'
                            onClick={this.props.onSubmit}
                        >
                            Confirm
                        </button>
                        }
                        <button
                            className='cancelBtn'
                            onClick={this.props.onCancel}
                        >
                            Cancel
                        </button>
                    </div>
                </div>
            </div>
        )
    }
    
    componentDidMount() {
        this.props.fetchAccountJiraEnabled(this.props.account, this.props.token)
    }

    postAutoPilot() {
        this.props.onSubmit(this.state.starthour, this.state.endhour, this.state.sla)
    }

    handleAMIRotationSLA(e) {
        if (e.target.value === '' || e.target.value.match("^(?:[1-9]|(?:[1-9][0-9])|(?:[1-9][0-9])|(?:99))$")) {
          this.setState({
              ...this.state,
              sla: e.target.value
          })
      }
      }

    postSubmit() {
        this.props.onSubmit(this.state.jiraticket);
    }

    handleJiraTicket(e) {
        if(e.target.value.match("^[a-zA-Z0-9-]*$")!=null) 
          this.setState({
              ...this.state,
              jiraticket: e.target.value
          })
      }
    
      handleTimeChange(e, type) {
        if (type === 'start') {
            this.setState({
                ...this.state,
                starthour: e.target.value
            })
        } else {
            this.setState({
                ...this.state,
                endhour: e.target.value
            })
        }  
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
  
  export default connect(mapStateToProps, mapDispatchToProps)(CustomAlert)