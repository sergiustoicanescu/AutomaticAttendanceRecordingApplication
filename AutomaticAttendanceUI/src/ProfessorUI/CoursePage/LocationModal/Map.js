import React from 'react';
import { GoogleMap, MarkerF, LoadScriptNext } from '@react-google-maps/api';

const containerStyle = {
    width: '95%',
    height: '60%',
};

const googleMapsApiKey = process.env.REACT_APP_GOOGLE_MAPS_API_KEY;

const Map = ({ center, onMarkerDrag }) => {
    const onMarkerDragEnd = (event) => {
        const newLat = event.latLng.lat();
        const newLng = event.latLng.lng();
        onMarkerDrag({ lat: newLat, lng: newLng });
    };

    return (
        <LoadScriptNext googleMapsApiKey={googleMapsApiKey}>
            <GoogleMap
                mapContainerStyle={containerStyle}
                center={center}
                zoom={17}
            >
                <MarkerF
                    position={center}
                    draggable={true}
                    onDragEnd={onMarkerDragEnd}
                />
            </GoogleMap>
        </LoadScriptNext>
    );
};

export default Map;
