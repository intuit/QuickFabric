import React from 'react';

/**
 * Legends and filters for the observability graph.
 */
export default class Legends extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        return (
            <div className="legends">
              <ul className="legend">
                <li>
                    <span className="job waiting" /> Healthy
                </li>
                <li>
                    <span className="job running" /> Running
                </li>
                <li>
                    <span className="job terminated" /> Unhealthy
                </li>
                <li>
                    <input type="checkbox" value="scheduled" onChange={this.props.handleFilterClick} />
                    Scheduled
                </li>
                <li>
                    <input type="checkbox" value="exploratory" onChange={this.props.handleFilterClick} />
                    Exploratory
                </li>
                <li>
                    <input type="checkbox" value="transient" onChange={this.props.handleFilterClick} />
                    Transient
                </li>
                {this.props.segs.map((segment, index) => {
                    return (<li>
                        <input type="checkbox" value={segment.toLowerCase()} disabled={!(this.props.segmentFilters.includes(segment.toLowerCase()) || this.props.superAdmin || this.props.admin)} onChange={this.props.handleFilterClick} />
                        <span className={`${this.props.segmentFilters.includes(segment.toLowerCase()) || this.props.superAdmin || this.props.admin ? "" : "disabled-filter"}`}>{segment}</span>
                    </li>)
                })}
              </ul>
            </div>
          );
    }
}