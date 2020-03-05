import React from 'react';
import { Grid, GridColumn as Column } from '@progress/kendo-react-grid'
import emrStepStatusCell from '../emrStepStatusCell'
import { withState } from '../../../utils/components/with-state.jsx';

const StatefulGrid = withState(Grid);

/**
 * Component to show the steps executed in the create cluster process.
 */
export default class ModalStepsStatusDetail extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            dataDetails: []
        }
    }

    render() {
        let data = this.props.stepsStatusData == undefined ? [] : this.props.stepsStatusData;
        let popupLabel = "Steps";
        if (this.props.type !== undefined) {
            if (this.props.type == "custom") {
                popupLabel = "Custom Steps";
                data = data.filter(x => x.stepType == "Custom");
            } else if (this.props.type == "bootstraps") {
                popupLabel = "Bootstraps";
                data = data.filter(x => x.stepType == "Bootstrap")
            }
        }
        return (
            <div>
            <div className='popup'>
                <div className='popup_inner'>
                    <div className='popup_close'>
                        <button className='close_popup' onClick={this.props.onClose}>
                            <i className='material-icons'>keyboard_arrow_right</i>
                        </button>
                    </div>
                    <div style={{ padding: '35px' }}>
                    <h3 style={{marginBottom: '28px'}}>{popupLabel} Status: <span style={{color: "rgb(0, 151, 228)"}}>{this.props.name}</span></h3>
                        <StatefulGrid
                            data={data}
                            resizeable={true}
                            reorderable={true}
                            pageable={false}
                            {...this.state}
                        >
                            <Column field="stepId" title="Step ID" width="auto" />
                            <Column field="name" title="Step Name" width="auto" />
                            <Column field="status" title="Step Status" width="auto" cell={emrStepStatusCell} />
                            <Column field="stepType" title="Step Type" width="auto" />
                            <Column field="creationTimestamp" title="Creation Timestamp" width="auto" />                         
                        </StatefulGrid>
                    </div>
                </div>
            </div>
        </div>

        )
    }
}