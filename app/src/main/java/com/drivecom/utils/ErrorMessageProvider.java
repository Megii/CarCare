package com.drivecom.utils;

import android.content.res.Resources;
import android.support.annotation.Nullable;

import com.drivecom.R;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class ErrorMessageProvider {

    public static @Nullable
    String provideLoginErrorMessageViaExceptionType(Resources resources, Exception ex) {
        return provideLoginErrorMessageViaExceptionType(resources, ex, null);
    }

    public static @Nullable
    String provideLoginErrorMessageViaExceptionType(Resources resources, Exception ex, @Nullable String defaultMessage) {
        if (ex instanceof FirebaseAuthInvalidCredentialsException ||
                ex instanceof FirebaseAuthInvalidUserException) {
            return resources.getString(R.string.progress_login_bad_credentials_message);
        }
        return defaultMessage;
    }

    public static @Nullable
    String provideRegisterErrorMessageViaExceptionType(Resources resources, Exception ex) {
        return provideRegisterErrorMessageViaExceptionType(resources, ex, null);
    }

    public static @Nullable
    String provideRegisterErrorMessageViaExceptionType(Resources resources, Exception ex, @Nullable String defaultMessage) {
        if (ex instanceof FirebaseAuthWeakPasswordException) {
            return resources.getString(R.string.progress_register_weak_password_message);
        } else if (ex instanceof FirebaseAuthInvalidCredentialsException) {
            return resources.getString(R.string.progress_register_bad_email_message);
        } else if (ex instanceof FirebaseAuthUserCollisionException) {
            return resources.getString(R.string.progress_register_user_in_use_message);
        }
        return defaultMessage;
    }
}
