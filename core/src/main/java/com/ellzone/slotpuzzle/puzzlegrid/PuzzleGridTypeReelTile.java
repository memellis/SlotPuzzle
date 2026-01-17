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

package com.ellzone.slotpuzzle.puzzlegrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.screens.PlayScreen;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;

import java.text.MessageFormat;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import static com.ellzone.slotpuzzle.puzzlegrid.ReelTileGridValue.Compass;

public class PuzzleGridTypeReelTile {
    public static final float FLOAT_ROUNDING_DELTA_FOR_BOX2D = 1.0f;

    public Array<ReelTile> checkGrid(Array<ReelTile> reelLevel,
                                     GridSize levelGridSize) {
        TupleValueIndex[][] grid = populateMatchGrid(reelLevel, levelGridSize);
        int arraySizeR = grid.length;
        int arraySizeC = grid[0].length;

        for(int r = 0; r < arraySizeR; r++) {
            for(int c = 0; c < arraySizeC; c++) {
                if(grid[r][c] == null)
                    throw new GdxRuntimeException("Level incorrect. Found null grid tile. r=" + r + " c= " + c);
            }
        }
        return reelLevel;
    }

    public void initialiseGrid(
        ReelTileGridValue[][] workingGrid,
        ReelTileGridValue[][] puzzleGrid) {
        for (int r = 0; r < workingGrid.length; r++) {
            for (int c = 0; c < workingGrid[r].length; c++) {
                if (puzzleGrid[r][c] == null) {
                    workingGrid[r][c] = new ReelTileGridValue(r, c, -1, -1);
                } else {
                    workingGrid[r][c] = new ReelTileGridValue(puzzleGrid[r][c].getReelTile(), r, c, puzzleGrid[r][c].index, -1);
                }
            }
        }
    }

    public Array<ReelTileGridValue> matchGridSlots(ReelTileGridValue[][] puzzleGrid) {
        ReelTileGridValue[][] matchedGridRows = matchRowSlots(puzzleGrid);
        ReelTileGridValue[][] matchedGridCols = matchColumnSlots(puzzleGrid);

        Array<ReelTileGridValue> matchedSlots = new Array<>();
        matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
        matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);

