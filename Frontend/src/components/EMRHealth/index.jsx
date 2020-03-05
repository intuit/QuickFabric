import React, { Component } from 'react';
import { Grid, GridColumn as Column, GridToolbar } from '@progress/kendo-react-grid';
import { process } from '@progress/kendo-data-query';
import { ExcelExport } from '@progress/kendo-react-excel-export';
import { ProgressBar, Spinner, Tab, Tabs, Menu, MenuItem ,Popover, Position} from '@blueprintjs/core';
import emrHealthCell from './emrHealthCell';
import './emrHealth1.scss';
import './emrHealth.css';
import ObservabilityGraph from './observabilityGraph/ObservabilityGraph';
import Toggle from 'react-toggle';
import './observabilityGraph/toggle.scss';
import ToggleText from './ToggleText';
import { withState } from '../../utils/components/with-state.jsx';

import { matchPath } from 'react-router';
const StatefulGrid = withState(Grid);

const CLUSTER_REFRESH_TIME = 1000 * 10

export default class EMRHealth extends Component {
  segment_filters = [];
  account_range = '';
  constructor(props) {
    super(props)
  
    this.state = {
      data: this.props.data,
      emrHealthData: this.props.emrHealthData,
      show: false,
      primaryTab: 'all',
      graphView: true,
      updateDate: '',
      updated: false,
      refreshTimer: 120,
      refreshInterval: {}
    }

    this.createGroupState = this.createGroupState.bind(this)
    this.dataStateChange = this.dataStateChange.bind(this)
    this.expandChange = this.expandChange.bind(this)
    this.export = this.export.bind(this)
    this.handlePrimaryTabChange = this.handlePrimaryTabChange.bind(this)
    this.renderTable = this.renderTable.bind(this)
    this.handleToggleChange = this.handleToggleChange.bind(this)
    this.fetchDataForGraph = this.fetchDataForGraph.bind(this);
    this.addColorToGraph = this.addColorToGraph.bind(this);
    this.shortlistData = this.shortlistData.bind(this);
    this.refreshTimer = this.refreshTimer.bind(this);
    this.checkTimer = this.checkTimer.bind(this);
    this.formatDate = this.formatDate.bind(this);
  }

  render() {
    this.formatDate()
    const dropdownContent = (
      this.props.uiListData.accounts && <Menu>
        {
          this.props.uiListData.accounts.map(n => {
            return <MenuItem text={n} key={n} onClick={() => {
              this.account_range = n;
              this.props.handleRangeChange('accounts',n)
            }}/>
          })
        }
      </Menu>
    );

    let admin = true;

    return (
    <div className="emr-benchmarking-component">
        <h2 style={{ marginBottom: "30px" }}>EMR <i className="material-icon title-arrow">double_arrow</i> <span style={{ color: "#0097E4" }}>Observability</span></h2> 
        <div className="header-row">
          <label className="toggle-blue-container">
            <Toggle
              defaultChecked={this.state.graphView}
              icons={{
                checked: <ToggleText label="Graph" keyword="Table" text="tableText" />,
                unchecked: <ToggleText label="Table" keyword="Table" text="tableText" />
              }}
              onChange={this.handleToggleChange}
            />
          </label>
          <div className="empty-container"></div>
          <label className="refresh-container">
            <div className="l-1"><p>Next refresh</p></div><div className="l-2">:</div><div className="l-3"><span>{this.state.refreshTimer} <span className="seconds-span">sec</span></span></div>
            <div className="last-refreshed">
              <div className="l-1"> <span>Last refreshed</span> </div><div className="l-2">:</div><div className="l-3">{this.state.updateDate}</div>
                
              </div>
          </label>
        </div>

        {this.state.graphView ? this.props.fetching || (Object.entries(this.props.uiListData).length === 0 && this.props.uiListData.constructor === Object) ?
              <Spinner /> :
              <ObservabilityGraph 
                  token={this.props.token} 
                  data={this.fetchDataForGraph(this.props.data).graphData} 
                  dataMap={this.fetchDataForGraph(this.props.data).dataMap} 
                  superAdmin={this.props.superAdmin}
                  admin={admin}
                  segmentFilters={this.segment_filters}
                  segs={this.props.uiListData.segments}
                  accounts={this.props.uiListData.accounts}
                  segments={this.props.uiListData.response.segments}
                  handleFilterClick={this.handleFilterClick} /> :
          <div>
            <Tabs className='line' id="primary-tab" selectedTabId={this.state.primaryTab} onChange={this.handlePrimaryTabChange}>
                <Tab id="all" title="All Clusters" />
                <Tab id="exploratory" title="Exploratory Clusters" />
                <Tab id="schedule" title="Schedule Clusters"/>

                <Tab id="accounts">
                  <Popover content={dropdownContent} position={Position.BOTTOM}>
                    <li className="bp3-tab" role="tab" aria-selected="true">Select an Account</li>
                  </Popover>
                </Tab>
          </Tabs>
          {
            
            this.props.fetching ? 
              <Spinner />
              :
              <ExcelExport
                data={this.props.data}
                ref={(exporter) => { this._export = exporter; }}
              >
                {
                  this.renderTable(this.state.primaryTab)
                }
              </ExcelExport>
          }
            </div> 
        }
    </div>
    )
  }

