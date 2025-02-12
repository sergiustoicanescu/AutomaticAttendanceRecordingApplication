import React from 'react'
import SubHeader from '../../ProfessorUI/SubHeader/SubHeader'
import FAQItem from './FAQItem'

const StudentFAQ = () => {
    return (
        <>
            <SubHeader title={"FAQ"} link={"/student/about"} />
            <div className='about-page'>
                <FAQItem question={"How do I mark attendance?"} answer={"Please scan the QR code presented by the professor or enter the codes for the course and session. After pressing 'Enter', you will be directed to the attendance page. There, you will see the session details and a 'GO' button below. Press the button, and a confirmation will appear that your attendance has been marked as present!"} />
                <FAQItem question={"Where are the codes for the course and session?"} answer={"The codes are displayed above the QR code. Ask your professor for more details."} />
                <FAQItem question={"Why do I get a 'Failed to Fetch Session Details' error after scanning the QR code?"} answer={"This means that there isn't an active session corresponding to those course and session codes."} />
                <FAQItem question={"Why does the app ask for my location?"} answer={"We need your location to verify if you are present in the classroom. This is done by comparing the location from your device with the course's saved location. We do not store your location in any way."} />
                <FAQItem question={"Why do I get a 'Location obtained is not highly accurate' message?"} answer={"This means that the location obtained from your device is not highly accurate, and the location verification will probably not work. To remedy this, use a cellphone with GPS and ensure that precise location settings are enabled in your browser or device settings."} />
                <FAQItem question={"Why do I get a 'We couldn't verify your attendance' error?"} answer={"This means that an issue occurred when trying to mark your presence, and it could not be completed. Try to refresh the page, and if this issue persists, inform your professor."} />
                <FAQItem question={"What if I don't want my location to be used?"} answer={"You can opt out at any time, and your attendance will be manually handled by your professor."} />
                <FAQItem question={"Where can I see my attendances?"} answer={"You can view all your attendances on the Statistics page. There, all your courses are listed, and by tapping on a course, you can see all your attendances for each session type."} />
            </div>
        </>
    )
}

export default StudentFAQ