        return matchedSlots;
    }

    public ReelTileGridValue[][] matchRowSlots(ReelTileGridValue[][] puzzleGrid) {
        int arraySizeR = puzzleGrid.length;
        int arraySizeC = puzzleGrid[0].length;
        ReelTileGridValue[][] workingGrid = new ReelTileGridValue[arraySizeR][arraySizeC];

        initialiseGrid(workingGrid, puzzleGrid);
        int r = 0;
        while(r < arraySizeR) {
            int c = 0;
            while (c < arraySizeC) {
                if(puzzleGrid[r][c] != null) {
                    if(puzzleGrid[r][c].value >= 0) {
                        int co = c + 1;
                        boolean match = true;
                        while (match) {
                            if (!(co < arraySizeC)) {
                                match = false;
                            } else {
                                if (puzzleGrid[r][c] != null) {
                                    if (puzzleGrid[r][co] != null) {
                                        if (puzzleGrid[r][co-1].value == puzzleGrid[r][co].value) {
                                            workingGrid[r][co-1].setE(puzzleGrid[r][co].getReelTile());
                                            workingGrid[r][co].setW(puzzleGrid[r][co-1].getReelTile());
                                            workingGrid[r][co-1].setEReelTileGridValue(workingGrid[r][co]);
                                            workingGrid[r][co].setWReelTileGridValue(workingGrid[r][co-1]);
                                            co++;
                                        } else
                                            match = false;
                                    } else {
                                        Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "r=" + r + " co=" + co + " is Null - ignoring this tile.");
                                        match = false;
                                    }
                                } else {
                                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "r=" + r + " c" + c + " is Null - ignoring this tile.");
                                    match = false;
                                }
                            }
                        }
                        for (int i = c; i < co; i++)
                            workingGrid[r][i].value = co - c;

                        c = co - 1;
                    }
                }
                c++;
            }
            r++;
        }
        return workingGrid;
    }

    public ReelTileGridValue[][] matchColumnSlots(ReelTileGridValue[][] puzzleGrid) {
        int arraySizeR = puzzleGrid.length;
        int arraySizeC = puzzleGrid[0].length;
        ReelTileGridValue[][] workingGrid = new ReelTileGridValue[arraySizeR][arraySizeC];

        initialiseGrid(workingGrid, puzzleGrid);
        int c = 0;
        while(c < arraySizeC) {
            int r = 0;
            while (r < arraySizeR) {
                if (puzzleGrid[r][c] != null) {
                    if(puzzleGrid[r][c].value >= 0) {
                        int ro = r + 1;
                        boolean match = true;
                        while (match) {
                            if (!(ro < arraySizeR)) {
                                match = false;
                            } else {
                                if (puzzleGrid[r][c] != null) {
                                    if (puzzleGrid[ro][c] != null) {
                                        if (puzzleGrid[ro-1][c].value == puzzleGrid[ro][c].value) {
                                            workingGrid[ro-1][c].setS(puzzleGrid[ro][c].getReelTile());
                                            workingGrid[ro][c].setN(workingGrid[ro-1][c].getReelTile());
                                            workingGrid[ro-1][c].setSReelTileGridValue(workingGrid[ro][c]);
                                            workingGrid[ro][c].setNReelTileGridValue(workingGrid[ro-1][c]);
                                            ro++;
                                        } else
                                            match = false;
                                    } else {
                                        Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "ro=" + ro + " c=" + c + " is Null - ignoring this tile.");
                                        match = false;
                                    }
                                } else {
                                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "r=" + r + " c=" + r + " is Null - ignoring this tile.");
                                    match = false;
                                }
                            }
                        }
                        for (int i = r; i < ro; i++)
                            workingGrid[i][c].value = ro - r;
                        r = ro - 1;
                    }
                }
                r++;
            }
            c++;
        }
        return workingGrid;
    }

    private Array<ReelTileGridValue> getMatchedRowSlots(
        ReelTileGridValue[][] puzzleGrid, Array<ReelTileGridValue> matchedSlots) {
        int arraySizeR = puzzleGrid.length;
        int arraySizeC = puzzleGrid[0].length;

        for (int r = 0; r < arraySizeR; r++) {
            for (int c = 0; c < arraySizeC; c++) {
                if (puzzleGrid[r][c].value > 1)
                    matchedSlots.add(puzzleGrid[r][c]);
            }
        }
        return matchedSlots;
    }

    private Array<ReelTileGridValue> getMatchedColSlots(
        ReelTileGridValue[][] puzzleGrid, Array<ReelTileGridValue> matchedSlots) {
        int arraySizeR = puzzleGrid.length;
        int arraySizeC = puzzleGrid[0].length;

        for (int c = 0; c < arraySizeC; c++) {
            for (int r = 0; r < arraySizeR; r++) {
                if (puzzleGrid[r][c].value > 1)
                    matchedSlots.add(puzzleGrid[r][c]);
            }
        }
        return matchedSlots;
    }

    public boolean compareGrids(ReelTileGridValue[][] first, ReelTileGridValue[][] second) {
        if (first.length != second.length) {
            return false;
        }
        for (int r = 0; r < first.length; r++) {
            if (first[r].length != second[r].length) {
                return false;
            }
            for (int c = 0; c < first[r].length; c++) {
                if(first[r][c].value != second[r][c].value) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean anyLonelyTiles(ReelTileGridValue[][] puzzleGrid) {
        ReelTileGridValue[][] matchedGridRows = matchRowSlots(puzzleGrid);
        ReelTileGridValue[][] matchedGridCols = matchColumnSlots(puzzleGrid);
        Array<ReelTileGridValue> matchedSlots = new Array<ReelTileGridValue>();

        matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
        matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);

        ReelTileGridValue[][] workingGrid = crossOffMatchSlots(matchedSlots, puzzleGrid);

        matchedGridRows = findLonelyRowTiles(workingGrid);
        matchedGridCols = findLonelyColumnTiles(workingGrid);
        matchedSlots = new Array<ReelTileGridValue>();
        matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
        matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);

        workingGrid = crossOffMatchSlots(matchedSlots, workingGrid);

        for (int r = 0; r < workingGrid.length; r++) {
            for (int c = 0; c < workingGrid[0].length; c++) {
                if (workingGrid[r][c].value >= 0) {
                    return true;
                }
            }
        }
        return false;
    }

    public Array<ReelTileGridValue> getLonelyTiles(ReelTileGridValue[][] puzzleGrid) {
        ReelTileGridValue[][] matchedGridRows = matchRowSlots(puzzleGrid);
        ReelTileGridValue[][] matchedGridCols = matchColumnSlots(puzzleGrid);
        Array<ReelTileGridValue> matchedSlots = new Array<ReelTileGridValue>();

        matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
        matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);

        ReelTileGridValue[][] workingGrid = crossOffMatchSlots(matchedSlots, puzzleGrid);

        matchedGridRows = findLonelyRowTiles(workingGrid);
        matchedGridCols = findLonelyColumnTiles(workingGrid);
        matchedSlots = new Array<ReelTileGridValue>();
        matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
        matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);

        workingGrid = crossOffMatchSlots(matchedSlots, workingGrid);

        matchedSlots = new Array<ReelTileGridValue>();
        for (int r = 0; r < workingGrid.length; r++) {
            for (int c = 0; c < workingGrid[0].length; c++) {
                if (workingGrid[r][c].value >= 0) {
                    matchedSlots.add(new ReelTileGridValue(r, c, workingGrid[r][c].index, workingGrid[r][c].value));
                }
            }
        }
        return matchedSlots;
    }

    private ReelTileGridValue[][] findLonelyRowTiles(ReelTileGridValue[][] puzzleGrid) {
        int arraySizeR = puzzleGrid.length;
        int arraySizeC = puzzleGrid[0].length;
        ReelTileGridValue[][] workingGrid = new ReelTileGridValue[arraySizeR][arraySizeC];

        initialiseGrid(workingGrid, puzzleGrid);
        int r = 0;
        while(r < arraySizeR) {
            int c = 0;
            while (c < arraySizeC) {
                if(puzzleGrid[r][c] != null) {
                    if(puzzleGrid[r][c].value >= 0) {
                        int co = c + 1;
                        boolean match = true;
                        while (match == true) {
                            if (!(co < arraySizeC)) {
                                match = false;
                            } else {
                                if (puzzleGrid[r][c] != null) {
                                    if (puzzleGrid[r][co] != null) {
                                        if (puzzleGrid[r][co].value >=0) {
                                            co++;
                                        } else
                                            match = false;
                                    } else {
                                        Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "r=" + r + " co=" + co + " is Null - ignoring this tile.");
                                        match = false;
                                    }
                                } else {
                                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "r=" + r + " c=" + c + " is Null - ignoring this tile.");
                                    match = false;
                                }
                            }
                        }
                        for (int i = c; i < co; i++)
                            workingGrid[r][i].value = co - c;

                        c = co - 1;
                    }
                }
                c++;
            }
            r++;
        }
        return workingGrid;
    }

    private ReelTileGridValue[][] findLonelyColumnTiles(ReelTileGridValue[][] puzzleGrid) {
        int arraySizeR = puzzleGrid.length;
        int arraySizeC = puzzleGrid[0].length;
        ReelTileGridValue[][] workingGrid = new ReelTileGridValue[arraySizeR][arraySizeC];

        initialiseGrid(workingGrid, puzzleGrid);
        int c = 0;
        while(c < arraySizeC) {
            int r = 0;
            while (r < arraySizeR) {
                if (puzzleGrid[r][c] != null) {
                    if(puzzleGrid[r][c].value >= 0) {
                        int co = r + 1;
                        boolean match = true;
                        while (match == true) {
                            if (!(co < arraySizeR)) {
                                match = false;
                            } else {
                                if (puzzleGrid[r][c] != null) {
                                    if (puzzleGrid[r][co] != null) {
                                        if (puzzleGrid[co][c].value >=0) {
                                            co++;
                                        } else
                                            match = false;
                                    } else {
                                        Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "r=" + r + " co=" + co + " is Null - ignoring this tile.");
                                        match = false;
                                    }
                                } else {
                                    Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "r=" + r + " c=" + c + " is Null - ignoring this tile.");
                                    match = false;
                                }
                            }
                        }
                        for (int i = r; i < co; i++)
                            workingGrid[i][c].value = co - r;

                        r = co - 1;;
                    }
                }
                r++;
            }
            c++;
        }
        return workingGrid;
    }

    private ReelTileGridValue[][] crossOffMatchSlots(
        Array<ReelTileGridValue> matchedSlots,
        ReelTileGridValue[][] puzzleGrid) {
        ReelTileGridValue[][] workingGrid = copyGrid(puzzleGrid);
        for (TupleValueIndex matchSlot : matchedSlots)
            workingGrid[matchSlot.r][matchSlot.c].value = -1;
        return workingGrid;
    }

    public Array<ReelTile> adjustForAnyLonelyReels(Array<ReelTile> levelReel,
                                                   GridSize levelGridSize) {
        PuzzleGridType puzzleGrid = new PuzzleGridType();
        TupleValueIndex[][] grid = populateMatchGrid(levelReel, levelGridSize);
        Array<TupleValueIndex> lonelyTiles = puzzleGrid.getLonelyTiles(grid);
        for (TupleValueIndex lonelyTile : lonelyTiles) {
            if (lonelyTile.r == 0) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r + 1][lonelyTile.c].index).getEndReel());
            } else if (lonelyTile.c == 0) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c + 1].index).getEndReel());
            } else if (lonelyTile.r == levelGridSize.getRows() - 1) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r - 1][lonelyTile.c].index).getEndReel());
            } else if (lonelyTile.c == levelGridSize.getColumns() - 1) {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r][lonelyTile.c - 1].index).getEndReel());
            } else {
                levelReel.get(grid[lonelyTile.r][lonelyTile.c].index)
                    .setEndReel(levelReel.get(grid[lonelyTile.r + 1][lonelyTile.c].index).getEndReel());
            }
        }
        return levelReel;
    }


    public static void printGrid(ReelTileGridValue[][] puzzleGrid){
        for(int r = 0; r < puzzleGrid.length; r++){
            for (int c = 0; c < puzzleGrid[r].length; c++) {
                if (puzzleGrid[r][c] == null)
                    System.out.print(" ! ");
                else {
                    if (puzzleGrid[r][c].value == -1)
                        System.out.print(puzzleGrid[r][c].value + " ");
                    else {
                        if (puzzleGrid[r][c].value == -2)
                            System.out.print(" S ");
                        else
                            System.out.print(" " + puzzleGrid[r][c].value + " ");
                    }
                }
            }
            System.out.println();
        }
    }

    public static void printMatchGrid(Array<ReelTile> reelLevel, GridSize levelGridSize) {
        PuzzleGridTypeReelTile.printGrid(
            PuzzleGridTypeReelTile.populateMatchGridStatic(reelLevel, levelGridSize));
    }

    private static ReelTileGridValue[][] copyGrid(ReelTileGridValue[][] puzzleGrid) {
        int arraySizeR = puzzleGrid.length;
        int arraySizeC = puzzleGrid[0].length;
        ReelTileGridValue[][] targetGrid = new ReelTileGridValue[arraySizeR][arraySizeC];

        for(int r = 0; r < puzzleGrid.length; r++){
            for (int c = 0; c < puzzleGrid[r].length; c++) {
                targetGrid[r][c] = new ReelTileGridValue(
                    puzzleGrid[r][c].getReelTile(),
                    puzzleGrid[r][c].r,
                    puzzleGrid[r][c].c,
                    puzzleGrid[r][c].index,
                    puzzleGrid[r][c].value);
            }
        }
        return targetGrid;
    }

    public static void printMatchedSlots(Array<ReelTileGridValue> tuples) {
        System.out.println("printMatchedSlots");
        for (int i = 0; i < tuples.size; i++) {
            System.out.println(i + "=[" + tuples.get(i).getR() + "," + tuples.get(i).getC() + "]=" + tuples.get(i).getValue());
        }
    }

    public static void printMatchedSlotsTuples(Array<TupleValueIndex> tuples) {
        System.out.println("printMatchedSlots");
        for (int i = 0; i < tuples.size; i++) {
            System.out.println(i + "=[" + tuples.get(i).getR() + "," + tuples.get(i).getC() + "]=" + tuples.get(i).getValue());
        }
    }

    public static Array<ReelTileGridValue> findDuplicateMatches(Array<ReelTileGridValue> matchSlots) {
        Array<ReelTileGridValue> duplicateMatches = new Array<ReelTileGridValue>();
        for (int i = 0; i < matchSlots.size; i++) {
            for (int j = i + 1; j < matchSlots.size; j++) {
                if ((matchSlots.get(i).getR() == matchSlots.get(j).getR()) &&
                    (matchSlots.get(i).getC() == matchSlots.get(j).getC())) {
                    duplicateMatches.add(matchSlots.get(i));
                    duplicateMatches.add(matchSlots.get(j));
                }
            }
        }
        return duplicateMatches;
    }

    public static Array<ReelTileGridValue> removeDuplicateMatches(
        Array<ReelTileGridValue> duplicateSlots, Array<ReelTileGridValue> matchedSlots) {
        int i = 0;
        while (i < duplicateSlots.size) {
            ReelTileGridValue currentDuplicate = duplicateSlots.get(i);
            matchedSlots = removeValue(matchedSlots, currentDuplicate);
            i++;
            while ((i < duplicateSlots.size) &&
                ((currentDuplicate.r == duplicateSlots.get(i).r) &
                    (currentDuplicate.c == duplicateSlots.get(i).c)))
                i++;
        }
        return matchedSlots;
    }

    private static Array<ReelTileGridValue> removeValue(Array<ReelTileGridValue> matchedSlots, ReelTileGridValue valueToBeRemoved) {
        int row = valueToBeRemoved.r;
        int column = valueToBeRemoved.c;
        for (int index = 0; index < matchedSlots.size; index++) {
            if ((matchedSlots.get(index).r == row) & (matchedSlots.get(index).c == column)) {
                matchedSlots.removeIndex(index);
                break;
            }
        }
        return matchedSlots;
    }

    public static Array<ReelTileGridValue> adjustMatchSlotDuplicates(
        Array<ReelTileGridValue> matchedSlots,
        Array<ReelTileGridValue> duplicateMatchedSlots) {
        int i = 0;
        while (i < duplicateMatchedSlots.size) {
            linkDuplicatesForReelTiles(
                matchedSlots,
                duplicateMatchedSlots.get(i),
                duplicateMatchedSlots.get(i+1),
                Compass.NORTH);
            linkDuplicatesForReelTiles(
                matchedSlots,
                duplicateMatchedSlots.get(i),
                duplicateMatchedSlots.get(i+1),
                Compass.EAST);
            linkDuplicatesForReelTiles(
                matchedSlots,
                duplicateMatchedSlots.get(i),
                duplicateMatchedSlots.get(i+1),
                Compass.SOUTH);
            linkDuplicatesForReelTiles(
                matchedSlots,
                duplicateMatchedSlots.get(i),
                duplicateMatchedSlots.get(i+1),
                Compass.WEST);
            i = i + 2;
        }
        return matchedSlots;
    }

    private static void linkDuplicatesForReelTiles(
        Array<ReelTileGridValue> matchSlots,
        ReelTileGridValue value1,
        ReelTileGridValue value2,
        Compass compass) {
        switch (compass) {
            case NORTH:
                if ((value1.getN() == null) && (value2.getN() != null)) {
                    value1.setN(value2.getN());
                    value1.setNReelTileGridValue(value2.getNReelTileGridValue());
                    ReelTile value2n = value2.getN();
                    ReelTileGridValue nRTGV = findReelTileGridValue(matchSlots, value2n);
                    nRTGV.setS(value1.getReelTile());
                    nRTGV.setSReelTileGridValue(value1);
                }
                break;
            case EAST:
                if ((value1.getE() == null) && (value2.getE() != null)) {
                    value1.setE(value2.getE());
                    value1.setEReelTileGridValue(value2.getEReelTileGridValue());
                    ReelTile value2e = value2.getE();
                    ReelTileGridValue eRTGV = findReelTileGridValue(matchSlots, value2e);
                    eRTGV.setW(value1.getReelTile());
                    eRTGV.setWReelTileGridValue(value1);
                }
                break;
            case SOUTH:
                if ((value1.getS() == null) && (value2.getS() != null)) {
                    value1.setS(value2.getS());
                    value1.setSReelTileGridValue(value2.getSReelTileGridValue());
                    ReelTile value2s = value2.getS();
                    ReelTileGridValue sRTGV = findReelTileGridValue(matchSlots, value2s);
                    sRTGV.setN(value1.getReelTile());
                    sRTGV.setNReelTileGridValue(value1);
                }
                break;
            case WEST:
                if ((value1.getW() == null) && (value2.getW() != null)) {
                    value1.setW(value2.getW());
                    value1.setWReelTileGridValue(value2.getWReelTileGridValue());
                    ReelTile value2w = value2.getW();
                    ReelTileGridValue wRTGV = findReelTileGridValue(matchSlots, value2w);
                    wRTGV.setE(value1.getReelTile());
                    wRTGV.setEReelTileGridValue(value1.getEReelTileGridValue());
                }
                break;
        }
    }

    public static ReelTileGridValue findReelTileGridValue(
        Array<ReelTileGridValue> matchSlots,
        ReelTile reelTile) {
        for (ReelTileGridValue reelTileGridValue : matchSlots) {
            if (reelTileGridValue.getReelTile() == reelTile) {
                return reelTileGridValue;
            }
        }
        return null;
    }

    public ReelTileGridValue[][] populateMatchGrid(Array<ReelTile> reelLevel,
                                                   GridSize levelGridSize) {
        int gridHeight = levelGridSize.getRows();
        int gridWidth = levelGridSize.getColumns();
        ReelTileGridValue[][] matchGrid =
            new ReelTileGridValue[gridHeight][gridWidth];
        int r, c;
        for (int i = 0; i < reelLevel.size; i++) {
            c = getColumnFromLevel(reelLevel.get(i).getDestinationX());
            r = getRowFromLevel(reelLevel.get(i).getDestinationY(), gridHeight);
            if ((r >= 0) & (r <= gridHeight) & (c >= 0) & (c <= gridWidth)) {
                if (reelLevel.get(i).isReelTileDeleted())
                    matchGrid[r][c] = new ReelTileGridValue(r, c, i, -1);
                else {
                    if (reelLevel.get(i).isSpinning())
                        matchGrid[r][c] = new ReelTileGridValue(
                            reelLevel.get(i),
                            r,
                            c,
                            i,
                            -2);
                    else
                        matchGrid[r][c] = new ReelTileGridValue(
                            reelLevel.get(i),
                            r,
                            c,
                            i,
                            reelLevel.get(i).getEndReel());
                }
                Gdx.app.debug(
                    SlotPuzzleConstants.SLOT_PUZZLE,
                    MessageFormat.format("r={0} c={1} x={2} y={3} dx={4} dy={5} i={6} v={7}",
                        r,
                        c,
                        reelLevel.get(i).getX(), reelLevel.get(i).getY(),
                        reelLevel.get(i).getDestinationX(), reelLevel.get(i).getDestinationY(),
                        i,
                        reelLevel.get(i).getEndReel()));
            } else
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to ***r="+r+" c="+c);
        }
        return matchGrid;
    }

    public static ReelTileGridValue[][] populateMatchGridStatic(Array<ReelTile> reelLevel,
                                                                GridSize levelGridSize) {
        int gridHeight = levelGridSize.getRows();
        int gridWidth = levelGridSize.getColumns();
        ReelTileGridValue[][] matchGrid = new ReelTileGridValue[gridHeight][gridWidth];
        int r, c;
        for (int i = 0; i < reelLevel.size; i++) {
            c = getColumnFromLevel(reelLevel.get(i).getDestinationX());
            r = getRowFromLevel(reelLevel.get(i).getDestinationY(), gridHeight);
            if ((r >= 0) & (r <= gridHeight) & (c >= 0) & (c <= gridWidth)) {
                if (reelLevel.get(i).isReelTileDeleted())
                    matchGrid[r][c] = new ReelTileGridValue(r, c, i, -1);
                else {
                    if (reelLevel.get(i).isSpinning())
                        matchGrid[r][c] = new ReelTileGridValue(r, c, i, -2);
                    else
                        matchGrid[r][c] = new ReelTileGridValue(reelLevel.get(i), r, c, i, reelLevel.get(i).getEndReel());
                }
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, MessageFormat.format("r={0} c={1} x={2} y={3} dx={4} dy={5} i={6} v={7}",
                    r, c,
                    reelLevel.get(i).getX(), reelLevel.get(i).getY(),
                    reelLevel.get(i).getDestinationX(), reelLevel.get(i).getDestinationY(),
                    i,
                    reelLevel.get(i).getEndReel()));
            } else
                Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to ***r="+r+" c="+c);
        }
        return matchGrid;
    }

    public ReelTileGridValue[][] populateMatchGrid(int[][] puzzleGrid) {
        ReelTileGridValue[][] matchGrid = new ReelTileGridValue[puzzleGrid.length][puzzleGrid[0].length];
        for (int r = 0; r < puzzleGrid.length; r++) {
            for (int c = 0; c < puzzleGrid[0].length; c++) {
                matchGrid[r][c] = new ReelTileGridValue(r, c, -1, puzzleGrid[r][c]);
            }
        }
        return matchGrid;
    }

    public ReelTileGridValue[][] createGridLinks(ReelTileGridValue[][] puzzleGrid) {
        for (int r = 0; r < puzzleGrid.length; r++) {
            for (int c = 0; c < puzzleGrid[0].length; c++) {
                puzzleGrid = createGridLink(puzzleGrid, r, c);
            }
        }
        return puzzleGrid;
    }

    private ReelTileGridValue[][] createGridLink(ReelTileGridValue[][] puzzleGrid, int r, int c) {
        puzzleGrid = setCompassPoint(puzzleGrid, r    , c    ,r - 1, c       , Compass.NORTH);
        puzzleGrid = setCompassPoint(puzzleGrid, r    , c    ,r - 1,c + 1, Compass.NORTHEAST);
        puzzleGrid = setCompassPoint(puzzleGrid, r    , c    , r       ,c + 1, Compass.EAST);
        puzzleGrid = setCompassPoint(puzzleGrid, r    , c    ,r + 1,c + 1, Compass.SOUTHEAST);
        puzzleGrid = setCompassPoint(puzzleGrid, r    , c    ,r + 1, c       , Compass.SOUTH);
        puzzleGrid = setCompassPoint(puzzleGrid, r    , c    ,r + 1,c - 1, Compass.SOUTHWEST);
        puzzleGrid = setCompassPoint(puzzleGrid, r    , c    , r       ,c - 1, Compass.WEST);
        puzzleGrid = setCompassPoint(puzzleGrid, r    , c    ,r - 1,c - 1, Compass.NORTHWEST);
        return puzzleGrid;
    }

    private ReelTileGridValue[][] setCompassPoint(
        ReelTileGridValue[][] puzzleGrid,
        int r,
        int c,
        int r1,
        int c1,
        Compass compassPoint) {
        if (isWithinGrid(puzzleGrid, r1, c1)) {
            if (puzzleGrid[r][c].value == puzzleGrid[r1][c1].value) {
                puzzleGrid[r][c].setCompassPoint(compassPoint, puzzleGrid[r1][c1]);
                puzzleGrid[r1][c1].setCompassPoint(getOppositeCompassPoint(compassPoint), puzzleGrid[r][c]);
            }
        }
        return puzzleGrid;
    }

    private Compass getOppositeCompassPoint(Compass compassPoint) {
        return Compass.getCompass((compassPoint.ordinal() + Compass.values().length / 2)
            % Compass.getLength());
    }

    public boolean isWithinGrid(ReelTileGridValue[][] puzzleGrid, int r, int c) {
        return(r >= 0) & (r < puzzleGrid.length) &
            (c >= 0) & (c < puzzleGrid[0].length);
    }

    public ReelTileGridValue[][] createGridLinksWithoutMatch(ReelTileGridValue[][] puzzleGrid) {
        for (int r = 0; r < puzzleGrid.length; r++) {
            for (int c = 0; c < puzzleGrid[0].length; c++) {
                puzzleGrid = createGridLinkWithoutMatch(puzzleGrid, r, c);
            }
        }
        return puzzleGrid;
    }

    private ReelTileGridValue[][] createGridLinkWithoutMatch(ReelTileGridValue[][] puzzleGrid, int r, int c) {
        puzzleGrid = setCompassPointWithoutMatch(puzzleGrid, r    , c    ,r - 1, c       , Compass.NORTH);
        puzzleGrid = setCompassPointWithoutMatch(puzzleGrid, r    , c    ,r - 1,c + 1, Compass.NORTHEAST);
        puzzleGrid = setCompassPointWithoutMatch(puzzleGrid, r    , c    , r       ,c + 1, Compass.EAST);
        puzzleGrid = setCompassPointWithoutMatch(puzzleGrid, r    , c    ,r + 1,c + 1, Compass.SOUTHEAST);
        puzzleGrid = setCompassPointWithoutMatch(puzzleGrid, r    , c    ,r + 1, c       , Compass.SOUTH);
        puzzleGrid = setCompassPointWithoutMatch(puzzleGrid, r    , c    ,r + 1,c - 1, Compass.SOUTHWEST);
        puzzleGrid = setCompassPointWithoutMatch(puzzleGrid, r    , c    , r       ,c - 1, Compass.WEST);
        puzzleGrid = setCompassPointWithoutMatch(puzzleGrid, r    , c    ,r - 1,c - 1, Compass.NORTHWEST);
        return puzzleGrid;
    }

    private ReelTileGridValue[][] setCompassPointWithoutMatch(
        ReelTileGridValue[][] puzzleGrid,
        int r,
        int c,
        int r1,
        int c1,
        Compass compassPoint) {
        if (isWithinGrid(puzzleGrid, r1, c1)) {
            if ((puzzleGrid[r][c].value >= 0) & (puzzleGrid[r1][c1].value >= 0)) {
                puzzleGrid[r][c].setCompassPoint(compassPoint, puzzleGrid[r1][c1]);
                puzzleGrid[r1][c1].setCompassPoint(getOppositeCompassPoint(compassPoint), puzzleGrid[r][c]);
            }
        }
        return puzzleGrid;
    }


    public static int getRowFromLevel(float y, int levelHeight) {
        int row = (int) (y - PlayScreen.PUZZLE_GRID_START_Y) / SlotPuzzleConstants.TILE_HEIGHT;
        row = levelHeight - 1 - row;
        return row;
    }

    public static int getColumnFromLevel(float x) {
        int column = (int) (x - PlayScreen.PUZZLE_GRID_START_X) / SlotPuzzleConstants.TILE_WIDTH;
        return column;
    }

    public Array<ReelTileGridValue> depthFirstSearch(ReelTileGridValue startReelTile) {
        Array<ReelTileGridValue> localMatchedSlotBatch = new Array<ReelTileGridValue>();
        ReelTileGridValue currentReelTile;
        Stack<ReelTileGridValue> reelTileGridValuesStack = new Stack<ReelTileGridValue>();
        reelTileGridValuesStack.push(startReelTile);
        while (!reelTileGridValuesStack.empty()) {
            currentReelTile = reelTileGridValuesStack.pop();
            if (!currentReelTile.getDiscovered()) {
                currentReelTile.setDiscovered(true);
                localMatchedSlotBatch.add(currentReelTile);
                if (currentReelTile.getNReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getNReelTileGridValue());
                }
                if (currentReelTile.getEReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getEReelTileGridValue());
                }
                if (currentReelTile.getSReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getSReelTileGridValue());
                }
                if (currentReelTile.getWReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getWReelTileGridValue());
                }
            }
        }
        return localMatchedSlotBatch;
    }

    public Array<ReelTileGridValue> depthFirstSearchAddToMatchSlotBatch(ReelTileGridValue startReelTile, Array<ReelTileGridValue> matchedSlotBatch) {
        ReelTileGridValue currentReelTile;
        Stack<ReelTileGridValue> reelTileGridValuesStack = new Stack<ReelTileGridValue>();
        reelTileGridValuesStack.push(startReelTile);
        while (!reelTileGridValuesStack.empty()) {
            currentReelTile = reelTileGridValuesStack.pop();
            if (currentReelTile != null && !currentReelTile.getDiscovered()) {
                currentReelTile.setDiscovered(true);
                matchedSlotBatch.add(currentReelTile);
                if (currentReelTile.getNReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getNReelTileGridValue());
                }
                if (currentReelTile.getEReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getEReelTileGridValue());
                }
                if (currentReelTile.getSReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getSReelTileGridValue());
                }
                if (currentReelTile.getWReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getWReelTileGridValue());
                }
            }
        }
        return matchedSlotBatch;
    }

    public Array<ReelTileGridValue> depthFirstSearchIncludeDiagonals(ReelTileGridValue startReelTile) {
        Array<ReelTileGridValue> localMatchedSlotBatch = new Array<ReelTileGridValue>();
        ReelTileGridValue currentReelTile;
        Stack<ReelTileGridValue> reelTileGridValuesStack = new Stack<ReelTileGridValue>();
        reelTileGridValuesStack.push(startReelTile);
        while (!reelTileGridValuesStack.empty()) {
            currentReelTile = reelTileGridValuesStack.pop();
            if (!currentReelTile.getDiscovered()) {
                currentReelTile.setDiscovered(true);
                localMatchedSlotBatch.add(currentReelTile);
                if (currentReelTile.getNReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getNReelTileGridValue());
                }
                if (currentReelTile.getNeReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getNeReelTileGridValue());
                }
                if (currentReelTile.getEReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getEReelTileGridValue());
                }
                if (currentReelTile.getSeReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getSeReelTileGridValue());
                }
                if (currentReelTile.getSReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getSReelTileGridValue());
                }
                if (currentReelTile.getSwReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getSwReelTileGridValue());
                }
                if (currentReelTile.getWReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getWReelTileGridValue());
                }
                if (currentReelTile.getNwReelTileGridValue() != null) {
                    reelTileGridValuesStack.push(currentReelTile.getNwReelTileGridValue());
                }
            }
        }
        return localMatchedSlotBatch;
    }

    public void clearDiscovered(ReelTileGridValue[][] puzzleGrid) {
        for(int r = 0; r < puzzleGrid.length; r++) {
            for(int c = 0; c < puzzleGrid[0].length; c++) {
                puzzleGrid[r][c].setDiscovered(false);
            }
        }
    }

    public boolean isRow(Array<ReelTileGridValue> potentialRow, ReelTileGridValue[][] puzzleGrid) {
        Set<Integer> columns = new HashSet<>();
        for (ReelTileGridValue cell : potentialRow) {
            columns.add(cell.c);
        }
        return columns.size() == puzzleGrid[0].length;
    }

    public ReelTileGridValue[][] createGridFromMatrix(int[][] matrix) {
        ReelTileGridValue[][] grid = new ReelTileGridValue[matrix.length][matrix[0].length];
        for (int r = 0; r < matrix.length; r++) {
            for (int c = 0; c < matrix[0].length; c++) {
                grid[r][c] = new ReelTileGridValue(r, c, r * matrix[0].length + c, matrix[r][c]);
            }
        }
        return grid;
    }

    public static void printSlotMatrix(Array<AnimatedReel> animatedReels) {
        PuzzleGridTypeReelTile.printGrid(
            PuzzleGridTypeReelTile.populateMatchGridStatic(
                PuzzleGridTypeReelTile.getReelTilesFromAnimatedReels(animatedReels),
                new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT))
        );
        System.out.println();
    }

    public static Array<ReelTile> getReelTilesFromAnimatedReels(Array<AnimatedReel> animatedReels) {
        Array<ReelTile> reelTiles = new Array<>();
        for (AnimatedReel animatedReel : animatedReels)
            reelTiles.add(animatedReel.getReel());
        return reelTiles;
    }

    public static Array<ReelTileGridValue> getSurroundingReelTiles(
        Array<ReelTileGridValue> matchedSlots,
        final ReelTileGridValue[][] grid) {
        Array<ReelTileGridValue> surroundingReelTiles = new Array<>();
        if (matchedSlots == null)
            return null;
        for (ReelTileGridValue matchedSlot : matchedSlots)
            surroundingReelTiles =
                getSurroundingReelTilesForMatchedSlot(matchedSlot, grid, surroundingReelTiles);

        if (surroundingReelTiles == null)
            return null;

        for (ReelTileGridValue matchedSlot : matchedSlots) {
            if (surroundingReelTiles.contains(matchedSlot, false))
                surroundingReelTiles.removeValue(matchedSlot, false);
        }

        surroundingReelTiles.sort(new Comparator<ReelTileGridValue>() {
            @Override
            public int compare(ReelTileGridValue o1, ReelTileGridValue o2) {
                return (o1.r * grid[0].length + o1.c) - (o2.r * grid[0].length + o2.c);
            }
        });

        return surroundingReelTiles;
    }

    private static Array<ReelTileGridValue> getSurroundingReelTilesForMatchedSlot(
        ReelTileGridValue matchedSlot,
        ReelTileGridValue[][] grid,
        Array<ReelTileGridValue> currentSurroundingReelTiles) {
        if (grid == null)
            return null;
        if (grid[matchedSlot.r][matchedSlot.c].getNReelTileGridValue() != null)
            addReelTile(currentSurroundingReelTiles,
                grid[matchedSlot.r][matchedSlot.c].getNReelTileGridValue());
        if (grid[matchedSlot.r][matchedSlot.c].getNeReelTileGridValue() != null)
            addReelTile(currentSurroundingReelTiles,
                grid[matchedSlot.r][matchedSlot.c].getNeReelTileGridValue());
        if (grid[matchedSlot.r][matchedSlot.c].getEReelTileGridValue() != null)
            addReelTile(currentSurroundingReelTiles,
                grid[matchedSlot.r][matchedSlot.c].getEReelTileGridValue());
        if (grid[matchedSlot.r][matchedSlot.c].getSeReelTileGridValue() != null)
            addReelTile(currentSurroundingReelTiles,
                grid[matchedSlot.r][matchedSlot.c].getSeReelTileGridValue());
        if (grid[matchedSlot.r][matchedSlot.c].getSReelTileGridValue() != null)
            addReelTile(currentSurroundingReelTiles,
                grid[matchedSlot.r][matchedSlot.c].getSReelTileGridValue());
        if (grid[matchedSlot.r][matchedSlot.c].getSwReelTileGridValue() != null)
            addReelTile(currentSurroundingReelTiles,
                grid[matchedSlot.r][matchedSlot.c].getSwReelTileGridValue());
        if (grid[matchedSlot.r][matchedSlot.c].getWReelTileGridValue() != null)
            addReelTile(currentSurroundingReelTiles,
                grid[matchedSlot.r][matchedSlot.c].getWReelTileGridValue());
        if (grid[matchedSlot.r][matchedSlot.c].getNwReelTileGridValue() != null)
            addReelTile(currentSurroundingReelTiles,
                grid[matchedSlot.r][matchedSlot.c].getNwReelTileGridValue());
        return currentSurroundingReelTiles;
    }

    private static void addReelTile(Array<ReelTileGridValue> reelTiles, ReelTileGridValue reelTile) {
        if (!reelTiles.contains(reelTile, false))
            reelTiles.add(reelTile);
    }
}
