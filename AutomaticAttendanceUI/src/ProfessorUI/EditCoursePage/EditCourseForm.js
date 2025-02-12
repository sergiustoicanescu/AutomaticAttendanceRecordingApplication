import React, { useState, useEffect } from 'react';
import fetchWithRedirect from '../../fetchUtil';

const EditCourseForm = ({ user, course, setCourse, setCourses, setWeeks }) => {
    const [courseName, setCourseName] = useState('');
    const [numberOfWeeks, setNumberOfWeeks] = useState(0);
    const [initialCourseName, setInitialCourseName] = useState('');
    const [initialNumberOfWeeks, setInitialNumberOfWeeks] = useState(0);
    const [disableButton, setDisableButton] = useState(false);

    const courseNameStyle = courseName !== initialCourseName ? { border: '1px solid blue' } : {};
    const numberOfWeeksStyle = numberOfWeeks !== initialNumberOfWeeks ? { border: '1px solid blue' } : {};

    const updateCourse = async (e) => {
        setDisableButton(true);
        try {
            const response = await fetchWithRedirect(`/professor/updateCourse/details/${user.id}/${course.code}`, {
                method: 'PATCH',
                body: {
                    name: courseName,
                    numberOfWeeks: numberOfWeeks
                }
            });
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setWeeks(data);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
        }
    };

    const handleUpdateCourse = async (e) => {
        e.preventDefault()
        setDisableButton(true);
        try {
            await updateCourse();
            setTimeout(() => {
                const courseId = course.id;
                setCourses(prevCourses => prevCourses.map(course =>
                    course.id === courseId ? { ...course, name: courseName, numberOfWeeks: numberOfWeeks } : course
                ));
                setCourse({ ...course, name: courseName, numberOfWeeks: numberOfWeeks })
                setDisableButton(false);
            }, 500);
        } catch (error) {
            setDisableButton(false);
        }
    };



    useEffect(() => {
        setInitialCourseName(course.name);
        setInitialNumberOfWeeks(course.numberOfWeeks);
        setCourseName(course.name);
        setNumberOfWeeks(course.numberOfWeeks);
    }, [course]);

    return (
        <form className='edit-course-form' onSubmit={handleUpdateCourse}>
            <label htmlFor="courseName">Course Name:</label>
            <input
                id="courseName"
                value={courseName}
                onChange={(e) => setCourseName(e.target.value)}
                required
                style={courseNameStyle}
            />
            <label htmlFor="numberOfWeeks">Number of Weeks:</label>
            <input
                id="numberOfWeeks"
                type="number"
                value={numberOfWeeks}
                onChange={(e) => {
                    const value = parseInt(e.target.value, 10);
                    const clampedValue = Math.min(Math.max(value, 1), 20);
                    setNumberOfWeeks(clampedValue);
                }}
                required
                min={1}
                max={20}
                style={numberOfWeeksStyle}
            />
            <button disabled={disableButton} type="submit">Update Course</button>
        </form>
    );
};

export default EditCourseForm;
