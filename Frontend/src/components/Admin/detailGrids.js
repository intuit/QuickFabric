import React, { Component } from 'react';
import './admin.scss';
import { Grid, GridColumn as Column, GridDetailRow, GridToolbar, GridCell } from '@progress/kendo-react-grid'

import { withState } from '../../utils/components/with-state.jsx';
const StatefulGrid = withState(Grid);

class AccountDetails extends Component {
  render() {
    let accountsList = this.props.dataItem.accounts.map((a, i) => {
        return a.accountId
    }).join(', ')
    return <td><span>{accountsList}</span></td>
  }
}

class DCCAccountsGrid extends GridDetailRow {
    constructor(props) {
      super(props)
      this.state = {}
      this.expandChange = this.expandChange.bind(this);
      }
      expandChange(event) {
        event.dataItem.expanded = event.value;
        this.setState({ ...this.state });
  
  
        if (!event.value || event.dataItem.details) {
          return;
        }
      }
      render() {
          const data = this.props.dataItem.accounts;
          if (data) {
              return (
                <div>
                  <StatefulGrid
                  data={data}
                  filterable={true}
                  pageable={data.length > 10 ? true : false}
                  >
                    <Column field="accountId" title={'Account ID'}  />
                    <Column field="accountType" title={'Account Type'}  />
                  </StatefulGrid>
                </div>
  
              );
          }
          return (
              <div style={{ height: "50px", width: '100%', display: 'none' }}>
                  <div style={{ position: 'absolute', width: '100%' }}>
                      <div className="k-loading-image" />
                  </div>
              </div>
          );
      }
}
class DCCSegmentGrid extends GridDetailRow {
    constructor(props) {
      super(props)
      this.state = {}
      this.expandChange = this.expandChange.bind(this);
      }
      expandChange(event) {
        event.dataItem.expanded = event.value;
        this.setState({ ...this.state });
  
  
        if (!event.value || event.dataItem.details) {
          return;
        }
      }
      render() {
          const data = this.props.dataItem.segments;
          if (data) {
              return (
                <div>
                  <StatefulGrid
                    data={data}
                    resizeable
                    reorderable={true}
                    pageable={data.length > 10 ? true : false}
                    {...this.state}              
                  >
                    <Column field="segmentName" title={'Segment Name'}  />
                    <Column field="accountId" title={'Accounts'} cell={AccountDetails} />
                  </StatefulGrid>
                </div>
  
              );
          }
          return (
              <div style={{ height: "50px", width: '100%', display: 'none' }}>
                  <div style={{ position: 'absolute', width: '100%' }}>
                      <div className="k-loading-image" />
                  </div>
              </div>
          );
      }
}
export { 
    DCCAccountsGrid,
    DCCSegmentGrid
}