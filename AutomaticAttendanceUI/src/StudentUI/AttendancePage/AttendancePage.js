import React, { useState, useEffect, useCallback } from 'react'
import { useParams, useNavigate } from 'react-router-dom';
import fetchWithRedirect from '../../fetchUtil';
import { format } from 'date-fns';
import LoadingSpinner from '../../Loading/LoadingSpinner';
import './AttendancePage.css';
import Tooltip from '../../ProfessorUI/ToolTip/Tooltip';
import SubHeader from '../../ProfessorUI/SubHeader/SubHeader';
import SuccessMessage from './SuccessMessage/SuccessMessage';
import MessagePage from '../../MessagePage/MessagePage';

const knownErrors = [
    "The student is not enrolled in this course!",
    "The student is already present in a session with this type this week!"
];

const AttendancePage = ({ user }) => {
    const { courseCode, sessionCode } = useParams();

    const [isLoading, setIsLoading] = useState(true);
    const [isError, setIsError] = useState(false);
    const [course, setCourse] = useState({});
    const [session, setSession] = useState({});
    const [date, setDate] = useState();
    const [userLocation, setUserLocation] = useState({ lat: null, lng: null });
    const [tooltipMessage, setTooltipMessage] = useState('');
    const [isButtonLoading, setIsButtonLoading] = useState(false);
    const [success, setSuccess] = useState(false);
    const [errorMessage, setErrorMessage] = useState('');
    const [isLocationDisabled, setIsLocationDisabled] = useState(false);
    const navigate = useNavigate();


    const fetchSessionDetails = useCallback(async () => {
        try {
            const response = await fetchWithRedirect(
                `/student/attendance/session/${user.id}/${courseCode}/${sessionCode}`,
            );
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                let message = 'Failed fetching the session details. Please try again later.';
                if (knownErrors.some(knownError => errorText.includes(knownError))) {
                    message = errorText;
                    setErrorMessage(message);
                }
                throw new Error(errorText || 'Network response was not ok');
            }

            const data = await response.json();
            setCourse(data.course)
            setSession(data.session)
            setDate(format(new Date(data.session.startDate), "EEEE dd MMM yyyy"))
            if (!data.course.location.lat && !data.course.location.lng) {
                setIsLocationDisabled(true);
            }
            setIsLoading(false);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
            setIsLoading(false);
            setIsError(true);
        }
    }, [user.id, courseCode, sessionCode]);

    useEffect(() => {
        fetchSessionDetails();
    }, [fetchSessionDetails]);

    useEffect(() => {
        setTooltipMessage('');
        setUserLocation({ lat: null, lng: null })

        let watchId = null;
        let bestAccuracy = Infinity;
        let bestPosition = null;

        const options = {
            enableHighAccuracy: true,
            timeout: 5000,
            maximumAge: 0
        };
        if (!isLoading && !isError && !isLocationDisabled && "geolocation" in navigator) {
            setIsButtonLoading(true);

            watchId = navigator.geolocation.watchPosition((position) => {
                console.log(`Received position: ${position.coords.latitude}, ${position.coords.longitude} with accuracy ${position.coords.accuracy}`);
                if (position.coords.accuracy < bestAccuracy) {
                    bestAccuracy = position.coords.accuracy;
                    bestPosition = position;
                }

                if (position.coords.accuracy < 99) {
                    setIsButtonLoading(false);
                    setUserLocation({
                        lat: position.coords.latitude,
                        lng: position.coords.longitude
                    });
                    navigator.geolocation.clearWatch(watchId);
                }
            }, (error) => {
                console.log("Error obtaining location", error);
                setIsButtonLoading(false);
                setTooltipMessage("Error obtaining location. Please try again!");
            }, options);
        }

        const timerId = setTimeout(() => {
            if (watchId) {
                navigator.geolocation.clearWatch(watchId);
                if (bestPosition) {
                    setIsButtonLoading(false);
                    setUserLocation({
                        lat: bestPosition.coords.latitude,
                        lng: bestPosition.coords.longitude
                    });
                    if (bestAccuracy >= 95) {
                        setTooltipMessage("Location obtained is not highly accurate.");
                    }
                } else {
                    setIsButtonLoading(false);
                    setTooltipMessage("Failed to obtain location. Refresh and try again!");
                }
            }
        }, 5000);

        return () => {
            clearTimeout(timerId);
            if (watchId) {
                navigator.geolocation.clearWatch(watchId);
            }
        };
    }, [isLoading, isError, isLocationDisabled]);


    const markAttendance = async () => {
        try {
            const response = await fetchWithRedirect(
                `/student/attendance/markAttendance/${user.id}/${session.id}/${sessionCode}`,
                {
                    method: 'POST',
                    body: {
                        userLocation: userLocation,
                        email: user.email
                    }
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Network response was not ok');
            }
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    };

    const handleMarkAttendance = async (e) => {
        setTooltipMessage('');
        setIsButtonLoading(true);
        try {
            await markAttendance();
            setTimeout(() => {
                setIsButtonLoading(false);
                setSuccess(true);
                setTimeout(() => {
                    setSuccess(false);
                    navigate("/student");
                }, 2000);
            }, 500);
        } catch (error) {
            setTimeout(() => {
                setIsButtonLoading(false);
                setTooltipMessage("We coulnd't verify your attendance, refresh and try again!");
            }, 500)
        }
    };

    if (isLoading) {
        return <LoadingSpinner />;
    }

    if (isError) {
        return <MessagePage
            title={errorMessage ? "Validation Error" : "Failed to Fetch Session Details"}
            titleColor={"#D32F2F"}
            message={errorMessage ? errorMessage : "There was a problem fetching the session details. Please try again later."}
            role={"student"}
        />
    }

    return (
        <>
            <SubHeader title={"Mark Attendance"} />
            <div className="attendance-container">
                <div className="attendance-details">
                    <p><strong>{course.name}</strong></p>
                    <p><strong>{session.name}</strong></p>
                    <p>{session.type}</p>
                    <p>{date}</p>
                    <p>{session.startTime.substring(0, 5)}-{session.endTime.substring(0, 5)}</p>
                </div>
                <div className="marking-container">
                    {!success ? (
                        <button className={`circular-button ${!isLocationDisabled && !userLocation.lat && !userLocation.lng ? 'disabled' : ''}`}
                            disabled={!isLocationDisabled && !userLocation.lat && !userLocation.lng}
                            onClick={handleMarkAttendance}>
                            {isButtonLoading ? (
                                <div className="spinner"></div>
                            ) : (
                                "GO"
                            )}
                        </button>
                    ) : (
                        <SuccessMessage message="You are marked present!" />
                    )}
                    {tooltipMessage && <Tooltip message={tooltipMessage} />}
                </div>
            </div>
        </>
    );
};

export default AttendancePage;