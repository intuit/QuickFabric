import React from 'react';
import Graph from 'react-graph-vis';
import RowData from './RowData';
import Legends from './Legends';
import Toggle from 'react-toggle';
import './toggle.scss';
import './VisOptions';
import VisOptions from './VisOptions';
import ToggleText from '../ToggleText';

var graphDataWithCost = {}

export default class ObservabilityGraph extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            network: {},
            graphData: this.props.data,
            tableData: this.props.tableData,
            graph: {},
            showRowData: false,
            rowDataLeft: 0,
            rowDataTop: 0,
            selectedNode: '',
            emr_id: '',
            emr_name: '',
            rm_url: '',
            emr_status: '',
            account_id: '',
            active_nodes: '',
            coresUsagePct: '',
            memoryUsagePct: '',
            creation_timestamp: '',
            refresh_timestamp: '',
            cost: '',
            emr_wf_url: '',
            role: '',
            filters: [],
            costView: false,
            graphDataWithCost: {}
        }

        this.getNodeAt = this.getNodeAt.bind(this);
        this.handleNodeChange = this.handleNodeChange.bind(this);
        this.togglePopup = this.togglePopup.bind(this);
        this.handleFilterClick = this.handleFilterClick.bind(this);
        this.findAdjacentNodes = this.findAdjacentNodes.bind(this);
        this.handleToggleChange = this.handleToggleChange.bind(this);
        this.getDataWithCost = this.getDataWithCost.bind(this);
    }

    render = () => {
        if (graphDataWithCost === undefined || graphDataWithCost.length === 0) {
          this.getDataWithCost();
        }

        let data = {};
        if (Object.keys(this.state.graph).length === 0 && this.state.graph.constructor === Object){
          if (this.state.costView) {
            data = graphDataWithCost
          } else {
            data = this.props.data;
          }
        } else {
          data = this.state.graph
        }
        return (
            <div className='graph-wrapper'>
              <div className='cost-option'>
                <span style={{ fontSize: '20px', top: '5px' }}>Cost: </span>
                <label className='toggle-styling'>
                  <Toggle
                    defaultChecked={this.state.costView}
                    icons={{
                      checked: <ToggleText label="On" keyword="Off" text="costText" />,
                      unchecked: <ToggleText label="Off" keyword="Off" text="costText" />
                    }}
                    onChange={this.handleToggleChange}
                  />
                </label>
              </div>
                
                {this.state.showRowData &&
                  <RowData
                    left={this.state.rowDataLeft}
                    top={this.state.rowDataTop}
                    emr_id={this.state.emr_id}
                    emr_name={this.state.emr_name}
                    rm_url={this.state.rm_url}
                    emr_status={this.state.emr_status}
                    account_id={this.state.account_id}
                    active_nodes={this.state.active_nodes}
                    coresUsagePct={this.state.coresUsagePct}
                    memoryUsagePct={this.state.memoryUsagePct}
                    creation_timestamp={this.state.creation_timestamp}
                    refresh_timestamp={this.state.refresh_timestamp}
                    cost={this.state.cost}
                    emr_wf_url={this.state.emr_wf_url}
                    cancelPopup={this.togglePopup}
                    token={this.props.token}
                    apps_failed={this.state.apps_failed}
                    apps_pending={this.state.apps_pending}
                    apps_running={this.state.apps_running}
                    apps_succeeded={this.state.apps_succeeded}
                    role={this.state.role}
                    segments={this.props.segments}
                  />
                }
                <div>
                    <Legends 
                      handleFilterClick={this.handleFilterClick}
                      segmentFilters={this.props.segmentFilters}
                      superAdmin={this.props.superAdmin}
                      admin={this.props.admin}
                      segs={this.props.segs}
                    />
                    <Graph 
                        graph={data} 
                        options={VisOptions} 
                        events={{
                            selectNode: this.handleNodeChange
                        }} 
                        getNetwork={network => this.setState({ network })}
                        style={{ height: "79vh", width: "82vw" }} 
                        />
                </div>
            </div>
        )
    }

    // Triggered when cost toggle is clicked.
    handleToggleChange = () => {
      if (Object.keys(graphDataWithCost).length === 0 && graphDataWithCost.constructor === Object) {
        this.getDataWithCost();
      }
  
      this.setState({
        ...this.state,
        costView: !this.state.costView
      })
    }

    /**
     * Filters out the data based on the filters selected. 
     * First a list of filters containing the id of the filter nodes in the graph data is created. 
     * Then nodes and edges are removed which are not part of the filter list. 
     * If the filter list is empty then original graph data is used.
     */
    handleFilterClick = event => {
      let newFilter = event.target.value;
      if (newFilter === 'scheduled') {
         if (this.state.filters.findIndex(el => el.includes('scheduled')) !== -1) {
            let i = -1;
            while ((i = this.state.filters.findIndex(el => el.includes('scheduled'))) !== -1) {
              this.state.filters.splice(i, 1);
            }
         } else {
           this.props.accounts.forEach(act => {
             this.state.filters.push(act + '.scheduled');
           })
         }
      } else if (newFilter === 'exploratory') {
        if (this.state.filters.findIndex(el => el.includes('exploratory')) !== -1) {
           let i = -1;
           while ((i = this.state.filters.findIndex(el => el.includes('exploratory'))) !== -1) {
             this.state.filters.splice(i, 1);
           }
        } else {
          this.props.accounts.forEach(act => {
            this.state.filters.push(act + '.exploratory');
          })
        }
     } else if (newFilter === 'transient') {
        if (this.state.filters.findIndex(el => el.includes('transient')) !== -1) {
          let i = -1;
          while ((i = this.state.filters.findIndex(el => el.includes('transient'))) !== -1) {
            this.state.filters.splice(i, 1);
          }
        } else {
          this.props.accounts.forEach(act => {
            this.state.filters.push(act + '.transient');
          })
        }
      } else {
        if (this.state.filters.indexOf(newFilter) > -1) {
          this.state.filters.splice(this.state.filters.indexOf(newFilter), 1);
        } else {
          this.state.filters.push(newFilter)
        }
      }
      
      if (this.state.filters.length === 0) {
        this.setState({
          ...this.state,
          graph: {}
        })
      } else {
        let graph_nodes = [];
        if (this.state.costView) {
          graph_nodes = graphDataWithCost.nodes;
        } else {
          graph_nodes = this.state.graphData.nodes;
        }
        let newgraph = {};
        let newNodes = [];
        newNodes = graph_nodes.filter((value) => {
          if (value.id === 1 || value.group === 'ACT' || this.state.filters.includes(value.id)) return value;
        })

        if (this.state.filters.filter(x => x.includes('.')).length > 0 && this.state.filters.filter(x => !x.includes('.')).length > 0) {
          let filteredNodes = graph_nodes.filter(node => this.state.filters.includes(node.label.split('-')[1]) && this.state.filters.includes(this.props.dataMap[node.id].account + '.' + node.label.split('-')[0]))
          newNodes = [...newNodes, ...filteredNodes]
        } else if (this.state.filters.filter(x => !x.includes('.')).length > 0) {
          let filteredNodes = graph_nodes.filter(node => this.state.filters.includes(node.label.split('-')[1]) || node.group === 'ACT_TYPE')
          newNodes = [...newNodes, ...filteredNodes]
        } else {
          let adjNodes = this.findAdjacentNodes(this.state.filters)
          newNodes = [...newNodes, ...adjNodes]
        }

        if (this.state.costView) {
          const adjTo = graphDataWithCost.edges.filter(x => {
            if (newNodes.filter(y => y.group === 'DATA' && x.from === y.id).length > 0) {
              return x;
            }
          }).map(x => x.to)
          const adjNodes = graphDataWithCost.nodes.filter(x => adjTo.includes(x.id))
          newNodes = [...newNodes, ...adjNodes]
        }

        newgraph.nodes = newNodes;
        newgraph.edges = this.state.graphData.edges;
        
        this.setState({ 
          ...this.state,
          graph: newgraph
        })
      }
    }

    /**
     * Helper method used to find all the adjacent nodes given a list of nodes.
     */
    findAdjacentNodes = nodeIds => {
      const adjacentTo = this.state.graphData.edges
        .filter(x => nodeIds.includes(x.from))
        .map(x => x.to);
  
      let adjacentToNodes = this.state.graphData.nodes.filter(x =>
        adjacentTo.includes(x.id),
      );

      const cluster_types = this.props.segs;
      let contains_cluster_filter = false;
      cluster_types.forEach((type) => {
        if (nodeIds.includes(type)) {
          contains_cluster_filter = true;
        }
      })

      if (contains_cluster_filter) {
        adjacentToNodes = adjacentToNodes.filter((value) => {
          let type = value.label.split('-')[1];
          if (nodeIds.includes(type)) {
            return value;
          }
        })
      }

      return [...adjacentToNodes];
    };

    /**
     * Triggered when clicked on a node to open the popup with cluster information.
     */
    handleNodeChange = param => {
        let clickedNode = this.getNodeAt(param.pointer.DOM);
        if (clickedNode !== undefined && clickedNode.group === 'DATA' && this.state.selectedNode !== clickedNode) {
          let data = this.props.dataMap[clickedNode.id];
          this.setState({
            ...this.state,
            showRowData: true,
            rowDataLeft: param.pointer.DOM.x,
            rowDataTop: param.pointer.DOM.y,
            selectedNode: clickedNode,
            emr_id: clickedNode.id,
            emr_name: data.emr_name,
            rm_url: data.rm_url,
            emr_status: data.emr_status,
            account_id: data.account,
            active_nodes: data.active_nodes,
            coresUsagePct: data.coresUsagePct,
            memoryUsagePct: data.memoryUsagePct,
            creation_timestamp: data.cluster_create_timestamp,
            refresh_timestamp: data.refresh_timestamp,
            cost: data.cost,
            emr_wf_url: data.emr_wf_url,
            apps_failed: data.apps_failed,
            apps_pending: data.apps_pending,
            apps_running: data.apps_running,
            apps_succeeded: data.apps_succeeded,
            role: data.role
          })
        } else {
          this.setState({
            ...this.state,
            showRowData: false,
            rowDataLeft: 0,
            rowDataTop: 0,
            selectedNode: ''
          })
        }
    }

    /**
     * Returns the node which is clicked in the graph.
     */
    getNodeAt = pos => {
        const clickedNodeId = this.state.network.getNodeAt(pos);
        if (clickedNodeId !== undefined) {
          const clickedNode = this.props.data.nodes.find(i => i.id === clickedNodeId);
          return clickedNode;
        }
        return undefined;
      };
  
    /**
     * Close the cluster information popup.
     */
    togglePopup = () => {
      this.setState({
        ...this.state,
        showRowData: false
      })
      this.forceUpdate();
    }

    /**
     * Adds cost nodes to the graph data when cost toggle is switched on.
     */
    getDataWithCost = () => {
      graphDataWithCost = this.state.graphData;
      let dataNodes = this.state.graphData.nodes;
      let dataEdges = this.state.graphData.edges;
      graphDataWithCost.nodes.forEach((value) => {
        if (value.group === 'DATA') {
          let costVal = this.props.dataMap[value.id].cost;
          let node = { id: value.id + '$', label: '$'+costVal, group: "COST", color: '#5dd2f0' }
          let edge = { from: value.id, to: value.id + '$' }
          dataNodes.push(node);
          dataEdges.push(edge);
        }
      })
    }
}