  getRefreshButton = primaryTab => {
    switch(primaryTab) {
      case 'all': 
        return (
          <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.props.handleRangeChange('all')}>
                <i className='material-icons'>refresh</i>
                <span>Refresh Table</span>
          </button>
        )
      case 'exploratory':
        return (
          <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.props.handleRangeChange('exploratory')}>
                <i className='material-icons'>refresh</i>
                <span>Refresh Table</span>
              </button>
        )
      case 'schedule':
        return (
          <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.props.handleRangeChange('schedule')}>
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

  renderEMRTable = primaryTab => {
    return (
      <div>
        <div style={{ position: 'relative', top: '10px' }}>
            {
            this.props.role !== 'readonly' ?
            <div>
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

        }
      </div>
    )
  }

  showTable = () => {
    let EMR_ID = <span style={{ fontSize: '15px', fontWeight: '600' }}>EMR #</span>
    let EMR_Name = <span style={{ fontSize: '15px', fontWeight: '600' }}>EMR Name</span>
    let RM_Url = <span style={{ fontSize: '15px', fontWeight: '600' }}>RM URL</span>
    let EMR_Status = <span style={{ fontSize: '15px', fontWeight: '600' }}>EMR Status</span>
    let Account_ID = <span style={{ fontSize: '15px', fontWeight: '600' }}>Account ID</span>
    let Active_Nodes = <span style={{ fontSize: '15px', fontWeight: '600' }}>Active # Nodes</span>
    let Res_Avail_Mem = <span style={{ fontSize: '15px', fontWeight: '600' }}>Resources Available Memory %</span>
    let Res_Avail_Cores = <span style={{ fontSize: '15px', fontWeight: '600' }}>Resources Available Cores %</span>
    let Creation_Timestamp = <span style={{ fontSize: '15px', fontWeight: '600' }}>Creation Timestamp</span>
    let Refresh_Timestamp = <span style={{ fontSize: '15px', fontWeight: '600' }}>Refresh Timestamp</span>
    let Cost = <span style={{ fontSize: '15px', fontWeight: '600' }}>Cost(Current Month)</span>
    let Wavefront_Url = <span style={{ fontSize: '15px', fontWeight: '600' }}>Wavefront URL</span>
    let Apps_Failed = <span style={{ fontSize: '15px', fontWeight: '600' }}>Apps Failed #</span>
    let Apps_Pending = <span style={{ fontSize: '15px', fontWeight: '600' }}>Apps Pending #</span>
    let Apps_Running = <span style={{ fontSize: '15px', fontWeight: '600' }}>Apps Running #</span>
    let Apps_Succeeded = <span style={{ fontSize: '15px', fontWeight: '600' }}>Apps Succeeded #</span>

    return (
      <StatefulGrid
          style={{ 'marginTop': 20 , height: '75vh', fontSize: '13px' }}
          resizable={true}
          reorderable={true}
          filterable={true}
          sortable={true}
          pageable={{ pageSizes: true }}
          groupable={true}

          data={this.state.result !== undefined ? this.shortlistData(this.state.result.data) : this.state.result}
          onDataStateChange={this.dataStateChange}
          {...this.state.dataState}

          onExpandChange={this.expandChange}
          expandField="expanded"
          
        >
          <Column field="emrId" title={EMR_ID} width="180px" />
          <Column field="emrName" title={EMR_Name} width="180px" />
                <Column field="rmUrl" title={RM_Url} width="200px"  cell={ (props) => 
                          <td>
                            <a href={props.dataItem.rmUrl} target="_blank" style={{color: "#106ba3"}}>
                            {props.dataItem.rmUrl}
                            </a>
                          </td>} 
                />
                
               
                <Column field="emrStatus" title={EMR_Status} width="180px" cell={emrHealthCell} />
                <Column field="account" title={Account_ID} width="180px" />
                <Column field="activeNodes" title={Active_Nodes} width="180px" filter="numeric" />
                <Column  field="availableMemoryPercentage" title={Res_Avail_Mem} width="250px" filter="numeric" 
                
                cell={ (props) => 
                  <td>
              
                    <ProgressBar   animate="false" stripes="false"  intent= 
                    {
                      
                    props.dataItem.availableMemoryPercentage/100 > 0.5 ?
                    "SUCCESS" : (props.dataItem.availableMemoryPercentage/100 < 0.5 && props.dataItem.availableMemoryPercentage/100 > 0.3) ? 
                    "WARNING" : 
                    "DANGER"     
                    } 
                    value={props.dataItem.availableMemoryPercentage/100} />
                
                    <span className="my-span"  > {props.dataItem.availableMemoryPercentage}%
                 

                    </span>
                  </td>} 

                />
                <Column field="availableCoresPercentage" title={Res_Avail_Cores} width="250px" filter="numeric" 
                
                cell={ (props) => 
                  <td>
                    <ProgressBar animate="true" stripes="true" 
                    intent= 
                    {props.dataItem.availableCoresPercentage/100 > 0.5 ?
                    "SUCCESS" : (props.dataItem.availableCoresPercentage/100 < 0.5 && props.dataItem.availableCoresPercentage/100 > 0.3) ? 
                    "WARNING" : 
                    "DANGER" }   
                    value={props.dataItem.availableCoresPercentage/100} 
                    />
                    <span className="my-span">{Math.round(props.dataItem.availableCoresPercentage , 3) }% 
                    
                    </span>
                  </td>} 

                />
               
               
               <Column field="clusterCreateTimestamp" title={Creation_Timestamp} width="180px" />
                <Column field="refreshTimestamp" title={Refresh_Timestamp} width="300px"  cell={ (props) =>
                   <td><div>{props.dataItem.refreshTimestamp}</div></td>
                }/>
                  <Column field="cost" title={Cost}  width="150px" 
                cell={ (props) => 
                  <td>
                    <span >$ {props.dataItem.cost} </span>
                  </td>} 
                />

                <Column field="emrWfUrl" title={Wavefront_Url} width="200px"  cell={ (props) => 
                          <td>
                            <a href={props.dataItem.emrWfUrl} target="_blank" style={{color: "#106ba3"}}>
                            Wavefront URL
                            </a>
                          </td>} 
                />
                <Column field="appsFailed" title={Apps_Failed} width="180px" filter="numeric" />
                <Column field="appsPending" title={Apps_Pending} width="180px" filter="numeric" />
                <Column field="appsRunning" title={Apps_Running} width="180px" filter="numeric" />
                <Column field="appsSucceeded" title={Apps_Succeeded} width="180px" filter="numeric" />
            </StatefulGrid>
    )
  }

