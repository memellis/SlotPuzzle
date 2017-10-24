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

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Card;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.LevelPopUp;
import com.ellzone.slotpuzzle2d.level.Pip;
import com.ellzone.slotpuzzle2d.level.Suit;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.Score;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;
import aurelienribon.tweenengine.equations.Sine;

public class PlayScreen implements Screen {
    public static final int TILE_WIDTH = 40;
	public static final int TILE_HEIGHT = 40;
	public static final int GAME_LEVEL_WIDTH = 11;
	public static final int GAME_LEVEL_HEIGHT = 8;
	public static final int SLOT_REEL_OBJECT_LAYER = 2;
	public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
	public static final int HIDDEN_PATTERN_LAYER = 0;
	public static final float PUZZLE_GRID_START_X = 160.0f;
	public static final float PUZZLE_GRID_START_Y = 40.0f;
	public static final int NUMBER_OF_SUITS = 4;
	public static final int NUMBER_OF_CARDS_IN_A_SUIT = 13;
    public static final int FLASH_MATCHED_SLOTS_BATCH_SIZE = 8;
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String LOG_TAG = "SlotPuzzle_PlayScreen";
	public static final String SLOTPUZZLE_SCREEN = "PlayScreen";
	public static final String LEVEL_TIP_DESC =  "Reveal the hidden pattern to complete the level.";
	public static final String LEVEL_LOST_DESC =  "Sorry you lost that level. Touch/Press to restart the level.";
	public static final String LEVEL_WON_DESC =  "Well done you've won that level. Touch/Press to start the nextlevel.";

	public enum PlayStates {INITIALISING, INTRO_SEQUENCE, INTRO_POPUP, INTRO_SPINNING, INTRO_FLASHING, PLAYING, LEVEL_TIMED_OUT, LEVEL_LOST, WON_LEVEL, RESTARTING_LEVEL};
	private PlayStates playState;
	private SlotPuzzle game;
	private final OrthographicCamera camera = new OrthographicCamera();
	private Viewport viewport;
	private Stage stage;
    private Sprite cheesecake, cherry, grapes, jelly, lemon, peach, pear, tomato;
	private float spriteWidth, spriteHeight;
	private float sW, sH;
 	private final TweenManager tweenManager = new TweenManager();
 	private Timeline introSequence, reelFlashSeq;
 	private TextureAtlas reelAtlas, tilesAtlas, carddeckAtlas;
 	private Sound chaChingSound, pullLeverSound, reelSpinningSound, reelStoppedSound;
 	private Sound jackpotSound;
	private boolean isLoaded = false;
	private Pixmap slotReelPixmap, slotReelScrollPixmap;
	private Texture slotReelTexture, slotReelScrollTexture;
	private Array<ReelTile> reels;
	private int reelsSpinning;
	private Array<DampenedSineParticle> dampenedSines;
	private TiledMap level;
	private Random random;
	private OrthogonalTiledMapRenderer renderer;
	private boolean gameOver = false;
	private boolean inRestartLevel = false;
	private boolean win = false;
	private int touchX, touchY;
	private boolean displaySpinHelp;
	private int displaySpinHelpSprite;
	private Sprite[] sprites;
    private Hud hud;
    private Array<Score> scores;
	private Vector accelerator, velocityMin;
	private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;
	private Array<Timeline> endReelSeqs;
	private float slotReelScrollheight;
	private float reelSlowingTargetTime;
    private BitmapFont font;
	private LevelPopUp levelPopUp, levelLostPopUp, levelWonPopUp;
	private Array<Sprite> popUpSprites, levelLostSprites, levelWonSprites;
	private BitmapFont currentLevelFont;
	private LevelDoor levelDoor;
	private Array<Integer> hiddenPlayingCards;
	private Array<Card> cards;
	private MapTile mapTile;
	private int mapWidth, mapHeight, tilePixelWidth, tilePixelHeight;
    private boolean show = false;

	public PlayScreen(SlotPuzzle game, LevelDoor levelDoor, MapTile mapTile) {
		this.game = game;
		this.levelDoor = levelDoor;
		this.mapTile = mapTile;
		createPlayScreen();
	}

	private void createPlayScreen() {
		playState = PlayStates.INITIALISING;
		initialiseScreen();
		initialiseTweenEngine();
		loadAssets();
		getAssets();
		createSprites();
		initialisePlayScreen();
		createSlotReelTexture();
		createLevels();
        getMapProperties(this.level);
        hud = new Hud(game.batch);
		hud.setLevelName(levelDoor.levelName);
		createReelIntroSequence();
   	}

