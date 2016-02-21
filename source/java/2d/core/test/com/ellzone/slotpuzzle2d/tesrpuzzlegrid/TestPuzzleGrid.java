package com.ellzone.slotpuzzle2d.tesrpuzzlegrid;

import static org.junit.Assert.*;

import com.ellzone.slotpuzzle2d.puzzlegrid.*;
import org.junit.Test;

public class TestPuzzleGrid {

	@Test
	public void testMatch() {
		PuzzleGrid puzzleGrid = new PuzzleGrid();
		int[][] testGrid = { 
				{ -1, -1,  0,  0},
				{  1,  1,  1,  1},
				{ -1, -1, -1, -1},
		};
		int[][] expectedGrid = {
				{ -1, -1,  2,  2},
				{  4,  4,  4,  4},
				{ -1, -1, -1, -1},
		};
		int[][] resultGrid = puzzleGrid.matchRowSlots(testGrid);
		puzzleGrid.printGrid(resultGrid);
		for (int x = 0; x < resultGrid.length; x++) {
			for (int y = 0; y < resultGrid[x].length; y++) {
				assertTrue(expectedGrid[x][y] == resultGrid[x][y]);
			}
		}
	}
}
