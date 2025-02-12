import React from 'react';
import CourseFeed from './CourseFeed'
import LoadingSpinner from '../../Loading/LoadingSpinner';

import './Home.css'

const Home = ({ user, courses, setCourses, isLoading }) => {
    return (
        <div>
            {!isLoading && (
                <main className="Home">
                    {courses.length ? (
                        <CourseFeed user={user} courses={courses} setCourses={setCourses} />
                    ) : (
                        <p style={{ marginTop: "1rem " }} >
                            No courses to display.
                        </p>
                    )}
                </main>
            )}
            {isLoading && (
                <LoadingSpinner />
            )}
        </div>
    )
}

export default Home