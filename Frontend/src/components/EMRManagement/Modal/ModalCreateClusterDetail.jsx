import React from 'react';
import { connect } from 'react-redux';
import { Grid, GridColumn as Column, GridDetailRow, GridToolbar, GridCell } from '@progress/kendo-react-grid'
import { process } from '@progress/kendo-data-query'

import { withState } from '../../../utils/components/with-state.jsx';
import { fetchClusterClone } from '../../../actions/emrManagement'
const StatefulGrid = withState(Grid);

/**
 * Component to show details of the Create Cluster step in the workflow.
 */
class ModalCreateClusterDetail extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dataDetails: []
        }
        this.addDataDetail = this.addDataDetail.bind(this)
    }

    render() {
        return (
            <div>
            <div className='popup'>
                <div className='popup_inner'>
                    <div className='popup_close'>
                        <button className='close_popup' onClick={this.props.onClose}>
                            <i className='material-icons'>keyboard_arrow_right</i>
                        </button>
                    </div>
                    <div style={{ padding: '35px' }}>
                    <h3 style={{marginBottom: '28px'}}>Create New Clusters: <span style={{color: "rgb(0, 151, 228)"}}>{this.props.data.cluster_name}</span></h3>
                        {
                            this.props.data && 
                            <div className='reviewCluster'>
                                <div className='grey'><span className='reviewTitles'>Cluster ID: </span><span className='reviewValue'>{this.props.clusterCloneData.clusterId}</span></div>
                                <div className='grey'><span className='reviewTitles'>Cluster Name: </span><span className='reviewValue'>{this.props.clusterCloneData.clusterName}</span></div>
                                <div className='grey'><span className='reviewTitles'>Account: </span><span className='reviewValue'>{this.props.clusterCloneData.account}</span></div>
                                <div className='grey'><span className='reviewTitles'>Created By: </span><span className='reviewValue'>{this.props.clusterCloneData.createdBy}</span></div>
                                <div className='grey'><span className='reviewTitles'>Type: </span><span className='reviewValue'>{this.props.clusterCloneData.type}</span></div>
                                <div className='grey'><span className='reviewTitles'>Segment: </span><span className='reviewValue'>{this.props.clusterCloneData.segment}</span></div>
                                <div className='grey'><span className='reviewTitles'>Core EBS Volume Size: </span><span className='reviewValue'>{this.props.clusterCloneData.coreEbsVolSize}</span></div>
                                {this.props.clusterCloneData.coreInstanceCount > 0 && <div className='grey'><span className='reviewTitles'>Core Instance Type: </span><span className='reviewValue'>{this.props.clusterCloneData.coreInstanceType}</span></div>}
                                <div className='grey'><span className='reviewTitles'>Core Instance Count: </span><span className='reviewValue'>{this.props.clusterCloneData.coreInstanceCount}</span></div>
                                {this.props.clusterCloneData.taskInstanceCount > 0 && <div className='grey'><span className='reviewTitles'>Task Instance Type: </span><span className='reviewValue'>{this.props.clusterCloneData.taskInstanceType}</span></div>}
                                <div className='grey'><span className='reviewTitles'>Task Instance Count: </span><span className='reviewValue'>{this.props.clusterCloneData.taskInstanceCount}</span></div>
                                <div className='grey'><span className='reviewTitles'>DNS Name: </span><span className='reviewValue'>{this.props.clusterCloneData.dnsName}</span></div>
                                <div className='grey'><span className='reviewTitles'>Is Prod: </span><span className='reviewValue'>{`${this.props.clusterCloneData.isProd}` + ''}</span></div>
                                <div className='grey'><span className='reviewTitles'>DNS Flip Completed: </span><span className='reviewValue'>{`${this.props.clusterCloneData.dnsFlipCompleted}` + ''}</span></div>
                                <div className='grey'><span className='reviewTitles'>Status: </span><span className='reviewValue'>{this.props.clusterCloneData.status}</span></div>
                                <div className='grey'><span className='reviewTitles'>Auto AMI Rotation: </span><span className='reviewValue'>{`${this.props.clusterCloneData.autoAmiRotation}` + ''}</span></div>
                                <div className='grey'><span className='reviewTitles'>Auto-Pilot Window Start: </span><span className='reviewValue'>{this.props.clusterCloneData.autopilotWindowStart}</span></div>
                                <div className='grey'><span className='reviewTitles'>Auto-Pilot Window End: </span><span className='reviewValue'>{this.props.clusterCloneData.autopilotWindowEnd}</span></div>
                                <div className='grey'><span className='reviewTitles'>AMI Rotation SLA Days: </span><span className='reviewValue'>{this.props.clusterCloneData.amiRotationSlaDays}</span></div>
                                {this.props.clusterCloneData.instanceGroup == undefined || this.props.clusterCloneData.instanceGroup == null || this.props.clusterCloneData.instanceGroup.length == 0 ?
                                <div className='grey'><span className='reviewTitles'>Auto-Scaling: </span><span className='reviewValue'>off</span></div> : 
                                <div className='grey'><span className='reviewTitles'>Auto-Scaling: </span><span className='reviewValue'>on</span></div>}
                                {this.props.clusterCloneData.instanceGroup !== undefined && this.props.clusterCloneData.instanceGroup !== null && 
                                    <div className='grey'><span className='reviewTitles'>Auto-Scaling Instance Group: </span><span className='reviewValue'>{this.props.clusterCloneData.instanceGroup}</span></div>
                                }
                                {this.props.clusterCloneData.instanceGroup !== undefined && this.props.clusterCloneData.instanceGroup !== null &&
                                    <div className='grey'><span className='reviewTitles'>Auto-Scaling Min: </span><span className='reviewValue'>{this.props.clusterCloneData.min}</span></div>
                                }
                                {this.props.clusterCloneData.instanceGroup !== undefined && this.props.clusterCloneData.instanceGroup !== null &&
                                    <div className='grey'><span className='reviewTitles'>Auto-Scaling Max: </span><span className='reviewValue'>{this.props.clusterCloneData.max}</span></div>
                                }
                                
                            </div>
                        }
                    </div>
                </div>
            </div>
        </div>

        )
    }
    addDataDetail() {
        this.state.dataDetails.push(this.props.data)
        this.setState({
            dataDetail: this.state.dataDetails
        })
    }
    componentDidMount() {
        this.props.fetchClusterClone(this.props.data.clusterId, this.props.token)
    }

    componentWillUnmount() {

    }
}

const mapStateToProps = state => {
    return {
        clusterCloneData: state.emrMetadataData.clusterCloneData
    }
}

const mapDispatchToProps = dispatch => {
    return {
        fetchClusterClone: (id, token) => {dispatch(fetchClusterClone(id, token))}
    }
}

export default connect(mapStateToProps, mapDispatchToProps)(ModalCreateClusterDetail)