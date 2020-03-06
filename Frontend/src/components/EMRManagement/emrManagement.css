import React, { Component } from 'react'
import { Grid, GridColumn as Column, GridDetailRow, GridToolbar, GridCell } from '@progress/kendo-react-grid'
import { process } from '@progress/kendo-data-query'
import { ExcelExport } from '@progress/kendo-react-excel-export'
import { Tab, Tabs, Menu, MenuItem, Popover, Position} from '@blueprintjs/core'
import { debounce } from 'lodash';
import emrStatusCell from './emrStatusCell'
import emrStepStatusCell from './emrStepStatusCell'
import amirotationStatus from './AMIRotationDays'
import './emrManagement.css'
import SubmitActionButtonCell from './SubmitActionButtonCell'
import CreateCluster from './CreateCluster'
import { withState } from '../../utils/components/with-state.jsx';

import '@progress/kendo-theme-bootstrap/scss/all.scss';

const CLUSTER_REFRESH_TIME = 1000 * 60 * 5;

const StatefulGrid = withState(Grid);

/**
 * Component to show extra information about the cluster and creation process in the expanded version of the kendo table.
 */
export class StepsStatus extends GridDetailRow {
  constructor(props){
    super(props)
  }

  render() {
    const stepsStatusData = this.props.stepsStatusData[this.props.dataItem.clusterId]
    if(stepsStatusData && stepsStatusData.length) {
      return (
        <div className='step-status-component'>
          <Grid
            data={stepsStatusData}
            resizable={true}
            reorderable={true}
            style={{ fontSize: '13px' }}
          >
            <Column field="stepId" title="Step ID" width="auto" />
            <Column field="name" title="Step Name" width="auto" />
            <Column field="status" title="Step Status" width="auto" cell={emrStepStatusCell} />
            <Column field="stepType" title="Step Type" width="auto" />
            <Column field="creationTimestamp" title="Creation Timestamp" width="auto" />
          </Grid>
        </div>
      )
    } else {
      return (
        <div>
          <Grid
            resizable={true}
            reorderable={true}
            style={{ fontSize: '13px' }}
          >
            <Column field="stepId" title="Step ID" width="auto" />
            <Column field="name" title="Step Name" width="auto" />
            <Column field="status" title="Step Status" width="auto" cell={emrStepStatusCell} />
            <Column field="stepType" title="Step Type" width="auto" />
            <Column field="creationTimestamp" title="Creation Timestamp" width="auto" />
          </Grid></div>
      )
    }
  }
}

/**
 * EMR Management Component
 */
export default class EMRManagement extends Component {
  showCreateTab = false;
  showTerminateTab = false;
  showAddTab = false;
  showRotateTab = false;
  showTestSuitesTab = false;
  account_range = '';
  constructor(props) {
    super(props)
    this.state = {
      data: this.props.data,
      show: false,
      primaryTab: 'all',
      primaryAction: 'allClusters',
      tabHeading: 'View All Clusters',
      uiListData: this.props.uiListData,
      clusterId: ''
    }

    this.createGroupState = this.createGroupState.bind(this)
    this.dataStateChange = this.dataStateChange.bind(this)
    this.expandChange = this.expandChange.bind(this)
    this.export = this.export.bind(this)
    this.handlePrimaryTabChange = this.handlePrimaryTabChange.bind(this)
    this.renderEMRTable = this.renderEMRTable.bind(this)
    GridCell.role = this.props.role
    GridCell.fullName= this.props.fullName
    GridCell.postAddSteps = this.postAddSteps.bind(this)
    GridCell.postTerminateCluster = this.postTerminateCluster.bind(this)
    GridCell.postRotateAMI = this.postRotateAMI.bind(this)
    GridCell.postDNSFlip = this.postDNSFlip.bind(this);
    this.postCreateCluster= this.props.postCreateCluster
    this.handleActionBtnClick = this.handleActionBtnClick.bind(this);
    this.fetchAllClusterMetaData = this.fetchAllClusterMetaData.bind(this);
    this.fetchClusterWiseMetaData = this.fetchClusterWiseMetaData.bind(this);
    this.handleTabs = this.handleTabs.bind(this);
    this.getUIDropdownData = debounce(this.getUIDropdownData.bind(this), 2500, false);
    this.getRefreshButton = this.getRefreshButton.bind(this);
  }

