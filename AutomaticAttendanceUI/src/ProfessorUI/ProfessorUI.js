import React, { useState, useEffect, useCallback } from 'react';
import { Route, Routes } from 'react-router-dom';
import Home from "./Home/Home";
import Missing from '../Missing';
import CreateCourse from './CreateCourse/CreateCourse';
import CoursePage from './CoursePage/CoursePage';
import EditCoursePage from './EditCoursePage/EditCoursePage';
import { useSidebarContext } from '../Sidebar/SidebarProvider';
import { AiOutlineHome } from "react-icons/ai";
import { GoPlusCircle } from "react-icons/go";
import { IoIosInformationCircleOutline } from "react-icons/io";
import CourseLogo from './CourseLogo/CourseLogo';
import fetchWithRedirect from '../fetchUtil';
import SessionPage from './SessionPage/SessionPage';
import AboutProfessor from './AboutProfessor/AboutProfessor';
import ProfessorFAQ from './AboutProfessor/ProfessorFAQ';


const ProfessorUI = ({ user }) => {
  const [isLoading, setIsLoading] = useState(true);
  const [courses, setCourses] = useState([]);
  const { updateSidebarItems } = useSidebarContext();

  const fetchCourses = useCallback(async () => {
    try {
      const response = await fetchWithRedirect(`/professor/courses/${user.id}`,);
      if (!response) {
        return;
      }
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }

      const data = await response.json();
      setCourses(data);
      setIsLoading(false);

    } catch (error) {
      console.error('There was a problem with the fetch operation:', error);
      setIsLoading(false);
    }
  }, [user]);

  useEffect(() => {
    fetchCourses();
  }, [fetchCourses]);


  useEffect(() => {
    const staticItems = [
      { icon: <AiOutlineHome />, label: "Home", to: "/professor" },
      { icon: <GoPlusCircle />, label: "Create Course", to: "/professor/create-course" }
    ];

    const courseItems = courses.length > 0 ? [{ isDivider: true }] : [];

    courses.forEach(course => {
      const trimmedLabel = course.name.length > 20 ? `${course.name.slice(0, 20)}...` : course.name;
      courseItems.push({
        icon: <CourseLogo name={course.name} />,
        label: trimmedLabel,
        to: `/professor/course/${course.code}`,
      });
    });

    const aboutItem = [
      { isDivider: true },
      { icon: <IoIosInformationCircleOutline />, label: "About", to: "/professor/about" }
    ];

    updateSidebarItems([...staticItems, ...courseItems, ...aboutItem]);
  }, [courses, updateSidebarItems]);




  return (
    <>
      <Routes>
        <Route index element={<Home user={user} courses={courses} setCourses={setCourses} isLoading={isLoading} setIsLoading={setIsLoading} />} />
        <Route path="create-course" element={<CreateCourse user={user} courses={courses} setCourses={setCourses} />} />
        <Route path="/course/:code" element={<CoursePage user={user} />} />
        <Route path="/course/:code/edit" element={<EditCoursePage user={user} setCourses={setCourses} />} />
        <Route path="/course/:code/session" element={<SessionPage user={user} />} />
        <Route path="/about" element={<AboutProfessor />} />
        <Route path="/about/faq" element={<ProfessorFAQ />} />
        <Route path="*" element={<Missing role={"professor"} />} />
      </Routes>
    </>
  );
};

export default ProfessorUI;