   renderTable(primaryTab) {
    let EMR_ID = <span style={{ fontSize: '15px', fontWeight: '600' }}>EMR #</span>
    let EMR_Name = <span style={{ fontSize: '15px', fontWeight: '600' }}>EMR Name</span>
    let RM_Url = <span style={{ fontSize: '15px', fontWeight: '600' }}>RM URL</span>
    let EMR_Status = <span style={{ fontSize: '15px', fontWeight: '600' }}>EMR Status</span>
    let Account_ID = <span style={{ fontSize: '15px', fontWeight: '600' }}>Account ID</span>
    let Active_Nodes = <span style={{ fontSize: '15px', fontWeight: '600' }}>Active # Nodes</span>
    let Res_Memory_Usage = <span style={{ fontSize: '15px', fontWeight: '600' }}>Memory Utilization %</span>
    let Res_Cores_Usage = <span style={{ fontSize: '15px', fontWeight: '600' }}>Cores Utilization %</span>
    let Creation_Timestamp = <span style={{ fontSize: '15px', fontWeight: '600' }}>Creation Timestamp</span>
    let Refresh_Timestamp = <span style={{ fontSize: '15px', fontWeight: '600' }}>Refresh Timestamp</span>
    let Cost = <span style={{ fontSize: '15px', fontWeight: '600' }}>Cost(Current Month)</span>
    let Wavefront_Url = <span style={{ fontSize: '15px', fontWeight: '600' }}>Wavefront URL</span>
    let Apps_Failed = <span style={{ fontSize: '15px', fontWeight: '600' }}>Apps Failed #</span>
    let Apps_Pending = <span style={{ fontSize: '15px', fontWeight: '600' }}>Apps Pending #</span>
    let Apps_Running = <span style={{ fontSize: '15px', fontWeight: '600' }}>Apps Running #</span>
    let Apps_Succeeded = <span style={{ fontSize: '15px', fontWeight: '600' }}>Apps Succeeded #</span>

    switch(primaryTab) {
      
      case 'all':
          
        return (
          <div>
            <div style={{ position: 'relative', top: '10px' }}>
            {
            this.props.role !== 'readonly' ?
            <div>
              <button title='Download data into an excelsheet' className='exportBtn' onClick={this.export}>
                <i className='material-icons'>cloud_download</i>
                <span>Export to Excel</span>
              </button>
              <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.props.handleRangeChange('all')}>
                <i className='material-icons'>refresh</i>
                <span>Refresh Table</span>
              </button>
            </div>:
            <div/>
          }
            </div>
          <Grid
          style={{ 'marginTop': 20 , height: '75vh', fontSize: '13px' }}
          resizable={true}
          reorderable={true}
          filterable={true}
          sortable={true}
          pageable={{ pageSizes: true }}
          groupable={true}

          data={this.state.result !== undefined ? this.shortlistData(this.state.result.data) : this.state.result}
          onDataStateChange={this.dataStateChange}
          {...this.state.dataState}

          onExpandChange={this.expandChange}
          expandField="expanded"
          
        >
          <Column field="emrId" title={EMR_ID} width="180px" />
          <Column field="emrName" title={EMR_Name} width="180px" />
                <Column field="rmUrl" title={RM_Url} width="200px"  cell={ (props) => 
                          <td>
                            <a href={props.dataItem.rmUrl} target="_blank" style={{color: "#106ba3"}}>
                            {props.dataItem.rmUrl}
                            </a>
                          </td>} 
                />
                
               
                <Column field="emrStatus" title={EMR_Status} width="180px" cell={emrHealthCell} />
                <Column field="account" title={Account_ID} width="180px" />
                <Column field="activeNodes" title={Active_Nodes} width="180px" filter="numeric" />
                <Column  field="memoryUsagePct" title={Res_Memory_Usage} width="250px" filter="numeric" 
                
                cell={ (props) => 
                  <td>
              
                    <ProgressBar animate="false" stripes="false"  intent= 
                    {
                      props.dataItem.memoryUsagePct/100 < 0.5 ?
                      "SUCCESS" : (props.dataItem.memoryUsagePct/100 >= 0.5 && props.dataItem.memoryUsagePct/100 < 0.7) ? 
                      "WARNING" : 
                      "DANGER"     
                    } 
                    value={props.dataItem.memoryUsagePct/100} />
                
                    <span className="my-span"  > {props.dataItem.memoryUsagePct}%
                 

                    </span>
                  </td>} 

                />
                <Column field="coresUsagePct" title={Res_Cores_Usage} width="250px" filter="numeric" 
                
                cell={ (props) => 
                  <td>
                    <ProgressBar animate="true" stripes="true" 
                    intent= 
                    {props.dataItem.coresUsagePct/100 < 0.5 ?
                    "SUCCESS" : (props.dataItem.coresUsagePct/100 >= 0.5 && props.dataItem.coresUsagePct/100 < 0.7) ? 
                    "WARNING" : 
                    "DANGER" }   
                    value={props.dataItem.coresUsagePct/100} 
                    />
                    <span className="my-span">{Math.round(props.dataItem.coresUsagePct , 3) }% 
                    
                    </span>
                  </td>} 

                />
               
               
               <Column field="clusterCreateTimestamp" title={Creation_Timestamp} width="180px" />
                <Column field="refreshTimestamp" title={Refresh_Timestamp} width="300px"  cell={ (props) =>
                   <td><div>{props.dataItem.refreshTimestamp}</div></td>
                }/>
                  <Column field="cost" title={Cost}  width="150px" 
                cell={ (props) => 
                  <td>
                    <span >$ {props.dataItem.cost} </span>
                  </td>} 
                />

                <Column field="emrWfUrl" title={Wavefront_Url} width="200px"  cell={ (props) => 
                          <td>
                            <a href={props.dataItem.emrWfUrl} target="_blank" style={{color: "#106ba3"}}>
                            Wavefront URL
                            </a>
                          </td>} 
                />
                <Column field="appsFailed" title={Apps_Failed} width="180px" filter="numeric" />
                <Column field="appsPending" title={Apps_Pending} width="180px" filter="numeric" />
                <Column field="appsRunning" title={Apps_Running} width="180px" filter="numeric" />
                <Column field="appsSucceeded" title={Apps_Succeeded} width="180px" filter="numeric" />
            </Grid>
            </div>
        )

      case 'exploratory':
      return (
        <div>
          <div style={{ position: 'relative', top: '10px' }}>
            {
            this.props.role !== 'readonly' ?
            <div>
              <button title='Download data into an excelsheet' className='exportBtn' onClick={this.export}>
                <i className='material-icons'>cloud_download</i>
                <span>Export to Excel</span>
              </button>
              <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.props.handleRangeChange('exploratory')}>
                <i className='material-icons'>refresh</i>
                <span>Refresh Table</span>
              </button>
            </div>:
            <div/>
          }
          </div>
        <Grid
        style={{ 'marginTop': 20 , height: '75vh'  }}
        resizable={true}
        reorderable={true}
        filterable={true}
        sortable={true}
        pageable={{ pageSizes: true }}
        groupable={true}

        data={this.state.result !== undefined ? this.shortlistData(this.state.result.data) : this.state.result}
        onDataStateChange={this.dataStateChange}
        {...this.state.dataState}

        onExpandChange={this.expandChange}
        expandField="expanded"
      >
        <Column field="emrId" title={EMR_ID} width="180px" />
          <Column field="emrName" title={EMR_Name} width="180px" />
              <Column field="rmUrl" title={RM_Url} width="200px"  cell={ (props) => 
                        <td>
                          <a href={props.dataItem.rmUrl} target="_blank" style={{color: "#106ba3"}}>
                          {props.dataItem.rmUrl}
                          </a>
                        </td>} 
              />
              
             
              <Column field="emrStatus" title={EMR_Status} width="180px" cell={emrHealthCell} />
                <Column field="account" title={Account_ID} width="180px" />
                <Column field="activeNodes" title={Active_Nodes} width="180px" filter="numeric" />
                <Column  field="memoryUsagePct" title={Res_Memory_Usage} width="250px" filter="numeric"
              
              cell={ (props) => 
                <td>
            
                  <ProgressBar   animate="false" stripes="false"  intent= 
                  {
                    
                  props.dataItem.memoryUsagePct/100 < 0.5 ?
                  "SUCCESS" : (props.dataItem.memoryUsagePct/100 >= 0.5 && props.dataItem.memoryUsagePct/100 < 0.7) ? 
                  "WARNING" : 
                  "DANGER"     
                  } 
                  value={props.dataItem.memoryUsagePct/100} />
              
                  <span className="my-span"  > {props.dataItem.memoryUsagePct}%
               

                  </span>
                </td>} 

              />
              <Column field="coresUsagePct" title={Res_Cores_Usage} width="250px" filter="numeric" 
              
              cell={ (props) => 
                <td>
                  <ProgressBar animate="true" stripes="true" 
                  intent= 
                  {props.dataItem.coresUsagePct/100 < 0.5 ?
                  "SUCCESS" : (props.dataItem.coresUsagePct/100 >= 0.5 && props.dataItem.coresUsagePct/100 < 0.7) ? 
                  "WARNING" : 
                  "DANGER" }   
                  value={props.dataItem.coresUsagePct/100} 
                  />
                  <span className="my-span">{Math.round(props.dataItem.coresUsagePct , 3) }% 
                 
                  </span>
                </td>} 

              />
             
             
             <Column field="clusterCreateTimestamp" title={Creation_Timestamp} width="180px" />
              <Column field="refreshTimestamp" title={Refresh_Timestamp} width="300px"  cell={ (props) =>
                 <td><div>{props.dataItem.refreshTimestamp}</div></td>
              }/>
                <Column field="cost" title={Cost}  width="150px" 
              cell={ (props) => 
                <td>
                  <span >$ {props.dataItem.cost} </span>
                </td>} 
              />

              <Column field="emrWfUrl" title={Wavefront_Url} width="200px"  cell={ (props) => 
                        <td>
                          <a href={props.dataItem.emrWfUrl} target="_blank" style={{color: "#106ba3"}}>
                          Wavefront URL
                          </a>
                        </td>} 
              />
              <Column field="appsFailed" title={Apps_Failed} width="180px" filter="numeric" />
                <Column field="appsPending" title={Apps_Pending} width="180px" filter="numeric" />
                <Column field="appsRunning" title={Apps_Running} width="180px" filter="numeric" />
                <Column field="appsSucceeded" title={Apps_Succeeded} width="180px" filter="numeric" />
      </Grid>
      </div>
      )

