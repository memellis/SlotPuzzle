/***************************************************6****************************
 * Copyright 2011 See AUTHORS file.
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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FillViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ReelLetterAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.sprites.ReelLetterTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.FileUtils;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import aurelienribon.tweenengine.equations.Elastic;


public class EndOfGameScreen implements Screen {

	public static final int TEXT_SPACING_SIZE = 30;
	public static final float SIXTY_FPS = 0.0166f;
	public static final int EXO_FONT_SMALL_SIZE = 24;
    public static final int REEL_WIDTH = 40;
    public static final int REEL_HEIGHT = 40;
    public static final int SCROLL_STEP = 4;
	public static final int SCROLL_HEIGHT = 20;
	public static final String LIBERATION_MONO_REGULAR_FONT_NAME = "LiberationMono-Regular.ttf";
	public static final String GENERATED_FONTS_DIR = "generated-fonts/";
	public static final String FONT_SMALL = "exo-small";
	public static final String FONT_MEDIUM = "exo-medium";
	public static final String FONT_LARGE = "exo-large";
	public static final String GAME_OVER_TEXT = "Game Over";

	private BitmapFont fontSmall;
	private Array<ReelLetterTile> reelLetterTiles;
    private Array<DampenedSineParticle> dampenedSines;
    private int numReelLettersSpinning, numReelLetterSpinLoops;
    private SlotPuzzle game;
	private Viewport viewport;
	private Stage stage;
	private Timeline endReelSeq; 
    private TweenManager tweenManager = new TweenManager();
    private Random random;
    private Boolean endOfEndOfGameScreen;
	private Vector accelerator, accelerate, velocity, velocityMin;
	private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;
	
	public EndOfGameScreen(SlotPuzzle game) {
		this.game = game;
		createEndOfGameScreen();
		initialiseTweenEngine();
		initialiseFonts();
		initialiseEndOfScreenText();
		initialiseDampenedSine();
	}

	private void createEndOfGameScreen() {
		viewport = new FillViewport(800, 480, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("GAME OVER", font);
        Label playAgainLabel = new Label("Click to Play Again", font);

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);	
        endOfEndOfGameScreen = false;
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

    private void initialiseEndOfScreenText() {
		reelLetterTiles = new Array<ReelLetterTile>();
    	initialiseFontReel(GAME_OVER_TEXT, viewport.getWorldWidth() / 3.6f, viewport.getWorldHeight() / 2.0f + TEXT_SPACING_SIZE + 10);
		numReelLettersSpinning = reelLetterTiles.size;
		numReelLetterSpinLoops = 10;
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
					endOfEndOfGameScreen = true;
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

    @Override
	public void show() {
	}

	private void update(float delta) {
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
		if(Gdx.input.justTouched() || endOfEndOfGameScreen) {
			game.setScreen(new SplashScreen(game));
	        dispose();
	    }
 	}

	@Override
	public void render(float delta) {
		update(delta);
	    Gdx.gl.glClearColor(0, 0, 0, 1);
	    Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 		game.batch.begin();
		for (ReelLetterTile reel : reelLetterTiles) {
			reel.draw(game.batch);
		}
		game.batch.end();
	    stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,  height);
		fontSmall.newFontCache();
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
		stage.dispose();
	}
}
