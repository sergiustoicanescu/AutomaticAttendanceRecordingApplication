import React from 'react';
import './Modal.css'
import Tooltip from '../ProfessorUI/ToolTip/Tooltip';

const Modal = ({ isOpen, onClose, onConfirm, title, children, confirmText, cancelText, isLoading, tooltipVisible, tooltipMessage }) => {
    if (!isOpen) return null;

    return (
        <div className="modal-overlay">
            <div className="modal-content">
                <h2 className='modal-title'>{title}</h2>
                <form className='modal-form' onSubmit={onConfirm}>
                    {tooltipVisible && (
                        <Tooltip message={tooltipMessage} />
                    )}
                    <div className="modal-body">{children}</div>
                    <div className="modal-actions">
                        <button type='cancel' onClick={onClose} className="modal-cancel-button" disabled={isLoading}>
                            {cancelText || 'Cancel'}
                        </button>
                        <button type="submit" className="modal-confirm-button" disabled={isLoading}>
                            {isLoading ? (confirmText) : confirmText || 'Confirm'}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
};

export default Modal;
