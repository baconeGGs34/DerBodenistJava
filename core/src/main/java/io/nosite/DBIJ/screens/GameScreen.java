package io.nosite.DBIJ.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.entities.Player;

public class GameScreen implements Screen {

    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private SpriteBatch batch;
    private static final float MIN_WORLD_WIDTH = 720;
    private static final float MIN_WORLD_HEIGHT = 1280;
    private Player player;  // Unser Spieler-Objekt

    @Override
    public void show() {
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        player = new Player(MIN_WORLD_WIDTH / 2, 100);  // x = Mitte, y = etwas über dem unteren Rand
        batch = ((Main) Gdx.app.getApplicationListener()).getBatch(); // SpriteBatch von der Main-Klasse holen

    }

    @Override
    public void render(float delta) {
        // Screen clearen
        Gdx.gl.glClearColor(0.4f, 0.4f, 0.4f, 1); // Grauer Hintergrund
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // Kamera updaten
        camera.update();

        // SpriteBatch mit Kamera-Koordinaten konfigurieren
        batch.setProjectionMatrix(camera.combined);

        // Hier kommt später die Spiellogik hin...

        // Spieler updaten
        player.update(delta);
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

    }
}
