import React, { useState } from 'react'
import Modal from '../../../Modal/Modal';
import { MdOutlineDeleteOutline } from "react-icons/md";
import fetchWithRedirect from '../../../fetchUtil';
import { MdOutlineEdit } from 'react-icons/md';
import { FiXCircle } from "react-icons/fi";
import { FiCheck } from "react-icons/fi";
import Tooltip from '../../ToolTip/Tooltip';

const StudentDetails = ({ index, userId, code, student, handleRemoveStudent, handleEditStudent }) => {
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [isEditMode, setIsEditMode] = useState(false);
    const [editableEmail, setEditableEmail] = useState(student.email);
    const [tooltipMessage, setTooltipMessage] = useState('');

    const deleteStudent = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/course/deleteStudent/${userId}/${code}/${student.email}`,
                {
                    method: 'DELETE',
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
            alert(error.message)
            throw error;
        }
    };

    const handleDeleteStudent = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await deleteStudent(student.email);
            setTimeout(() => {
                handleRemoveStudent(student.email);
                setIsLoading(false);
                setShowDeleteModal(false)
            }, 500);
        } catch (error) {
            setIsLoading(false);
        }
    };

    const handleCancelEdit = () => {
        setTooltipMessage('');
        setIsEditMode(false);
        setEditableEmail(student.email)
    };

    const handleOpenEdit = () => {
        setTooltipMessage('');
        setIsEditMode(true);
        setEditableEmail(student.email)
    };

    const handleEditStudentFetch = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/course/editStudent/${code}`,
                {
                    method: 'POST',
                    body: {
                        oldEmail: student.email,
                        newEmail: editableEmail
                    }
                });
            if (!response) {
                return;
            }
            if (!response.ok) {
                const errMsg = await response.text();
                throw new Error(errMsg);
            }
            const data = await response.text();
            return data;
        } catch (error) {
            console.error('Error:', error);
            throw error;
        }
    };

    const handleConfirmEdit = async () => {
        setTooltipMessage('');
        try {
            const newStudentEmail = await handleEditStudentFetch();
            handleEditStudent(student.email, newStudentEmail);
            setIsEditMode(false);
        } catch (error) {
            setTooltipMessage(error.message);
        }
    };

    return (
        <>
            <div className='edit-students-details'>
                {isEditMode ? (
                    <input className='search-students-input' type="text" value={editableEmail} onChange={(e) => setEditableEmail(e.target.value)} required />
                ) : (
                    student.email
                )}
                <div className="session-actions">
                    {isEditMode ? (
                        <>
                            <button title='Cancel edit' className="session-edit-cancel" onClick={handleCancelEdit}>
                                <FiXCircle size={16} />
                            </button>
                            <button title='Confirm edit' className="session-edit-confirm" onClick={handleConfirmEdit}>
                                <FiCheck size={16} />
                            </button>
                        </>
                    ) : (
                        <>
                            <button title='Delete student' onClick={() => setShowDeleteModal(true)} className="session-edit-cancel">
                                <MdOutlineDeleteOutline size={16} />
                            </button>
                            <button title='Edit student' onClick={handleOpenEdit} className="edit-session">
                                <MdOutlineEdit size={16} />
                            </button>
                        </>
                    )}
                </div>
                {tooltipMessage && (
                    <>
                        {index === 0 ? (
                            <>
                                <Tooltip message={tooltipMessage} position={"bottom"} />
                            </>
                        ) : (
                            <>
                                <Tooltip message={tooltipMessage} />
                            </>
                        )}
                    </>
                )}
            </div>
            {showDeleteModal && (<Modal
                isOpen={showDeleteModal}
                onClose={() => setShowDeleteModal(false)}
                onConfirm={handleDeleteStudent}
                title='Delete student'
                confirmText={isLoading ? 'Deleting...' : 'Delete'}
                cancelText="Cancel"
                isLoading={isLoading}>

                <p>Are you sure you want to delete the student <strong>{student.email}</strong>?</p>
            </Modal>)}
        </>

    )
}

export default StudentDetails