package com.drivecom.utils;

import android.os.Parcel;
import android.os.Parcelable;

public class InternalUserModel implements Parcelable {

    private String name;
    private String id;
    private String registrationId;
    private String token;
    private String groupId;
    private Boolean wasSend;
    private Boolean wasAccepted;

    public InternalUserModel(String name, String id, String registrationId, String token, String groupId) {
        this.name = name;
        this.id = id;
        this.registrationId = registrationId;
        this.token = token;
        this.groupId = groupId;
    }

    public InternalUserModel(String name, String id, String registrationId, String token, String groupId, Boolean wasSend, Boolean wasAccepted) {
        this(name, id, registrationId, token, groupId);
        this.wasSend = wasSend;
        this.wasAccepted = wasAccepted;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getRegistrationId() {
        return registrationId;
    }

    public String getToken() {
        return token;
    }

    public String getGroupId() {
        return groupId;
    }

    public Boolean getWasAccepted() {
        return wasAccepted;
    }

    public Boolean getWasSend() {
        return wasSend;
    }

    public void setWasSend(Boolean wasSend) {
        this.wasSend = wasSend;
    }

    public void setWasAccepted(Boolean wasAccepted) {
        this.wasAccepted = wasAccepted;
    }

    protected InternalUserModel(Parcel in) {
        name = in.readString();
        id = in.readString();
        registrationId = in.readString();
        token = in.readString();
        groupId = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(id);
        dest.writeString(registrationId);
        dest.writeString(token);
        dest.writeString(groupId);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<InternalUserModel> CREATOR = new Parcelable.Creator<InternalUserModel>() {
        @Override
        public InternalUserModel createFromParcel(Parcel in) {
            return new InternalUserModel(in);
        }

        @Override
        public InternalUserModel[] newArray(int size) {
            return new InternalUserModel[size];
        }
    };
}