import React, { useState } from 'react'
import fetchWithRedirect from '../../../fetchUtil';
import Modal from '../../../Modal/Modal';
import './EditWeeksModal.css';
import { IoIosArrowUp, IoIosArrowDown } from "react-icons/io";

const EditWeeksModal = ({ userId, code, weeks, setWeeks, isOpen, onClose }) => {
    const [isSubmitLoading, setIsSubmitLoading] = useState(false);
    const [tooltipVisible, setTooltipVisible] = useState(false);
    const [tooltipMessage, setTooltipMessage] = useState('');
    const [initialWeeks] = useState(weeks);

    const adjustWeek = (index, direction) => {
        let newWeeks = weeks.map(week => ({
            ...week,
            firstDay: new Date(week.firstDay),
            lastDay: new Date(week.lastDay),
        }));

        const daysToAdjust = direction === 'up' ? 7 : -7;
        newWeeks[index].firstDay.setDate(newWeeks[index].firstDay.getDate() + daysToAdjust);
        newWeeks[index].lastDay.setDate(newWeeks[index].lastDay.getDate() + daysToAdjust);
        newWeeks[index].difference += direction === 'up' ? 1 : -1;

        if (direction === 'up') {
            for (let i = index + 1; i < newWeeks.length; i++) {
                if (newWeeks[i].firstDay <= newWeeks[i - 1].lastDay) {
                    newWeeks[i].firstDay.setDate(newWeeks[i].firstDay.getDate() + daysToAdjust);
                    newWeeks[i].lastDay.setDate(newWeeks[i].lastDay.getDate() + daysToAdjust);
                    newWeeks[i].difference += direction === 'up' ? 1 : -1;
                }
            }
        } else {
            for (let i = index - 1; i >= 0; i--) {
                if (newWeeks[i].lastDay >= newWeeks[i + 1].firstDay) {
                    newWeeks[i].firstDay.setDate(newWeeks[i].firstDay.getDate() + daysToAdjust);
                    newWeeks[i].lastDay.setDate(newWeeks[i].lastDay.getDate() + daysToAdjust);
                    newWeeks[i].difference += direction === 'up' ? 1 : -1;
                }
            }
        }

        setWeeks(newWeeks.map(week => ({
            ...week,
            firstDay: week.firstDay.toLocaleDateString('en-CA'),
            lastDay: week.lastDay.toLocaleDateString('en-CA'),
        })));
    };

    const editWeeks = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/session/editWeeks/${userId}/${code}`,
                {
                    method: 'PATCH',
                    body: weeks
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
            setTooltipMessage("An unexpected error occured please try again later.");
            setTooltipVisible(true);
            throw error;
        }
    };

    const handleEditWeeks = async (e) => {
        e.preventDefault()
        setTooltipMessage('');
        setTooltipVisible(false);
        setIsSubmitLoading(true);
        try {
            await editWeeks();
            setTimeout(() => {
                setWeeks(prevWeeks => prevWeeks.map(week => ({ ...week, difference: 0 })));
                setIsSubmitLoading(false);
                onClose()
            }, 500);
        } catch (error) {
            setIsSubmitLoading(false);
        }
    };

    const handleOnClose = () => {
        setTooltipMessage('');
        setTooltipVisible(false);
        setWeeks(initialWeeks);
        onClose();
    }

    return (
        <>
            {isOpen && (<Modal
                isOpen={isOpen}
                onClose={handleOnClose}
                onConfirm={handleEditWeeks}
                title='Edit Weeks'
                confirmText={isSubmitLoading ? 'Saving...' : 'Save'}
                cancelText="Cancel"
                isLoading={isSubmitLoading}
                tooltipMessage={tooltipMessage}
                tooltipVisible={tooltipVisible}
            >
                <div className="weeks-list">
                    {weeks.map((week, index) => (
                        <div key={index} className={`edit-weeks-details ${week.difference !== 0 ? 'changed-week' : ''}`}>
                            <span>Week {week.weekNumber}</span>
                            <span>{week.firstDay} - {week.lastDay}</span>
                            <div>
                                <button type='button' className='session-qr' onClick={() => adjustWeek(index, 'down')}><IoIosArrowUp /></button>
                                <button type='button' className='session-qr' onClick={() => adjustWeek(index, 'up')}><IoIosArrowDown /></button>
                            </div>
                        </div>
                    ))}
                </div>
            </Modal>)}

        </>
    )
}

export default EditWeeksModal