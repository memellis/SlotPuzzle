/* Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.screens;

import java.io.IOException;
import java.util.Random;
import org.jrenner.smartfont.SmartFontGenerator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.Version;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ReelLetterAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.sprites.LightButtonBuilder;
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.slotpuzzle2d.sprites.ReelLetterTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.StarField;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
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
    public static float SCALE = 0.5f;
    public static int NUM_STARS = 64;

    private static final int TEXT_SPACING_SIZE = 30;
    private static final int REEL_WIDTH = 40;
    private static final int REEL_HEIGHT = 40;
    private static final String COPYRIGHT = "\u00a9";
    private static final String SLOT_PUZZLE_REEL_TEXT = "Slot Puzzle";
    private static final String BY_TEXT = "by";
    private static final String AUTHOR_TEXT = "Mark Ellis";
    private static final String COPYRIGHT_YEAR_AUTHOR_TEXT = COPYRIGHT + "2017 Mark Ellis";
    private static final String LAUNCH_BUTTON_LABEL = "LAUNCH!";
    public static final float ONE_SECOND = 1.0f;
    private SlotPuzzle game;
    private Texture textTexture;
    private Pixmap slotReelPixmap;
    private Texture slotReelTexture;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport, lightViewport;
    private Stage stage;
    private BitmapFont fontSmall;
    private BitmapFont fontMedium;
    private BitmapFont fontLarge;
    private Array<ReelLetterTile> reelLetterTiles;
    private Array<DampenedSineParticle> dampenedSines;
    private int numReelLettersSpinning, numReelLetterSpinLoops;
    private boolean endOfIntroScreen;
    private TextButton button;
    private TextButtonStyle textButtonStyle;
    private Skin skin;
    private TextureAtlas buttonAtlas;
    private TweenManager tweenManager = new TweenManager();
    private Sprite cheesecake, cherry, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite[] sprites;
    private ReelTile reelTile;
    private Timeline endReelSeq;
    private boolean isLoaded = false;
    private Random random;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    private LightButtonBuilder launchButton;
    private Vector3 point = new Vector3();
	private Vector accelerator, accelerate, velocity, velocityMin;
	private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;
    private Array<PointLight> signLights;
    private float timerCount = 0;
    private int nextScreenTimer = 3;
    private ShapeRenderer shapeRenderer;
    private StarField starField;
    private float sceneWidth = SlotPuzzleConstants.V_WIDTH / SlotPuzzleConstants.PIXELS_PER_METER;
    private float sceneHeight = SlotPuzzleConstants.V_HEIGHT / SlotPuzzleConstants.PIXELS_PER_METER;
    private boolean show = false;

    public IntroScreen(SlotPuzzle game) {
        this.game = game;
        defineIntroScreen();
    }

    void defineIntroScreen() {
    	initialiseIntroScreen();
        initialiseTweenEngine();
        initialiseFonts();
        initialiseIntroScreenText();
	    initialiseDampenedSine();
        initialiseBox2D();
        initialiseLaunchButton();
        initialiseIntroSequence();
        initialiseStarfield();
    }

    private void initialiseIntroScreen() {
        viewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        lightViewport = new FitViewport(sceneWidth, sceneHeight);
        lightViewport.getCamera().position.set(lightViewport.getCamera().position.x + sceneWidth * 0.5f,
                                               lightViewport.getCamera().position.y + sceneHeight * 0.5f,
                                               0);
        lightViewport.getCamera().update();
        lightViewport.update(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT);


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
		Texture textTexture = new Texture(textPixmap);
		return textTexture;
	}

	private void initialiseFontReel(String reelText, float x, float y) {
		Texture textTexture = initialiseFontTexture(reelText);
		for (int i = 0; i < reelText.length(); i++) {
			ReelLetterTile reelLetter = new ReelLetterTile(textTexture, (float)(x + i * REEL_WIDTH), y, (float)REEL_WIDTH, (float)REEL_HEIGHT, i); 
			reelLetter.setSy(random.nextInt(reelText.length() - 1) * REEL_HEIGHT);
			reelLetter.setSpinning();
			reelLetterTiles.add(reelLetter);
		}
	}

    private void initialiseIntroScreenText() {
	reelLetterTiles = new Array<ReelLetterTile>();
    	initialiseFontReel(SLOT_PUZZLE_REEL_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
    	initialiseFontReel(BY_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
    	initialiseFontReel(AUTHOR_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
    	initialiseFontReel(COPYRIGHT_YEAR_AUTHOR_TEXT, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
        initialiseFontReel("v"+ Version.VERSION, viewport.getWorldWidth() / 3.2f, viewport.getWorldHeight() / 4.0f + TEXT_SPACING_SIZE + 10);
        numReelLettersSpinning = reelLetterTiles.size;
        numReelLetterSpinLoops = 10;
    }

    private void initialiseBox2D() {
        world = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();

        rayHandler = new RayHandler(world);
        rayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.5f, 0.5f, 0.5f, 0.1f);

        signLights = new Array<PointLight>();
        PointLight signLight1 = new PointLight(rayHandler, 32);
        signLight1.setActive(true);
        signLight1.setColor(Color.WHITE);
        signLight1.setDistance(2.0f);
        signLight1.setPosition(sceneWidth / 2, sceneHeight / 2);
        signLights.add(signLight1);

        PointLight signLight2 = new PointLight(rayHandler, 32);
        signLight2.setActive(true);
        signLight2.setColor(Color.WHITE);
        signLight2.setDistance(2.0f);
        signLight2.setPosition(sceneWidth / 4, sceneHeight / 2);
        signLights.add(signLight2);

        PointLight signLight3 = new PointLight(rayHandler, 32);
        signLight1.setActive(true);
        signLight1.setColor(Color.WHITE);
        signLight1.setDistance(2.0f);
        signLight1.setPosition(sceneWidth / 2 + sceneWidth / 4, sceneHeight / 2);
        signLights.add(signLight3);
    }

    private void initialiseLaunchButton() {
        Color buttonBackgroundColor = new Color(Color.ORANGE);
        Color buttonForeGroundColor = new Color(Color.ORANGE.r, Color.ORANGE.g, Color.ORANGE.b, 120);
        Color buttonEdgeColor = new Color(Color.BROWN);
        Color buttonTransparentColor = new Color(0, 200, 200, 0);
        Color buttonFontColor = new Color(Color.YELLOW);
        float buttonPositionX = 275 / (float) SlotPuzzleConstants.PIXELS_PER_METER;
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

       launchButton.getSprite().setSize((float) (buttonWidth / (float)SlotPuzzleConstants.PIXELS_PER_METER), buttonHeight / (float)SlotPuzzleConstants.PIXELS_PER_METER);
    }

    private void initialiseIntroSequence() {
        Timeline introSeq = Timeline.createSequence();
        for (int i = 0; i < reelLetterTiles.size; i++) {
            introSeq = introSeq.push(SlotPuzzleTween.set(reelLetterTiles.get(i), ReelLetterAccessor.POS_XY).target(-40f, -20f + i * 20f));
        }

        introSeq = introSeq.pushPause(1.0f);
        for (int i = 0; i < SLOT_PUZZLE_REEL_TEXT.length(); i++) {
            introSeq = introSeq.push(SlotPuzzleTween.to(reelLetterTiles.get(i), ReelLetterAccessor.POS_XY, 0.4f).target(250f + i * 30f, 280f));    
        }

        int startOfText = SLOT_PUZZLE_REEL_TEXT.length();
        int endOfText = SLOT_PUZZLE_REEL_TEXT.length() + BY_TEXT.length();
        for (int i = startOfText; i < endOfText; i++) {
        	introSeq = introSeq.push(SlotPuzzleTween.to(reelLetterTiles.get(i), ReelLetterAccessor.POS_XY, 0.4f).target(60f + i * 30f, 240f));
        }

        startOfText = endOfText;
        endOfText = startOfText + AUTHOR_TEXT.length();
        for (int i = startOfText; i < endOfText; i++) {
            introSeq = introSeq.push(SlotPuzzleTween.to(reelLetterTiles.get(i), ReelLetterAccessor.POS_XY, 0.4f).target(-120f + i * 30f, 200f));
        }

        startOfText = endOfText;
        endOfText = startOfText + COPYRIGHT_YEAR_AUTHOR_TEXT.length();
        for (int i = startOfText; i < endOfText; i++) {
            introSeq = introSeq.push(SlotPuzzleTween.to(reelLetterTiles.get(i), ReelLetterAccessor.POS_XY, 0.4f).target(-520f + i * 30f, 90f));
        }

        startOfText = endOfText;
        endOfText = startOfText + 1 + Version.VERSION.length();
        for (int i = startOfText; i < endOfText; i++) {
            introSeq = introSeq.push(SlotPuzzleTween.to(reelLetterTiles.get(i), ReelLetterAccessor.POS_XY, 0.4f).target(-700f + i * 30f, 40f));
        }

        introSeq = introSeq
                .start(tweenManager);

        initialiseAssets();

        slotReelPixmap = new Pixmap(REEL_WIDTH, REEL_HEIGHT, Pixmap.Format.RGBA8888);
        slotReelPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelTexture = new Texture(slotReelPixmap);

        reelTile = new ReelTile(slotReelTexture, slotReelTexture.getHeight() / REEL_HEIGHT, slotReelTexture.getWidth(), slotReelTexture.getHeight(), slotReelTexture.getWidth(), slotReelTexture.getHeight(), 32, 32, 0, null);

        Timeline reelSeq = Timeline.createSequence();
        reelSeq = reelSeq.push(SlotPuzzleTween.set(reelTile, ReelAccessor.SCROLL_XY).target(0f, 0f).ease(Bounce.IN));
        reelSeq = reelSeq.push(SlotPuzzleTween.to(reelTile, ReelAccessor.SCROLL_XY, 5.0f).target(0f, 32.0f * 8 * 3 + random.nextInt(slotReelTexture.getHeight() / 32) * 32).ease(Elastic.OUT));

        reelSeq = reelSeq.
                repeat(100, 0.0f).
                push(SlotPuzzleTween.to(reelTile, ReelAccessor.SCROLL_XY, 5.0f).target(0f, 32.0f * 8 * 3 + random.nextInt(slotReelTexture.getHeight() / 32) * 32).ease(Elastic.OUT)).
                start(tweenManager);
    }

    private void initialiseAssets() {
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
        isLoaded = true;

        TextureAtlas atlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = atlas.createSprite("cherry");
        cheesecake = atlas.createSprite("cheesecake");
        grapes = atlas.createSprite("grapes");
        jelly = atlas.createSprite("jelly");
        lemon = atlas.createSprite("lemon");
        peach = atlas.createSprite("peach");
        pear = atlas.createSprite("pear");
        tomato = atlas.createSprite("tomato");

        sprites = new Sprite[]{cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
        }
    }

	private void initialiseDampenedSine() {
		velocityY = 4.0f;
		velocityYMin = 2.0f;
		acceleratorY = 3.0f;
		accelerateY = 2.0f;
		acceleratorFriction = 0.97f;
		velocityFriction = 0.97f;
		DampenedSineParticle dampenedSine;
		dampenedSines = new Array<DampenedSineParticle>();
		for (ReelLetterTile reel : reelLetterTiles) {
			velocity = new Vector(0, velocityY);
			velocityMin = new Vector(0, velocityYMin);
			accelerator = new Vector(0, acceleratorY);
			accelerate = new Vector(0, accelerateY);
			dampenedSine = new DampenedSineParticle(0, reel.getSy(), 0, 0, 0, velocity, velocityMin, accelerator, accelerate, velocityFriction, acceleratorFriction);
			dampenedSine.setCallback(dsCallback);
			dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE);
			dampenedSine.setUserData(reel);
			dampenedSines.add(dampenedSine);
		}
	}

	private SPPhysicsCallback dsCallback = new SPPhysicsCallback() {
		@Override
		public void onEvent(int type, SPPhysicsEvent source) {
			delegateDSCallback(type, source); 
		}
	};

	private void delegateDSCallback(int type, SPPhysicsEvent source) {
		if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
			DampenedSineParticle ds = (DampenedSineParticle)source.getSource();
			ReelLetterTile reel = (ReelLetterTile)ds.getUserData();

			endReelSeq = Timeline.createSequence();
			float endSy = (reel.getEndReel() * REEL_HEIGHT) % reel.getTextureHeight();		
			reel.setSy(reel.getSy() % (reel.getTextureHeight()));
	        endReelSeq = endReelSeq.push(SlotPuzzleTween.to(reel, ReelLetterAccessor.SCROLL_XY, 5.0f)
	        		               .target(0f, endSy)
	        		               .ease(Elastic.OUT)
	        		               .setUserData(reel)
	        		               .setCallbackTriggers(TweenCallback.END)
	        		               .setCallback(slowingSpinningCallback));	        					
	        endReelSeq = endReelSeq
	        				.start(tweenManager);
		}
	}

	private TweenCallback slowingSpinningCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			delegateSlowingSpinning(type, source);
		}
	};

	private void delegateSlowingSpinning(int type, BaseTween<?> source) {
		ReelLetterTile reel = (ReelLetterTile)source.getUserData();
		if (type == TweenCallback.END) {
			reel.setSpinning(false);
			numReelLettersSpinning--;
			if (numReelLettersSpinning == 0) {
				numReelLettersSpinning = reelLetterTiles.size;
				numReelLetterSpinLoops--;
				if (numReelLetterSpinLoops >0){
					restartReelLettersSpinning();
				} else {
					endOfIntroScreen = true;
				}
			}
		}
	}

	private void initialiseStarfield() {
        shapeRenderer = new ShapeRenderer();
        starField = new StarField(shapeRenderer,
                NUM_STARS,
                SCALE,
                SlotPuzzleConstants.V_WIDTH,
                SlotPuzzleConstants.V_HEIGHT,
                random,
                viewport);
    }

	private void restartReelLettersSpinning() {
		int dsIndex = 0;
		int nextSy = 0;
		int endReel = 0;
		for (ReelLetterTile reel : reelLetterTiles) {
			reel.setEndReel(endReel++);
			if (endReel == reel.getTextureHeight() / REEL_HEIGHT) {
				endReel = 0;
			}
	        reel.setSpinning(true);
	        nextSy = random.nextInt(reelLetterTiles.size - 1) * REEL_HEIGHT;
			reel.setSy(nextSy);
			dampenedSines.get(dsIndex).initialiseDampenedSine();
			dampenedSines.get(dsIndex).position.y = nextSy;
			dampenedSines.get(dsIndex).velocity = new Vector(0, velocityY);
			accelerator = new Vector(0, acceleratorY);
			dampenedSines.get(dsIndex).accelerator = accelerator;
			accelerate = new Vector(0, accelerateY);
			dampenedSines.get(dsIndex).accelerate(accelerate);
			dampenedSines.get(dsIndex).velocityMin.y = velocityMin.y;
			dsIndex++;
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
                return true;
            }
        }
        return false;
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
        tweenManager.update(delta);
        updateTimer(delta);
        int dsIndex = 0;
		for (ReelLetterTile reel : reelLetterTiles) { 		  
			dampenedSines.get(dsIndex).update();
         	if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
         		reel.setSy(dampenedSines.get(dsIndex).position.y);
  	       	}
         	reel.update(delta);
         	dsIndex++;
		}
        reelTile.update(delta);
        if (endOfIntroScreen) {
            if (nextScreenTimer < 1) {
                game.setScreen(new WorldScreen(game));
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
                for (ReelLetterTile reel : reelLetterTiles) {
                    reel.draw(game.batch);
                }
                reelTile.draw(game.batch);
                game.batch.setProjectionMatrix(lightViewport.getCamera().combined);
                launchButton.getSprite().draw(game.batch);
                game.batch.end();
                rayHandler.setCombinedMatrix(lightViewport.getCamera().combined);
                rayHandler.updateAndRender();
                debugRenderer.render(world, lightViewport.getCamera().combined);
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
        if (tweenManager != null) {
        	tweenManager.killAll();
        }
        if (slotReelPixmap != null) {
        	//slotReelPixmap.dispose();
        }
        if (slotReelTexture != null) {
        	//slotReelTexture.dispose();
        }
        if (textTexture != null) {
        	//textTexture.dispose();
        }
        if (fontSmall != null) {
        	fontSmall.dispose();
        }
        if (fontMedium != null) {
        	fontMedium.dispose();
        }
        if (fontLarge != null) {
        	fontLarge.dispose();
        }
    }

    public SlotPuzzle getGame() {
        return this.game;
    }
}
