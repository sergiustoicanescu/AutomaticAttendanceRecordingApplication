import React, { useEffect, useState } from 'react';
import SockJS from 'sockjs-client';
import { Client } from '@stomp/stompjs';
import { MdOutlineEdit } from "react-icons/md";
import { IoQrCode } from "react-icons/io5";
import { AiOutlineStop } from "react-icons/ai";
import { FiXCircle } from "react-icons/fi";
import { FiCheck } from "react-icons/fi";
import QRModal from '../QRModal/QRModal';
import { SERVER_URL } from '../../../constant';
import DateInputWithinWeek from '../DateInputWithinWeek';
import './Session.css'
import fetchWithRedirect from '../../../fetchUtil';
import Tooltip from '../../ToolTip/Tooltip';
import { Link } from 'react-router-dom';

const Session = ({ course, session, setSessions }) => {
    const [isQRModalOpen, setIsQRModalOpen] = useState(false);
    const [isActive, setIsActive] = useState(session.active);
    const [isEditMode, setIsEditMode] = useState(false);
    const [editableDate, setEditableDate] = useState(session.startDate);
    const [editableStartTime, setEditableStartTime] = useState(session.startTime);
    const [editableEndTime, setEditableEndTime] = useState(session.endTime);
    const [tooltipVisible, setTooltipVisible] = useState(false);
    const [tooltipMessage, setTooltipMessage] = useState('');

    useEffect(() => {
        if (session.active) {
            const client = new Client({
                webSocketFactory: () => new SockJS(SERVER_URL + '/ws'),
                onConnect: () => {
                    client.subscribe(`/topic/course/${course.id}/sessionStatus`, (message) => {
                        const statusSession = JSON.parse(message.body);
                        if (session.id === statusSession.id) {
                            setIsActive(statusSession.active);
                            setSessions(prevSessions => prevSessions.map(s => s.id === session.id ? { ...s, active: statusSession.active, code: null } : s));
                        }
                    });
                    client.publish({
                        destination: `/app/session/status/${session.id}`,
                        body: '{}',
                    });
                },
            });

            client.activate();

            return () => client.deactivate();
        }
    }, [session.id, session.active, setSessions, course.id]);

    const handleActivateSession = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/session/activate/${course.userId}/${session.id}`,
                {
                    method: 'PATCH',
                    body: {
                        startDate: session.startDate,
                        startTime: session.startTime,
                        endTime: session.endTime,
                        active: session.active,
                        code: session.code,
                        courseId: course.id
                    }
                });

            if (!response) {
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Network response was not ok');
            }
            const data = await response.json();
            if (data.active) {
                setIsActive(true);
                setSessions(prevSessions => prevSessions.map(s => s.id === session.id ? { ...s, active: true, code: data.code } : s));
            }
            setIsQRModalOpen(true);
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    };

    const handleDeactivateSession = async () => {
        try {
            const response = await fetchWithRedirect(`/professor/session/deactivate/${course.userId}/${session.id}`, {
                method: 'PATCH',
                headers: { 'Content-Type': 'application/json' },
            });
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Failed to deactivate session');
            }

            setIsActive(false);
            setSessions(prevSessions => prevSessions.map(s => s.id === session.id ? { ...s, active: false, code: null } : s));
        } catch (error) {
            console.error('Error:', error);
        }
    };

    const handleEditSession = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/session/edit/${course.userId}/${course.code}/${session.id}`,
                {
                    method: 'PATCH',
                    body: {
                        startDate: editableDate,
                        startTime: editableStartTime,
                        endTime: editableEndTime,
                        week: session.week,
                        courseId: course.id
                    }
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errMsg = await response.text();
                throw new Error(errMsg);
            } else {
                setSessions(prevSessions => prevSessions.map(s => s.id === session.id ? { ...s, startDate: editableDate, startTime: editableStartTime, endTime: editableEndTime } : s));
                setIsEditMode(false);
            }
        } catch (error) {
            console.error('Error:', error);
            setTooltipMessage(error.message);
            setTooltipVisible(true);
        }
    };

    const handleEdit = () => {
        setIsEditMode(true);
    };

    const handleCancelEdit = () => {
        setTooltipVisible(false);
        setIsEditMode(false);
        setEditableDate(session.startDate);
        setEditableStartTime(session.startTime);
        setEditableEndTime(session.endTime);
    };

    const handleConfirmEdit = () => {
        setTooltipVisible(false);
        handleEditSession();
    };

    return (
        <div className={`session ${isActive ? 'active' : ''}`}>
            <div className={`session-details ${isEditMode ? 'edit' : ''}`}>
                <Link to={`session?name=${encodeURIComponent(session.name)}&week=${session.week}`}>
                    <div className="session-name">{session.name}</div>
                </Link>
                <div className="session-type">{session.type}</div>
                {isEditMode ? (
                    <>
                        <DateInputWithinWeek editableDate={editableDate} setEditableDate={setEditableDate} className={'session-input-date'} />
                        <input className='session-input' type="time" value={editableStartTime} onChange={(e) => setEditableStartTime(e.target.value)} />
                        <input className='session-input' type="time" value={editableEndTime} onChange={(e) => setEditableEndTime(e.target.value)} />
                    </>
                ) : (
                    <>
                        <div className="session-date">{session.startDate}</div>
                        <div className="session-time">{session.startTime.substring(0, 5)} - {session.endTime.substring(0, 5)}</div>
                    </>
                )}
            </div>
            <div className="session-actions">
                {isEditMode ? (
                    <>
                        <button title='Cancel edit' className="session-edit-cancel" onClick={handleCancelEdit}>
                            <FiXCircle size={16} />
                        </button>
                        <button title='Confirm edit' className="session-edit-confirm" onClick={handleConfirmEdit}>
                            <FiCheck size={16} />
                        </button>
                    </>
                ) : (
                    <>
                        {isActive && (
                            <button title='Deactivate session' className="session-stop" onClick={handleDeactivateSession}>
                                <AiOutlineStop size={16} />
                            </button>
                        )}
                        <button title='Show QR code' className="session-qr" onClick={handleActivateSession}>
                            <IoQrCode size={16} />
                        </button>
                        {!isActive && (
                            <button title='Edit session' className="edit-session" onClick={handleEdit}>
                                <MdOutlineEdit size={16} />
                            </button>
                        )}
                    </>
                )}
            </div>
            {isQRModalOpen && (<QRModal
                isOpen={isQRModalOpen}
                onClose={() => setIsQRModalOpen(false)}
                course={course}
                session={session} />
            )}
            {tooltipVisible && (
                <Tooltip message={tooltipMessage} />
            )}
        </div>
    );
}

export default Session;
