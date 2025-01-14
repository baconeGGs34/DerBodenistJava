package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;  // Für Kollisionen
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;

public class Platform {

    private Rectangle bounds;
    private ShapeRenderer shapeRenderer;

    private static final float PLATFORM_WIDTH = 70;
    private static final float PLATFORM_HEIGHT = 10;

    public Platform(float x, float y) {
        // Erstellt ein Rechteck an der gegebenen Position mit fester Größe
        bounds = new Rectangle(x, y, PLATFORM_WIDTH, PLATFORM_HEIGHT);
        // ShapeRenderer für's Zeichnen
        shapeRenderer = new ShapeRenderer();
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.GREEN);  // Grüne Plattformen
        shapeRenderer.rect(bounds.x, bounds.y, bounds.width, bounds.height);
    }

    public void dispose() {
        shapeRenderer.dispose();
    }

}
