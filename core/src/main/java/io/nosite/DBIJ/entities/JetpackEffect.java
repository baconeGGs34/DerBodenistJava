package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class JetpackEffect {
    private static final int FRAME_COUNT = 18;  // 0-17 = 18 Frames
    private static final float ANIMATION_FRAME_DURATION = 0.05f;
    private Texture[] frames;
    private float stateTime;
    private float x, y;
    private int currentFrame;
    private boolean isActive;


    public JetpackEffect() {
        // Frames laden
        frames = new Texture[FRAME_COUNT];
        for(int i = 0; i < FRAME_COUNT; i++) {
            frames[i] = new Texture("images/boostframes/boost" + i + ".png");
        }

        stateTime = 0f;
        isActive = false;
    }

    public void update(float delta, float playerX, float playerY) {
        if (isActive) {
            stateTime += delta;
            if (stateTime >= ANIMATION_FRAME_DURATION) {
                stateTime = 0;
                currentFrame = (currentFrame + 1) % FRAME_COUNT;
            }

            // Position unter dem Spieler
            this.x = playerX - 17;  // Horizontaler Offset für Zentrierung
            this.y = playerY - 130;   // Vertikaler Offset
        }
    }

    public void render(SpriteBatch batch) {
        if (isActive) {
            batch.draw(frames[currentFrame], x, y, 35, 150);  // Größe anpassen falls nötig
        }
    }

    public void start() {
        isActive = true;
        currentFrame = 0;
        stateTime = 0;
    }

    public void stop() {
        isActive = false;
    }

    public void dispose() {
        for(Texture frame : frames) {
            frame.dispose();
        }
    }
}
