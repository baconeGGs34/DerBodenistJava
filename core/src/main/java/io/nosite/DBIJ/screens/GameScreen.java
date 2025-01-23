package io.nosite.DBIJ.screens;

import com.badlogic.gdx.Gdx;
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
import com.badlogic.gdx.utils.viewport.ExtendViewport;
import io.nosite.DBIJ.Main;
import io.nosite.DBIJ.entities.*;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.Texture;
import io.nosite.DBIJ.managers.FontManager;
import io.nosite.DBIJ.managers.PreferencesManager;
import io.nosite.DBIJ.managers.ScoreManager;
import io.nosite.DBIJ.managers.SoundManager;

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
    private static final float GAME_OVER_THRESHOLD = 400;  // Wie weit der Spieler fallen darf
    private ShapeRenderer shapeRenderer;  // Für das Zeichnen von Formen
    private BitmapFont font;
    private Texture backgroundTexture;
    private Texture wallTexture;
    private Texture platformTexture;
    private Texture breakableplatformTexture;
    private Texture movingplatformTexture;
    private ScoreManager scoreManager;
    private Texture startButtonTexture;
    private Texture leaveButtonTexture;
    private PreferencesManager prefsManager;
    private Texture leftButtonTexture, rightButtonTexture;
    private Rectangle leftButtonBounds, rightButtonBounds;
    private static final float CONTROL_BUTTON_SIZE = 120;
    private GlyphLayout glyphLayout = new GlyphLayout();
    private boolean startButtonIsPressed = false;
    private boolean leaveButtonIsPressed = false;
    private float backgroundScrollPosition = 0;
    private boolean showTouchControls;
    private Texture pauseButtonTexture;
    private Texture pauseButtonPressedTexture;
    private Rectangle pauseButtonBounds;
    private static final float PAUSE_BUTTON_SIZE = 50;
    private boolean isPaused = false;
    private Array<PowerUp> powerUps;
    private static final float POWER_UP_CHANCE = 0.05f; // 5% Wahrscheinlichkeit
    private static final float JETPACK_DURATION = 2f; // 2 Sekunden
    private float jetpackTimer = 0;
    private boolean jetpackActive = false;
    private static final float POWER_UP_CHANCE_NORMAL = 0.03f;
    private static final float POWER_UP_CHANCE_EASY = 0.04f;
    private static final float PLATFORM_SPACING_NORMAL = 150f;
    private static final float PLATFORM_SPACING_EASY = 75f;

    @Override
    public void show() {
        prefsManager = PreferencesManager.getInstance();  // Klassenfeld initialisieren
        SoundManager.playGameMusic();
        camera = new OrthographicCamera();
        viewport = new ExtendViewport(MIN_WORLD_WIDTH, MIN_WORLD_HEIGHT, camera);
        batch = ((Main) Gdx.app.getApplicationListener()).getBatch();
        shapeRenderer = new ShapeRenderer();
        scoreManager = new ScoreManager();
        font = FontManager.getFont();
        powerUps = new Array<>();
        startButtonTexture = new Texture("images/buttons/startbutton.png");
        leaveButtonTexture = new Texture("images/buttons/leavebutton.png");
        showTouchControls = prefsManager.isAndroid() && !prefsManager.isGyroEnabled();

        pauseButtonTexture = new Texture("images/buttons/buttonpause.png");
        pauseButtonPressedTexture = new Texture("images/buttons/buttonpausepressed.png");
        pauseButtonBounds = new Rectangle();

        platformTexture = new Texture("images/platforms/platform.png");
        breakableplatformTexture = new Texture("images/platforms/breakeableplatform.png");
        movingplatformTexture = new Texture("images/platforms/movingplatform.png");

        wallTexture = new Texture(Gdx.files.internal("images/wallbrick1.png"));


        if (showTouchControls) {
            try {
                leftButtonTexture = new Texture(Gdx.files.internal("images/buttons/buttonleft.png"));
                rightButtonTexture = new Texture(Gdx.files.internal("images/buttons/buttonright.png"));
                leftButtonBounds = new Rectangle();
                rightButtonBounds = new Rectangle();
                Gdx.app.log("GameScreen", "Android controls initialized");
            } catch (Exception e) {
                Gdx.app.log("GameScreen", "Error loading control textures: " + e.getMessage());
            }
        }

        player = new Player(MIN_WORLD_WIDTH / 2, 400, prefsManager, camera,
            leftButtonBounds, rightButtonBounds);


        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.0f);

        backgroundTexture = new Texture(Gdx.files.internal("images/bg2.jpg"));
        backgroundTexture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);

        float margin = 40f;
        float platformSpawnAreaWidth = MIN_WORLD_WIDTH - (2 * margin) - 70;
        float platformSpacing = prefsManager.isEasyMode() ? PLATFORM_SPACING_EASY : PLATFORM_SPACING_NORMAL;

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

            if (platformType < 15) {
                platforms.add(new MovingPlatform(randomX, nextY));
            } else if (platformType < 30) {
                platforms.add(new BreakablePlatform(randomX, nextY));
            } else {
                platforms.add(new Platform(randomX, nextY));
            }

            nextY += MathUtils.random(platformSpacing * 0.7f, platformSpacing);
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
            ((Main) Gdx.app.getApplicationListener()).setScreen(new GameOverScreen((int) (highscore / 100)));
            return;
        }

        // Kamera folgt dem Spieler nur nach oben
        if (player.getPosition().y > camera.position.y) {
            camera.position.y = player.getPosition().y;
            camera.update();
        }
        updatePlatforms();

        for (Platform platform : platforms) {
            if (platform instanceof MovingPlatform) {
                ((MovingPlatform) platform).update(delta);
            }
        }

        // Hintergrund rendern
        batch.begin();
        float bgHeight = backgroundTexture.getHeight();
        float baseY = camera.position.y - viewport.getWorldHeight() / 2;
        float offsetY = baseY % bgHeight;
        for (int i = -1; i < 2; i++) {
            float y = baseY - offsetY + (i * bgHeight);
            batch.draw(backgroundTexture,
                camera.position.x - viewport.getWorldWidth() / 2,
                y,
                viewport.getWorldWidth(),
                bgHeight);
        }
        batch.end();

        // Ränder zeichnen
        batch.begin();
        float borderWidth = 40f;
        float wallHeight = wallTexture.getHeight();
        baseY = camera.position.y - viewport.getWorldHeight() / 2;
        offsetY = baseY % wallHeight;

        int tilesNeeded = (int) Math.ceil(viewport.getWorldHeight() / wallHeight) + 2;

        // Left border
        for (int i = -1; i < tilesNeeded; i++) {
            float y = baseY - offsetY + (i * wallHeight);
            batch.draw(wallTexture,
                camera.position.x - viewport.getWorldWidth() / 2,
                y,
                borderWidth,
                wallHeight);
        }

        // Right border
        for (int i = -1; i < tilesNeeded; i++) {
            float y = baseY - offsetY + (i * wallHeight);
            batch.draw(wallTexture,
                camera.position.x + viewport.getWorldWidth() / 2 - borderWidth,
                y,
                borderWidth,
                wallHeight);
        }
        batch.end();

        // Plattformen rendern
        batch.begin();
        for (Platform platform : platforms) {
            if (platform instanceof BreakablePlatform) {
                batch.draw(breakableplatformTexture, platform.getBounds().x, platform.getBounds().y,
                    platform.getBounds().width, platform.getBounds().height);
            } else if (platform instanceof MovingPlatform) {
                batch.draw(movingplatformTexture, platform.getBounds().x, platform.getBounds().y,
                    platform.getBounds().width, platform.getBounds().height);
            } else {
                batch.draw(platformTexture, platform.getBounds().x, platform.getBounds().y,
                    platform.getBounds().width, platform.getBounds().height);
            }
        }
        batch.end();

        // Player rendern
        batch.begin();
        player.render(batch);
        batch.end();

        // Score rendern
        batch.begin();
        font = FontManager.getFont();
        font.getData().setScale(1.0f);
        font.draw(batch, "Score: " + (int) (highscore / 100),
            camera.position.x - viewport.getWorldWidth() / 2 + 20,
            camera.position.y + viewport.getWorldHeight() / 2 - 20);
        batch.end();

        // Pausebutton
        batch.begin();
        float pauseX = camera.position.x + viewport.getWorldWidth() / 2 - PAUSE_BUTTON_SIZE - 20;
        float pauseY = camera.position.y + viewport.getWorldHeight() / 2 - PAUSE_BUTTON_SIZE - 20;
        batch.draw(pauseButtonTexture, pauseX, pauseY, PAUSE_BUTTON_SIZE, PAUSE_BUTTON_SIZE);
        pauseButtonBounds.set(pauseX, pauseY, PAUSE_BUTTON_SIZE, PAUSE_BUTTON_SIZE);
        batch.end();

        // Bewegung updaten
        updatePlayerMovement();

        // Nur Touch-Controls rendern wenn nötig
        if (prefsManager.isAndroid() && !prefsManager.isGyroEnabled()) {
            renderTouchControls();
        }

        if (Gdx.input.justTouched()) {
            Vector3 touchPos = new Vector3();
            touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(touchPos);

            if (pauseButtonBounds.contains(touchPos.x, touchPos.y)) {
                Gdx.app.log("GameScreen", "Opening pause screen");  // Debug output
                ((Main) Gdx.app.getApplicationListener()).setScreen(new PauseScreen(this));
            }
        }

        if (jetpackActive) {
            jetpackTimer -= delta;
            player.setVelocityY(1600); // Starker Aufwärtsschub
            if (jetpackTimer <= 0) {
                jetpackActive = false;
                player.stopJetpack();  // Jetpack-Effekt stoppen
            }
        }

        // PowerUp Kollisionserkennung
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            if (powerUp.isActive() && powerUp.getBounds().overlaps(player.getBounds())) {
                powerUp.collect();
                player.startJetpack();  // Jetpack-Effekt starten
                jetpackActive = true;
                jetpackTimer = JETPACK_DURATION;
                powerUps.removeIndex(i);
                SoundManager.playPowerUpSound();
                SoundManager.playJetPackSound();
            }
        }

        // PowerUps rendern
        batch.begin();
        for (PowerUp powerUp : powerUps) {
            powerUp.render(batch);
        }
        batch.end();

    }


    private void renderTouchControls() {
        if (!prefsManager.isGyroEnabled() && leftButtonTexture != null && rightButtonTexture != null) {
            batch.begin();
            batch.setColor(1, 1, 1, 0.5f);

            float leftX = camera.position.x - viewport.getWorldWidth() / 2 + 20;
            float rightX = camera.position.x + viewport.getWorldWidth() / 2 - CONTROL_BUTTON_SIZE - 20;
            float buttonY = camera.position.y - viewport.getWorldHeight() / 2 + 20;

            batch.draw(leftButtonTexture, leftX, buttonY, CONTROL_BUTTON_SIZE, CONTROL_BUTTON_SIZE);
            batch.draw(rightButtonTexture, rightX, buttonY, CONTROL_BUTTON_SIZE, CONTROL_BUTTON_SIZE);

            batch.setColor(1, 1, 1, 1f);
            batch.end();

            // WICHTIG: Button-Bounds aktualisieren
            leftButtonBounds.set(leftX, buttonY, CONTROL_BUTTON_SIZE, CONTROL_BUTTON_SIZE);
            rightButtonBounds.set(rightX, buttonY, CONTROL_BUTTON_SIZE, CONTROL_BUTTON_SIZE);
        }
    }

    private void updatePlayerMovement() {
        if (prefsManager.isAndroid()) {
            if (prefsManager.isGyroEnabled()) {
                // Gyro Steuerung
                float accelX = Gdx.input.getAccelerometerX();
                player.setVelocityX(-(accelX / 10.0f) * Player.MOVEMENT_SPEED);
            } else {
                // Button Steuerung
                if (Gdx.input.isTouched()) {
                    Vector3 touchPos = new Vector3();
                    touchPos.set(Gdx.input.getX(), Gdx.input.getY(), 0);
                    camera.unproject(touchPos);

                    if (leftButtonBounds != null && rightButtonBounds != null) {
                        if (leftButtonBounds.contains(touchPos.x, touchPos.y)) {
                            player.setVelocityX(-Player.MOVEMENT_SPEED);
                            return;  // Wichtig: Früher Return wenn Button gedrückt
                        } else if (rightButtonBounds.contains(touchPos.x, touchPos.y)) {
                            player.setVelocityX(Player.MOVEMENT_SPEED);
                            return;  // Wichtig: Früher Return wenn Button gedrückt
                        }
                    }
                }
                // Wenn kein Button gedrückt wird, Geschwindigkeit auf 0 setzen
                player.setVelocityX(0);
            }
        }
    }

    private void spawnPowerUp(Platform platform) {
        if (Math.random() < POWER_UP_CHANCE) {
            float x = platform.getBounds().x + platform.getBounds().width / 2;
            float y = platform.getBounds().y + platform.getBounds().height;
            powerUps.add(new PowerUp(x, y, platform));
        }
    }

    private void resetGame() {
        gameOver = false;
        highscore = 0;
        camera.position.set(viewport.getWorldWidth() / 2, viewport.getWorldHeight() / 2, 0);

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
        float platformSpacing = prefsManager.isEasyMode() ? PLATFORM_SPACING_EASY : PLATFORM_SPACING_NORMAL;
        float powerUpChance = prefsManager.isEasyMode() ? POWER_UP_CHANCE_EASY : POWER_UP_CHANCE_NORMAL;

        // Alte Plattformen und PowerUps entfernen
        for (int i = platforms.size - 1; i >= 0; i--) {
            Platform platform = platforms.get(i);
            if (platform.getBounds().y < camera.position.y - viewport.getWorldHeight() ||
                (platform instanceof BreakablePlatform && !((BreakablePlatform) platform).isActive())) {
                platforms.removeIndex(i);
            }
        }

        // PowerUps aktualisieren und alte entfernen
        for (int i = powerUps.size - 1; i >= 0; i--) {
            PowerUp powerUp = powerUps.get(i);
            powerUp.update();
            if (powerUp.getBounds().y < camera.position.y - viewport.getWorldHeight()) {
                powerUps.removeIndex(i);
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
            Platform newPlatform;

            if (platformType < 15) {
                newPlatform = new MovingPlatform(randomX, highestPlatformY + MathUtils.random(platformSpacing * 0.7f, platformSpacing));
            } else if (platformType < 30) {
                newPlatform = new BreakablePlatform(randomX, highestPlatformY + MathUtils.random(platformSpacing * 0.7f, platformSpacing));
            } else {
                newPlatform = new Platform(randomX, highestPlatformY + MathUtils.random(platformSpacing * 0.7f, platformSpacing));
                if (MathUtils.random() < powerUpChance) {
                    float powerUpX = newPlatform.getBounds().x + newPlatform.getBounds().width/2;
                    float powerUpY = newPlatform.getBounds().y + newPlatform.getBounds().height;
                    powerUps.add(new PowerUp(powerUpX, powerUpY, newPlatform));
                }
            }

            platforms.add(newPlatform);
            highestPlatformY += MathUtils.random(platformSpacing * 0.7f, platformSpacing);
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
        leaveButtonTexture.dispose();
        wallTexture.dispose();
        platformTexture.dispose();
        breakableplatformTexture.dispose();
        movingplatformTexture.dispose();
        pauseButtonTexture.dispose();
        pauseButtonPressedTexture.dispose();
        SoundManager.stopMusic();
        if (leftButtonTexture != null) leftButtonTexture.dispose();
        if (rightButtonTexture != null) rightButtonTexture.dispose();
        for (PowerUp powerUp : powerUps) {
            powerUp.dispose();
        }
        player.dispose();
    }
}
