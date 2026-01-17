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
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle.SlotPuzzleGame;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.audio.AudioManager;
import com.ellzone.slotpuzzle.effects.ReelAccessor;
import com.ellzone.slotpuzzle.effects.ScoreAccessor;
import com.ellzone.slotpuzzle.effects.SpriteAccessor;
import com.ellzone.slotpuzzle.finitestatemachine.PlayInterface;
import com.ellzone.slotpuzzle.finitestatemachine.PlayState;
import com.ellzone.slotpuzzle.finitestatemachine.PlayStateMachine;
import com.ellzone.slotpuzzle.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle.level.LevelDoor;
import com.ellzone.slotpuzzle.level.card.Card;
import com.ellzone.slotpuzzle.level.creator.LevelCallback;
import com.ellzone.slotpuzzle.level.creator.LevelCreator;
import com.ellzone.slotpuzzle.level.creator.LevelCreatorInjectionInterface;
import com.ellzone.slotpuzzle.level.creator.LevelLoader;
import com.ellzone.slotpuzzle.level.creator.PlayScreenLevel;
import com.ellzone.slotpuzzle.level.hidden.HiddenPattern;
import com.ellzone.slotpuzzle.level.hidden.HiddenPlayingCard;
import com.ellzone.slotpuzzle.level.popups.PlayScreenPopUps;
import com.ellzone.slotpuzzle.level.sequence.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle.scene.MapTile;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.sprites.score.ScoreSprite;
import com.ellzone.slotpuzzle.tweenengine.BaseTween;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle.utils.convert.TileMapToWorldConvert;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.util.Random;

import aurelienribon.tweenengine.equations.Quad;
import box2dLight.RayHandler;

import static com.ellzone.slotpuzzle.SlotPuzzleConstants.PIXELS_PER_METER;
import static com.ellzone.slotpuzzle.SlotPuzzleConstants.VIRTUAL_HEIGHT;
import static com.ellzone.slotpuzzle.SlotPuzzleConstants.VIRTUAL_WIDTH;
import static com.ellzone.slotpuzzle.level.creator.LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE;
import static com.ellzone.slotpuzzle.level.creator.LevelCreator.PLAYING_CARD_LEVEL_TYPE;
import static com.ellzone.slotpuzzle.messaging.MessageType.PauseAudio;
import static com.ellzone.slotpuzzle.messaging.MessageType.PlayAudio;
import static com.ellzone.slotpuzzle.messaging.MessageType.StopAudio;

public class PlayScreen implements Screen, PlayInterface, LevelCreatorInjectionInterface {
    public static final float PUZZLE_GRID_START_X = 160.0f;
    public static final float PUZZLE_GRID_START_Y = 40.0f;
    public static final String SLOTPUZZLE_SCREEN = "PlayScreen";
    public static final int LEVEL_TIME_LENGTH_IN_SECONDS = 300;

    protected SlotPuzzleGame game;
    protected Viewport viewport, lightViewport;
    protected OrthographicCamera camera = new OrthographicCamera();
    protected LevelDoor levelDoor;
    protected PlayScreenLevel playScreenLevel;
    protected Sprite[] sprites;
    protected Array<AnimatedReel> animatedReels;
    protected Array<ReelTile> reelTiles;
    protected int reelsSpinning;
    protected MapTile mapTileLevel;
    protected Random random;
    protected PlayStates playState;
    protected PlayStateMachine playStateMachine;
    protected PlayScreenPopUps playScreenPopUps;
    protected boolean displaySpinHelp;
    protected int displaySpinHelpSprite;
    protected boolean gameOver = false;
    protected Array<ScoreSprite> scores;
    protected Sound chaChingSound,
        pullLeverSound,
        reelSpinningSound,
        reelStoppedSound;

    private LevelLoader levelLoader;
    protected Stage stage;
    private TextureAtlas tilesAtlas;
    protected boolean isLoaded = false;
    protected TiledMap tiledMapLevel;
    protected OrthogonalTiledMapRenderer renderer;
    private boolean inRestartLevel = false;
    private boolean win = false;
    private BitmapFont font;
    private HiddenPattern hiddenPattern;
    protected boolean show = false;
    protected World box2dWorld;
    private Box2DDebugRenderer debugRenderer;
    private RayHandler rayHandler;
    protected ReelSprites reelSprites;
    protected ShapeRenderer shapeRenderer;
    protected AudioManager audioManager;
    protected MessageManager messageManager;
    private int currentReel = 0;

