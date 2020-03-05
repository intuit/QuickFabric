import React, { Component } from 'react'

/**
 * Showing status of the cluster in the colored dot.
 */
export default class JobStatusCell extends Component {
  render() {
    const status = this.props.dataItem.status

      switch (status) {
      case 'SUCCEEDED':
        return <td><div className='cell-positive'></div><div className='value'>{status}</div></td>
      case 'FAILED':
        return <td><div className='cell-negative'></div><div className='value'>{status}</div></td>
      case 'TERMINATED':
        return <td><div className='cell-terminated'></div><div className='value'>{status}</div></td>
      case 'TERMINATED_WITH_ERRORS':
        return <td><div className='cell-terminated'></div><div className='value'>TERMINATED</div></td>
      case 'IN_PROGRESS':
        return <td><div className='cell-progress'></div><div className='value'>{status}</div></td>
      case 'IN_PROGRESS_IN_ERROR':
      return <td><div className='cell-negative'></div><div className='value'>{status}</div></td>
      case 'CREATED':
        return <td><div className='cell-created'></div><div className='value'>{status}</div></td>
      case 'TERMINATION_INITIATED':
        return <td><div className='cell-terminating'></div><div className='value'>{status}</div></td>
      case 'TERMINATING':
        return <td><div className='cell-terminating'></div><div className='value'>{status}</div></td>
      case 'RUNNING':
        return <td><div className='cell-positive'></div><div className='value'>{status}</div></td>
      case 'BOOTSTRAPPING':
        return <td><div className='cell-progress'></div><div className='value'>{status}</div></td>
      case 'HEALTHY':
        return <td><div className='cell-positive'></div><div className='value'>{status}</div></td>
      case 'unhealthy':
        return <td><div className='cell-negative'></div><div className='value'>{status}</div></td>
      case 'WAITING':
        return <td><div className='cell-positive'></div><div className='value'>{status}</div></td>
      case 'STARTING':
        return <td><div className='cell-created'></div><div className='value'>{status}</div></td>
      default:
        return <td>{status}</td>
    } 
  }
}