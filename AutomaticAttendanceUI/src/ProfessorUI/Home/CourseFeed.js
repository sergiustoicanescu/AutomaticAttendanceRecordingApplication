import React from 'react'
import Course from './Course'

const CourseFeed = ({ user, courses, setCourses }) => {
    return (
        <>
            {courses.map(course => (
                <Course user={user} key={course.id} course={course} setCourses={setCourses} />
            ))}
        </>
    )
}

export default CourseFeed