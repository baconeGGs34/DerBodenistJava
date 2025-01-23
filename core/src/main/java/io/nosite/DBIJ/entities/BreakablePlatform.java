package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class BreakablePlatform extends Platform {
    private boolean isBroken;

    public BreakablePlatform(float x, float y) {
        super(x, y);
        this.isBroken = false;
    }

    @Override
    public void render(ShapeRenderer shapeRenderer) {
        if (!isBroken) {
            shapeRenderer.setColor(Color.ORANGE);
            shapeRenderer.rect(getBounds().x, getBounds().y, getBounds().width, getBounds().height);
        }
    }

    public boolean isActive() {
        return !isBroken;
    }

    public void breakPlatform() {
        this.isBroken = true;
    }
}
