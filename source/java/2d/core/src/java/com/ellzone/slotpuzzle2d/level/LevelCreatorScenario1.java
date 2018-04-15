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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeWithLevelCreator;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
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
import com.ellzone.slotpuzzle2d.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Sine;

public class LevelCreatorScenario1 {
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final String REELS_LAYER_NAME = "Reels";

    private LevelDoor levelDoor;
    private TiledMap level;
    private HiddenPlayingCard hiddenPlayingCard;
    private Array<Integer> hiddenPlayingCards;
    private TweenManager tweenManager;
    private Timeline reelFlashSeq;
    private AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> reelTiles;
    private AnnotationAssetManager annotationAssetManager;
    private TextureAtlas carddeckAtlas;
    private PhysicsManagerCustomBodies physics;
    private int levelWidth,
                levelHeight,
                reelsSpinning,
                reelsFlashing;
    private PlayScreen.PlayStates playState;
    private boolean win = false, gameOver = false;
    private Array<Score> scores;
    private boolean hitSinkBottom = false, dropReplacementReelBoxes = false;
    private Array<Body> reelBoxes;
    private Array<Body> reelBoxesCollided;
    public Array<Integer> replacementReelBoxes;
    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private int numberOfReelBoxesToReplace, numberOfReelBoxesToDelete;
    boolean matchedReels = false;
    boolean reelsAboveHaveFallen = false;
    //int scenario1Reels[] = {4, 6, 4, 3, 0, 0, 6, 0, 0, 2, 3, 4};
    int scenario1Reels[] = {4, 6, 2, 5, 0, 1, 3, 1, 0, 4, 3, 4};
    Array<TupleValueIndex> reelsToFall;

    public LevelCreatorScenario1(LevelDoor levelDoor, TiledMap level, AnnotationAssetManager annotationAssetManager, TextureAtlas carddeckAtlas, TweenManager tweenManager, PhysicsManagerCustomBodies physics, int levelWidth, int levelHeight, PlayScreen.PlayStates playState) {
        this.levelDoor = levelDoor;
        this.level = level;
        this.tweenManager = tweenManager;
        this.annotationAssetManager = annotationAssetManager;
        this.carddeckAtlas = carddeckAtlas;
        this.physics = physics;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;
        this.playState = playState;
        this.puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        this.reelBoxes = new Array<Body>();
        this.replacementReelBoxes = new Array<Integer>();
        this.animatedReelHelper = new AnimatedReelHelper(this.annotationAssetManager, this.tweenManager, level.getLayers().get(REELS_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).size);
        this.reelTiles = animatedReelHelper.getReelTiles();
        this.reelTiles = createLevel(this.levelDoor, this.level, this.reelTiles, this.levelWidth, this.levelHeight);
        this.reelsSpinning = reelBoxes.size - 1;
        this.reelsFlashing = 0;
        this.hitSinkBottom = false;
        this.scores = new Array<Score>();
        reelsToFall = new Array<TupleValueIndex>();
    }

