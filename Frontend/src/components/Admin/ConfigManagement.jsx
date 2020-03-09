import React from 'react';
import './admin.scss';
import { connect } from 'react-redux';
import { Grid, GridColumn as Column } from '@progress/kendo-react-grid'
import { ComboBox } from '@progress/kendo-react-dropdowns';
import baseURL from '../../api-config';
import { 
    postAddRoles,
    postRemoveRoles,
    fetchUserRoles,
    postAccountSetup,
    fetchConfigDefinitions,
    fetchConfigDefinitionsById,
    fetchConfigDefinitionList,
    putConfigDefinitions,
    decryptConfigByName,
    clearStatus
} from '../../actions/admin'
import { fetchUIDropdownList } from '../../actions/emrManagement'
import { withState } from '../../utils/components/with-state.jsx';
import {ToggleSlider} from '../../utils/components/ToggleSlider'
import { MyCommandCell } from '../../utils/components/MyCommandCell'
import ConfigDialogContainer from './ConfigDialogContainer'
const StatefulGrid = withState(Grid);

class ConfigManagement extends React.Component {
   
    constructor(props) {
        super(props)
        this.state = {
            accountList: this.props.uiListData.accounts,
            data: [],
            selectedAccount: '',
            selectedConfig: {},
            dataLoaded: false,
            objectInEdit: undefined,
            decrypted:false,
            awaitUpdate: {
                dataItem: {},
                awaiting: false
            }
        }
        this.CommandCell = MyCommandCell({
            edit: this.enterEdit,
            remove: this.remove,
            decrypt: this.decrypt,
            add: this.add,
            discard: this.discard,

            update: this.update,
            cancel: this.cancel,
            decrypted: this.state.decrypted,
            editField: "inEdit",
            removeAllowed: false,
            decryptAllowed: true
        });
    }
    
    enterEdit = (e, dataItem) => {
        if(dataItem.configDataType === 'Boolean' && typeof dataItem.configValue === 'string') {
            if(dataItem.configValue === 'False' || dataItem.configValue === 'false') {
                dataItem.configValue = false
            } else if(dataItem.configValue === 'True' || dataItem.configValue === 'true') {
                dataItem.configValue = true
            }
        }
        e.preventDefault()
        this.setState({ objectInEdit: this.cloneObject(dataItem) }, () => {console.log('Objet in Edit ======>', this.state.objectInEdit)});
    }
    dialogTitle() {
        return `${this.state.objectInEdit.objectId === undefined ? 'Add' : 'Edit'} product`;
    }
    cloneObject(object) {
        return Object.assign({}, object);
    }

    remove = (dataItem) => {
        const data = [ ...this.state.data ];
        this.removeItem(data, dataItem);
        this.setState({ data });
    }
    decrypt = async (e, dataItem, accId) => {
        e.preventDefault()
        if(!dataItem.decrypted && dataItem.isEncrypted) {
            this.props.decryptConfigByName(dataItem.configName, accId, this.props.token)

        } else if (dataItem.decrypted && dataItem.isEncrypted && dataItem.encryptValue !== '') {
            this.setState({
                data: this.state.data.map((d, i) => {
                    if(d.configName === dataItem.configName) {
                        d.configValue = d.encryptValue
                        d.decrypted = false
                        return d
                    } else {
                        return d
                    }
                })
            })
        } else {}

    }

    add = (e, dataItem) => {
        e.preventDefault()
        dataItem.inEdit = undefined;
        this.setState({
            data: [ ...this.state.data ]
        });
    }

    discard = (e, dataItem) => {
        e.preventDefault()
        const data = [ ...this.state.data ];
        this.removeItem(data, dataItem);

        this.setState({ data });
    }

