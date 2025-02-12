import React, { useState } from 'react'
import { LuPlus } from "react-icons/lu";
import { FiCheck } from "react-icons/fi";
import fetchWithRedirect from '../../../../fetchUtil';

const ProfessorStatus = {
    IDLE: 'IDLE',
    LOADING: 'LOADING',
    CONFIRMED: 'CONFIRMED',
    ERROR: 'ERROR'
};

const AddProfessorDetails = ({ code, professor, handleAddProfessor }) => {
    const [status, setStatus] = useState(ProfessorStatus.IDLE);

    const addProfessorPost = async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/course/addProfessor/${code}`,
                {
                    method: 'POST',
                    headers: { 'Content-Type': 'text/plain' },
                    body: professor.email
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

    const addProfessor = async () => {
        setStatus(ProfessorStatus.LOADING);
        try {
            await addProfessorPost();
            setTimeout(() => {
                handleAddProfessor(professor.email);
                setStatus(ProfessorStatus.CONFIRMED);
            }, 500);
        } catch (error) {
            setStatus(ProfessorStatus.ERROR);
        }
    }

    return (
        <>
            <div className='edit-students-details'>
                {professor.email}
                {status === ProfessorStatus.IDLE && (
                    <button title='Add professor' onClick={addProfessor} className="edit-session">
                        <LuPlus size={16} />
                    </button>
                )}
                {status === ProfessorStatus.LOADING && (
                    <div className='spinner-professor' />
                )}
                {status === ProfessorStatus.CONFIRMED && (
                    <button title='Added' className="edit-session">
                        <FiCheck size={16} color='green' />
                    </button>
                )}
            </div>
        </>
    )
}

export default AddProfessorDetails