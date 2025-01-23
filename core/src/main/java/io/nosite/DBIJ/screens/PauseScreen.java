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

public class PauseScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private ExtendViewport viewport;
    private ShapeRenderer shapeRenderer;
    private Texture backgroundTexture;
    private BitmapFont font;
    private static final float MIN_WORLD_WIDTH = 480;
    private static final float MIN_WORLD_HEIGHT = 800;
    private static final float BUTTON_WIDTH = 285;
    private static final float BUTTON_HEIGHT = 90;
    private static final float BUTTON_SPACING = 50;
    private float backgroundScrollPosition = 0;
    private static final float SCROLL_SPEED = 30f;
    private static final Color SCORE_COLOR = new Color(1, 1, 0, 1);
    private GlyphLayout glyphLayout;

    private Texture resumeButtonTexture, resumeButtonPressedTexture;
    private Texture settingsButtonTexture, settingsButtonPressedTexture;
    private Texture quitButtonTexture, quitButtonPressedTexture;
    private Rectangle resumeBounds, settingsBounds, quitBounds;
    private boolean resumePressed, settingsPressed, quitPressed;
    private GameScreen gameScreen;

    public PauseScreen(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        batch = ((Main) Gdx.app.getApplicationListener()).getBatch();
        shapeRenderer = new ShapeRenderer();
        font = FontManager.getFont();
        this.glyphLayout = new GlyphLayout();

        // Texturen laden
        backgroundTexture = new Texture("images/bg2.jpg");
        resumeButtonTexture = new Texture("images/buttons/startbutton.png");
        resumeButtonPressedTexture = new Texture("images/buttons/startbuttonpressed.png");
        settingsButtonTexture = new Texture("images/buttons/settingsbutton.png");
        settingsButtonPressedTexture = new Texture("images/buttons/settingsbuttonpressed.png");
        quitButtonTexture = new Texture("images/buttons/quitbutton.png");
        quitButtonPressedTexture = new Texture("images/buttons/quitbuttonpressed.png");

        // Bounds initialisieren
        float centerX = MIN_WORLD_WIDTH / 2 - BUTTON_WIDTH / 2;
        float topY = MIN_WORLD_HEIGHT * 0.7f;
        resumeBounds = new Rectangle(centerX, topY, BUTTON_WIDTH, BUTTON_HEIGHT);
        settingsBounds = new Rectangle(centerX, topY - BUTTON_HEIGHT - BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT);
        quitBounds = new Rectangle(centerX, topY - (2 * BUTTON_HEIGHT) - (2 * BUTTON_SPACING), BUTTON_WIDTH, BUTTON_HEIGHT);
    }

    @Override
    public void render(float delta) {
        backgroundScrollPosition += SCROLL_SPEED * delta;

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

        // Overlay
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

        batch.begin();

        // Überschrift
        font.getData().setScale(3.0f);
        font.setColor(SCORE_COLOR);
        String pausetext = "PAUSE";
        glyphLayout.setText(font, pausetext);
        float pauseY = camera.position.y + viewport.getWorldHeight()/2 - 40;
        font.draw(batch, pausetext,
            camera.position.x - glyphLayout.width/2,
            pauseY);
        font.setColor(Color.WHITE);

        float centerX = MIN_WORLD_WIDTH / 2 - BUTTON_WIDTH / 2;
        float topY = MIN_WORLD_HEIGHT * 0.7f;

        // Resume Button
        batch.draw(resumePressed ? resumeButtonPressedTexture : resumeButtonTexture,
            centerX, topY, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Settings Button
        batch.draw(settingsPressed ? settingsButtonPressedTexture : settingsButtonTexture,
            centerX, topY - BUTTON_HEIGHT - BUTTON_SPACING, BUTTON_WIDTH, BUTTON_HEIGHT);

        // Quit Button
        batch.draw(quitPressed ? quitButtonPressedTexture : quitButtonTexture,
            centerX, topY - (2 * BUTTON_HEIGHT) - (2 * BUTTON_SPACING),
            BUTTON_WIDTH, BUTTON_HEIGHT);
        batch.end();

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (resumeBounds.contains(touchPos.x, touchPos.y)) {
                resumePressed = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Main) Gdx.app.getApplicationListener()).setScreen(gameScreen);
                    }
                }, 0.1f);
            } else if (settingsBounds.contains(touchPos.x, touchPos.y)) {
                settingsPressed = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Main) Gdx.app.getApplicationListener()).setScreen(new SettingsScreen(PauseScreen.this, gameScreen));
                    }
                }, 0.1f);

            } else if (quitBounds.contains(touchPos.x, touchPos.y)) {
                quitPressed = true;
                Timer.schedule(new Timer.Task() {
                    @Override
                    public void run() {
                        ((Main) Gdx.app.getApplicationListener()).setScreen(new MenuScreen());
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
        resumeButtonTexture.dispose();
        resumeButtonPressedTexture.dispose();
        settingsButtonTexture.dispose();
        settingsButtonPressedTexture.dispose();
        quitButtonTexture.dispose();
        quitButtonPressedTexture.dispose();
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
