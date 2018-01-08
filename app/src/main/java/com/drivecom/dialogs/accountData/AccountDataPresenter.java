package com.drivecom.dialogs.accountData;

import com.drivecom.models.UserModel;
import com.drivecom.remote.FirebaseUserDataProvider;
import com.drivecom.remote.UserDataProvider;

public class AccountDataPresenter implements AccountDataPresenterInterface {

    private AccountDataViewInterface dialogView;

    public AccountDataPresenter(AccountDataViewInterface dialogView) {
        this.dialogView = dialogView;
    }

    @Override
    public void invokeAccountDataChangesSaving(UserModel currentUser, String name, String registrationNumber) {
        currentUser.model = name;
        currentUser.nr = registrationNumber;
        new FirebaseUserDataProvider().updateCurrentUserData(currentUser, new UserDataProvider.UpdateUserDataCallback() {
            @Override
            public void onUserDataUpdateSuccess() {
                dialogView.onAccountDataChangesSavingSuccess();
            }

            @Override
            public void onUserDataUpdateFailure(Exception e) {
                dialogView.onAccountDataChangesSavingFailure();
            }
        });
    }
}