    private Array<ReelTile> createLevel(LevelDoor levelDoor, TiledMap level, Array<ReelTile> reelTiles, int levelWidth, int levelHeight) {
        if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
            hiddenPlayingCard = new HiddenPlayingCard(level, carddeckAtlas);
        }
        reelTiles = populateLevel(level, reelTiles, levelWidth, levelHeight);
        reelTiles = checkLevel(reelTiles, levelWidth, levelHeight);
        reelTiles = adjustForAnyLonelyReels(reelTiles, levelWidth, levelHeight);
        for (ReelTile reelTile : reelTiles) {
            reelTile.setEndReel(scenario1Reels[reelTile.getIndex()]);
        }
        return reelTiles;
    }

    /* This method needs to throw an exception if its to be of use
     */
    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel, int levelWidth, int levelHeight) {
        ReelTileGridValue[][] grid = puzzleGridTypeReelTile.populateMatchGrid(reelLevel, levelWidth, levelHeight);
        int arraySizeR = grid.length;
        int arraySizeC = grid[0].length;

        for (int r = 0; r < arraySizeR; r++) {
            for (int c = 0; c < arraySizeC; c++) {
                if (grid[r][c] == null) {
                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "Found null grid tile. r=" + r + " c= " + c + ". I will therefore create a deleted entry for the tile.");
                    grid[r][c] = new ReelTileGridValue(r, c, -1, -1);
                }
            }
        }
        return reelLevel;
    }

    Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid = puzzleGridTypeReelTile.populateMatchGrid(levelReel, levelWidth, levelHeight);
        Array<TupleValueIndex> lonelyTiles = puzzleGrid.getLonelyTiles(grid);
        for (TupleValueIndex lonelyTile : lonelyTiles) {
            adjustAnyLonelyTileAtEdge(levelReel, grid, lonelyTile);
        }
        return levelReel;
    }

    private void adjustAnyLonelyTileAtEdge(Array<ReelTile> levelReel, TupleValueIndex[][] grid, TupleValueIndex lonelyTile) {
        if (lonelyTile.r == 0) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r + 1][lonelyTile.c].index).getEndReel());
        } else if (lonelyTile.c == 0) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c + 1].index).getEndReel());
        } else if (lonelyTile.r == levelHeight) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r - 1][lonelyTile.c].index).getEndReel());
        } else if (lonelyTile.c == levelWidth) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c - 1].index).getEndReel());
        } else {
            adjustAnyLoneyAdjacentTile(levelReel, grid, lonelyTile);
        }
    }

    private void adjustAnyLoneyAdjacentTile(Array<ReelTile> levelReel, TupleValueIndex[][] grid, TupleValueIndex lonelyTile) {
        if ((grid[lonelyTile.r + 1][lonelyTile.c] != null) && (grid[lonelyTile.r + 1][lonelyTile.c].value != -1)) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r + 1][lonelyTile.c].index).getEndReel());
        } else if ((grid[lonelyTile.r - 1][lonelyTile.c] != null) && (grid[lonelyTile.r - 1][lonelyTile.c].value != -1)) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r - 1][lonelyTile.c].index).getEndReel());
        } else if ((grid[lonelyTile.r][lonelyTile.c + 1] != null) && (grid[lonelyTile.r][lonelyTile.c + 1].value != -1)) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c + 1].index).getEndReel());
        } else if ((grid[lonelyTile.r][lonelyTile.c - 1] != null) && (grid[lonelyTile.r][lonelyTile.c - 1].value != -1)) {
            levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c - 1].index).getEndReel());
        }
    }

    private Array<ReelTile> populateLevel(TiledMap level, Array<ReelTile> reelTiles, int levelWidth, int levelHeight) {
        int index = 0;
        for (MapObject mapObject : level.getLayers().get(REELS_LAYER_NAME).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = getColumnFromLevel(mapRectangle.getX());
            int r = getRowFromLevel(mapRectangle.getY(), levelHeight);

            if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                addReel(mapRectangle, reelTiles, index);
                index++;
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r=" + r + " c=" + c + ". There it won't be added to the level! Sort it out in a level editor.");
            }
        }
        return reelTiles;
    }


    public ReelTileGridValue[][] populateMatchGrid(Array<ReelTile> reelLevel, int gridWidth, int gridHeight) {
        return puzzleGridTypeReelTile.populateMatchGrid(reelLevel, gridWidth, gridHeight);
    }

    public void printMatchGrid(Array<ReelTile> reelLevel, int gridWidth, int gridHeight) {
        PuzzleGridTypeReelTile.printGrid(populateMatchGrid(reelLevel, gridWidth, gridHeight));
    }

    private int getRowFromLevel(float y, int levelHeight) {
        int row = (int) (y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
        row = levelHeight - 1 - row;
        return row;
    }

    private int getColumnFromLevel(float x) {
        int column = (int) (x  - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
        return column;
    }

    private void addReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, int index) {
        ReelTile reelTile = setUpAddedReel(mapRectangle, reelTiles, index);
        setUpRelatedReelTileBody(reelTile);
    }

    private void setUpRelatedReelTileBody(ReelTile reelTile) {
        Body reelTileBody = physics.createBoxBody(BodyDef.BodyType.DynamicBody,
                reelTile.getX() + 20,
                reelTile.getY() + 360,
                19,
                19,
                true);
        reelTileBody.setUserData(reelTile);
        reelBoxes.add(reelTileBody);
    }

    private ReelTile setUpAddedReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, int index) {
        ReelTile reelTile = reelTiles.get(index);
        reelTile.setX(mapRectangle.getX());
        reelTile.setY(mapRectangle.getY());
        reelTile.setDestinationX(mapRectangle.getX());
        reelTile.setDestinationY(mapRectangle.getY());
        reelTile.setIndex(index);
        reelTile.setSx(0);
        setUpReelTileListener(reelTile);
        return reelTile;
    }

    private void setUpReelTileListener(ReelTile reelTile) {
        reelTile.addListener(new ReelTileListener() {
            @Override
            public void actionPerformed(ReelTileEvent event, ReelTile source) {
                if (event instanceof ReelStoppedSpinningEvent) {
                    actionReelStoppedSpinning(event, source);
                }
                if ((event instanceof ReelStoppedFlashingEvent)) {
                    actionReelStoppedFlashing(event, source);
                }
            }
        });
    }

    public void setPlayState(PlayScreen.PlayStates playState) {
        this.playState = playState;
    }

    public PlayScreen.PlayStates getPlayState() {
        return this.playState;
    }

    private void actionReelStoppedSpinning(ReelTileEvent event, ReelTile source) {
        source.stopSpinningSound();
        reelsSpinning--;
        if ((playState == PlayScreen.PlayStates.INTRO_SPINNING) |
            (playState == PlayScreen.PlayStates.REELS_SPINNING) |
            (playState == PlayScreen.PlayStates.PLAYING)) {
            allReelsHaveStoppedSpinning();
        }
    }

    private void allReelsHaveStoppedSpinning() {
        if ((reelsSpinning <= -1) & (hitSinkBottom)) {
            if (levelDoor.levelType.equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
                if (testForHiddenPatternRevealed(reelTiles, levelWidth, levelHeight)) {
                    iWonTheLevel();
                }
            }
            if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
                if (testForHiddenPlayingCardsRevealed(reelTiles, levelWidth, levelHeight)) {
                    iWonTheLevel();
                }
            }
            if (levelDoor.levelType.equals(BONUS_LEVEL_TYPE)) {
                if (testForJackpot(reelTiles, levelWidth, levelHeight)) {
                    iWonABonus();
                }
            }
        } else {
            if (testForJackpot(reelTiles, levelWidth, levelHeight)) {
                iWonABonus();
            }
        }
    }

    private void actionReelStoppedFlashing(ReelTileEvent event, ReelTile reelTile) {
        if ((playState == PlayScreen.PlayStates.INTRO_FLASHING) | (playState != PlayScreen.PlayStates.REELS_FLASHING)) {
            if (reelsFlashing <= 0) {
                // When do I need to testForAnyLonelyReels?
                //
                /*if (testForAnyLonelyReels(reelTiles, this.levelWidth, this.levelHeight)) {
                    win = false;
                    if (Hud.getLives() > 0) {
                        setPlayState(PlayScreen.PlayStates.LEVEL_LOST);
                    } else {
                        gameOver = true;
                    }
                }*/
            }
        }
        reelScoreAnimation(reelTile);
        deleteReelAnimation(reelTile);
    }

    private ReelTileGridValue[][] flashSlots(Array<ReelTile> reelTiles, int levelWidth, int levelHeight) {
        if (playState == PlayScreen.PlayStates.INTRO_SPINNING) {
            playState = PlayScreen.PlayStates.INTRO_FLASHING;
        }
        if (playState == PlayScreen.PlayStates.PLAYING) {
            playState = PlayScreen.PlayStates.REELS_FLASHING;
        }
        ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);

        Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

        matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);

        if (matchedSlots.size > 0) {
            setUpToFlashMatchedReels(reelTiles, matchedSlots, duplicateMatchedSlots);
        } else {
            matchedReels = false;
        }
        return puzzleGrid;
    }

    private void setUpToFlashMatchedReels(Array<ReelTile> reelTiles, Array<ReelTileGridValue> matchedSlots, Array<ReelTileGridValue> duplicateMatchedSlots) {
        matchedSlots.reverse();
        numberOfReelBoxesToReplace = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;
        numberOfReelBoxesToDelete = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;

        for (TupleValueIndex matchedSlot : matchedSlots) {
            reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);
        }

        flashMatchedSlots(matchedSlots, puzzleGridTypeReelTile);
        matchedReels = true;
    }

    private void flashMatchedSlotsBatch(Array<ReelTileGridValue> matchedSlots, float pushPause) {
        int index;
        for (int i = 0; i < matchedSlots.size; i++) {
            index = matchedSlots.get(i).getIndex();
            if (index >= 0) {
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
        while (matchedSlots.size > 0) {
            batchIndex = matchSlotIndex;
            for (int batchCount = batchIndex; batchCount < batchIndex + 3; batchCount++) {
                if (batchCount < matchedSlots.size) {
                    batchPosition = matchSlotsBatch.size;
                    matchSlotsBatch = puzzleGridTypeReelTile.depthFirstSearchAddToMatchSlotBatch(matchedSlots.get(0), matchSlotsBatch);

                    for (int deleteIndex = batchPosition; deleteIndex < matchSlotsBatch.size; deleteIndex++) {
                        matchedSlots.removeValue(matchSlotsBatch.get(deleteIndex), true);
                    }
                }
            }
            if (matchSlotsBatch.size == 0) {
                break;
            }
            flashMatchedSlotsBatch(matchSlotsBatch, pushPause);
            pushPause += 2.0f;
            matchSlotsBatch.clear();
        }
    }

    private boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelWidth, levelHeight);
        return hiddenPatternRevealed(matchGrid);
    }

    private boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelWidth, levelHeight);
        return hiddenPlayingCardsRevealed(matchGrid);
    }

    private boolean hiddenPlayingCardsRevealed(TupleValueIndex[][] grid) {
        boolean hiddenPlayingCardsRevealed = true;
        for (Integer hiddenPlayingCard : hiddenPlayingCards) {
            MapObject mapObject = level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(hiddenPlayingCard.intValue());
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            for (int ro = (int) (mapRectangle.getX()); ro < (int) (mapRectangle.getX() + mapRectangle.getWidth()); ro += PlayScreen.TILE_WIDTH) {
                for (int co = (int) (mapRectangle.getY()); co < (int) (mapRectangle.getY() + mapRectangle.getHeight()); co += PlayScreen.TILE_HEIGHT) {
                    int c = getColumnFromLevel(ro);
                    int r = getRowFromLevel(co, levelHeight);

                    if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                        if (grid[r][c] != null) {
                            if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted()) {
                                hiddenPlayingCardsRevealed = false;
                            }
                        }
                    } else {
                        Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r=" + r + "c=" + c);
                    }
                }
            }
        }
        return hiddenPlayingCardsRevealed;
    }

    private boolean hiddenPatternRevealed(TupleValueIndex[][] grid) {
        boolean hiddenPattern = true;
        for (MapObject mapObject : level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = getColumnFromLevel(mapRectangle.getX());
            int r = getRowFromLevel(mapRectangle.getY(), levelHeight);
            if ((r >= 0) & (r <= levelHeight) & (c >= 0) & (c <= levelWidth)) {
                if (grid[r][c] != null) {
                    if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted()) {
                        hiddenPattern = false;
                    }
                }
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r=" + r + "c=" + c);
            }
        }
        return hiddenPattern;
    }

    boolean testForAnyLonelyReels(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid = puzzleGridTypeReelTile.populateMatchGrid(levelReel, levelWidth, levelHeight);
        PuzzleGridType.printGrid(grid);
        return puzzleGrid.anyLonelyTiles(grid);
    }

    private boolean testForJackpot(Array<ReelTile> levelReel, int levelWidth, int levelHeight) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelWidth, levelHeight);
        return true;
    }

    private void iWonTheLevel() {
        gameOver = true;
        win = true;
        playState = PlayScreen.PlayStates.WON_LEVEL;
        //mapTile.getLevel().setLevelCompleted();
        //mapTile.getLevel().setScore(Hud.getScore());
    }

    private void iWonABonus() {
        System.out.println("iWonABonus!");
    }

    public Array<ReelTile> getReelTiles() {
        return this.reelTiles;
    }

    public Array<Body> getReelBoxes() {
        return this.reelBoxes;
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return this.animatedReelHelper.getAnimatedReels();
    }

    private void reelScoreAnimation(ReelTile source) {
        Score score = new Score(source.getX(), source.getY(), (source.getEndReel() + 1) * source.getScore());
        scores.add(score);
        Timeline.createSequence()
                .beginParallel()
                .push(SlotPuzzleTween.to(score, ScoreAccessor.POS_XY, 2.0f).targetRelative(Random.getInstance().nextInt(20), Random.getInstance().nextInt(160)).ease(Quad.IN))
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
                    int reelTilesIndex = reelTiles.indexOf(reel, true);
                    Hud.addScore((reel.getEndReel() + 1) * reel.getScore());
                    //playSound(reelStoppedSound);
                    //playSound(chaChingSound);

                    reel.deleteReelTile();
                    physics.deleteBody(reelBoxes.get(reelTilesIndex));
                    replacementReelBoxes.add(reelTilesIndex);
                    reelsFlashing--;
                    numberOfReelBoxesToDelete--;
                    if (numberOfReelBoxesToDelete < 0) {
                        if ((playState == PlayScreen.PlayStates.INTRO_FLASHING) | (playState == PlayScreen.PlayStates.REELS_FLASHING)) {
                            findReelsAboveMe();
                            matchedReels = isThereMatchedSlots();
                            if (!matchedReels) {
                                if (replacementReelBoxes.size == 0) {
                                    playState = PlayScreen.PlayStates.PLAYING;
                                } else {
                                    if (reelsToFall.size == 0) {
                                        createReplacementReelBoxes();
                                    }
                                }
                            }
                        }
                    }

                    if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
                        testPlayingCardLevelWon(levelWidth, levelHeight);
                    } else {
                        if (levelDoor.levelType.equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
                            testForHiddenPlatternLevelWon(levelWidth, levelHeight);
                        }
                    }
            }
        }
    };

    private boolean isThereMatchedSlots() {
        ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
        Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

        matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);
        if (matchedSlots.size > 0) {
            matchedSlots.reverse();
            numberOfReelBoxesToReplace = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;
            numberOfReelBoxesToDelete = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;

            for (TupleValueIndex matchedSlot : matchedSlots) {
                reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);
            }
            flashMatchedSlots(matchedSlots, puzzleGridTypeReelTile);
            return true;
        }
        return false;
    }

    private void testPlayingCardLevelWon(int levelWidth, int levelHeight) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPlayingCardsRevealed(matchGrid)) {
            iWonTheLevel();
        }
    }

    private void testForHiddenPlatternLevelWon(int levelWidth, int levelHeight) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPatternRevealed(matchGrid)) {
            iWonTheLevel();
        }
    }

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
        reelsFlashing++;
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

    public void update(float dt) {
       if ((playState == PlayScreen.PlayStates.INTRO_FLASHING) | (playState == PlayScreen.PlayStates.REELS_FLASHING)) {
            if ((reelsAboveHaveFallen) & (reelsToFall.size==0) & (reelsFlashing == 0)) {
                ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
                Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
                Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

                matchedReels = isThereMatchedSlots();
                if (!matchedReels) {
                    if (replacementReelBoxes.size == 0) {
                        playState = PlayScreen.PlayStates.PLAYING;
                    } else {
                        if (reelsToFall.size == 0) {
                            createReplacementReelBoxes();
                        }
                    }
                    reelsAboveHaveFallen = false;
                }
            } else {
                if ((playState == PlayScreen.PlayStates.INTRO_FLASHING) | (playState == PlayScreen.PlayStates.REELS_FLASHING) | playState == PlayScreen.PlayStates.PLAYING) {
                    if ((numberOfReelBoxesToDelete < 0) & (!matchedReels) & (reelsToFall.size == 0) & (replacementReelBoxes.size > 0) & (reelsFlashing == 0)) {
                        matchedReels = isThereMatchedSlots();
                        if (!matchedReels) {
                            if ((numberOfReelBoxesToDelete < 0) & (reelsToFall.size == 0) & (replacementReelBoxes.size > 0) & (reelsToFall.size ==0)) {
                                createReplacementReelBoxes();
                            }
                        }
                    } else {
                        if ((numberOfReelBoxesToDelete < 0) & (!matchedReels) & (reelsToFall.size == 0) & (replacementReelBoxes.size == 0)) {
                            playState = PlayScreen.PlayStates.PLAYING;
                        }
                    }
                }
            }
        }
        if ((playState == PlayScreen.PlayStates.INTRO_SPINNING) | (playState == PlayScreen.PlayStates.REELS_SPINNING)) {
           if ((numberOfReelBoxesToDelete < 0) & (reelsToFall.size == 0) & (replacementReelBoxes.size == 0)) {
               playState = PlayScreen.PlayStates.PLAYING;
           }
        }
        animatedReelHelper.update(dt);
        physics.update(dt);
        updateReelBoxes();
    }

    private void createReplacementReelBoxes() {
        for (Integer reelBoxIndex : replacementReelBoxes) {
            ReelTile reelTile = reelTiles.get(reelBoxIndex.intValue());
            reelTile.unDeleteReelTile();
            reelTile.setScale(1.0f);
            Color reelTileColor = reelTile.getColor();
            reelTileColor.set(reelTileColor.r, reelTileColor.g, reelTileColor.b, 1.0f);
            reelTile.setColor(reelTileColor);
            reelTile.setEndReel(Random.getInstance().nextInt(animatedReelHelper.getReels().getReels().length - 1));
            reelTile.resetReel();
            Body reelTileBody = physics.createBoxBody(BodyDef.BodyType.DynamicBody,
                    reelTile.getDestinationX() + 20,
                    reelTile.getDestinationY() + 360,
                    19,
                    19,
                    true);
            reelTileBody.setUserData(reelTile);
            reelBoxes.set(reelBoxIndex, reelTileBody);
            AnimatedReel animatedReel = animatedReelHelper.getAnimatedReels().get(reelBoxIndex);
            animatedReel.reinitialise();
        }
        reelsSpinning = replacementReelBoxes.size - 1;
        MiniSlotMachineLevelPrototypeWithLevelCreator.numberOfReelsToHitSinkBottom = replacementReelBoxes.size;
        replacementReelBoxes.removeRange(0, replacementReelBoxes.size - 1);
        dropReplacementReelBoxes = false;
        if (playState == PlayScreen.PlayStates.INTRO_FLASHING) {
            playState = PlayScreen.PlayStates.INTRO_SPINNING;
        }
        if (playState == PlayScreen.PlayStates.REELS_FLASHING) {
            playState = PlayScreen.PlayStates.REELS_SPINNING;
        }
    }

    public void setHitSinkBottom(boolean hitSinkBottom) {
        this.hitSinkBottom = hitSinkBottom;
    }

    public void setNumberOfReelsSpinning(int numberOfReelsSpinning) {
        this.reelsSpinning = numberOfReelsSpinning;
    }

    public int getNumberOfReelsSpinning() {
        return reelsSpinning;
    }

    public Array<Score> getScores() {
        return scores;
    }

    public Array<Integer> getReplacementReelBoxes() {
        return replacementReelBoxes;
    }

    private void playSound(Sound sound) {
        if (sound != null) {
            sound.play();
        }
    }

    public int findReel(int destinationX, int destinationY) {
        int findReelIndex = 0;
        while (findReelIndex < reelTiles.size) {
            if ((reelTiles.get(findReelIndex).getDestinationX() == destinationX) &
                    (reelTiles.get(findReelIndex).getDestinationY() == destinationY)) {
                return findReelIndex;
            }
            findReelIndex++;
        }
        return -1;
    }

    private void findReelsAboveMe() {
        TupleValueIndex[][] matchGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelWidth, levelHeight);
        TupleValueIndex[] reelsAboveMe = null;
        reelsToFall = new Array<TupleValueIndex>();
        PuzzleGridType puzzleGridType = new PuzzleGridType();
        for (Integer replacementReelBox : replacementReelBoxes) {
            reelsAboveMe = puzzleGridType.getReelsAboveMe(matchGrid,
                    PuzzleGridTypeReelTile.getRowFromLevel(reelTiles.get(replacementReelBox).getDestinationY(), levelHeight),
                    PuzzleGridTypeReelTile.getColumnFromLevel(reelTiles.get(replacementReelBox).getDestinationX()));
            for (int rami = 0; rami < reelsAboveMe.length; rami++) {
                    if (!reelsToFall.contains(reelsAboveMe[rami], true)) {
                    reelsToFall.add(reelsAboveMe[rami]);
                }
            }
        }
    }

    public Array<TupleValueIndex> getReelsToFall() {
        return reelsToFall;
    }

    public void setReelsToFall(Array<TupleValueIndex> reelsToFall) {
        this.reelsToFall = reelsToFall;
    }

    public void setReelsAboveHaveFallen(boolean reelsAboveHaveFallen) {
        this.reelsAboveHaveFallen = reelsAboveHaveFallen;
    }

    private void updateReelBoxes() {
        if ((reelBoxesCollided != null) && (reelBoxesCollided.size > 0)) {
            for (Body reelBoxCollided : reelBoxesCollided) {
                ReelTile reelTile = (ReelTile) reelBoxCollided.getUserData();
                reelBoxCollided.setTransform((reelTile.getDestinationX() + 20) / 100, (reelTile.getDestinationY() + 20) / 100, 0);
            }
            reelBoxesCollided.removeRange(0, reelBoxesCollided.size - 1);
        }
    }
}
