package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class JetpackEffect {
    private static final int FRAME_COUNT = 18;
    private static final float ANIMATION_FRAME_DURATION = 0.05f;
    private Texture[] frames;
    private float stateTime;
    private float x, y;
    private int currentFrame;
    private boolean isActive;


    public JetpackEffect() {
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

            // Position unter Spieler
            this.x = playerX - 17;
            this.y = playerY - 130;
        }
    }

    public void render(SpriteBatch batch) {
        if (isActive) {
            batch.draw(frames[currentFrame], x, y, 35, 150);
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
