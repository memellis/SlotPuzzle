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

package com.ellzone.slotpuzzle.level.creator;

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
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.effects.ReelAccessor;
import com.ellzone.slotpuzzle.effects.ScoreAccessor;
import com.ellzone.slotpuzzle.effects.SpriteAccessor;
import com.ellzone.slotpuzzle.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle.level.LevelDoor;
import com.ellzone.slotpuzzle.level.hidden.HiddenPlayingCard;
import com.ellzone.slotpuzzle.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle.scene.Hud;
import com.ellzone.slotpuzzle.screens.PlayScreen;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReelHelper;
import com.ellzone.slotpuzzle.sprites.reel.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle.sprites.reel.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.sprites.reel.ReelTileEvent;
import com.ellzone.slotpuzzle.sprites.reel.ReelTileListener;
import com.ellzone.slotpuzzle.sprites.score.ScoreSprite;
import com.ellzone.slotpuzzle.tweenengine.BaseTween;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import java.text.MessageFormat;

import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Sine;
/*

@startuml

MiniSlotMachineLevelPrototypeWithLevelCreator <-- LevelCreator : uses

class LevelCreator {
-TweenCallback deleteScoreCallback
-TweenCallback reelFlashCallback
-TweenCallback deleteReelCallback
-Array<ReelTile> createLevel(LevelDoor levelDoor, TiledMap level, Array<ReelTile> reelTiles, int levelWidth, int levelHeight)
-Array<ReelTile> checkGrid(Array<ReelTile> reelLevel, int levelWidth, int levelHeight)
-Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel, int levelWidth, int levelHeight)
-Array<ReelTile> populateLevel(TiledMap level, Array<ReelTile> reelTiles, int levelWidth, int levelHeight)
+ReelTileGridValue[][] populateMatchGrid(Array<ReelTile> reelLevel, int gridWidth, int gridHeight)
+printMatchGrid(Array<ReelTile> reelLevel, int gridWidth, int gridHeight)
-int getRowFromLevel(float y, int levelHeight)
-int getColumnFromLevel(float x)
+LevelCreator(LevelDoor levelDoor, TiledMap level, AnnotationAssetManager annotationAssetManager, TextureAtlas carddeckAtlas, TweenManager tweenManager, PhysicsManagerCustomBodies physics, int levelWidth, int levelHeight, PlayScreen.PlayStates playState)
-addReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, int index)
+setPlayState(PlayScreen.PlayStates playState)
+PlayScreen.PlayStates getPlayState()
-actionReelStoppedSpinning(ReelTileEvent event, ReelTile source)
-actionReelStoppedFlashing(ReelTileEvent event, ReelTile reelTile)
-ReelTileGridValue[][] flashSlots(Array<ReelTile> reelTiles, int levelWidth, int levelHeight)
-flashMatchedSlotsBatch(Array<ReelTileGridValue> matchedSlots, float pushPause)
-flashMatchedSlots(Array<ReelTileGridValue> matchedSlots, PuzzleGridTypeReelTile puzzleGridTypeReelTile)
-boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel, int levelWidth, int levelHeight)
-boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> levelReel, int levelWidth, int levelHeight)
-hiddenPatternRevealed(TupleValueIndex[][] grid)
-testForJackpot(Array<ReelTile> levelReel, int levelWidth, int levelHeight)
-iWonTheLevel()
+Array<ReelTile> getReelTiles()
+Array<Body> getReelBoxes()
+Array<AnimatedReel> getAnimatedReels()
-reelScoreAnimation(ReelTile source)
-deleteReelAnimation(ReelTile source)
-testPlayingCardLevelWon(int levelWidth, int levelHeight)
-testForHiddenPlatternLevelWon(int levelWidth, int levelHeight)
-initialiseReelFlash(ReelTile reel, float pushPause)
-delegateReelFlashCallback(int type, BaseTween<?> source)
+update(float dt)
-createReplacementReelBoxes()
-setHitSinkBottom(boolean hitSinkBottom)
+setNumberOfReelsSpinning(int numberOfReelsSpinning)
+int getNumberOfReelsSpinning()
+Array<Score> getScores()
+Array<Integer> getReplacementReelBoxes()
-playSound(Sound sound)
+printReelBoxes(Array<Body> reelBoxes)
+printReelTiles()
+int findReel(int destinationX, int destinationY)
}

@enduml

*/

