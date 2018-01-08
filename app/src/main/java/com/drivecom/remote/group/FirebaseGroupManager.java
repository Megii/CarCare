package com.drivecom.remote.group;

import android.support.annotation.NonNull;
import android.util.Log;

import com.drivecom.models.GroupModel;
import com.drivecom.models.UserModel;
import com.drivecom.remote.FirebaseUserDataProvider;
import com.drivecom.utils.InternalUserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebaseGroupManager implements GroupManager {

    private final DatabaseReference reference;
    private DatabaseReference groupReference;
    private final Map<MemberChangedCallback, ChildEventListener> currentMembersListeners;
    private final Map<DeleteCallback, ValueEventListener> currentDeletedListeners;
    private final Map<InvitationChangesCallback, ValueEventListener> currentInvitedListeners;

    public FirebaseGroupManager() {
        reference = FirebaseDatabase.getInstance().getReference().child("groups");
        currentMembersListeners = new HashMap<>();
        currentDeletedListeners = new HashMap<>();
        currentInvitedListeners = new HashMap<>();
    }

    @Override
    public GroupModel createGroup(String name, UserModel currentUser, ArrayList<UserModel> members) throws IllegalStateException {
        log("createGroup invoked!");
        String uniqueKey = reference.push().getKey();
        GroupModel groupModel = new GroupModel();
        String userId = getUserId();
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        ArrayList<UserModel> membersToSet = new ArrayList<>();
        membersToSet.add(currentUser);
        groupModel.owner = userId;
        groupModel.name = name;
        groupModel.invited = generateInvitedCollection(getUserModelIdsList(members));
        groupModel.groupId = uniqueKey;
        groupModel.members = membersToSet;
        reference.child(uniqueKey).setValue(groupModel);
        new FirebaseUserDataProvider().joinGroup(uniqueKey);
        groupReference = reference.child(uniqueKey);
        return groupModel;
    }

    private List<String> getUserModelIdsList(List<UserModel> users) {
        List<String> result = new ArrayList<>(users.size());
        for (UserModel user : users) {
            result.add(user.id);
        }
        return result;
    }

    private Map<String, GroupModel.InvitedModel> generateInvitedCollection(List<String> invitedUsersIds) {
        Map<String, GroupModel.InvitedModel> result = new HashMap<>();
        for (String userId : invitedUsersIds) {
            GroupModel.InvitedModel invitedModel = new GroupModel.InvitedModel();
            invitedModel.wasSend = false;
            result.put(userId, invitedModel);
        }
        return result;
    }

    @Override
    public void setCurrentGroupId(String groupId) {
        groupReference = reference.child(groupId);
    }

    private String getUserId() {
        try {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    @Override
    public void deleteGroup(final DeleteCallback callback) throws IllegalStateException {
        log("deleteGroup started!");
        if (groupReference != null) {
            groupReference.child("owner").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    String value = dataSnapshot.getValue(String.class);
                    if (value == null) {
                        log("deleteGroup failed!Group not exists");
                        callback.onGroupDeletionError(new IllegalStateException("Group not exists!"));
                        return;
                    }
                    String userId = getUserId();
                    if (userId == null) {
                        log("deleteGroup failed!User not logged in");
                        callback.onGroupDeletionError(new IllegalStateException("User not logged in"));
                        return;
                    }
                    if (!value.equals(userId)) {
                        log("deleteGroup failed!Only owner can delete group");
                        callback.onGroupDeletionError(new IllegalStateException("Only owner can delete group!"));
                        return;
                    }
                    groupReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                log("deleteGroup success");
                                new FirebaseUserDataProvider().joinGroup(null);
                                callback.onGroupDeleted();
                            } else {
                                log("deleteGroup failed!" + task.getException().getMessage());
                                callback.onGroupDeletionError(task.getException());
                            }
                        }
                    });
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    log("deleteGroup failed!" + databaseError.getMessage());
                    callback.onGroupDeletionError(databaseError.toException());
                }
            });
        }
    }

    @Override
    public void sendGroupInvitation(ArrayList<InternalUserModel> invitedUsers) throws IllegalStateException {
        log("sendGroupInvitation invoked!");
        List<String> ids = getInternalUserModelIdsList(invitedUsers);
        for (int i = 0; i < ids.size(); i++) {
            final String userId = ids.get(i);
            groupReference.child("invited").child(userId).setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    GroupModel.InvitedModel invitedModel = new GroupModel.InvitedModel();
                    invitedModel.wasSend = false;
                    invitedModel.wasAccepted = null;
                    groupReference.child("invited").child(userId).setValue(invitedModel);
                }
            });
        }
    }

    private List<String> getInternalUserModelIdsList(List<InternalUserModel> users) {
        List<String> result = new ArrayList<>(users.size());
        for (InternalUserModel user : users) {
            result.add(user.getId());
        }
        return result;
    }

    @Override
    public void acceptGroupInvitation(final String groupId, final GroupAcceptCallback callback) throws IllegalStateException {
        log("acceptGroupInvitation invoked!");
        final String userId = getUserId();
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        reference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GroupModel value = dataSnapshot.getValue(GroupModel.class);
                if (value == null) {
                    log("acceptGroupInvitation onError: " + "Group does not exists");
                    callback.onError(new IllegalStateException("Group does not exists"));
                    return;
                }
                if (value.invited == null || !value.invited.containsKey(userId)) {
                    log("acceptGroupInvitation onError: " + "Invitation does not exists");
                    callback.onError(new IllegalStateException("Invitation does not exists"));
                    return;
                }

                reference.child(groupId).child("invited").child(userId).child("wasAccepted").setValue(true);
                new FirebaseUserDataProvider().joinGroup(groupId);
                log("acceptGroupInvitation success!");
                callback.onSuccess(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("acceptGroupInvitation onError: " + databaseError.getMessage());
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void rejectGroupInvitation(final String groupId, final GroupRejectCallback callback) throws IllegalStateException {
        log("rejectGroupInvitation invoked!");
        final String userId = getUserId();
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        reference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GroupModel value = dataSnapshot.getValue(GroupModel.class);
                if (value == null) {
                    log("rejectGroupInvitation onError: " + "Group does not exists");
                    callback.onError(new IllegalStateException("Group does not exists"));
                    return;
                }
                if (value.invited == null || !value.invited.containsKey(userId)) {
                    log("rejectGroupInvitation onError: " + "Invitation does not exists");
                    callback.onError(new IllegalStateException("Invitation does not exists"));
                    return;
                }

                reference.child(groupId).child("invited").child(userId).child("wasAccepted").setValue(false);
                log("rejectGroupInvitation success!");
                callback.onSuccess();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("rejectGroupInvitation onError: " + databaseError.getMessage());
                callback.onError(databaseError.toException());
            }
        });
    }

    @Override
    public void getGroupById(final String groupId, final GroupCallback callback) {
        log("getGroupById started!");
        reference.child(groupId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                log("getGroupById success!");
                GroupModel value = dataSnapshot.getValue(GroupModel.class);
                if (value == null) {
                    callback.onGroupAlreadyDeleted();
                } else {
                    callback.onGroupSuccess(value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("getGroupById failed!" + databaseError.getMessage());
                callback.onGroupError(databaseError.toException());
            }
        });
    }

    @Override
    public void subscribeMembersChanges(final MemberChangedCallback callback) throws IllegalStateException {
        log("subscribeMembersChanges started");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                log("Member added!");
                UserModel value = dataSnapshot.getValue(UserModel.class);
                callback.onMemberAdded(dataSnapshot.getKey(), value);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                log("Member changed!");
                UserModel value = dataSnapshot.getValue(UserModel.class);
                callback.onMemberChanged(dataSnapshot.getKey(), value);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                log("Member removed!");
                callback.onMemberRemoved(dataSnapshot.getKey(), ((UserModel) dataSnapshot.getValue(UserModel.class)).id);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                log("Member moved!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("Member cancelled!");
                callback.onError(databaseError.toException());
            }
        };
        currentMembersListeners.put(callback, childEventListener);
        groupReference.child("members").addChildEventListener(childEventListener);
    }

    @Override
    public void unsubscribeMembersChanges(MemberChangedCallback callback) throws IllegalStateException {
        log("unsubscribeMembersChanges invoked!");
        ChildEventListener event = currentMembersListeners.get(callback);
        if (event == null) {
            return;
        }
        currentMembersListeners.remove(callback);
        groupReference.child("members").removeEventListener(event);

    }

    @Override
    public void subscribeForGroupDelete(final DeleteCallback callback) throws IllegalStateException {
        log("subscribeForGroupDelete invoked!");
        if (groupReference == null) {
            throw new IllegalStateException("Not in a group!");
        }
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                if (value == null) {
                    callback.onGroupDeleted();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onGroupDeletionError(databaseError.toException());
            }
        };
        currentDeletedListeners.put(callback, listener);
        groupReference.addValueEventListener(listener);
    }

    @Override
    public void unsubscribeForGroupDelete(DeleteCallback callback) throws IllegalStateException {
        log("unsubscribeForGroupDelete invoked!");
        ValueEventListener event = currentDeletedListeners.get(callback);
        if (event == null) {
            return;
        }
        currentDeletedListeners.remove(callback);
        groupReference.removeEventListener(event);
    }

    @Override
    public void subscribeInvitationsChanges(InvitationChangesCallback callback) throws IllegalStateException {
        log("subscribeInvitationsChanges invoked!");
        ValueEventListener ve = new InvitedChangedListener(callback);
        currentInvitedListeners.put(callback, ve);
        groupReference.child("invited").addValueEventListener(ve);
    }

    @Override
    public void unsubscribeInvitationsChanges(InvitationChangesCallback callback) throws IllegalStateException {
        log("unsubscribeInvitationsChanges invoked!");
        ValueEventListener event = currentInvitedListeners.get(callback);
        if (event == null) {
            return;
        }
        currentInvitedListeners.remove(callback);
        groupReference.child("invited").removeEventListener(event);
    }

    @Override
    public void leaveGroup(final LeaveCallback callback) {
        final String userId = getUserId();
        if (userId == null) {
            throw new IllegalStateException("User not logged in");
        }
        groupReference.child("members").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                GenericTypeIndicator<List<UserModel>> t = new GenericTypeIndicator<List<UserModel>>() {
                };
                List<UserModel> members = dataSnapshot.getValue(t);
                if (members != null) {
                    for (int i = 0; i < members.size(); i++) {
                        if (userId.equals(members.get(i).id)) {
                            groupReference.child("members").child(String.valueOf(i)).removeValue();
                        }
                    }
                }
                new FirebaseUserDataProvider().joinGroup(null);
                callback.onLeaveSuccess();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                callback.onLeaveError(databaseError.toException());
            }
        });
    }

    private static class InvitedChangedListener implements ValueEventListener {
        private Map<String, GroupModel.InvitedModel> currentInvited;
        private InvitationChangesCallback callback;

        public InvitedChangedListener(InvitationChangesCallback callback) {
            this.callback = callback;
            currentInvited = new HashMap<>();
        }

        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            log("on invitation field changed!");
            GenericTypeIndicator<Map<String, GroupModel.InvitedModel>> t = new GenericTypeIndicator<Map<String, GroupModel.InvitedModel>>() {
            };
            Map<String, GroupModel.InvitedModel> newInvited = dataSnapshot.getValue(t);
            if (newInvited == null) {
                currentInvited.clear();
            } else {
                compareMapChanges(newInvited);
            }
        }

        private synchronized void compareMapChanges(Map<String, GroupModel.InvitedModel> newInvited) {
            Set<String> newInvitedKeys = newInvited.keySet();
            for (String key : newInvitedKeys) {
                if (currentInvited.containsKey(key) &&
                        currentInvited.get(key).wasAccepted == null &&
                        newInvited.get(key).wasAccepted != null) {
                    if (newInvited.get(key).wasAccepted) {
                        log("on invitation accepted! " + key);
                        callback.onInvitationAccepted(key);
                    } else {
                        log("on invitation rejected! " + key);
                        callback.onInvitationRejected(key);
                    }
                }
            }
            currentInvited = newInvited;
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            callback.onError(databaseError.toException());
        }

        private void log(String message) {
            Log.println(Log.ASSERT, "GroupManager", message);
        }
    }

    private void log(String message) {
        Log.println(Log.ASSERT, "GroupManager", message);
    }
}
