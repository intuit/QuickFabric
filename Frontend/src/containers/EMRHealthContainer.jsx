import React, { Component } from 'react'
import { EMRHealth } from '../components'
import { connect } from 'react-redux'
import { fetchEMRHealthData, fetchAccountEMRHealthData} from '../actions/emrHealth'
import { fetchUIDropdownList } from '../actions/emrManagement'
import '../components/EMRHealth/emrHealth.css'
let timer = null;

/**
 * Container for EMR Health component.
 */
class EMRHealthContainer extends Component {

  constructor(props) {
    super(props)

    this.handleRangeChange = this.handleRangeChange.bind(this)
  }


  render() {
    console.log("UI list success", this.props.uiListSuccess)
    return (
      <div className='emr-benchmarking-container'>
      
      <span id='displayTimer' className="displayTimer"/>
      
      <div id='displayDiv'/>
      {
        this.props.uiListSuccess &&
        <EMRHealth 
        token={this.props.token}
        signedIn={this.props.signedIn}
        locationName={this.props.location.pathname}
        dccRoles={this.props.dccRoles} 
        superAdmin={this.props.superAdmin}
        fetching={this.props.allEmrHealthFetching} 
        handleRangeChange={this.handleRangeChange} 
        uiListData={this.props.uiListData}
        data={this.props.allEmrHealthData } />
      }
      </div>
    )
  }

  componentDidMount() {
    this.handleRangeChange('all');
    this.props.fetchUIDropdownList(this.props.token);
  }
  countDown(i, callback ){

    var original = i;
    callback = callback || function(){};
    callback();
    return setInterval(() => {
        if (i < 0) {
          i = original;
          callback()
        }
        document.getElementById("displayTimer").innerHTML =  "Next Refresh in : "+ i+" Seconds";
        i-- 
    }, 1000);
}

  handleRangeChange(type,range) {
    //clearInterval(timer);
    if (type === 'all') {
      //timer = this.countDown(10,  this.props.fetchAllEMRHealthData(type));
      this.props.fetchEMRHealthData(type,this.props.token)
    } else if (type === 'exploratory') {
      //timer = this.countDown(300,  this.props.fetchExploratoryEMRHealthData(type));
      this.props.fetchEMRHealthData(type,this.props.token);
    }else if (type === 'schedule') {
      //timer = this.countDown(300,  this.props.fetchScheduleEMRHealthData(type));
      this.props.fetchEMRHealthData(type,this.props.token);
    }else if (type === 'accounts') {
      //timer = this.countDown(300,  this.props.fetchScheduleEMRHealthData(type));
      this.props.fetchAccountEMRHealthData(range,this.props.token);
    }
  }

  componentWillUnmount() {
    clearInterval(timer);        
  }

}

const mapStateToProps = state => {
  console.log(state)
  return {
    allEmrHealthData: state.allEmrHealthData.allEmrHealthData,
    allEmrHealthFetching: state.allEmrHealthData.allEmrHealthFetching,
    uiListData: state.allEmrHealthData.uiListData,
    uiListFetching: state.allEmrHealthData.uiListFetching,
    uiListSuccess: state.allEmrHealthData.uiListSuccess
  }
}

const mapDispatchToProps = dispatch => {
  return {
    fetchEMRHealthData: (type,token) => dispatch(fetchEMRHealthData(type,token)),
    fetchAccountEMRHealthData: (range,token) => dispatch(fetchAccountEMRHealthData(range,token)),
    fetchUIDropdownList: (token) => dispatch(fetchUIDropdownList(token))
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(EMRHealthContainer)