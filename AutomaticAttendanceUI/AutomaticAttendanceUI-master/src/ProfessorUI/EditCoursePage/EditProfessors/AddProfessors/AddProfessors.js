import React, { useState, useEffect, useCallback } from 'react'
import './AddProfessors.css'
import InfoModal from '../../../../InfoModal/InfoModal'
import LoadingSpinner from '../../../../Loading/LoadingSpinner'
import fetchWithRedirect from '../../../../fetchUtil'
import AddProfessorDetails from './AddProfessorDetails'

const AddProfessors = ({ code, isOpen, onClose, handleAddProfessor }) => {
    const [existingProfessors, setExistingProfessors] = useState([]);
    const [searchTerm, setSearchTerm] = useState('');
    const [isLoading, setIsLoading] = useState(true);

    const fetchExistingProfessors = useCallback(async () => {
        try {
            const response = await fetchWithRedirect(
                `/professor/course/existingProfessors/${code}`,
            );
            if (!response) {
                return;
            }
            if (!response.ok) {
                throw new Error('Network response was not ok');
            }

            const data = await response.json();
            setExistingProfessors(data);
            setIsLoading(false);
        } catch (error) {
            console.error('There was a problem with the fetch operation:', error);
        }
    }, [code]);

    useEffect(() => {
        fetchExistingProfessors();
    }, [fetchExistingProfessors]);

    const handleSearchChange = (event) => {
        setSearchTerm(event.target.value);
    };

    const filteredProfessors = existingProfessors.filter(professor =>
        professor.email.toLowerCase().includes(searchTerm.toLowerCase())
    );


    if (isLoading) {
        return <LoadingSpinner />;
    }

    return (
        <>
            <InfoModal
                isOpen={isOpen}
                onClose={onClose}
            >
                <div className='add-professors-container'>
                    <p>Add Professors</p>
                    <div className="search-students-bar">
                        <input
                            type="text"
                            placeholder="Search professors..."
                            value={searchTerm}
                            onChange={handleSearchChange}
                            className="search-students-input"
                        />
                    </div>
                    <div className='students-list'>
                        {filteredProfessors.length > 0 ? (
                            filteredProfessors.map((professor, index) => (
                                <AddProfessorDetails key={index} code={code} professor={professor} handleAddProfessor={handleAddProfessor} />
                            ))
                        ) : (
                            <div className='no-students-message'>
                                No professors that are not already in this course found.
                            </div>
                        )}
                    </div>
                </div>
            </InfoModal >
        </>
    )
}

export default AddProfessors