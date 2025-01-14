package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MovingPlatform extends Platform {
    private static final float PLATFORM_WIDTH = 60;
    private static final float PLATFORM_HEIGHT = 8;
    private static final float MIN_WORLD_WIDTH = 480;
    private float startX;           // Startposition
    private float movementSpeed;    // Bewegungsgeschwindigkeit
    private float movementRange;    // Bewegungsreichweite
    private float currentDirection; // 1 für rechts, -1 für links
    private static final float MARGIN = 40f;

    public MovingPlatform(float x, float y) {
        super(x, y);
        this.startX = x;
        this.movementSpeed = 150f;
        this.movementRange = PLATFORM_WIDTH * 2;
        this.currentDirection = 1;
    }

    public void update(float delta) {
        float currentX = getBounds().x;

        // Prüfe ob die nächste Bewegung den Rand überschreiten würde
        float nextX = currentX + (currentDirection * movementSpeed * delta);

        // Richtungswechsel wenn Grenzen oder Rand erreicht würden
        if(nextX > startX + movementRange || nextX < startX ||
            nextX < MARGIN || nextX > (MIN_WORLD_WIDTH - MARGIN - PLATFORM_WIDTH)) {
            currentDirection *= -1;
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