        case 'schedule':
        return (
          <div>
            <div style={{ position: 'relative', top: '10px' }}>
              {
              this.props.role !== 'readonly' ?
              <div>
                <button title='Download data into an excelsheet' className='exportBtn' onClick={this.export}>
                  <i className='material-icons'>cloud_download</i>
                  <span>Export to Excel</span>
                </button>
                <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.props.handleRangeChange('schedule')}>
                  <i className='material-icons'>refresh</i>
                  <span>Refresh Table</span>
                </button>
              </div>:
              <div/>
            }
            </div>
          <Grid
          style={{ 'marginTop': 20 , height: '75vh' }}
          resizable={true}
          reorderable={true}
          filterable={true}
          sortable={true}
          pageable={{ pageSizes: true }}
          groupable={true}

          data={this.state.result !== undefined ? this.shortlistData(this.state.result.data) : this.state.result}
          onDataStateChange={this.dataStateChange}
          {...this.state.dataState}

          onExpandChange={this.expandChange}
          expandField="expanded"
        >
          <Column field="emrId" title={EMR_ID} width="180px" />
          <Column field="emrName" title={EMR_Name} width="180px" />
                <Column field="rmUrl" title={RM_Url} width="200px"  cell={ (props) => 
                          <td>
                            <a href={props.dataItem.rmUrl} target="_blank" style={{color: "#106ba3"}}>
                            {props.dataItem.rmUrl}
                            </a>
                          </td>} 
                />
                
               
                <Column field="emrStatus" title={EMR_Status} width="180px" cell={emrHealthCell} />
                <Column field="account" title={Account_ID} width="180px" />
                <Column field="activeNodes" title={Active_Nodes} width="180px" filter="numeric" />
                
