import React, { createContext, useContext, useState, useCallback } from 'react';

const SidebarContext = createContext();

export const SidebarProvider = ({ children }) => {
    const [sidebarItems, setSidebarItems] = useState([]);

    const updateSidebarItems = useCallback((items) => {
        setSidebarItems(items);
    }, []);

    return (
        <SidebarContext.Provider value={{ sidebarItems, updateSidebarItems }}>
            {children}
        </SidebarContext.Provider>
    );
};

export const useSidebarContext = () => useContext(SidebarContext);
