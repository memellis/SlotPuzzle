/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle.SlotPuzzleGame;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.audio.MusicManager;
import com.ellzone.slotpuzzle.audio.MusicPlayer;
import com.ellzone.slotpuzzle.effects.ReelAccessor;
import com.ellzone.slotpuzzle.effects.ReelLetterAccessor;
import com.ellzone.slotpuzzle.effects.SpriteAccessor;
import com.ellzone.slotpuzzle.messaging.MessageType;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.lights.LightButtonBuilder;
import com.ellzone.slotpuzzle.sprites.reel.ReelLetter;
import com.ellzone.slotpuzzle.sprites.reel.ReelLetterTile;
import com.ellzone.slotpuzzle.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle.sprites.reel.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.sprites.reel.ReelTileEvent;
import com.ellzone.slotpuzzle.sprites.reel.ReelTileListener;
import com.ellzone.slotpuzzle.sprites.starfield.StarField;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle.utils.FileUtils;
import com.ellzone.slotpuzzle.utils.PixmapProcessors;
import com.ellzone.slotpuzzle.utils.VersionInfo;
import com.ellzone.slotpuzzle.utils.VersionInfoFile;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.jrenner.smartfont.SmartFontGenerator;

import java.io.IOException;
import java.util.Random;

import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class IntroScreen extends InputAdapter implements Screen {
    public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
    public static final String GENERATED_FONTS_DIR = "generated-fonts/";
    public static final String FONT_SMALL = "exo-small";
    public static final String FONT_MEDIUM = "exo-medium";
    public static final String FONT_LARGE = "exo-large";
    private static final String VERSION_INF_FILE = "version_info/SlotPuzzleVersionInfo";
    public static float SCALE = 0.5f;
    public static int NUM_STARS = 64;
    private static final int TEXT_SPACING_SIZE = 30;
    private static final int REEL_WIDTH = 40;
    private static final int REEL_HEIGHT = 40;
    private static final String COPYRIGHT = "\u00a9";
    private static final String SLOT_PUZZLE_REEL_TEXT = "Slot Puzzle";
    private static final String BY_TEXT = "by";
    private static final String LAUNCH_BUTTON_LABEL = "LAUNCH!";
    public static final float ONE_SECOND = 1.0f;

    private final SlotPuzzleGame game;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport, lightViewport;
    private Stage stage;
    private BitmapFont fontSmall;
    private BitmapFont fontMedium;
    private BitmapFont fontLarge;
    private Array<AnimatedReel> reelLetterTiles;
    private int numReelLettersSpinning, numReelLetterSpinLoops;
    private boolean endOfIntroScreen;
    private ReelTile reelTile;
    private ReelSprites reelSprites;
    private boolean isLoaded = false;
    private Random random;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private LightButtonBuilder launchButton;
    private final Vector3 point = new Vector3();
    private float timerCount = 0;
    private int nextScreenTimer = 3;
    private ShapeRenderer shapeRenderer;
    private StarField starField;
    private final float sceneWidth =
        SlotPuzzleConstants.VIRTUAL_WIDTH / SlotPuzzleConstants.PIXELS_PER_METER;
    private final float sceneHeight =
        SlotPuzzleConstants.VIRTUAL_HEIGHT / SlotPuzzleConstants.PIXELS_PER_METER;
    private boolean show = false;
    private MusicManager musicManager;
    private MessageManager messageManager;
    private MusicPlayer musicPlayer;
    private VersionInfo versionInfo;

    public IntroScreen(SlotPuzzleGame game) {
        this.game = game;
        defineIntroScreen();
    }

    void defineIntroScreen() {
        initialiseVersionInfo();
        initialiseIntroScreen();
        initialiseTweenEngine();
        initialiseFonts();
        reelSprites = initialiseReelSprites(game.annotationAssetManager);
        initialiseIntroScreenText();
        initialiseBox2D();
        initialiseLaunchButton();
        initialiseIntroSequence();
        initialiseStarField();
        initialiseAudio();
        messageManager = setUpMessages();
        initialiseInput();
        messageManager.dispatchMessage(MessageType.PlayMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
        isLoaded = true;
    }

    private void initialiseVersionInfo() {
        VersionInfoFile versionInfoFile = new VersionInfoFile();
        versionInfo = versionInfoFile.loadVersionInfoInternal(VERSION_INF_FILE);
    }

    private void initialiseInput() {
        InputMultiplexer inputMultiplexer = new InputMultiplexer();
        inputMultiplexer.addProcessor(this);
        inputMultiplexer.addProcessor(stage);
        Gdx.input.setInputProcessor(inputMultiplexer);
    }

    private void initialiseAudio() {
        musicManager = new MusicManager(game.annotationAssetManager);
        musicPlayer = new MusicPlayer(game.annotationAssetManager, game.batch, stage, viewport, 0, 0);
        musicPlayer.setVisible(false);
    }

    private MessageManager setUpMessages() {
        messageManager = MessageManager.getInstance();
        messageManager.addListeners(musicManager,
            MessageType.PlayMusic.index,
            MessageType.StopMusic.index,
            MessageType.PauseMusic.index);
        messageManager.addListeners(musicPlayer,
            MessageType.GetCurrentMusicTrack.index);
        return messageManager;
    }

    private ReelSprites initialiseReelSprites(AnnotationAssetManager annotationAssetManager) {
        return new ReelSprites(annotationAssetManager);
    }

    private void initialiseIntroScreen() {
        viewport = new FitViewport(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        lightViewport = new FitViewport(sceneWidth, sceneHeight);
        lightViewport.getCamera().position.set(lightViewport.getCamera().position.x + sceneWidth * 0.5f,
            lightViewport.getCamera().position.y + sceneHeight * 0.5f,
            0);
        lightViewport.getCamera().update();
        lightViewport.update(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT);

        ReelLetter.instanceCount = 0;
        endOfIntroScreen = false;
        Gdx.input.setInputProcessor(this);
        random = new Random();
    }

    private void initialiseTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(ReelLetterTile.class, new ReelLetterAccessor());
    }

    private void initialiseFonts() {
        SmartFontGenerator fontGen = new SmartFontGenerator();
        FileHandle exoFileInternal = Gdx.files.internal(LIBERATION_MONO_REGULAR_FONT_NAME);
        FileHandle generatedFontDir = Gdx.files.local(GENERATED_FONTS_DIR);
        generatedFontDir.mkdirs();

        FileHandle exoFile = Gdx.files.local(GENERATED_FONTS_DIR + LIBERATION_MONO_REGULAR_FONT_NAME);

        try {
            FileUtils.copyFile(exoFileInternal, exoFile);
        } catch (IOException ex) {
            Gdx.app.error(SlotPuzzleConstants.SLOT_PUZZLE, "Could not copy " + exoFileInternal.file().getPath() + " to file " + exoFile.file().getAbsolutePath() + " " + ex.getMessage());
        }

        fontSmall = fontGen.createFont(exoFile, FONT_SMALL, 24);
        fontMedium = fontGen.createFont(exoFile, FONT_MEDIUM, 48);
        fontLarge = fontGen.createFont(exoFile, FONT_LARGE, 64);
    }

    private Texture initialiseFontTexture(String reelText) {
        Pixmap textPixmap = new Pixmap(REEL_WIDTH, reelText.length() * REEL_HEIGHT, Pixmap.Format.RGBA8888);
        textPixmap = PixmapProcessors.createDynamicVerticalFontText(fontSmall, reelText, textPixmap);
        return new Texture(textPixmap);
    }

    private final ReelTileListener reelTileListener = new ReelTileListener() {
        @Override
        public void actionPerformed(ReelTileEvent event, ReelTile source) {
            if (event instanceof ReelStoppedSpinningEvent) {
                source.setSpinning(false);
                numReelLettersSpinning--;
                if (numReelLettersSpinning == 0) {
                    numReelLettersSpinning = reelLetterTiles.size;
                    numReelLetterSpinLoops--;
                    if (numReelLetterSpinLoops > 0)
                        restartReelLettersSpinning();
                    else
                        endOfIntroScreen = true;
                }
            }
        }
    };

    private void initialiseIntroScreenText() {
        reelLetterTiles = new Array<>();
        initialiseFontReel(SLOT_PUZZLE_REEL_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
        initialiseFontReel(BY_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
        initialiseFontReel(versionInfo.getAuthor(), viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
        initialiseFontReel(getCopyrightYearAuthorText(), viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
        initialiseFontReel("v"+ versionInfo.getVersion(), viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 4.0f + TEXT_SPACING_SIZE + 10);
        numReelLettersSpinning = reelLetterTiles.size;
        numReelLetterSpinLoops = 10;
    }

    private String getCopyrightYearAuthorText() {
        return COPYRIGHT + versionInfo.getTimestampSerializer().getYear() + " " + versionInfo.getAuthor();
    }

    private void initialiseFontReel(String reelText, float x, float y) {
        Texture textTexture = initialiseFontTexture(reelText);
        for (int i = 0; i < reelText.length(); i++) {
            AnimatedReel reelLetterTile =
                new AnimatedReel(
                    textTexture,
                    (float)(x + i * REEL_WIDTH),
                    y,
                    (float)REEL_WIDTH,
                    (float)REEL_HEIGHT,
                    (float)REEL_WIDTH,
                    (float)REEL_HEIGHT,
                    i,
                    game.getTweenManager());
            reelLetterTile.setupSpinning();
            reelLetterTile.setSy(random.nextInt(reelText.length() - 1) * REEL_HEIGHT);
            reelLetterTile.getReel().addListener(reelTileListener);
            reelLetterTile.getReel().startSpinning();
            reelLetterTiles.add(reelLetterTile);
        }
    }

    private void initialiseBox2D() {
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        rayHandler = new RayHandler(world);
        RayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.5f, 0.5f, 0.5f, 0.1f);

        Array<PointLight> signLights = new Array<>();
        signLights.add(getPointLight(sceneWidth / 2, sceneWidth / 2));
        signLights.add(getPointLight(sceneWidth / 4, sceneHeight / 2));
        signLights.add(getPointLight(sceneWidth / 2 + sceneWidth / 4, sceneHeight / 2));
    }

    private PointLight getPointLight(float x, float y) {
        PointLight signLight = new PointLight(rayHandler, 32);
        signLight.setActive(true);
        signLight.setColor(Color.WHITE);
        signLight.setDistance(2.0f);
        signLight.setPosition(x, y);
        return signLight;
    }

    private void initialiseLaunchButton() {
        Color buttonBackgroundColor = new Color(Color.ORANGE);
        Color buttonForeGroundColor = new Color(Color.ORANGE.r, Color.ORANGE.g, Color.ORANGE.b, 120);
        Color buttonEdgeColor = new Color(Color.BROWN);
        Color buttonTransparentColor = new Color(0, 200, 200, 0);
        Color buttonFontColor = new Color(Color.YELLOW);
        float buttonPositionX = 320 / (float) SlotPuzzleConstants.PIXELS_PER_METER;
        float buttonPositionY = sceneHeight / 12.0f;
        int buttonWidth = 200;
        int buttonHeight = 40;

        launchButton = new LightButtonBuilder.Builder()
            .world(world)
            .rayHandler(rayHandler)
            .buttonBackground(buttonBackgroundColor)
            .buttonForeground(buttonForeGroundColor)
            .buttonEdgeColor(buttonEdgeColor)
            .buttontTransparentColor(buttonTransparentColor)
            .buttonLightColor(Color.RED)
            .buttonLightDistance(1.5f)
            .buttonFontColor(buttonFontColor)
            .buttonPositionX(buttonPositionX)
            .buttonPositionY(buttonPositionY)
            .buttonWidth(buttonWidth)
            .buttonHeight(buttonHeight)
            .buttonFont(fontMedium)
            .buttonText(LAUNCH_BUTTON_LABEL)
            .startButtonTextX(4)
            .startButtonTextY(36)
            .build();

        launchButton.
            getSprite().
            setSize((float) (buttonWidth / (float)SlotPuzzleConstants.PIXELS_PER_METER),
                buttonHeight / (float)SlotPuzzleConstants.PIXELS_PER_METER);
    }

    private void initialiseIntroSequence() {
        Timeline introSeq = Timeline.createSequence();
        for (int i = 0; i < reelLetterTiles.size; i++)
            introSeq.push(
                SlotPuzzleTween.set(
                        reelLetterTiles.get(i).getReel(),
                        ReelLetterAccessor.POS_XY).
                    target(-40f, -20f + i * 20f));

        introSeq.pushPause(1.0f);
        getTimeline(introSeq, 0, SLOT_PUZZLE_REEL_TEXT.length(), 250.0f, 360.0f);

        int startOfText = SLOT_PUZZLE_REEL_TEXT.length();
        int endOfText = SLOT_PUZZLE_REEL_TEXT.length() + BY_TEXT.length();
        getTimeline(introSeq, startOfText, endOfText, 60.0f, 320.0f);

        startOfText = endOfText;
        endOfText = startOfText + versionInfo.getAuthor().length();
        getTimeline(introSeq, startOfText, endOfText, -120.0f, 280.0f);

        startOfText = endOfText;
        endOfText = startOfText + getCopyrightYearAuthorText().length();
        getTimeline(introSeq, startOfText, endOfText, -520.0f, 150.0f);

        startOfText = endOfText;
        endOfText = startOfText + 1 + versionInfo.getVersion().length();
        getTimeline(introSeq, startOfText, endOfText, -850.0f, 110.0f);

        introSeq.start(game.getTweenManager());

        Pixmap slotReelPixmap;
        slotReelPixmap = PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        Texture slotReelTexture = new Texture(slotReelPixmap);

        reelTile = new ReelTile(
            slotReelTexture,
            slotReelTexture.getHeight() / REEL_HEIGHT,
            slotReelTexture.getWidth(),
            viewport.getScreenHeight() / 2,
            slotReelTexture.getWidth(),
            viewport.getWorldHeight() / 2,
            REEL_WIDTH, REEL_HEIGHT,
            0
        );

        Timeline reelSeq = Timeline.createSequence();
        reelSeq.push(SlotPuzzleTween.set(reelTile, ReelAccessor.SCROLL_XY).target(0f, 0f).ease(Bounce.IN));
        reelSeq.push(SlotPuzzleTween.to(reelTile, ReelAccessor.SCROLL_XY, 5.0f).target(0f, 40.0f * 8 * 3 + random.nextInt(slotReelTexture.getHeight() / 40) * 40).ease(Elastic.OUT));

        reelSeq.
            repeat(100, 0.0f).
            push(SlotPuzzleTween.to(reelTile, ReelAccessor.SCROLL_XY, 5.0f).
                target(0f, 40.0f * 8 * 3 + random.nextInt(slotReelTexture.getHeight() / 40) * 40).
                ease(Elastic.OUT)).
            start(game.getTweenManager());
    }

    private void getTimeline(
        Timeline introSeq,
        int startOfText,
        int endOfText,
        float targetX,
        float targetY) {
        for (int i = startOfText; i < endOfText; i++)
            introSeq.push(
                SlotPuzzleTween.to(
                        reelLetterTiles.get(i).getReel(),
                        ReelLetterAccessor.POS_XY, (float) 0.4)
                    .target(targetX + i * (float) 30.0,
                        targetY));
    }

    private void initialiseStarField() {
        shapeRenderer = new ShapeRenderer();
        starField = new StarField(shapeRenderer,
            NUM_STARS,
            SCALE,
            SlotPuzzleConstants.VIRTUAL_WIDTH,
            SlotPuzzleConstants.VIRTUAL_HEIGHT,
            random,
            viewport);
    }

    private void restartReelLettersSpinning() {
        int nextSy;
        int endReel = 0;
        for (AnimatedReel reel : reelLetterTiles) {
            reel.setEndReel(endReel++);
            if (endReel == reel.getReel().getNumberOfReelsInTexture())
                endReel = 0;
            reel.getReel().startSpinning();
            nextSy = random.nextInt(reelLetterTiles.size - 1) * REEL_HEIGHT;
            reel.setSy(nextSy);
            reel.reinitialise();
        }
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT) {
            point.set(screenX, screenY, 0);
            lightViewport.getCamera().unproject(point);
            if (launchButton.getSprite().getBoundingRectangle().contains(point.x, point.y)) {
                launchButton.getLight().setActive(true);
                endOfIntroScreen = true;
                stopBackgroundMusic();
            }
        }
        if (button == Input.Buttons.RIGHT) {
            musicPlayer.setVisible(!musicPlayer.isVisible());
        }
        return false;
    }

    private void stopBackgroundMusic() {
        messageManager.dispatchMessage(MessageType.StopMusic.index, AssetsAnnotation.MUSIC_INTRO_SCREEN);
    }

    private void updateTimer(float delta) {
        if (endOfIntroScreen) {
            timerCount += delta;
            if (timerCount > ONE_SECOND) {
                timerCount = 0;
                nextScreenTimer--;
            }
        }
    }

    public void update(float delta) {
        musicPlayer.update(delta);
        game.getTweenManager().update(delta);
        updateTimer(delta);
        for (AnimatedReel reel : reelLetterTiles)
            reel.update(delta);
        reelTile.update(delta);
        if (endOfIntroScreen) {
            if (nextScreenTimer < 1) {
                stopBackgroundMusic();
                //game.setScreen(new WorldScreen(game));
                dispose();
            }
        }
    }

    @Override
    public void render(float delta) {
        if (show) {
            update(delta);

            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

            if (isLoaded) {
                starField.updateStarfield(delta, this.shapeRenderer);
                game.batch.begin();
                musicPlayer.draw();
                for (AnimatedReel reel : reelLetterTiles)
                    reel.draw(game.batch);
                reelTile.draw(game.batch);
                game.batch.setProjectionMatrix(lightViewport.getCamera().combined);
                launchButton.getSprite().draw(game.batch);
                game.batch.end();
                rayHandler.setCombinedMatrix((OrthographicCamera) lightViewport.getCamera());
                rayHandler.updateAndRender();
                debugRenderer.render(world, lightViewport.getCamera().combined);
                stage.act();
                stage.draw();
            }
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        lightViewport.update(width, height);
        fontSmall.newFontCache();
    }

    @Override
    public void show() {
        this.show = true;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "show() called.");
    }

    @Override
    public void pause() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "pause() called.");
    }

    @Override
    public void resume() {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "rename() called.");
    }

    @Override
    public void hide() {
        this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "hide() called.");
    }

    @Override
    public void dispose() {
        if (fontSmall != null)
            fontSmall.dispose();

        if (fontMedium != null)
            fontMedium.dispose();

        if (fontLarge != null)
            fontLarge.dispose();
    }

    public SlotPuzzleGame getGame() {
        return this.game;
    }
}