    update = (e, dataItem) => {
        e.preventDefault()
        const data = [ ...this.state.data ];
        const updatedItem = { ...dataItem, inEdit: undefined };

        this.updateItem(data, updatedItem);
        this.setState({ data });
    }
    checkDecrypted = (dataObj, i, val) => {
        if(val === true && !dataObj.decrypted) {
            let url = dataObj.accountId === '' ? (baseURL + `/configurations/decrypt/${dataObj.configName}`) : (baseURL + `/configurations/decrypt/${dataObj.configName}/${dataObj.accountId}`)
            fetch(url, {
              method: 'GET',
              headers: {
                'Content-type': 'application/json',
                  'Authorization': 'Bearer ' + this.props.token
              }
            }).then(res => res.json()).then(data => {
                let updateObj = dataObj
                updateObj.configValue = data.configValue
                updateObj.isEncrypted = !val
                
                this.setState({
                    data: this.state.data.map((d, i) => {
                        if(d.configName === updateObj.configName) {
                            return updateObj
                        } else { return d }
                    })
                })
                if(this.props.type === 'accountConfig') {
                    let dataobj = {
                        configName: updateObj['configName'],
                        configValue: updateObj['configValue'] === 'null' ? null : updateObj['configValue'],
                        isEncrypted: updateObj['isEncrypted'],
                        configDataType: updateObj['configDataType'],
                        configType: updateObj['configType'],
                        isMandatory: updateObj['isMandatory'],
                        isEncryptionRequired: updateObj['isEncryptionRequired'],
                        accountId: this.state.selectedAccount
                    }
                    this.props.putConfigDefinitions(this.state.selectedAccount, dataobj, this.props.token)
                } else if(this.props.type === 'applicationConfig') {
                    let dataobj = {
                        configName: updateObj['configName'],
                        configValue: updateObj['configValue'] === 'null' ? null : updateObj['configValue'],
                        isEncrypted: updateObj['isEncrypted'],
                        configDataType: updateObj['configDataType'],
                        configType: updateObj['configType'],
                        isMandatory: updateObj['isMandatory'],                                            
                        isEncryptionRequired: updateObj['isEncryptionRequired']
                    }
                    this.props.putConfigDefinitions('', dataobj, this.props.token)
                }
                return data
            }).catch(err => {
                console.log('ConfigManagement.jsx --- ERR ******** ', err)
            }) 
        }
    }
    handleToggleEncrypt2 = (id, val) => {
        let i = this.state.data.findIndex(c => c.configId + '_acc' == id);

        if(this.state.data[i].isEncrypted && !this.state.data[i].decrypted) {
            this.checkDecrypted(this.state.data[i], i, val)
        } 

    }
    handleToggleEncrypt = (id, val) => {
        var newObj = this.state.data;
        var tempData = this.state.data;
        let i
        if(this.props.type === 'accountConfig') {
            i = this.state.data.findIndex(c => c.configId + '_acc' == id);
        }
        if(this.props.type === 'applicationConfig') {
            i = this.state.data.findIndex(c => c.configId + '_app' == id);
        }       
        newObj[i].isEncrypted = !val
        let updateObj = newObj[i]
        
        if(val === true && !tempData[i].decrypted) {
            this.checkDecrypted(this.state.data[i], i, val)
        } else {
            this.setState({
                ...this.state,
                data:  newObj
            }, () => console.log('handleToggleEncrypt ---> state', this.state))
    
            if(this.props.type === 'accountConfig') {
                let dataobj = {
                    configName: updateObj['configName'],
                    configValue: updateObj['configValue'] === 'null' ? null : updateObj['configValue'],
                    isEncrypted: updateObj['isEncrypted'],
                    configDataType: updateObj['configDataType'],
                    configType: updateObj['configType'],
                    isMandatory: updateObj['isMandatory'],
                    configValue: updateObj['configValue'],
                    isEncryptionRequired: updateObj['isEncryptionRequired'],
                    accountId: this.state.selectedAccount
                }
                this.props.putConfigDefinitions(this.state.selectedAccount, dataobj, this.props.token)
            } else if(this.props.type === 'applicationConfig') {
                let dataobj = {
                    configName: updateObj['configName'],
                    configValue: updateObj['configValue'] === 'null' ? null : updateObj['configValue'],
                    isEncrypted: updateObj['isEncrypted'],
                    configDataType: updateObj['configDataType'],
                    configType: updateObj['configType'],
                    isMandatory: updateObj['isMandatory'],
                    configValue: updateObj['configValue'],
                    isEncryptionRequired: updateObj['isEncryptionRequired']
                }
                this.props.putConfigDefinitions('', dataobj, this.props.token)
            }
        }

    }
    handleToggleConfig = (id, val) => {
        var newObj = this.state.data;
        let i = this.state.data.findIndex(c => c.configId == id);
        newObj[i].configValue = !val

        this.setState({
            ...this.state,
            data:  newObj
        }, () => console.log('handleToggleConfig -------> State', this.state))
    }
    cancel = (e, dataItem) => {
        e.preventDefault()
        this.setState({ objectInEdit: undefined });
    }

