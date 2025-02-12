package com.example.dto.Location;

public class UserLocationDTO {
    private LocationDTO userLocation;
    private String email;

    public LocationDTO getUserLocation() {
        return userLocation;
    }

    public void setUserLocation(LocationDTO userLocation) {
        this.userLocation = userLocation;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
