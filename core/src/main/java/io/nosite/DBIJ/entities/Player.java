package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

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
    private Texture spriteSheet;
    private TextureRegion[] jumpAnimation;
    private float animationTimer;
    private static final float FRAME_DURATION = 0.1f;
    private boolean isFacingLeft = false;
    private static final int JUMP_FRAME_COUNT = 5;  // 5 Frames für die Animation
    private static final int FRAME_WIDTH = 32;      // Breite eines Sprites
    private static final int FRAME_HEIGHT = 32;     // Höhe eines Sprites

    public Player(float x, float y) {
        position = new Vector2(x, y);
        velocity = new Vector2();
        bounds = new Rectangle(x, y, PLAYER_SIZE, PLAYER_SIZE);

        // Sprite Sheet laden
        spriteSheet = new Texture("images/charjump.png");
        jumpAnimation = new TextureRegion[JUMP_FRAME_COUNT];

        // Die 5 Frames aus dem Spritesheet ausschneiden
        for(int i = 0; i < JUMP_FRAME_COUNT; i++) {
            jumpAnimation[i] = new TextureRegion(spriteSheet,
                i * FRAME_WIDTH,  // x-Position: jedes Sprite ist 32px breit
                0,               // y-Position: nur eine Reihe, daher 0
                FRAME_WIDTH,     // Breite: 32px
                FRAME_HEIGHT     // Höhe: 32px
            );
        }
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
        velocity.y -= 4000 * delta;  // Stärke der Schwerkraft

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

        // Animation Timer aktualisieren
        animationTimer += delta;
        if(animationTimer > FRAME_DURATION * JUMP_FRAME_COUNT) {
            animationTimer = 0;
        }
    }

    public void render(SpriteBatch batch) {
        int currentFrame = (int)(animationTimer / FRAME_DURATION);
        batch.draw(jumpAnimation[currentFrame],
            position.x,
            position.y,
            PLAYER_SIZE,    // Größe an dein Spiel anpassen
            PLAYER_SIZE);   // Größe an dein Spiel anpassen
    }

    public void dispose() {
        spriteSheet.dispose();
    }
}
