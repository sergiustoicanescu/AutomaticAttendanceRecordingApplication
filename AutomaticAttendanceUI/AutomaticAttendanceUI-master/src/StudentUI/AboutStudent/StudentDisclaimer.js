import React from 'react'
import SubHeader from '../../ProfessorUI/SubHeader/SubHeader'
import './AboutStudent.css';

const StudentDisclaimer = () => {
    return (
        <>
            <SubHeader title={"Disclaimer"} link={"/student/about"} />
            <div className="about-page">
                <div className="about-content disclaimer">
                    <p>The student can opt out at any time, and their attendance will be manually marked by the professor.</p>
                    <p>The app does not store location data; it uses location just once to verify presence in the classroom.</p>
                    <p>Consent to use location data is required to utilize the application whenever location-based attendance is enabled by the instructor.</p>
                </div>
            </div>
        </>
    )
}

export default StudentDisclaimer