    public PlayScreen(SlotPuzzleGame game, LevelDoor levelDoor, MapTile mapTile) {
        this.game = game;
        this.levelDoor = levelDoor;
        this.mapTileLevel = mapTile;
        createPlayScreen();
        playStateMachine.getStateMachine().changeState(PlayState.INTRO_SPINNING_SEQUENCE);
    }

    protected void createPlayScreen() {
        initialisePlayFiniteStateMachine();
        playState = PlayStates.INITIALISING;
        initialiseWorld();
        initialiseDependencies();
        setupPlayScreen();
        messageManager = setUpMessageManager();
        createReelIntroSequence();
    }

    protected void initialisePlayFiniteStateMachine() {
        playStateMachine = new PlayStateMachine();
        playStateMachine.setConcretePlay(this);
        playStateMachine.getStateMachine().changeState(PlayState.INITIALISING);
    }

    protected void initialiseDependencies() {
        initialiseScreen();
        initialiseTweenEngine();
        getAssets(game.annotationAssetManager);
        createSprites();
        audioManager = new AudioManager(game.annotationAssetManager);
    }

    protected MessageManager setUpMessageManager() {
        MessageManager messageManager = MessageManager.getInstance();
        messageManager.addListeners(
            audioManager,
            PlayAudio.index,
            StopAudio.index,
            PauseAudio.index);
        return messageManager;
    }

    protected void setupPlayScreen() {
        loadLevel();
        initialisePlayScreen();
    }

