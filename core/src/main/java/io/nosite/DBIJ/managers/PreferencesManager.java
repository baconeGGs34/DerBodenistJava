package io.nosite.DBIJ.managers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesManager {
    private static final String PREF_NAME = "GamePreferences";
    private static final String SOUND_ENABLED = "sound_enabled";
    private static final String GYRO_ENABLED = "gyro_enabled";
    private Preferences prefs;
    private static boolean isAndroid;

    public PreferencesManager() {
        prefs = Gdx.app.getPreferences(PREF_NAME);
        isAndroid = Gdx.app.getType() == Application.ApplicationType.Android;

        // Debug output
        Gdx.app.log("PreferencesManager", "isAndroid: " + isAndroid);
        Gdx.app.log("PreferencesManager", "isGyroEnabled: " + isGyroEnabled());
    }

    public boolean isAndroid() {
        return isAndroid;
    }

    public void setSoundEnabled(boolean enabled) {
        prefs.putBoolean(SOUND_ENABLED, enabled);
        prefs.flush();
    }

    public boolean isSoundEnabled() {
        return prefs.getBoolean(SOUND_ENABLED, true);
    }

    public void setGyroEnabled(boolean enabled) {
        prefs.putBoolean(GYRO_ENABLED, enabled);
        prefs.flush();
        Gdx.app.log("PreferencesManager", "Gyro set to: " + enabled);
    }

    public boolean isGyroEnabled() {
        return prefs.getBoolean(GYRO_ENABLED, false);
    }
}
