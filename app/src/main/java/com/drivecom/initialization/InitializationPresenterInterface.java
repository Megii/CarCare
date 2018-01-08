package com.drivecom.initialization;


public interface InitializationPresenterInterface {

    void invokeRegistration(String email, String password, String number, String name);

    void invokeLogin(String email, String password);

    boolean isUserLoggedIn();
}
