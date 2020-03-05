import React, { Component } from 'react'

/**
 * Showing status as a colored dot.
 */
export default class JobStatusCell extends Component {
  render() {
    console.log('inside step status', this.props)
    let stepstatus = this.props.dataItem && this.props.dataItem.status

      switch (stepstatus) {
      case 'COMPLETED':
        return <td><div className='cell-positive'></div><div className='value'>{stepstatus}</div></td>
      case 'FAILED':
        return <td><div className='cell-negative'></div><div className='value'>{stepstatus}</div></td>
      case 'CANCELLED':
        return <td><div className='cell-terminated'></div><div className='value'>{stepstatus}</div></td>
      case 'NEW':
        return <td><div className='cell-progress'></div><div className='value'>{stepstatus}</div></td>
      case 'CREATED':
        return <td><div className='cell-created'></div><div className='value'>{stepstatus}</div></td>
      case 'RUNNING':
        return <td><div className='cell-positive'></div><div className='value'>{stepstatus}</div></td>
      case 'PENDING':
        return <td><div className='cell-terminating'></div><div className='value'>{stepstatus}</div></td>
      case 'COMPLETED_TERMINATEDCLUSTER':
        return <td><div className='cell-terminated'></div><div className='value'>{stepstatus}</div></td>
      default:
        return <td>{stepstatus}</td>
    } 
  }
}