import React, { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom'
import fetchWithRedirect from '../../fetchUtil';
import './EditCoursePage.css'
import EditCourseForm from './EditCourseForm';
import LoadingSpinner from '../../Loading/LoadingSpinner';
import AddSessionModal from './AddSessionModal/AddSessionModal';
import SessionEdit from './SessionEdit';
import EditWeeksModal from './EditWeeksModal/EditWeeksModal';
import EditStudents from './EditStudents/EditStudents';
import SubHeader from '../SubHeader/SubHeader';
import EditProfessors from './EditProfessors/EditProfessors';

const EditCoursePage = ({ user, setCourses }) => {

    const { code } = useParams();
    const [isLoading, setIsLoading] = useState(true);
    const [course, setCourse] = useState({});
    const [sessions, setSessions] = useState([]);
    const [students, setStudents] = useState([]);
    const [professors, setProfessors] = useState([]);
    const [isAddSessionModalOpen, setIsAddSessionModalOpen] = useState(false);
    const [weeks, setWeeks] = useState({});
    const [isEditWeeksModalOpen, setIsEditWeeksModalOpen] = useState(false);

    const fetchCourseDetails = useCallback(async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/allCourseDetails/${user.id}/${code}`,
            );
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            setCourse(data.course)
            setWeeks(data.weeks);
            setSessions(data.sessions)
            setStudents(data.students)
            setProfessors(data.professors)
            setIsLoading(false);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
        }
    }, [user, code]);

    useEffect(() => {
        fetchCourseDetails();
    }, [fetchCourseDetails]);

    const handleAddSession = (sName, sType, sFrequency) => {
        const newSession = { name: sName, type: sType, frequency: sFrequency };
        setSessions([...sessions, newSession]);
    }

    const handleDeleteSession = (sessionName) => {
        const updatedSessions = sessions.filter(session => session.name !== sessionName);
        setSessions(updatedSessions);
    };

    const handleRemoveStudent = (studentEmail) => {
        const updatedStudents = students.filter(student => student.email !== studentEmail);
        setStudents(updatedStudents);
    };

    const handleEditStudent = (oldEmail, newEmail) => {
        const updatedStudents = students.map(student => {
            if (student.email === oldEmail) {
                return { ...student, email: newEmail };
            }
            return student;
        });
        setStudents(updatedStudents);
    };


    const handleAddProfessor = (professorEmail) => {
        const newProfessor = { email: professorEmail };
        setProfessors([...professors, newProfessor]);
    }

    const handleRemoveProfessor = (professorEmail) => {
        const updatedProfessors = professors.filter(professor => professor.email !== professorEmail);
        setProfessors(updatedProfessors);
    };

    const handleEditSession = (lastSession, newSession) => {
        const updatedSessions = sessions.map(session => {
            if (session.name === lastSession.name) {
                return newSession;
            } else {
                return session;
            }
        });
        setSessions(updatedSessions);
    };

    if (isLoading) {
        return <LoadingSpinner />;
    }

    return (
        <>
            <SubHeader title={`Edit: ${course.name}`} link={`/professor/course/${code}`} />
            <div className="edit-course-details">
                <EditCourseForm user={user} course={course} setCourse={setCourse} setCourses={setCourses} setWeeks={setWeeks} />
                <div className='edit-sidebar-divider'></div>
                <p>Sessions</p>
                {sessions.map((session, index) => (
                    <SessionEdit key={index} userId={user.id} code={code} session={session} isDeletable={sessions.length > 1} handleSessions={handleDeleteSession} handleEditConfirm={handleEditSession} />
                ))}
                <div className='edit-div-buttons'>
                    <button className='edit-add-session-btn' onClick={e => setIsAddSessionModalOpen(true)}>Add Session</button>
                    <button className='edit-weeks-session-btn' onClick={e => setIsEditWeeksModalOpen(true)}>Edit Weeks</button>
                </div>
                <div className='edit-sidebar-divider'></div>
                <p style={{ marginBottom: '10px' }}>Students</p>
                <EditStudents userId={user.id} code={code} students={students} handleRemoveStudent={handleRemoveStudent} handleEditStudent={handleEditStudent} setStudents={setStudents} />
                <div className='edit-sidebar-divider'></div>
                <p style={{ marginBottom: '10px' }}>Professors</p>
                <EditProfessors user={user} code={code} professors={professors} owner={course.professor.email} handleAddProfessor={handleAddProfessor} handleRemoveProfessor={handleRemoveProfessor} />
            </div >
            <AddSessionModal userId={user.id} code={code} isOpen={isAddSessionModalOpen} onClose={e => setIsAddSessionModalOpen(false)} weeks={weeks} handleSessions={handleAddSession} />
            {isEditWeeksModalOpen && (<EditWeeksModal userId={user.id} code={code} weeks={weeks} setWeeks={setWeeks} isOpen={isEditWeeksModalOpen} onClose={e => setIsEditWeeksModalOpen(false)} />)}
        </>
    )
}

export default EditCoursePage