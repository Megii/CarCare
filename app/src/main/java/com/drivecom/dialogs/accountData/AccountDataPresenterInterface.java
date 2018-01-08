package com.drivecom.dialogs.accountData;

import com.drivecom.models.UserModel;

public interface AccountDataPresenterInterface {

    void invokeAccountDataChangesSaving(UserModel currentUser, String name, String registrationNumber);
}
