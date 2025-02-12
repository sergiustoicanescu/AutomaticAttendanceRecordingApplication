import React from 'react';

const DateInputWithinWeek = ({ editableDate, setEditableDate, className }) => {
    const currentDate = new Date(editableDate);
    const firstDayOfWeek = new Date(currentDate.setDate(currentDate.getDate() - currentDate.getDay() + (currentDate.getDay() === 0 ? -6 : 1))).toISOString().split('T')[0];
    const lastDayOfWeek = new Date(currentDate.setDate(currentDate.getDate() + 6)).toISOString().split('T')[0];

    const handleChange = (e) => {
        const value = e.target.value;
        setEditableDate(value || firstDayOfWeek);
    };

    return (
        <input
            type="date"
            className={className || ''}
            value={editableDate}
            onChange={handleChange}
            min={firstDayOfWeek}
            max={lastDayOfWeek}
            onKeyDown={(e) => e.preventDefault()}
        />
    );
};

export default DateInputWithinWeek;
