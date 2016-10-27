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
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton.TextButtonStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
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
import com.ellzone.slotpuzzle2d.sprites.ReelLetter;
import com.ellzone.slotpuzzle2d.sprites.ReelLetterTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import aurelienribon.tweenengine.equations.Bounce;
import aurelienribon.tweenengine.equations.Elastic;

public class IntroScreen implements Screen {
	private static final int VIEWPORT_WIDTH = 800;
	private static final int VIEWPORT_HEIGHT = 480;
	public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
	public static final String GENERATED_FONTS_DIR = "generated-fonts/";
	public static final String FONT_SMALL = "exo-small";
	public static final String FONT_MEDIUM = "exo-medium";
	public static final String FONT_LARGE = "exo-large";
    private static final int TEXT_SPACING_SIZE = 30;
    private static final int REEL_WIDTH = 40;
    private static final int REEL_HEIGHT = 40;
    private static final String COPYRIGHT = "\u00a9";
    private static final String SLOT_PUZZLE_REEL_TEXT = "Slot Puzzle";
    private static final String BY_TEXT = "by";
    private static final String AUTHOR_TEXT = "Mark Ellis";
    private static final String COPYRIGHT_YEAR_AUTHOR_TEXT = COPYRIGHT + "2016 Mark Ellis";
    private SlotPuzzle game;
    private Texture textTexture;
    private Pixmap slotReelPixmap;
    private Texture slotReelTexture;
    private final OrthographicCamera camera = new OrthographicCamera();
    private Viewport viewport;
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
	private Vector accelerator, accelerate, velocity, velocityMin;
	private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;

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
        initialiseUiStage();        
        initialiseIntroSequence();
    }
    
    private void initialiseIntroScreen() {
        viewport = new FitViewport(VIEWPORT_WIDTH, VIEWPORT_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
        ReelLetter.instanceCount = 0;
        endOfIntroScreen = false;
        Gdx.input.setInputProcessor(stage);
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
            Gdx.app.error(SlotPuzzle.SLOT_PUZZLE, "Could not copy " + exoFileInternal.file().getPath() + " to file " + exoFile.file().getAbsolutePath() + " " + ex.getMessage());
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
        
    private void initialiseUiStage() {
        skin = new Skin();
        buttonAtlas = new TextureAtlas(Gdx.files.internal("ui/ui-blue.atlas"));
        skin.addRegions(buttonAtlas);
        textButtonStyle = new TextButtonStyle();
        textButtonStyle.font = fontSmall;
        textButtonStyle.up = skin.getDrawable("icon_arrow_up");
        textButtonStyle.down = skin.getDrawable("icon_arrow_down");
        textButtonStyle.checked = skin.getDrawable("checkbox_on");
        button = new TextButton("                  ", textButtonStyle);
        button.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                endOfIntroScreen = true;
            }
        });

        Label.LabelStyle font = new Label.LabelStyle(fontSmall, Color.WHITE);
        Label buttonPressLabel = new Label("LAUNCH", font);

        Table table = new Table();
        table.bottom();
        table.setFillParent(true);
        table.add(button).expandX().padTop(50f);
        table.row();
        table.add(buttonPressLabel).expandX();

        stage.addActor(table);
        viewport.update(VIEWPORT_WIDTH, VIEWPORT_HEIGHT);    	
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

        reelTile = new ReelTile(slotReelTexture, slotReelTexture.getWidth(), slotReelTexture.getHeight(), 32, 32, 0);

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

    public void update(float delta) {
        tweenManager.update(delta);
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
            game.setScreen(new WorldScreen(game));
            dispose();
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        if (isLoaded) {
            game.batch.begin();
            for (ReelLetterTile reel : reelLetterTiles) {
                reel.draw(game.batch);
            }
            reelTile.draw(game.batch);
            game.batch.end();
            stage.draw();
        }
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height);
        fontSmall.newFontCache();
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

    @Override
    public void dispose() {
        if (tweenManager != null) {
        	tweenManager.killAll();
        }
        if (slotReelPixmap != null) {
        	slotReelPixmap.dispose();
        }
        if (slotReelTexture != null) {
        	slotReelTexture.dispose();
        }
        if (textTexture != null) {
        	textTexture.dispose();
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