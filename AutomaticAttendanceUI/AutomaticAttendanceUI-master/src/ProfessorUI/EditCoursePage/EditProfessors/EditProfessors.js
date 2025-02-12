import React, { useState } from 'react';
import ProfessorDetails from './ProfessorDetails';
import AddProfessors from './AddProfessors/AddProfessors';

const EditProfessors = ({ user, code, professors, owner, handleRemoveProfessor, handleAddProfessor }) => {
    const [searchTerm, setSearchTerm] = useState('');
    const [showEditModal, setShowEditModal] = useState(false);

    const handleSearchChange = (event) => {
        setSearchTerm(event.target.value);
    };

    const filteredProfessors = professors.filter(professor =>
        professor.email.toLowerCase().includes(searchTerm.toLowerCase())
    );



    return (
        <>
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
                        <ProfessorDetails key={index} user={user} code={code} professor={professor} owner={owner} handleRemoveProfessor={handleRemoveProfessor} />
                    ))
                ) : (
                    <div className='no-students-message'>
                        No professors found.
                    </div>
                )}
            </div>
            <>
                {user.email === owner && (
                    <button className='edit-add-session-btn' onClick={(e) => { setShowEditModal(true) }}>Add Professors</button>
                )}
            </>
            {showEditModal && (<AddProfessors code={code} isOpen={showEditModal} onClose={() => setShowEditModal(false)} handleAddProfessor={handleAddProfessor} />)}
        </>
    );
};

export default EditProfessors;
