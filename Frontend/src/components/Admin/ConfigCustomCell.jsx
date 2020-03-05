import React from 'react';
import './admin.scss';
import { connect } from 'react-redux';
import { Grid, GridColumn as Column } from '@progress/kendo-react-grid'
import { 
    postAccountSetup,
    fetchConfigDefinitions
} from '../../actions/admin'
import { fetchUIDropdownList } from '../../actions/emrManagement'
import { withState } from '../../utils/components/with-state.jsx';
import {ToggleSlider} from '../../utils/components/ToggleSlider'

const StatefulGrid = withState(Grid);

class ConfigCustomCell extends React.Component {

    constructor(props) {
        super(props)
        this.state = {

        }
    }
    renderInputString = (dataItem) => {

        if(this.props.dataItem.inEdit) {
            return (
                <input type="text" className="k-cell-txtfield" value={dataItem.configValue} onChange={(e) => this.props.handleChange(this.props.dataItem.configId, e.target.value)} />
            )
        } else if(!this.props.dataItem.inEdit) {
            return (
                <p>{this.props.dataItem.configValue}</p>
            )
        }

    }
    renderInputToggle = (dataItem) => {
        return (
            <ToggleSlider id={this.props.dataItem.configId} handleToggleChange={this.props.handleToggleChange} toggleType='apiConfigEncrypt' toggleOn={this.props.dataItem.configValue} isDisabled={this.props.dataItem.inEdit}  />
        )
    }
    renderInputInteger = (dataItem) => {
        return (
            <input type="number" min="0" className="k-cell-txtfield" value={this.props.dataItem.configValue} onChange={(e) => this.props.handleChange(e)} />
        )

    }

    render() {
        console.log('config Custom cell', this.props)
        return (
            <td>
                <div>
                    {   this.props.dataItem.configDataType === 'String' ? this.renderInputString(this.props.dataItem) :
                        this.props.dataItem.configDataType === 'Boolean' ? this.renderInputToggle(this.props.dataItem) :
                        this.props.dataItem.configDataType === 'Integer' ? this.renderInputInteger(this.props.dataItem) : ''
                    }
                </div>
            </td> 
        )
    }
    
}
const mapStateToProps = state => {
    return {
        getConfigDefinitionsData: state.adminMetadata.getConfigDefinitionsData,
        getConfigDefinitionsSuccess: state.adminMetadata.getConfigDefinitionsSuccess,
        postAccountSetupSuccess: state.adminMetadata.postAccountSetupSuccess,
        postAccountSetupError: state.adminMetadata.postAccountSetupError,
        postAccountSetupErrorMessage: state.adminMetadata.postAccountSetupErrorMessage, 
    }
  }
const mapDispatchToProps = dispatch => {
    return {
        fetchUIDropdownList: (token) => dispatch(fetchUIDropdownList(token)),
        postAccountSetup: (data, token) => dispatch(postAccountSetup(data, token)),
        fetchConfigDefinitions: (token) => dispatch(fetchConfigDefinitions(token))
    }
  }
  export default connect(mapStateToProps, mapDispatchToProps)(ConfigCustomCell)
