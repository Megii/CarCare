package com.drivecom.persistence;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;

public class LocalDataProvider {

    private static final String PERSISTENCE_PREFS = "PersistencePrefs";
    private static final String MY_ID_KEY = "myid";
    private final SharedPreferences prefs;

    public LocalDataProvider(Context context) {
        prefs = context.getSharedPreferences(PERSISTENCE_PREFS, Activity.MODE_PRIVATE);
    }

    private void putValue(String key, String value) {
        prefs.edit().putString(key, value).apply();
    }

    private String getStringValue(String key) {
        return prefs.getString(key, null);
    }

    private boolean haveValue(String key) {
        return prefs.contains(key);
    }

    public String getMyId() {
        return getStringValue(MY_ID_KEY);
    }

    public void setMyId(String id) {
        putValue(MY_ID_KEY, id);
    }

    public boolean isMyIdAvailable() {
        return haveValue(MY_ID_KEY);
    }
}
