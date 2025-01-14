package io.nosite.DBIJ.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.entities.Platform;
import io.nosite.DBIJ.entities.Player;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

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
    private static final float GAME_OVER_THRESHOLD = 1000;  // Wie weit der Spieler fallen darf
    private ShapeRenderer shapeRenderer;  // Für das Zeichnen von Formen
    private BitmapFont font;

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        player = new Player(MIN_WORLD_WIDTH / 2, 400);
        batch = ((Main) Gdx.app.getApplicationListener()).getBatch();
        shapeRenderer = new ShapeRenderer();
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(2.0f);

        // Hier den festen Randabstand verwenden
        float margin = 40f;  // Fester Randabstand von 40px
        float platformSpawnAreaWidth = MIN_WORLD_WIDTH - (2 * margin) - 70;  // 70 ist PLATFORM_WIDTH

        platforms = new Array<>();
        // Erste Platform in der Mitte
        platforms.add(new Platform(MIN_WORLD_WIDTH / 2 - 60, 200));

        float nextY = 400;
        for(int i = 0; i < 10; i++) {
            float randomX = margin + MathUtils.random(platformSpawnAreaWidth);
            platforms.add(new Platform(randomX, nextY));
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
            gameOver = true;
        }

        // Wenn Game Over, Spiel neu starten bei Leertaste
        if (gameOver) {
            if (Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                resetGame();
            }
        }

        // Kamera folgt dem Spieler nur nach oben
        if (player.getPosition().y > camera.position.y) {
            camera.position.y = player.getPosition().y;
            camera.update();
        }
        updatePlatforms();

        // Formen rendern
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        for (Platform platform : platforms) {
            platform.render(shapeRenderer);
        }
        player.render(shapeRenderer);
        shapeRenderer.end();

// UI und Score rendern
        batch.begin();
        font.getData().setScale(2.0f);
        font.draw(batch, "Score: " + (int)(highscore/100),
            camera.position.x - viewport.getWorldWidth()/2 + 20,
            camera.position.y + viewport.getWorldHeight()/2 - 20);

        if(gameOver) {
            font.getData().setScale(2.0f);
            font.draw(batch, "Game Over! Press SPACE to restart",
                camera.position.x - 100,
                camera.position.y);
        }
        font.getData().setScale(1.0f);
        batch.end();
    }

    private void resetGame() {
        gameOver = false;
        highscore = 0;
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);
        player = new Player(MIN_WORLD_WIDTH / 2, 400);

        // Plattform-Generierung mit Margin
        float margin = 40f;  // Fester Randabstand von 40px
        float platformSpawnAreaWidth = MIN_WORLD_WIDTH - (2 * margin) - 70;  // 70 ist PLATFORM_WIDTH

        platforms.clear();
        platforms.add(new Platform(MIN_WORLD_WIDTH / 2 - 60, 200));

        float nextY = 400;
        for(int i = 0; i < 10; i++) {
            float randomX = margin + MathUtils.random(platformSpawnAreaWidth);
            platforms.add(new Platform(randomX, nextY));
            nextY += MathUtils.random(110, 150);
        }
    }

    private void updatePlatforms() {

        float margin = 40f;  // Fester Randabstand von 40px
        float platformSpawnAreaWidth = MIN_WORLD_WIDTH - (2 * margin) - 70;  // 70 ist PLATFORM_WIDTH

        // Lösche Plattformen die weit unter der Kamera sind
        for (int i = platforms.size - 1; i >= 0; i--) {
            Platform platform = platforms.get(i);
            if (platform.getBounds().y < camera.position.y - viewport.getWorldHeight()) {
                platforms.removeIndex(i);
            }
        }

        // Füge neue Plattformen hinzu wenn wir nach oben Platz haben
        float highestPlatformY = 0;
        for (Platform platform : platforms) {
            highestPlatformY = Math.max(highestPlatformY, platform.getBounds().y);
        }

        // Generiere neue Plattformen wenn wir weniger als eine Bildschirmhöhe über der höchsten haben
        while (highestPlatformY < camera.position.y + viewport.getWorldHeight()) {
            float randomX = margin + MathUtils.random(platformSpawnAreaWidth);
            platforms.add(new Platform(randomX, highestPlatformY + MathUtils.random(110, 150)));
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
        font.dispose();  // Font aufräumen
    }
}
