import React, { Component } from 'react'
import { EMRCost } from '../components'
import { connect } from 'react-redux'
import { fetchEMRCostData, fetchEMRCostDataAccountWise, fetchEMRClusterCost } from '../actions/emrCost'

/**
 * Container for EMR Cost component
 */
class EMRCostContainer extends Component {
  constructor(props) {
    super(props)

    this.handleRangeChange = this.handleRangeChange.bind(this)
  }

  render() {
    return (
      <div className='emr-cost-container'>
      
      <div id='displayDiv'/>
        <EMRCost 
          fetching={this.props.fetching} 
          handleRangeChange={this.handleRangeChange} 
          data={this.props.emrCostData} 
          dccRoles={this.props.dccRoles} 
          superAdmin={this.props.superAdmin} 
        />
      </div>
    )
  }

  componentDidMount() {
    this.props.fetchEMRClusterCost(this.props.token)
  }

  handleRangeChange(type, account) {
    if (type === 'all') {
      this.props.fetchEMRCostData(type,this.props.token);
    } else if (type === 'exploratory') {
      this.props.fetchEMRCostData(type,this.props.token);
    }else if (type === 'scheduled') {
      this.props.fetchEMRCostData(type,this.props.token);
    }else if (type === 'account_id') {
      this.props.fetchEMRCostDataAccountWise(account,this.props.token);
    }
  }

}

const mapStateToProps = state => {
  console.log(state)
  return {
    emrCostData: state.emrCostData.emrCostData,
    fetching: state.emrCostData.emrCostFetching,
  }
}

const mapDispatchToProps = dispatch => {
  return {
    fetchEMRCostData: (type,token) => dispatch(fetchEMRCostData(type,token)),
    fetchEMRCostDataAccountWise: (account,token) => dispatch(fetchEMRCostDataAccountWise(account,token)),
    fetchEMRClusterCost: (token) => dispatch(fetchEMRClusterCost(token))
  }
}

export default connect(mapStateToProps, mapDispatchToProps)(EMRCostContainer)