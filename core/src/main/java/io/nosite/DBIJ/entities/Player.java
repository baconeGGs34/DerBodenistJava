package io.nosite.DBIJ.entities;

import com.badlogic.gdx.math.Vector2; // Position und Geschwindigkeit als 2D Vektoren
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Zum Zeichnen des Spielers

public class Player {

    private Vector2 position;     // Aktuelle Position des Spielers
    private Vector2 velocity;     // Geschwindigkeit und Richtung
    private float width;          // Breite des Spielers für Kollisionen
    private float height;         // Höhe des Spielers für Kollisionen
    private static final float JUMP_VELOCITY = 800;    // Wie hoch der Spieler springt
    private static final float MOVEMENT_SPEED = 300;   // Horizontale Bewegungsgeschwindigkeit

    public Player(float v, int i) {
    }

    public void update(float delta) {
    }
}
