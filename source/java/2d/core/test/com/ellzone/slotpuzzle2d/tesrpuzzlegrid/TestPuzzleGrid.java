package com.ellzone.slotpuzzle2d.tesrpuzzlegrid;

import static org.junit.Assert.*;

import com.ellzone.slotpuzzle2d.puzzlegrid.*;
import org.junit.Test;

public class TestPuzzleGrid {

	@Test
	public void testMatchRows() {
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
		assertTrueGridsAreEqual(resultGrid, expectedGrid);
	}
	
	@Test 
	public void testMatchColumns(){
		PuzzleGrid puzzleGrid = new PuzzleGrid();
		int[][] testGrid = { 
				{  3, -1,  0,  0},
				{  3,  1,  4,  1},
				{  3, -1,  4,  5},
				{  3, -1,  4,  5},
		};
		int[][] expectedGrid = {
				{  4, -1,  1,  1},
				{  4,  1,  3,  1},
				{  4, -1,  3,  2},
				{  4, -1,  3,  2},
		};
		int[][] resultGrid = puzzleGrid.matchColumnSlots(testGrid);
		puzzleGrid.printGrid(resultGrid);
		assertTrueGridsAreEqual(resultGrid, expectedGrid);
	}
	
	private void assertTrueGridsAreEqual(int[][] first, int[][] second) {
		for (int x = 0; x < first.length; x++) {
			for (int y = 0; y < first[x].length; y++) {
				assertTrue(first[x][y] == second[x][y]);
			}
		}		
	}
}
