package com.drivecom.models;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.Map;

@IgnoreExtraProperties
public class GroupModel {
    public String name;
    public String owner;
    public Map<String, InvitedModel> invited;
    public String groupId;
    public ArrayList<UserModel> members;

    public GroupModel() {
    }

    @IgnoreExtraProperties
    public static class InvitedModel {
        public Boolean wasSend;
        public Boolean wasAccepted;

        public InvitedModel() {
        }
    }
}
