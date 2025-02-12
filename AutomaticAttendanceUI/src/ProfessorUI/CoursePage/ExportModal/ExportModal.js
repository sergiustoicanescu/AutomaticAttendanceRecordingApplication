import React, { useState } from 'react'
import InfoModal from '../../../InfoModal/InfoModal'
import './ExportModal.css'
import { PiExport } from "react-icons/pi";
import { FaCheck } from "react-icons/fa";
import { LuX } from "react-icons/lu";
import fetchWithRedirect from '../../../fetchUtil';

const ExportStatus = {
    IDLE: 'IDLE',
    LOADING: 'LOADING',
    CONFIRMED: 'CONFIRMED',
    ERROR: 'ERROR'
};

const ExportModal = ({ course, isOpen, onClose }) => {
    const [status, setStatus] = useState(ExportStatus.IDLE);

    const handleExport = async () => {
        setStatus(ExportStatus.LOADING)
        try {
            const response = await fetchWithRedirect(
                `/professor/course/export/${course.code}`
            );
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Network response was not ok');
            }

            const blob = await response.blob();
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement('a');
            a.href = url;
            a.download = `export_${course.name}.csv`;
            document.body.appendChild(a);
            a.click();
            window.URL.revokeObjectURL(url);
            document.body.removeChild(a);
            setStatus(ExportStatus.CONFIRMED)
        } catch (error) {
            console.error('Error:', error);
            setStatus(ExportStatus.ERROR)
        }
    };

    return (
        <InfoModal
            isOpen={isOpen}
            onClose={onClose}
        >
            <div className='course-export-container'>
                <h2 className='course-export-text'>Export to CSV</h2>
                <span className='course-export-text'>Note that this may take a while.</span>
                <button className='course-export-button'>
                    {status === ExportStatus.IDLE && (
                        <PiExport size={30} onClick={handleExport} />
                    )}
                    {status === ExportStatus.LOADING && (
                        <div className='spinner-professor' style={{ border: "4px solid white", borderTop: "4px solid #0cc015" }} />
                    )}
                    {status === ExportStatus.CONFIRMED && (
                        <FaCheck size={30} color='#0cc015' />
                    )}
                    {status === ExportStatus.ERROR && (
                        <LuX size={30} color='red' />
                    )}
                </button>
            </div>
        </InfoModal>
    )
}

export default ExportModal