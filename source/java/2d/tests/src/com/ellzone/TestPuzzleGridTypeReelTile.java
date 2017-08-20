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

package com.ellzone;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.prototypes.Reels;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import de.tomgrill.gdxtesting.GdxTestRunnerGetAllTestClasses;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunnerGetAllTestClasses.class)

public class TestPuzzleGridTypeReelTile {
    private Reels reels;
    private Texture slotReelTexture;
    private AssetManager assetManager;
    private TiledMap level;
    private int mapWidth, mapHeight, tilePixelWidth, tilePixelHeight, gridWidth;
    private Array<ReelTile> reelTiles;
    private Random random = new Random();

    @Before
    public void setUp() throws Exception {
        reels = new Reels();
        reelTiles = new Array<ReelTile>();
        slotReelTexture = createSlotReelTexture(reels);
        loadAssets();
        getAssets();
        getMapProperties(level);
        createLevel(reelTiles, reels);
    }

    @Test
    public void testMatchSlots() {
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(reelTiles,  mapWidth, mapHeight);
        setLevelReelTileTestPattern(puzzleGrid);
        Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

        matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);
        assertEquals(matchedSlots.get(0).getReelTile(), matchedSlots.get(1).getW());
        assertEquals(matchedSlots.get(0).getE(), matchedSlots.get(1).getReelTile());
        assertEquals(matchedSlots.get(1).getE(), matchedSlots.get(2).getReelTile());
        assertEquals(matchedSlots.get(1).getReelTile(), matchedSlots.get(2).getW());
        ReelTileGridValue linkReelTileGridValue = PuzzleGridTypeReelTile.findReelTileGridValue(matchedSlots, matchedSlots.get(2).getS());
        int linkReelTileGridValueIndex = matchedSlots.indexOf(linkReelTileGridValue, true);
        assertEquals(matchedSlots.get(2).getS(), linkReelTileGridValue.getReelTile());
        assertEquals(matchedSlots.get(linkReelTileGridValueIndex).getN(), matchedSlots.get(2).getReelTile());
        assertEquals(matchedSlots.get(linkReelTileGridValueIndex).getS(), matchedSlots.get(linkReelTileGridValueIndex + 1).getReelTile());

        assertEquals(matchedSlots.get(0).getR(), 3);
        assertEquals(matchedSlots.get(0).getC(), 2);
        assertEquals(matchedSlots.get(1).getR(), 3);
        assertEquals(matchedSlots.get(1).getC(), 3);
        assertEquals(matchedSlots.get(2).getR(), 3);
        assertEquals(matchedSlots.get(2).getC(), 4);
        assertEquals(matchedSlots.get(linkReelTileGridValueIndex).getR(), 4);
        assertEquals(matchedSlots.get(linkReelTileGridValueIndex).getC(), 4);
        assertEquals(matchedSlots.get(linkReelTileGridValueIndex + 1).getR(), 5);
        assertEquals(matchedSlots.get(linkReelTileGridValueIndex + 1).getC(), 4);
    }

    private Texture createSlotReelTexture(Reels reels) {
        Pixmap slotReelScrollPixmap = new Pixmap((int) reels.getReelWidth(), (int) reels.getReelHeight(), Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reels.getReels());
        return new Texture(slotReelScrollPixmap);
    }

    private void createLevel(Array<ReelTile> reelTiles, Reels reels) {
        for (MapObject mapObject : level.getLayers().get(PlayScreen.SLOT_REEL_OBJECT_LAYER).getObjects().getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
            int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
            r = PlayScreen.GAME_LEVEL_HEIGHT - r;
            if ((r >= 0) & (r <= PlayScreen.GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= PlayScreen.GAME_LEVEL_WIDTH)) {
                addReel(mapRectangle, reelTiles, reels);
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r="+r+" c="+c+". There it won't be added to the level! Sort it out in a level editor.");
            }
        }
    }

    private void addReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, Reels reels) {
        int endReel = random.nextInt(reels.getReels().length);
        ReelTile reel = new ReelTile(slotReelTexture, reels.getReels().length, 0, 0, reels.getReelWidth(), reels.getReelHeight(), reels.getReelWidth(), reels.getReelHeight(), endReel, null);
        reel.setX(mapRectangle.getX());
        reel.setY(mapRectangle.getY());
        reel.setSx(0);
        int startReel = random.nextInt((int) slotReelTexture.getHeight());
        startReel = (startReel / ((int) reels.getReelWidth())) * (int)reels.getReelHeight();
        reel.setSy(startReel);
        reelTiles.add(reel);
    }

    private void loadAssets() {
        assetManager = new AssetManager();
        assetManager.setLoader(TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        assetManager.load("levels/mini slot machine level.tmx", TiledMap.class);
        assetManager.finishLoading();
    }

    private void getAssets() {
        level = assetManager.get("levels/mini slot machine level.tmx");
    }

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
        tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        tilePixelHeight = mapProperties.get("tileheight", Integer.class);
    }

    private void setLevelReelTileTestPattern(ReelTileGridValue[][] puzzleGrid) {
        puzzleGrid[3][2].setValue(4);
        puzzleGrid[3][3].setValue(4);
        puzzleGrid[3][4].setValue(4);
        puzzleGrid[4][4].setValue(4);
        puzzleGrid[5][4].setValue(4);
    }
}