    save = (e, item) => {
        e.preventDefault()
        let data = this.state.data
        let index = data.findIndex(d => d === item || (item.configId && d.configId === item.configId));
        if (index >= 0) {
            data[index] = { ...item };
        }
        this.setState({
            data: data,
            objectInEdit: undefined
        });
        if(item['isMandatory']) {item['isEncrypted'] = true}
        let dataobj = {
            configName: item['configName'],
            configValue: item['configValue'] === 'null' ? null : item['configValue'],
            isEncrypted: item['isEncrypted'],
            configDataType: item['configDataType'],
            configType: item['configType'],
            isMandatory: item['isMandatory'],
            configValue: item['configValue'],
            isEncryptionRequired: item['isEncryptionRequired'],
            accountId: this.state.selectedAccount
        }
        this.props.putConfigDefinitions(this.state.selectedAccount, dataobj, this.props.token)
    }

    itemChange = (event) => {
        const data = this.state.data.map(item =>
            item.configId === event.dataItem.configId ?
            { ...item, [event.field]: event.value } : item
        );

        this.setState({ data });
    }
    inputConfigChange = (e, id) => {
        e.preventDefault()
        const data = this.state.data.map(item =>
            item.configId === id ?
            { ...item, configValue: e.target.value } : item
        );

        this.setState({ data });
    }

    addNew = () => {
        const newDataItem = { inEdit: true, Discontinued: false };

        this.setState({
            data: [ newDataItem, ...this.state.data ]
        });
    }

    cancelCurrentChanges = () => {
        this.setState({ data: [ ...this.props.getConfigByIdData ] });
    }
    generateId = data => data.reduce((acc, current) => Math.max(acc, current.configId), 0) + 1;

    removeItem(data, item) {
        let index = data.findIndex(p => p === item || item.configId && p.configId === item.configId);
        if (index >= 0) {
            data.splice(index, 1);
        }
    }
    handleChange = (event) => {
        if(this.state.selectedAccount !== event.target.value && event.target.value !== null) {
            this.setState({
                selectedAccount: event.target.value,
                dataLoaded: false
            }, () => {
            console.log('Handling Change ** ', event.target.value)
            this.props.fetchConfigDefinitionsById(event.target.value, this.props.token)
            });          
        }

    }

