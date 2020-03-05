import React from 'react';
import './profile.scss';
import Cookies from 'js-cookie';
import { Tab, Tabs, Intent,Toaster,Toast, Position, Dialog, Classes } from '@blueprintjs/core';
import { connect } from 'react-redux';
import emrStatusCell from '../EMRManagement/emrStatusCell'
import { fetchAllClusterMetaData } from '../../actions/emrManagement';
import { fetchSubscriptions, updateSubscriptions, clearUpdates } from '../../actions/profile';
import { Grid, GridColumn as Column, GridToolbar } from '@progress/kendo-react-grid';

import { withState } from '../../utils/components/with-state.jsx';

const StatefulGrid = withState(Grid);

var roles = {
    "rotateami": "Rotate AMI",
    "read": "Read",
    "addstep": "Add Step",
    "terminatecluster": "Terminate Cluster",
    "createcluster": "Create Cluster",
    "runclusterhealthchecks": "Run Health Checks",
    "superadmin": "Super Admin",
    "admin": "Admin"
}

/**
 * Component for showing detailed information about the user.
 */
class Profile extends React.Component {
    constructor(props) {
        super(props);
        this.toaster = Toaster
        this.refHandlers = {
            toaster: (ref) => this.toaster = ref
        }

        this.state = {
            primaryTab: "myAccess",
            amirotationReport: true,
            clusterMetricsReport: false,
            toasts: [],
            sort: [
                { field: 'creation_timestamp', dir: 'desc' }
            ],
            showAlert: false,
            alertMessage: '',
            alertTitle: '',
            alertIcon: '',
            showConfirmButton: false,
            reportToUpdate: ''
        }

        this.handlePrimaryTabChange = this.handlePrimaryTabChange.bind(this);
        this.handleAMIRotationReport = this.handleAMIRotationReport.bind(this);
        this.handleClusterMetricsReport = this.handleClusterMetricsReport.bind(this);
        this.handleUpdateSubscriptions = this.handleUpdateSubscriptions.bind(this);
        this.SuccessToast = this.SuccessToast.bind(this);
        this.ErrorToast = this.ErrorToast.bind(this);
        this.renderTabData = this.renderTabData.bind(this);
        this.setAlert = this.setAlert.bind(this);
        this.handleAlert = this.handleAlert.bind(this);
    }
    
