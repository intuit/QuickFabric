import React from 'react';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, Legend } from 'recharts';
import { Container, Row, Col } from 'react-bootstrap';
import { connect } from 'react-redux';
import { formatDate } from '@telerik/kendo-intl';
import { DatePicker, TimePicker } from '@progress/kendo-react-dateinputs'
import '@progress/kendo-react-intl'
import '@progress/kendo-react-common'
import '@progress/kendo-react-popup'
import '@progress/kendo-date-math'
import '@progress/kendo-react-dropdowns'
import { Grid, GridColumn as Column, GridDetailRow } from '@progress/kendo-react-grid'
import { Tab, Tabs, ProgressBar} from '@blueprintjs/core'
import {
    fetchClusterMetricsHourly,
    fetchClusterMetricsDaily,
    fetchClusterMetricsWeekly,
    fetchClusterMetricsMonthly,
    fetchClusterMetricsAdviceDaily,
    fetchClusterMetricsAdviceWeekly,
    fetchClusterMetricsAdviceMonthly,
    fetchClusterAppsRunning,
    fetchAllAdvice,
    fetchClusterCustomRange,
} from '../../../actions/emrHealth';
import {fetchClusterCosts} from './../../../actions/emrCost'
import { withState } from '../../../utils/components/with-state.jsx';


const StatefulGrid = withState(Grid);

/**
 * Component for showing Dr. Elephant advice data in the Advice table.
 */
class ElephantAdviceDetail extends GridDetailRow {
  constructor(props) {
    super(props)
    this.state = {}
    this.expandChange = this.expandChange.bind(this);
    }
    expandChange() {}
    render() {
        const data = this.props.dataItem.yarn_app_heuristic_results;
        if (data) {
            return (
              <div>
                <StatefulGrid
                    data={data}
                    filterable={true}
                >
                  <Column field="heuristic_name" title={'Name'}  />
                  <Column field="advice" title={'Advice'} />
                  <Column field="severity" title={'Severity'} />
                </StatefulGrid>
              </div>

            );
        }
        return (
            <div style={{ height: "50px", width: '100%', display: 'none' }}>
                <div style={{ position: 'absolute', width: '100%' }}>
                    <div className="k-loading-image" />
                </div>
            </div>
        );
    }
}

/**
 * Custome Date Input Component for one of the durations.
 */
export class CustomDateInput extends React.Component {
    inputStyle = {
        display: 'none'
    }
    handleChange = (syntheticEvent) => {
        const date = {
            day: this.props.value.getDate(),
            month: this.props.value.getMonth(),
            year: this.props.value.getFullYear()
        };

        const value = new Date(date.year, date.month, date.day);

        this.props.onChange({
            value,
            syntheticEvent,
            target: this
        });
    }
    render() {
        return [
            <input style={this.inputStyle} type="number" data-section="day" value={this.props.value.getDate()} onChange={this.handleChange} />,
            <input style={this.inputStyle} type="number" data-section="month" value={this.props.value.getMonth()} onChange={this.handleChange} />,
            <input style={this.inputStyle} type="number" data-section="year" value={this.props.value.getFullYear()} onChange={this.handleChange} />
        ];
    }
}

/**
 * Advice Table containing Job Schedule Window and Dr. Elephant Advice in tabular format.
 */
class AdviceDetail extends GridDetailRow {
  constructor(props) {
    super(props)
    this.state = {
      elephantTab: 'all'
    }
    this.expandChange = this.expandChange.bind(this);
    this.handleElephantTabChange = this.handleElephantTabChange.bind(this);
  }

  expandChange(event) {
    event.dataItem.expanded = event.value;
    let categoryID = event.dataItem._id;
    this.setState({ ...this.state });


    if (!event.value || event.dataItem.details) {
      return;
    }
  }

  handleElephantTabChange(newTabId) {
    this.setState({
      ...this.state,
      elephantTab: newTabId
    })
  }

