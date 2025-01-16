package io.nosite.DBIJ.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.managers.FontManager;

public class SettingsScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Texture backgroundTexture;
    private BitmapFont font;
    private GlyphLayout glyphLayout;
    private float backgroundScrollPosition = 0;
    private static final float SCROLL_SPEED = 30f;
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float MIN_WORLD_HEIGHT = 800;
    private static final float BUTTON_WIDTH = 285;
    private static final float BUTTON_HEIGHT = 90;
    private static final float BUTTON_SPACING = 50;

    // Buttons
    private Texture soundButtonOnTexture, soundButtonOffTexture;
    private Texture gyroButtonOnTexture, gyroButtonOffTexture;
    private Texture backButtonTexture, backButtonPressedTexture;
    private Rectangle soundButtonBounds, gyroButtonBounds, backButtonBounds;
    private boolean soundButtonPressed, gyroButtonPressed, backButtonPressed;
    private boolean soundEnabled = true;
    private boolean gyroEnabled = true;

    public SettingsScreen() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        batch = ((Main)Gdx.app.getApplicationListener()).getBatch();
        shapeRenderer = new ShapeRenderer();
        font = FontManager.getFont();
        glyphLayout = new GlyphLayout();

        // Texturen laden
        backgroundTexture = new Texture("images/bg.jpg");
        soundButtonOnTexture = new Texture("images/buttons/onbutton.png");
        soundButtonOffTexture = new Texture("images/buttons/offbutton.png");
        gyroButtonOnTexture = new Texture("images/buttons/onbutton.png");
        gyroButtonOffTexture = new Texture("images/buttons/offbutton.png");
        backButtonTexture = new Texture("images/buttons/buttonleft.png");
        backButtonPressedTexture = new Texture("images/buttons/buttonleft.png");

        // Button-Bereiche initialisieren
        float centerX = MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2;
        float topY = MIN_WORLD_HEIGHT/2 + BUTTON_HEIGHT;

        soundButtonBounds = new Rectangle(centerX, topY, BUTTON_WIDTH, BUTTON_HEIGHT);
        gyroButtonBounds = new Rectangle(centerX, topY - BUTTON_HEIGHT - BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT);
        backButtonBounds = new Rectangle(centerX, topY - (2 * BUTTON_HEIGHT) - (2 * BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    @Override
    public void render(float delta) {
        // Background Scroll
        backgroundScrollPosition += SCROLL_SPEED * delta;

        // Screen clearen
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Scrollender Hintergrund
        batch.begin();
        float bgHeight = backgroundTexture.getHeight();
        float baseY = camera.position.y - viewport.getWorldHeight()/2;
        float offsetY = backgroundScrollPosition % bgHeight;

        for(int i = -1; i < 2; i++) {
            float y = baseY - offsetY + (i * bgHeight);
            batch.draw(backgroundTexture,
                camera.position.x - viewport.getWorldWidth()/2,
                y,
                viewport.getWorldWidth(),
                bgHeight);
        }
        batch.end();

        // Halbtransparenter Overlay
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(
            camera.position.x - viewport.getWorldWidth()/2,
            camera.position.y - viewport.getWorldHeight()/2,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Buttons zeichnen
        batch.begin();
        // Sound Button
        batch.draw(soundEnabled ? soundButtonOnTexture : soundButtonOffTexture,
            soundButtonBounds.x, soundButtonBounds.y,
            BUTTON_WIDTH, BUTTON_HEIGHT);

        // Gyro Button
        batch.draw(gyroEnabled ? gyroButtonOnTexture : gyroButtonOffTexture,
            gyroButtonBounds.x, gyroButtonBounds.y,
            BUTTON_WIDTH, BUTTON_HEIGHT);

        // Back Button
        batch.draw(backButtonPressed ? backButtonPressedTexture : backButtonTexture,
            backButtonBounds.x, backButtonBounds.y,
            BUTTON_WIDTH, BUTTON_HEIGHT);
        batch.end();

        // Klick-Erkennung
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if(soundButtonBounds.contains(touchPos.x, touchPos.y)) {
                soundEnabled = !soundEnabled;
                // Hier Sound Einstellungen speichern/ändern
            } else if(gyroButtonBounds.contains(touchPos.x, touchPos.y)) {
                gyroEnabled = !gyroEnabled;
                // Hier Gyro Einstellungen speichern/ändern
            } else if(backButtonBounds.contains(touchPos.x, touchPos.y)) {
                backButtonPressed = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Main)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
                    }
                }, 0.1f);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        shapeRenderer.dispose();
        backgroundTexture.dispose();
        soundButtonOnTexture.dispose();
        soundButtonOffTexture.dispose();
        gyroButtonOnTexture.dispose();
        gyroButtonOffTexture.dispose();
        backButtonTexture.dispose();
        backButtonPressedTexture.dispose();
    }

    @Override
    public void show() {}

    @Override
    public void pause() {}

    @Override
    public void resume() {}

    @Override
    public void hide() {}
}
