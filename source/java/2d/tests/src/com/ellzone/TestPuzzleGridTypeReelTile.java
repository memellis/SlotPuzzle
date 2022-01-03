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
import com.ellzone.slotpuzzle2d.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;

import de.tomgrill.gdxtesting.GdxTestRunnerGetAllTestClasses;

import static org.junit.Assert.assertEquals;

@RunWith(GdxTestRunnerGetAllTestClasses.class)

public class TestPuzzleGridTypeReelTile {
    private Texture slotReelTexture;
    private TiledMap level;
    private int gridWidth, gridHeight;
    private Array<ReelTile> reelTiles;
    private final Random random = new Random();

    @Before
    public void setUp() throws Exception {
        AnnotationAssetManager annotationAssetManager = new AnnotationAssetManager();
        loadAssets(annotationAssetManager);
        getAssets(annotationAssetManager);
        ReelSprites reelSprites = new ReelSprites(annotationAssetManager);
        reelTiles = new Array<ReelTile>();
        slotReelTexture = createSlotReelTexture(reelSprites);
        getMapProperties(level);
        createLevel(reelTiles, reelSprites);
    }

    @Test
    public void testMatchSlots() {
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] puzzleGrid =
                puzzleGridTypeReelTile.populateMatchGrid(
                        reelTiles,
                        new GridSize(gridWidth, gridHeight));

