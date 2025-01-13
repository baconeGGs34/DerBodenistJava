package io.nosite.DBIJ;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.nosite.DBIJ.screens.GameScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        setScreen(new GameScreen());  // Startet das Spiel mit dem GameScreen
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();  // Ressourcen freigeben
    }

    public SpriteBatch getBatch() {
        return batch;
    }



}
