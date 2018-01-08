package com.drivecom.initialization;


import com.drivecom.models.UserModel;
import com.drivecom.remote.FirebaseUserDataProvider;
import com.drivecom.remote.UserDataProvider;

public class InitializationPresenter implements InitializationPresenterInterface {

    private final UserDataProvider userManager;
    private InitializationActivityInterface activity;

    public InitializationPresenter(InitializationActivityInterface activity) {
        this.activity = activity;
        this.userManager = new FirebaseUserDataProvider();
    }

    @Override
    public void invokeRegistration(String email, String password, final String number, final String name) {
        activity.onRegistrationStart();
        userManager.registerUser(email, password, new UserDataProvider.RegistrationCallback() {
            @Override
            public void onRegistrationSuccess() {
                UserModel model = new UserModel();
                model.nr = number;
                model.model = name;
                userManager.updateCurrentUserData(model, new UserDataProvider.UpdateUserDataCallback() {
                    @Override
                    public void onUserDataUpdateSuccess() {
                        activity.onRegistrationSuccess();
                    }

                    @Override
                    public void onUserDataUpdateFailure(Exception e) {
                        activity.onRegistrationFailure(e);
                    }
                });
            }

            @Override
            public void onRegistrationFailure(Exception e) {
                activity.onRegistrationFailure(e);
            }
        });
    }

    @Override
    public void invokeLogin(String email, String password) {
        activity.onLoginStart();
        userManager.loginUser(email, password, new UserDataProvider.LoginCallback() {
            @Override
            public void onLoginSuccess() {
                activity.onLoginSuccess();
            }

            @Override
            public void onLoginFailure(Exception e) {
                activity.onLoginFailure(e);
            }
        });
    }

    @Override
    public boolean isUserLoggedIn() {
        return userManager.isUserLoggedIn();
    }
}
