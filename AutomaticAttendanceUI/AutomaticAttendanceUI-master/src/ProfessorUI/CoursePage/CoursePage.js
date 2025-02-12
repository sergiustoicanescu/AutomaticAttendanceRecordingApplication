import React, { useState, useEffect, useCallback } from 'react';
import { useParams, Link, useSearchParams } from 'react-router-dom'
import './CoursePage.css'
import { FaRegEdit } from "react-icons/fa";
import { TbFileExport } from "react-icons/tb";
import LocationModal from './LocationModal/LocationModal';
import SessionFeed from './SessionFeed';
import fetchWithRedirect from '../../fetchUtil';
import Missing from '../../Missing';
import SubHeader from '../SubHeader/SubHeader';
import ExportModal from './ExportModal/ExportModal';

const CoursePage = ({ user }) => {
    const [searchParams] = useSearchParams();
    const { code } = useParams();
    const urlWeek = searchParams.get('week');

    const [course, setCourse] = useState({});
    const [sessions, setSessions] = useState([]);
    const [openWeek, setOpenWeek] = useState(null);
    const [isModalOpen, setIsModalOpen] = useState(false);
    const [isExportModalOpen, setIsExportModalOpen] = useState(false);
    const [isMissing, setIsMissing] = useState(false);


    const fetchCourseDetails = useCallback(async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/courseAndSession/${user.id}/${code}`,
            );
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            setCourse(data.course);
            setSessions(data.sessions);
            setOpenWeek(urlWeek ? urlWeek : String(data.course.currentWeek));
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
            setIsMissing(true);
        }
    }, [user, code, urlWeek]);

    useEffect(() => {
        fetchCourseDetails();
    }, [fetchCourseDetails]);

    const toggleWeek = (week) => {
        setOpenWeek(currentWeek => currentWeek === week ? null : week);
    };

    const sessionsByWeek = sessions.reduce((acc, session) => {
        (acc[session.week] = acc[session.week] || []).push(session);
        return acc;
    }, {});

    const handleLocationLinkClick = (event) => {
        event.preventDefault();
        setIsModalOpen(true);
    };

    const handleSaveLocation = async (coordinates) => {
        try {
            const response = await fetchWithRedirect(
                `/professor/updateCourse/location/${user.id}/${course.id}`,
                {
                    method: 'PATCH',
                    body: { location: coordinates }
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Network response was not ok');
            }
            const updatedCourse = { ...course, location: coordinates };
            setCourse(updatedCourse);
            setIsModalOpen(false);
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    };

    useEffect(() => {
        if (openWeek) {
            const openWeekElement = document.getElementById(`week-${openWeek}`);
            if (openWeekElement) {
                openWeekElement.scrollIntoView({ behavior: 'smooth', block: 'center' });
            }
        }
    }, [openWeek]);

    if (isMissing) {
        return <Missing />
    }


    return (
        <>
            <SubHeader title={course.name} >
                <span className="course-location">
                    <a href="location" className="course-location-link" onClick={handleLocationLinkClick}>
                        {course.location ? 'Change location' : 'No location set'}
                    </a>
                </span>
                <button onClick={() => setIsExportModalOpen(true)}>
                    <TbFileExport size={20} />
                </button>
                <Link to={`edit`}>
                    <button>
                        <FaRegEdit size={20} />
                    </button>
                </Link>
            </SubHeader>
            <main className="course-page">
                {Object.keys(sessionsByWeek).map((week) => (
                    <div key={week} id={`week-${week}`} className="week-block">
                        <button
                            className={`week-header ${openWeek === week ? 'opened' : ''}`}
                            onClick={() => toggleWeek(week)}
                        >
                            Week {week}{String(course.currentWeek) === week ? ' - Current week' : ''}
                        </button>
                        {openWeek === week && (
                            <SessionFeed course={course} sessions={sessionsByWeek[week]} setSessions={setSessions} />
                        )}
                    </div>
                ))}
            </main>

            {isModalOpen && (
                <LocationModal
                    isOpen={isModalOpen}
                    onClose={() => setIsModalOpen(false)}
                    initialLocation={course.location}
                    onSave={handleSaveLocation}
                />
            )}
            {isExportModalOpen && (
                <ExportModal
                    isOpen={isExportModalOpen}
                    onClose={() => setIsExportModalOpen(false)}
                    course={course}
                />
            )}
        </>
    );
}

export default CoursePage