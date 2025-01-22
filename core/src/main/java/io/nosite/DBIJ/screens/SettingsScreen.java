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
import io.nosite.DBIJ.managers.PreferencesManager;
import io.nosite.DBIJ.managers.SoundManager;

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
    private static final float BUTTON_WIDTH = 180;
    private static final float BUTTON_HEIGHT = 60;
    private static final float BUTTON_BACK_WIDTH = 160;
    private static final float BUTTON_BACK_HEIGHT = 80;
    private Screen previousScreen;
    private GameScreen gameScreen;

    // Buttons
    private Texture soundButtonOnTexture, soundButtonOffTexture;
    private Texture gyroButtonOnTexture, gyroButtonOffTexture;
    private Texture backButtonTexture, backButtonPressedTexture;
    private Rectangle soundButtonBounds, gyroButtonBounds, backButtonBounds;
    private boolean backButtonPressed;
    private boolean soundEnabled;
    private boolean gyroEnabled;
    private PreferencesManager prefsManager;
    private boolean isAndroid;


    public SettingsScreen() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        batch = ((Main)Gdx.app.getApplicationListener()).getBatch();
        shapeRenderer = new ShapeRenderer();
        font = FontManager.getFont();
        glyphLayout = new GlyphLayout();
        prefsManager = PreferencesManager.getInstance();
        soundEnabled = prefsManager.isSoundEnabled();
        isAndroid = prefsManager.isAndroid();


        gyroEnabled = prefsManager.isGyroEnabled();

        // Texturen laden
        backgroundTexture = new Texture("images/bg.jpg");
        soundButtonOnTexture = new Texture("images/buttons/onbutton.png");
        soundButtonOffTexture = new Texture("images/buttons/offbutton.png");
        gyroButtonOnTexture = new Texture("images/buttons/onbutton.png");
        gyroButtonOffTexture = new Texture("images/buttons/offbutton.png");
        backButtonTexture = new Texture("images/buttons/leavebutton.png");
        backButtonPressedTexture = new Texture("images/buttons/leavebuttonpressed.png");


        // Button-Bereiche initialisieren
        float centerX = MIN_WORLD_WIDTH / 2 - BUTTON_WIDTH / 2;
        // Sound Button mittig im oberen Drittel
        soundButtonBounds = new Rectangle(centerX, MIN_WORLD_HEIGHT * 0.6f, BUTTON_WIDTH, BUTTON_HEIGHT);
        // Gyro Button mittig
        gyroButtonBounds = new Rectangle(centerX, MIN_WORLD_HEIGHT * 0.4f, BUTTON_WIDTH, BUTTON_HEIGHT);
        // Back Button wird in render() positioniert
        backButtonBounds = new Rectangle();

    }
    public SettingsScreen(Screen previousScreen, GameScreen gameScreen) {
        this(); // Ruft den ersten Konstruktor auf
        this.previousScreen = previousScreen;
        this.gameScreen = gameScreen;
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
        float baseY = camera.position.y - viewport.getWorldHeight() / 2;
        float offsetY = backgroundScrollPosition % bgHeight;

        for (int i = -1; i < 2; i++) {
            float y = baseY - offsetY + (i * bgHeight);
            batch.draw(backgroundTexture,
                camera.position.x - viewport.getWorldWidth() / 2,
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
            camera.position.x - viewport.getWorldWidth() / 2,
            camera.position.y - viewport.getWorldHeight() / 2,
            viewport.getWorldWidth(),
            viewport.getWorldHeight()
        );
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        // Text und Buttons in einem einzigen batch
        batch.begin();

        // Überschrift "Settings"
        font.getData().setScale(3.5f);
        glyphLayout.setText(font, "SETTINGS");
        font.draw(batch, "SETTINGS",
            camera.position.x - glyphLayout.width / 2,
            camera.position.y + viewport.getWorldHeight() / 2 - 50);

        // Sound Text und Labels
        font.getData().setScale(2.0f);
        glyphLayout.setText(font, "SOUND");
        font.draw(batch, "SOUND",
            camera.position.x - glyphLayout.width / 2,
            soundButtonBounds.y + BUTTON_HEIGHT + 50);

        font.getData().setScale(1.4f);
        font.draw(batch, "ON",
            soundButtonBounds.x - 50,
            soundButtonBounds.y + BUTTON_HEIGHT / 2);
        font.draw(batch, "OFF",
            soundButtonBounds.x + BUTTON_WIDTH + 20,
            soundButtonBounds.y + BUTTON_HEIGHT / 2);

        // Gyro Text und Labels
        font.getData().setScale(2.0f);
        glyphLayout.setText(font, "Controls");
        font.draw(batch, "Controls",
            camera.position.x - glyphLayout.width / 2,
            gyroButtonBounds.y + BUTTON_HEIGHT + 50);

        font.getData().setScale(1.4f);
        font.draw(batch, "Gyro",
            gyroButtonBounds.x - 85,
            gyroButtonBounds.y + BUTTON_HEIGHT / 2);
        font.draw(batch, "Buttons",
            gyroButtonBounds.x + BUTTON_WIDTH + 20,
            gyroButtonBounds.y + BUTTON_HEIGHT / 2);

        // Sound Button
        batch.draw(soundEnabled ? soundButtonOnTexture : soundButtonOffTexture,
            soundButtonBounds.x, soundButtonBounds.y,
            BUTTON_WIDTH, BUTTON_HEIGHT);

        // Gyro Button
        if(!isAndroid) {
            batch.setColor(0.5f, 0.5f, 0.5f, 1f);  // Grau für Desktop
        }
        batch.draw(gyroEnabled ? gyroButtonOnTexture : gyroButtonOffTexture,
            gyroButtonBounds.x, gyroButtonBounds.y,
            BUTTON_WIDTH, BUTTON_HEIGHT);
        if(!isAndroid) {
            batch.setColor(Color.WHITE);  // Farbe zurücksetzen
        }

        // Back Button
        batch.draw(backButtonPressed ? backButtonPressedTexture : backButtonTexture,
            backButtonBounds.x, backButtonBounds.y,
            BUTTON_BACK_WIDTH, BUTTON_BACK_HEIGHT);

        batch.end();

        // Back Button Bounds aktualisieren
        backButtonBounds.set(
            camera.position.x - BUTTON_BACK_WIDTH / 2,
            camera.position.y - viewport.getWorldHeight() / 2 + 50,
            BUTTON_BACK_WIDTH,
            BUTTON_BACK_HEIGHT
        );

        // Klick-Erkennung
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if(soundButtonBounds.contains(touchPos.x, touchPos.y)) {
                soundEnabled = !soundEnabled;
                prefsManager.setSoundEnabled(soundEnabled);
                SoundManager.setSoundEnabled(soundEnabled);
                Gdx.app.log("SettingsScreen", "Sound toggled to: " + soundEnabled);
            } else if(gyroButtonBounds.contains(touchPos.x, touchPos.y) && isAndroid) {
                gyroEnabled = !gyroEnabled;
                prefsManager.setGyroEnabled(gyroEnabled);
                Gdx.app.log("SettingsScreen", "Gyro enabled: " + gyroEnabled);
            } else if(backButtonBounds.contains(touchPos.x, touchPos.y)) {
                backButtonPressed = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Main)Gdx.app.getApplicationListener()).setScreen(previousScreen);
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
    public void show() {
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
}
