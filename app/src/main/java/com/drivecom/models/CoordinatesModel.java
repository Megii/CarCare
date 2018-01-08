package com.drivecom.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CoordinatesModel {
    public Double lat;
    public Double lon;

    public CoordinatesModel() {
    }

    public CoordinatesModel(Double lat, Double lon) {
        this.lat = lat;
        this.lon = lon;
    }
}
