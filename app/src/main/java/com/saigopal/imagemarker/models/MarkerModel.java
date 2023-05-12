package com.saigopal.imagemarker.models;

import com.google.firebase.Timestamp;

import java.util.ArrayList;

public class MarkerModel {

    String markerType;
    String markerId;
    String markerContent;
    Timestamp timestamp ;
    ArrayList<Long> points ;
    String imageId;

    public MarkerModel(String markerType, String markerId, String markerContent, Timestamp timestamp, ArrayList<Long> points, String imageId) {
        this.markerType = markerType;
        this.markerId = markerId;
        this.markerContent = markerContent;
        this.timestamp = timestamp;
        this.points = points;
        this.imageId = imageId;
    }

    public String getMarkerType() {
        return markerType;
    }

    public void setMarkerType(String markerType) {
        this.markerType = markerType;
    }

    public String getMarkerId() {
        return markerId;
    }

    public void setMarkerId(String markerId) {
        this.markerId = markerId;
    }

    public String getMarkerContent() {
        return markerContent;
    }

    public void setMarkerContent(String markerContent) {
        this.markerContent = markerContent;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public ArrayList<Long> getPoints() {
        return points;
    }

    public void setPoints(ArrayList<Long> points) {
        this.points = points;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
