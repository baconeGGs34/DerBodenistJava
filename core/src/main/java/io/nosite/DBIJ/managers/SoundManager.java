package io.nosite.DBIJ.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;

public class SoundManager {
    private static Sound jumpSound;
    private static Music epicMusic;
    private static Music gameMusic;
    private static Sound gameOver;
    private static boolean soundEnabled = true;

    public static void init() {
        jumpSound = Gdx.audio.newSound(Gdx.files.internal("sounds/jump_sound2.wav"));
        gameOver = Gdx.audio.newSound(Gdx.files.internal("sounds/game_over.wav"));
        epicMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/epic_music.mp3"));
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("sounds/game_music.mp3"));

        // Musik-Einstellungen
        epicMusic.setLooping(true);
        gameMusic.setLooping(true);
    }

    public static void playJumpSound() {
        if (soundEnabled) {
            jumpSound.play();
        }
    }
    public static void playGameOverSound() {
        if (soundEnabled) {
            gameOver.play();
        }
    }

    public static void playEpicMusic() {
        if (soundEnabled) {
            gameMusic.stop();
            epicMusic.play();
        }
    }

    public static void playGameMusic() {
        if (soundEnabled) {
            epicMusic.stop();
            gameMusic.play();
        }
    }

    public static void setSoundEnabled(boolean enabled) {
        soundEnabled = enabled;
        if (!enabled) {
            epicMusic.stop();
            gameMusic.stop();
        }
    }

    public static boolean isSoundEnabled() {
        return soundEnabled;
    }

    public static void dispose() {
        jumpSound.dispose();
        epicMusic.dispose();
        gameMusic.dispose();
    }
}
