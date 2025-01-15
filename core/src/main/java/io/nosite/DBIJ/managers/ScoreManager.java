package io.nosite.DBIJ.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import java.util.Arrays;

public class ScoreManager {
    private static final String PREF_NAME = "highscores";
    private static final int MAX_SCORES = 5;
    private Preferences prefs;

    public ScoreManager() {
        prefs = Gdx.app.getPreferences(PREF_NAME);
        // Initialisiere Scores falls noch keine existieren
        if (!prefs.contains("score0")) {
            for (int i = 0; i < MAX_SCORES; i++) {
                prefs.putInteger("score" + i, 0);
            }
            prefs.flush();
        }
    }

    public void addScore(int score) {
        // Hole existierende Scores
        int[] scores = new int[MAX_SCORES];
        for (int i = 0; i < MAX_SCORES; i++) {
            scores[i] = prefs.getInteger("score" + i);
        }

        // Füge neuen Score hinzu und sortiere
        scores[MAX_SCORES - 1] = score;
        Arrays.sort(scores);

        // Reverse für absteigende Sortierung
        for (int i = 0; i < scores.length / 2; i++) {
            int temp = scores[i];
            scores[i] = scores[scores.length - 1 - i];
            scores[scores.length - 1 - i] = temp;
        }

        // Speichere sortierte Scores
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

    // Optional: Methode zum Zurücksetzen der Scores
    public void resetScores() {
        for (int i = 0; i < MAX_SCORES; i++) {
            prefs.putInteger("score" + i, 0);
        }
        prefs.flush();
    }
}