public class LevelCreator {

    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String BONUS_LEVEL_TYPE = "Bonus";
    public static final String FALLING_REELS_LEVEL_TYPE = "FallingReels";
    public static final String MINI_SLOT_MACHINE_LEVEL_TYPE = "MiniSlotMachine";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final String REELS_LAYER_NAME = "Reels";

    private final LevelDoor levelDoor;
    private final TiledMap level;
    private Array<Integer> hiddenPlayingCards;
    private final TweenManager tweenManager;
    private final AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> reelTiles;
    private final TextureAtlas cardDeckAtlas;
    private final PhysicsManagerCustomBodies physics;
    private int reelsSpinning;
    private final GridSize levelGridSize;
    private PlayStates playState;
    private final Hud hud;
    private final Array<ScoreSprite> scores;
    private boolean hitSinkBottom, dropReplacementReelBoxes;
    private final Array<Body> reelBoxes;
    public Array<Integer> replacementReelBoxes;
    private final PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private int numberOfReelBoxesToDelete;
    boolean matchedReels = false;
    boolean reelsAboveHaveFallen = false;
    private boolean win;
    private boolean gameOver;
    private int numberOfReelsToHitSinkBottom;

    public LevelCreator(LevelDoor levelDoor,
                        TiledMap level,
                        AnnotationAssetManager annotationAssetManager,
                        TextureAtlas cardDeckAtlas,
                        TweenManager tweenManager,
                        PhysicsManagerCustomBodies physics,
                        GridSize levelGridSize,
                        PlayStates playState,
                        Hud hud) {
        this.levelDoor = levelDoor;
        this.level = level;
        this.tweenManager = tweenManager;
        this.cardDeckAtlas = cardDeckAtlas;
        this.physics = physics;
        this.levelGridSize = levelGridSize;
        this.playState = playState;
        this.hud = hud;
        puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        reelBoxes = new Array<>();
        replacementReelBoxes = new Array<>();
        animatedReelHelper = new AnimatedReelHelper(annotationAssetManager, this.tweenManager, level.getLayers().get(REELS_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).size);
        reelTiles = animatedReelHelper.getReelTiles();
        reelTiles = createLevel(
            this.levelDoor,
            this.level,
            this.reelTiles,
            this.levelGridSize);
        reelsSpinning = reelBoxes.size - 1;
        hitSinkBottom = false;
        scores = new Array<>();
    }

