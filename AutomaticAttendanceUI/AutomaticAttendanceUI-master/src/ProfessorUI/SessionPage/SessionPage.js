import React, { useState, useEffect, useCallback } from 'react'
import SubHeader from '../SubHeader/SubHeader'
import { useParams, useSearchParams } from 'react-router-dom';
import { BsBarChartFill, BsInfoLg } from "react-icons/bs";
import LoadingSpinner from '../../Loading/LoadingSpinner';
import fetchWithRedirect from '../../fetchUtil';
import { format } from 'date-fns';
import './SessionPage.css'
import StudentTable from './StudentTable/StudentTable';
import { TbCircleLetterA, TbCircleLetterP } from "react-icons/tb";
import AttendanceStatistics from './AttendanceStatistics/AttendanceStatistics';

const SessionPage = ({ user }) => {
    const { code } = useParams();
    const [searchParams] = useSearchParams();
    const name = searchParams.get('name');
    const week = searchParams.get('week');

    const [isLoading, setIsLoading] = useState(true);
    const [course, setCourse] = useState({});
    const [session, setSession] = useState({});
    const [students, setStudents] = useState([]);
    const [showSessionInfo, setShowSessionInfo] = useState(false);
    const [date, setDate] = useState();
    const [isAttendanceStatsOpen, setIsAttendanceStatsOpen] = useState(false);
    const [activeEmail, setActiveEmail] = useState(null);



    const fetchSessionDetails = useCallback(async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/attendance/sessionDetails/${user.id}/${code}/${name}/${week}`,
            );
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            setCourse(data.course)
            setSession(data.session)
            setStudents(data.students)
            setDate(format(new Date(data.session.startDate), "EEEE dd MMM yyyy"))
            setIsLoading(false);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
        }
    }, [user, code, name, week]);

    useEffect(() => {
        fetchSessionDetails();
    }, [fetchSessionDetails]);

    const markAttendance = async (email, status) => {
        setActiveEmail(email);
        try {
            const response = await fetchWithRedirect(
                `/professor/attendance/markAttendance/${user.id}/${course.code}/${session.name}/${session.week}`,
                {
                    method: 'POST',
                    body: {
                        email: email,
                        attendance: {
                            status: status
                        }
                    }
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            setStudents(students =>
                students.map(student =>
                    student.email === data.email ?
                        { ...student, attendance: { ...student.attendance, status: data.attendance.status, date: data.attendance.date, time: data.attendance.time } }
                        : student
                )
            );
            setActiveEmail(null);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
            setActiveEmail(null);
        }
    };

    const handleStatusChange = (sEmail, newStatus) => {
        markAttendance(sEmail, newStatus);
    };

    const markBulkAttendance = async (newStatus) => {
        try {
            const response = await fetchWithRedirect(
                `/professor/attendance/markBulkAttendance/${user.id}/${course.code}/${session.name}/${session.week}`,
                {
                    method: 'POST',
                    body: {
                        status: newStatus
                    }
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
            const data = await response.json();
            const updatedStudents = students.map(student => {
                if (!student.attendance.status) {
                    return { ...student, attendance: { ...student.attendance, status: data.status, date: data.date, time: data.time } };
                }
                return student;
            });
            setStudents(updatedStudents);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
        }
    };

    const saveComment = async (email, newComment) => {
        try {
            const response = await fetchWithRedirect(
                `/professor/attendance/saveComment/${user.id}/${course.code}/${session.name}/${session.week}`,
                {
                    method: 'PATCH',
                    body: {
                        email: email,
                        attendance: {
                            comment: newComment
                        }
                    }
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
        }
    };

    const handleCommentSave = (sEmail, newComment) => {
        saveComment(sEmail, newComment);
        setStudents(students =>
            students.map(student =>
                student.email === sEmail ?
                    { ...student, attendance: { ...student.attendance, comment: newComment } }
                    : student
            )
        );
    };


    if (isLoading) {
        return <LoadingSpinner />;
    }

    return (
        <>
            <SubHeader
                title={
                    <>
                        {`${course.name} > Week ${session.week} - ${session.name}`}
                        <span className={`session-info ${showSessionInfo ? 'show' : ''}`}>
                            {showSessionInfo ? ` > ${date}, ${session.startTime.substring(0, 5)}-${session.endTime.substring(0, 5)}` : ''}
                        </span>
                    </>
                }
                link={`/professor/course/${course.code}?week=${session.week}`}
            >
                <button onClick={() => setIsAttendanceStatsOpen(true)}>
                    <BsBarChartFill size={20} />
                </button>
                <button
                    onClick={() => setShowSessionInfo(prev => !prev)}
                    style={{
                        backgroundColor: showSessionInfo ? 'black' : 'inherit',
                        color: showSessionInfo ? 'white' : 'inherit',
                    }}
                >
                    <BsInfoLg size={20} />
                </button>
            </SubHeader>
            <div className="bulk-action-container">
                <span className="bulk-action-text">Register all currently unmarked records either present or absent:</span>
                <button className="bulk-action-button-present" onClick={() => markBulkAttendance('PRESENT')}>
                    <TbCircleLetterP size={35} />
                </button>
                <button className="bulk-action-button-absent" onClick={() => markBulkAttendance('ABSENT')}>
                    <TbCircleLetterA size={35} />
                </button>
            </div>
            <StudentTable students={students} onStatusChange={handleStatusChange} onCommentSave={handleCommentSave} activeEmail={activeEmail} />
            {isAttendanceStatsOpen && <AttendanceStatistics userId={user.id} code={course.code} sessionName={session.name} week={session.week} isOpen={isAttendanceStatsOpen} onClose={() => setIsAttendanceStatsOpen(false)} nrStudents={students.length} />}
        </>
    )
}

export default SessionPage