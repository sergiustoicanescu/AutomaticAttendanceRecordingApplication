import React from 'react';
import { Link } from 'react-router-dom';
import './MessagePage.css';

const MessagePage = ({ title, titleColor, message, role }) => {
    return (
        <div className="message-page-container">
            <h2 style={{ color: titleColor }}>{title}</h2>
            <p>{message}</p>
            <Link to={`/${role}`} className="back-to-dashboard">Back to Home</Link>
        </div>
    );
}

export default MessagePage;