    protected void initialiseWorld() {
        box2dWorld = new World(new Vector2(0, -9.8f), true);
        debugRenderer = new Box2DDebugRenderer();
        rayHandler = new RayHandler(box2dWorld);
        RayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.25f, 0.25f, 0.25f, 0.25f);
    }

    private void initialisePlayScreen() {
        random = new Random();
        renderer = new OrthogonalTiledMapRenderer(tiledMapLevel);
        displaySpinHelp = false;
        scores = new Array<>();
        font = new BitmapFont();
        playScreenPopUps = new PlayScreenPopUps(
            tilesAtlas,
            (int) (float) SlotPuzzleConstants.VIRTUAL_WIDTH,
            (int) (float) SlotPuzzleConstants.VIRTUAL_HEIGHT,
            game.batch,
            game.getTweenManager(),
            levelDoor);
        playScreenPopUps.initialise();
        shapeRenderer = new ShapeRenderer();
    }

    protected void loadLevel() {
        playScreenLevel =  new PlayScreenLevel(
            this, game, levelDoor);
        playScreenLevel.loadLevel(
            mapTileLevel, stoppedSpinningCallback, stoppedFlashingCallback);
        getLevelEntities();
    }

    protected void getLevelEntities() {
        animatedReels = playScreenLevel.getAnimatedReels();
        reelTiles = playScreenLevel.getReelTiles();
        hiddenPattern = playScreenLevel.getHiddenPattern();
        levelLoader = playScreenLevel.getLevelLoader();
    }

    private final LevelCallback stoppedSpinningCallback = new LevelCallback() {
        @Override
        public void onEvent (ReelTile source) {
            playSound(AssetsAnnotation.SOUND_REEL_STOPPED);
            reelsSpinning--;
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
                if (reelsSpinning < 1) {
                    if (levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE))
                        if (isHiddenPatternRevealed(reelTiles))
                            iWonTheLevel();
                    if (levelDoor.getLevelType().equals(LevelCreator.PLAYING_CARD_LEVEL_TYPE))
                        if (isHiddenPlayingCardsRevealed(reelTiles))
                            iWonTheLevel();
                }
            }
        }
    };

    private final LevelCallback stoppedFlashingCallback = new LevelCallback() {
        @Override
        public void onEvent(ReelTile source) {
            if ((levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE)) ||
                (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))) {
                if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY)
                    if (testForAnyLonelyReels(reelTiles)) {
                        win = false;
                        if (playScreenLevel.getHud().getLives() > 0) {
                            playState = PlayStates.LEVEL_LOST;
                            playScreenPopUps.setLevelLostSpritePositions();
                            playScreenPopUps.getLevelLostPopUp().showLevelPopUp(null);
                        } else
                            gameOver = true;
                    }
                reelScoreAnimation(source);
                deleteReelAnimation(source);
            }
        }
    };

    private void initialiseScreen() {
        viewport = new FitViewport(SlotPuzzleConstants.VIRTUAL_WIDTH, SlotPuzzleConstants.VIRTUAL_HEIGHT, camera);
        stage = new Stage(viewport, game.batch);

        lightViewport = new FitViewport(
            (float) VIRTUAL_WIDTH / PIXELS_PER_METER,
            (float) VIRTUAL_HEIGHT / PIXELS_PER_METER);
        lightViewport.
            getCamera().
            position.
            set( (float) VIRTUAL_WIDTH / PIXELS_PER_METER * 0.5f,
                (float) VIRTUAL_HEIGHT / PIXELS_PER_METER * 0.5f,
                0);
        lightViewport.getCamera().update();
        lightViewport.update(VIRTUAL_WIDTH, VIRTUAL_HEIGHT);
    }

    private void initialiseTweenEngine() {
        SlotPuzzleTween.setWaypointsLimit(10);
        SlotPuzzleTween.setCombinedAttributesLimit(3);
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(ScoreSprite.class, new ScoreAccessor());
    }

    private void getAssets(AnnotationAssetManager annotationAssetManager) {
        getAtlasAssets(annotationAssetManager);
        getSoundAssets(annotationAssetManager);
        getLevelAssets(annotationAssetManager);
    }

    private void getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        tiledMapLevel = annotationAssetManager.get(
            "levels/level " + (this.levelDoor.getId() + 1) + " - 40x40.tmx");
    }

    private void getSoundAssets(AnnotationAssetManager annotationAssetManager) {
        chaChingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_CHA_CHING);
        pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        reelStoppedSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    private void getAtlasAssets(AnnotationAssetManager annotationAssetManager) {
        tilesAtlas = annotationAssetManager.get(AssetsAnnotation.TILES);
    }

    private void createSprites() {
        reelSprites = new ReelSprites(game.annotationAssetManager);
        sprites = reelSprites.getSprites();
    }

    private void createReelIntroSequence() {
        createStartReelTimer();
        playState = PlayStates.INTRO_SEQUENCE;
        PlayScreenIntroSequence playScreenIntroSequence = new PlayScreenIntroSequence(reelTiles, game.getTweenManager());
        playScreenIntroSequence.createReelIntroSequence(introSequenceCallback);
    }

    private void createStartReelTimer() {
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                               startAReel();
                           }
                       }
            , 0.0f
            , 0.02f
            , reelTiles.size
        );
    }

    private void startAReel() {
        if (currentReel < reelTiles.size) {
            animatedReels.get(currentReel).setupSpinning();
            reelTiles.get(currentReel++).setSpinning(true);
        }
    }

    protected TweenCallback introSequenceCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            delegateIntroSequenceCallback(type, (ReelTile) source.getUserData());
        }
    };

    protected void delegateIntroSequenceCallback(int type, ReelTile reelTile) {
        if (type == TweenCallback.END) {
            playState = PlayStates.INTRO_POPUP;
            playScreenPopUps.setPopUpSpritePositions();
            playScreenPopUps.getLevelPopUp().showLevelPopUp(null);
        }
    }

    private boolean isHiddenPatternRevealed(Array<ReelTile> reelTiles) {
        TupleValueIndex[][] matchGrid = playScreenLevel.getFlashSlots().flashSlots(reelTiles);
        return
            hiddenPattern.isHiddenPatternRevealed(
                matchGrid,
                reelTiles,
                SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                SlotPuzzleConstants.GAME_LEVEL_HEIGHT);
    }

    private boolean isHiddenPlayingCardsRevealed(Array<ReelTile> reelTiles) {
        TupleValueIndex[][] matchGrid = playScreenLevel.getFlashSlots().flashSlots(reelTiles);
        return
            hiddenPattern.isHiddenPatternRevealed(
                matchGrid,
                reelTiles,
                SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                SlotPuzzleConstants.GAME_LEVEL_HEIGHT);
    }

    private boolean testForAnyLonelyReels(Array<ReelTile> levelReel) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid =
            levelLoader.populateMatchGrid(
                levelReel,
                new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT));
        return puzzleGrid.anyLonelyTiles(grid);
    }

    private void deleteReelAnimation(ReelTile source) {
        Timeline.createSequence()
            .beginParallel()
            .delay(random.nextFloat() * 2.0f)
            .push(SlotPuzzleTween.to(
                source,
                SpriteAccessor.SCALE_XY,
                0.5f).target(6, 6).ease(Quad.IN))
            .push(SlotPuzzleTween.to(
                source,
                SpriteAccessor.OPACITY,
                0.5f).target(0).ease(Quad.IN))
            .end()
            .setUserData(source)
            .setCallback(deleteReelCallback)
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .start(game.getTweenManager());
    }

    private final TweenCallback deleteReelCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            if (type == TweenCallback.COMPLETE) {
                processDeleteReel(source);
            }
        }
    };

    private void processDeleteReel(BaseTween<?> source) {
        ReelTile reel = (ReelTile) source.getUserData();
        playScreenLevel.getHud().addScore((reel.getEndReel() + 1) * reel.getScore());
        playSound(AssetsAnnotation.SOUND_REEL_STOPPED);
        playSound(AssetsAnnotation.SOUND_CHA_CHING);
        reel.deleteReelTile();
        playScreenLevel.getFlashSlots().deleteAReel();
        if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
            if (levelDoor.getLevelType().equals(LevelCreator.PLAYING_CARD_LEVEL_TYPE))
                testPlayingCardLevelWon();
            if (levelDoor.getLevelType().equals(LevelCreator.HIDDEN_PATTERN_LEVEL_TYPE))
                testForHiddenPatternLevelWon();
        }
    }

    protected void reelScoreAnimation(ReelTile source) {
        ScoreSprite score = new ScoreSprite(
            source.getX(),
            source.getY(),
            (source.getEndReel() + 1) * source.getScore());
        scores.add(score);
        Timeline.createSequence()
            .beginParallel()
            .delay(random.nextFloat()*2.0f)
            .push(SlotPuzzleTween.to(
                    score,
                    ScoreAccessor.POS_XY,
                    2.5f)
                .targetRelative(
                    random.nextInt(20), random.nextInt(160)).ease(Quad.IN))
            .push(SlotPuzzleTween.to(
                    score,
                    ScoreAccessor.SCALE_XY,
                    2.5f)
                .target(2.0f, 2.0f).ease(Quad.IN))
            .end()
            .setUserData(score)
            .setCallback(deleteScoreCallback)
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .start(game.getTweenManager());
    }

    private final TweenCallback deleteScoreCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            if (type == TweenCallback.COMPLETE) {
                ScoreSprite score = (ScoreSprite) source.getUserData();
                scores.removeValue(score, false);
            }
        }
    };

    protected boolean isOver(Sprite sprite, float x, float y) {
        return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
            && sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
    }

    private void processIsTileClicked(float touchX, float touchY) {
        int c = PuzzleGridTypeReelTile.getColumnFromLevel(touchX);
        int r = PuzzleGridTypeReelTile.getRowFromLevel(touchY, SlotPuzzleConstants.GAME_LEVEL_HEIGHT);
        if ((r >= 0) &
            (r <= SlotPuzzleConstants.GAME_LEVEL_HEIGHT) &
            (c >= 0) &
            (c <= SlotPuzzleConstants.GAME_LEVEL_WIDTH)) {
            TupleValueIndex[][] grid = levelLoader.populateMatchGrid(reelTiles,
                new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT));
            ReelTile reelTile = reelTiles.get(grid[r][c].index);
            AnimatedReel animatedReel = animatedReels.get(grid[r][c].index);
            if (!reelTile.isReelTileDeleted()) {
                if (reelTile.isSpinning()) {
                    if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE)
                        processReelTouchedWhileSpinning(
                            reelTile, reelTile.getCurrentReel(), reelTile.getCurrentReel());
                } else
                if (!reelTile.getFlashTween())
                    startReelSpinning(reelTile, animatedReel);
            }
        } else
            Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
    }

    private void testPlayingCardLevelWon() {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid =
            levelLoader.populateMatchGrid(
                reelTiles,
                new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT));
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPattern.isHiddenPatternRevealed(
            matchGrid, reelTiles, SlotPuzzleConstants.GAME_LEVEL_WIDTH, SlotPuzzleConstants.GAME_LEVEL_HEIGHT))
            iWonTheLevel();
    }

    private void testForHiddenPatternLevelWon() {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid =
            levelLoader.populateMatchGrid(
                reelTiles,
                new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT));
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPattern.isHiddenPatternRevealed(
            matchGrid,
            reelTiles,
            SlotPuzzleConstants.GAME_LEVEL_WIDTH, SlotPuzzleConstants.GAME_LEVEL_HEIGHT))
            iWonTheLevel();
    }

    private void iWonTheLevel() {
        gameOver = true;
        win = true;
        playState = PlayStates.WON_LEVEL;
        mapTileLevel.getLevel().setLevelCompleted();
        mapTileLevel.getLevel().setScore(playScreenLevel.getHud().getScore());
        playScreenPopUps.setLevelWonSpritePositions();
        playScreenPopUps.getLevelWonPopUp().showLevelPopUp(null);
    }

    protected TweenCallback hideLevelPopUpCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            if (type == TweenCallback.END) {
                playState = PlayStates.PLAYING;
                playScreenLevel.getHud().resetWorldTime(LEVEL_TIME_LENGTH_IN_SECONDS);
                playScreenLevel.getHud().startWorldTimer();
                if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE))
                    isHiddenPatternRevealed(reelTiles);
                if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
                    isHiddenPatternRevealed(reelTiles);
            }
        }
    };

    protected TweenCallback levelWonCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            dispose();
            ((WorldScreen)game.getWorldScreen()).worldScreenCallBack(mapTileLevel);
            game.setScreen(game.getWorldScreen());
        }
    };

    private void startReelSpinning(ReelTile reel, AnimatedReel animatedReel) {
        reel.setEndReel(random.nextInt(sprites.length - 1));
        reel.startSpinning();
        reelsSpinning++;
        reel.setSy(0);
        animatedReel.reinitialise();
        playScreenLevel.getHud().addScore(-1);
        playSound(AssetsAnnotation.SOUND_PULL_LEVER);
    }

    protected void processReelTouchedWhileSpinning(ReelTile reel, int currentReel, int spinHelpSprite) {
        reel.setEndReel(currentReel);
        displaySpinHelp = true;
        displaySpinHelpSprite = spinHelpSprite;
        playScreenLevel.getHud().addScore(-1);
        playSound(AssetsAnnotation.SOUND_PULL_LEVER);
        playSound(AssetsAnnotation.SOUND_REEL_SPINNING);
    }

    protected TweenCallback levelOverCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            if (type == TweenCallback.END) {
                delegateLevelOverCallback();
            }
        }
    };

    private void delegateLevelOverCallback() {
        game.getTweenManager().killAll();
        playScreenLevel.getHud().resetScore();
        playScreenLevel.getHud().loseLife();
        playScreenLevel.getHud().resetWorldTime(LEVEL_TIME_LENGTH_IN_SECONDS);
        playScreenLevel.getHud().stopWorldTimer();
        renderer = new OrthogonalTiledMapRenderer(tiledMapLevel);
        displaySpinHelp = false;
        inRestartLevel = false;
        currentReel = 0;
        loadLevel();
        createReelIntroSequence();
        playStateMachine.getStateMachine().changeState(PlayState.INTRO_SPINNING_SEQUENCE);
    }

    @Override
    public int getNumberOfReelsFalling() {
        return 0;
    }

    @Override
    public int getNumberOfReelsSpinning() {
        return reelsSpinning;
    }

    @Override
    public int getNumberOfReelsMatched() {
        return 0;
    }

    @Override
    public int getNumberOfReelsFlashing() {
        return
            playScreenLevel.getFlashSlots().getNumberOfReelsFlashing();
    }

    @Override
    public int getNumberOfReelsToDelete() {
        return
            playScreenLevel.getFlashSlots().getNumberOfReelsToDelete();
    }

    @Override
    public boolean areReelsFalling() {
        return false;
    }

    @Override
    public boolean areReelsSpinning() {
        return reelsSpinning > 0;
    }

    @Override
    public boolean areReelsFlashing() {
        return
            playScreenLevel.getFlashSlots().areReelsFlashing();
    }

    @Override
    public boolean areReelsStartedFlashing() {
        return
            playScreenLevel.getFlashSlots().areReelsStartedFlashing();
    }

    @Override
    public boolean isFinishedMatchingSlots() {
        return
            playScreenLevel.getFlashSlots().isFinishedMatchingSlots();
    }

    @Override
    public boolean areReelsDeleted() {
        return
            levelDoor.getLevelType().equals(LevelCreator.MINI_SLOT_MACHINE_LEVEL_TYPE) ?
                false : playScreenLevel.getFlashSlots().areReelsDeleted();
    }

    @Override
    public void setReelsAreFlashing(boolean reelsAreFlashing) {
//        playScreenLevel.getFlashSlots.setReelsAreFlashing(reelsAreFlashing);
    }

    public void updateState(float delta) {
    }

    protected void update(float delta) {
        playStateMachine.update();
        game.getTweenManager().update(delta);
        renderer.setView(camera);
        updateAnimatedReels(delta);
        playScreenLevel.getHud().update(delta);
        if (isOutOfTime())
            weAreOutOfTime();
        playScreenLevel.getFrameRate().update();
        checkForGameOverCondition();
    }

    protected boolean isOutOfTime() {
        return playScreenLevel.getHud().getWorldTime() == 0 && playState != PlayStates.BONUS_LEVEL_ENDED;
    }

    protected void checkForGameOverCondition() {
        if ((gameOver) & (!win) & (playScreenLevel.getHud().getLives() == 0)) {
            dispose();
            game.setScreen(new EndOfGameScreen(game));
        }
    }

    protected void weAreOutOfTime() {
        if ((playScreenLevel.getHud().getLives() > 0) & (!inRestartLevel)) {
            inRestartLevel = true;
            playState = PlayStates.LEVEL_LOST;
            playScreenPopUps.setLevelLostSpritePositions();
            playScreenPopUps.getLevelLostPopUp().showLevelPopUp(null);
        } else
            gameOver = true;
    }

    protected void updateAnimatedReels(float delta) {
        for (AnimatedReel animatedReel : animatedReels)
            animatedReel.update(delta);
    }

    @Override
    public void start() {
    }

    @Override
    public boolean isStopped() {
        return false;
    }

    @Override
    public void render(float delta) {
        if (show) {
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
            if (isLoaded)
                renderGame(delta);
            else
                isLoaded = isAssetsLoaded();
        }
    }

    protected void renderGame(float delta) {
        update(delta);
        handleInput();
        renderer.render();
        renderMainGameElements();
        drawCurrentPlayState(delta);
        renderHud();
        stage.draw();
        playScreenLevel.getFrameRate().render();
    }

    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            int touchX = Gdx.input.getX();
            int touchY = Gdx.input.getY();
            Vector3 unProjectTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unProjectTouch);
            switch (playState) {
                case INTRO_POPUP:
                    if (isOver(playScreenPopUps.getLevelPopUpSprites().get(0),
                        unProjectTouch.x,
                        unProjectTouch.y))
                        playScreenPopUps.getLevelPopUp().hideLevelPopUp(hideLevelPopUpCallback);
                    break;
                case LEVEL_LOST:
                    Gdx.app.debug(SLOTPUZZLE_SCREEN, "Lost Level");
                    if (isOver(playScreenPopUps.getLevelLostSprites().get(0), unProjectTouch.x, unProjectTouch.y))
                        playScreenPopUps.getLevelLostPopUp().hideLevelPopUp(levelOverCallback);
                    break;
                case WON_LEVEL:
                    Gdx.app.debug(SLOTPUZZLE_SCREEN, "Won Level");
                    if(isOver(playScreenPopUps.getLevelWonSprites().get(0), unProjectTouch.x, unProjectTouch.y))
                        playScreenPopUps.getLevelWonPopUp().hideLevelPopUp(levelWonCallback);
                    break;
                default: break;
            }
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY) {
                Gdx.app.debug(SLOTPUZZLE_SCREEN, "Play");
                if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE))
                    processIsTileClicked(unProjectTouch.x, unProjectTouch.y);
                if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
                    processIsTileClicked(unProjectTouch.x, unProjectTouch.y);
            }
        }
    }

    private boolean isAssetsLoaded() {
        if (game.annotationAssetManager.getProgress() < 1) {
            game.annotationAssetManager.update();
            return false;
        } else
            return true;
    }

    protected void renderMainGameElements() {
        game.batch.begin();
        renderHiddenPattern();
        renderAnimatedReels();
        renderScore();
        renderSpinHelper();
        game.batch.end();
        game.batch.begin();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        renderAnimatedReelsFlash();
        game.batch.end();
        renderWorld();
        renderRayHandler();
    }

    protected void renderHud() {
        game.batch.setProjectionMatrix(playScreenLevel.getHud().stage.getCamera().combined);
        playScreenLevel.getHud().stage.draw();
    }

    protected void renderHiddenPattern() {
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            drawPlayingCards(game.batch);
    }

    protected void renderWorld() {
        debugRenderer.render(box2dWorld, lightViewport.getCamera().combined);
    }

    protected void renderRayHandler() {
        rayHandler.setCombinedMatrix((OrthographicCamera) lightViewport.getCamera());
        rayHandler.updateAndRender();
    }

    protected void renderAnimatedReels() {
        for (AnimatedReel animatedReel : animatedReels)
            if (!animatedReel.getReel().isReelTileDeleted())
                animatedReel.draw(game.batch);
    }

    protected void renderAnimatedReelsFlash() {
        for (AnimatedReel animatedReel : animatedReels)
            if (!animatedReel.getReel().isReelTileDeleted())
                if (animatedReel.getReel().getFlashState() == ReelTile.FlashState.FLASH_ON)
                    animatedReel.draw(shapeRenderer);
    }


    protected void renderSpinHelper() {
        if (displaySpinHelp)
            reelSprites.getSprites()[displaySpinHelpSprite].draw(game.batch);
    }

    protected void renderScore() {
        for (ScoreSprite score : scores)
            score.render(game.batch);
    }

    protected void drawCurrentPlayState(float delta) {
        game.batch.setProjectionMatrix(camera.combined);
        switch (playState) {
            case INTRO_POPUP:
                playScreenPopUps.getLevelPopUp().draw(game.batch);
                playScreenPopUps.getLevelPopUp().drawSpeechBubble(game.batch, delta);
                break;
            case LEVEL_LOST:
                playScreenPopUps.getLevelLostPopUp().draw(game.batch);
                playScreenPopUps.getLevelLostPopUp().drawSpeechBubble(game.batch, delta);
                break;
            case WON_LEVEL:
                playScreenPopUps.getLevelWonPopUp().draw(game.batch);
                playScreenPopUps.getLevelWonPopUp().drawSpeechBubble(game.batch, delta);
                break;
            case BONUS_LEVEL_ENDED:
                playScreenPopUps.getLevelBonusCompletedPopUp().draw(game.batch);
                playScreenPopUps.getLevelBonusCompletedPopUp().drawSpeechBubble(game.batch, delta);
            default:
                break;
        }
    }

    @Override
    public void show() {
        show = true;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "show() called.");
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width,  height);
    }

    @Override
    public void pause() {
        show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "pause() called.");
    }

    @Override
    public void resume() {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "resume() called.");
    }

    @Override
    public void hide() {
        show = false;
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "hide() called.");
    }

    @Override
    public void dispose() {
        stage.dispose();
        font.dispose();
    }

    private void drawPlayingCards(SpriteBatch spriteBatch) {
        if (hiddenPattern instanceof HiddenPlayingCard)
            for (Card card : ((HiddenPlayingCard) hiddenPattern).getCards())
                card.draw(spriteBatch);
    }

    @Override
    public AnnotationAssetManager getAnnotationAssetManager() {
        return game.annotationAssetManager;
    }

    @Override
    public ReelSprites getReelSprites() {
        return reelSprites;
    }

    @Override
    public Texture getSlotReelScrollTexture() {
        return playScreenLevel.getSlotReelScrollTexture();
    }

    @Override
    public TweenManager getTweenManager() {
        return game.getTweenManager();
    }

    @Override
    public TextureAtlas getSlotHandleAtlas() {
        return game.annotationAssetManager.get(AssetsAnnotation.SLOT_HANDLE);
    }

    @Override
    public TileMapToWorldConvert getTileMapToWorldConvert() {
        return null;
    }

    private void playSound(String sound) {
        messageManager.dispatchMessage(PlayAudio.index, sound);
    }
}