  render() {
    const dropdownContent = (
      this.props.uiListData.accounts && <Menu>
        {
          this.props.uiListData.accounts.map(n => {
            return <MenuItem text={n} key={n} onClick={() => {
              this.account_range = n;
              this.props.handleRangeChange('accounts',n)
            }
          }/>
          })
        }
      </Menu>
    );
    this.handleTabs();
    return (
    <div className='emr-management-component'>
        {this.props.activeSpinner ? <div active={this.props} className='loader-wrapper'>
          <div className='loader' />
        </div>
        : null}
        <h2 style={{ marginLeft: '35px' }}>EMR <i className='material-icon title-arrow'>double_arrow</i> <span style={{ color: '#0097E4' }}>Orchestration </span><i className='material-icon title-arrow'>double_arrow</i><span style={{ color: '#7a7a7a' }}> {this.state.tabHeading}</span></h2>
        {/* Primary Tabs */}
          <div className='actions'>
            <ul>
              <li><button className={`actionBtn ${this.state.primaryAction == 'allClusters' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('allClusters')}><span>View Workflows</span></button></li>
              {this.showCreateTab ? <li><button className={`actionBtn ${this.state.primaryAction == 'createCluster' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('createCluster')}><span>Create Cluster</span></button></li> : null}
              {this.showCreateTab ? <li><button className={`actionBtn ${this.state.primaryAction == 'cloneCluster' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('cloneCluster')}><span>Clone Cluster</span></button></li> : null}
              {this.showTerminateTab ? <li><button className={`actionBtn ${this.state.primaryAction == 'terminateCluster' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('terminateCluster')}><span>Terminate Cluster</span></button></li> : null}
              {this.showAddTab ? <li><button className={`actionBtn ${this.state.primaryAction == 'addStep' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('addStep')}><span>Add Step</span></button></li> : null }
              {this.showRotateTab ? <li><button className={`actionBtn ${this.state.primaryAction == 'rotateAMI' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('rotateAMI')}><span>Rotate AMI</span></button></li> : null}
              {this.showTestSuitesTab ? <li><button className={`actionBtn ${this.state.primaryAction == 'testSuites' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('testSuites')}><span>Test Suites</span></button></li> : null }
              {this.showRotateTab ? <li><button className={`actionBtn ${this.state.primaryAction == 'dnsFlip' ? 'active1' : ''}`} onClick={() => this.handleActionBtnClick('dnsFlip')}><span>Flip To Production</span></button></li> : null }
            </ul>
          </div>
          <div className={`actionContent ${this.state.primaryAction == 'createCluster' ? 'actionContentCluster' : ''}`}>
            {this.state.primaryAction == 'createCluster' && !this.props.uiListFetching && this.props.uiListSuccess ?
              <div>
                {!this.props.globalJiraFetching && <CreateCluster 
                  {...this.props} 
                  clusterId={this.state.clusterId}
                /> }

              </div> :
              <div className='tabContent'>
                <Tabs className='line' id="primary-tab" selectedTabId={this.state.primaryTab} onChange={this.handlePrimaryTabChange}>
                  <Tab id="all" title="All Clusters" />
                  <Tab id="exploratory" title="Exploratory Clusters" />
                  <Tab id="scheduled" title="Schedule Clusters"/>
                  <Tab id="transient" title="Transient Clusters"/>
                  <Tab id="accounts">
                    <Popover content={dropdownContent} position={Position.BOTTOM}>
                      <li className="bp3-tab" role="tab" aria-selected="true">Select an Account</li>
                    </Popover>
                  </Tab>
            </Tabs>
            {
              this.props.fetching || this.props.uiListFetching ?
              <div className='loader-wrapper'>
                <div className='loader' />
              </div>
                :
                <div>
                  <ExcelExport
                  data={this.props.data}
                  ref={(exporter) => { this._export = exporter; }}
                >
                  {
                    this.renderEMRTable(this.state.primaryTab, this.shortlistData(this.props.data))
                  }
                </ExcelExport>
              </div>

            }
              </div>
            }

          </div>
        </div>
    )
  }

  renderEMRTable = (primaryTab, tableData) => {
    return (
      <div>
        <div>
          {
            this.props.role !== 'readonly' ?
            <div className="export-container">
              <button title='Download data into an excelsheet' className='exportBtn' onClick={this.export}>
                <i className='material-icons'>cloud_download</i>
                <span>Export to Excel</span>
              </button>
              {
                this.getRefreshButton(primaryTab)
              }
            </div>:
            <div/>
          }
        </div>
        {
          this.showTable(tableData)
        }
      </div>
    )
  }

  getRefreshButton = primaryTab => {
    switch (primaryTab) {
      case 'all':
        return (
          <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={this.fetchAllClusterMetaData}>
              <i className='material-icons'>refresh</i>
              <span>Refresh Table</span>
          </button>
        )
      case 'exploratory':
        return (
          <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.fetchClusterWiseMetaData('exploratory')}>
              <i className='material-icons'>refresh</i>
              <span>Refresh Table</span>
          </button>
        )
      case 'scheduled':
        return (
          <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.fetchClusterWiseMetaData('scheduled')}>
              <i className='material-icons'>refresh</i>
              <span>Refresh Table</span>
          </button>
        )
      case 'transient':
        return (
          <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.fetchClusterWiseMetaData('transient')}>
              <i className='material-icons'>refresh</i>
              <span>Refresh Table</span>
          </button>
        )
      case 'accounts':
        return (
          <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.props.handleRangeChange('accounts', this.account_range)}>
              <i className='material-icons'>refresh</i>
              <span>Refresh Table</span>
          </button>
        )
    }
  }

  showTable = tableData => {
    let EMR_ID = <span style={{ fontSize: '15px', fontWeight: '600' }}>EMR ID</span>
    let EMR_Name = <span style={{ fontSize: '15px', fontWeight: '600' }}>EMR Name</span>
    let Cluster_Status = <span style={{ fontSize: '15px', fontWeight: '600' }}>Cluster Status</span>
    let Account_ID = <span style={{ fontSize: '15px', fontWeight: '600' }}>Account ID</span>
    let Created_by = <span style={{ fontSize: '15px', fontWeight: '600' }}>Created By</span>
    let Creation_Timestamp = <span style={{ fontSize: '15px', fontWeight: '600' }}>Created At</span>
    let Actions = <span style={{ fontSize: '15px', fontWeight: '600' }}>Actions</span>
    let Workflows = <span style={{ fontSize: '15px', fontWeight: '600' }}>Workflows</span>
    let AMIRotationDays = <span style={{ fontSize: '15px', fontWeight: '600' }}>AMI Rotation Days</span>
    let AutoPilot = <span style={{ fontSize: '15px', fontWeight: '600' }}>Auto Pilot Window & Days</span>
    let DNS_Name = <span style={{ fontSize: '15px', fontWeight: '600' }}>DNS Name</span>
    let JIRA_ID = <span style={{ fontSize: '15px', fontWeight: '600' }}>JIRA ID</span>
     
    let allClusters_id_WIDTH = "200px"
    let rotateAMI_id_WIDTH = "145px"
    let default_id_WIDTH = "260px"

    let allClusters_name_WIDTH = "325px"
    let rotateAMI_name_WIDTH = "190px"
    let default_name_WIDTH = "440px"


    let allClusters_status_WIDTH = "145px"
    let rotateAMI_status_WIDTH = "224px"
    let default_status_WIDTH = "150px"
    
    let rotateAMI_account_WIDTH = "120px"
    let default_account_WIDTH = "160px"

    let default_createdBy_WIDTH = "140px"
    let rotateAMI_createdBy_WIDTH = "120px"

    let default_createdAt_WIDTH = "115px"
    let rotateAMI_createdAt_WIDTH = "115px"
    let default_RotationDays_WIDTH = '130px'
    return (
      <StatefulGrid
              data={tableData}
              detail={(props) => <StepsStatus {...props} stepsStatusData={this.props.stepsStatusData} />}
              resizable
              groupable={true}
              onDataStateChange={this.dataStateChange}
              {...this.state.dataState}
              onExpandChange={this.expandChange}
              expandField="expanded"
              style={{ height: '80vh', fontSize: '13px' }}
          >
          {this.state.primaryAction === 'terminateCluster' ? <Column field="clusterId" title={EMR_ID} width={allClusters_id_WIDTH} /> : 
            this.state.primaryAction === 'rotateAMI' || this.state.primaryAction === 'allClusters' ? <Column field="clusterId" title={EMR_ID} width={rotateAMI_id_WIDTH} /> : 
             <Column field="clusterId" title={EMR_ID} width={default_id_WIDTH} />
          }
          {
            this.state.primaryAction === 'allClusters' ? <Column field="clusterName" title={EMR_Name} width='170px' /> :
          this.state.primaryAction === 'terminateCluster' ?  <Column field="clusterName" title={EMR_Name} width={allClusters_name_WIDTH} />  : 
            this.state.primaryAction === 'rotateAMI' ?  <Column field="clusterName" title={EMR_Name} width={rotateAMI_name_WIDTH} />  : 
            this.state.primaryAction === 'dnsFlip' ? <Column field="clusterName" title={EMR_Name} width="240px" />  :
            <Column field="clusterName" title={EMR_Name} width={default_name_WIDTH} /> 
          }

          {
            this.state.primaryAction === "rotateAMI" || this.state.primaryAction === 'allClusters' ? <Column field="status" title={Cluster_Status} width={allClusters_status_WIDTH} cell={emrStatusCell} /> : 
            <Column field="status" title={Cluster_Status} width={default_status_WIDTH} cell={emrStatusCell} />
          }     

          {this.state.primaryAction === 'rotateAMI' || this.state.primaryAction === 'allClusters' ?  <Column field="account" title={Account_ID} width={rotateAMI_account_WIDTH}/> : <Column field="account" title={Account_ID} width={default_account_WIDTH}/>}

          {(this.state.primaryAction === 'allClusters' || this.state.primaryAction === 'dnsFlip') && <Column field="dnsName" title={DNS_Name} width='200px' /> }
          
          {this.state.primaryAction === 'allClusters' && <Column field="requestTicket" title={JIRA_ID} width='110px' /> }

          {this.state.primaryAction === 'rotateAMI' ? <Column field="createdBy" title={Created_by} width={rotateAMI_createdBy_WIDTH} /> :
          <Column field="createdBy" title={Created_by} width={default_createdBy_WIDTH} /> }

          {this.state.primaryAction === 'rotateAMI' ? <Column field="creationTimestamp" title={Creation_Timestamp} width={rotateAMI_createdAt_WIDTH} /> :
          <Column field="creationTimestamp" title={Creation_Timestamp} width={default_createdAt_WIDTH} /> }

          {this.state.primaryAction === "rotateAMI" ? <Column field='rotationDaysToGo' title={AMIRotationDays} width={default_RotationDays_WIDTH} cell={amirotationStatus}/> : null}

          {this.state.primaryAction === 'rotateAMI' ? <Column field="actions" title={AutoPilot} width="230px" cell={(props) => <SubmitActionButtonCell {...props} {...this.props} action='autopilot' tab={this.state.primaryTab} handleActionBtnClick={this.handleActionBtnClick} />}/> : null }

          {this.state.primaryAction === "allClusters" ? <Column field="actions" title={Workflows} width="auto" cell={(props) => <SubmitActionButtonCell {...props} {...this.props} action={this.state.primaryAction} tab={this.state.primaryTab} handleActionBtnClick={this.handleActionBtnClick} />}/> :
          <Column field="actions" title={Actions} width="auto" cell={(props) => <SubmitActionButtonCell {...props} {...this.props} action={this.state.primaryAction} tab={this.state.primaryTab} handleActionBtnClick={this.handleActionBtnClick} />}/> }
        </StatefulGrid>
    )
  }

  handleTabs = () => {
    if (this.props.superAdmin) {
      this.showCreateTab = true;
      this.showAddTab = true;
      this.showTerminateTab = true;
      this.showRotateTab = true;
      this.showTestSuitesTab = true;
      return;
    }
    let emrRoles = this.props.dccRoles.filter(x => x.serviceType === 'EMR')[0];
    if (emrRoles !== undefined) {
      emrRoles.roles.forEach( x => {
        switch(x.name) {
          case 'createcluster':
            this.showCreateTab = true;
            break;
          case 'addstep':
            this.showAddTab = true;
            break;
          case 'terminatecluster':
            this.showTerminateTab = true;
            break;
          case 'rotateami':
            this.showRotateTab = true;
            break;
          case 'runclusterhealthchecks':
            this.showTestSuitesTab = true;
            break;
        }
      })
    }
  }

  shortlistData = data => {
    if (this.props.superAdmin) return data;
    let emrRoles = this.props.dccRoles.filter(x => x.serviceType === 'EMR')[0];

    if (emrRoles === undefined) return [];

    let roleSegments = [];
    emrRoles.roles.forEach((value) => {
      if (this.state.primaryAction === 'terminateCluster') {
        if (value.name === 'terminatecluster') {
          roleSegments = value.segments;
        }
      } else if (this.state.primaryAction == 'rotateAMI' || this.state.primaryAction == 'dnsFlip') {
        if (value.name == 'rotateami') {
          roleSegments = value.segments;
        }
      } else if (this.state.primaryAction === 'addStep') {
        if (value.name === 'addstep') {
           roleSegments = value.segments;
        }
      } else if (this.state.primaryAction === 'testSuites') {
        if (value.name === 'runclusterhealthchecks') {
          roleSegments = value.segments;
        }
      } else if (this.state.primaryAction === 'cloneCluster') {
        if (value.name === 'createcluster') {
          roleSegments = value.segments;
        }
      } else {
        value.segments.forEach(x => {
          roleSegments.push(x);
        })
      }
    });

    let roleMap = {};
    roleSegments.forEach((value, index) => {
      let accounts = [];
      value.accounts.forEach((act, index) => {
        accounts.push(act.accountId);
      })
      roleMap[value["segmentName"].toLowerCase()] = accounts;
    })
    let newData = data;
    
    newData = newData.filter((value) => {
      if (value.segment in roleMap) {
        if (roleMap[value.segment].includes(value.account)) {
          return value;
        }
      }
    });
    return newData;
  }

  fetchAllClusterMetaData() {
    console.log("Refresh all data");
    this.props.fetchAllClusterMetaData(this.props.token);
  }

  fetchClusterWiseMetaData(type) {
    console.log("Refreshing " + type + " data");
    this.props.fetchClusterWiseMetaData(type, this.props.token);
  }

  postAddSteps(data,token) {
    // this.props.postAddSteps(data,token)
    this.props.handleSpinnerActivity(data, token, 'add')
  }

  postTerminateCluster(data,token) {
    // this.props.postTerminateCluster(data,token)
    this.props.handleSpinnerActivity(data, token, 'terminate')
  }

  postRotateAMI(data,token) {
    // this.props.postRotateAMI(data,token)
    this.props.handleSpinnerActivity(data, token, 'rotate')
  }

  postCreateCluster(data,token) {
    // this.props.postCreateCluster(data,token)
    this.props.handleSpinnerActivity(data, token, 'create');
  }

  postDNSFlip(data, token) {
    this.props.handleSpinnerActivity(data, token, 'dnsFlip');
  }

  _export
  export() {
    this._export.save()
  }

  handlePrimaryTabChange(newTabId, prevTabId) {
    this.setState({
      ...this.state,
      primaryTab: newTabId
    })
    if (newTabId !== 'accounts')
      this.props.handleRangeChange(newTabId,'');

  }

  handleActionBtnClick(action, type, cluster_id) {
    let heading = '';
    if (action === 'allClusters') {
      heading = 'View All Clusters'
    } else if (action === 'createCluster') {
      heading = 'Create Cluster'
    } else if (action === 'addStep') {
      heading = 'Add Step'
    } else if (action === 'terminateCluster') {
      heading = 'Terminate Cluster'
    } else if (action === 'rotateAMI') {
      heading = 'Rotate AMI'
    } else if (action === 'testSuites') {
      heading = 'Run Test Suites'
    } else if (action === 'dnsFlip') {
      heading = 'DNS Flip'
    } else if (action === 'cloneCluster') {
      heading = 'Clone Cluster'
    }

    if (type == 'clone') {
      this.setState({
        ...this.state,
        primaryAction: action,
        tabHeading: heading,
        clusterId: cluster_id
      })
    } else {
      this.setState({
        ...this.state,
        primaryAction: action,
        tabHeading: heading
      })
    }
  }

  createGroupState(dataState) {
    //console.log('inside createGroup', this.props);
    return {
      result: process(this.props.data, dataState),
      dataState: dataState
    }
  }

  dataStateChange(event) {
    this.setState({
      ...this.state,
      ...this.createGroupState(event.data)
    })
  }

  expandChange(event) {
    event.dataItem.expanded = event.value;
    let cluster = event.dataItem.clusterName
    let clusterId = event.dataItem.clusterId
    this.setState({
      ...this.state,
      result: Object.assign({}, this.state.result),
      dataState: this.state.dataState
    });
    event.value && this.props.handleStepsStatus(cluster,clusterId);

    if (!event.value || event.dataItem.stepsStatusData) {
      return;
    }
  }
  getUIDropdownData() {
  }

  componentDidUpdate(prevProps) {
    if (prevProps.data && this.props.data && prevProps.data !== this.props.data) {
      this.setState({
        ...this.state,
        ...this.createGroupState({
          take: 100,
          group: Array(0),
          sort: undefined,
          filter: undefined,
          skip: 0
        })
      })
    }

    clearInterval(this.interval)

    if (this.state.primaryTab === "all") {
      this.interval = setInterval(this.fetchAllClusterMetaData, CLUSTER_REFRESH_TIME)
    } else if (this.state.primaryTab === "exploratory") {
      this.interval = setInterval(() => this.fetchClusterWiseMetaData('exploratory'), CLUSTER_REFRESH_TIME)
    } else if (this.state.primaryTab === "scheduled") {
      this.interval = setInterval(() => this.fetchClusterWiseMetaData('scheduled'), CLUSTER_REFRESH_TIME)
    } else if (this.state.primaryTab === "transient") {
      this.interval = setInterval(() => this.fetchClusterWiseMetaData('transient'), CLUSTER_REFRESH_TIME)
    } else if (this.state.primaryTab === "accounts") {
      this.interval = setInterval(() => this.props.handleRangeChange('accounts', this.account_range), CLUSTER_REFRESH_TIME)
    }


  }

  componentDidMount(prevProps) {
    this.setState({
      ...this.state,
      ...this.createGroupState({
        take: 100,
        group: Array(0),
        sort: undefined,
        filter: undefined,
        skip: 0
      })
    })
    this.props.fetchUIDropdownList(this.props.token)
    console.log('Comp EMR Managemnent', this)
  }

  componentWillReceiveProps(props, state) {
    this.forceUpdate();
  }
}