                <Column  field="memoryUsagePct" title={Res_Memory_Usage} width="250px" filter="numeric" 
                
                cell={ (props) => 
                  <td>
              
                    <ProgressBar   animate="false" stripes="false"  intent= 
                    {
                      
                    props.dataItem.memoryUsagePct/100 < 0.5 ?
                    "SUCCESS" : (props.dataItem.memoryUsagePct/100 >= 0.5 && props.dataItem.memoryUsagePct/100 < 0.7) ? 
                    "WARNING" : 
                    "DANGER"     
                    } 
                    value={props.dataItem.memoryUsagePct/100} />
                
                    <span className="my-span"  > {props.dataItem.memoryUsagePct}%
                 

                    </span>
                  </td>} 

                />
                <Column field="coresUsagePct" title={Res_Cores_Usage} width="250px" filter="numeric" 
                
                cell={ (props) => 
                  <td>
                    <ProgressBar animate="true" stripes="true" 
                    intent= 
                    {props.dataItem.coresUsagePct/100 < 0.5 ?
                    "SUCCESS" : (props.dataItem.coresUsagePct/100 >= 0.5 && props.dataItem.coresUsagePct/100 < 0.7) ? 
                    "WARNING" : 
                    "DANGER" }   
                    value={props.dataItem.coresUsagePct/100} 
                    />
                    <span className="my-span">{Math.round(props.dataItem.coresUsagePct , 3) }% 
                    
                    </span>
                  </td>} 

