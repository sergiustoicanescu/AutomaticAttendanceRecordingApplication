import React from 'react'
import { useNavigate } from 'react-router-dom'
import { IoArrowBackCircleOutline } from "react-icons/io5";
import './SubHeader.css'

const SubHeader = ({ title, link, children }) => {
    const navigate = useNavigate();

    return (
        <>
            <div className='sub-header'>
                <div className="sub-header-title">
                    {link && (
                        <div className='back' onClick={e => navigate(link)}>
                            <IoArrowBackCircleOutline size={35} />
                        </div>)}
                    <h2 className="sub-header-name">{title}</h2>
                </div>
                {children && (
                    <div className="course-actions">
                        {children}
                    </div>)}
            </div>
            <div className="sub-header-ghost"></div>
        </>
    )
}

export default SubHeader