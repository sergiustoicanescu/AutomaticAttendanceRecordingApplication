package com.example.utils;

public class LocationUtil {

    private static final double EARTH_RADIUS_KM = 6371.0;

    public static double calculateDistance(double startLat, double startLong,
                                           double endLat, double endLong) {

        double dLat = Math.toRadians(endLat - startLat);
        double dLong = Math.toRadians(endLong - startLong);

        startLat = Math.toRadians(startLat);
        endLat = Math.toRadians(endLat);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.sin(dLong / 2) * Math.sin(dLong / 2) * Math.cos(startLat) * Math.cos(endLat);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
