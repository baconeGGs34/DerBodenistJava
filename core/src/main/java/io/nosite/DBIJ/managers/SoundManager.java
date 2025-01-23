package io.nosite.DBIJ.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.utils.Timer;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.screens.GameScreen;

public class SoundManager {
    private static Sound jumpSound;
    private static Music epicMusic;
    private static Music gameMusic;
    private static Sound gameOver;
    private static Sound jetPack;
    private static Sound collectPowerUp;
    private static long jetPackSoundId;
    private static boolean soundEnabled = true;
    private static PreferencesManager prefsManager;


    public static void init() {
        Gdx.app.log("SoundManager", "Initializing SoundManager");
        prefsManager = PreferencesManager.getInstance();

        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump_sound2.wav"));
        gameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/game_over.wav"));
        jetPack = Gdx.audio.newSound(Gdx.files.internal("sounds/jetpack_sound.wav"));
        collectPowerUp = Gdx.audio.newSound(Gdx.files.internal("sounds/power_up_sound.ogg"));
        epicMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/epic_music.mp3"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/game_music.mp3"));


        // Musik-Einstellungen
        epicMusic.setLooping(true);
        gameMusic.setLooping(true);

        if (prefsManager.isSoundEnabled()) {
            Gdx.app.log("SoundManager", "Starting initial music");
            playEpicMusic();
        }

        // Debug-Ausgabe hinzufügen
        Gdx.app.log("SoundManager", "Sound enabled status: " + prefsManager.isSoundEnabled());

    }

    public static void playJumpSound() {
        Gdx.app.log("SoundManager", "Attempting to play jump sound. Sound enabled: " + prefsManager.isSoundEnabled());
        if (prefsManager.isSoundEnabled()) {
            jumpSound.play(0.6f);
            Gdx.app.log("SoundManager", "Jump sound played");
        }
    }

    public static void playPowerUpSound() {
        Gdx.app.log("SoundManager", "Attempting to play jump sound. Sound enabled: " + prefsManager.isSoundEnabled());
        if (prefsManager.isSoundEnabled()) {
            collectPowerUp.play(0.6f);
            Gdx.app.log("SoundManager", "Jump sound played");
        }
    }

    public static void playJetPackSound() {
        Gdx.app.log("SoundManager", "Attempting to play jetpack sound. Sound enabled: " + prefsManager.isSoundEnabled());
        if (prefsManager.isSoundEnabled()) {
            jetPackSoundId = jetPack.play(1f);

            Timer.schedule(new Timer.Task() {
                @Override
                public void run() {
                    jetPack.stop(jetPackSoundId);
                }
            }, 2f);

            Gdx.app.log("SoundManager", "Jetpack sound played");
        }
    }

    public static void playGameOverSound() {
        if (prefsManager.isSoundEnabled()) {
            gameOver.play();
        }
    }

    public static void playEpicMusic() {
        if (prefsManager.isSoundEnabled()) {
            if (gameMusic != null) {
                gameMusic.stop();
            }
            if (epicMusic != null) {
                epicMusic.play();
            }
        }
    }

    public static void playGameMusic() {
        if (prefsManager.isSoundEnabled()) {
            if (epicMusic != null) {
                epicMusic.stop();
            }
            if (gameMusic != null) {
                gameMusic.play();
            }
        }
    }

    public static void setSoundEnabled(boolean enabled) {
        if (!enabled) {
            epicMusic.stop();
            gameMusic.stop();
        } else {
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
        if (epicMusic != null) {
            epicMusic.stop();
        }
        if (gameMusic != null) {
            gameMusic.stop();
        }
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


    public static void dispose() {
        jumpSound.dispose();
        epicMusic.dispose();
        gameMusic.dispose();
        gameOver.dispose();
        jetPack.dispose();
        collectPowerUp.dispose();
    }
}
