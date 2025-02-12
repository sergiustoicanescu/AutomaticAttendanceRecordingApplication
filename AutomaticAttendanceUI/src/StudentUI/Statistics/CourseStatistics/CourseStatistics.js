import React, { useState, useCallback, useEffect } from 'react'
import './CourseStatistics.css'
import fetchWithRedirect from '../../../fetchUtil';

const CourseStatistics = ({ user, course }) => {
    const [statistics, setStatistics] = useState([]);
    const [isLoading, setIsLoading] = useState(true);

    const fetchAttendances = useCallback(async () => {
        try {
            const response = await fetchWithRedirect(
                `/student/statistics/attendances/${user.id}/${course.code}`,
            );
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText);
            }

            const data = await response.json();
            setStatistics(data);
            setIsLoading(false);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
            setIsLoading(true);
        }
    }, [user, course.code]);

    useEffect(() => {
        fetchAttendances();
    }, [fetchAttendances]);

    return (
        <>
            <div className='stats-course-block'>
                {isLoading ? (
                    <div className='stats-spinner-container'>
                        <div className='stats-spinner' />
                    </div>
                ) : (
                    <>
                        {
                            statistics.map((stat, index) => (
                                <div key={index} className='stats-course-details'>
                                    <span>{stat.type}</span>
                                    <span style={{ color: "green" }}>{stat.present}</span>
                                </div>
                            ))
                        }
                    </>
                )}

            </div>
        </>
    )
}

export default CourseStatistics