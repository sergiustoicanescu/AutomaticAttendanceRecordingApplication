import React, { useState, useCallback, useEffect } from 'react'
import './Statistics.css'
import MessagePage from '../../MessagePage/MessagePage'
import SubHeader from '../../ProfessorUI/SubHeader/SubHeader'
import LoadingSpinner from '../../Loading/LoadingSpinner'
import fetchWithRedirect from '../../fetchUtil'
import CourseStatistics from './CourseStatistics/CourseStatistics'

const Statistics = ({ user }) => {

    const [courses, setCourses] = useState([]);
    const [isLoading, setIsLoading] = useState(true);
    const [isError, setIsError] = useState(false);
    const [openCourse, setOpenCourse] = useState(null);

    const fetchCoursesForStatistics = useCallback(async () => {
        try {
            const response = await fetchWithRedirect(
                `/student/statistics/courses/${user.id}`,
            );
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            setCourses(data);
            setIsLoading(false);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
            setIsLoading(false);
            setIsError(true);
        }
    }, [user]);

    useEffect(() => {
        fetchCoursesForStatistics();
    }, [fetchCoursesForStatistics]);

    const toggleWeek = (code) => {
        setOpenCourse(currentCode => currentCode === code ? null : code);
    };

    useEffect(() => {
        if (openCourse) {
            const openCourseElement = document.getElementById(`week-${openCourse}`);
            if (openCourseElement) {
                openCourseElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }
    }, [openCourse]);

    if (isLoading) {
        return <LoadingSpinner />
    }

    if (isError) {
        return <MessagePage
            title={"Failed to Fetch Statistics"}
            titleColor={"#D32F2F"}
            message={"There was a problem fetching the statistics. Please try again later."}
            role={"student"}
        />
    }

    if (courses.length === 0) {
        return (
            <MessagePage
                title={"No Courses Found"}
                titleColor={"#D32F2F"}
                message={"You are not currently enrolled in any courses!"}
                role={"student"}
            />
        );
    }

    return (
        <>
            <SubHeader title={"Statistics"} />
            <main className='course-page'>
                {courses.map((course) => (
                    <div key={course.code} id={`course-${course.code}`} className="week-block">
                        <button
                            className={`week-header ${openCourse === course.code ? 'opened' : ''}`}
                            onClick={() => toggleWeek(course.code)}
                        >
                            {course.name}
                        </button>
                        {openCourse === course.code && (
                            <>
                                <CourseStatistics user={user} course={course} />
                            </>
                        )}
                    </div>
                ))}
            </main>

        </>

    )
}

export default Statistics