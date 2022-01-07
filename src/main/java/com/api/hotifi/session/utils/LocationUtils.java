package com.api.hotifi.session.utils;

import com.api.hotifi.session.entity.Session;

import java.util.List;
import java.util.stream.Collectors;

public class LocationUtils {

    //Haversine Formula
    public static double calculateHaversineDistance(double longitude1, double latitude1, double longitude2, double latitude2) {
        longitude1 = Math.toRadians(longitude1);
        longitude2 = Math.toRadians(longitude2);
        latitude1 = Math.toRadians(latitude1);
        latitude2 = Math.toRadians(latitude2);

        // Haversine formula
        double longitudeDistance = longitude2 - longitude1;
        double latitudeDistance = latitude2 - latitude1;
        double altitude = Math.pow(Math.sin(latitudeDistance / 2), 2) + Math.cos(latitude1) * Math.cos(latitude2) * Math.pow(Math.sin(longitudeDistance / 2), 2);
        double circumference = 2 * Math.asin(Math.sqrt(altitude));

        // Radius of earth in kilometers
        double radius = 6371;
        // calculate the result
        return Math.round(circumference * radius * 100.0) / 100.0;
    }

    public static List<Session> getNearbyActiveSessions(List<Session> sessions, double buyerLongitude, double buyerLatitude, int nearbySessionsLimit) {

        return sessions
                .stream()
                .sorted((session1, session2) -> {
                    double d1 = calculateHaversineDistance(session1.getLongitude(), session1.getLatitude(), buyerLongitude, buyerLatitude);
                    double d2 = calculateHaversineDistance(session2.getLongitude(), session2.getLatitude(), buyerLongitude, buyerLatitude);
                    return (int) d1 - (int) d2;
                }).limit(nearbySessionsLimit)
                .collect(Collectors.toList());
    }

}
