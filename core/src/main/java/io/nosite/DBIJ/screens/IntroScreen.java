package io.nosite.DBIJ.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.managers.SoundManager;

public class IntroScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private Texture logoTexture;
    private float timer = 0;
    private static final float DISPLAY_TIME = 2f; // 2 Sekunden Anzeigezeit
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float MIN_WORLD_HEIGHT = 800;
    private boolean isTransitioning = false;

    public IntroScreen() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        batch = ((Main) Gdx.app.getApplicationListener()).getBatch();

        // Logo-Textur laden
        logoTexture = new Texture("images/logos/IntroImage.png");
    }

    @Override
    public void render(float delta) {
        // Timer aktualisieren
        timer += delta;

        // Zum Hauptmenü wechseln nach DISPLAY_TIME oder bei Touch
        if ((timer >= DISPLAY_TIME || Gdx.input.justTouched()) && !isTransitioning) {
            isTransitioning = true;
            SoundManager.playEpicMusic(); // Hier wird die Epic-Musik gestartet
            ((Main)Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
            return;
        }

        // Screen clearen
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        camera.update();
        batch.setProjectionMatrix(camera.combined);

        // Logo zentriert zeichnen
        batch.begin();
        float logoX = camera.position.x - logoTexture.getWidth() / 2;
        float logoY = camera.position.y - logoTexture.getHeight() / 2;
        batch.draw(logoTexture, logoX, logoY);
        batch.end();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void dispose() {
        logoTexture.dispose();
    }
}
