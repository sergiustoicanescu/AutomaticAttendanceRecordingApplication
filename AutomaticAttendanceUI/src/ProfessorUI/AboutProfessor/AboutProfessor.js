import React from 'react'
import SubHeader from '../SubHeader/SubHeader';
import AboutItems from '../../StudentUI/AboutStudent/AboutItems';
import { FaQuestion } from "react-icons/fa";

const AboutProfessor = () => {
    return (
        <>
            <SubHeader title={"About"} />
            <div className='about-item-container'>
                <AboutItems icon={<FaQuestion size={22} />} title={"FAQ"} text={"Frequently Asked Questions"} link={"/professor/about/faq"} />
            </div>
        </>
    );
};

export default AboutProfessor;