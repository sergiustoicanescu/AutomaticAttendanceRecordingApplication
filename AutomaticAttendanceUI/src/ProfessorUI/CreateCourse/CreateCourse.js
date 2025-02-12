import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Popup from '../Popup/Popup';
import './CreateCourse.css';
import validateSessions from '../ValidateSessions';
import fetchWithRedirect from '../../fetchUtil';

const CreateCourse = ({ user, setCourses }) => {
    const [courseName, setCourseName] = useState('');
    const [numberOfWeeks, setNumberOfWeeks] = useState('');
    const [sessions, setSessions] = useState([
        { name: '', type: '', startDate: '', startTime: '', endTime: '', frequency: '1' }
    ]);
    const [studentEmails, setStudentEmails] = useState('');
    const [validationError, setValidationError] = useState('');
    const [sessionErrors, setSessionErrors] = useState(new Array(sessions.length).fill(false));
    const [showPopup, setShowPopup] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [popupMessage, setPopupMessage] = useState('');
    const [operationStatus, setOperationStatus] = useState('');
    const navigate = useNavigate();

    const handleSubmit = async (event) => {
        event.preventDefault();
        const { hasError, updatedSessionErrors } = validateSessions(sessions, setValidationError);

        setSessionErrors(updatedSessionErrors);

        if (hasError) {
            return;
        }

        setValidationError('')

        setShowPopup(true);
        setIsLoading(true);

        const createCourseData = {
            userId: user.id,
            name: courseName,
            numberOfWeeks: numberOfWeeks,
            sessions: sessions,
            students: studentEmails
        };

        try {
            const response = await fetchWithRedirect('/professor/createCourse', {
                method: 'POST',
                body: createCourseData
            });
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errMsg = await response.text();
                setOperationStatus('failed');
                throw new Error(errMsg);
            } else {
                setOperationStatus('success');
            }

            const newCourse = await response.json();
            setPopupMessage('Course created successfully!');
            setIsLoading(false);
            setTimeout(() => {
                setCourses(courses => [...courses, newCourse]);
                navigate(`/professor/course/${newCourse.code}`);
            }, 2000);
        } catch (error) {
            setIsLoading(false);
            setPopupMessage(error.message);
            setTimeout(() => {
                setShowPopup(false);
                setValidationError(error.message)
            }, 2000);
        }
    };

    const handleSessionChange = (index, event) => {
        const updatedSessions = [...sessions];
        updatedSessions[index][event.target.name] = event.target.value;
        setSessions(updatedSessions);
    };

    const addSession = () => {
        setSessions([...sessions, { name: '', type: '', startDate: '', startTime: '', endTime: '', frequency: '1' }]);
        setValidationError('')
        setSessionErrors(new Array(sessions.length).fill(false))
    };

    const deleteSession = (index) => {
        const updatedSessions = [...sessions];
        updatedSessions.splice(index, 1);
        setSessions(updatedSessions);
        setValidationError('')
        setSessionErrors(new Array(sessions.length).fill(false))
    };

    return (
        <div>
            <div className="create-course-container">
                <h2 className='create-header'>Create Course</h2>
                <form onSubmit={handleSubmit}>
                    <div className="form-row">
                        <div>
                            <label>Name:</label>
                            <input
                                className='courseInput'
                                type="text"
                                value={courseName}
                                onChange={e => setCourseName(e.target.value)}
                                required
                            />
                        </div>
                        <div>
                            <label>Number of Weeks:</label>
                            <input
                                type="number"
                                value={numberOfWeeks}
                                onChange={e => {
                                    const value = parseInt(e.target.value, 10);
                                    const clampedValue = Math.min(Math.max(value, 1), 20);
                                    setNumberOfWeeks(clampedValue);
                                }}
                                min={1}
                                max={20}
                                required
                            />
                        </div>
                    </div>
                    <div className="sessions-container">
                        <h3>Create the sessions for the first week:</h3>
                        {sessions.map((session, index) => (
                            <div
                                key={index}
                                className={`session-row ${sessionErrors[index] ? 'session-error' : ''}`}
                                style={{ backgroundColor: index % 2 === 0 ? '#f5f5f5' : '#e0e0e0' }}
                            >
                                <input
                                    type="text"
                                    name="name"
                                    placeholder="Session Name"
                                    value={session.name}
                                    onChange={(e) => handleSessionChange(index, e)}
                                    required
                                />
                                <select
                                    name="type"
                                    value={session.type}
                                    onChange={(e) => handleSessionChange(index, e)}
                                    required
                                >
                                    <option value="">Select Type</option>
                                    <option value="Lecture">Lecture</option>
                                    <option value="Lab">Lab</option>
                                    <option value="Seminar">Seminar</option>
                                </select>
                                <input
                                    title='Start date'
                                    type="date"
                                    name="startDate"
                                    value={session.startDate}
                                    onChange={(e) => handleSessionChange(index, e)}
                                    required
                                />
                                <input
                                    title='Start time'
                                    type="time"
                                    name="startTime"
                                    value={session.startTime}
                                    onChange={(e) => handleSessionChange(index, e)}
                                    required
                                />
                                <input
                                    title='End time'
                                    type="time"
                                    name="endTime"
                                    value={session.endTime}
                                    onChange={(e) => handleSessionChange(index, e)}
                                    required
                                />
                                <select
                                    name="frequency"
                                    value={session.frequency}
                                    onChange={(e) => handleSessionChange(index, e)}
                                    required
                                >
                                    <option value="">Select Frequency</option>
                                    <option value="1">Every week</option>
                                    <option value="2">Every two weeks</option>
                                </select>
                                {index !== 0 && (
                                    <button className='deleteBtn' type="button" onClick={() => deleteSession(index)}>Delete</button>
                                )}
                            </div>
                        ))}
                        <button className="addSessionBtn" type="button" onClick={addSession}>Add Session</button>
                    </div>
                    <div className="email-container">
                        <h3 htmlFor="studentEmails">Enter the students emails:</h3>
                        <textarea
                            id="studentEmails"
                            value={studentEmails}
                            onChange={(e) => setStudentEmails(e.target.value)}
                            placeholder="Enter the students emails"
                            rows="10"
                            required
                        ></textarea>
                    </div>
                    {validationError && <div className="tooltip">{validationError}</div>}
                    <button className='submit-button' type="submit">Create</button>
                </form>
            </div>
            {showPopup && <Popup
                isLoading={isLoading}
                message={popupMessage}
                status={operationStatus} />
            }
        </div>
    );
}

export default CreateCourse;
