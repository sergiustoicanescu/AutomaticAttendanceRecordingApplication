import React from 'react'
import './InfoModal.css'

const InfoModal = ({ isOpen, onClose, children }) => {
    if (!isOpen) return null;

    const handleOverlayClick = (e) => {
        onClose();
    };

    const handleContentClick = (e) => {
        e.stopPropagation();
    };

    return (
        <div className="info-modal-overlay" onClick={handleOverlayClick}>
            <div className="info-modal-content" onClick={handleContentClick}>
                {children}
                <button className="info-modal-close" onClick={onClose}>&times;</button>
            </div>
        </div>
    );
};

export default InfoModal;