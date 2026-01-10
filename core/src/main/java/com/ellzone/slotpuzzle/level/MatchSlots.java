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

package com.ellzone.slotpuzzle.level;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;

public class MatchSlots {
    private  GridSize levelGridSize;
    private Array<ReelTile> reelTiles;
    private PuzzleGridTypeReelTile puzzleGridTypeReelTile;
    private ReelTileGridValue[][] puzzleGrid;
    private Array<ReelTileGridValue> matchedSlots;

    public MatchSlots(Array<ReelTile> reelTiles, GridSize levelGridSize) {
        this.reelTiles = reelTiles;
        this.levelGridSize = levelGridSize;
    }

    public PuzzleGridTypeReelTile getPuzzleGridTypeReelTile() {
        return puzzleGridTypeReelTile;
    }

    public ReelTileGridValue[][] getPuzzleGrid() {
        return puzzleGrid;
    }

    public Array<ReelTileGridValue> getMatchedSlots() {
        return matchedSlots;
    }

    public ReelTileGridValue[][] populateMatchGrid(int[][] puzzleGrid) {
        return puzzleGridTypeReelTile.populateMatchGrid(puzzleGrid);
    }

    public ReelTileGridValue[][] createGridLinks() {
        return puzzleGridTypeReelTile.createGridLinks(puzzleGrid);
    }

    public ReelTileGridValue[][] createGridLinksWithoutMatch() {
        return puzzleGridTypeReelTile.createGridLinksWithoutMatch(puzzleGrid);
    }

    public MatchSlots invoke() {
        puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        puzzleGrid = puzzleGridTypeReelTile.populateMatchGrid(
            reelTiles,
            levelGridSize);

        matchedSlots = puzzleGridTypeReelTile.matchGridSlots(puzzleGrid);
        Array<ReelTileGridValue> duplicateMatchedSlots = PuzzleGridTypeReelTile.findDuplicateMatches(matchedSlots);

        matchedSlots = PuzzleGridTypeReelTile.adjustMatchSlotDuplicates(matchedSlots, duplicateMatchedSlots);
        matchedSlots = PuzzleGridTypeReelTile.removeDuplicateMatches(duplicateMatchedSlots, matchedSlots);
        return this;
    }
}
