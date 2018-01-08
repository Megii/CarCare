package com.drivecom.utils;


import java.util.ArrayList;

public class InvitationModel {

    private String groupId;
    private String groupName;
    private ArrayList<InternalUserModel> users;

    public InvitationModel(String groupId, String groupName, ArrayList<InternalUserModel> users) {
        this.groupId = groupId;
        this.groupName = groupName;
        this.users = users;
    }

    public String getGroupId() {
        return groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public ArrayList<InternalUserModel> getUsers() {
        return users;
    }
}
