package io.nosite.DBIJ.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.entities.BreakablePlatform;
import io.nosite.DBIJ.entities.MovingPlatform;
import io.nosite.DBIJ.entities.Platform;
import io.nosite.DBIJ.entities.Player;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import io.nosite.DBIJ.managers.FontManager;
import io.nosite.DBIJ.managers.ScoreManager;

public class GameScreen implements Screen {

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch batch;
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float MIN_WORLD_HEIGHT = 800;
    private Player player;  // Unser Spieler-Objekt
    private Array<Platform> platforms;
    private float highscore = 0;  // Höchste erreichte Position
    private boolean gameOver = false;  // Spielstatus
    private static final float GAME_OVER_THRESHOLD = 450;  // Wie weit der Spieler fallen darf
    private ShapeRenderer shapeRenderer;  // Für das Zeichnen von Formen
    private BitmapFont font;
    private Texture backgroundTexture;
    private TextureRegion backgroundRegion;
    private ScoreManager scoreManager;
    private Texture startButtonTexture;
    private Texture leaveButtonTexture;
    private Rectangle startButtonBounds, leaveButtonBounds;
    private static final float BUTTON_WIDTH = 285;  // 3x Originalgröße wie im Menu
    private static final float BUTTON_HEIGHT = 90;
    private static final float BUTTON_SPACING = 50;
    private GlyphLayout glyphLayout = new GlyphLayout();
    private Texture startButtonPressedTexture;
    private Texture leaveButtonPressedTexture;
    private boolean startButtonIsPressed = false;
    private boolean leaveButtonIsPressed = false;
    private float backgroundScrollPosition = 0;
    private static final float SCROLL_SPEED = 30f;
    private static final Color SCORE_COLOR = new Color(1, 1, 0, 1); // Gelb

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        player = new Player(MIN_WORLD_WIDTH / 2, 400,true);
        batch = ((Main) Gdx.app.getApplicationListener()).getBatch();
        shapeRenderer = new ShapeRenderer();
        scoreManager = new ScoreManager();
        startButtonTexture = new Texture("images/buttons/startbutton.png");
        leaveButtonTexture = new Texture("images/buttons/leavebutton.png");
        font = FontManager.getFont();

        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);

        backgroundTexture = new Texture(Gdx.files.internal("images/bg.jpg"));
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        float margin = 40f;
        float platformSpawnAreaWidth = MIN_WORLD_WIDTH - (2 * margin) - 70;

        platforms = new Array<>();
        platforms.add(new Platform(MIN_WORLD_WIDTH / 2 - 35, 200));

        float nextY = 400;
        for (int i = 0; i < 10; i++) {
            int numSections = 3;
            float sectionWidth = platformSpawnAreaWidth / numSections;
            int randomSection = MathUtils.random(numSections - 1);
            float randomOffset = MathUtils.random(sectionWidth * 0.8f);
            float randomX = margin + (randomSection * sectionWidth) + randomOffset;
            int platformType = MathUtils.random(100);

            if (platformType < 15) {  // 15% Chance für bewegende Plattform
                platforms.add(new MovingPlatform(randomX, nextY));
            } else if (platformType < 30) {  // 15% Chance für zerbrechliche Plattform
                platforms.add(new BreakablePlatform(randomX, nextY));
            } else {  // 70% Chance für normale Plattform
                platforms.add(new Platform(randomX, nextY));
            }

            nextY += MathUtils.random(110, 150);
        }
    }


    @Override
    public void render(float delta) {
        // Screen clearen
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Kamera updaten
        camera.update();

        // SpriteBatch mit Kamera-Koordinaten konfigurieren
        batch.setProjectionMatrix(camera.combined);
        shapeRenderer.setProjectionMatrix(camera.combined);

        // Spiellogik
        player.update(delta, platforms);
        highscore = Math.max(highscore, player.getPosition().y);

        // Game Over Check
        if (player.getPosition().y < camera.position.y - GAME_OVER_THRESHOLD) {
            ((Main)Gdx.app.getApplicationListener()).setScreen(new GameOverScreen((int)(highscore/100)));
            return;
        }

        // Kamera folgt dem Spieler nur nach oben
        if (player.getPosition().y > camera.position.y) {
            camera.position.y = player.getPosition().y;
            camera.update();
        }
        updatePlatforms();

        for(Platform platform : platforms) {
            if(platform instanceof MovingPlatform) {
                ((MovingPlatform) platform).update(delta);
            }
        }

        // Hintergrund rendern
        batch.begin();
        float bgHeight = backgroundTexture.getHeight();
        float baseY = camera.position.y - viewport.getWorldHeight()/2;
        float offsetY = baseY % bgHeight;
        for(int i = -1; i < 2; i++) {
            float y = baseY - offsetY + (i * bgHeight);
            batch.draw(backgroundTexture,
                camera.position.x - viewport.getWorldWidth()/2,
                y,
                viewport.getWorldWidth(),
                bgHeight);
        }
        batch.end();

        // Ränder zeichnen
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.DARK_GRAY);
        shapeRenderer.rect(
            camera.position.x - viewport.getWorldWidth()/2,
            camera.position.y - viewport.getWorldHeight()/2,
            40,
            viewport.getWorldHeight()
        );
        shapeRenderer.rect(
            camera.position.x + viewport.getWorldWidth()/2 - 40,
            camera.position.y - viewport.getWorldHeight()/2,
            40,
            viewport.getWorldHeight()
        );
        shapeRenderer.end();

        // Plattformen rendern
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Platform platform : platforms) {
            platform.render(shapeRenderer);
        }
        shapeRenderer.end();

        // Player rendern
        batch.begin();
        player.render(batch);
        batch.end();

        // Score rendern
        batch.begin();
        font = FontManager.getFont();
        font.getData().setScale(1.0f);
        font.draw(batch, "Score: " + (int)(highscore/100),
            camera.position.x - viewport.getWorldWidth()/2 + 20,
            camera.position.y + viewport.getWorldHeight()/2 - 20);
        batch.end();
    }

    private void resetGame() {
        gameOver = false;
        highscore = 0;
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
//        player = new Player(MIN_WORLD_WIDTH / 2, 400,false);

        float margin = 40f;
        float platformSpawnAreaWidth = MIN_WORLD_WIDTH - (2 * margin) - 70;

        platforms.clear();
        platforms.add(new Platform(MIN_WORLD_WIDTH / 2 - 35, 200));

        float nextY = 400;
        for (int i = 0; i < 10; i++) {
            int numSections = 3;
            float sectionWidth = platformSpawnAreaWidth / numSections;
            int randomSection = MathUtils.random(numSections - 1);
            float randomOffset = MathUtils.random(sectionWidth * 0.8f);
            float randomX = margin + (randomSection * sectionWidth) + randomOffset;

            int platformType = MathUtils.random(100);
            if (platformType < 15) {  // 15% Chance für bewegende Plattform
                platforms.add(new MovingPlatform(randomX, nextY));
            } else if (platformType < 30) {  // 15% Chance für zerbrechliche Plattform
                platforms.add(new BreakablePlatform(randomX, nextY));
            } else {  // 70% Chance für normale Plattform
                platforms.add(new Platform(randomX, nextY));
            }

            nextY += MathUtils.random(110, 150);
        }
    }

    private void updatePlatforms() {
        float margin = 40f;
        float platformSpawnAreaWidth = MIN_WORLD_WIDTH - (2 * margin) - 70;

        // Alte Plattformen entfernen
        for (int i = platforms.size - 1; i >= 0; i--) {
            Platform platform = platforms.get(i);
            if (platform.getBounds().y < camera.position.y - viewport.getWorldHeight() ||
                (platform instanceof BreakablePlatform && !((BreakablePlatform) platform).isActive())) {
                platforms.removeIndex(i);
            }
        }

        // Höchste Plattform finden
        float highestPlatformY = 0;
        for (Platform platform : platforms) {
            highestPlatformY = Math.max(highestPlatformY, platform.getBounds().y);
        }

        // Neue Plattformen generieren
        while (highestPlatformY < camera.position.y + viewport.getWorldHeight()) {
            int numSections = 3;
            float sectionWidth = platformSpawnAreaWidth / numSections;
            int randomSection = MathUtils.random(numSections - 1);
            float randomOffset = MathUtils.random(sectionWidth * 0.8f);
            float randomX = margin + (randomSection * sectionWidth) + randomOffset;

            int platformType = MathUtils.random(100);
            if (platformType < 15) {
                platforms.add(new MovingPlatform(randomX, highestPlatformY + MathUtils.random(110, 150)));
            } else if (platformType < 30) {
                platforms.add(new BreakablePlatform(randomX, highestPlatformY + MathUtils.random(110, 150)));
            } else {
                platforms.add(new Platform(randomX, highestPlatformY + MathUtils.random(110, 150)));
            }

            highestPlatformY += MathUtils.random(110, 150);
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
        shapeRenderer.dispose();
        backgroundTexture.dispose();
        startButtonTexture.dispose();
        startButtonPressedTexture.dispose();
        leaveButtonTexture.dispose();
        leaveButtonPressedTexture.dispose();
    }
}
