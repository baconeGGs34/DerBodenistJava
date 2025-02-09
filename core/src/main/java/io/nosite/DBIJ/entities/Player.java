package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.nosite.DBIJ.managers.PreferencesManager;
import io.nosite.DBIJ.managers.SoundManager;

public class Player {

    private Vector2 position;     // Aktuelle Position des Spielers
    private Vector2 velocity;     // Geschwindigkeit und Richtung
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float JUMP_VELOCITY = 1600;    // Wie hoch der Spieler springt
    public static final float MOVEMENT_SPEED = 400;   // Horizontale Bewegungsgeschwindigkeit
    private static final float PLAYER_SIZE = 30;  // Spielergröße in Pixeln
    private Rectangle bounds;
    private Texture spriteSheet;
    private TextureRegion[] jumpAnimation;
    private float stateTime = 0;
    private int currentFrame = 0;
    private static final float FRAME_DURATION = 0.2f;
    private boolean isJumping = true;
    private static final int FRAME_COUNT = 5;
    private static final float PLAYER_WIDTH = 35;
    private static final float PLAYER_HEIGHT = 50;
    private Texture[] jumpTextures;
    private PreferencesManager prefsManager;
    private OrthographicCamera camera;
    private Rectangle leftButtonBounds;
    private Rectangle rightButtonBounds;
    private JetpackEffect jetpackEffect;
    private boolean jetpackActive;

    public Player(float x, float y, PreferencesManager prefsManager, OrthographicCamera camera,
                  Rectangle leftButtonBounds, Rectangle rightButtonBounds) {
        this.position = new Vector2(x, y);
        this.velocity = new Vector2();
        this.prefsManager = prefsManager;
        this.camera = camera;
        this.leftButtonBounds = leftButtonBounds;
        this.rightButtonBounds = rightButtonBounds;
        this.bounds = new Rectangle(x, y, PLAYER_WIDTH, PLAYER_HEIGHT);

        jetpackEffect = new JetpackEffect();
        jetpackActive = false;

        jumpTextures = new Texture[FRAME_COUNT];
        for(int i = 0; i < FRAME_COUNT; i++) {
            jumpTextures[i] = new Texture("images/jump" + (i+1) + ".png");
        }

        // Sprite Sheet laden
        spriteSheet = new Texture("images/charjump.png");
        jumpAnimation = new TextureRegion[FRAME_COUNT];

        int[] frameX = {0, 32, 64, 96, 128};
        int[] frameWidths = {32, 32, 32, 32, 32};

        for (int i = 0; i < FRAME_COUNT; i++) {
            jumpAnimation[i] = new TextureRegion(spriteSheet,
                frameX[i],
                0,
                frameWidths[i],
                32
            );
        }
    }


    public void update(float delta, Array<Platform> platforms) {
        // Bewegungssteuerung
        if(prefsManager.isAndroid()) {
            if(prefsManager.isGyroEnabled()) {
                // Gyro Steuerung
                float accelX = Gdx.input.getAccelerometerX();
                velocity.x = -(accelX / 2.0f) * MOVEMENT_SPEED;
            }
        } else {
            if (!prefsManager.isAndroid()) {
                if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
                    velocity.x = -MOVEMENT_SPEED;
                } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
                    velocity.x = MOVEMENT_SPEED;
                } else {
                    velocity.x = 0;
                }
            }
        }
        if (jetpackActive) {
            jetpackEffect.update(delta, position.x + PLAYER_WIDTH/2, position.y);
        }

        // Schwerkraft auf vertikale Geschwindigkeit anwenden
        velocity.y -= 4000 * delta;

        // Position basierend auf Geschwindigkeit aktualisieren
        position.x += velocity.x * delta;
        position.y += velocity.y * delta;

        // Rand-Kollision prüfen
        if (position.x < 40) {
            position.x = 40;
            velocity.x = 0;
        }
        if (position.x > MIN_WORLD_WIDTH - 40 - PLAYER_SIZE) {
            position.x = MIN_WORLD_WIDTH - 40 - PLAYER_SIZE;
            velocity.x = 0;
        }

        bounds.setPosition(position.x, position.y);
        bounds.set(position.x, position.y, PLAYER_WIDTH, PLAYER_HEIGHT);

        // Bewegungsrichtung bestimmen
        if (velocity.y > 0) {
            isJumping = true;
        } else if (velocity.y < 0) {
            isJumping = false;
        }

        // Animation aktualisieren
        stateTime += delta;
        if (stateTime >= FRAME_DURATION) {
            stateTime = 0;
            if (isJumping) {
                // Vorwärts Animation beim Springen
                currentFrame++;
                if (currentFrame >= FRAME_COUNT) {
                    currentFrame = FRAME_COUNT - 1;
                }
            } else {
                // Rückwärts Animation beim Fallen
                currentFrame--;
                if (currentFrame < 0) {
                    currentFrame = 0;
                }
            }
        }

        // Plattform Kollisionsprüfung (nur beim Fallen)
        if (velocity.y < 0) {
            for (Platform platform : platforms) {
                if (bounds.overlaps(platform.getBounds())) {
                    // Für zerbrechliche Plattformen
                    if(platform instanceof BreakablePlatform) {
                        if(((BreakablePlatform) platform).isActive()) {
                            SoundManager.playJumpSound();
                            position.y = platform.getBounds().y + platform.getBounds().height;
                            velocity.y = JUMP_VELOCITY;
                            ((BreakablePlatform) platform).breakPlatform();
                            // Animation zurücksetzen
                            currentFrame = 0;
                            stateTime = 0;
                            isJumping = true;
                        }
                    } else {
                        // Normale Plattform-Kollision
                        SoundManager.playJumpSound();
                        position.y = platform.getBounds().y + platform.getBounds().height;
                        velocity.y = JUMP_VELOCITY;
                        // Animation zurücksetzen
                        currentFrame = 0;
                        stateTime = 0;
                        isJumping = true;
                    }
                    break;
                }
            }
        }
    }


    public void setVelocityY(float velY) {
        velocity.y = velY;
    }

    public void render(SpriteBatch batch) {

        if (jetpackActive) {
            jetpackEffect.render(batch);
        }


        batch.draw(jumpTextures[currentFrame],
            position.x,
            position.y,
            PLAYER_WIDTH,
            PLAYER_HEIGHT);
    }

    public void startJetpack() {
        jetpackActive = true;
        jetpackEffect.start();
    }

    public void stopJetpack() {
        jetpackActive = false;
        jetpackEffect.stop();
    }

    public void setVelocityX(float velX) {
        velocity.x = velX;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void dispose() {
        for(Texture texture : jumpTextures) {
            texture.dispose();
        }
        jetpackEffect.dispose();
    }
}

