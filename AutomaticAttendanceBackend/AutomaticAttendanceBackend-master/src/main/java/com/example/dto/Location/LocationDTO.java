package com.example.dto.Location;

public class LocationDTO {
    private String lat;
    private String lng;

    public LocationDTO(String lat, String lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public LocationDTO(String strLocation) {
        if(strLocation != null) {
            String[] location = strLocation.split(" ");
            this.lat = location[0];
            this.lng = location[1];
        }
    }

    public String getLat() {
        return lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}
