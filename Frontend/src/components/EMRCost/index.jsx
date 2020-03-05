import React, { Component } from 'react'
import { Grid, GridColumn as Column } from '@progress/kendo-react-grid'
import { Spinner } from '@blueprintjs/core'
import emrHealthCell from '../EMRHealth/emrHealthCell'
import './emrCost.scss'
import '../EMRHealth/emrHealth1.scss'

import { withState } from '../../utils/components/with-state.jsx';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip } from 'recharts';

const StatefulGrid = withState(Grid);

/**
 * EMR Cost component: Table containing cost data with a final column of cost trend chart visualization.
 */
export default class EMRCost extends Component{
    account_range = '';
    constructor(props){
        super(props)
        
        this.state = {
            data: this.props.data,
            show: false,
            primaryTab: 'all'
        }

        this.renderTableData = this.renderTableData.bind(this)
        this.getTableData = this.getTableData.bind(this);
    }

    render = () => {
        return (
        <div className="emr-cost-component">
            <h2>EMR <i className="material-icon title-arrow">double_arrow</i> <span style={{ color: "#0097E4" }}>Cost</span></h2> 
            {
              this.props.fetching || this.props.data.length === 0? 
                <Spinner /> :
                this.renderTableData(this.props.data)
            }
        </div>
        )
    }

    renderTableData = data => {
      // Modifying data: Adding costs per month array as columns for table to consume.
      let modifiedData = this.getTableData(data);
      return (
        <StatefulGrid
          data={modifiedData.newData}
          style={{ "marginTop": 30 , height: "90vh", fontSize: '13px' }}
        >
          <Column field="emrGroup" title="EMR Group" width="150px" />
          <Column field="account" title="Account" width="120px" />
          <Column field="segment" title="Segment" width="100px" />
          <Column field="businessOwner" title="Business Owner" width="150px" />
          {modifiedData.costTitles.map((title, index) => {
            return (
            <Column field={title} title={title} width="80px" cell={(props) =>
              props.dataItem[title] == "0" ?
              <td>
                <span>N/A</span>
              </td> :
              <td>
                <span>${props.dataItem[title]}</span>
              </td>
            } />
            )
          })}
          <Column title="Cost Trend" width="auto" 
            cell={(props) => 
              <td>
                <LineChart width={480} height={120} data={props.dataItem.costPerMonth.reverse()}
                    >
                    <XAxis dataKey="billMonth" />
                    <YAxis />
                    <CartesianGrid strokeDasharray="3 3"/>
                    <Tooltip />
                    <Line type="monotone" dataKey="cost" stroke="#0097E4" activeDot={{r: 8}}/>
                </LineChart>
              </td>
            } 
          />
        </StatefulGrid>
      )
    }

  getTableData = data => {
    data = this.shortlistData(data.emrGroupCost);
    let newData = []
    let costTitles = []
    data.map((row, key) => {
      let vals = {
        emrGroup: row.emrGroup,
        account: row.account,
        segment: row.segment,
        businessOwner: row.businessOwner,
        costPerMonth: row.costPerMonth
      }
      row.costPerMonth.map((c) => {
        vals[c.billMonth] = c.cost;
        if (!costTitles.includes(c.billMonth))
          costTitles.push(c.billMonth)
      })
      newData.push(vals)
    })
    return {
      newData: newData,
      costTitles: costTitles
    }
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
      roleMap[value['segmentName'].toLowerCase()] = accounts;
    })
    
    let newData = data;
    newData = newData.filter((value) => {
      let type = value.segment;
      if (type in roleMap) {
        if (roleMap[type].includes(value.account)) {
          return value;
        }
      }
    });
  
    return newData;
  }
}