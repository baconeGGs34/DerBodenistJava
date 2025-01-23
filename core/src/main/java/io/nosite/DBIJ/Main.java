package io.nosite.DBIJ;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import io.nosite.DBIJ.managers.FontManager;
import io.nosite.DBIJ.managers.SoundManager;
import io.nosite.DBIJ.screens.GameScreen;
import io.nosite.DBIJ.screens.IntroScreen;
import io.nosite.DBIJ.screens.MenuScreen;
import io.nosite.DBIJ.screens.SettingsScreen;

/** {@link com.badlogic.gdx.ApplicationListener} implementation shared by all platforms. */
public class Main extends Game {

    private SpriteBatch batch;

    @Override
    public void create() {
        batch = new SpriteBatch();
        SoundManager.init();
        FontManager.initialize();
        setScreen(new IntroScreen());
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        FontManager.dispose();
        SoundManager.dispose();
    }

    public SpriteBatch getBatch() {
        return batch;
    }



}
