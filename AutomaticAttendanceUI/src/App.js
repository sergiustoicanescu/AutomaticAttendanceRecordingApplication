import Header from './Header/Header';
import { Route, Routes, useNavigate, useLocation } from 'react-router-dom';
import { useState, useEffect, useLayoutEffect } from 'react';
import StudentUI from './StudentUI/StudentUI';
import ProfessorUI from './ProfessorUI/ProfessorUI';
import LoadingSpinner from './Loading/LoadingSpinner';
import Sidebar from './Sidebar/Sidebar';
import { SidebarProvider } from './Sidebar/SidebarProvider';
import fetchWithRedirect from './fetchUtil';
import { SERVER_URL } from './constant';

function App() {
  const navigate = useNavigate();
  const location = useLocation();
  const [user, setUser] = useState();
  const [isLoading, setIsLoading] = useState(true);
  const [sidebarOpen, setSidebarOpen] = useState(true);
  const screenWidth = window.innerWidth;

  useEffect(() => {
    if (screenWidth < 768) {
      setSidebarOpen(false);
    }
  }, [screenWidth])


  const getUser = async () => {
    try {
      const response = await fetchWithRedirect(`/user/auth/status`);
      if (!response) {
        return;
      }
      const data = await response.json();
      setUser(data)
      setIsLoading(false);
    } catch (error) {
      console.log(error)
    }

  }

  useEffect(() => {
    getUser();
  }, []);

  useLayoutEffect(() => {
    if (isLoading) {
      return;
    }

    const currentPath = location.pathname;
    const baseRolePath = user?.role === 'STUDENT' ? '/student' : '/professor';

    if (!currentPath.startsWith(baseRolePath)) {
      navigate(baseRolePath);
    }
  }, [user, isLoading, location, navigate]);

  const handleLogout = async () => {
    setIsLoading(true);

    try {
      const response = await fetchWithRedirect(
        `/user/logout`,
        {
          method: 'POST',
        });
      if (!response) {
        return;
      }
      if (response.ok) {
        setUser(null);
        window.location.href = SERVER_URL + '/logout';
      }
    } catch (error) {
      console.error('Logout failed', error);
    }
  };

  if (isLoading) {
    return <LoadingSpinner />;
  }

  return (
    <SidebarProvider>
      <div className="App">
        <Header
          title="Automatic Attendance"
          user={user}
          onLogout={handleLogout}
          sidebarOpen={sidebarOpen}
          setSidebarOpen={setSidebarOpen}
        />
        <Sidebar isOpen={sidebarOpen} setSidebarOpen={setSidebarOpen} />
        <div className={`content ${sidebarOpen ? 'sidebar-open' : ''}`}>
          <Routes>
            <Route path="/student/*" element={<StudentUI user={user} />} />
            <Route path="/professor/*" element={<ProfessorUI user={user} />} />
          </Routes>
        </div>
      </div>
    </SidebarProvider>
  );
}

export default App;

