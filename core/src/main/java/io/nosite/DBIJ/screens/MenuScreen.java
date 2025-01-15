package io.nosite.DBIJ.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
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
    private Rectangle startBounds, quitBounds;
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float MIN_WORLD_HEIGHT = 800;

    // Button Größen definieren
    private static final float BUTTON_WIDTH = 285;  // 95 * 3
    private static final float BUTTON_HEIGHT = 90;  // 30 * 3
    private static final float BUTTON_SPACING = 30; // Abstand zwischen den Buttons
    private ScoreManager scoreManager;
    private BitmapFont font;


    public MenuScreen() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        batch = ((Main)Gdx.app.getApplicationListener()).getBatch();
        scoreManager = new ScoreManager();
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("fonts/ThaleahFat.ttf"));
        FreeTypeFontParameter parameter = new FreeTypeFontParameter();
        font = FontManager.getFont();
        generator.dispose(); // Generator aufräumen


        startButtonTexture = new Texture("images/buttons/startbutton.png");
        quitButtonTexture = new Texture("images/buttons/quitbutton.png");

        // Kollisionsbereiche für die Buttons
        startBounds = new Rectangle(
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            MIN_WORLD_HEIGHT/2 + BUTTON_SPACING,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        quitBounds = new Rectangle(
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            MIN_WORLD_HEIGHT/2 - BUTTON_SPACING - BUTTON_HEIGHT,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );
    }

    @Override
    public void show() {

    }


    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        batch.begin();
        // Start Button
        batch.draw(startButtonTexture,
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            MIN_WORLD_HEIGHT/2 + BUTTON_SPACING,
            BUTTON_WIDTH,
            BUTTON_HEIGHT);

        // Quit Button
        batch.draw(quitButtonTexture,
            MIN_WORLD_WIDTH/2 - BUTTON_WIDTH/2,
            MIN_WORLD_HEIGHT/2 - BUTTON_SPACING - BUTTON_HEIGHT,
            BUTTON_WIDTH,
            BUTTON_HEIGHT);

        // Überschrift
        font.getData().setScale(1.5f);
        font.draw(batch, "HIGHSCORES:",
            MIN_WORLD_WIDTH/2 - 70,  // X-Position anpassen nach Bedarf
            MIN_WORLD_HEIGHT - 50);  // Oben mit etwas Abstand

// Highscores anzeigen
        font.getData().setScale(1.2f);
        int[] highScores = scoreManager.getHighScores();
        for (int i = 0; i < highScores.length; i++) {
            font.draw(batch,
                (i + 1) + ".  " + highScores[i],
                MIN_WORLD_WIDTH/2 - 50,  // X-Position
                MIN_WORLD_HEIGHT - 100 - (i * 40));  // Y-Position, 40 Pixel Abstand zwischen Einträgen
        }

        batch.end();

        // Klick-Erkennung
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if(startBounds.contains(touchPos.x, touchPos.y)) {
                ((Main)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
            } else if(quitBounds.contains(touchPos.x, touchPos.y)) {
                Gdx.app.exit();
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
        quitButtonTexture.dispose();
    }
}
