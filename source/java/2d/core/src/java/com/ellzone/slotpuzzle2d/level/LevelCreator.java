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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReelHelper;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelStoppedSpinningEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.ReelTileEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTileListener;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

public class LevelCreator {

    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final String REELS_LAYER_NAME = "Reels";

    private LevelDoor levelDoor;
    private TiledMap level;
    private HiddenPlayingCard hiddenPlayingCard;
    private TweenManager tweenManager;
    private AnimatedReelHelper animatedReelHelper;
    private Array<ReelTile> animatedReelTiles;
    private AssetManager assetManager;
    private TextureAtlas carddeckAtlas;
    private int levelWidth, levelHeight;

    public LevelCreator(LevelDoor levelDoor, TiledMap level, AssetManager assetManager, TextureAtlas carddeckAtlas, TweenManager tweenManager, int levelWidth, int levelHeight) {
        this.levelDoor = levelDoor;
        this.level = level;
        this.tweenManager = tweenManager;
        this.assetManager = assetManager;
        this.carddeckAtlas = carddeckAtlas;
        this.levelWidth = levelWidth;
        this.levelHeight = levelHeight;

        animatedReelHelper = new AnimatedReelHelper(this.assetManager, this.tweenManager, this.levelWidth * this.levelHeight);
        animatedReelTiles = animatedReelHelper.getReelTiles();
        createLevel(this.levelDoor, this.level, animatedReelTiles);
    }

    private Array<ReelTile> createLevel(LevelDoor levelDoor, TiledMap level, Array<ReelTile> reelTiles) {
        if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
            this.hiddenPlayingCard = new HiddenPlayingCard(level, carddeckAtlas);
        }
        reelTiles = populateLevel(level, reelTiles);
        reelTiles = checkLevel(reelTiles);
        reelTiles = adjustForAnyLonelyReels(reelTiles);
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
            } else if (lonelyTile.r == this.levelHeight) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r-1][lonelyTile.c].index).getEndReel());
            } else if (lonelyTile.c == this.levelWidth) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c-1].index).getEndReel());
            } else {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index).setEndReel(levelReel.get(grid[lonelyTile.r+1][lonelyTile.c].index).getEndReel());
            }
        }
        return levelReel;
    }

    private Array<ReelTile> populateLevel(TiledMap level, Array<ReelTile> reelTiles) {
        for (MapObject mapObject : level.getLayers().get(REELS_LAYER_NAME).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
            int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
            r = PlayScreen.GAME_LEVEL_HEIGHT - r;
            if ((r >= 0) & (r <= PlayScreen.GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= PlayScreen.GAME_LEVEL_WIDTH)) {
                addReel(mapRectangle, reelTiles, r, c);
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r=" + r + " c=" + c + ". There it won't be added to the level! Sort it out in a level editor.");
            }
        }
        return reelTiles;
    }

    private TupleValueIndex[][] populateMatchGrid(Array<ReelTile> reelLevel) {
        TupleValueIndex[][] matchGrid = new TupleValueIndex[9][12];
        int r, c;
        for (int i = 0; i < reelLevel.size; i++) {
            c = (int) (reelLevel.get(i).getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
            r = (int) (reelLevel.get(i).getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
            r = this.levelHeight - r;
            if ((r >= 0) & (r <= this.levelHeight) & (c >= 0) & (c <= this.levelHeight)) {
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

    private void addReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, int row, int column) {
        ReelTile reelTile = reelTiles.get(row * this.levelWidth + column);
        reelTile.setX(mapRectangle.getX());
        reelTile.setY(mapRectangle.getY());
        reelTile.setSx(0);
        reelTile.addListener(new ReelTileListener() {
            @Override
            public void actionPerformed(ReelTileEvent event, ReelTile source) {
                if (event instanceof ReelStoppedSpinningEvent) {
                    dealWithReelStoppedSpinningEvent(source);
                }
                if ((event instanceof ReelStoppedFlashingEvent)) {
                    dealWithReelStoppedFlashingEvent(source);
                }
            }
        });
    }

    private void dealWithReelStoppedSpinningEvent(ReelTile reelTile) {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "In dealWithReelStoppedSpinningEvent with reelTile:" + reelTile);
    }

    private void dealWithReelStoppedFlashingEvent(ReelTile reelTile) {
        Gdx.app.log(SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName(), "In dealWithReelStoppedFlashingEvent with reelTile:" + reelTile);
    }
}
