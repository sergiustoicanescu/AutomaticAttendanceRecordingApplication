import React, { useState, useEffect } from 'react'
import { MdOutlineDeleteOutline, MdOutlineEdit } from "react-icons/md";
import fetchWithRedirect from '../../fetchUtil';
import Modal from '../../Modal/Modal';
import Tooltip from '../ToolTip/Tooltip';

const SessionEdit = ({ userId, code, index, session, isDeletable, handleSessions, handleEditConfirm }) => {
    const [eSession, setESession] = useState({});
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [showEditModal, setShowEditModal] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [tooltipVisible, setTooltipVisible] = useState(false);
    const [tooltipMessage, setTooltipMessage] = useState('');
    const [tooltipEditVisible, setTooltipEditVisible] = useState(false);
    const [tooltipEditMessage, setTooltipEditMessage] = useState('');

    const nameStyle = eSession.name !== session.name ? { border: '1px solid blue' } : {};
    const typeStyle = eSession.type !== session.type ? { border: '1px solid blue' } : {};
    const frequencyStyle = String(eSession.frequency) !== String(session.frequency) ? { border: '1px solid blue' } : {};

    useEffect(() => {
        let timer;
        if (tooltipVisible) {
            timer = setTimeout(() => {
                setTooltipVisible(false);
            }, 2000);
        }
        return () => clearTimeout(timer);
    }, [tooltipVisible]);


    const deleteSession = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/session/deleteSession/${userId}/${code}/${session.name}`,
                {
                    method: 'DELETE',
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Network response was not ok');
            }
        } catch (error) {
            console.error('Error:', error);
            alert(error.message)
            throw error;
        }
    };

    const handleDeleteSession = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await deleteSession();
            setTimeout(() => {
                handleSessions(session.name)
                setIsLoading(false);
            }, 500);
        } catch (error) {
            setIsLoading(false);
        }
    };

    const handleInputChange = (e) => {
        if (tooltipEditVisible) {
            setTooltipEditVisible(false)
            setTooltipEditMessage('')
        }
        const { name, value } = e.target;
        setESession((prevSession) => ({
            ...prevSession,
            [name]: value,
        }));
    };

    const editSession = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/session/editSessionDetails/${userId}/${code}/${session.name}`,
                {
                    method: 'PATCH',
                    body: eSession
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Network response was not ok');
            }
        } catch (error) {
            console.error('Error:', error);
            setTooltipEditMessage(error.message);
            setTooltipEditVisible(true);
            throw error;
        }
    };

    const handleEditSession = async (e) => {
        e.preventDefault();
        setTooltipEditMessage('');
        setTooltipEditVisible(false);
        setIsLoading(true);
        try {
            await editSession();
            setTimeout(() => {
                handleEditConfirm(session, eSession)
                setIsLoading(false);
                setShowEditModal(false)
            }, 500);
        } catch (error) {
            setIsLoading(false);
        }
    };

    const handleEditSessionComponent = () => {
        setESession(session);
        setShowEditModal(true)
    }

    const checkSessionDeletion = () => {
        if (!isDeletable) {
            setTooltipMessage("Cannot delete the last session!");
            setTooltipVisible(true);
            return;
        }
        setTooltipVisible(false);
        setShowDeleteModal(true);
    }
    return (
        <>
            <div key={index} className='edit-session-details'>
                {session.name}
                <div className="session-actions">
                    <button title='Delete session' onClick={() => checkSessionDeletion()} className="session-edit-cancel">
                        <MdOutlineDeleteOutline size={16} />
                    </button>
                    <button title='Edit session' onClick={() => handleEditSessionComponent()} className="edit-session">
                        <MdOutlineEdit size={16} />
                    </button>
                </div>
                {tooltipVisible && (
                    <Tooltip message={tooltipMessage} />
                )}
            </div>
            {showDeleteModal && (<Modal
                isOpen={showDeleteModal}
                onClose={() => setShowDeleteModal(false)}
                onConfirm={handleDeleteSession}
                title='Delete session'
                confirmText={isLoading ? 'Deleting...' : 'Delete'}
                cancelText="Cancel"
                isLoading={isLoading}>

                <p>Are you sure you want to delete the session <strong>{session.name}</strong>?</p>
            </Modal>)}

            {showEditModal && (<Modal
                isOpen={showEditModal}
                onClose={() => {
                    setTooltipEditMessage('');
                    setTooltipEditVisible(false);
                    setShowEditModal(false)
                }}
                onConfirm={handleEditSession}
                title='Edit session'
                confirmText={isLoading ? 'Updating...' : 'Update'}
                cancelText="Cancel"
                isLoading={isLoading}
                tooltipMessage={tooltipEditMessage}
                tooltipVisible={tooltipEditVisible}>
                <div className="add-session-details">
                    <input
                        type="text"
                        name="name"
                        placeholder="Session Name"
                        value={eSession.name}
                        onChange={handleInputChange}
                        required
                        style={nameStyle}
                    />

                    <select
                        name="type"
                        value={eSession.type}
                        onChange={handleInputChange}
                        required
                        style={typeStyle}
                    >
                        <option value="">Select Type</option>
                        <option value="Lecture">Lecture</option>
                        <option value="Lab">Lab</option>
                        <option value="Seminar">Seminar</option>
                    </select>

                    <select
                        name="frequency"
                        value={eSession.frequency}
                        onChange={handleInputChange}
                        required
                        style={frequencyStyle}
                    >
                        <option value="">Select Frequency</option>
                        <option value="1">Every week</option>
                        <option value="2">Every two weeks</option>
                    </select>
                </div>
            </Modal>)}
        </>
    )
}

export default SessionEdit