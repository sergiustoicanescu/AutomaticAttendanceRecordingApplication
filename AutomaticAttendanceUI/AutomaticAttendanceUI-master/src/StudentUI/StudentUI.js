import React, { useEffect } from 'react'
import { Route, Routes } from 'react-router-dom';
import { useSidebarContext } from '../Sidebar/SidebarProvider';
import { TbChartBar } from "react-icons/tb";
import { AiOutlineHome } from "react-icons/ai";
import { IoIosInformationCircleOutline } from "react-icons/io";
import StudentHome from './StudentHome/StudentHome';
import AttendancePage from './AttendancePage/AttendancePage';
import Statistics from './Statistics/Statistics';
import AboutStudent from './AboutStudent/AboutStudent';
import Missing from '../Missing';
import StudentFAQ from './AboutStudent/StudentFAQ';
import StudentDisclaimer from './AboutStudent/StudentDisclaimer';

const StudentUI = ({ user }) => {
    const { updateSidebarItems } = useSidebarContext();

    useEffect(() => {
        const sidebarItems = [
            { icon: <AiOutlineHome />, label: "Home", to: "/student" },
            { icon: <TbChartBar />, label: "Statistics", to: "/student/statistics" },
            { isDivider: true },
            { icon: <IoIosInformationCircleOutline />, label: "About", to: "/student/about" }
        ];

        updateSidebarItems([...sidebarItems]);
    }, [updateSidebarItems]);

    return (
        <>
            <Routes>
                <Route index element={<StudentHome />} />
                <Route path="/attendance/:courseCode/:sessionCode" element={<AttendancePage user={user} />} />
                <Route path="/statistics" element={<Statistics user={user} />} />
                <Route path="/about" element={<AboutStudent />} />
                <Route path="/about/faq" element={<StudentFAQ />} />
                <Route path="/about/disclaimer" element={<StudentDisclaimer />} />
                <Route path="*" element={<Missing role={"student"} />} />
            </Routes>
        </>
    )
}

export default StudentUI