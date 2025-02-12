import React from 'react'
import './AboutStudent.css';

const FAQItem = ({ question, answer }) => {
    return (
        <div className="about-content">
            <h3>{question}</h3>
            <p>{answer}</p>
        </div>
    )
}

export default FAQItem