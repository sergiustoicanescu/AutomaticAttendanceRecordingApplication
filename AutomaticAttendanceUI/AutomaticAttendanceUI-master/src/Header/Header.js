import React, { useEffect, useState, useRef } from 'react';
import { Link } from 'react-router-dom';
import { RxHamburgerMenu } from "react-icons/rx";
import './Header.css'

const Header = ({ title, user, onLogout, sidebarOpen, setSidebarOpen }) => {
    const [open, setOpen] = useState(false);
    let menuRef = useRef();

    useEffect(() => {
        let handler = (e) => {
            if (!menuRef.current.contains(e.target)) {
                setOpen(false);
            }
        };

        document.addEventListener("mousedown", handler);


        return () => {
            document.removeEventListener("mousedown", handler);
        }

    });

    return (
        <header className="Header">
            <div className="header-content" >
                <button className="sidebar-trigger" onClick={() => setSidebarOpen(!sidebarOpen)}>
                    <RxHamburgerMenu size={24} />
                </button>
                <Link className="headerTitle" to={user.role === 'PROFESSOR' ? "/professor" : "/student"}>{title}</Link>
            </div>
            <div className='user-menu' ref={menuRef}>
                <div className={`dropdown-menu ${open ? 'active' : 'inactive'}`}>
                    <h3 className='user-name'>{user.name}<br /><span className='user-role'>{user.role}</span></h3>
                    <ul>
                        <DropdownItem onClick={onLogout} img="/img/log-out.png" text="Logout" />
                    </ul>
                </div>
                <img className="dropdown-trigger" src={user.photo} onClick={() => setOpen(!open)} alt={user.name} referrerPolicy="no-referrer" />
            </div>
        </header>

    );
};

function DropdownItem(props) {
    return (
        <div className='dropdownItem'>
            <img src={props.img} alt={props.text}></img>
            <button onClick={props.onClick} href={props.text}>{props.text}</button>
        </div>
    );
}

export default Header;
