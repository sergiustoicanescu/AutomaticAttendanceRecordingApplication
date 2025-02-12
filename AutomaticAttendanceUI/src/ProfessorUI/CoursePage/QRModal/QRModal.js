import React, { useState, useEffect } from 'react';
import { QRCodeCanvas } from 'qrcode.react';
import './QRModal.css';
import InfoModal from '../../../InfoModal/InfoModal';

const QRModal = ({ isOpen, onClose, course, session }) => {
    const [qrSize, setQrSize] = useState(256);

    useEffect(() => {
        const updateQrSize = () => {
            const screenWidth = window.innerWidth;
            const screenHeight = window.innerHeight;
            if (screenWidth < 1200 || screenHeight < 700) {
                setQrSize(250);
            } else {
                setQrSize(400);
            }
        };
        window.addEventListener('resize', updateQrSize);
        updateQrSize();

        return () => window.removeEventListener('resize', updateQrSize);
    }, []);

    const qrValue = `http://` + process.env.REACT_APP_DOMAIN + `/student/attendance/${course.code}/${session.code}`;

    return (
        <>
            <InfoModal
                isOpen={isOpen}
                onClose={onClose}
            >
                <div style={{ textAlign: "center", padding: "0 60px" }}>
                    <h2>{course.name}</h2>
                    <p>{session.name}</p>
                    <p>{session.startDate} {session.startTime.substring(0, 5)} - {session.endTime.substring(0, 5)}</p>
                    <p>Course code: {course.code}</p>
                    {session.active ? (<p>Session code: {session.code}</p>) : (<p style={{ color: "red" }}>The session is inactive!</p>)}
                    <div className="qr-code-container">
                        <QRCodeCanvas value={qrValue} size={qrSize} />
                    </div>
                </div>

            </InfoModal>
        </>
    );
};

export default QRModal;
