package com.drivecom.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;

@IgnoreExtraProperties
public class UserModel {
    public String id;
    public String color;
    public CoordinatesModel coords;
    public String model;
    public ArrayList<UserModel> nearby;
    public String nr;
    public String token;
    public String groupId;

    public UserModel(String id, String color, CoordinatesModel coords, String model, ArrayList<UserModel> nearby, String nr, String token, String groupId) {
        this.id = id;
        this.color = color;
        this.coords = coords;
        this.model = model;
        this.nearby = nearby;
        this.nr = nr;
        this.token = token;
        this.groupId = groupId;
    }

    public UserModel() {

    }
}
