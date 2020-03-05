import React from 'react';

const ToggleText = props => (
    <span className={`toggle-text ${props.label == props.keyword ? props.text : ''}`}>{props.label}</span>
);

export default ToggleText;