                />
               
               
               <Column field="clusterCreateTimestamp" title={Creation_Timestamp} width="180px" />
                <Column field="refreshTimestamp" title={Refresh_Timestamp} width="300px"  cell={ (props) =>
                   <td><div>{props.dataItem.refreshTimestamp}</div></td>
                }/>
                  <Column field="cost" title={Cost}  width="150px" 
                cell={ (props) => 
                  <td>
                    <span >$ {props.dataItem.cost} </span>
                  </td>} 
                />

                <Column field="emrWfUrl" title={Wavefront_Url} width="200px"  cell={ (props) => 
                          <td>
                            <a href={props.dataItem.emrWfUrl} target="_blank" style={{color: "#106ba3"}}>
                            Wavefront URL
                            </a>
                          </td>} 
                />
                <Column field="appsFailed" title={Apps_Failed} width="180px" filter="numeric" />
                <Column field="appsPending" title={Apps_Pending} width="180px" filter="numeric" />
                <Column field="appsRunning" title={Apps_Running} width="180px" filter="numeric" />
                <Column field="appsSucceeded" title={Apps_Succeeded} width="180px" filter="numeric" />
        </Grid>
        </div>
        )

        case 'accounts':
          
        return (
          <div>
            <div style={{ position: 'relative', top: '10px' }}>
              {
              this.props.role !== 'readonly' ?
              <div>
                <button title='Download data into an excelsheet' className='exportBtn' onClick={this.export}>
                  <i className='material-icons'>cloud_download</i>
                  <span>Export to Excel</span>
                </button>
                <button title='Refresh to get the latest data' className='exportBtn refresh' onClick={() => this.props.handleRangeChange('accounts', this.account_range)}>
                  <i className='material-icons'>refresh</i>
                  <span>Refresh Table</span>
                </button>
              </div>:
              <div/>
            }
            </div>
          <Grid
          style={{ 'marginTop': 20 , height: '75vh'  }}
          resizable={true}
          reorderable={true}
          filterable={true}
          sortable={true}
          pageable={{ pageSizes: true }}
          groupable={true}

          data={this.state.result !== undefined ? this.shortlistData(this.state.result.data) : this.state.result}
          onDataStateChange={this.dataStateChange}
          {...this.state.dataState}

          onExpandChange={this.expandChange}
          expandField="expanded"
        >
          <Column field="emrId" title={EMR_ID} width="180px" />
          <Column field="emrName" title={EMR_Name} width="180px" />
                <Column field="rmUrl" title={RM_Url} width="200px"  cell={ (props) => 
                          <td>
                            <a href={props.dataItem.rmUrl} target="_blank" style={{color: "#106ba3"}}>
                            {props.dataItem.rmUrl}
                            </a>
                          </td>} 
                />
                
               
                <Column field="emrStatus" title={EMR_Status} width="180px" cell={emrHealthCell} />
                <Column field="account" title={Account_ID} width="180px" />
                <Column field="activeNodes" title={Active_Nodes} width="180px" filter="numeric" />
                
                <Column  field="memoryUsagePct" title={Res_Memory_Usage} width="250px" filter="numeric" 
                
                cell={ (props) => 
                  <td>
              
                    <ProgressBar   animate="false" stripes="false"  intent= 
                    {
                      
                    props.dataItem.memoryUsagePct/100 < 0.5 ?
                    "SUCCESS" : (props.dataItem.memoryUsagePct/100 >= 0.5 && props.dataItem.memoryUsagePct/100 < 0.7) ? 
                    "WARNING" : 
                    "DANGER"     
                    } 
                    value={props.dataItem.memoryUsagePct/100} />
                
                    <span className="my-span"  > {props.dataItem.memoryUsagePct}%
                 

                    </span>
                  </td>} 

                />
                <Column field="coresUsagePct" title={Res_Cores_Usage} width="250px" filter="numeric" 
                
                cell={ (props) => 
                  <td>
                    <ProgressBar animate="true" stripes="true" 
                    intent= 
                    {props.dataItem.coresUsagePct/100 < 0.5 ?
                    "SUCCESS" : (props.dataItem.coresUsagePct/100 >= 0.5 && props.dataItem.coresUsagePct/100 < 0.7) ? 
                    "WARNING" : 
                    "DANGER" }   
                    value={props.dataItem.coresUsagePct/100} 
                    />
                    <span className="my-span">{Math.round(props.dataItem.coresUsagePct , 3) }% 
                    </span>
                  </td>} 

                />
               
               
               <Column field="clusterCreateTimestamp" title={Creation_Timestamp} width="180px" />
                <Column field="refreshTimestamp" title={Refresh_Timestamp} width="300px"  cell={ (props) =>
                   <td><div>{props.dataItem.refreshTimestamp}</div></td>
                }/>
                  <Column field="cost" title={Cost}  width="150px" 
                cell={ (props) => 
                  <td>
                    <span >$ {props.dataItem.cost} </span>
                  </td>} 
                />

                <Column field="emrWfUrl" title={Wavefront_Url} width="200px"  cell={ (props) => 
                          <td>
                            <a href={props.dataItem.emrWfUrl} target="_blank" style={{color: "#106ba3"}}>
                            Wavefront URL
                            </a>
                          </td>} 
                />
                <Column field="appsFailed" title={Apps_Failed} width="180px" filter="numeric" />
                <Column field="appsPending" title={Apps_Pending} width="180px" filter="numeric" />
                <Column field="appsRunning" title={Apps_Running} width="180px" filter="numeric" />
                <Column field="appsSucceeded" title={Apps_Succeeded} width="180px" filter="numeric" />
        </Grid>
        </div>
        )

    }
  }

  showTable = tableData => {

  }

  _export
  export() {
    this._export.save()
  }

  fetchDataForGraph(data) {
    let accounts = this.props.uiListData.accounts
    let types = [ 'scheduled', 'exploratory', 'transient' ];
    var graph = {
      nodes: [
        { id: 1, label: 'AWS Accounts', group: 'ACCOUNT' }
      ],
      edges: []
    }
 
    accounts.forEach(function(act){
      types.forEach(function(type){
        let atype = act + '.' + type;
        let node = { id: act, label: act, group: 'ACT' }
        if (graph.nodes.filter(n => n.id === act).length === 0) {
          graph.nodes.push(node);
        }
        node = { id: atype, label: type, group: 'ACT_TYPE' }
        graph.nodes.push(node)
        let edge = { from: 1, to: act }
        if (graph.edges.filter(e => e.from === 1 && e.to === act).length === 0) {
          graph.edges.push(edge)
        } 
        edge = { from: act, to: atype }
        graph.edges.push(edge)
      })
    })

    var map = {};
    if (data !== undefined && data.length > 0) {
      data = this.shortlistData(data);
      data.forEach(function(item) {
        let info = { 
          account: item.account, 
          active_nodes: item.activeNodes, 
          coresUsagePct: item.coresUsagePct,
          memoryUsagePct: item.memoryUsagePct,
          cluster_create_timestamp: item.clusterCreateTimestamp,
          containers_pending: item.containersPending,
          cost: item.cost,
          emr_name: item.emrName,
          emr_status: item.emrStatus,
          emr_wf_url: item.emr_wf_url,
          metrics_json: item.metrics_json,
          refresh_timestamp: item.refreshTimestamp,
          rm_url: item.rmUrl,
          apps_failed: item.appsFailed,
          apps_pending: item.appsPending,
          apps_running: item.appsRunning,
          apps_succeeded: item.appsSucceeded,
          role: item.clusterSegment
        }
        let node = {};
        if (item.appsRunning > 0)
            node = { id: item.emrId, label: `${item.emrName}--<b>${item.appsRunning}</b>`, group: 'DATA' }
        else 
            node = { id: item.emrId, label: item.emrName, group: 'DATA' }
        
        let edge = {};
        if (item.emrName.startsWith('scheduled')) {
          edge = { from: item.account + '.scheduled', to: item.emrId }
          graph.nodes.push(node);
          graph.edges.push(edge);
        } else if (item.emrName.startsWith('exploratory')) {
          edge = { from: item.account + '.exploratory', to: item.emrId }
          graph.nodes.push(node);
          graph.edges.push(edge);
        } else if (item.emrName.startsWith('transient')) {
          edge = { from: item.account + '.transient', to : item.emrId }
          graph.nodes.push(node);
          graph.edges.push(edge);
        }
        map[item.emrId] = info;
      })
    }

    graph = this.addColorToGraph(graph, map)

    return {
      graphData: graph,
      dataMap: map
    };
  }

