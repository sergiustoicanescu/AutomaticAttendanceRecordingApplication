import React, { useState, useEffect } from 'react';
import Map from './Map';
import './LocationModal.css'

const googleMapsApiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

const LocationModal = ({ isOpen, onClose, initialLocation, onSave }) => {
    const [address, setAddress] = useState('');
    const [isLoadingAddress, setIsLoadingAddress] = useState(false);
    const [searchUsed, setSearchUsed] = useState(false);
    const [selectedLocation, setSelectedLocation] = useState({
        lat: 0,
        lng: 0
    });
    const [noLocationSelected, setNoLocationSelected] = useState(false);



    useEffect(() => {
        setSelectedLocation({
            lat: parseFloat(initialLocation?.lat) || 45.7471195,
            lng: parseFloat(initialLocation?.lng) || 21.2316152
        });
    }, [isOpen, initialLocation]);

    useEffect(() => {
        const fetchAddress = async () => {
            if (!selectedLocation.lat || !selectedLocation.lng || searchUsed) return;
            setIsLoadingAddress(true);
            const apiURL = `https://maps.googleapis.com/maps/api/geocode/json?latlng=${selectedLocation.lat},${selectedLocation.lng}&key=${googleMapsApiKey}`;
            try {
                const response = await fetch(apiURL);
                const data = await response.json();
                if (data.status === 'OK' && data.results[0]) {
                    setAddress(data.results[0].formatted_address);
                } else {
                    setAddress('Unable to fetch address');
                }
            } catch (error) {
                console.error('Geocoding error:', error);
                setAddress('Error fetching address');
            } finally {
                setIsLoadingAddress(false);
            }
        };

        if (isOpen) {
            fetchAddress();
        }
    }, [selectedLocation, isOpen, searchUsed]);

    const handleMarkerDrag = (newCoordinates) => {
        setSelectedLocation(newCoordinates);
        if (searchUsed) {
            setSearchUsed(false);
        }
    };

    const handleAddressChange = (e) => {
        setAddress(e.target.value);
    };

    const handleSearchAddress = async () => {
        setIsLoadingAddress(true);
        const apiURL = `https://maps.googleapis.com/maps/api/geocode/json?address=${encodeURIComponent(address)}&key=${googleMapsApiKey}`;
        try {
            const response = await fetch(apiURL);
            const data = await response.json();
            if (data.status === 'OK' && data.results[0]) {
                const { lat, lng } = data.results[0].geometry.location;
                setSelectedLocation({ lat, lng });
                setAddress(data.results[0].formatted_address);
                setSearchUsed(true);
            } else {
                setAddress('Unable to fetch location');
            }
        } catch (error) {
            console.error('Geocoding error:', error);
            setAddress('Error fetching location');
        } finally {
            setIsLoadingAddress(false);
        }
    };

    return (
        <div className={`location-modal ${isOpen ? 'open' : ''}`}>
            <div className="location-modal-content">
                {initialLocation && (<h2 className='location-title'> Change location</h2>)}
                {!initialLocation && (<h2 className='location-title'>Select Location | No location saved</h2>)}
                <div className="location-search-container">
                    <input
                        className="location-search-input"
                        type="text"
                        value={address}
                        onChange={handleAddressChange}
                        placeholder="Enter a location"
                        disabled={isLoadingAddress}
                        onClick={(e) => e.target.select()}
                    />
                    <button
                        className="location-search-button"
                        onClick={handleSearchAddress}
                        disabled={isLoadingAddress}
                    >
                        Search
                    </button>
                </div>
                <Map
                    className='location-modal-map'
                    center={selectedLocation}
                    onMarkerDrag={handleMarkerDrag}
                />
                <div className="location-no-location-container">
                    <input
                        className="location-no-location-checkbox"
                        type="checkbox"
                        checked={noLocationSelected}
                        onChange={(e) => setNoLocationSelected(e.target.checked)}
                    />
                    <label className='location-no-location-label'>Select no location</label>
                </div>
                <div className="location-modal-actions">
                    <button className='cancel' onClick={() => onClose()}>Cancel</button>
                    <button className='confirm' onClick={() => onSave(noLocationSelected ? null : selectedLocation)}>Save</button>
                </div>
            </div>
        </div>
    );
};

export default LocationModal;
