package io.nosite.DBIJ.screens;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.managers.FontManager;
import io.nosite.DBIJ.managers.ScoreManager;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;

public class MenuScreen implements Screen {

    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Texture startButtonTexture;
    private Texture quitButtonTexture;
    private Texture startButtonPressedTexture;
    private Texture quitButtonPressedTexture;
    private Texture settingsButtonTexture;
    private Texture settingsButtonPressedTexture;
    private Rectangle startBounds, quitBounds, settingsBounds;
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float MIN_WORLD_HEIGHT = 800;
    private Texture backgroundTexture;
    private boolean startButtonIsPressed = false;
    private boolean quitButtonIsPressed = false;
    private boolean settingsButtonIsPressed = false;
    private float backgroundScrollPosition = 0;
    private static final float SCROLL_SPEED = 30f;

    // Button Größen definieren
    private static final float BUTTON_WIDTH = 285;  // 95 * 3
    private static final float BUTTON_HEIGHT = 90;  // 30 * 3
    private static final float BUTTON_SPACING = 25; // Abstand zwischen den Buttons
    private static final float TOP_THIRD_Y = MIN_WORLD_HEIGHT * 2/3;
    private static final float VERTICAL_SPACING = BUTTON_HEIGHT + 20;  // Buttonhöhe + 20px Abstand
    private ScoreManager scoreManager;
    private BitmapFont font;
    private ShapeRenderer shapeRenderer;


    public MenuScreen() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        batch = ((Main)Gdx.app.getApplicationListener()).getBatch();
        scoreManager = new ScoreManager();
        shapeRenderer = new ShapeRenderer();

        // Rectangles initialisieren
        startBounds = new Rectangle();
        settingsBounds = new Rectangle();
        quitBounds = new Rectangle();

        // Textures laden
        startButtonTexture = new Texture("images/buttons/startbutton.png");
        quitButtonTexture = new Texture("images/buttons/quitbutton.png");
        startButtonPressedTexture = new Texture("images/buttons/startbuttonpressed.png");
        quitButtonPressedTexture = new Texture("images/buttons/quitbuttonpressed.png");
        settingsButtonTexture = new Texture("images/buttons/settingsbutton.png");
        settingsButtonPressedTexture = new Texture("images/buttons/settingsbuttonpressed.png");
        backgroundTexture = new Texture("images/bg.jpg");
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);


        startBounds.set(
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            TOP_THIRD_Y + BUTTON_SPACING,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        settingsBounds.set(
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            TOP_THIRD_Y - BUTTON_HEIGHT - BUTTON_SPACING,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        quitBounds.set(
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            TOP_THIRD_Y - (2 * BUTTON_HEIGHT) - (2 * BUTTON_SPACING),
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );
    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {

        GlyphLayout glyphLayout = new GlyphLayout();

        // Hintergrund-Scroll aktualisieren
        backgroundScrollPosition += SCROLL_SPEED * delta;

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

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

        // Halbtransparente Overlay-Schicht
        Gdx.gl.glEnable(GL20.GL_BLEND);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.5f);
        shapeRenderer.rect(
            camera.position.x - viewport.getWorldWidth()/2,  // x-Position
            camera.position.y - viewport.getWorldHeight()/2, // y-Position
            viewport.getWorldWidth(),    // Breite
            viewport.getWorldHeight()    // Höhe
        );
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        float topButtonY = TOP_THIRD_Y;

        // Start Button (oben)
        batch.draw(startButtonIsPressed ? startButtonPressedTexture : startButtonTexture,
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            topButtonY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT);

// Settings Button (mitte)
        batch.draw(settingsButtonIsPressed ? settingsButtonPressedTexture : settingsButtonTexture,
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            topButtonY - VERTICAL_SPACING,
            BUTTON_WIDTH,
            BUTTON_HEIGHT);

// Quit Button (unten)
        batch.draw(quitButtonIsPressed ? quitButtonPressedTexture : quitButtonTexture,
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            topButtonY - (2 * VERTICAL_SPACING),
            BUTTON_WIDTH,
            BUTTON_HEIGHT);


        // Highscores in der unteren Hälfte
        font = FontManager.getFont();
        font.getData().setScale(1.5f);
        String highscoreTitle = "HIGHSCORES";
        glyphLayout.setText(font, highscoreTitle);
        font.draw(batch, highscoreTitle,
            camera.position.x - glyphLayout.width/2,
            camera.position.y - viewport.getWorldHeight()/4);

        font.getData().setScale(1.2f);
        int[] highScores = scoreManager.getHighScores();
        for (int i = 0; i < highScores.length; i++) {
            String scoreEntry = (i + 1) + ".  " + highScores[i];
            glyphLayout.setText(font, scoreEntry);
            font.draw(batch, scoreEntry,
                camera.position.x - glyphLayout.width/2,
                camera.position.y - viewport.getWorldHeight()/4 - ((i + 1) * 40));
        }
        batch.end();

        startBounds.set(
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            topButtonY,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        settingsBounds.set(
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            topButtonY - VERTICAL_SPACING,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        quitBounds.set(
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            topButtonY - (2 * VERTICAL_SPACING),
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        // Klick-Logik
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if(startBounds.contains(touchPos.x, touchPos.y)) {
                startButtonIsPressed = true;
                // Kurze Verzögerung vor dem Screenwechsel
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Main)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
                    }
                }, 0.2f);
            } else if (settingsBounds.contains(touchPos.x, touchPos.y)){
                settingsButtonIsPressed = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Main)Gdx.app.getApplicationListener()).setScreen(new SettingsScreen());
                    }
                }, 0.2f);
            } else if(quitBounds.contains(touchPos.x, touchPos.y)) {
                quitButtonIsPressed = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        Gdx.app.exit();
                    }
                }, 0.2f);
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
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
        startButtonTexture.dispose();
        settingsButtonPressedTexture.dispose();
        quitButtonTexture.dispose();
        quitButtonPressedTexture.dispose();
        settingsButtonTexture.dispose();
        settingsButtonPressedTexture.dispose();
        backgroundTexture.dispose();
        shapeRenderer.dispose();
    }
}