  renderAdviceTable() {
      switch(this.state.elephantTab) {

        case 'all':
          return (
            <StatefulGrid
            data={[...this.props.adviceData[1].adviceObj.critical_apps, ...this.props.adviceData[1].adviceObj.severe_apps, ...this.props.adviceData[1].adviceObj.moderate_apps]}
            filterable={true}
            resizable
            reorderable={true}
            detail={(props) => <ElephantAdviceDetail {...props} elephantData={this.props.adviceData} />}
            expandField="expanded"
            onExpandChange={this.expandChange}
            {...this.state}
            >
              <Column field="applicationId" title={'ID'}  />
              <Column field="applicationName" title={'Application Name'} />
              <Column field="applicationType" title={'Type'} />
              <Column field="startTimestamp" title={'Start'} />
              <Column field="elapsedTime" title={'Elapsed Time'} />
            </StatefulGrid>
          )
        case 'moderate':
          return (
            <StatefulGrid
            data={this.props.adviceData[1].adviceObj.moderate_apps}
            filterable={true}
            resizable
            reorderable={true}
            detail={(props) => <ElephantAdviceDetail {...props} elephantData={this.props.adviceData} />}
            expandField="expanded"
            onExpandChange={this.expandChange}
            {...this.state}
            >
              <Column field="applicationId" title={'ID'}  />
              <Column field="applicationName" title={'Application Name'} />
              <Column field="applicationType" title={'Type'} />
              <Column field="startTimestamp" title={'Start'} />
              <Column field="elapsedTime" title={'Elapsed Time'} />
            </StatefulGrid>
          )
        case 'severe':
          return (
            <StatefulGrid
            data={this.props.adviceData[1].adviceObj.severe_apps}
            filterable={true}
            resizable
            reorderable={true}
            detail={(props) => <ElephantAdviceDetail {...props} elephantData={this.props.adviceData} />}
            expandField="expanded"
            onExpandChange={this.expandChange}
            {...this.state}
            >
              <Column field="applicationId" title={'ID'}  />
              <Column field="applicationName" title={'Application Name'} />
              <Column field="applicationType" title={'Type'} />
              <Column field="startTimestamp" title={'Start'} />
              <Column field="elapsedTime" title={'Elapsed Time'} />
            </StatefulGrid>
          )
        case 'critical':
          return (
            <StatefulGrid
            data={this.props.adviceData[1].adviceObj.critical_apps}
            filterable={true}
            resizable
            reorderable={true}
            detail={(props) => <ElephantAdviceDetail {...props} elephantData={this.props.adviceData} />}
            expandField="expanded"
            onExpandChange={this.expandChange}
            {...this.state}
            >
              <Column field="applicationId" title={'ID'}  />
              <Column field="applicationName" title={'Application Name'} />
              <Column field="applicationType" title={'Type'} />
              <Column field="startTimestamp" title={'Start'} />
              <Column field="elapsedTime" title={'Elapsed Time'} />
            </StatefulGrid>
          )
      }
    }
    render() {
        const data = this.props.adviceData;
        if (data && this.props.dataItem.advice_type !== 'advice') {
            return (
              <div>
              <Tabs className='line' id="primary-tab" selectedTabId={this.state.elephantTab} onChange={this.handleElephantTabChange}>
                  <Tab id="all" title="All" />
                  <Tab id="critical" title="Critical"/>
                  <Tab id="severe" title="Severe" />
                  <Tab id="moderate" title="Moderate" />
              </Tabs>
              {this.renderAdviceTable()}
              </div>

            );
        }
        return (
            <div style={{ height: "50px", width: '100%', display: 'none' }}>
                <div style={{ position: 'absolute', width: '100%' }}>
                    <div className="k-loading-image" />
                </div>
            </div>
        );
    }
}

/**
 * Popup window opened when clicked on a node in the observability graph. 
 * Contains different tabs of data with tables and chart visualizations for various durations.
 */
