import './Popup.css';
import { useEffect, useState } from 'react';
import LoadingSpinner from '../../Loading/LoadingSpinner';

const Popup = ({ isLoading, message, status }) => {
    const [phase, setPhase] = useState('loading');

    useEffect(() => {
        let timeoutId;
        if (!isLoading) {
            timeoutId = setTimeout(() => {
                setPhase('showMessage');
            }, 500);
        }
        return () => clearTimeout(timeoutId);
    }, [isLoading]);

    return (
        <div className="popup-overlay">
            <div className={`popup ${phase === 'loading' ? 'loading' : 'loaded'} ${status}`}>
                {phase === 'loading' && <LoadingSpinner />}
                {phase === 'showMessage' && (
                    <div className="popup-content">
                        <h4>{status === 'success' ? 'Success' : 'Failed'}</h4>
                        <p>{message}</p>
                    </div>
                )}
            </div>
        </div>
    );
};

export default Popup;
