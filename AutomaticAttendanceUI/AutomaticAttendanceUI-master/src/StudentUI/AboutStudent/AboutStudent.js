import React from 'react';
import './AboutStudent.css';
import SubHeader from '../../ProfessorUI/SubHeader/SubHeader';
import AboutItems from './AboutItems';
import { FaQuestion } from "react-icons/fa";
import { IoWarningOutline } from "react-icons/io5";

const AboutStudent = () => {
    return (
        <>
            <SubHeader title={"About"} />
            <div className='about-item-container'>
                <AboutItems icon={<FaQuestion size={22} />} title={"FAQ"} text={"Frequently Asked Questions"} link={"/student/about/faq"} />
                <AboutItems icon={<IoWarningOutline size={25} />} title={"Disclaimer"} text={"Disclaimer for the application"} link={"/student/about/disclaimer"} />
            </div>
        </>
    );
};

export default AboutStudent;