class RowData extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            duration_type: 'minutes',
            chart_data: [],
            xaxis_label: '',
            show_charts: false,
            primaryTab: 'emr',
            showRunningApps: false,
            elephantTab: 'moderate',
            adviceObj: {_id: 1, advice_title: "Job Scheduling Window", advice_data: {}, adviceObj: {advice: ''}},
            elephantObj: {_id: 2, advice_title: "Dr. Elepnant's Advice", advice_data: {}, adviceObj: {advice: ''}},
            adviceArray: [
              {_id: 1, advice_type: "advice", advice_title: "Job Scheduling Window", advice_data: "", adviceObj: {advice: ''}},
              {_id: 2, advice_type: "elephant", advice_title: "Dr. Elephant's Advice", advice_data: "", adviceObj: {advice: '', critical_apps: [], severe_apps: [], moderate_apps: []}},
            ],
            value: new Date(),
            formattedDate: '',
            time: new Date(),
            formattedTime: '',
            initialDate: new Date(new Date().getTime() - 60 * 60 * 24 * 1000)
        }

        this.fetchChartData = this.fetchChartData.bind(this);
        this.handleRadioClick = this.handleRadioClick.bind(this);
        this.handlebuttonclick = this.handlebuttonclick.bind(this);
        this.formatXAxis = this.formatXAxis.bind(this);
        this.handlePrimaryTabChange = this.handlePrimaryTabChange.bind(this);
        this.handleRunningAppsClick = this.handleRunningAppsClick.bind(this);
        this.expandChange = this.expandChange.bind(this);
        this.handleChange = this.handleChange.bind(this); 
        this.handleTimeChange = this.handleTimeChange.bind(this);
        this.handleDateSubmit = this.handleDateSubmit.bind(this);
        this.handleInitialCustomCluster = this.handleInitialCustomCluster.bind(this);
    }

    render() {
        let complete_data = {};
        console.log("Row Data props", this.props )
        if (this.state.duration_type == 'hourly') {
            complete_data = this.props.clusterMetricsHourlyData
        } else if (this.state.duration_type == 'daily') {
            complete_data = this.props.clusterMetricsDailyData
        } else if (this.state.duration_type == 'weekly') {
            complete_data = this.props.clusterMetricsWeeklyData
        } else if (this.state.duration_type == 'monthly') {
            complete_data = this.props.clusterMetricsMonthlyData
        } else if (this.state.duration_type == 'custom') {
            complete_data = this.props.clusterMetricsGetCustomData
        }

        let advice_data = {};
        if (this.state.duration_type == 'daily' && this.props.allEmrHealthData.allClusterMetricsDailySuccess) {
          this.state.adviceArray[0].adviceObj = !this.props.allEmrHealthData.allClusterMetricsDailyError ? this.props.allEmrHealthData.allClusterMetricsDailyData.jobSchedulingAdvice : {advice: ''};
          this.state.adviceArray[1].adviceObj = !this.props.allEmrHealthData.allClusterMetricsDailyError ? this.props.allEmrHealthData.allClusterMetricsDailyData.jobPerformanceAdvice : {advice: '', leastUsed: [], mostUsed: []};
        }
        if (this.state.duration_type == 'weekly' && this.props.allEmrHealthData.allClusterMetricsWeeklySuccess) {
          this.state.adviceArray[0].adviceObj = !this.props.allEmrHealthData.allClusterMetricsWeeklyError ? this.props.allEmrHealthData.allClusterMetricsWeeklyData.jobSchedulingAdvice : {advice: ''};
          this.state.adviceArray[1].adviceObj = !this.props.allEmrHealthData.allClusterMetricsWeeklyError ? this.props.allEmrHealthData.allClusterMetricsWeeklyData.jobPerformanceAdvice : {advice: '', leastUsed: [], mostUsed: []};
        }
        if (this.state.duration_type == 'monthly' && this.props.allEmrHealthData.allClusterMetricsMonthlySuccess) {
          this.state.adviceArray[0].adviceObj = !this.props.allEmrHealthData.allClusterMetricsMonthlyError ? this.props.allEmrHealthData.allClusterMetricsMonthlyData.jobSchedulingAdvice : {advice: ''};
          this.state.adviceArray[1].adviceObj = !this.props.allEmrHealthData.allClusterMetricsMonthlyError ? this.props.allEmrHealthData.allClusterMetricsMonthlyData.jobPerformanceAdvice : {advice: '', leastUsed: [], mostUsed: []};
        }
        

        return (
            <div className='popup'>
                <div className='popup-inner'>
                    <div className='popup-close'>
                        <button className='close-popup' onClick={this.props.cancelPopup}>
                            <i className='material-icons'>keyboard_arrow_right</i>
                        </button>
                    </div>
                    <div className='popup-components'>
                        <h3 style={{ color: '#0097E6', marginRight: '10px'}}>Data for {this.props.emr_name}</h3>
                        <div className='tab-components'>
                            <input
                                type="radio"
                                name="duration"
                                value="minutes"
                                className="duration-radio-buttons"
                                defaultChecked
                                onChange={this.handleRadioClick}
                            /> Last Ten Minutes
                            <input
                                type="radio"
                                name="duration"
                                value="hourly"
                                className="duration-radio-buttons"
                                onChange={this.handleRadioClick}
                            /> Last One Hour
                            <input
                                type="radio"
                                name="duration"
                                value="daily"
                                className="duration-radio-buttons"
                                onChange={this.handleRadioClick}
                            /> Last One Day
                            <input
                                type="radio"
                                name="duration"
                                value="weekly"
                                className="duration-radio-buttons"
                                onChange={this.handleRadioClick}
                            /> Last One Week
                            <input
                                type="radio"
                                name="duration"
                                value="monthly"
                                className="duration-radio-buttons"
                                onChange={this.handleRadioClick}
                            /> Last One Month
                            <input
                                type="radio"
                                name="duration"
                                value="custom"
                                className="duration-radio-buttons"
                                onChange={this.handleRadioClick}
                            /> Custom Date

                                                
                        </div>
                        {this.state.duration_type === 'custom' &&
                            <div className='datePickerContainer'>
                            <DatePicker 
                                className='customDatePicker'
                                format={"MMM, dd, yyyy"}
                                value={this.state.value} 
                                max={this.state.initialDate}
                                onChange={this.handleChange} 
                            />                                
                            <TimePicker 
                                className='customDatePicker'
                                onChange={this.handleTimeChange}
                                format={"HH:mm:ss a"}
                                value={this.state.time}
                            />          
                            <button className='dateSubmitButton' onClick={this.handleDateSubmit}>Submit</button>
                            </div>
                        }
                        <Tabs className='line' id="primary-tab" selectedTabId={this.state.primaryTab} onChange={this.handlePrimaryTabChange}>
                            <Tab id="emr" title="Cluster Details" />
                            <Tab id="jobs" title="Jobs Stats" />
                            <Tab id="stats" title="System Stats"/>
                            <Tab id="cost" title="Cost"/>
                            {this.state.duration_type !== 'hourly' && this.state.duration_type !== 'minutes' && this.state.duration_type !== 'custom' && <Tab id="expert" title="Expert Advice"/>}
                        </Tabs>
                        {
                            complete_data &&
                            this.renderRowData(this.state.primaryTab, complete_data, this.state.adviceArray[1])
                        }
                    </div>
                </div>
            </div>

        )
    }

    /**
     * Chart X-Axis Formatting.
     */
    formatXAxis = val => {
        if (this.state.duration_type == 'hourly') {
            return (10 * ( val + 1));
        } else {
            return val + 1;
        }
    }

    handleChange = (event) => {
        let updatedDate = formatDate(event.target.value, "yyyy-MM-dd")
        let updatedTime = formatDate(this.state.time, "HH:mm:ss")
        let dateAndTime = updatedDate + ' ' + updatedTime
        let currentDate = new Date()
        let formatCurrentDate = formatDate(currentDate, "yyyy-MM-dd HH:mm:ss")
        this.setState({
            ...this.state,
            show_charts: true,
            xaxis_label: 'Time',
            showRunningApps: false,
            value: event.target.value,
            formattedDate: formatDate(event.target.value, "yyyy-MM-dd"),
            duration_type: 'custom',
        })
    }

    /**
     * Setting initial values for the custom duration.
     */
    handleInitialCustomCluster = () => {
        let currentDate = new Date()
        let day = 60 * 60 * 24 * 1000;
        let initialDate = formatDate(new Date(currentDate.getTime() - day * 2), 'yyyy-MM-dd HH:mm:ss');
        let formatInitialDate = formatDate(this.state.initialDate, 'yyyy-MM-dd HH:mm:ss');
        let formatCurrentDate = formatDate(currentDate, "yyyy-MM-dd HH:mm:ss"); 

        this.props.fetchClusterCustomRange(this.props.emr_id, formatInitialDate, formatCurrentDate, this.props.token);
        this.props.fetchClusterCosts(this.props.emr_id, formatInitialDate, formatCurrentDate, 'CUSTOM', this.props.token);
    }
          
    handleTimeChange = (event) => {
        let updatedTime = formatDate(event.target.value, "HH:mm:ss")
        let updatedDate = formatDate(this.state.value, "yyyy-MM-dd")
        let dateAndTimeObj = updatedDate + ' ' + updatedTime
        let currentDate = new Date()

        let formatCurrentDate = formatDate(currentDate, "yyyy-MM-dd HH:mm:ss")
        this.setState({
            ...this.state,
            show_charts: true,
            xaxis_label: 'Time',
            showRunningApps: false,
            time: event.target.value,
            formattedTime: formatDate(event.target.value, "HH:mm:ss"),
            duration_type: 'custom',
        })
    }

    handleDateSubmit = () => {
        let currentDate = new Date()
        let formatCurrentDate = formatDate(currentDate, "yyyy-MM-dd HH:mm:ss")  
        let updatedTime = formatDate(this.state.time, "HH:mm:ss")
        let updatedDate = formatDate(this.state.value, "yyyy-MM-dd")
        let dateAndTimeObj = updatedDate + ' ' + updatedTime
        this.props.fetchClusterCustomRange(this.props.emr_id, dateAndTimeObj, formatCurrentDate, this.props.token)
        this.props.fetchClusterCosts(this.props.emr_id, dateAndTimeObj, formatCurrentDate, 'CUSTOM', this.props.token);
    }

    expandChange = (event) => {
      event.dataItem.expanded = event.value;
      let categoryID = event.dataItem._id;
      this.setState({ ...this.state });
      if (!event.value || event.dataItem.details) {
        return;
      }
    }

    renderRowData = (primaryTab, complete_data, adviceObj) => {
        console.log('let the render begin!', this.props)
        let emr_status = 'Healthy';
        if (this.props.active_nodes == 0) {
            emr_status = 'Unhealthy';
        }
        else if (this.props.apps_running > 0) {
            emr_status = 'Running';
        }
        else {
            emr_status = 'Healthy';
        }
        let business_owner = this.props.segments.filter(x => x.segmentName === this.props.role)[0].businessOwner;
        switch(primaryTab) {
            case 'emr':
                return (
                    <div className='tab-components'>
                        <table>
                            <tbody>
                                <tr>
                                    <td className='rowDataTitle'>Business Owner: </td>
                                    <td style={{ color: '#326da8', fontWeight: '600' }}>{business_owner}</td>
                                </tr>
                                <tr>
                                    <td className='rowDataTitle'>EMR #: </td>
                                    <td>{this.props.emr_id}</td>
                                </tr>
                                <tr>
                                    <td className='rowDataTitle'>EMR Name: </td>
                                    <td>{this.props.emr_name}</td>
                                </tr>
                                <tr>
                                    <td className='rowDataTitle'>RM URL: </td>
                                    <td>{this.props.rm_url}</td>
                                </tr>
                                <tr>
                                    <td className='rowDataTitle'>EMR Status: </td>
                                    <td><div className={`cell ${emr_status}`}></div>{emr_status}</td>
                                </tr>
                                <tr>
                                    <td className='rowDataTitle'>Account ID: </td>
                                    <td>{this.props.account_id}</td>
                                </tr>
                                <tr>
                                    <td className='rowDataTitle'>Active # Nodes: </td>
                                    <td>{this.props.active_nodes}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                )

            case 'jobs':
                return (
                    <div className='tab-components'>
                        {this.state.show_charts && complete_data[0] !== undefined && <Container>
                            <Row>
                                <Col className='tab-components chart-container'>
                                    <h5>Jobs Pending</h5>
                                    <LineChart width={250} height={150} data={complete_data[0].timeSeriesMetrics}
                                        margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                        <XAxis tickFormatter={this.formatXAxis} label={{ value: this.state.xaxis_label, dy: 10 }}/>
                                        <YAxis label={{ value: "Number of jobs", angle: -90, dx: -10 }}/>
                                        <CartesianGrid strokeDasharray="3 3"/>
                                        <Tooltip />
                                        <Line type="monotone" dataKey="appsPending" stroke="#0097E4" activeDot={{r: 8}}/>
                                    </LineChart>
                                </Col>
                                <Col className='tab-components chart-container'>
                                    <h5>Jobs Running</h5>
                                    <LineChart width={250} height={150} data={complete_data[0].timeSeriesMetrics}
                                        margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                        <XAxis tickFormatter={this.formatXAxis} label={{ value: this.state.xaxis_label, dy: 10 }}/>
                                        <YAxis label={{ value: "Number of jobs", angle: -90, dx: -10  }}/>
                                        <CartesianGrid strokeDasharray="3 3"/>
                                        <Tooltip />
                                        <Line type="monotone" dataKey="appsRunning" stroke="#0097E4" activeDot={{r: 8}}/>
                                    </LineChart>
                                </Col>
                            </Row>
                            <Row>
                                <Col className='tab-components chart-container'>
                                    <h5>Jobs Failed</h5>
                                    <LineChart width={250} height={150} data={complete_data[0].timeSeriesMetrics}
                                        margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                        <XAxis tickFormatter={this.formatXAxis} label={{ value: this.state.xaxis_label, dy: 10 }}/>
                                        <YAxis label={{ value: "Number of jobs", angle: -90, dx: -10  }}/>
                                        <CartesianGrid strokeDasharray="3 3"/>
                                        <Tooltip />
                                        <Line type="monotone" dataKey="appsFailed" stroke="#0097E4" activeDot={{r: 8}}/>
                                        {/* <Line type="monotone" dataKey="uv" stroke="#82ca9d" /> */}
                                    </LineChart>
                                </Col>
                                <Col className='tab-components chart-container'>
                                    <h5>Jobs Succeeded</h5>
                                    <LineChart width={250} height={150} data={complete_data[0].timeSeriesMetrics}
                                        margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                        <XAxis tickFormatter={this.formatXAxis} label={{ value: this.state.xaxis_label, dy: 10 }}/>
                                        <YAxis label={{ value: "Number of jobs", angle: -90, dx: -10  }}/>
                                        <CartesianGrid strokeDasharray="3 3"/>
                                        <Tooltip />
                                        <Line type="monotone" dataKey="appsSucceeded" stroke="#0097E4" activeDot={{r: 8}}/>
                                        {/* <Line type="monotone" dataKey="uv" stroke="#82ca9d" /> */}
                                    </LineChart>
                                </Col>
                            </Row>
                        </Container>}
                        <table>
                            <tbody>
                                <tr>
                                    <td className='rowDataTitle'>Total Number of Jobs Failed: </td>
                                    {this.state.duration_type == 'minutes'  ? <td>{this.props.apps_failed}</td> : complete_data[0] !== undefined ? <td>{complete_data[0].appsFailed}</td> : <td></td>}
                                </tr>
                                <tr>
                                    <td className='rowDataTitle'>Average Number of Jobs Running: </td>
                                    {this.state.duration_type == 'minutes'  ?
                                    <td><div className='outer'>{this.props.apps_running}{this.props.apps_running > 0 && <a style={{ color: 'blue', marginLeft: '4px' }} onClick={this.handleRunningAppsClick}> (Click for Details)</a>}</div></td> :
                                    complete_data[0] !== undefined && <td>{complete_data[0].appsRunning}</td>}
                                </tr>
                                <tr>
                                    <td className='rowDataTitle'>Average Number of Jobs Pending: </td>
                                    {this.state.duration_type == 'minutes' ? <td>{this.props.apps_pending}</td> : complete_data[0] !== undefined ? <td>{complete_data[0].appsPending}</td> : <td></td>}
                                </tr>
                                <tr>
                                    <td className='rowDataTitle'>Total Number of Jobs Succeeded: </td>
                                    {this.state.duration_type == 'minutes' ? <td>{this.props.apps_succeeded}</td> : complete_data[0] !== undefined ? <td>{complete_data[0].appsSucceeded}</td> : <td></td>}
                                </tr>
                            </tbody>
                        </table>
                        {this.state.showRunningApps &&
                            <div className="k-grid-running-apps">
                                <h5 style={{ color: '#0097E6', marginRight: '10px'}}>Running Apps</h5>
                                <StatefulGrid
                                    data={this.props.clusterAppsRunningData.apps}
                                    resizable
                                    filterable={true} 
                                    width={'100%'}
                                >
                                  <Column field="id" title="Application ID" width="185px" />
                                  <Column field="jobType" title="Type"  width="92px" />
                                  <Column field="clusterUsagePercentage" title="Cluster Usage"  width="140px" cell={(props) => 
                                    <td><ProgressBar animate="true" stripes="true"
                                            intent={
                                            props.dataItem.clusterUsagePercentage / 100 < 0.25 ?
                                            "SUCCESS" : (props.dataItem.clusterUsagePercentage / 100 < 0.75) ?
                                            "WARNING" : 
                                            "DANGER"}
                                            value={props.dataItem.clusterUsagePercentage / 100}
                                            /><span className="my-span-kendo"> {Math.round(props.dataItem.clusterUsagePercentage)}%</span></td>}/>
                                  <Column field="elapsedTime" title="Elapsed Time (Minutes)"  className="k-col-elapsed" width="124px"/>
                                  <Column field="progress" title="Progress"  width="140px" cell={(props) => 
                                    <td><ProgressBar animate="true" stripes="true" intent="PRIMARY" value={props.dataItem.progress / 100} /><span className="my-span-kendo"> {Math.round(props.dataItem.progress)}%</span></td>} />
                                  <Column field="startTimestamp" title="Start Timestamp" width="128px"/>
                                  <Column field="user" title="User" width="128px"/>

                                </StatefulGrid>
                            </div>
                        }
                    </div>
                )

            case 'stats':
                return (
                    <div className='tab-components'>
                            {this.state.show_charts && complete_data[0] !== undefined && <Container>
                                <Row>
                                    <Col className='tab-components'>
                                        <h5>Cores Utilization %</h5>
                                        <LineChart width={250} height={150} data={complete_data[0].timeSeriesMetrics}
                                            margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                            <XAxis tickFormatter={this.formatXAxis} label={{ value: this.state.xaxis_label, dy: 10 }}/>
                                            <YAxis label={{ value: "Cores %", angle: -90, dx: -10  }}/>
                                            <CartesianGrid strokeDasharray="3 3"/>
                                            <Tooltip />
                                            <Line type="monotone" dataKey="coresUsagePct" stroke="#0097E4" activeDot={{r: 8}}/>
                                        </LineChart>
                                    </Col>
                                    <Col className='tab-components'>
                                        <h5>Memory Utilization %</h5>
                                        <LineChart width={250} height={150} data={complete_data[0].timeSeriesMetrics}
                                            margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                            <XAxis tickFormatter={this.formatXAxis} label={{ value: this.state.xaxis_label, dy: 10 }}/>
                                            <YAxis label={{ value: "Memory %", angle: -90, dx: -10  }}/>
                                            <CartesianGrid strokeDasharray="3 3"/>
                                            <Tooltip />
                                            <Line type="monotone" dataKey="memoryUsagePct" stroke="#0097E4" activeDot={{r: 8}}/>
                                            {/* <Line type="monotone" dataKey="uv" stroke="#82ca9d" /> */}
                                        </LineChart>
                                    </Col>
                                </Row>
                            </Container>}
                            <table>
                                <tbody>
                                    <tr>
                                        <td className='rowDataTitle'>Average Memory Utilization: </td>
                                        {this.state.duration_type == 'minutes' ? <td>{this.props.memoryUsagePct}%</td> : complete_data[0] !== undefined ? <td>{complete_data && complete_data[0].memoryUsagePct}%</td> : <td></td>}
                                    </tr>
                                    <tr>
                                        <td className='rowDataTitle'>Average Cores Utilization: </td>
                                        {this.state.duration_type == 'minutes' ? <td>{this.props.coresUsagePct}%</td> : complete_data[0] !== undefined ? <td>{complete_data && complete_data[0].coresUsagePct}%</td> : <td></td>}
                                    </tr>
                                </tbody>
                            </table>
                    </div>
                )

            case 'cost':
                return (
                    <div className='tab-components'>
                        {this.state.duration_type === 'daily' && null}
                        {this.state.duration_type === 'weekly' && 
                            <LineChart width={250} height={150} data={this.props.clusterCostWeeklyData}
                                margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                <XAxis tickFormatter={this.formatXAxis} label={{ value: this.state.xaxis_label, dy: 10 }}/>
                                <YAxis label={{ value: "Cost", angle: -90, dx: -10 }}/>
                                <CartesianGrid strokeDasharray="3 3"/>
                                <Tooltip />
                                <Line type="monotone" dataKey="cost" stroke="#0097E4" activeDot={{r: 8}}/>
                            </LineChart>
                        }
                        {this.state.duration_type === 'monthly' && 
                            <LineChart width={250} height={150} data={this.props.clusterCostMonthlyData}
                                margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                <XAxis tickFormatter={this.formatXAxis} label={{ value: this.state.xaxis_label, dy: 10 }}/>
                                <YAxis label={{ value: "Cost", angle: -90, dx: -10 }}/>
                                <CartesianGrid strokeDasharray="3 3"/>
                                <Tooltip />
                                <Line type="monotone" dataKey="cost" stroke="#0097E4" activeDot={{r: 8}}/>
                            </LineChart>
                        }
                        {this.state.duration_type === 'custom' && 
                            <LineChart width={250} height={150} data={this.props.clusterCostCustomData}
                                margin={{top: 5, right: 30, left: 20, bottom: 5}}>
                                <XAxis tickFormatter={this.formatXAxis} label={{ value: this.state.xaxis_label, dy: 10 }}/>
                                <YAxis label={{ value: "Cost", angle: -90, dx: -10 }}/>
                                <CartesianGrid strokeDasharray="3 3"/>
                                <Tooltip />
                                <Line type="monotone" dataKey="cost" stroke="#0097E4" activeDot={{r: 8}}/>
                            </LineChart>
                        }

                        <table>
                            <tbody>
                                <tr>
                                    <td className='rowDataTitle'>Cost Until Today (Current Month)</td>
                                    <td>${this.props.cost}</td>
                                </tr>
                            </tbody>
                        </table>
                    </div>
                )

            case 'expert':
                if(this.props.allClusterMetricsDailyFetching || this.props.allClusterMetricsWeeklyFetching || this.props.allClusterMetricsMonthlyFetching) {
                    return(
                        <div className='loader-wrapper-mini'>
                            <div className='loader-mini' />
                        </div> 
                    )
                } else if(this.state.adviceArray[1].adviceObj !== undefined && this.state.adviceArray[1].adviceObj.advice != 'All of your jobs have great job performance. Way to go!'){
                    return (
                        <div className='tab-components expert-grid'>
                            <StatefulGrid
                              data={this.state.adviceArray}
                              resizable
                              reorderable={true}
                              detail={(props) => <AdviceDetail {...props} adviceData={this.state.adviceArray} />}
                              expandField="expanded"
                              onExpandChange={this.expandChange}
                              pageable={false}
                              {...this.state}
                            >
                              <Column field="advice_title" title={'Title'} />
                              <Column field="adviceObj.advice" title={'Advice'} cell={(props) => 
                                    <td><span>{props.dataItem.adviceObj.advice}</span></td>}/>
                            </StatefulGrid>
                        </div>
    
                    )                    
                } else if(this.state.adviceArray[1].adviceObj !== undefined && this.state.adviceArray[1].adviceObj.advice === 'All of your jobs have great job performance. Way to go!'){
                    return (
                        <div className='tab-components expert-grid'>
                        <StatefulGrid
                          data={this.state.adviceArray}
                          resizable
                          reorderable={true}
                          pageable={false}
                          {...this.state}
                        >
                          <Column field="advice_title" title={'Title'} />
                          <Column field="adviceObj.advice" title={'Advice'} cell={(props) => 
                            <td><span>{props.dataItem.adviceObj.advice}</span></td>}/>
                        </StatefulGrid>
                    </div>
                    )
                } else if (this.state.adviceArray[0].adviceObj !== undefined) {
                    let data = this.state.adviceArray;
                    data[1].advice = "No data to show."
                    return (
                        <div className='tab-components expert-grid'>
                        <StatefulGrid
                          data={data}
                          resizable
                          reorderable={true}
                          pageable={false}
                          {...this.state}
                        >
                          <Column field="advice_title" title={'Title'} />
                          <Column field="adviceObj.advice" title={'Advice'} cell={(props) => 
                            <td><span>{props.dataItem.adviceObj.advice}</span></td>}/>
                        </StatefulGrid>
                    </div>
                    )
                }
        }
    }

    /**
     * Toggle to show running apps table.
     */
    handleRunningAppsClick = () => {
        this.setState({
            ...this.state,
            showRunningApps: !this.state.showRunningApps
        })
    }

    /**
     * Switch to new tab
     * @param {string} newTabId 
     */
    handlePrimaryTabChange = (newTabId) => {
        this.setState({
          ...this.state,
          primaryTab: newTabId
        })
      }

    fetchChartData = () => {
        let chart_data = [];
        let info = {};
        info.apps_pending = this.props.apps_pending;
        info.apps_failed = this.props.apps_failed;
        info.apps_succeeded = this.props.apps_succeeded;
        info.apps_running = this.props.apps_running;
        chart_data.push(info);
    }

    /**
     * Toggle to show charts. Charts are shown only for certain durations.
     */
    handlebuttonclick = (event) => {
        this.setState({
            ...this.state,
            show_charts: !this.state.show_charts
        })
    }

    /**
     * Fetch required data on load.
     */
    componentDidMount() {
      this.props.fetchAllAdvice(this.props.emr_id, 'day', this.props.token);
      this.props.fetchAllAdvice(this.props.emr_id, 'week', this.props.token);
      this.props.fetchAllAdvice(this.props.emr_id, 'month', this.props.token);

      this.props.fetchClusterMetricsHourly(this.props.emr_id, this.props.token);
      this.props.fetchClusterMetricsDaily(this.props.emr_id, this.props.token);
      this.props.fetchClusterMetricsWeekly(this.props.emr_id, this.props.token);
      this.props.fetchClusterMetricsMonthly(this.props.emr_id, this.props.token);
      this.props.fetchClusterMetricsAdviceDaily(this.props.emr_id, this.props.token);
      this.props.fetchClusterAppsRunning(this.props.emr_id, this.props.token);
      this.props.fetchClusterMetricsAdviceWeekly(this.props.emr_id, this.props.token);
      this.props.fetchClusterMetricsAdviceMonthly(this.props.emr_id, this.props.token);
      this.handleInitialCustomCluster()

      this.props.fetchClusterCosts(this.props.emr_id, '', '', 'WEEKLY', this.props.token)
      this.props.fetchClusterCosts(this.props.emr_id, '', '', 'MONTHLY', this.props.token)

    }

    componentDidUpdate() {
        if(this.props.clusterAppsRunningFetching && this.interval) {
            clearInterval(this.interval)
            this.interval = setInterval(() => {this.props.fetchClusterAppsRunning(this.props.emr_id, this.props.token)}, 1000 * 60 * 2)
        } else if(!this.interval) {
            this.interval = setInterval(() => {this.props.fetchClusterAppsRunning(this.props.emr_id, this.props.token)}, 1000 * 60 * 2)
        }

    }

    /**
     * Select duration.
     */
    handleRadioClick = event => {
        let type = event.target.value;
        let currTab = this.state.primaryTab;
        if (currTab === 'expert' && (type === 'minutes' || type === 'hourly')) {
            currTab = 'emr'
        }
        if (type == 'minutes') {
            this.setState({
                ...this.state,
                duration_type: event.target.value,
                show_charts: false,
                showRunningApps: false,
                primaryTab: currTab
            })
        } else if (type == 'hourly') {
            this.props.fetchClusterMetricsHourly(this.props.emr_id, this.props.token);
            this.setState({
                ...this.state,
                show_charts: true,
                duration_type: event.target.value,
                xaxis_label: 'Time (in minutes)',
                showRunningApps: false,
                primaryTab: currTab
            })
        } else if (type == 'daily') {
            this.props.fetchClusterMetricsDaily(this.props.emr_id, this.props.token);
            this.setState({
                ...this.state,
                show_charts: true,
                duration_type: event.target.value,
                xaxis_label: 'Time (in hours)',
                showRunningApps: false,
                primaryTab: currTab
            })
        } else if (type == 'weekly') {
            this.props.fetchClusterMetricsWeekly(this.props.emr_id, this.props.token);
            this.setState({
                ...this.state,
                show_charts: true,
                duration_type: event.target.value,
                xaxis_label: 'Time (in hours)',
                showRunningApps: false,
                primaryTab: currTab
            })
        } else if (type == 'monthly') {
            this.props.fetchClusterMetricsMonthly(this.props.emr_id, this.props.token);
            this.setState({
                ...this.state,
                show_charts: true,
                duration_type: event.target.value,
                xaxis_label: 'Time (in hours)',
                showRunningApps: false,
                primaryTab: currTab
            })
        } else if (type == 'custom') {
            this.handleInitialCustomCluster()
            this.setState({
                ...this.state,
                show_charts: false,
                duration_type: event.target.value,
                xaxis_label: 'Time',
                showRunningApps: false,
                primaryTab: currTab
            })
        }
    }

}

