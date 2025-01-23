package io.nosite.DBIJ.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public class PowerUp {
    private Vector2 position;
    private Rectangle bounds;
    private boolean active;
    private static final float SIZE = 40f;
    private Texture texture;
    private Platform platform;

    public PowerUp(float x, float y, Platform platform) {
        position = new Vector2(x, y);
        bounds = new Rectangle(x, y, SIZE, SIZE);
        active = true;
        this.platform = platform;
        texture = new Texture("images/jetpack.png");
    }

    public void update() {
        position.x = platform.getBounds().x + platform.getBounds().width/2 - SIZE/2;
        position.y = platform.getBounds().y + platform.getBounds().height;
        bounds.setPosition(position.x, position.y);
    }

    public void render(SpriteBatch batch) {
        if (active) {
            batch.draw(texture, position.x, position.y, SIZE, SIZE);
        }
    }

    public Rectangle getBounds() {
        return bounds;
    }

    public boolean isActive() {
        return active;
    }

    public void collect() {
        active = false;
    }

    public void dispose() {
        texture.dispose();
    }
}
