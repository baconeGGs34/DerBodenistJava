package io.nosite.DBIJ.managers;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

public class PreferencesManager {

    public static PreferencesManager instance;
    private static final String EASY_MODE = "easy_mode";
    private static final String PREF_NAME = "GamePreferences";
    private static final String SOUND_ENABLED = "sound_enabled";
    private static final String GYRO_ENABLED = "gyro_enabled";
    private Preferences prefs;
    private static boolean isAndroid;

    // Private Konstruktor für Singleton
    private PreferencesManager() {
        prefs = Gdx.app.getPreferences(PREF_NAME);
        isAndroid = Gdx.app.getType() == Application.ApplicationType.Android;

        if (!prefs.contains(SOUND_ENABLED)) {
            prefs.putBoolean(SOUND_ENABLED, true);
            prefs.flush();
        }
        Gdx.app.log("PreferencesManager", "Sound enabled on init: " + isSoundEnabled());
    }

    // Singleton getInstance Methode
    public static PreferencesManager getInstance() {
        if (instance == null) {
            Gdx.app.log("PreferencesManager", "Creating new instance");
            instance = new PreferencesManager();
        }
        return instance;
    }

    public boolean isAndroid() {
        return isAndroid;
    }

    public void setSoundEnabled(boolean enabled) {
        Gdx.app.log("PreferencesManager", "Setting sound enabled to: " + enabled);
        prefs.putBoolean(SOUND_ENABLED, enabled);
        prefs.flush();
        Gdx.app.log("PreferencesManager", "After saving, sound enabled is: " + prefs.getBoolean(SOUND_ENABLED, true));
    }

    public boolean isSoundEnabled() {
        boolean enabled = prefs.getBoolean(SOUND_ENABLED, true);
        Gdx.app.log("PreferencesManager", "Checking if sound is enabled: " + enabled);
        return enabled;
    }

    public void setGyroEnabled(boolean enabled) {
        prefs.putBoolean(GYRO_ENABLED, enabled);
        prefs.flush();  // Wichtig: Sofortiges Speichern erzwingen
        Gdx.app.log("PreferencesManager", "Gyro setting saved: " + enabled);
    }

    public boolean isGyroEnabled() {
        boolean value = prefs.getBoolean(GYRO_ENABLED, true);  // Default auf Gyro
        Gdx.app.log("PreferencesManager", "Gyro setting loaded: " + value);
        return value;
    }

    public void setEasyMode(boolean enabled) {
        prefs.putBoolean(EASY_MODE, enabled);
        prefs.flush();
    }

    public boolean isEasyMode() {
        return prefs.getBoolean(EASY_MODE, false);
    }
}
