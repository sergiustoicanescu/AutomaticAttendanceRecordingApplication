import React from 'react';
import './SuccessMessage.css';

const SuccessMessage = ({ message }) => {
    return (
        <div className="success-message">{message}</div>
    );
};

export default SuccessMessage;