	private void initialiseScreen() {
		viewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);
	}

	private void initialiseTweenEngine() {
		SlotPuzzleTween.setWaypointsLimit(10);
		SlotPuzzleTween.setCombinedAttributesLimit(3);
		SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
		SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
		SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
	}

	private void loadAssets() {
		game.assetManager.load("reel/reels.pack.atlas", TextureAtlas.class);
		game.assetManager.load("tiles/tiles.pack.atlas", TextureAtlas.class);
		game.assetManager.load("playingcards/carddeck.atlas", TextureAtlas.class);
		game.assetManager.load("sounds/cha-ching.mp3", Sound.class);
		game.assetManager.load("sounds/pull-lever1.mp3", Sound.class);
		game.assetManager.load("sounds/reel-spinning.mp3", Sound.class);
		game.assetManager.load("sounds/reel-stopped.mp3", Sound.class);
		game.assetManager.load("sounds/jackpot.mp3", Sound.class);
		game.assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
		game.assetManager.load("levels/level " + (this.levelDoor.id + 1) + " - 40x40.tmx", TiledMap.class);
		game.assetManager.finishLoading();
	}

	private void getAssets() {
	    reelAtlas = game.assetManager.get("reel/reels.pack.atlas", TextureAtlas.class);
        tilesAtlas = game.assetManager.get("tiles/tiles.pack.atlas", TextureAtlas.class);
        carddeckAtlas = game.assetManager.get("playingcards/carddeck.atlas", TextureAtlas.class);
        chaChingSound = game.assetManager.get("sounds/cha-ching.mp3", Sound.class);
        pullLeverSound = game.assetManager.get("sounds/pull-lever1.mp3", Sound.class);
        reelSpinningSound = game.assetManager.get("sounds/reel-spinning.mp3", Sound.class);
        reelStoppedSound = game.assetManager.get("sounds/reel-stopped.mp3", Sound.class);
        jackpotSound = game.assetManager.get("sounds/jackpot.mp3", Sound.class);
	    level = game.assetManager.get("levels/level " + (this.levelDoor.id + 1) + " - 40x40.tmx");
 	}

	private void createSprites() {
		cherry = reelAtlas.createSprite("cherry 40x40");
		cheesecake = reelAtlas.createSprite("cheesecake 40x40");
		grapes = reelAtlas.createSprite("grapes 40x40");
		jelly = reelAtlas.createSprite("jelly 40x40");
		lemon = reelAtlas.createSprite("lemon 40x40");
		peach = reelAtlas.createSprite("peach 40x40");
		pear = reelAtlas.createSprite("pear 40x40");
		tomato = reelAtlas.createSprite("tomato 40x40");

		sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
		for (Sprite sprite : sprites) {
			sprite.setOrigin(0, 0);
		}
		spriteWidth = sprites[0].getWidth();
		spriteHeight = sprites[0].getHeight();

		popUpSprites = new Array<Sprite>();
        popUpSprites.add(tilesAtlas.createSprite("GamePopUp"));
	    popUpSprites.add(tilesAtlas.createSprite("level"));
	    setPopUpSpritePositions();

	    levelLostSprites = new Array<Sprite>();
	    levelLostSprites.add(tilesAtlas.createSprite("GamePopUp"));
	    levelLostSprites.add(tilesAtlas.createSprite("level"));
	    levelLostSprites.add(tilesAtlas.createSprite("level"));
	    levelLostSprites.add(tilesAtlas.createSprite("over"));
	    setLevelLostSpritePositions();

	    levelWonSprites = new Array<Sprite>();
	    levelWonSprites.add(tilesAtlas.createSprite("GamePopUp"));
	    levelWonSprites.add(tilesAtlas.createSprite("level"));
	    levelWonSprites.add(tilesAtlas.createSprite("level"));
	    levelWonSprites.add(tilesAtlas.createSprite("complete"));
	    setLevelWonSpritePositions();
	}

	private void setPopUpSpritePositions() {
	    popUpSprites.get(0).setPosition(sW/ 2 - popUpSprites.get(0).getWidth() / 2, sH / 2 - popUpSprites.get(0).getHeight() /2);
		popUpSprites.get(1).setPosition(-200, sH / 2 - popUpSprites.get(1).getHeight() /2);
	}

	private void setLevelLostSpritePositions() {
	    levelLostSprites.get(0).setPosition(sW / 2 - levelLostSprites.get(0).getWidth() / 2, sH / 2 - levelLostSprites.get(0).getHeight() /2);
	    levelLostSprites.get(1).setPosition(-200, sH / 2 - levelLostSprites.get(1).getHeight() / 2);
	    levelLostSprites.get(2).setPosition(-200, sH / 2 - levelLostSprites.get(2).getHeight() / 2 + 40);
	    levelLostSprites.get(3).setPosition(200 + sW, sH / 2 - levelLostSprites.get(3).getHeight() / 2 + 40);
	}

	private void setLevelWonSpritePositions() {
	    levelWonSprites.get(0).setPosition(sW / 2 - levelWonSprites.get(0).getWidth() / 2, sH / 2 - levelWonSprites.get(0).getHeight() /2);
	    levelWonSprites.get(1).setPosition(-200, sH / 2 - levelWonSprites.get(1).getHeight() / 2);
	    levelWonSprites.get(2).setPosition(-200, sH / 2 - levelWonSprites.get(2).getHeight() / 2 + 40);
	    levelWonSprites.get(3).setPosition(200 + sW, sH / 2 - levelWonSprites.get(3).getHeight() / 2 + 40);
	}

	private void initialisePlayScreen() {
	    random = new Random();
	    renderer = new OrthogonalTiledMapRenderer(level);
	    reels = new Array<ReelTile>();
	    dampenedSines = new Array<DampenedSineParticle>();
	    displaySpinHelp = false;
	    scores = new Array<Score>();
	    font = new BitmapFont();
	    sW = SlotPuzzleConstants.V_WIDTH;
	    sH = SlotPuzzleConstants.V_HEIGHT;
        createPopUps();
	}

	private void createPopUps() {
	    currentLevelFont = new BitmapFont();
	    currentLevelFont.getData().scale(1.5f);
	    levelPopUp = new LevelPopUp(game.batch, tweenManager, popUpSprites, currentLevelFont, levelDoor.levelName, LEVEL_TIP_DESC);
	    levelLostPopUp = new LevelPopUp(game.batch, tweenManager, levelLostSprites, currentLevelFont, levelDoor.levelName, LEVEL_LOST_DESC);
	    levelWonPopUp = new LevelPopUp(game.batch, tweenManager, levelWonSprites, currentLevelFont, levelDoor.levelName, LEVEL_WON_DESC);
	}

	private void createSlotReelTexture() {
	    slotReelPixmap = new Pixmap(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT, Pixmap.Format.RGBA8888);		
	    slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(sprites, sprites.length);
	    slotReelTexture = new Texture(slotReelPixmap);
        slotReelScrollPixmap = new Pixmap((int) spriteWidth, (int)spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        slotReelScrollheight = slotReelScrollTexture.getHeight();
	}

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
        tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        tilePixelHeight = mapProperties.get("tileheight", Integer.class);
    }

    private void createLevels() {
 		if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
 			initialiseHiddenPlayingCards();
		}
		for (MapObject mapObject : level.getLayers().get(SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = PlayScreen.GAME_LEVEL_HEIGHT - r;
			if ((r >= 0) & (r <= PlayScreen.GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= PlayScreen.GAME_LEVEL_WIDTH)) {
				addReel(mapRectangle);
			} else {
				Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r="+r+" c="+c+". There it won't be added to the level! Sort it out in a level editor.");				
			}
		}
		reelsSpinning = reels.size - 1;
		reels = checkLevel(reels);
		reels = adjustForAnyLonelyReels(reels);
		createDampenedSines(reels);
	}

	private void initialiseHiddenPlayingCards() {
		Suit randomSuit = null;
		Pip randomPip = null;
		cards = new Array<Card>();
		int maxNumberOfPlayingCardsForLevel = level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).size;
		MapProperties levelProperties = level.getProperties();
		int numberOfCardsToDisplayForLevel = Integer.parseInt(levelProperties.get("Number Of Cards", String.class));
		hiddenPlayingCards = new Array<Integer>();
		for (int i=0; i<numberOfCardsToDisplayForLevel; i++) {
			int nextRandomHiddenPlayCard = random.nextInt(maxNumberOfPlayingCardsForLevel);
			hiddenPlayingCards.add(nextRandomHiddenPlayCard);
			if ((i & 1) == 0) {
				randomSuit = Suit.values()[random.nextInt(NUMBER_OF_SUITS)];
				randomPip = Pip.values()[random.nextInt(NUMBER_OF_CARDS_IN_A_SUIT)];
			}

			Card card = new Card(randomSuit,
					             randomPip,
					             carddeckAtlas.createSprite("back", 3),
					             carddeckAtlas.createSprite(randomSuit.name, randomPip.value));
			RectangleMapObject hiddenLevelPlayingCard = getHiddenPlayingCard(nextRandomHiddenPlayCard);
			card.setPosition(hiddenLevelPlayingCard.getRectangle().x,
					         hiddenLevelPlayingCard.getRectangle().y);
			card.setSize((int)hiddenLevelPlayingCard.getRectangle().width,
					     (int)hiddenLevelPlayingCard.getRectangle().height);

			cards.add(card);
		}
	}

	private RectangleMapObject getHiddenPlayingCard(int cardIndex) {
		return level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(cardIndex);
	}

	private void addReel(Rectangle mapRectangle) {
        int endReel = random.nextInt(sprites.length);
		ReelTile reel = new ReelTile(slotReelTexture, sprites.length, 0, 0, spriteWidth, spriteHeight, spriteWidth, spriteHeight, endReel, game.assetManager.get("sounds/reel-spinning.mp3", Sound.class));
		reel.setX(mapRectangle.getX());
		reel.setY(mapRectangle.getY());
		reel.setSx(0);
		int startReel = random.nextInt((int) slotReelScrollheight);
		startReel = (startReel / ((int) spriteHeight)) * (int)spriteHeight;
		reel.setSy(startReel);
		reel.addListener(new ReelTileListener() {
			@Override
			public void actionPerformed(ReelTileEvent event, ReelTile source) {
					if (event instanceof ReelStoppedSpinningEvent) {
						reelStoppedSound.play();
						reelsSpinning--;
						if (playState == PlayStates.PLAYING) {
							if (reelsSpinning <= -1) {
								if (levelDoor.levelType.equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
							        if (testForHiddenPatternRevealed(reels)) {
							        	iWonTheLevel();
							        }
								} else {
									if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
										if (testForHiddenPlayingCardsRevealed(reels)) {
											iWonTheLevel();
										}
									}
								}
							}
						}
					}
					if ((event instanceof ReelStoppedFlashingEvent)) {
						if (testForAnyLonelyReels(reels)) {
							win = false;
							if (Hud.getLives() > 0) {
								playState = PlayStates.LEVEL_LOST;
								setLevelLostSpritePositions();
								levelLostPopUp.showLevelPopUp(null);
							} else {
								gameOver = true;
							}
						}
						reelScoreAnimation(source);
						deleteReelAnimation(source);
					}
				}
			}
		);
		reels.add(reel);
	}

	private void createDampenedSines(Array<ReelTile> reelLevel) {
		endReelSeqs = new Array<Timeline>();
		velocityY = 4.0f;
		velocityYMin = 2.0f;
		velocityMin = new Vector(0, velocityYMin);
		acceleratorY = 3.0f;
		accelerator = new Vector(0, acceleratorY);
		accelerateY = 2.0f;
		acceleratorFriction = 0.97f;
		velocityFriction = 0.97f;
		for (ReelTile reel : reelLevel) {
			DampenedSineParticle dampenedSine = new DampenedSineParticle(0, reel.getSy(), 0, 0, 0, new Vector(0, velocityY), velocityMin, new Vector(0, acceleratorY), new Vector(0, accelerateY), velocityFriction, acceleratorFriction);
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
			ReelTile reel = (ReelTile)ds.getUserData();
			Timeline endReelSeq = Timeline.createSequence();
			float endSy = (reel.getEndReel() * spriteHeight) % slotReelScrollheight;
			reel.setSy(reel.getSy() % (slotReelScrollheight));
	        endReelSeq = endReelSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.SCROLL_XY, reelSlowingTargetTime)
	        		               .target(0f, endSy)
	        		               .ease(Elastic.OUT)
	        		               .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
	        		               .setCallback(slowingSpinningCallback)
	        		               .setUserData(reel));
	        endReelSeq = endReelSeq
	        				.start(tweenManager);
	        endReelSeqs.add(endReelSeq);
		}
	}

	private TweenCallback slowingSpinningCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			delegateSlowingSpinning(type, source);
		}
	};

	private void delegateSlowingSpinning(int type, BaseTween<?> source) {
		ReelTile reel = (ReelTile)source.getUserData();
		if (type == TweenCallback.END) {
			reel.stopSpinning();
			reel.processEvent(new ReelStoppedSpinningEvent());
		}
	}

	private void createReelIntroSequence() {
		playState = PlayStates.INTRO_SEQUENCE;
		reelSlowingTargetTime = 3.0f;
		introSequence = Timeline.createParallel();
		for(int i=0; i < reels.size; i++) {
			introSequence = introSequence
					      .push(buildSequence(reels.get(i), i, random.nextFloat() * 3.0f, random.nextFloat() * 3.0f));
		}
		introSequence = introSequence
				      .pushPause(0.3f)
				      .setCallback(introSequenceCallback)
				      .setCallbackTriggers(TweenCallback.END)
				      .start(tweenManager);
	}

	private Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
        Vector2 targetXY = getRandomCorner();
        return Timeline.createSequence()
			.push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(targetXY.x, targetXY.y))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(20, 20))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
			.push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
			.pushPause(delay1)
			.beginParallel()
				.push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
				.push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
			.end()
			.pushPause(-0.5f)
			.push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 0.8f).target(reels.get(id).getX(), reels.get(id).getY()).ease(Back.OUT))
			.push(SlotPuzzleTween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
			.pushPause(delay2)
			.beginParallel()
				.push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
				.push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
			.end()
			.pushPause(-0.5f)
			.beginParallel()
			    .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
			    .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1.0f, 1.0f).ease(Quart.INOUT))
		    .end();
	}

    private Vector2 getRandomCorner() {
        int randomCorner = random.nextInt(4);
        switch (randomCorner) {
            case 0:
                return new Vector2(-1 * random.nextFloat(), -1 * random.nextFloat());
            case 1:
                return new Vector2(-1 * random.nextFloat(), SlotPuzzleConstants.V_WIDTH + random.nextFloat());
            case 2:
                return new Vector2(SlotPuzzleConstants.V_HEIGHT / 2 + random.nextFloat(), -1 * random.nextFloat());
            case 3:
                return new Vector2(SlotPuzzleConstants.V_HEIGHT + random.nextFloat(), SlotPuzzleConstants.V_WIDTH + random.nextFloat());
            default:
                return new Vector2(-0.5f, -0.5f);
        }
    }

    private TweenCallback introSequenceCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			delegateIntroSequenceCallback(type, source);
		}
	};

	private void delegateIntroSequenceCallback(int type, BaseTween<?> source) {
		switch (type) {
		    case TweenCallback.END:
	        	playState = PlayStates.INTRO_POPUP;
	        	setPopUpSpritePositions();
	        	levelPopUp.showLevelPopUp(null);
		        break;
		}
	}

    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel) {
        TupleValueIndex[][] grid = populateMatchGrid(reelLevel);
        int arraySizeR = grid.length;
        int arraySizeC = grid[0].length;

        for(int r = 0; r < arraySizeR; r++) {
            for(int c = 0; c < arraySizeC; c++) {
                if(grid[r][c] == null) {
                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "Found null grid tile. r=" + r + " c= " + c + ". I will therefore create a deleted entry for the tile.");
                    throw new GdxRuntimeException("Level incorrect. Found null grid tile. r=" + r + " c= " + c);
               }
            }
        }
        return reelLevel;
    }

	boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel) {
		TupleValueIndex[][] matchGrid = flashSlots(levelReel);
		return hiddenPatternRevealed(matchGrid);
	}

	boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> levelReel) {
		TupleValueIndex[][] matchGrid = flashSlots(levelReel);
		return hiddenPlayingCardsRevealed(matchGrid);
	}

	boolean testForAnyLonelyReels(Array<ReelTile> levelReel) {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] grid = populateMatchGrid(levelReel);
		return puzzleGrid.anyLonelyTiles(grid);
	}

	Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel) {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] grid = populateMatchGrid(levelReel);
		Array<TupleValueIndex> lonelyTiles = puzzleGrid.getLonelyTiles(grid);
		for (TupleValueIndex lonelyTile : lonelyTiles) {
			if (lonelyTile.r == 0) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r+1][lonelyTile.c].index).getEndReel());
			} else if (lonelyTile.c == 0) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c+1].index).getEndReel());
			} else if (lonelyTile.r == GAME_LEVEL_HEIGHT) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r-1][lonelyTile.c].index).getEndReel());
			} else if (lonelyTile.c == GAME_LEVEL_WIDTH) {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c-1].index).getEndReel());
			} else {
				levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r+1][lonelyTile.c].index).getEndReel());
			}
		}
		return levelReel;
	}

	private TupleValueIndex[][] populateMatchGrid(Array<ReelTile> reelLevel) {
		TupleValueIndex[][] matchGrid = new TupleValueIndex[9][12];
		int r, c;
		for (int i = 0; i < reelLevel.size; i++) {
			c = (int) (reelLevel.get(i).getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			r = (int) (reelLevel.get(i).getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = GAME_LEVEL_HEIGHT - r;
			if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
				if (reelLevel.get(i).isReelTileDeleted()) {
					matchGrid[r][c] = new TupleValueIndex(r, c, i, -1);
				} else {
					matchGrid[r][c] = new TupleValueIndex(r, c, i, reelLevel.get(i).getEndReel());
				}
			} else {
				Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't know how to deal with r="+r+" c="+c);
			}
		}
		return matchGrid;
	}

	private void deleteReelAnimation(ReelTile source) {
		Timeline.createSequence()
		    .beginParallel()
			    .push(SlotPuzzleTween.to(source, SpriteAccessor.SCALE_XY, 0.3f).target(6, 6).ease(Quad.IN))
			    .push(SlotPuzzleTween.to(source, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
		    .end()
		    .setUserData(source)
		    .setCallback(deleteReelCallback)
		    .setCallbackTriggers(TweenCallback.COMPLETE)
			.start(tweenManager);
	}

	private TweenCallback deleteReelCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
				case TweenCallback.COMPLETE:
					ReelTile reel = (ReelTile) source.getUserData();
					Hud.addScore((reel.getEndReel() + 1) * reel.getScore());
					reelStoppedSound.play();
					chaChingSound.play();
					reel.deleteReelTile();
					if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
						testPlayingCardLevelWon();
					} else {
						if (levelDoor.levelType.equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
							testForHiddenPlatternLevelWon();
						}
					}
			}
		}
	};

	private void reelScoreAnimation(ReelTile source) {
		Score score = new Score(source.getX(), source.getY(), (source.getEndReel() + 1) * source.getScore());
		scores.add(score);
		Timeline.createSequence()
			.beginParallel()
				.push(SlotPuzzleTween.to(score, ScoreAccessor.POS_XY, 2.0f).targetRelative(random.nextInt(20), random.nextInt(160)).ease(Quad.IN))
				.push(SlotPuzzleTween.to(score, ScoreAccessor.SCALE_XY, 2.0f).target(2.0f, 2.0f).ease(Quad.IN))
			.end()
			.setUserData(score)
			.setCallback(deleteScoreCallback)
			.setCallbackTriggers(TweenCallback.COMPLETE)
			.start(tweenManager);
	}

	private TweenCallback deleteScoreCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
			case TweenCallback.COMPLETE:
				Score score = (Score) source.getUserData();
				scores.removeValue(score, false);
			}
		}
	};

	private void initialiseReelFlash(ReelTile reel, float pushPause) {
		Array<Object> userData = new Array<Object>();
		reel.setFlashTween(true);
		reelFlashSeq = Timeline.createSequence();
        reelFlashSeq = reelFlashSeq.pushPause(pushPause);

		Color fromColor = new Color(Color.WHITE);
		fromColor.a = 1;
		Color toColor = new Color(Color.RED);
		toColor.a = 1;

		userData.add(reel);
		userData.add(reelFlashSeq);

		reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
				   .target(fromColor.r, fromColor.g, fromColor.b)
				   .ease(Sine.IN));
		reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.2f)
				   .target(toColor.r, toColor.g, toColor.b)
				   .ease(Sine.OUT)
				   .repeatYoyo(17, 0));

		reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
				           .target(fromColor.r, fromColor.g, fromColor.b)
				           .ease(Sine.IN));
		reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.05f)
						   .target(toColor.r, toColor.g, toColor.b)
						   .ease(Sine.OUT)
				           .repeatYoyo(25, 0))
						   .setCallback(reelFlashCallback)
						   .setCallbackTriggers(TweenCallback.COMPLETE)
						   .setUserData(userData)
						   .start(tweenManager);
	}

	private TweenCallback reelFlashCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
				case TweenCallback.COMPLETE:
					delegateReelFlashCallback(type, source);
			}
		}
	};

	private void delegateReelFlashCallback(int type, BaseTween<?> source) {
		@SuppressWarnings("unchecked")
		Array<Object> userData = (Array<Object>) source.getUserData();
		ReelTile reel = (ReelTile) userData.get(0);
		Timeline reelFlashSeq = (Timeline) userData.get(1);
		reelFlashSeq.kill();
		if (reel.getFlashTween()) {
			reel.setFlashOff();
			reel.setFlashTween(false);
			reel.processEvent(new ReelStoppedFlashingEvent());
		}
	}

	public void handleInput(float dt) {
		if (Gdx.input.justTouched()) {
			touchX = Gdx.input.getX();
			touchY = Gdx.input.getY();
            Vector3 unprojTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojTouch);
			switch (playState) {
				case INITIALISING:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Initialising");
					break;
				case INTRO_SEQUENCE:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Intro Sequence");
					break;
				case INTRO_POPUP:
                    if (isOver(popUpSprites.get(0), unprojTouch.x, unprojTouch.y)) {
						levelPopUp.hideLevelPopUp(hideLevelPopUpCallback);
					}
					break;
				case INTRO_SPINNING:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Intro Spinning");
					break;
				case INTRO_FLASHING:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Intro Flashing");
					break;
				case PLAYING:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Playing");
					processIsTileClicked();
					break;
				case LEVEL_LOST:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Lost Level");
					if (isOver(levelLostSprites.get(0), unprojTouch.x, unprojTouch.y)) {
					    levelLostPopUp.hideLevelPopUp(levelOverCallback);
					}
					break;
				case WON_LEVEL:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Won Level");
					if(isOver(levelWonSprites.get(0), unprojTouch.x, unprojTouch.y)) {
						levelWonPopUp.hideLevelPopUp(levelWonCallback);
					}
					break;
				case RESTARTING_LEVEL:
					Gdx.app.debug(SLOTPUZZLE_SCREEN, "Restarting Level");
					break;
				default: break;
			}
		}
	}

	public boolean isOver(Sprite sprite, float x, float y) {
        return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
			&& sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
	}

	private void testPlayingCardLevelWon() {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] matchGrid = populateMatchGrid(reels);
		puzzleGrid.matchGridSlots(matchGrid);
		if (hiddenPlayingCardsRevealed(matchGrid)) {
			iWonTheLevel();
		}
	}

	private void testForHiddenPlatternLevelWon() {
		PuzzleGridType puzzleGrid = new PuzzleGridType();
		TupleValueIndex[][] matchGrid = populateMatchGrid(reels);
		puzzleGrid.matchGridSlots(matchGrid);
		if (hiddenPatternRevealed(matchGrid)) {
			iWonTheLevel();
		}
	}

	private void iWonTheLevel() {
		gameOver = true;
	    win = true;
	    playState = PlayStates.WON_LEVEL;
		mapTile.getLevel().setLevelCompleted();
		mapTile.getLevel().setScore(Hud.getScore());
	    setLevelWonSpritePositions();
	    levelWonPopUp.showLevelPopUp(null);
	}

	private TweenCallback hideLevelPopUpCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
			    case TweenCallback.END:
			    	playState = PlayStates.PLAYING;
			    	hud.resetWorldTime(300);
			    	hud.startWorldTimer();
			    	testForHiddenPatternRevealed(reels);
			}
		}
	};

	private TweenCallback levelWonCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			dispose();
			((WorldScreen)game.getWorldScreen()).worldScreenCallBack();
			game.setScreen(game.getWorldScreen());
		}
	};

    private void processIsTileClicked() {
		touchX = Gdx.input.getX();
		touchY = Gdx.input.getY();
		Vector2 newPoints = new Vector2(touchX, touchY);
		newPoints = viewport.unproject(newPoints);
		int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
		int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
		r = GAME_LEVEL_HEIGHT - r;
		if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
			TupleValueIndex[][] grid = populateMatchGrid(reels);
			ReelTile reel = reels.get(grid[r][c].index);
			DampenedSineParticle ds = dampenedSines.get(grid[r][c].index);
			if (!reel.isReelTileDeleted()) {
				if (reel.isSpinning()) {
					if (ds.getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
						reel.setEndReel(reel.getCurrentReel());
						displaySpinHelp = true;
						displaySpinHelpSprite = reel.getCurrentReel();
						Hud.addScore(-1);
						pullLeverSound.play();
						reelSpinningSound.play();
					}
				} else {
					if (!reel.getFlashTween()) {
						reelSlowingTargetTime = 3.0f;
						reel.setEndReel(random.nextInt(sprites.length - 1));

				        reel.startSpinning();
				        reelsSpinning++;
				        reel.setSy(0);
						ds.initialiseDampenedSine();
						ds.position.y = 0;
						ds.velocity = new Vector(0, velocityY);
						accelerator = new Vector(0, acceleratorY);
						ds.accelerator = accelerator;
						ds.accelerate(new Vector(0, accelerateY));
						ds.velocityMin.y = velocityMin.y;
						Hud.addScore(-1);
						pullLeverSound.play();
					}
				}
			}
		} else {
			Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
		}
	}

	private boolean hiddenPatternRevealed(TupleValueIndex[][] grid) {
		boolean hiddenPattern = true;
		for (MapObject mapObject : level.getLayers().get(HIDDEN_PATTERN_LAYER).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
			int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
			r = GAME_LEVEL_HEIGHT - r;
			if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
				if (grid[r][c] != null) {
					if (!reels.get(grid[r][c].getIndex()).isReelTileDeleted()) {
						hiddenPattern = false;
					}
				}
			} else {
				Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
			}
		}
		return hiddenPattern;
	}

	private boolean hiddenPlayingCardsRevealed(TupleValueIndex[][] grid) {
		boolean hiddenPlayingCardsRevealed = true;
		for (Integer hiddenPlayingCard : hiddenPlayingCards) {
		    MapObject mapObject = level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(hiddenPlayingCard.intValue()); 
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			for (int ro = (int) (mapRectangle.getX()); ro < (int) (mapRectangle.getX() + mapRectangle.getWidth()); ro += PlayScreen.TILE_WIDTH) {
			    for (int co = (int) (mapRectangle.getY()) ; co < (int) (mapRectangle.getY() + mapRectangle.getHeight()); co += PlayScreen.TILE_HEIGHT) {
					int c = (int) (ro - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
					int r = (int) (co - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
					r = GAME_LEVEL_HEIGHT - r;
					if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
						if (grid[r][c] != null) {
							if (!reels.get(grid[r][c].getIndex()).isReelTileDeleted()) {
								hiddenPlayingCardsRevealed = false;
							}
						}
					} else {
						Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
					}
			    }
			}
		}
		return hiddenPlayingCardsRevealed;
	}

	private ReelTileGridValue[][] flashSlots(Array<ReelTile> reelTiles) {
		PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
		ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles,  mapWidth, mapHeight);

		Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
		Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

		matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);
		for (TupleValueIndex matchedSlot : matchedSlots) {
			reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);
		}
		flashMatchedSlots(matchedSlots, puzzleGridTypeReelTile);
		return puzzleGrid;
	}

	private void flashMatchedSlotsBatch(Array<ReelTileGridValue> matchedSlots, float pushPause) {
		int index;
		for (int i = 0; i < matchedSlots.size; i++) {
			index = matchedSlots.get(i).getIndex();
			if (index  >= 0) {
				ReelTile reel = reels.get(index);
				if (!reel.getFlashTween()) {
					reel.setFlashMode(true);
					Color flashColor = new Color(Color.WHITE);
					reel.setFlashColor(flashColor);
					initialiseReelFlash(reel, pushPause);
				}
			}
		}
	}

	private void flashMatchedSlots(Array<ReelTileGridValue> matchedSlots, PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
		int matchSlotIndex, batchIndex, batchPosition;
		Array<ReelTileGridValue> matchSlotsBatch = new Array<ReelTileGridValue>();
		float pushPause = 0.0f;
		matchSlotIndex = 0;
		while (matchedSlots.size > 0) {
			batchIndex = matchSlotIndex;
			for (int batchCount = batchIndex; batchCount < batchIndex+3; batchCount++) {
				if (batchCount < matchedSlots.size) {
					batchPosition = matchSlotsBatch.size;
					matchSlotsBatch = puzzleGridTypeReelTile.depthFirstSearchAddToMatchSlotBatch(matchedSlots.get(0), matchSlotsBatch);

					for (int deleteIndex=batchPosition; deleteIndex<matchSlotsBatch.size; deleteIndex++) {
						matchedSlots.removeValue(matchSlotsBatch.get(deleteIndex), true);
					}
				}
			}
			flashMatchedSlotsBatch(matchSlotsBatch, pushPause);
			pushPause += 2.0f;
			matchSlotsBatch.clear();
		}
	}

	private TweenCallback levelOverCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
				case TweenCallback.END:
					delegateLevelOverCallback(type, source);
			}
		}
	};

	private void delegateLevelOverCallback(int type, BaseTween<?> source) {
		tweenManager.killAll();
		Hud.resetScore();
		Hud.loseLife();
		hud.resetWorldTime(300);
		renderer = new OrthogonalTiledMapRenderer(level);
		reels = new Array<ReelTile>();
		dampenedSines = new Array<DampenedSineParticle>();
		displaySpinHelp = false;
		inRestartLevel = false;
		createLevels();
		createReelIntroSequence();
	}

    private void update(float delta) {
		tweenManager.update(delta);
		int dsIndex = 0;
		for (ReelTile reel : reels) {
			dampenedSines.get(dsIndex).update();
        	if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
         		reel.setSy(dampenedSines.get(dsIndex).position.y);
  	       	}
			reel.update(delta);
			dsIndex++;
		}
		renderer.setView(camera);
		hud.update(delta);
		if (hud.getWorldTime() == 0) {
			if ((Hud.getLives() > 0) & (!inRestartLevel)) {
				inRestartLevel = true;
				playState = PlayStates.LEVEL_LOST;
				setLevelLostSpritePositions();
				levelLostPopUp.showLevelPopUp(null);
			} else {
				gameOver = true;
			}
		}
		if ((gameOver) & (!win) & (Hud.getLives() == 0)) {
			dispose();
			game.setScreen(new EndOfGameScreen(game));
		}
	}

	@Override
	public void render(float delta) {
        if (show) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if (isLoaded) {
                update(delta);
                handleInput(delta);
                renderer.render();
                game.batch.begin();
                if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
                    drawPlayingCards(game.batch);
                }
                for (ReelTile reel : reels) {
                    if (!reel.isReelTileDeleted()) {
                        reel.draw(game.batch);
                    }
                }
                for (Score score : scores) {
                    score.render(game.batch);
                }
                if (displaySpinHelp) {
                    sprites[displaySpinHelpSprite].draw(game.batch);
                }
                game.batch.end();
                switch (playState) {
                    case INTRO_POPUP:
                        levelPopUp.draw(game.batch);
                        break;
                    case LEVEL_LOST:
                        levelLostPopUp.draw(game.batch);
                        break;
                    case WON_LEVEL:
                        levelWonPopUp.draw(game.batch);
                        break;
                    case INITIALISING:
                        break;
                    case INTRO_FLASHING:
                        break;
                    case INTRO_SEQUENCE:
                        break;
                    case INTRO_SPINNING:
                        break;
                    case PLAYING:
                        break;
                    case RESTARTING_LEVEL:
                        break;
                    default:
                        break;
                }
                game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
                hud.stage.draw();
            } else {
                if (game.assetManager.getProgress() < 1) {
                    game.assetManager.update();
                } else {
                    isLoaded = true;
                }
            }
            stage.draw();
        }
    }

    @Override
    public void show() {
        this.show = true;
		Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "show() called.");
    }

    @Override
	public void resize(int width, int height) {
		viewport.update(width,  height);
	}

	@Override
	public void pause() {
        this.show = false;
		Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "pause() called.");
	}

	@Override
	public void resume() {
		Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "resume() called.");
	}

	@Override
	public void hide() {
		this.show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "hide() called.");
	}

	@Override
	public void dispose() {
		stage.dispose();
		font.dispose();
		for (ReelTile reel : reels) {
			reel.dispose();
		}
		chaChingSound.dispose();
	}

	private void drawPlayingCards(SpriteBatch spriteBatch) {
		for (Card card : cards) {
			card.draw(spriteBatch);
		}
	}
}
