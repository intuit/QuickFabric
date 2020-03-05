import React from 'react';

/**
 * Show status colored according to the value.
 */
export default class AMIRotationStatus extends React.Component {
    render() {
      const ami_rotation_days = this.props.dataItem.rotationDaysToGo;
      switch(true) {
        case (ami_rotation_days.includes('Left')):
          return <td><div className='cell-positive'></div><div className='value'>{ami_rotation_days}</div></td>
        
        case ami_rotation_days.includes('Overdue'):
          return <td><div className='cell-negative'></div><div className='value'>{ami_rotation_days}</div></td>
        
        default:
            return <td><div className='cell-progress'></div><div className='value'>{ami_rotation_days}</div></td>
      }
    }
  }