    render() {
        return (
            <div>
                {
                    this.props.type === 'accountConfig' ? <div className="combo-config">
                    <p>Account Ids: </p><ComboBox data={this.state.accountList} value={this.state.selectedAccount} onChange={this.handleChange} />
                </div> : null
                }

{
                    this.props.type === 'applicationConfig' ?  
                    this.renderTable() :
                    null
                }                
                {
                    this.state.selectedAccount !== '' && this.props.type === 'accountConfig' ? 
                    this.renderTable() :
                    null
                } 
               
            </div>
        ) 
     }
     loadTableData = () => {

        if(this.props.type === 'applicationConfig' && !this.state.dataLoaded && !this.props.getConfigFetching) {
            this.props.fetchConfigDefinitionList(this.props.token)
        }
        if(this.props.type === 'applicationConfig' && this.props.getConfigSuccess && this.props.getConfigData.length > 0 && !this.state.dataLoaded) {
            let newArray = this.props.getConfigData.map((c, i) => {
                if(!c.configId) {
                    c.configId = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
                }

                if(c.configDataType === 'Boolean') {
                    if(c.configValue === 'False' || c.configValue === 'false') {
                        c.configValue = false
                    } else if(c.configValue === 'True' || c.configValue === 'true') {
                        c.configValue = true
                    }
                }
                if(c.configDataType === 'Int' && c.configValue == null) { c.configValue = 0} 
                c.decrypted = false
                return c
            })
            this.setState({
                data: newArray,
                dataLoaded: true
            }, () => {})
        }
        if(this.props.type === 'accountConfig' && this.props.getConfigByIdSuccess && this.props.getConfigByIdData.length > 0 && !this.state.dataLoaded) {
            let newArray = this.props.getConfigByIdData.map((c, i) => {
                if(!c.configId) {
                    c.configId = Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15);
                }

                if(c.configDataType === 'Boolean') {
                    if(c.configValue === 'False' || c.configValue === 'false') {
                        c.configValue = false
                    } else if(c.configValue === 'True' || c.configValue === 'true') {
                        c.configValue = true
                    }
                }
                if(c.configDataType === 'Int' && c.configValue == null) { c.configValue = 0} 
                c.decrypted = false
                return c
            })
            this.setState({
                data: newArray,
                dataLoaded: true
            }, () => {})
        }
    }
    renderTable = () => {
        let TITLE_NAME_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Name</span>
        let TITLE_VALUE_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Value</span>
        let TITLE_ENCRYPT_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Encrypt API?</span>
        let TITLE_DECRYPT_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Decrypt</span>
        let TITLE_ACTIONS_A = <span style={{ fontSize: '15px', fontWeight: '600' }}>Actions</span>
        let TITLE_DESCRIPTION_A = <span style={{ fontStize: '15px', fontWeight: '600'}}>Description</span>
        const hasEditedItem = this.state.data.some(p => p.inEdit);

         return (
            <div className='clusterField' style={{marginLeft: '0%', marginRight: "0%"}}>
            <div className='k-grid-conf'  >
            <StatefulGrid
                data={this.state.data}
                resizable
                reorderable={true}
                filterable={true}
                onItemChange= {this.itemChange}
                editField="inEdit"
                pageable={true}
                {...this.state}
            >
                <Column width={'240px'} height={'25px'} field="configName"  title={TITLE_NAME_A} cell={(props) => <td><span style={{fontWeight: 600, fontSize: '14px', wordBreak: 'break-all'}}>{props.dataItem.configName}</span></td>} />
                <Column width={'280px'} height={'20px'} field="configDescription"  title={TITLE_DESCRIPTION_A} cell={(props) => <td><span style={{fontSize: '14px'}}>{props.dataItem.configDescription}</span></td>} />
                <Column width={'330px'} height={'25px'} field="configValue" title={TITLE_VALUE_A} cell={(props) => <td className="k-table-config-center">
                    {props.dataItem.isNull ? <span style={{color: 'rgb(224, 224, 224)', fontStyle: 'italic'}}>null</span> : 
                        props.dataItem.configDataType === 'Boolean' && props.dataItem.configValue == true ? <span>true</span> :
                        props.dataItem.configDataType === 'Boolean' && props.dataItem.configValue == false ? <span>false</span> :
                        props.dataItem.isEncrypted && !props.dataItem.decrypted ? <span className="k-grid-null-value" style={{color: 'rgb(224, 224, 224)', fontStyle: 'italic'}}>{props.dataItem.configValue}</span> :
                        <span style={{wordBreak: 'break-all'}}>{props.dataItem.configValue}</span>
                        }
                    </td>} /> 
                <Column width={'116px'} height={'25px'} field="isEncrypted" title={TITLE_ENCRYPT_A} cell={(props) =>  <td>
                    <div className="encrypt-toggle">
                        {this.props.type === 'accountConfig' && <ToggleSlider id={props.dataItem.configId + '_acc'} handleToggleChange={this.handleToggleEncrypt} toggleType='encrypt' toggleOn={props.dataItem.isEncrypted} isDisabled={props.dataItem.isEncryptionRequired || props.dataItem.configDataType === 'String' && props.dataItem.configValue == null || props.dataItem.isNull || props.dataItem.configDataType === 'Int' && props.dataItem.configValue == null ? true : false} /> }
                        {this.props.type === 'applicationConfig' && <ToggleSlider id={props.dataItem.configId + '_app'} handleToggleChange={this.handleToggleEncrypt} toggleType='encrypt' toggleOn={props.dataItem.isEncrypted} isDisabled={props.dataItem.isEncryptionRequired || props.dataItem.configDataType === 'String' && props.dataItem.configValue == null || props.dataItem.configDataType === 'Int' && props.dataItem.configValue == null || props.dataItem.isNull ? true : false} />}
                </div></td>} />
                <Column title={TITLE_ACTIONS_A} cell={this.CommandCell} height={'25px'} width="143px" />
            </StatefulGrid>
            {this.state.objectInEdit && <ConfigDialogContainer dataItem={this.state.objectInEdit} save={this.save} cancel={this.cancel}/>}

        <br/>
            </div>
        </div>
         )
    }
    renderInputString = (dataItem) => {

        if(dataItem.inEdit) {
            return (
                <input type="text" className="k-cell-txtfield" value={dataItem.configValue} onChange={(e) => {this.inputConfigChange(e, dataItem.configId)}} />
            )
        } else if(!dataItem.inEdit) {
            return (
                <p>{dataItem.configValue}</p>
            )
        }

    }
    renderInputToggle = (dataItem) => {
        return (
            <ToggleSlider id={dataItem.configId} handleToggleChange={this.handleToggleConfig} toggleType='apiConfigEncrypt' toggleOn={dataItem.configValue} isDisabled={dataItem.inEdit}  />
        )
    }
    renderInputInteger = (dataItem) => {
        return (
            <input type="number" min="0" className="k-cell-txtfield" value={dataItem.configValue} onChange={(e) => this.inputConfigChange(e, dataItem.configId)} />
        )

    }
    componentDidUpdate = (prevProps) => {
        if (prevProps != this.props) {
            
            if(prevProps.getConfigByIdData != this.props.getConfigByIdData && this.props.type === 'accountConfig') {
                 this.setState({
                     data: this.props.getConfigByIdData,
                     dataLoaded: true
                    })
            }
            if(prevProps.getConfigData != this.props.getConfigData && this.props.type === 'applicationConfig' ) {
                this.setState({
                    data: this.props.getConfigData,
                    dataLoaded: true
                })
           }
           if(this.props.type === 'accountConfig' && this.props.getConfigByIdData && this.props.getConfigByIdSuccess) {
            this.setState({
                data: this.props.getConfigByIdData,
                dataLoaded: true
               })
            }
            if(this.props.type === 'applicationConfig' && this.props.getConfigData && this.props.getConfigSuccess) {
                this.setState({
                    data: this.props.getConfigData,
                    dataLoaded: true
                })
            }
            if(this.props.putConfigByIdSuccess) {
                this.props.clearStatus()
                if(this.props.type === 'applicationConfig') {
                    this.setState({
                        dataLoaded: false
                    })
                    this.props.fetchConfigDefinitionList(this.props.token)
                } else if(this.props.type === 'accountConfig') {
                    this.setState({
                        dataLoaded: false
                    })
                    this.props.fetchConfigDefinitionsById(this.state.selectedAccount, this.props.token)
                    
                }
            }
            if(prevProps.decryptConfigData != this.props.decryptConfigData && this.props.decryptConfigSuccess) {
                if(this.state.awaitUpdate.awaiting) {
                    let newAwait = {awaitObj: this.props.decryptConfigData, awaiting: false}
                    this.setState({
                        data: this.state.data.map((d, i) => {
                            if(d.configName === this.props.decryptConfigData.configName) {
                                d.configValue = this.props.decryptConfigData.configValue
                                d.decrypted = true
                                return d
                            } else {
                                return d
                            }
                        }),
                        awaitUpdate: newAwait
                    })
                } else {
                    if(this.props.type === 'accountConfig') {
                        this.setState({
                            data: this.state.data.map((d, i) => {                               
                                if(d.configName === this.props.decryptConfigData.configName) {
                                    d.configValue = this.props.decryptConfigData.configValue
                                    d.decrypted = true
                                    return d
                                } else {
                                    return d
                                }
                            })
                        })                       
                    } else if(this.props.type === 'applicationConfig') {
                        this.setState({
                            data: this.state.data.map((d, i) => {
                                if(d.configName === this.props.decryptConfigData.configName) {
                                    d.configValue = this.props.decryptConfigData.configValue
                                    d.decrypted = true
                                    return d
                                } else {
                                    return d
                                }
                            })
                        })
                    }

                }

            }
            this.loadTableData()

        }
    }
    
    componentDidMount() {
        this.setState({
            dataLoaded: false,
            data: this.props.type === 'accountConfig' ? this.props.getConfigByIdData : this.props.type === 'applicationConfig' ? this.props.getConfigData : []
        })
        this.loadTableData()
    }

}

