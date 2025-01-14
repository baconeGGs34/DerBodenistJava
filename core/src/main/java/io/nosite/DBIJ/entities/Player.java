package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2; // Position und Geschwindigkeit als 2D Vektoren
import com.badlogic.gdx.graphics.g2d.SpriteBatch; // Zum Zeichnen des Spielers
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;

public class Player {

    private Vector2 position;     // Aktuelle Position des Spielers
    private Vector2 velocity;     // Geschwindigkeit und Richtung
    private float width;          // Breite des Spielers für Kollisionen
    private float height;         // Höhe des Spielers für Kollisionen
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float JUMP_VELOCITY = 1600;    // Wie hoch der Spieler springt
    private static final float MOVEMENT_SPEED = 400;   // Horizontale Bewegungsgeschwindigkeit
    private static final float PLAYER_SIZE = 30;  // Spielergröße in Pixeln
    private Rectangle bounds;  // Für Kollisionserkennung und Zeichnung
    private ShapeRenderer shapeRenderer;  // Als Klassenvariable

    public Player(float x, float y) {
        position = new Vector2(x, y);  // Position initialisieren
        velocity = new Vector2();      // Geschwindigkeit initialisieren
        bounds = new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);
        shapeRenderer = new ShapeRenderer();
    }

    public Vector2 getPosition() {
        return position;
    }

    public void update(float delta, Array<Platform> platforms) {
        // Horizontale Bewegung durch Tastatur
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            velocity.x = -MOVEMENT_SPEED;
        } else if(Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            velocity.x = MOVEMENT_SPEED;
        } else {
            velocity.x = 0;
        }

        // Schwerkraft auf vertikale Geschwindigkeit anwenden
        velocity.y -= 4000 * delta;  // 1200 ist die Stärke der Schwerkraft

        // Position basierend auf Geschwindigkeit aktualisieren
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // Rand-Kollision prüfen
        if(position.x < 40) {  // Linker Rand
            position.x = 40;
            velocity.x = 0;
        }
        if(position.x > MIN_WORLD_WIDTH - 40 - PLAYER_SIZE) {  // Rechter Rand
            position.x = MIN_WORLD_WIDTH - 40 - PLAYER_SIZE;
            velocity.x = 0;
        }

        bounds.setPosition(position.x, position.y);  // Bounds aktualisieren

        // Nur wenn wir nach unten fallen
        if(velocity.y < 0) {
            // Überprüfe jede Platform
            for(Platform platform : platforms) {
                if(bounds.overlaps(platform.getBounds())) {
                    // Kollision! Setze Position auf Platform-Oberkante
                    position.y = platform.getBounds().y + platform.getBounds().height;
                    // Nach oben springen
                    velocity.y = JUMP_VELOCITY;
                    break;  // Eine Kollision reicht
                }
            }
        }


    }

    public void render(ShapeRenderer shapeRenderer) {
        bounds.setPosition(position.x, position.y);
        shapeRenderer.setColor(Color.RED);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
