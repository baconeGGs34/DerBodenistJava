package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;  // Für Kollisionen
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

public class Platform {
    protected Rectangle bounds;
    private static final float PLATFORM_WIDTH = 60;
    private static final float PLATFORM_HEIGHT = 8;

    public Platform(float x, float y) {
        bounds = new Rectangle(x, y, PLATFORM_WIDTH, PLATFORM_HEIGHT);
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GREEN);
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }
}