const mapStateToProps = state => {
    return {
        adminAddRolesSuccess: state.adminMetadata.adminAddRolesSuccess,
        adminAddRolesError: state.adminMetadata.adminAddRolesError,
        adminAddRolesErrorData: state.adminMetadata.adminAddRolesErrorData,
        adminRemoveRolesSuccess: state.adminMetadata.adminRemoveRolesSuccess,
        adminRemoveRolesError: state.adminMetadata.adminRemoveRolesError,
        adminRemoveRolesErrorData: state.adminMetadata.adminRemoveRolesErrorData,
        getUserRolesSuccess: state.adminMetadata.getUserRolesSuccess,
        getUserRolesData: state.adminMetadata.getUserRolesData,
        getConfigDefinitionsData: state.adminMetadata.getConfigDefinitionsData,
        getConfigDefinitionsSuccess: state.adminMetadata.getConfigDefinitionsSuccess,
        postAccountSetupSuccess: state.adminMetadata.postAccountSetupSuccess,
        postAccountSetupError: state.adminMetadata.postAccountSetupError,
        postAccountSetupErrorMessage: state.adminMetadata.postAccountSetupErrorMessage,
        getConfigFetching: state.adminMetadata.getConfigFetching,
        getConfigByIdSuccess: state.adminMetadata.getConfigByIdSuccess,
        getConfigByIdData: state.adminMetadata.getConfigByIdData,
        getConfigSuccess: state.adminMetadata.getConfigSuccess,
        getConfigData: state.adminMetadata.getConfigData,
        putConfigByIdSuccess: state.adminMetadata.putConfigByIdSuccess,
        putConfigByIdData: state.adminMetadata.putConfigByIdData,           
        decryptConfigSuccess: state.adminMetadata.decryptConfigSuccess,
        decryptConfigData: state.adminMetadata.decryptConfigData
         
    }
  }

const mapDispatchToProps = dispatch => {
    return {
        postAddRoles: (data, token) => dispatch(postAddRoles(data, token)),
        postRemoveRoles: (data, token) => dispatch(postRemoveRoles(data, token)),
        fetchUserRoles: (email, token) => dispatch(fetchUserRoles(email, token)),
        fetchUIDropdownList: (token) => dispatch(fetchUIDropdownList(token)),
        postAccountSetup: (data, token) => dispatch(postAccountSetup(data, token)),
        fetchConfigDefinitions: (token) => dispatch(fetchConfigDefinitions(token)),
        fetchConfigDefinitionList: (token) => dispatch(fetchConfigDefinitionList(token)),
        fetchConfigDefinitionsById: (id, token) => dispatch(fetchConfigDefinitionsById(id, token)),
        putConfigDefinitions: (id, data, token) => dispatch(putConfigDefinitions(id, data, token)),
        decryptConfigByName: (name, accId, token) => dispatch(decryptConfigByName(name, accId, token)),
        clearStatus: () => dispatch(clearStatus())
    }
  }

export default connect(mapStateToProps, mapDispatchToProps)(ConfigManagement)