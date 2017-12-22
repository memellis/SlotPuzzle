/*******************************************************************************
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

package com.ellzone.slotpuzzle2d.prototypes.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.assets.AssetManager;
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
import com.ellzone.slotpuzzle2d.level.Pip;
import com.ellzone.slotpuzzle2d.level.Suit;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.screens.EndOfGameScreen;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.Score;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Sine;

public class PlayScreenPrototype implements Screen {
    public static final int GAME_LEVEL_WIDTH = 11;
    public static final int GAME_LEVEL_HEIGHT = 8;
    public static final int SLOT_REEL_OBJECT_LAYER = 2;
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final int NUMBER_OF_SUITS = 4;
    public static final int HIDDEN_PATTERN_LAYER = 0;
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final int NUMBER_OF_CARDS_IN_A_SUIT = 13;
    public static final int FLASH_MATCHED_SLOTS_BATCH_SIZE = 8;
    public static final String SLOTPUZZLE_SCREEN = "PlayScreen";

    private SlotPuzzle game;
    private LevelDoor levelDoor;
    private PlayScreen.PlayStates playState;
    private Viewport viewport;
    private Stage stage;
    private OrthographicCamera camera;
    private AnnotationAssetManager annotationAssetManager;
    private TextureAtlas reelAtlas, tilesAtlas, carddeckAtlas;
    private Sound chaChingSound, pullLeverSound, reelSpinningSound, reelStoppedSound, jackpotSound;
    private TiledMap level;
    private MapTile mapTile;
    private Reels reels;
    private Array<DampenedSineParticle> dampenedSines;
    private Random random;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private BitmapFont font;
    private int sW, sH;
    private Pixmap slotReelPixmap, slotReelScrollPixmap;
    private Texture slotReelTexture, slotReelScrollTexture;
    private int slotReelScrollheight;
    private Array<Card> cards;
    private int reelsSpinning;
    private Array<Score> scores;
    private Array<ReelTile> reelTiles;
    private Array<Integer> hiddenPlayingCards;
    private boolean gameOver = false;
    private boolean inRestartLevel = false;
    private boolean win = false;
    private Vector accelerator, velocityMin;
    private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;
    private Array<Timeline> endReelSeqs;
    private Timeline reelFlashSeq;
    private TweenManager tweenManager = new TweenManager();;
    private int touchX, touchY;
    private boolean displaySpinHelp;
    private int displaySpinHelpSprite;
    private float reelSlowingTargetTime;
    private Hud hud;
    private boolean isLoaded = false;
    private MapProperties levelProperties;
    private int mapWidth, mapHeight, tilePixelWidth, tilePixelHeight;

    public PlayScreenPrototype(SlotPuzzle game, LevelDoor levelDoor, MapTile mapTile) {
        this.game = game;
        this.levelDoor = levelDoor;
        this.mapTile = mapTile;
        createPlayScreen();
    }

    private void createPlayScreen() {
        playState = PlayScreen.PlayStates.INITIALISING;
        initialiseScreen();
        initialiseTweenEngine();
        getAssets(this.game.annotationAssetManager);
        initialiseReels(this.game.annotationAssetManager);
        initialisePlayScreen();
        createSlotReelTexture();
        createLevels();
        getMapProperties(this.level);
        hud = new Hud(this.game.batch);
        hud.setLevelName(levelDoor.levelName);
        playState = PlayScreen.PlayStates.PLAYING;
    }

    private void initialiseScreen() {
        this.camera = new OrthographicCamera();
        this.viewport = new FitViewport(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT, camera);
        this.stage = new Stage(this.viewport, this.game.batch);
    }

    private void initialiseTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
    }


    private void getAssets(AnnotationAssetManager annotationAssetManager) {
        this.reelAtlas = annotationAssetManager.get(AssetsAnnotation.REELS);
        this.tilesAtlas = annotationAssetManager.get(AssetsAnnotation.TILES);
        this.carddeckAtlas = annotationAssetManager.get(AssetsAnnotation.CARDDECK);
        this.chaChingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_CHA_CHING);
        this.pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        this.reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        this.reelStoppedSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
        this.jackpotSound = annotationAssetManager.get(AssetsAnnotation.SOUND_JACKPOINT);
        this.level = annotationAssetManager.get("levels/level " + (this.levelDoor.id + 1) + " - 40x40.tmx");
    }

    private void initialiseReels(AnnotationAssetManager annotationAssetManager) {
        reels = new Reels(annotationAssetManager);
    }

    private void initialisePlayScreen() {
        this.tileMapRenderer = new OrthogonalTiledMapRenderer(level);
        this.dampenedSines = new Array<DampenedSineParticle>();
        this.font = new BitmapFont();
        this.sW = SlotPuzzleConstants.V_WIDTH;
        this.sH = SlotPuzzleConstants.V_HEIGHT;
        reelTiles = new Array<ReelTile>();
        scores = new Array<Score>();
    }

    private void createSlotReelTexture() {
        slotReelPixmap = new Pixmap(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT, Pixmap.Format.RGBA8888);
        slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(reels.getReels(), reels.getReels().length);
        slotReelTexture = new Texture(slotReelPixmap);
        slotReelScrollPixmap = new Pixmap((int) reels.getReelWidth(), (int)reels.getReelHeight(), Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reels.getReels());
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        slotReelScrollheight = slotReelScrollTexture.getHeight();
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
        reelsSpinning = reelTiles.size - 1;
        reelTiles = checkLevel(reelTiles);
        reelTiles = adjustForAnyLonelyReels(reelTiles);
        createDampenedSines(reelTiles);
    }

    private void initialiseHiddenPlayingCards() {
        Suit randomSuit = null;
        Pip randomPip = null;
        cards = new Array<Card>();
        int maxNumberOfPlayingCardsForLevel = level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).size;
        levelProperties = level.getProperties();
        int numberOfCardsToDisplayForLevel = Integer.parseInt(levelProperties.get("Number Of Cards", String.class));
        hiddenPlayingCards = new Array<Integer>();
        for (int i=0; i<numberOfCardsToDisplayForLevel; i++) {
            int nextRandomHiddenPlayCard = Random.getInstance().nextInt(maxNumberOfPlayingCardsForLevel);
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

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
        tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        tilePixelHeight = mapProperties.get("tileheight", Integer.class);
    }

    private void addReel(Rectangle mapRectangle) {
        int endReel = Random.getInstance().nextInt(this.reels.getReels().length);
        ReelTile reel = new ReelTile(slotReelTexture, this.reels.getReels().length, 0, 0, reels.getReelWidth(), reels.getReelHeight(), reels.getReelWidth(), reels.getReelHeight(), endReel, (Sound) game.annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING));
        reel.setX(mapRectangle.getX());
        reel.setY(mapRectangle.getY());
        reel.setSx(0);
        int startReel = random.nextInt((int) slotReelScrollheight);
        startReel = (startReel / ((int) this.reels.getReelWidth())) * (int)this.reels.getReelHeight();
        reel.setSy(startReel);
        reel.addListener(new ReelTileListener() {
                             @Override
                             public void actionPerformed(ReelTileEvent event, ReelTile source) {
                                 if (event instanceof ReelStoppedSpinningEvent) {
                                     reelStoppedSound.play();
                                     reelsSpinning--;
                                     if (playState == PlayScreen.PlayStates.PLAYING) {
                                         if (reelsSpinning <= -1) {
                                             if (levelDoor.levelType.equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
                                                 if (testForHiddenPatternRevealed(reelTiles)) {
                                                     iWonTheLevel();
                                                 }
                                             } else {
                                                 if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
                                                     if (testForHiddenPlayingCardsRevealed(reelTiles)) {
                                                         iWonTheLevel();
                                                     }
                                                 }
                                             }
                                         }
                                     }
                                 }
                                 if ((event instanceof ReelStoppedFlashingEvent)) {
                                     if (testForAnyLonelyReels(reelTiles)) {
                                         win = false;
                                         if (Hud.getLives() > 0) {
                                             playState = PlayScreen.PlayStates.LEVEL_LOST;
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
        reelTiles.add(reel);
    }

    private void createDampenedSines(Array<ReelTile> reelLevel) {
        reelSlowingTargetTime = 3.0f;
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
            float endSy = (reel.getEndReel() * reels.getReelHeight()) % slotReelScrollheight;
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

    private RectangleMapObject getHiddenPlayingCard(int cardIndex) {
        return level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(cardIndex);
    }

    boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel);
        return hiddenPatternRevealed(matchGrid);
    }

    boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> levelReel) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel);
        return hiddenPlayingCardsRevealed(matchGrid);
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
                            if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted()) {
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
                ReelTile reel = reelTiles.get(index);
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
        PuzzleGridTypeReelTile.printMatchedSlots(matchedSlots);
        while (matchedSlots.size > 0) {
            batchIndex = matchSlotIndex;
            for (int batchCount = batchIndex; batchCount < batchIndex+3; batchCount++) {
                if (batchCount < matchedSlots.size) {
                    batchPosition = matchSlotsBatch.size;
                    matchSlotsBatch = puzzleGridTypeReelTile.depthFirstSearchAddToMatchSlotBatch(matchedSlots.get(0), matchSlotsBatch);
                    PuzzleGridTypeReelTile.printMatchedSlots(matchSlotsBatch);

                    for (int deleteIndex=batchPosition; deleteIndex<matchSlotsBatch.size; deleteIndex++) {
                        matchedSlots.removeValue(matchSlotsBatch.get(deleteIndex), true);
                    }
                }
            }
            PuzzleGridTypeReelTile.printMatchedSlots(matchSlotsBatch);
            if (matchSlotsBatch.size == 0) {
                break;
            }
            flashMatchedSlotsBatch(matchSlotsBatch, pushPause);
            pushPause += 2.0f;
            matchSlotsBatch.clear();
        }
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
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+" c="+c);
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
                    break;
                case WON_LEVEL:
                    Gdx.app.debug(SLOTPUZZLE_SCREEN, "Won Level");
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

    private void processIsTileClicked() {
        touchX = Gdx.input.getX();
        touchY = Gdx.input.getY();
        Vector2 newPoints = new Vector2(touchX, touchY);
        newPoints = viewport.unproject(newPoints);
        int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
        int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
        r = GAME_LEVEL_HEIGHT - r;
        if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
            TupleValueIndex[][] grid = populateMatchGrid(reelTiles);
            ReelTile reel = reelTiles.get(grid[r][c].index);
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
                        reel.setEndReel(random.nextInt(reels.getReels().length - 1));

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

    private void testPlayingCardLevelWon() {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = populateMatchGrid(reelTiles);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPlayingCardsRevealed(matchGrid)) {
            iWonTheLevel();
        }
    }

    private void testForHiddenPlatternLevelWon() {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = populateMatchGrid(reelTiles);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPatternRevealed(matchGrid)) {
            iWonTheLevel();
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
                    if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted()) {
                        hiddenPattern = false;
                    }
                }
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
            }
        }
        return hiddenPattern;
    }

    private void iWonTheLevel() {
        gameOver = true;
        win = true;
        playState = PlayScreen.PlayStates.WON_LEVEL;
        mapTile.getLevel().setLevelCompleted();
        mapTile.getLevel().setScore(Hud.getScore());
     }

    private void update(float delta) {
        tweenManager.update(delta);
        int dsIndex = 0;
        for (ReelTile reel : reelTiles) {
            dampenedSines.get(dsIndex).update();
            if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                reel.setSy(dampenedSines.get(dsIndex).position.y);
            }
            reel.update(delta);
            dsIndex++;
        }
        tileMapRenderer.setView(camera);
        hud.update(delta);
        if (hud.getWorldTime() == 0) {
            if ((Hud.getLives() > 0) & (!inRestartLevel)) {
                inRestartLevel = true;
                playState = PlayScreen.PlayStates.LEVEL_LOST;
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
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        if(isLoaded) {
            update(delta);
            handleInput(delta);
            tileMapRenderer.render();
            game.batch.begin();
            if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
                drawPlayingCards(game.batch);
            }
            for (ReelTile reel : reelTiles) {
                if (!reel.isReelTileDeleted()) {
                    reel.draw(game.batch);
                }
            }
            for (Score score : scores) {
                score.render(game.batch);
            }
            if (displaySpinHelp) {
                reels.getReels()[displaySpinHelpSprite].draw(game.batch);
            }
            game.batch.end();
            switch (playState) {
                case INTRO_POPUP:
                    break;
                case LEVEL_LOST:
                    break;
                case WON_LEVEL:
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
            stage.draw();
        } else {
            if (game.annotationAssetManager.getProgress() < 1) {
                game.annotationAssetManager.update();
            } else {
                isLoaded = true;
            }
        }
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
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
        if (stage != null ) {
            stage.dispose();
        }
        if (font != null ) {
            font.dispose();
        }
        for (ReelTile reel : reelTiles) {
            reel.dispose();
        }
        if (chaChingSound != null) {
            chaChingSound.dispose();
        }
    }

    private void drawPlayingCards(SpriteBatch spriteBatch) {
        for (Card card : cards) {
            card.draw(spriteBatch);
        }
    }
}
