import React, { useState } from 'react';
import fetchWithRedirect from '../../../fetchUtil';
import './EditStudents.css';
import StudentDetails from './StudentDetails';
import Modal from '../../../Modal/Modal';

const EditStudents = ({ userId, code, students, handleRemoveStudent, handleEditStudent, setStudents }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [isLoading, setIsLoading] = useState(false);
    const [studentEmails, setStudentEmails] = useState('');
    const [showEditModal, setShowEditModal] = useState(false);
    const [tooltipVisible, setTooltipVisible] = useState(false);
    const [tooltipMessage, setTooltipMessage] = useState('');

    const handleSearchChange = (event) => {
        setSearchTerm(event.target.value);
    };

    const filteredStudents = students.filter(student =>
        student.email.toLowerCase().includes(searchTerm.toLowerCase())
    );

    const addStudents = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/course/addStudents/${userId}/${code}`,
                {
                    method: 'POST',
                    headers: { 'Content-Type': 'text/plain' },
                    body: studentEmails
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errorText = await response.text();
                throw new Error(errorText || 'Network response was not ok');
            }

            const data = await response.json();
            return data;

        } catch (error) {
            console.error('Error:', error);
            setTooltipMessage(error.message);
            setTooltipVisible(true);
            throw error;
        }
    };

    const handleAddStudents = async (e) => {
        e.preventDefault()
        setTooltipMessage('');
        setTooltipVisible(false);
        setIsLoading(true);
        try {
            const newStudentsParsed = await addStudents();
            setTimeout(() => {
                setIsLoading(false);
                handleOnClose()
                setStudents([...students, ...newStudentsParsed]);
            }, 500);
        } catch (error) {
            setIsLoading(false);
        }
    };

    const handleOnClose = () => {
        setTooltipMessage('');
        setTooltipVisible(false);
        setShowEditModal(false);
    }

    return (
        <>
            <div className="search-students-bar">
                <input
                    type="text"
                    placeholder="Search students..."
                    value={searchTerm}
                    onChange={handleSearchChange}
                    className="search-students-input"
                />
            </div>
            <div className='students-list'>
                {filteredStudents.length > 0 ? (
                    filteredStudents.map((student, index) => (
                        <StudentDetails key={index} index={index} userId={userId} code={code} student={student} handleRemoveStudent={handleRemoveStudent} handleEditStudent={handleEditStudent} />
                    ))
                ) : (
                    <div className='no-students-message'>
                        No students found.
                    </div>
                )}
            </div>
            <button className='edit-add-session-btn' onClick={(e) => { setStudentEmails(''); setShowEditModal(true) }}>Add Students</button>
            {showEditModal && (<Modal
                isOpen={showEditModal}
                onClose={handleOnClose}
                onConfirm={handleAddStudents}
                title='Add Students'
                confirmText={isLoading ? 'Adding...' : 'Add'}
                cancelText="Cancel"
                isLoading={isLoading}
                tooltipMessage={tooltipMessage}
                tooltipVisible={tooltipVisible}>
                <div className="email-container">
                    <textarea
                        id="studentEmails"
                        value={studentEmails}
                        onChange={(e) => setStudentEmails(e.target.value)}
                        placeholder="Enter the students emails"
                        rows="10"
                        required
                        style={{ marginBottom: '0px', resize: 'none' }}
                    ></textarea>
                </div>
            </Modal>)}
        </>
    );
};

export default EditStudents;
