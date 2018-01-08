package com.drivecom.remote;


public interface UserDataListener extends UserDataProvider.NearbyChangedCallback,
        UserDataProvider.UpdateUserDataCallback,
        UserDataProvider.UserDataCallback,
        UserDataProvider.LoginCallback,
        UserDataProvider.RegistrationCallback {
}
