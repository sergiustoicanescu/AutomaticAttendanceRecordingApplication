import React, { useState } from 'react';
import './AddSessionModal.css';
import Modal from '../../../Modal/Modal';
import fetchWithRedirect from '../../../fetchUtil';
import DatePicker from 'react-datepicker';
import 'react-datepicker/dist/react-datepicker.css';
import { registerLocale } from "react-datepicker";
import enGB from 'date-fns/locale/en-GB';
import { parse, isWithinInterval, isValid } from 'date-fns';



const AddSessionModal = ({ userId, code, isOpen, onClose, weeks, handleSessions }) => {
    const [session, setSession] = useState({
        name: '',
        type: '',
        startDate: '',
        startTime: '',
        endTime: '',
        frequency: '1',
    });
    const [isLoading, setIsLoading] = useState(false);
    const [tooltipVisible, setTooltipVisible] = useState(false);
    const [tooltipMessage, setTooltipMessage] = useState('');
    registerLocale('dateType', enGB);

    const handleInputChange = (e) => {
        if (tooltipVisible) {
            setTooltipVisible(false)
            setTooltipMessage('')
        }
        const { name, value } = e.target;
        setSession((prevSession) => ({
            ...prevSession,
            [name]: value,
        }));
    };

    const isDateWithinWeeks = (date) => {
        return weeks.some(week => {
            const start = parse(week.firstDay, 'yyyy-MM-dd', new Date());
            const end = parse(week.lastDay, 'yyyy-MM-dd', new Date());

            if (!isValid(start) || !isValid(end)) {
                console.error('One of the interval dates is invalid:', week);
                return false;
            }

            if (start > end) {
                console.error('Start date comes after end date:', week);
                return false;
            }

            return isWithinInterval(date, { start, end });
        });
    };


    const addSession = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/session/addSession/${userId}/${code}`,
                {
                    method: 'POST',
                    body: session
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
            setTooltipMessage(error.message);
            setTooltipVisible(true);
            throw error;
        }
    };

    const handleAddSession = async (e) => {
        e.preventDefault()
        setTooltipMessage('');
        setTooltipVisible(false);
        setIsLoading(true);
        try {
            await addSession();
            setTimeout(() => {
                setIsLoading(false);
                onClose()
                handleSessions(session.name, session.type, session.frequency)
                setSession({ frequency: 1 })
            }, 500);
        } catch (error) {
            setIsLoading(false);
        }
    };

    const handleOnClose = () => {
        setTooltipMessage('');
        setTooltipVisible(false);
        setSession({ frequency: 1 })
        onClose();
    }

    return (
        <>
            {isOpen && <Modal
                isOpen={isOpen}
                onClose={handleOnClose}
                onConfirm={handleAddSession}
                title='Add session'
                confirmText={isLoading ? 'Saving...' : 'Save'}
                cancelText="Cancel"
                isLoading={isLoading}
                tooltipMessage={tooltipMessage}
                tooltipVisible={tooltipVisible}>
                <div className="add-session-details">
                    <input
                        type="text"
                        name="name"
                        placeholder="Session Name"
                        value={session.name}
                        onChange={handleInputChange}
                        required
                    />
                    <select
                        name="type"
                        value={session.type}
                        onChange={handleInputChange}
                        required
                    >
                        <option value="">Select Type</option>
                        <option value="Lecture">Lecture</option>
                        <option value="Lab">Lab</option>
                        <option value="Seminar">Seminar</option>
                    </select>
                    <DatePicker
                        title='Start date'
                        className='custom-datepicker'
                        dateFormat="dd/MM/yyyy"
                        selected={session.startDate ? new Date(session.startDate) : null}
                        onChange={date => setSession(prevSession => ({ ...prevSession, startDate: date.toLocaleDateString('en-CA') }))}
                        filterDate={isDateWithinWeeks}
                        locale="dateType"
                        required
                    />
                    <input
                        title='Start time'
                        type="time"
                        name="startTime"
                        value={session.startTime}
                        onChange={handleInputChange}
                        required
                    />
                    <input
                        title='End time'
                        type="time"
                        name="endTime"
                        value={session.endTime}
                        onChange={handleInputChange}
                        required
                    />
                    <select
                        name="frequency"
                        value={session.frequency}
                        onChange={handleInputChange}
                        required
                    >
                        <option value="">Select Frequency</option>
                        <option value="1">Every week</option>
                        <option value="2">Every two weeks</option>
                    </select>
                </div>
            </Modal>}

        </>
    );
};

export default AddSessionModal;