addColorToGraph = (graphData, dataMap) => {
  let greenColor = '#7FD000';
  let yellowColor = '#FDF291';
  let redColor = '#F8A0A0';

  let actTypeColorMap = {};
  graphData.nodes.forEach(function(node) {
    let id = node.id;
    if (node.group === 'DATA') {
      let mapKey = '';
      if (node.label.startsWith('scheduled')) {
        mapKey = dataMap[id].account + '.scheduled'
      } else if (node.label.startsWith('exploratory')) {
        mapKey = dataMap[id].account + '.exploratory'
      } else if (node.label.startsWith('transient')) {
        mapKey = dataMap[id].account + '.transient'
      }
      if (dataMap[id].emr_status == 'TERMINATED' || dataMap[id].active_nodes === 0) {
        node.color = redColor;
        actTypeColorMap[mapKey] = redColor;
      } else if (dataMap[id].emr_status == 'RUNNING' || dataMap[id].apps_running > 0) {
        node.color = yellowColor;
        if (actTypeColorMap[mapKey] != redColor) {
          actTypeColorMap[mapKey] = yellowColor;
        }
      } else {
        node.color = greenColor;
      }
    } 
  })
  let actColorMap = {};
    graphData.nodes.forEach(function(node) {
      if (node.group === 'ACT_TYPE') {
        let act = node.id.split('.')[0]
        if (actTypeColorMap[node.id] !== undefined) {
          node.color = actTypeColorMap[node.id]
          if (actColorMap[act] != redColor) {
            actColorMap[act] = node.color
          }
        } else {
          node.color = greenColor;
        }
      }
    })

    graphData.nodes.forEach(function(node) {
      if (node.group === 'ACT') {
        if (actColorMap[node.id] !== undefined) {
          node.color = actColorMap[node.id];
        } else {
          node.color = greenColor;
        }
      }
    })

    return graphData
}

