package com.drivecom.remote;

import com.drivecom.models.CoordinatesModel;
import com.drivecom.models.UserModel;

public interface UserDataProvider {
    boolean isUserLoggedIn();

    void registerUser(String email, String password, RegistrationCallback callback);

    void loginUser(String email, String password, LoginCallback callback);

    void getCurrentUserData(UserDataCallback callback) throws IllegalStateException;

    void updateCurrentUserData(UserModel userModel, UpdateUserDataCallback callback) throws IllegalStateException;

    void subscribeNearbyChanges(NearbyChangedCallback callback) throws IllegalStateException;

    void unsubscribeNearbyChanges(NearbyChangedCallback callback) throws IllegalStateException;

    void updateCurrentUserToken(String newToken);

    void updateCurrentUserCoords(CoordinatesModel coordinatesModel);

    void logout();

    void getMessageFrom(GetMessageFromCallback callback, String userId);

    void joinGroup(String groupId);


    interface RegistrationCallback {
        void onRegistrationSuccess();

        void onRegistrationFailure(Exception e);
    }

    interface LoginCallback {
        void onLoginSuccess();

        void onLoginFailure(Exception e);
    }

    interface UserDataCallback {
        void onUserDataSuccess(String email, UserModel userModel);

        void onUserDataFailure(Exception e);
    }

    interface UpdateUserDataCallback {
        void onUserDataUpdateSuccess();

        void onUserDataUpdateFailure(Exception e);
    }

    interface NearbyChangedCallback {
        void onNearbyElementAdded(String elementKey, UserModel newNearbyUser);

        void onNearbyElementChanged(String elementKey, UserModel changedNearbyUser);

        void onNearbyElementRemoved(String elementKey, final String itemId);
    }

    interface GetMessageFromCallback {
        void onGetUserNameSuccess(String name);

        void onGetUserNameFailure(Exception e);
    }
}
