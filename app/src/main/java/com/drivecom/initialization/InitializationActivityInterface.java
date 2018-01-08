package com.drivecom.initialization;


public interface InitializationActivityInterface {

    void onRegistrationStart();

    void onRegistrationSuccess();

    void onRegistrationFailure(Exception e);

    void onLoginStart();

    void onLoginSuccess();

    void onLoginFailure(Exception e);
}
