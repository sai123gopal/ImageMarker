package com.saigopal.imagemarker.models;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import com.google.firebase.Timestamp;

import java.io.Serializable;

public class ImageItemsModel {

    String name;
    String imageUrl;
    String userId;
    Timestamp timestamp;
    Long markers;
    String id;

    public ImageItemsModel(String name, String imageUrl, String userId, Timestamp timestamp, Long markers, String id) {
        this.name = name;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.timestamp = timestamp;
        this.markers = markers;
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Long getMarkers() {
        return markers;
    }

    public void setMarkers(Long markers) {
        this.markers = markers;
    }

}
