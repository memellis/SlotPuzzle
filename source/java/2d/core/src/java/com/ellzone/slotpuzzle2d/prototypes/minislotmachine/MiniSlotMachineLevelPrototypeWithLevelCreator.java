/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
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
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Card;
import com.ellzone.slotpuzzle2d.level.LevelCreator;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.MiniSlotMachineLevel;
import com.ellzone.slotpuzzle2d.level.Pip;
import com.ellzone.slotpuzzle2d.level.Suit;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.sprites.Score;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import net.dermetfan.gdx.assets.AnnotationAssetManager;
import aurelienribon.tweenengine.equations.Elastic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Sine;

public class MiniSlotMachineLevelPrototypeWithLevelCreator extends SPPrototypeTemplate {
    public static final int GAME_LEVEL_WIDTH = 11;
    public static final int GAME_LEVEL_HEIGHT = 8;
    public static final String REEL_OBJECT_LAYER = "Reels";
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String MINI_SLOT_MACHINE_LEVEL_NAME = "Mini Slot Machine";
    public static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final int NUMBER_OF_SUITS = 4;
    public static final int NUMBER_OF_CARDS_IN_A_SUIT = 13;

    private String logTag = SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName();
    private TiledMap miniSlotMachineLevel;
    private MapTile mapTile;
    private MapProperties levelProperties;
    private TextureAtlas reelAtlas, tilesAtlas, carddeckAtlas;
    private MiniSlotMachineLevel miniSlotMachineLeve1;
    private Array<DampenedSineParticle> dampenedSines;
    private PlayScreen.PlayStates playState;
    private Reels reels;
    private Array<ReelTile> reelTiles;
    private LevelDoor levelDoor;
    private Array<Score> scores;
    private Array<Card> cards;
    private Array<Integer> hiddenPlayingCards;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private int sW, sH;
    private Pixmap slotReelPixmap, slotReelScrollPixmap;
    private Texture slotReelTexture, slotReelScrollTexture;
    private int slotReelScrollheight;
    private int reelsSpinning;
    private Sound chaChingSound, pullLeverSound, reelSpinningSound, reelStoppedSound, jackpotSound;
    private Vector accelerator, velocityMin;
    private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;
    private float reelSlowingTargetTime;
    private Array<Timeline> endReelSeqs;
    private Timeline reelFlashSeq;
    private LevelCreator levelCreator;
    private boolean gameOver = false;
    private boolean inRestartLevel = false;
    private boolean win = false;
    private boolean displaySpinHelp;
    private int displaySpinHelpSprite;
    private int mapWidth, mapHeight, tilePixelWidth, tilePixelHeight;
    private Hud hud;