    render() {
        let emailID = Cookies.get('username');
        let creationDate = Cookies.get('creationDate');
        let clusterData = this.props.emrMetadataData.filter(x => x.createdBy === this.props.fullName)
        return (
            <div className='profile-component'>
                <Toaster position={Position.TOP} ref={this.refHandlers.toaster}>
                {this.state.toasts.map(toast => <Toast {...toast} />)}
                </Toaster>
                <h2>User Profile</h2>
                <div className='profile-section'>
                    <h3 style={{ color: '#0097E4' }}>Basic Information</h3>
                    <table>
                        <tbody>
                            <tr>
                                <td><h4>Full Name:</h4></td>
                                <td><span className='user-data'>{this.props.fullName}</span></td>
                            </tr>
                            <tr>
                                <td><h4>Email ID: </h4></td>
                                <td><span className='user-data'>{emailID}</span></td>
                            </tr>
                            <tr>
                                <td><h4>Super Admin:</h4></td>
                                <td><span className='user-data'>{`${this.props.superAdmin}`}</span></td>
                            </tr>
                            <tr>
                                <td><h4>Created On: </h4></td>
                                <td><span className='user-data'>{creationDate}</span></td>
                            </tr>
                        </tbody>
                    </table>
                </div>
                <div className='profile-section side-section'>
                    <Tabs className='line' id="primary-tab" selectedTabId={this.state.primaryTab} onChange={this.handlePrimaryTabChange}>
                        <Tab id="myAccess" title="My Access" />
                        <Tab id="myClusters" title="My Clusters" />
                        <Tab id="mySubscriptions" title="My Subscriptions" />
                    </Tabs>
                    {
                        this.renderTabData(this.state.primaryTab, clusterData)
                    }
                </div>
                {this.state.showAlert && 
          <Dialog
            isOpen={this.state.showAlert}
            onClose={this.handleAlert}
            title={this.state.alertTitle}
            icon={this.state.alertIcon}
          >
            <div className={Classes.DIALOG_BODY}><span style={{ fontSize: '15px', marginTop: '10px' }}>{this.state.alertMessage}</span></div>
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
                onClick={this.handleUpdateSubscriptions}>
                Confirm
                </button>
              }
            </div>

            
          </Dialog> }
            </div>
        )
    }

    renderTabData(primaryTab, clusterData) {
        let Cluster_ID = <span style={{ fontSize: '15px', fontWeight: '600' }}>Cluster ID</span>
        let Cluster_Name = <span style={{ fontSize: '15px', fontWeight: '600' }}>Cluster Name</span>
        let Cluster_Status = <span style={{ fontSize: '15px', fontWeight: '600' }}>Cluster Status</span>
        let Creation_Timestamp = <span style={{ fontSize: '15px', fontWeight: '600' }}>Created At</span>
        let Role = <span style={{ fontSize: '15px', fontWeight: '600' }}>Role</span>
        let Type = <span style={{ fontSize: '15px', fontWeight: '600' }}>Type</span>
        switch(primaryTab) {
            case 'myAccess':
                return (
                    <div className='tab-content'>
                        {this.props.dccRoles !== undefined && this.props.dccRoles.map((role, key) => {
                            return (
                                <table style={{ marginBottom: '30px', width: '100%' }}>
                                    <tbody style={{ bottom: '10px' }}>
                                        <tr>
                                            <td><h3 style={{ color: '#0097E4' }}>{role.serviceType}</h3></td>
                                        </tr>
                                        <tr>
                                            <table style={{ width: '100%' }}>
                                                <tbody>
                                                {role.roles.map((val, key) => {
                                                    return (
                                                        <tr>
                                                            <h4>{roles[val.name]}</h4>
                                                            <table className='roles-table'>
                                                                <tbody>
                                                                    {val.segments.map((x, key) => {
                                                                        return (
                                                                            <tr>
                                                                                <td style={{ width: '200px' }}><h5>{x.segmentName}</h5></td>
                                                                                <td><span className='user-data'>{x.accounts.map(e => e.accountId).join(", ")}</span></td>
                                                                            </tr>
                                                                        )
                                                                    })}
                                                                </tbody>
                                                            </table>
                                                        </tr>
                                                    )
                                                })}
                                            </tbody>
                                            </table>
                                        </tr>
                                    </tbody>
                                    <hr />
                                </table>
                            )
                        })}
                    </div>
                )
            
            case 'myClusters':
                return (
                    <div className='tab-content'>
                        <StatefulGrid
                            data={clusterData} 
                            resizable
                            style={{ 'marginTop': 20 , height: '75vh' }}
                        >
                            <Column field="clusterId" title={Cluster_ID} width="180px" />
                            <Column field="clusterName" title={Cluster_Name} width="auto" />
                            <Column field="status" title={Cluster_Status} width="180px" cell={emrStatusCell} />
                            <Column field="creationTimestamp" title={Creation_Timestamp} width="200px" />
                            <Column field="role" title={Role} width="120px" />
                            <Column field="type" title={Type} width="160px" />
                        </StatefulGrid>
                    </div>
                )
            
            case 'mySubscriptions':
                return (
                    <div className='tab-content'>
                        <table>
                            <tr>
                                <td><span style={{ fontSize: '15px', position: 'relative' }}>AMI Rotation Report</span></td>
                                <td>
                                    <div className="auto-rotation-actions">
                                        <input
                                        className="react-switch-checkbox"
                                        id="amirotationReport"
                                        type="checkbox"
                                        onChange={(e) => this.handleUpdateSubscriptions(e, 'amirotationReport')}
                                        checked={this.state.amirotationReport}

                                    />
                                    <label
                                        style={{ background: this.state.amirotationReport ? '#53b700' : '#a3a296', width: '130px' }}
                                        className="auto-rotate-switch-label"
                                        htmlFor="amirotationReport"
                                    >
                                        <span className={`react-switch-button`} />
                                        {this.state.amirotationReport ? <p className="subscribe-toggle-on">Subscribed</p> : <p className="subscribe-toggle-off">Unsubscribed</p>}
                                    </label>    
                                    </div>
                                </td>
                            </tr>
                            <tr>
                                <td><span style={{ fontSize: '15px', position: 'relative'}}>Cluster Metrics Report</span></td>
                                <td>
                                    <div className="auto-rotation-actions">
                                        <input
                                        className="react-switch-checkbox"
                                        id="clusterMetricsReport"
                                        type="checkbox"
                                        onChange={(e) => this.handleUpdateSubscriptions(e, 'clusterMetricsReport')}
                                        checked={this.state.clusterMetricsReport}

                                    />
                                    <label
                                        style={{ background: this.state.clusterMetricsReport ? '#53b700' : '#a3a296', width: '130px' }}
                                        className="auto-rotate-switch-label"
                                        htmlFor="clusterMetricsReport"
                                    >
                                        <span className={`react-switch-button`} />
                                        {this.state.clusterMetricsReport ? <p className="subscribe-toggle-on">Subscribed</p> : <p className="subscribe-toggle-off">Unsubscribed</p>}
                                    </label>    
                                    </div>
                                </td>
                            </tr>
                        </table>
                    </div>
                )
        }
    }

    setAlert( status, type, message, title, icon) {
        if(type === 'confirm' && title === 'Cluster Metrics Report Subscription') {
          this.setState({
            ...this.state,
            showAlert: status,
            alertMessage: message,
            alertTitle: title,
            alertIcon: icon,
            showConfirmButton: true,
            reportToUpdate: 'Cluster Metrics Report'
          })  
        } else if (type === 'confirm' && title === 'AMI Rotation Report Subscription') {
            this.setState({
                ...this.state,
                showAlert: status,
                alertMessage: message,
                alertTitle: title,
                alertIcon: icon,
                showConfirmButton: true,
                reportToUpdate: 'AMI Rotation Report'
              })
        } 
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
        }

    handleUpdateSubscriptions = (event, type) => {
        let subs_data = this.props.getSubscriptions;
        if (type === 'clusterMetricsReport') {
            subs_data[0].subscribed = this.state.amirotationReport;
            subs_data[1].subscribed = !this.state.clusterMetricsReport;
            this.props.updateSubscriptions(subs_data, this.props.token)
            this.setState({
                ...this.state,
                spinner: true,
                clusterMetricsReport: !this.state.clusterMetricsReport
            })
        } else if (type === 'amirotationReport') {
            subs_data[0].subscribed = !this.state.amirotationReport;
            subs_data[1].subscribed = this.state.clusterMetricsReport;
            this.props.updateSubscriptions(subs_data, this.props.token)
            this.setState({
                ...this.state,
                spinner: true,
                amirotationReport: !this.state.amirotationReport
            })
        } 
    }

    handleAMIRotationReport = (event) => {
        this.setState({
            ...this.state,
            amirotationReport: !this.state.amirotationReport
        })
    }

    handleClusterMetricsReport = (event) => {
        this.setState({
            ...this.state,
            clusterMetricsReport: !this.state.clusterMetricsReport
        })
    }

    pageChange = (event) => {
        this.setState({
            skip: event.page.skip,
            take: event.page.take
        });
    }

    handlePrimaryTabChange(newTabId) {
        this.setState({
          ...this.state,
          primaryTab: newTabId
        })
      }
    
    componentDidMount() {
        this.props.fetchAllClusterMetaData(this.props.token);
        this.props.fetchSubscriptions(this.props.token)
    }

    componentDidUpdate(nextProps) {
        if (nextProps.updateSubscriptionsSuccess != this.props.updateSubscriptionsSuccess || nextProps.updateSubscriptionsError != this.props.updateSubscriptionsError) {
            if (this.props.updateSubscriptionsSuccess) {
                this.SuccessToast('Successfully Posted')
            } else if (this.props.updateSubscriptionsError) {
                this.ErrorToast(this.props.updateSubscriptionsErrorData)
            }
            this.props.clearUpdates();
        }
        if (nextProps.getSubscriptions !== this.props.getSubscriptions) {
            this.setState({
                ...this.state,
                amirotationReport: this.props.getSubscriptions[0].subscribed,
                clusterMetricsReport: this.props.getSubscriptions[1].subscribed
            })
        }

    }

    SuccessToast() {
        this.toaster.show({ 
          intent: Intent.SUCCESS,
          icon: 'tick',
          message: "Successfully Posted!" 
        })
        this.setState({
          ...this.state
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
        this.setState({
          ...this.state
        })
      }
}

const mapStateToProps = state => {
    return {
        emrMetadataData: state.emrMetadataData.emrMetadataData,
        emrMetadataDataFetching: state.emrMetadataData.emrMetadataDataFetching,
        getSubscriptions: state.profileMetadata.getSubscriptions,
        updateSubscriptionsSuccess: state.profileMetadata.updateSubscriptionsSuccess,
        updateSubscriptionsError: state.profileMetadata.updateSubscriptionsError,
        updateSubscriptionsErrorData: state.profileMetadata.updateSubscriptionsErrorData
    }
  }
  
  const mapDispatchToProps = dispatch => {
    return {
        fetchAllClusterMetaData: (token) => dispatch(fetchAllClusterMetaData(token)),
        fetchSubscriptions: (token) => dispatch(fetchSubscriptions(token)),
        updateSubscriptions: (data, token) => dispatch(updateSubscriptions(data, token)),
        clearUpdates: () => dispatch(clearUpdates())
    }
  }

  export default connect(mapStateToProps, mapDispatchToProps) (Profile)