    private Array<ReelTile> createLevel(LevelDoor levelDoor,
                                        TiledMap level,
                                        Array<ReelTile> reelTiles,
                                        GridSize levelGridSize) {
        HiddenPlayingCard hiddenPlayingCard;
        if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE))
            hiddenPlayingCard = new HiddenPlayingCard(level, cardDeckAtlas);

        reelTiles = populateLevel(level, reelTiles, levelGridSize);
        reelTiles = checkLevel(reelTiles, levelGridSize);
        reelTiles = adjustForAnyLonelyReels(reelTiles, levelGridSize);
        return reelTiles;
    }

    private Array<ReelTile> checkLevel(Array<ReelTile> reelLevel, GridSize levelGridSize) {
        ReelTileGridValue[][] grid = puzzleGridTypeReelTile.populateMatchGrid(
            reelLevel, levelGridSize);
        int arraySizeR = grid.length;
        int arraySizeC = grid[0].length;

        for(int r = 0; r < arraySizeR; r++) {
            for(int c = 0; c < arraySizeC; c++) {
                if(grid[r][c] == null) {
                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "Found null grid tile. r=" + r + " c= " + c + ". I will therefore create a deleted entry for the tile.");
                    grid[r][c] = new ReelTileGridValue(r, c, -1, -1);
                }
            }
        }
        return reelLevel;
    }

    Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel, GridSize levelGridSize) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid =
            puzzleGridTypeReelTile.populateMatchGrid(levelReel, levelGridSize);
        Array<TupleValueIndex> lonelyTiles = puzzleGrid.getLonelyTiles(grid);
        for (TupleValueIndex lonelyTile : new Array.ArrayIterator<>(lonelyTiles)) {
            if (lonelyTile.r == 0) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r+1][lonelyTile.c].index).getEndReel());
            } else if (lonelyTile.c == 0) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c+1].index).getEndReel());
            } else if (lonelyTile.r == levelGridSize.getHeight()) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r-1][lonelyTile.c].index).getEndReel());
            } else if (lonelyTile.c == levelGridSize.getWidth()) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c-1].index).getEndReel());
            } else if ((grid[lonelyTile.r + 1][lonelyTile.c] != null) && (grid[lonelyTile.r + 1][lonelyTile.c].value != -1)) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r + 1][lonelyTile.c].index).getEndReel());
            } else if ((grid[lonelyTile.r - 1][lonelyTile.c] != null) && (grid[lonelyTile.r - 1][lonelyTile.c].value != -1)) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r - 1][lonelyTile.c].index).getEndReel());
            } else if ((grid[lonelyTile.r][lonelyTile.c + 1] != null) && (grid[lonelyTile.r][lonelyTile.c + 1].value != -1)) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c + 1].index).getEndReel());
            } else if ((grid[lonelyTile.r][lonelyTile.c - 1] != null) && (grid[lonelyTile.r][lonelyTile.c - 1].value != -1)) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c - 1].index).getEndReel());
            }
        }
        return levelReel;
    }

    private Array<ReelTile> populateLevel(TiledMap level,
                                          Array<ReelTile> reelTiles,
                                          GridSize levelGridSize) {
        int index = 0;
        Array<MapObject> mapObjects = level
            .getLayers()
            .get(REELS_LAYER_NAME)
            .getObjects()
            .getByType(MapObject.class);
        for (MapObject mapObject : new Array.ArrayIterator<MapObject>(mapObjects)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = getColumnFromLevel(mapRectangle.getX());
            int r = getRowFromLevel(mapRectangle.getY(), levelGridSize.getHeight());

            if ((r >= 0) &
                (r <= levelGridSize.getHeight()) &
                (c >= 0) &
                (c <= levelGridSize.getWidth())) {
                addReel(mapRectangle, reelTiles, index);
                index++;
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r=" + r + " c=" + c + ". There it won't be added to the level! Sort it out in a level editor.");
            }
        }
        return reelTiles;
    }


    public ReelTileGridValue[][] populateMatchGrid(Array<ReelTile> reelLevel,
                                                   GridSize levelGridSize) {
        return puzzleGridTypeReelTile.populateMatchGrid(reelLevel, levelGridSize);
    }

    public void printMatchGrid(Array<ReelTile> reelLevel, GridSize levelGridSize) {
        PuzzleGridTypeReelTile.printGrid(populateMatchGrid(reelLevel, levelGridSize));
    }

    private int getRowFromLevel(float y, int levelHeight) {
        int row = (int) (y +
            PuzzleGridTypeReelTile.FLOAT_ROUNDING_DELTA_FOR_BOX2D -
            PlayScreen.PUZZLE_GRID_START_Y) / SlotPuzzleConstants.TILE_HEIGHT;
        row = levelHeight - 1 - row;
        return row;
    }

    private int getColumnFromLevel(float x) {
        return (int) (x + PuzzleGridTypeReelTile.FLOAT_ROUNDING_DELTA_FOR_BOX2D - PlayScreen.PUZZLE_GRID_START_X) / SlotPuzzleConstants.TILE_WIDTH;
    }

    private void addReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, int index) {
        ReelTile reelTile = reelTiles.get(index);
        reelTile.setX(mapRectangle.getX());
        reelTile.setY(mapRectangle.getY());
        reelTile.setDestinationX(mapRectangle.getX());
        reelTile.setDestinationY(mapRectangle.getY());
        reelTile.setIndex(index);
        reelTile.setSx(0);
        reelTile.addListener(new ReelTileListener() {
            @Override
            public void actionPerformed(ReelTileEvent event, ReelTile source) {
                if (event instanceof ReelStoppedSpinningEvent) {
                    actionReelStoppedSpinning(source);
                }
                if ((event instanceof ReelStoppedFlashingEvent)) {
                    actionReelStoppedFlashing(source);
                }
            }
        });
        Body reelTileBody = physics.createBoxBody(BodyDef.BodyType.DynamicBody,
            reelTile.getX() + 20,
            reelTile.getY() + 360,
            19,
            19,
            true);
        reelTileBody.setUserData(reelTile);
        reelBoxes.add(reelTileBody);
    }

    public void setPlayState(PlayStates playState) {
        this.playState = playState;
    }

    public PlayStates getPlayState() {
        return playState;
    }

    private void actionReelStoppedSpinning(ReelTile source) {
        source.stopSpinningSound();

        this.reelsSpinning--;
        if ((playState == PlayStates.PLAYING) | (playState == PlayStates.INTRO_SPINNING)) {
            if ((reelsSpinning <= -1) & (hitSinkBottom)) {
                if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
                    if (testForHiddenPatternRevealed(reelTiles, levelGridSize)) {
                        iWonTheLevel();
                    }
                }
                if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE)) {
                    if (testForHiddenPlayingCardsRevealed(reelTiles, levelGridSize)) {
                        iWonTheLevel();
                    }
                }
                if (levelDoor.getLevelType().equals(BONUS_LEVEL_TYPE)) {
                    printReelTiles();
                    if (testForJackpot(reelTiles, levelGridSize)) {
                        iWonABonus();
                    }
                }
            } else {
                if (testForJackpot(reelTiles, levelGridSize))
                    iWonABonus();
            }
        }
    }

    private void actionReelStoppedFlashing(ReelTile reelTile) {
        if (playState != PlayStates.INTRO_SPINNING) {
            if (testForAnyLonelyReels(reelTiles, levelGridSize)) {
                win = false;
                if (hud.getLives() > 0) {
                    setPlayState(PlayStates.LEVEL_LOST);
                } else {
                    gameOver = true;
                }
            }
        }
        reelScoreAnimation(reelTile);
        deleteReelAnimation(reelTile);
    }

    private ReelTileGridValue[][] flashSlots(Array<ReelTile> reelTiles, GridSize levelGridSize) {
        if (playState == PlayStates.INTRO_SPINNING)
            playState = PlayStates.INTRO_FLASHING;

        ReelTileGridValue[][] puzzleGrid =
            puzzleGridTypeReelTile.populateMatchGrid(reelTiles,  levelGridSize);

        Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

        matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);

        if (matchedSlots.size > 0) {
            matchedSlots.reverse();
            int numberOfReelBoxesToReplace = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;
            numberOfReelBoxesToDelete = matchedSlots.size - 1 - duplicateMatchedSlots.size / 2;

            for (TupleValueIndex matchedSlot : matchedSlots) {
                reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);
            }

            flashMatchedSlots(matchedSlots, puzzleGridTypeReelTile);
            matchedReels = true;
        } else {
            matchedReels = false;
        }
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

    private void flashMatchedSlots(Array<ReelTileGridValue> matchedSlots,
                                   PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
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
            if (matchSlotsBatch.size == 0) {
                break;
            }
            flashMatchedSlotsBatch(matchSlotsBatch, pushPause);
            pushPause += 2.0f;
            matchSlotsBatch.clear();
        }
    }

    private boolean testForHiddenPatternRevealed(Array<ReelTile> levelReel,
                                                 GridSize levelGridSize) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelGridSize);
        return hiddenPatternRevealed(matchGrid);
    }

    private boolean testForHiddenPlayingCardsRevealed(Array<ReelTile> levelReel,
                                                      GridSize levelGridSize) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelGridSize);
        return hiddenPlayingCardsRevealed(matchGrid);
    }

    private boolean hiddenPlayingCardsRevealed(TupleValueIndex[][] grid) {
        boolean hiddenPlayingCardsRevealed = true;
        for (Integer hiddenPlayingCard : hiddenPlayingCards) {
            MapObject mapObject = this.level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class).get(hiddenPlayingCard.intValue());
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            for (int ro = (int) (mapRectangle.getX()); ro < (int) (mapRectangle.getX() + mapRectangle.getWidth()); ro += SlotPuzzleConstants.TILE_WIDTH) {
                for (int co = (int) (mapRectangle.getY()) ; co < (int) (mapRectangle.getY() + mapRectangle.getHeight()); co += SlotPuzzleConstants.TILE_HEIGHT) {
                    int c = getColumnFromLevel(ro);
                    int r = getRowFromLevel(co, levelGridSize.getHeight());

                    if ((r >= 0) &
                        (r <= levelGridSize.getHeight()) &
                        (c >= 0) &
                        (c <= this.levelGridSize.getWidth())) {
                        if (grid[r][c] != null) {
                            if (!reelTiles.get(grid[r][c].getIndex()).isReelTileDeleted()) {
                                hiddenPlayingCardsRevealed = false;
                            }
                        }
                    } else {
                        Gdx.app.debug(
                            SlotPuzzleConstants.SLOT_PUZZLE,
                            "I don't respond to r="+r+"c="+c);
                    }
                }
            }
        }
        return hiddenPlayingCardsRevealed;
    }

    private boolean hiddenPatternRevealed(TupleValueIndex[][] grid) {
        boolean hiddenPattern = true;
        for (MapObject mapObject : this.level.getLayers().get(HIDDEN_PATTERN_LAYER_NAME).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = getColumnFromLevel(mapRectangle.getX());
            int r = getRowFromLevel(mapRectangle.getY(), levelGridSize.getHeight());
            if ((r >= 0) &
                (r <= levelGridSize.getHeight()) &
                (c >= 0) &
                (c <= levelGridSize.getWidth())) {
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

    boolean testForAnyLonelyReels(Array<ReelTile> levelReel, GridSize levelGridSize) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid =
            puzzleGridTypeReelTile.populateMatchGrid(levelReel, levelGridSize);
        PuzzleGridType.printGrid(grid);
        return puzzleGrid.anyLonelyTiles(grid);
    }

    private boolean testForJackpot(Array<ReelTile> levelReel, GridSize levelGridSize) {
        TupleValueIndex[][] matchGrid = flashSlots(levelReel, levelGridSize);
        return true;
    }

    private void iWonTheLevel() {
        gameOver = true;
        win = true;
        playState = PlayStates.WON_LEVEL;
    }

    private void iWonABonus() {
        System.out.println("iWonABonus!");
    }

    public Array<ReelTile> getReelTiles() {
        return this.reelTiles;
    }

    public Array<Body> getReelBoxes() { return  this.reelBoxes; }

    public Array<AnimatedReel> getAnimatedReels() {
        return this.animatedReelHelper.getAnimatedReels();
    }

    private void reelScoreAnimation(ReelTile source) {
        ScoreSprite scoreSprite = new ScoreSprite(source.getX(), source.getY(), (source.getEndReel() + 1) * source.getScore());
        scores.add(scoreSprite);
        Timeline.createSequence()
            .beginParallel()
            .push(SlotPuzzleTween.to(scoreSprite, ScoreAccessor.POS_XY, 2.0f).targetRelative(Random.getInstance().nextInt(20), Random.getInstance().nextInt(160)).ease(Quad.IN))
            .push(SlotPuzzleTween.to(scoreSprite, ScoreAccessor.SCALE_XY, 2.0f).target(2.0f, 2.0f).ease(Quad.IN))
            .end()
            .setUserData(scoreSprite)
            .setCallback(deleteScoreCallback)
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .start(tweenManager);
    }

    private TweenCallback deleteScoreCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            switch (type) {
                case TweenCallback.COMPLETE:
                    ScoreSprite scoreSprite = (ScoreSprite) source.getUserData();
                    scores.removeValue(scoreSprite, false);

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
                    hud.addScore((reel.getEndReel() + 1) * reel.getScore());

                    reel.deleteReelTile();
                    physics.deleteBody(reelBoxes.get(reelTilesIndex));
                    replacementReelBoxes.add(reelTilesIndex);
                    numberOfReelBoxesToDelete--;
                    if (numberOfReelBoxesToDelete <= 0) {
                        if ((playState == PlayStates.INTRO_FLASHING) |
                            (playState == PlayStates.REELS_FLASHING)) {
                            if (reelsAboveHaveFallen) {
                                if (!matchedReels)
                                    dropReplacementReelBoxes = true;
                                else
                                    flashSlots(reelTiles, levelGridSize);
                            }
                        }
                    }

                    if (levelDoor.getLevelType().equals(PLAYING_CARD_LEVEL_TYPE)) {
                        testPlayingCardLevelWon(levelGridSize);
                    } else {
                        if (levelDoor.getLevelType().equals(HIDDEN_PATTERN_LEVEL_TYPE)) {
                            testForHiddenPatternLevelWon(levelGridSize);
                        }
                    }
            }
        }
    };

    private void testPlayingCardLevelWon(GridSize levelGridSize) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid =
            puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelGridSize);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPlayingCardsRevealed(matchGrid)) {
            iWonTheLevel();
        }
    }

    private void testForHiddenPatternLevelWon(GridSize levelGridSize) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] matchGrid =
            puzzleGridTypeReelTile.populateMatchGrid(reelTiles, levelGridSize);
        puzzleGrid.matchGridSlots(matchGrid);
        if (hiddenPatternRevealed(matchGrid)) {
            iWonTheLevel();
        }
    }

    private void initialiseReelFlash(ReelTile reel, float pushPause) {
        Array<Object> userData = new Array<Object>();
        reel.setFlashTween(true);
        Timeline reelFlashSeq = Timeline.createSequence();
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

    public void update(float dt) {
        if (dropReplacementReelBoxes) {
            for (Integer replacementReelBox : replacementReelBoxes) {
                Gdx.app.debug(
                    SlotPuzzleConstants.SLOT_PUZZLE,
                    MessageFormat.format("r={0} c={1} x={2} y={3} dx={4} dy={5} i={6} v={7}",
                        PuzzleGridTypeReelTile.getRowFromLevel(
                            reelTiles.get(replacementReelBox).getDestinationY(),
                            levelGridSize.getHeight()),
                        PuzzleGridTypeReelTile.getColumnFromLevel(
                            reelTiles.get(replacementReelBox).getDestinationX()),
                        reelTiles.get(replacementReelBox).getX(),
                        reelTiles.get(replacementReelBox).getY(),
                        reelTiles.get(replacementReelBox).getDestinationX(),
                        reelTiles.get(replacementReelBox).getDestinationY(),
                        replacementReelBox.intValue(),
                        reelTiles.get(replacementReelBox).getEndReel()));
            }
        }
        this.animatedReelHelper.update(dt);
        this.physics.update(dt);
    }

    private void createReplacementReelBoxes() {
        for (Integer reelBoxIndex : replacementReelBoxes) {
            ReelTile reelTile = reelTiles.get(reelBoxIndex.intValue());
            reelTile.unDeleteReelTile();
            reelTile.setScale(1.0f);
            Color reelTileColor = reelTile.getColor();
            reelTileColor.set(reelTileColor.r, reelTileColor.g, reelTileColor.b, 1.0f);
            reelTile.setColor(reelTileColor);
            reelTile.setEndReel(Random.getInstance().nextInt(animatedReelHelper.getReelSprites().getSprites().length - 1));
            Body reelTileBody = physics.createBoxBody(BodyDef.BodyType.DynamicBody,
                reelTile.getX() + 20,
                reelTile.getY() + 360,
                19,
                19,
                true);
            reelTileBody.setUserData(reelTile);
            reelBoxes.set(reelBoxIndex, reelTileBody);
            AnimatedReel animatedReel = animatedReelHelper.getAnimatedReels().get(reelBoxIndex);
            animatedReel.reinitialise();
        }
        numberOfReelsToHitSinkBottom = replacementReelBoxes.size;
        replacementReelBoxes.removeRange(0, replacementReelBoxes.size - 1);
        dropReplacementReelBoxes = false;
        if (playState == PlayStates.INTRO_FLASHING)
            playState = PlayStates.INTRO_SPINNING;

    }

    public void setHitSinkBottom(boolean hitSinkBottom) {
        this.hitSinkBottom = hitSinkBottom;
    }

    public void setNumberOfReelsSpinning(int numberOfReelsSpinning) {
        this.reelsSpinning = numberOfReelsSpinning;
    }

    public int getNumberOfReelsSpinning() {
        return this.reelsSpinning;
    }

    public Array<ScoreSprite> getScores() {return this.scores; }

    public Array<Integer> getReplacementReelBoxes() {
        return  this.replacementReelBoxes;
    }

    private void playSound(Sound sound) {
        if (sound != null) {
            sound.play();
        }
    }

    public void printReelBoxes(Array<Body> reelBoxes) {
        int index = 0;
        for (Body reelBox : reelBoxes) {
            ReelTile reelTile = (ReelTile) reelBox.getUserData();
            System.out.println("i="+index+" reelBody="+reelBox+" reelBody.x="+reelBox.getPosition().x*100+" reelBody.y="+reelBox.getPosition().y*100+" reelTile="+reelTile+" reelTile.x="+reelTile.getX()+" reelTile.y="+reelTile.getY());
            index++;
        }
    }

    public void printReelTiles() {
        int index = 0;
        for (ReelTile reelTile : reelTiles) {
            System.out.println("i="+index+" reelTile="+reelTile+" reelTile.x="+reelTile.getX()+" reelTile.y="+reelTile.getY());
            index++;
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
}
