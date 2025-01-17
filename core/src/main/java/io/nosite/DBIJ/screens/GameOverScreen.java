package io.nosite.DBIJ.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.managers.FontManager;
import io.nosite.DBIJ.managers.ScoreManager;

public class GameOverScreen implements Screen {
    private final int finalScore;
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Texture backgroundTexture;
    private Texture startButtonTexture;
    private Texture startButtonPressedTexture;
    private Texture leaveButtonTexture;
    private Texture leaveButtonPressedTexture;
    private Rectangle startButtonBounds;
    private Rectangle leaveButtonBounds;
    private BitmapFont font;
    private ScoreManager scoreManager;
    private GlyphLayout glyphLayout;
    private float backgroundScrollPosition = 0;
    private boolean startButtonIsPressed = false;
    private boolean leaveButtonIsPressed = false;
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float MIN_WORLD_HEIGHT = 800;
    private static final float BUTTON_WIDTH = 285;
    private static final float BUTTON_HEIGHT = 90;
    private static final float BUTTON_SPACING = 50;
    private static final float SCROLL_SPEED = 30f;
    private static final Color SCORE_COLOR = new Color(1, 1, 0, 1);

    public GameOverScreen(int finalScore) {
        this.finalScore = finalScore;
        this.glyphLayout = new GlyphLayout();
        this.camera = new OrthographicCamera();
        this.viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        this.batch = ((Main) Gdx.app.getApplicationListener()).getBatch();
        this.shapeRenderer = new ShapeRenderer();
        this.scoreManager = new ScoreManager();
        this.font = FontManager.getFont();

        // Lade Texturen
        backgroundTexture = new Texture("images/bg.jpg");
        startButtonTexture = new Texture("images/buttons/startbutton.png");
        startButtonPressedTexture = new Texture("images/buttons/startbuttonpressed.png");
        leaveButtonTexture = new Texture("images/buttons/leavebutton.png");
        leaveButtonPressedTexture = new Texture("images/buttons/leavebuttonpressed.png");

        // Button Bounds initialisieren
        startButtonBounds = new Rectangle();
        leaveButtonBounds = new Rectangle();

        // Score speichern
        scoreManager.addScore(finalScore);
    }

    @Override
    public void render(float delta) {
        // Background Scroll
        backgroundScrollPosition += SCROLL_SPEED * delta;

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

        batch.begin();

        // Score anzeigen
        font.getData().setScale(3.0f);
        font.setColor(SCORE_COLOR);
        String scoreText = "SCORE: " + finalScore;
        glyphLayout.setText(font, scoreText);
        float scoreY = camera.position.y + viewport.getWorldHeight()/3;
        font.draw(batch, scoreText,
            camera.position.x - glyphLayout.width/2,
            scoreY);

        // Neuer Highscore Text
        if(scoreManager.isHighScore(finalScore)) {
            font.getData().setScale(2.0f);
            String newHighscoreText = "NEW HIGHSCORE!";
            glyphLayout.setText(font, newHighscoreText);
            font.draw(batch, newHighscoreText,
                camera.position.x - glyphLayout.width/2,
                scoreY - 50);
        }

        // Highscores (mittig)
        font.setColor(Color.WHITE);
        font.getData().setScale(1.5f);
        String highscoreTitle = "HIGHSCORES:";
        glyphLayout.setText(font, highscoreTitle);
// Ursprüngliche Position war: camera.position.y
        float highscoreY = camera.position.y + (viewport.getWorldHeight() * 0.2f); // 20% höher
        font.draw(batch, highscoreTitle,
            camera.position.x - glyphLayout.width/2,
            highscoreY);

        font.getData().setScale(1.2f);
        int[] highScores = scoreManager.getHighScores();
        for (int i = 0; i < highScores.length; i++) {
            String scoreEntry = (i + 1) + ".  " + highScores[i];
            glyphLayout.setText(font, scoreEntry);
            font.draw(batch, scoreEntry,
                camera.position.x - glyphLayout.width/2,
                highscoreY - ((i + 1) * 40));
        }

        // Buttons im unteren Drittel
        float bottomThirdY = camera.position.y - viewport.getWorldHeight()/3;

        batch.draw(startButtonIsPressed ? startButtonPressedTexture : startButtonTexture,
            camera.position.x - BUTTON_WIDTH/2,
            bottomThirdY + BUTTON_SPACING,
            BUTTON_WIDTH,
            BUTTON_HEIGHT);

        batch.draw(leaveButtonIsPressed ? leaveButtonPressedTexture : leaveButtonTexture,
            camera.position.x - BUTTON_WIDTH/2,
            bottomThirdY - BUTTON_HEIGHT,
            BUTTON_WIDTH,
            BUTTON_HEIGHT);

        batch.end();

        // Button Bounds aktualisieren
        startButtonBounds.set(
            camera.position.x - BUTTON_WIDTH/2,
            bottomThirdY + BUTTON_SPACING,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        leaveButtonBounds.set(
            camera.position.x - BUTTON_WIDTH/2,
            bottomThirdY - BUTTON_HEIGHT,
            BUTTON_WIDTH,
            BUTTON_HEIGHT
        );

        // Klick-Erkennung
        if(Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if(startButtonBounds.contains(touchPos.x, touchPos.y)) {
                startButtonIsPressed = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Main)Gdx.app.getApplicationListener()).setScreen(new GameScreen());
                    }
                }, 0.1f);
            } else if(leaveButtonBounds.contains(touchPos.x, touchPos.y)) {
                leaveButtonIsPressed = true;
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
        startButtonTexture.dispose();
        startButtonPressedTexture.dispose();
        leaveButtonTexture.dispose();
        leaveButtonPressedTexture.dispose();
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
