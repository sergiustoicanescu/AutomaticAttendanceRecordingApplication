import React from 'react'
import SubHeader from '../../ProfessorUI/SubHeader/SubHeader'
import FAQItem from '../../StudentUI/AboutStudent/FAQItem'

const ProfessorFAQ = () => {
    return (
        <>
            <SubHeader title={"FAQ"} link={"/professor/about"} />
            <div className='about-page'>
                <FAQItem question={"Questions, suggestions or bugs?"} answer={"Contact me using this email: sergiu.stoicanescu02@e-uvt.ro"} />
            </div>
        </>
    )
}

export default ProfessorFAQ