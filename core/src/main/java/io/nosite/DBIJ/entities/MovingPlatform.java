package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MovingPlatform extends Platform {
    private static final float PLATFORM_WIDTH = 60;
    private static final float PLATFORM_HEIGHT = 8;
    private float startX;           // Startposition
    private float movementSpeed;    // Bewegungsgeschwindigkeit
    private float movementRange;    // Bewegungsreichweite
    private float currentDirection; // 1 für rechts, -1 für links

    public MovingPlatform(float x, float y) {
        super(x, y);
        this.startX = x;
        this.movementSpeed = 150f;  // Geschwindigkeit anpassbar
        this.movementRange = PLATFORM_WIDTH * 2;  // Doppelte Plattformlänge
        this.currentDirection = 1;
    }

    public void update(float delta) {
        // Aktuelle Position der Plattform
        float currentX = getBounds().x;

        // Richtungswechsel wenn Grenzen erreicht
        if(currentX > startX + movementRange) {
            currentDirection = -1;
        } else if(currentX < startX) {
            currentDirection = 1;
        }

        // Bewege Plattform
        getBounds().x += currentDirection * movementSpeed * delta;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.BLUE);  // Andere Farbe für bewegliche Plattformen
        shapeRenderer.rect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
    }
}