shortlistData = data => {
  if (this.props.superAdmin) return data;
  let emrRoles = this.props.dccRoles.filter(x => x.serviceType === 'EMR')[0];
  if (emrRoles === undefined) return [];

  let roleSegments = [];
  emrRoles.roles.forEach((value) => {
      value.segments.forEach(x => {
        roleSegments.push(x);
      })
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
    let type = value.clusterSegment;
    if (type in roleMap) {
      if (!this.segment_filters.includes(type)) this.segment_filters.push(type)
      if (roleMap[type].includes(value.account)) {
        return value;
      }
    }
  });

  return newData;
}
  
  handleToggleChange() {
    this.setState({
      ...this.state,
      graphView: !this.state.graphView
    })
  }

  handlePrimaryTabChange(newTabId, prevTabId) {
    this.setState({
      ...this.state,
      primaryTab: newTabId
    })
    if (newTabId !== 'accounts')
      this.props.handleRangeChange(newTabId,'');

  }

  createGroupState(dataState) {
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
    event.dataItem[event.target.props.expandField] = event.value
    this.setState({
      result: Object.assign({}, this.state.result),
      dataState: this.state.dataState
    });
  }
  formatDate() {
    if(!this.state.updated) {
      let date = new Date()
      let hour = date.getHours()
      let minutes = date.getMinutes()
      let seconds = date.getSeconds()
      let year = date.getFullYear()
      let day = date.getDate()
      let month = date.getMonth()
      let ampm = (hour < 12 || hour === 24) ? "AM" : "PM";
      let updateObj = `${hour}:${minutes}:${seconds}${ampm}`
      this.setState({
        updateDate: updateObj,
        updated: true
      })
    }
  }
  refreshTimer() {

    const isMatch = !!matchPath(
      this.props.locationName, 
      '/emrHealth'
    ); 
    if(this.props.signedIn && isMatch) {
      this.setState({
        refreshInterval : setInterval(() => this.checkTimer(), 1000, this.checkTimer())
      }, () => {    console.log('hi refreshed')})
    } else {
      this.setState({
        refreshInterval : clearInterval(this.state.refreshInterval)})
    }

  }
  checkTimer() {
    let match = !!matchPath(
      this.props.locationName, 
      '/emrHealth'
    ); 
    if(match) {
      if(this.state.refreshTimer <= 0) {
        window.location.reload()      
      } else {
        this.setState({
          refreshTimer: this.state.refreshTimer - 1
        })
      }
    }

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
  }

  componentDidMount(prevProps) {
    this.refreshTimer()
    
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

  componentWillReceiveProps(props, state) {
    this.forceUpdate();
  }
  componentWillUnmount() {
    this.setState({
      refreshInterval: clearInterval(this.state.refreshInterval)
    })
    
  }
}