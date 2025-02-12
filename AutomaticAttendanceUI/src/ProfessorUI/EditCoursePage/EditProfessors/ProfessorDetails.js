import React, { useState } from 'react'
import Modal from '../../../Modal/Modal';
import { MdOutlineDeleteOutline } from "react-icons/md";
import { BsFillPersonLinesFill } from "react-icons/bs";
import fetchWithRedirect from '../../../fetchUtil';

const ProfessorDetails = ({ user, code, professor, owner, handleRemoveProfessor }) => {
    const [showDeleteModal, setShowDeleteModal] = useState(false);
    const [isLoading, setIsLoading] = useState(false);

    const deleteProfessor = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/course/deleteProfessor/${code}/${professor.email}`,
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

    const handleDeleteProfessor = async (e) => {
        e.preventDefault();
        setIsLoading(true);
        try {
            await deleteProfessor(professor.email);
            setTimeout(() => {
                handleRemoveProfessor(professor.email);
                setIsLoading(false);
                setShowDeleteModal(false)
            }, 500);
        } catch (error) {
            setIsLoading(false);
        }
    };

    return (
        <>
            <div className='edit-students-details'>
                {professor.email}
                <>
                    {professor.email === owner ? (
                        <div className="edit-session">
                            <BsFillPersonLinesFill size={16} />
                        </div>
                    ) : (
                        <>
                            {user.email === owner && (
                                <button title='Delete professor' onClick={() => setShowDeleteModal(true)} className="session-edit-cancel">
                                    <MdOutlineDeleteOutline size={16} />
                                </button>
                            )}
                        </>
                    )}
                </>
            </div>
            {showDeleteModal && (<Modal
                isOpen={showDeleteModal}
                onClose={() => setShowDeleteModal(false)}
                onConfirm={handleDeleteProfessor}
                title='Delete Professor'
                confirmText={isLoading ? 'Deleting...' : 'Delete'}
                cancelText="Cancel"
                isLoading={isLoading}>

                <p>Are you sure you want to delete the professor <strong>{professor.email}</strong>?</p>
            </Modal>)}
        </>

    )
}

export default ProfessorDetails