import React from 'react';
import { GridCell } from '@progress/kendo-react-grid';
import { H5, Icon, Intent, Label, Slider } from "@blueprintjs/core";
export function MyCommandCell({ edit, remove, add, update, discard, decrypt, cancel, editField, removeAllowed, decryptAllowed }) {
    return class extends GridCell {
        render() {
            const { dataItem } = this.props;
            const inEdit = dataItem[editField];
            const isNewItem = dataItem.configId === undefined;

            return inEdit ? (
                <td className="k-command-cell">
                    <button
                        className="k-button k-grid-save-command"
                        onClick={(e) => isNewItem ? add(e, dataItem) : update(e, dataItem)}
                    >
                        {isNewItem ? 'Add' : 'Update'}
                    </button>
                    <button
                        className="k-button k-grid-cancel-command"
                        onClick={(e) => isNewItem ? discard(e, dataItem) : cancel(e, dataItem)}
                    >
                        {isNewItem ? 'Discard' : 'Cancel'}
                    </button>
                </td>
            ) : (
                <td className="k-command-cell">
                    <button
                        disabled={dataItem.decrypted && dataItem.isEncrypted || !dataItem.isEncrypted ? false : true}
                        className="k-primary k-button k-grid-edit-command"
                        onClick={(e) => edit(e, dataItem)}
                    >
                        Edit
                    </button>
                    {
                        removeAllowed ? 
                        <button
                        className="k-button k-grid-remove-command"
                        onClick={() => {remove(dataItem)}
                        }
                        >
                            Remove
                        </button> :
                        null
                    }
                    
                    {
                        decryptAllowed ? 
                        <button
                        disabled={!dataItem.isEncrypted ? true : false}
                        className={dataItem.decrypted && dataItem.isEncrypted ? "k-button k-grid-remove-command k-eye-btn" : "k-button k-grid-remove-command k-eye-close-btn"}
                        style={{marginLeft: '5px'}}
                        onClick={(e) => {decrypt(e, dataItem, dataItem.accountId)}
                        }
                        >
                            {dataItem.decrypted && dataItem.isEncrypted ? <Icon icon={'eye-on'} iconSize={24}/>: <Icon icon={'eye-off'} iconSize={24}/>}
                        </button> :
                        null
                    }
                </td>
            );
        }
    }
};

