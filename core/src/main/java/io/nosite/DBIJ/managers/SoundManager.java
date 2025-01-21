package io.nosite.DBIJ.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.screens.GameScreen;

public class SoundManager {
    private static Sound jumpSound;
    private static Music epicMusic;
    private static Music gameMusic;
    private static Sound gameOver;
    private static boolean soundEnabled = true;
    private static PreferencesManager prefsManager;


    public static void init() {
        Gdx.app.log("SoundManager", "Initializing SoundManager");
        prefsManager = PreferencesManager.getInstance();

        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump_sound2.wav"));
        gameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/game_over.wav"));
        epicMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/epic_music.mp3"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/game_music.mp3"));

        // Musik-Einstellungen
        epicMusic.setLooping(true);
        gameMusic.setLooping(true);

        // Debug-Ausgabe hinzufügen
        Gdx.app.log("SoundManager", "Sound enabled status: " + prefsManager.isSoundEnabled());

    }

    public static void playJumpSound() {
        Gdx.app.log("SoundManager", "Attempting to play jump sound. Sound enabled: " + prefsManager.isSoundEnabled());
        if (prefsManager.isSoundEnabled()) {
            jumpSound.play(0.5f);  // Lautstärke auf 50%
            Gdx.app.log("SoundManager", "Jump sound played");
        }
    }
    public static void playGameOverSound() {
        if (prefsManager.isSoundEnabled()) {
            gameOver.play();
        }
    }

    public static void playEpicMusic() {
        Gdx.app.log("SoundManager", "Attempting to play epic music. Sound enabled: " + prefsManager.isSoundEnabled());
        if (prefsManager.isSoundEnabled()) {
            gameMusic.stop();
            epicMusic.play();
            Gdx.app.log("SoundManager", "Epic music started");
        }
    }

    public static void playGameMusic() {
        Gdx.app.log("SoundManager", "Attempting to play game music. Sound enabled: " + prefsManager.isSoundEnabled());
        if (prefsManager.isSoundEnabled()) {
            epicMusic.stop();
            gameMusic.play();
            Gdx.app.log("SoundManager", "Game music started");
        }
    }

    public static void setSoundEnabled(boolean enabled) {
        if (!enabled) {
            epicMusic.stop();
            gameMusic.stop();
        } else {
            // Musik wieder starten wenn Sound eingeschaltet wird
            if (Gdx.app.getApplicationListener() instanceof Main) {
                Screen currentScreen = ((Main)Gdx.app.getApplicationListener()).getScreen();
                if (currentScreen instanceof GameScreen) {
                    gameMusic.play();
                } else {
                    epicMusic.play();
                }
            }
        }
    }
    public static void stopMusic() {
        epicMusic.stop();
        gameMusic.stop();
    }

    public static void resumeMusic() {
        if (prefsManager.isSoundEnabled()) {
            if (Gdx.app.getApplicationListener() instanceof Main) {
                Screen currentScreen = ((Main)Gdx.app.getApplicationListener()).getScreen();
                if (currentScreen instanceof GameScreen) {
                    gameMusic.play();
                } else {
                    epicMusic.play();
                }
            }
        }
    }

    public static boolean isSoundEnabled() {
        return prefsManager.isSoundEnabled();
    }

    public static void dispose() {
        jumpSound.dispose();
        epicMusic.dispose();
        gameMusic.dispose();
        gameOver.dispose();
    }
}
