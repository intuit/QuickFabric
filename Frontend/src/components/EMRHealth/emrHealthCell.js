import React, { Component } from 'react'

export default class JobStatusCell extends Component {
  render() {
    const emr_status = this.props.dataItem.emrStatus
    
    switch (emr_status) {
    case 'healthy':
      return <td><div className='cell positive'></div><div className='value'>{emr_status}</div></td>
    case 'unhealthy':
      return <td><div className='cell negative'></div><div className='value'>{emr_status}</div></td>
    case 'RUNNING':
      return <td><div className='cell neutral'></div><div className='value'>{emr_status}</div></td>
    case 'WAITING':
      return <td><div className='cell positive'></div><div className='value'>HEALTHY</div></td>
    default:
      return <td>{emr_status}</td>
    } 
  }
}