        setLevelReelTileTestPattern(puzzleGrid);
        Array<ReelTileGridValue> matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots =
                PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);
        matchedSlots = PuzzleGridTypeReelTile.
                adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);

        assertEquals(matchedSlots.get(0).getReelTile(), matchedSlots.get(1).getW());
        assertEquals(matchedSlots.get(0).getE(), matchedSlots.get(1).getReelTile());
        assertEquals(matchedSlots.get(1).getE(), matchedSlots.get(2).getReelTile());
        assertEquals(matchedSlots.get(1).getReelTile(), matchedSlots.get(2).getW());
        ReelTileGridValue linkReelTileGridValue =
                PuzzleGridTypeReelTile.findReelTileGridValue(
                        matchedSlots, matchedSlots.get(2).getS());
        int linkReelTileGridValueIndex = matchedSlots.indexOf(linkReelTileGridValue, true);
        assertEquals(matchedSlots.get(2).getS(), linkReelTileGridValue.getReelTile());
        assertEquals(
                matchedSlots.get(linkReelTileGridValueIndex).getN(),
                matchedSlots.get(2).getReelTile());

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

        Array<ReelTileGridValue> matchSlotBatch = new Array<ReelTileGridValue>();
        matchSlotBatch = puzzleGridTypeReelTile.depthFirstSearch(matchedSlots.get(0));
        assertEquals(matchSlotBatch.get(0).getR(), 3);
        assertEquals(matchSlotBatch.get(0).getC(), 2);
        assertEquals(matchSlotBatch.get(1).getR(), 3);
        assertEquals(matchSlotBatch.get(1).getC(), 3);
        assertEquals(matchSlotBatch.get(2).getR(), 3);
        assertEquals(matchSlotBatch.get(2).getC(), 4);
        assertEquals(matchSlotBatch.get(3).getR(), 4);
        assertEquals(matchSlotBatch.get(3).getC(), 4);
        assertEquals(matchSlotBatch.get(4).getR(), 5);
        assertEquals(matchSlotBatch.get(4).getC(), 4);

        assertEquals(matchSlotBatch.get(0).getEReelTileGridValue(), matchSlotBatch.get(1));
        assertEquals(matchSlotBatch.get(1).getWReelTileGridValue(), matchSlotBatch.get(0));
        assertEquals(matchSlotBatch.get(1).getEReelTileGridValue(), matchSlotBatch.get(2));
        assertEquals(matchSlotBatch.get(2).getWReelTileGridValue(), matchSlotBatch.get(1));
        assertEquals(matchSlotBatch.get(2).getSReelTileGridValue(), matchSlotBatch.get(3));
        assertEquals(matchSlotBatch.get(3).getNReelTileGridValue(), matchSlotBatch.get(2));
        assertEquals(matchSlotBatch.get(3).getSReelTileGridValue(), matchSlotBatch.get(4));
        assertEquals(matchSlotBatch.get(4).getNReelTileGridValue(), matchSlotBatch.get(3));
    }

    private Texture createSlotReelTexture(ReelSprites reelSprites) {
        Pixmap slotReelScrollPixmap =
                new Pixmap(
                        (int) reelSprites.getReelWidth(),
                        (int) reelSprites.getReelHeight(),
                        Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        return new Texture(slotReelScrollPixmap);
    }

    private void createLevel(Array<ReelTile> reelTiles, ReelSprites reelSprites) {
        for (MapObject mapObject : level.
                getLayers().
                get(SlotPuzzleConstants.REEL_OBJECT_LAYER).
                getObjects().
                getByType(RectangleMapObject.class)) {
            Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
            int c = (int) (mapRectangle.getX() - PlayScreen.PUZZLE_GRID_START_X) / SlotPuzzleConstants.TILE_WIDTH;
            int r = (int) (mapRectangle.getY() - PlayScreen.PUZZLE_GRID_START_Y) / SlotPuzzleConstants.TILE_HEIGHT;
            r = SlotPuzzleConstants.GAME_LEVEL_HEIGHT - r;
            if ((r >= 0) & (r <= SlotPuzzleConstants.GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= SlotPuzzleConstants.GAME_LEVEL_WIDTH)) {
                addReel(mapRectangle, reelTiles, reelSprites);
            } else {
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to grid r="+r+" c="+c+". There it won't be added to the level! Sort it out in a level editor.");
            }
        }
    }

    private void addReel(Rectangle mapRectangle, Array<ReelTile> reelTiles, ReelSprites reelSprites) {
        int endReel = random.nextInt(reelSprites.getSprites().length);
        ReelTile reel = new ReelTile(
                slotReelTexture,
                reelSprites.getSprites().length,
                0,
                0,
                reelSprites.getReelWidth(),
                reelSprites.getReelHeight(),
                reelSprites.getReelWidth(),
                reelSprites.getReelHeight(),
                endReel
        );
        reel.setX(mapRectangle.getX());
        reel.setY(mapRectangle.getY());
        reel.setDestinationX(mapRectangle.getX());
        reel.setDestinationY(mapRectangle.getY());
        reel.setSx(0);
        int startReel = random.nextInt((int) slotReelTexture.getHeight());
        startReel =
            (startReel / ((int) reelSprites.getReelWidth())) * (int) reelSprites.getReelHeight();
        reel.setSy(startReel);
        reelTiles.add(reel);
    }

    private void loadAssets(AnnotationAssetManager annotationAssetManager) {
        annotationAssetManager.setLoader(
                TiledMap.class, new TmxMapLoader(new InternalFileHandleResolver()));
        annotationAssetManager.load("levels/mini slot machine level.tmx", TiledMap.class);
        annotationAssetManager.load(new AssetsAnnotation());
        annotationAssetManager.finishLoading();
    }

    private void getAssets(AnnotationAssetManager annotationAssetManager) {
        level = annotationAssetManager.get("levels/mini slot machine level.tmx");
    }

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        gridWidth = Integer.parseInt(mapProperties.get("GridWidth", String.class));
        gridHeight = Integer.parseInt(mapProperties.get("GridHeight", String.class));
    }

    private void setLevelReelTileTestPattern(ReelTileGridValue[][] puzzleGrid) {
        puzzleGrid[3][2].setValue(4);
        puzzleGrid[3][3].setValue(4);
        puzzleGrid[3][4].setValue(4);
        puzzleGrid[4][4].setValue(4);
        puzzleGrid[5][4].setValue(4);
    }
}