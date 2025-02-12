import React, { useState, useEffect, useCallback } from 'react'
import InfoModal from '../../../InfoModal/InfoModal'
import LoadingSpinner from '../../../Loading/LoadingSpinner';
import fetchWithRedirect from '../../../fetchUtil';
import './AttendanceStatistics.css'

const AttendanceStatistics = ({ userId, code, sessionName, week, isOpen, onClose, nrStudents }) => {
    const [attendanceStats, setAttendanceStats] = useState();
    const [isLoading, setIsLoading] = useState(true);

    const fetchAttendanceStatistics = useCallback(async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/attendance/statistics/${userId}/${code}/${sessionName}/${week}`,
            );
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            setAttendanceStats(data);
            setIsLoading(false);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
        }
    }, [code, sessionName, userId, week]);

    useEffect(() => {
        fetchAttendanceStatistics();
    }, [fetchAttendanceStatistics]);

    if (isLoading) {
        return <LoadingSpinner />;
    }
    return (
        <>
            <InfoModal
                isOpen={isOpen}
                onClose={onClose}
            >
                <div className="attendance-stats">
                    <h2>Recorded attendance:</h2>
                    <p>Total: {attendanceStats.total || 0} out of  {nrStudents}</p>
                    <p className="present">Present: {attendanceStats.present || 0} </p>
                    <p className="absent">Absent: {attendanceStats.absent || 0}</p>
                    <p className="authorized">Authorised: {attendanceStats.authorized || 0}</p>
                </div>
            </InfoModal>
        </>
    )
}

export default AttendanceStatistics