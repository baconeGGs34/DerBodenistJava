package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MovingPlatform extends Platform {
    private float startX;
    private float movementSpeed;
    private float movementRange;
    private float currentDirection;
    private static final float MARGIN = 40f;
    private static final float MIN_WORLD_WIDTH = 480;

    public MovingPlatform(float x, float y) {
        super(x, y);
        this.startX = x;
        this.movementSpeed = 150f;
        this.movementRange = getBounds().width * 2;
        this.currentDirection = 1;
    }

    public void update(float delta) {
        float currentX = getBounds().x;
        float nextX = currentX + (currentDirection * movementSpeed * delta);

        if(nextX > startX + movementRange || nextX < startX ||
            nextX < MARGIN || nextX > (MIN_WORLD_WIDTH - MARGIN - getBounds().width)) {
            currentDirection *= -1;
        }

        getBounds().x += currentDirection * movementSpeed * delta;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        shapeRenderer.setColor(Color.BLUE);
        shapeRenderer.rect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
    }
}
