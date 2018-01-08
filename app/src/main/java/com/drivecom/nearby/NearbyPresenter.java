package com.drivecom.nearby;


import com.drivecom.remote.FirebaseUserDataProvider;
import com.drivecom.remote.UserDataProvider;

public class NearbyPresenter implements NearbyPresenterInterface {

    private final UserDataProvider userManager;
    private NearbyFragmentInterface fragment;

    public NearbyPresenter(NearbyFragmentInterface fragment) {
        this.fragment = fragment;
        userManager = new FirebaseUserDataProvider();
    }

    public void subscribeNearbyChanges() {
        userManager.subscribeNearbyChanges(fragment);
    }

    public void unsubscribeNearbyChanges() {
        userManager.unsubscribeNearbyChanges(fragment);
    }
}
