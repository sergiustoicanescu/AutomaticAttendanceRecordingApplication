import React from 'react'
import './AboutStudent.css'
import { useNavigate } from 'react-router-dom';

const AboutItems = ({ icon, title, text, link }) => {
    const navigate = useNavigate();

    return (
        <div class="about-item" onClick={() => { navigate(link); }}>
            <span class="about-item-icon">{icon}</span>
            <div class="about-item-text">
                <h2>{title}</h2>
                <p>{text}</p>
            </div>
        </div>
    )
}

export default AboutItems