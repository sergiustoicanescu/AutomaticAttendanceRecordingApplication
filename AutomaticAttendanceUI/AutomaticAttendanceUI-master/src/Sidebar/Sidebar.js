import { Link } from 'react-router-dom';
import './Sidebar.css'
import { useSidebarContext } from './SidebarProvider';
import { useState, useRef } from 'react';


const SidebarItem = ({ icon, label, to, handleSidebarItemClicked, handleMouseEnter }) => (
    <Link to={to} className="sidebar-item" onClick={handleSidebarItemClicked} onMouseEnter={handleMouseEnter}>
        {icon}
        <span>{label}</span>
    </Link>
);

const Sidebar = ({ isOpen, setSidebarOpen }) => {
    const { sidebarItems } = useSidebarContext();
    const [hoverExpand, setHoverExpand] = useState(false);
    const [itemClicked, setItemClicked] = useState(false);
    const hoverTimeoutRef = useRef(null);

    const handleMouseEnter = () => {
        if (!isOpen) {
            hoverTimeoutRef.current = setTimeout(() => {
                setHoverExpand(true);
            }, 150);
        }
    };

    const handleMouseEnterItem = () => {
        if (itemClicked) {
            setHoverExpand(true);
        }
    };

    const handleMouseLeave = () => {
        if (hoverTimeoutRef.current) {
            clearTimeout(hoverTimeoutRef.current);
        }
        setHoverExpand(false);
        setItemClicked(false)
    };

    const handleSidebarItemClicked = () => {
        if (!isOpen) {
            setHoverExpand(false);
            setItemClicked(true)
        }
        if (window.innerWidth < 768) {
            setSidebarOpen(false);
        }
    };

    return (
        <>
            <div
                className={`${isOpen && window.innerWidth < 768 ? 'sidebar-overlay' : ''}`}
            >
                <div
                    className={`sidebar ${isOpen ? 'open' : 'closed'} ${hoverExpand ? 'hover-expand' : ''}`}
                    onMouseEnter={handleMouseEnter}
                    onMouseLeave={handleMouseLeave}
                >
                    {sidebarItems.map((item, index) => {
                        if (item.isDivider) {
                            return <div className="sidebar-divider" key={index}></div>;
                        } else {
                            return (
                                <SidebarItem
                                    isOpen={isOpen}
                                    icon={item.icon}
                                    label={item.label}
                                    to={item.to}
                                    handleSidebarItemClicked={handleSidebarItemClicked}
                                    key={index}
                                    handleMouseEnter={handleMouseEnterItem}
                                />
                            );
                        }
                    })}
                </div>
            </div >
        </>
    );
};

export default Sidebar;