const mapStateToProps = state => {
    return {
        clusterMetricsHourlyData: state.allEmrHealthData.clusterMetricsHourlyData,
        clusterMetricsHourlyFetching: state.allEmrHealthData.clusterMetricsHourlyFetching,
        clusterMetricsDailyData: state.allEmrHealthData.clusterMetricsDailyData,
        clusterMetricsDailyFetching: state.allEmrHealthData.clusterMetricsDailyFetching,
        clusterMetricsWeeklyData: state.allEmrHealthData.clusterMetricsWeeklyData,
        clusterMetricsWeeklyFetching: state.allEmrHealthData.clusterMetricsWeeklyFetching,
        clusterMetricsMonthlyData: state.allEmrHealthData.clusterMetricsMonthlyData,
        clusterMetricsMonthlyFetching: state.allEmrHealthData.clusterMetricsMonthlyFetching,
        clusterMetricsAdviceDailyData: state.allEmrHealthData.clusterMetricsAdviceDailyData,
        clusterMetricsAdviceDailyFetching: state.allEmrHealthData.clusterMetricsAdviceDailyFetching,
        clusterMetricsAdviceWeeklyData: state.allEmrHealthData.clusterMetricsAdviceWeeklyData,
        clusterMetricsAdviceWeeklyFetching: state.allEmrHealthData.clusterMetricsAdviceWeeklyFetching,
        clusterMetricsAdviceMonthlyData: state.allEmrHealthData.clusterMetricsAdviceMonthlyData,
        clusterMetricsAdviceMonthlyFetching: state.allEmrHealthData.clusterMetricsAdviceMonthlyFetching,
        clusterAppsRunningData: state.allEmrHealthData.clusterAppsRunningData,
        clusterAppsRunningFetching: state.allEmrHealthData.clusterAppsRunningFetching,
        clusterMetricsGetCustomData: state.allEmrHealthData.clusterMetricsGetCustomData,
        allClusterMetricsDailyFetching: state.allEmrHealthData.allClusterMetricsDailyFetching,
        allClusterMetricsMonthlyFetching: state.allEmrHealthData.allClusterMetricsMonthlyFetching, 
        allClusterMetricsWeeklyFetching: state.allEmrHealthData.allClusterMetricsWeeklyFetching,
        allEmrHealthData: state.allEmrHealthData,
        clusterCostWeeklyData: state.emrCostData.clusterCostWeeklyData,
        clusterCostMonthlyData: state.emrCostData.clusterCostMonthlyData,
        clusterCostCustomData: state.emrCostData.clusterCostCustomData

    }
  }

  const mapDispatchToProps = dispatch => {
    return {
        fetchClusterMetricsHourly: (emr_id, token) => dispatch(fetchClusterMetricsHourly(emr_id, token)),
        fetchClusterMetricsDaily: (emr_id, token) => dispatch(fetchClusterMetricsDaily(emr_id, token)),
        fetchClusterMetricsWeekly: (emr_id, token) => dispatch(fetchClusterMetricsWeekly(emr_id, token)),
        fetchClusterMetricsMonthly: (emr_id, token) => dispatch(fetchClusterMetricsMonthly(emr_id, token)),
        fetchClusterMetricsAdviceDaily: (emr_id, token) => dispatch(fetchClusterMetricsAdviceDaily(emr_id, token)),
        fetchClusterMetricsAdviceWeekly: (emr_id, token) => dispatch(fetchClusterMetricsAdviceWeekly(emr_id, token)),
        fetchClusterMetricsAdviceMonthly: (emr_id, token) => dispatch(fetchClusterMetricsAdviceMonthly(emr_id, token)),
        fetchClusterCustomRange: (emr_id, from, to, token) => dispatch(fetchClusterCustomRange(emr_id, from, to, token)),
        fetchAllAdvice: (emr_id, time, token) => dispatch(fetchAllAdvice(emr_id, time, token)),
        fetchClusterAppsRunning: (emr_id, token) => dispatch(fetchClusterAppsRunning(emr_id, token)),
        fetchClusterAppsRunning: (emr_id, token) => dispatch(fetchClusterAppsRunning(emr_id, token)),
        fetchClusterCosts: (cluster_id, from, to, type, token) => dispatch(fetchClusterCosts(cluster_id, from, to, type, token))
    }
  }

export default connect(mapStateToProps, mapDispatchToProps)(RowData)
