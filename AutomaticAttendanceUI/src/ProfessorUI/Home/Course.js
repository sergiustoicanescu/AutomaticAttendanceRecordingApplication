import React, { useState, useRef, useEffect } from 'react';
import { Link } from 'react-router-dom';
import Modal from '../../Modal/Modal';
import fetchWithRedirect from '../../fetchUtil';

const Course = ({ user, course, setCourses }) => {
    const shouldShowTooltip = course.name.length > 20;
    const displayName = shouldShowTooltip ? `${course.name.slice(0, 20)}...` : course.name;
    const [showModal, setShowModal] = useState(false);
    const [modalMode, setModalMode] = useState('');
    const [editedName, setEditedName] = useState(course.name);
    const [open, setOpen] = useState(false);
    const [isLoading, setIsLoading] = useState(false);
    const [operationText, setOperationText] = useState('');

    let menuRef = useRef();

    useEffect(() => {
        let handler = (e) => {
            if (!menuRef.current.contains(e.target)) {
                setOpen(false);
            }
        };
        document.addEventListener("mousedown", handler);
        return () => {
            document.removeEventListener("mousedown", handler);
        };
    }, []);

    const handleActionClick = (action) => {
        setModalMode(action);
        setShowModal(true);
        setOpen(false);
    };

    const deleteCourse = async (courseCode) => {
        try {
            const response = await fetchWithRedirect(
                `/professor/deleteCourse/${courseCode}`,
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
            throw error;
        }
    };

    const editCourse = async (courseCode, newName) => {
        try {
            const response = await fetchWithRedirect(
                `/professor/updateCourse/name/${courseCode}`,
                {
                    method: 'PATCH',
                    body: { name: newName }
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

    const handleEdit = async (e) => {
        setIsLoading(true);
        setOperationText('Saving...');
        try {
            await editCourse(course.code, editedName);
            setTimeout(() => {
                setIsLoading(false);
                setShowModal(false);
                setOperationText('');
                const courseId = course.id;
                setCourses(prevCourses => prevCourses.map(course =>
                    course.id === courseId ? { ...course, name: editedName } : course
                ));
            }, 500);
        } catch (error) {
            setIsLoading(false);
            alert(error.message);
        }
    };

    const handleDelete = async () => {
        setIsLoading(true);
        setOperationText('Deleting...');
        try {
            await deleteCourse(course.code);
            setTimeout(() => {
                setIsLoading(false);
                setShowModal(false);
                setOperationText('');
                const courseId = course.id;
                setCourses(prevCourses => prevCourses.filter(course => course.id !== courseId));
            }, 500);
        } catch (error) {
            setIsLoading(false);
            alert(error.message);
        }
    };

    const getModalContent = () => {
        switch (modalMode) {
            case 'edit':
                return {
                    title: "Edit Course",
                    children: (
                        <>
                            <label>Change the course name:</label>
                            <input
                                type="text"
                                value={editedName}
                                onChange={(e) => setEditedName(e.target.value)}
                                disabled={isLoading}
                            />
                        </>
                    ),
                    confirmAction: isLoading ? null : (e) => { e.preventDefault(); handleEdit() }
                };
            case 'delete':
                return {
                    title: "Confirm Delete",
                    children: <p>Are you sure you want to delete the course <strong>{course.name}</strong>?</p>,
                    confirmAction: isLoading ? null : (e) => { e.preventDefault(); handleDelete() }
                };
            default:
                return null;
        }
    };

    const modalProps = getModalContent();

    return (
        <>
            <article className='course' title={shouldShowTooltip ? course.name : ""}>
                <Link to={`course/${course.code}`}>
                    <h2>{displayName}</h2>
                </Link>
                <>
                    {user.email !== course.professor.email && (
                        <div className="home-professor-name">
                            <span>{course.professor.firstName} {course.professor.lastName}</span>
                        </div>
                    )}
                </>
                <div ref={menuRef}>
                    <>  {user.email === course.professor.email && (
                        <button className="three-dots-button" onClick={() => setOpen(!open)}>&#x22EE;</button>
                    )}
                    </>
                    <div className={`dropdown-menu-course ${open ? 'active' : 'inactive'}`} >
                        <button onClick={() => handleActionClick('edit')}>Edit</button>
                        <button onClick={() => handleActionClick('delete')}>Delete</button>
                    </div>
                </div>
            </article>
            {showModal && modalProps && (
                <Modal
                    isOpen={showModal}
                    onClose={() => setShowModal(false)}
                    onConfirm={modalProps.confirmAction}
                    title={modalProps.title}
                    confirmText={isLoading ? operationText : (modalMode === 'delete' ? "Delete" : "Save")}
                    cancelText="Cancel"
                    isLoading={isLoading}
                >
                    {modalProps.children}
                </Modal>
            )}
        </>
    );
};

export default Course;
