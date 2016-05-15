package com.ellzone.slotpuzzle2d.tesrpuzzlegrid;

import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGrid;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class TestPuzzleGridType {
    private static final int GRID_SIZE_X = 4;
    private static final int GRID_SIZE_Y = 4;

    @Test
    public void testMatchRowSlots() {
        PuzzleGridType puzzleGridType = new PuzzleGridType();
        TupleValueIndex[][] testPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];
        TupleValueIndex[][] expectedPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];

        testPuzzleGrid[0][0] = new TupleValueIndex(0, 0,  0, -1);
        testPuzzleGrid[0][1] = new TupleValueIndex(0, 1,  1, -1);
        testPuzzleGrid[0][2] = new TupleValueIndex(0, 2,  2,  0);
        testPuzzleGrid[0][3] = new TupleValueIndex(0, 3,  3,  0);

        testPuzzleGrid[1][0] = new TupleValueIndex(1, 0,  4,  1);
        testPuzzleGrid[1][1] = new TupleValueIndex(1, 1,  5,  1);
        testPuzzleGrid[1][2] = new TupleValueIndex(1, 2,  6,  1);
        testPuzzleGrid[1][3] = new TupleValueIndex(1, 3,  7,  1);

        testPuzzleGrid[2][0] = new TupleValueIndex(2, 0,  8,  -1);
        testPuzzleGrid[2][1] = new TupleValueIndex(2, 1,  9,  -1);
        testPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10,  -1);
        testPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11,  -1);

        testPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12,   3);
        testPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13,   3);
        testPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14,   3);
        testPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15,  -1);

        expectedPuzzleGrid[0][0] = new TupleValueIndex(0, 0,  0, -1);
        expectedPuzzleGrid[0][1] = new TupleValueIndex(0, 1,  1, -1);
        expectedPuzzleGrid[0][2] = new TupleValueIndex(0, 2,  2,  2);
        expectedPuzzleGrid[0][3] = new TupleValueIndex(0, 3,  3,  2);

        expectedPuzzleGrid[1][0] = new TupleValueIndex(1, 0,  4,  4);
        expectedPuzzleGrid[1][1] = new TupleValueIndex(1, 1,  5,  4);
        expectedPuzzleGrid[1][2] = new TupleValueIndex(1, 2,  6,  4);
        expectedPuzzleGrid[1][3] = new TupleValueIndex(1, 3,  7,  4);

        expectedPuzzleGrid[2][0] = new TupleValueIndex(2, 0,  8,  -1);
        expectedPuzzleGrid[2][1] = new TupleValueIndex(2, 1,  9,  -1);
        expectedPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10,  -1);
        expectedPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11,  -1);

        expectedPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12,   3);
        expectedPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13,   3);
        expectedPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14,   3);
        expectedPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15,  -1);

        TupleValueIndex[][] resultGridPuzzleGrid = puzzleGridType.matchRowSlots(testPuzzleGrid);
        assertTrueGridsAreEqual(expectedPuzzleGrid, resultGridPuzzleGrid);
    }
    @Test
    public void testMatchColumnSlots() {
        PuzzleGridType puzzleGridType = new PuzzleGridType();
        TupleValueIndex[][] testPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];
        TupleValueIndex[][] expectedPuzzleGrid = new TupleValueIndex[TestPuzzleGridType.GRID_SIZE_X][TestPuzzleGridType.GRID_SIZE_Y];

        testPuzzleGrid[0][0] = new TupleValueIndex(0, 0,  0,  3);
        testPuzzleGrid[0][1] = new TupleValueIndex(0, 1,  1,  0);
        testPuzzleGrid[0][2] = new TupleValueIndex(0, 2,  2,  0);
        testPuzzleGrid[0][3] = new TupleValueIndex(0, 3,  3,  0);

        testPuzzleGrid[1][0] = new TupleValueIndex(1, 0,  4,  3);
        testPuzzleGrid[1][1] = new TupleValueIndex(1, 1,  5,  1);
        testPuzzleGrid[1][2] = new TupleValueIndex(1, 2,  6,  4);
        testPuzzleGrid[1][3] = new TupleValueIndex(1, 3,  7,  4);

        testPuzzleGrid[2][0] = new TupleValueIndex(2, 0,  8,   3);
        testPuzzleGrid[2][1] = new TupleValueIndex(2, 1,  9,   3);
        testPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10,   4);
        testPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11,   5);

        testPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12,   3);
        testPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13,   3);
        testPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14,   4);
        testPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15,   5);

        expectedPuzzleGrid[0][0] = new TupleValueIndex(0, 0,  0,  4);
        expectedPuzzleGrid[0][1] = new TupleValueIndex(0, 1,  1,  1);
        expectedPuzzleGrid[0][2] = new TupleValueIndex(0, 2,  2,  1);
        expectedPuzzleGrid[0][3] = new TupleValueIndex(0, 3,  3,  1);

        expectedPuzzleGrid[1][0] = new TupleValueIndex(1, 0,  4,  4);
        expectedPuzzleGrid[1][1] = new TupleValueIndex(1, 1,  5,  1);
        expectedPuzzleGrid[1][2] = new TupleValueIndex(1, 2,  6,  3);
        expectedPuzzleGrid[1][3] = new TupleValueIndex(1, 3,  7,  1);

        expectedPuzzleGrid[2][0] = new TupleValueIndex(2, 0,  8,   4);
        expectedPuzzleGrid[2][1] = new TupleValueIndex(2, 1,  9,   2);
        expectedPuzzleGrid[2][2] = new TupleValueIndex(2, 2, 10,   3);
        expectedPuzzleGrid[2][3] = new TupleValueIndex(2, 3, 11,   2);

        expectedPuzzleGrid[3][0] = new TupleValueIndex(3, 0, 12,   4);
        expectedPuzzleGrid[3][1] = new TupleValueIndex(3, 1, 13,   2);
        expectedPuzzleGrid[3][2] = new TupleValueIndex(3, 2, 14,   3);
        expectedPuzzleGrid[3][3] = new TupleValueIndex(3, 3, 15,   2);

        TupleValueIndex[][] resultGridPuzzleGrid = puzzleGridType.matchColumnSlots(testPuzzleGrid);
        assertTrueGridsAreEqual(expectedPuzzleGrid, resultGridPuzzleGrid);
    }

    private void assertTrueGridsAreEqual(TupleValueIndex[][] first, TupleValueIndex[][] second) {
        for (int x = 0; x < first.length; x++) {
            for (int y = 0; y < first[x].length; y++) {
                assertTrue(first[x][y].r == second[x][y].r);
                assertTrue(first[x][y].c == second[x][y].c);
                assertTrue(first[x][y].index == second[x][y].index);
                assertTrue(first[x][y].value == second[x][y].value);
                assertTrue(first[x][y].getR() == second[x][y].getR());
                assertTrue(first[x][y].getC() == second[x][y].getC());
                assertTrue(first[x][y].getIndex() == second[x][y].getIndex());
                assertTrue(first[x][y].getValue() == second[x][y].getValue());
            }
        }
    }
}