    @Override
    protected void initialiseOverride() {
        initialiseReels();
        createSlotReelTexture();
        getAssets(annotationAssetManager);
        this.miniSlotMachineLevel = annotationAssetManager.get(AssetsAnnotation.MINI_SLOT_MACHINE_LEVEL);
        getMapProperties(this.miniSlotMachineLevel);
        initialiseLevelDoor();
        createPlayScreen();
        this.levelCreator = new LevelCreator(this.levelDoor, this.miniSlotMachineLevel, this.annotationAssetManager, this.carddeckAtlas, this.tweenManager, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
        this.reelTiles = new Array<ReelTile>();
        this.reelTiles = createLevels(this.miniSlotMachineLevel, this.reelTiles);
        reelsSpinning = reelTiles.size - 1;
        hud = new Hud(batch);
        hud.setLevelName(levelDoor.levelName);
        playState = PlayScreen.PlayStates.PLAYING;
    }

    private void getAssets(AssetManager assetManager) {
        this.carddeckAtlas = assetManager.get(AssetsAnnotation.CARDDECK);
        this.chaChingSound = assetManager.get(AssetsAnnotation.SOUND_CHA_CHING);
        this.pullLeverSound = assetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        this.reelSpinningSound = assetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        this.reelStoppedSound = assetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
        this.jackpotSound = assetManager.get(AssetsAnnotation.SOUND_JACKPOINT);
    }

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
        tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        tilePixelHeight = mapProperties.get("tileheight", Integer.class);
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

    @Override
    protected void initialiseScreenOverride() {
    }

    private void createPlayScreen() {
        this.playState = PlayScreen.PlayStates.INITIALISING;
        initialisePlayScreen();
    }

    private void initialiseReels() {
        this.reels = new Reels();
    }

    private void initialiseLevelDoor() {
        levelDoor = new LevelDoor();
        levelDoor.levelName = MINI_SLOT_MACHINE_LEVEL_NAME;
        levelDoor.levelType = BONUS_LEVEL_TYPE;
    }

    private void initialisePlayScreen() {
        this.tileMapRenderer = new OrthogonalTiledMapRenderer(miniSlotMachineLevel);
        this.dampenedSines = new Array<DampenedSineParticle>();
        this.font = new BitmapFont();
        this.sW = SlotPuzzleConstants.V_WIDTH;
        this.sH = SlotPuzzleConstants.V_HEIGHT;
        reelTiles = new Array<ReelTile>();
        scores = new Array<Score>();
    }

    private Array<ReelTile> createLevels(TiledMap level, Array<ReelTile> reelTiles) {
        if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
            initialiseHiddenPlayingCards();
        }
        reelTiles = populateLevel(level, reelTiles);
        reelTiles = checkLevel(reelTiles);
        reelTiles = adjustForAnyLonelyReels(reelTiles);
        createDampenedSines(reelTiles);
        return reelTiles;
    }

    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel) {
        TupleValueIndex[][] grid = populateMatchGrid(reelLevel);
        PuzzleGridType.printGrid(grid);
        int arraySizeR = grid.length;
        int arraySizeC = grid[0].length;

        for(int r = 0; r < arraySizeR; r++) {
            for(int c = 0; c < arraySizeC; c++) {
                if(grid[r][c] == null) {

                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "Found null grid tile. r=" + r + " c= " + c + ". I will therefore create a deleted entry for the tile.");
                    grid[r][c] = new TupleValueIndex(r, c, -1, -1);
                }
            }
        }
        return reelLevel;
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

    private void initialiseHiddenPlayingCards() {
        Suit randomSuit = null;
        Pip randomPip = null;
        cards = new Array<Card>();
        int maxNumberOfPlayingCardsForLevel = miniSlotMachineLevel.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).size;
        levelProperties = miniSlotMachineLevel.getProperties();
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

    private Array<ReelTile> populateLevel(TiledMap level, Array<ReelTile> reelTiles) {
        for (MapObject mapObject : level.getLayers().get(REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
            int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
            r = PlayScreen.GAME_LEVEL_HEIGHT - r;
            if ((r >= 0) & (r <= PlayScreen.GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= PlayScreen.GAME_LEVEL_WIDTH)) {
                addReel(mapRectangle, reelTiles);
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r=" + r + " c=" + c + ". There it won't be added to the level! Sort it out in a level editor.");
            }
        }
        return reelTiles;
    }

    private void addReel(Rectangle mapRectangle, Array<ReelTile> reelTiles) {
        int endReel = random.nextInt(this.reels.getReels().length);
        ReelTile reel = new ReelTile(slotReelTexture, this.reels.getReels().length, 0, 0, reels.getReelWidth(), reels.getReelHeight(), reels.getReelWidth(), reels.getReelHeight(), endReel, this.reelSpinningSound);
        reel.setX(mapRectangle.getX());
        reel.setY(mapRectangle.getY());
        reel.setSx(0);
        int startReel = random.nextInt((int) slotReelScrollheight);
        startReel = (startReel / ((int) this.reels.getReelWidth())) * (int) this.reels.getReelHeight();
        reel.setSy(startReel);
        addReelListener(reel);
        reelTiles.add(reel);
    }

    private ReelTile addReelListener(ReelTile reel) {
        reel.addListener(new ReelTileListener() {
            @Override
            public void actionPerformed(ReelTileEvent event, ReelTile source) {
                if (event instanceof ReelStoppedSpinningEvent) {
                    actionReelStoppedSpinning(event, source);
                }
                if (event instanceof ReelStoppedFlashingEvent) {
                    actionReelStoppedFlasshing(event, source);
                }
            }
        });
        return reel;
    }

    private void actionReelStoppedSpinning(ReelTileEvent event, ReelTile source) {
        playSound(reelSpinningSound);

        this.reelsSpinning--;
        if (playState == PlayScreen.PlayStates.PLAYING) {
            if (reelsSpinning <= -1) {
                if (levelDoor.levelType.equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
                    if (testForHiddenPatternRevealed(reelTiles)) {
                        iWonTheLevel();
                    }
                }
                if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
                    if (testForHiddenPlayingCardsRevealed(reelTiles)) {
                        iWonTheLevel();
                    }
                }
            }
            if (levelDoor.levelType.equals(BONUS_LEVEL_TYPE)) {
                if (testForJackpot(reelTiles)) {
                    iWonABonus();
                }
            }
        }
    }

    private boolean testForJackpot(Array<ReelTile> levelReel) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel);
        return true;
    }

    private void iWonTheLevel() {
        gameOver = true;
        win = true;
        playState = PlayScreen.PlayStates.WON_LEVEL;
        mapTile.getLevel().setLevelCompleted();
        mapTile.getLevel().setScore(Hud.getScore());
    }

    private void iWonABonus() {
        System.out.println("iWonABonus!");
    }

    private RectangleMapObject getHiddenPlayingCard(int cardIndex) {
        return miniSlotMachineLevel.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(cardIndex);
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
            MapObject mapObject = miniSlotMachineLevel.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(hiddenPlayingCard.intValue());
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

    private boolean hiddenPatternRevealed(TupleValueIndex[][] grid) {
        boolean hiddenPattern = true;
        for (MapObject mapObject : miniSlotMachineLevel.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class)) {
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


    private void actionReelStoppedFlasshing(ReelTileEvent event, ReelTile reelTile) {
        if (testForAnyLonelyReels(reelTiles)) {
            win = false;
            if (Hud.getLives() > 0) {
                playState = PlayScreen.PlayStates.LEVEL_LOST;
            } else {
                gameOver = true;
            }
        }
        reelScoreAnimation(reelTile);
        deleteReelAnimation(reelTile);
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
        PuzzleGridType.printGrid(grid);
        return puzzleGrid.anyLonelyTiles(grid);
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
                    playSound(reelStoppedSound);
                    playSound(chaChingSound);
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
        int touchX, touchY;

        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector3 unprojTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojTouch);
            switch (playState) {
                case INITIALISING:
                    Gdx.app.debug(logTag, "Initialising");
                    break;
                case INTRO_SEQUENCE:
                    Gdx.app.debug(logTag, "Intro Sequence");
                    break;
                case INTRO_POPUP:
                    break;
                case INTRO_SPINNING:
                    Gdx.app.debug(logTag, "Intro Spinning");
                    break;
                case INTRO_FLASHING:
                    Gdx.app.debug(logTag, "Intro Flashing");
                    break;
                case PLAYING:
                    Gdx.app.debug(logTag, "Playing");
                    processIsTileClicked();
                    break;
                case LEVEL_LOST:
                    Gdx.app.debug(logTag, "Lost Level");
                    break;
                case WON_LEVEL:
                    Gdx.app.debug(logTag, "Won Level");
                    break;
                case RESTARTING_LEVEL:
                    Gdx.app.debug(logTag, "Restarting Level");
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
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();
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
                        if (pullLeverSound != null) {
                            pullLeverSound.play();
                        }
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

    @Override
    protected void loadAssetsOverride() {
     }

    @Override
    protected void disposeOverride() {

    }

    @Override
    protected void updateOverride(float dt) {
        tweenManager.update(dt);
        int dsIndex = 0;
        for (ReelTile reel : reelTiles) {
            dampenedSines.get(dsIndex).update();
            if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                reel.setSy(dampenedSines.get(dsIndex).position.y);
            }
            reel.update(dt);
            dsIndex++;
        }
        tileMapRenderer.setView(orthographicCamera);
        hud.update(dt);
        if (hud.getWorldTime() == 0) {
            if ((Hud.getLives() > 0) & (!inRestartLevel)) {
                inRestartLevel = true;
                playState = PlayScreen.PlayStates.LEVEL_LOST;
            } else {
                gameOver = true;
            }
        }

    }

    @Override
    protected void renderOverride(float dt) {
        handleInput(dt);
        tileMapRenderer.render();
        batch.begin();
        if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
            drawPlayingCards(batch);
        }
        for (ReelTile reel : reelTiles) {
            if (!reel.isReelTileDeleted()) {
                reel.draw(batch);
            }
        }
        for (Score score : scores) {
            score.render(batch);
        }
        if (displaySpinHelp) {
            reels.getReels()[displaySpinHelpSprite].draw(batch);
        }
        batch.end();
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
        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        stage.draw();

    }

    private void drawPlayingCards(SpriteBatch spriteBatch) {
        for (Card card : cards) {
            card.draw(spriteBatch);
        }
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
    }

    private void playSound(Sound sound) {
        if (sound != null) {
            sound.play();
        }
    }
}
