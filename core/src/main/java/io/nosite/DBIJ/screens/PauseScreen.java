package io.nosite.DBIJ.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.nosite.DBIJ.Main;

public class PauseScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Texture backgroundTexture;
    private BitmapFont font;
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float MIN_WORLD_HEIGHT = 800;
    private static final float BUTTON_WIDTH = 285;
    private static final float BUTTON_HEIGHT = 90;
    private static final float BUTTON_SPACING = 50;
    private float backgroundScrollPosition = 0;
    private static final float SCROLL_SPEED = 30f;

    private Texture resumeButtonTexture, resumeButtonPressedTexture;
    private Texture settingsButtonTexture, settingsButtonPressedTexture;
    private Texture quitButtonTexture, quitButtonPressedTexture;
    private Rectangle resumeBounds, settingsBounds, quitBounds;
    private boolean resumePressed, settingsPressed, quitPressed;
    private GameScreen gameScreen;  // Referenz zum GameScreen

    public PauseScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        // ... Rest der Initialisierung wie im MenuScreen ...
    }

    @Override
    public void show() {

    }

    @Override
    public void render(float delta) {
        // Hintergrund und Overlay wie im MenuScreen

        // Buttons zentriert untereinander
        batch.begin();
        float centerX = MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2;
        float topY = MIN_WORLD_HEIGHT * 0.7f;

        // Resume Button
        batch.draw(resumePressed ? resumeButtonPressedTexture : resumeButtonTexture,
            centerX, topY, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Settings Button
        batch.draw(settingsPressed ? settingsButtonPressedTexture : settingsButtonTexture,
            centerX, topY - BUTTON_HEIGHT - BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Quit Button
        batch.draw(quitPressed ? quitButtonPressedTexture : quitButtonTexture,
            centerX, topY - (2 * BUTTON_HEIGHT) - (2 * BUTTON_SPACING),
            BUTTON_WIDTH, BUTTON_HEIGHT);
        batch.end();

        // Button Logik
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if(resumeBounds.contains(touchPos.x, touchPos.y)) {
                ((Main)Gdx.app.getApplicationListener()).setScreen(gameScreen);
            } else if(settingsBounds.contains(touchPos.x, touchPos.y)) {
                ((Main)Gdx.app.getApplicationListener()).setScreen(
                    new SettingsScreen(this));  // PauseScreen als previous screen
            } else if(quitBounds.contains(touchPos.x, touchPos.y)) {
                ((Main)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            }
        }
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }
}
