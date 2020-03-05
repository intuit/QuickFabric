import React from 'react';
import Toggle from 'react-toggle';
import LockIcon from '@material-ui/icons/Lock';
import NoEncryptionIcon from '@material-ui/icons/NoEncryption';
import LockOpenIcon from '@material-ui/icons/LockOpen';
import '../styles/toggle.scss';

{/* <ToggleSlider id={string} toggleType={string} handleToggleChange={funciton} toggleOn={boolean} /> */}

export const ToggleSlider = ({id, toggleType, handleToggleChange, toggleOn, isDisabled}) => {
    if(typeof toggleOn === 'String') {
        if(toggleOn === 'False' || toggleOn === 'false') {
            toggleOn = false
        } else if(toggleOn === 'True' || toggleOn === 'true') {
            toggleOn = true
        }
    }
    let randomNumID = id !== '' ? id :  Math.random().toString(36).substring(2, 15) + Math.random().toString(36).substring(2, 15)
    return (
        <div>
            { isDisabled && toggleType !== 'encrypt' ? 
                    <div>
                        <input
                            className="react-switch-checkbox"
                            id={randomNumID}
                            type="checkbox"
                            checked={toggleOn}
                        />
                        <label
                            style={{ background: "rgb(234, 236, 232)"}}
                            className="react-switch-label"
                            htmlFor={randomNumID}
                        >
                        <span className={`react-switch-button`} />
                        {toggleOn ? <p className="disable-toggle toggle-on">on</p> : <p className="disable-toggle toggle-off">off</p>}
                        </label> </div> : 
                    isDisabled && toggleType === 'encrypt' ? 
                    <div>
                        <input
                            className="react-switch-checkbox"
                            id={randomNumID}
                            type="checkbox"
                            checked={toggleOn}
                        />
                        <label
                            style={{ background: "rgb(234, 236, 232)",  borderRadius: '5px'}}
                            className="react-switch-label"
                            htmlFor={randomNumID}
                        ><span className={`react-switch-button`} style={{ borderRadius: '5px'}} />
                        {toggleOn ? <p className="disable-toggle toggle-on"><LockIcon width={'15px'} height={'15px'}/></p> : <p className="disable-toggle toggle-off"><NoEncryptionIcon width={'15px'} height={'15px'}/></p>}
                        </label> 
                    </div>                    
                    :
                toggleType === 'encrypt' && !isDisabled ?
                <div>          
                <input
                    className="react-switch-checkbox"
                    id={randomNumID}
                    type="checkbox"
                    onChange={() => handleToggleChange(id, toggleOn) }
                    checked={toggleOn}
                />
                <label
                    className={toggleOn ? "react-switch-label on-label" : "react-switch-label off-label"}
                    htmlFor={randomNumID}
                ><span className={`react-switch-button`} style={{ borderRadius: '5px'}} />
                {/* <span className="k-icon k-i-unlock k-icon-32"></span> */}
                {toggleOn ? <p className="toggle-on"><LockIcon width={'15px'} height={'15px'}/></p> : <p className="toggle-off"><NoEncryptionIcon width={'15px'} height={'15px'}/></p>}
                </label>  
                </div>
                 :
                    <div>          
                    <input
                        className="react-switch-checkbox"
                        id={randomNumID}
                        type="checkbox"
                        onChange={() => handleToggleChange(id, toggleOn) }
                        checked={toggleOn}
                    />

                    <label
                    style={{ background: toggleOn ? '#53b700' : '#ff0100' }}
                    className="react-switch-label"
                    htmlFor={randomNumID}
                ><span className={`react-switch-button`} />
                {toggleOn ? <p className="toggle-on">on</p> : <p className="toggle-off">off</p>}
                </label>  
                </div>
            }

 
        </div>
      )
}