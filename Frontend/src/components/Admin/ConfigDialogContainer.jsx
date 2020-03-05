import React from 'react';
import { Dialog, DialogActionsBar } from '@progress/kendo-react-dialogs';
import { Input, NumericTextBox } from '@progress/kendo-react-inputs';
import {ToggleSlider} from '../../utils/components/ToggleSlider.jsx'
import './admin.scss'

export default class DialogContainer extends React.Component {
  constructor(props) {
      super(props);
      this.state = {
          objectInEdit: this.props.dataItem || null,
          isNull: this.props.dataItem.isNull,
          toggleValue: this.props.dataItem.isNull ? false : this.props.dataItem.configValue
      };
  } 
  handleSubmit(event) {
      event.preventDefault();
  }
  renderToggleButton = () => {
    return (
        <div className="config-management-toggle">
            <ToggleSlider id={this.state.objectInEdit.configId + '135'} handleToggleChange={this.handleToggleEncrypt} toggleType='apiConfigEncrypt' toggleOn={this.state.toggleValue} isDisabled={false} />
        </div>
    )
  }
  handleToggleEncrypt = (id, val) => {
    console.log('VAL', val)

    let editObj = this.state.objectInEdit

    editObj.configValue = !val
    this.setState({
        toggleValue: !val,
        objectInEdit: editObj
    })

  }
  onDialogInputChange = (event) => {
      let target = event.target;
      console.log('TARGET', target)
      const value = target.type === 'checkbox' ? target.checked : target.value;
      const name = 'configValue'


      const edited = this.state.objectInEdit;
      console.log('edited', edited, target.checked)
      edited[name] = value;

      this.setState({
          objectInEdit: edited
      });
  }

  render() {
      return (
        <div className={this.state.objectInEdit.configDataType === 'Boolean' ? 'k-toggle-mode' : null}>


        <Dialog
            onClose={this.props.cancel}
        >           
                <div className="k-dialog-container" style={{ marginBottom: '1rem' }}>
                    <label>
                    <div className="dialog-title">
                        <span>{this.state.objectInEdit.configName}:</span><br />
                    </div>
                    <div className="dialog-description">
                        <span>{this.state.objectInEdit.configDescription}</span><br />
                    </div>
                    <div className="dialog-action">
                        {   this.state.objectInEdit.configDataType === 'String' ? <Input id={this.state.objectInEdit.configId} type="text" name="configValue" value={this.state.objectInEdit.configValue || ''} onChange={this.onDialogInputChange} /> :
                            this.state.objectInEdit.configDataType === 'Int' ?  <NumericTextBox id={this.state.objectInEdit.configId}  name="configValue" value={this.state.objectInEdit.configValue != null || this.state.objectInEdit.configValue != undefined ? this.state.objectInEdit.configValue : 0} onChange={this.onDialogInputChange} /> : 
                            this.state.objectInEdit.configDataType === 'Boolean' ? this.renderToggleButton() : ''
                        }   
                    </div>


                    </label>
                </div>
                <button
                    className="k-button k-grid-remove-command k-modal-button-cancel"
                    onClick={this.props.cancel}
                >
                    Cancel
                </button>
                <button
                    className="k-primary k-button k-grid-edit-command k-modal-button-save"
                    onClick={(e) => {this.props.save(e, this.state.objectInEdit)}}
                >
                    Save
                </button>
        </Dialog>
        </div>
    );
  }
}