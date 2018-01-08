package com.drivecom.utils;


import android.content.Context;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.View;

public class SnackBarCreator {

    private boolean actionClickedFirst = false;

    public Snackbar getSnackBar(Context context, View containerView, int messageResource, boolean isIndefinite, @Nullable final Runnable actionRunnable, @Nullable Integer actionLabelResource, @Nullable final Runnable onDismissRunnable) {
        if (context != null) {
            String actionLabel = null;
            if (actionLabelResource != null) {
                actionLabel = context.getString(actionLabelResource);
            }
            return getSnackBar(containerView, context.getString(messageResource), isIndefinite, actionRunnable, actionLabel, onDismissRunnable);
        } else {
            return null;
        }
    }

    public Snackbar getSnackBar(View containerView, String message, boolean isIndefinite, @Nullable final Runnable actionRunnable, @Nullable String actionLabel, @Nullable final Runnable onDismissRunnable) {
        int length;
        if (isIndefinite) {
            length = Snackbar.LENGTH_INDEFINITE;
        } else {
            length = Snackbar.LENGTH_LONG;
        }

        Snackbar snackbar = Snackbar.make(containerView, message, length);
        if (actionRunnable != null && actionLabel != null) {
            snackbar.setAction(actionLabel, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    actionClickedFirst = true;
                    actionRunnable.run();
                }
            });
        }
        if (onDismissRunnable != null) {
            snackbar.addCallback(new Snackbar.Callback() {
                @Override
                public void onDismissed(Snackbar snackbar, int event) {
                    if (!actionClickedFirst) {
                        onDismissRunnable.run();
                    } else {
                        actionClickedFirst = false;
                    }
                }
            });
        }
        return snackbar;
    }
}