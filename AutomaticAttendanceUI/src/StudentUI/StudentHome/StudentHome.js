import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './StudentHome.css';
import CodeInput from './CodeInput/CodeInput';
import { FaLongArrowAltRight, FaLongArrowAltLeft } from "react-icons/fa";

const StudentHome = () => {
    const [courseCode, setCourseCode] = useState(Array(6).fill(''));
    const [sessionCode, setSessionCode] = useState(Array(6).fill(''));
    const [showSessionInput, setShowSessionInput] = useState(false);
    const [transitionDirection, setTransitionDirection] = useState('left');
    const navigate = useNavigate();

    const isCodeComplete = (codeArray) => codeArray.every(char => char.trim() !== '');

    const handleShowSessionInput = (show) => {
        setTransitionDirection(show ? 'right' : 'left');
        setShowSessionInput(show);
    };

    return (
        <div className="student-home-container">
            <div className={`message-container ${transitionDirection}`}>
                {!showSessionInput ? (
                    <>
                        <p><strong>Scan the QR code with your phone.</strong></p>
                        <p>OR</p>
                        <div>
                            <p><strong>Enter the course code:</strong></p>
                            <CodeInput code={courseCode} setCode={setCourseCode} type={"text"} />
                            {isCodeComplete(courseCode) &&
                                <button className="sidebar-trigger" onClick={() => handleShowSessionInput(true)}>
                                    <FaLongArrowAltRight size={25} />
                                </button>}
                        </div>
                    </>
                ) : (
                    <>
                        <p><strong>Enter the session code:</strong></p>
                        <CodeInput code={sessionCode} setCode={setSessionCode} type={"tel"} />
                        <div className="button-container">
                            <button className="sidebar-trigger" onClick={() => handleShowSessionInput(false)}>
                                <FaLongArrowAltLeft size={25} />
                            </button>
                            {isCodeComplete(sessionCode) && (
                                <button className="attendance-page-button" onClick={() => navigate(`attendance/${courseCode.join('')}/${sessionCode.join('')}`)}>
                                    Enter
                                </button>
                            )}
                        </div>
                    </>
                )}
            </div>
        </div>
    );
};

export default StudentHome;
