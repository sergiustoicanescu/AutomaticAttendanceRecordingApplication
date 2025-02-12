import React from 'react';
import './Tooltip.css';

const Tooltip = ({ message, position = "top" }) => {
    return (
        <div className={`error-tooltip ${position}`}>{message}</div>
    );
};

export default Tooltip;
