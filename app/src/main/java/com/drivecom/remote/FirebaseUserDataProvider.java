package com.drivecom.remote;

import android.support.annotation.NonNull;
import android.util.Log;

import com.drivecom.models.CoordinatesModel;
import com.drivecom.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUserDataProvider implements UserDataProvider {

    private DatabaseReference mDatabase;
    private DatabaseReference currentUserReference;
    private final Map<NearbyChangedCallback, ChildEventListener> currentListeners;

    public FirebaseUserDataProvider() {
        mDatabase = FirebaseDatabase.getInstance().getReference();
        currentListeners = new HashMap<>();
    }

    @Override
    public boolean isUserLoggedIn() {
        boolean b = FirebaseAuth.getInstance().getCurrentUser() != null;
        log("isUserLoggedIn called: " + b);
        return b;
    }

    @Override
    public void registerUser(String email, String password, final RegistrationCallback callback) {
        log("registerUser started");
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            log("registerUser success");
                            callback.onRegistrationSuccess();
                        } else {
                            log("registerUser failure");
                            callback.onRegistrationFailure(task.getException());
                        }
                    }
                });
    }

    @Override
    public void loginUser(String email, String password, final LoginCallback callback) {
        log("loginUser started");
        FirebaseAuth.getInstance()
                .signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            log("loginUser success");
                            callback.onLoginSuccess();
                        } else {
                            log("loginUser failure");
                            callback.onLoginFailure(task.getException());
                        }
                    }
                });
    }

    @Override
    public void getCurrentUserData(final UserDataCallback callback) throws IllegalStateException {
        log("getCurrentUserData started!");
        getCurrentUserReference().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                log("getCurrentUserData success!");
                UserModel value = dataSnapshot.getValue(UserModel.class);
                value.id = dataSnapshot.getKey();
                if (callback != null) {
                    callback.onUserDataSuccess(FirebaseAuth.getInstance().getCurrentUser().getEmail(), value);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("getCurrentUserData failed!");
                if (callback != null) {
                    callback.onUserDataFailure(databaseError.toException());
                }
            }
        });
    }

    private String getCurrentUserId() {
        try {
            return FirebaseAuth.getInstance().getCurrentUser().getUid();
        } catch (Throwable t) {
            t.printStackTrace();
            return null;
        }
    }

    @Override
    public void getMessageFrom(final GetMessageFromCallback callback, String userId) {
        mDatabase.child("users").child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                log("getMessageFrom success!");
                UserModel value = dataSnapshot.getValue(UserModel.class);
                if (callback != null) {
                    callback.onGetUserNameSuccess(value.model);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("getMessageFrom failed!");
                if (callback != null) {
                    callback.onGetUserNameFailure(databaseError.toException());
                }
            }
        });
    }

    @Override
    public void updateCurrentUserData(UserModel userModel, final UpdateUserDataCallback callback) throws IllegalStateException {
        log("updateCurrentUserData started!");
        getCurrentUserReference().setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    log("updateCurrentUserData started!");
                    callback.onUserDataUpdateSuccess();
                } else {
                    log("updateCurrentUserData failure!");
                    callback.onUserDataUpdateFailure(task.getException());
                }
            }
        });
    }

    @Override
    public void subscribeNearbyChanges(final NearbyChangedCallback callback) {
        log("subscribeNearbyChanges started");
        ChildEventListener childEventListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                log("Nearby added!");
                UserModel value = dataSnapshot.getValue(UserModel.class);
                callback.onNearbyElementAdded(dataSnapshot.getKey(), value);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                log("Nearby changed!");
                UserModel value = dataSnapshot.getValue(UserModel.class);
                callback.onNearbyElementChanged(dataSnapshot.getKey(), value);
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                log("Nearby removed!");
                callback.onNearbyElementRemoved(dataSnapshot.getKey(), ((UserModel) dataSnapshot.getValue(UserModel.class)).id);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                log("Nearby moved!");
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                log("Nearby cancelled!");
            }
        };
        currentListeners.put(callback, childEventListener);
        getCurrentUserReference().child("nearby").addChildEventListener(childEventListener);
    }

    @Override
    public void unsubscribeNearbyChanges(final NearbyChangedCallback callback) {
        log("unsubscribeNearbyChanges invoked!");
        ChildEventListener event = currentListeners.get(callback);
        if (event == null) {
            return;
        }
        currentListeners.remove(callback);
        getCurrentUserReference().child("nearby").removeEventListener(event);
    }

    @Override
    public void updateCurrentUserToken(String newToken) throws IllegalStateException {
        log("updateCurrentUserToken invoked!");
        getCurrentUserReference().child("token").setValue(newToken);
    }

    @Override
    public void updateCurrentUserCoords(CoordinatesModel coordinatesModel) throws IllegalStateException {
        getCurrentUserReference().child("coords").setValue(coordinatesModel);
    }

    private DatabaseReference getCurrentUserReference() throws IllegalStateException {
        if (currentUserReference == null) {
            String userId = getCurrentUserId();
            if (userId == null) {
                throw new IllegalStateException("User not logged in!");
            }
            currentUserReference = mDatabase.child("users").child(userId);
        }
        return currentUserReference;
    }

    @Override
    public void joinGroup(String groupId) {
        getCurrentUserReference().child("groupId").setValue(groupId);
    }

    @Override
    public void logout() {
        FirebaseAuth.getInstance().signOut();
    }

    private void log(String message) {
        Log.println(Log.ASSERT, "UserData", message);
    }
}
