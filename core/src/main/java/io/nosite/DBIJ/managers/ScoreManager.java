package io.nosite.DBIJ.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.Arrays;

public class ScoreManager {
    private static final String PREF_NAME = "highscores";
    private static final int MAX_SCORES = 3;
    private Preferences prefs;

    public ScoreManager() {
        prefs = Gdx.app.getPreferences(PREF_NAME);
        if (!prefs.contains("score0")) {
            for (int i = 0; i < MAX_SCORES; i++) {
                prefs.putInteger("score" + i, 0);
            }
            prefs.flush();
        }
    }

    public void addScore(int score) {
        int[] scores = new int[MAX_SCORES];
        for (int i = 0; i < MAX_SCORES; i++) {
            scores[i] = prefs.getInteger("score" + i);
        }

        scores[MAX_SCORES - 1] = score;
        Arrays.sort(scores);

        for (int i = 0; i < scores.length / 2; i++) {
            int temp = scores[i];
            scores[i] = scores[scores.length - 1 - i];
            scores[scores.length - 1 - i] = temp;
        }

        for (int i = 0; i < MAX_SCORES; i++) {
            prefs.putInteger("score" + i, scores[i]);
        }
        prefs.flush();
    }

    public int[] getHighScores() {
        int[] scores = new int[MAX_SCORES];
        for (int i = 0; i < MAX_SCORES; i++) {
            scores[i] = prefs.getInteger("score" + i);
        }
        return scores;
    }

    public boolean isHighScore(int score) {
        return score > prefs.getInteger("score" + (MAX_SCORES - 1));
    }

    // Optional
    public void resetScores() {
        for (int i = 0; i < MAX_SCORES; i++) {
            prefs.putInteger("score" + i, 0);
        }
        prefs.flush();
    }
}
