package com.ellzone.slotpuzzle2d.tesrpuzzlegrid;

import static org.junit.Assert.*;

import com.badlogic.gdx.utils.Array;
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
		assertTrueGridsAreEqual(resultGrid, expectedGrid);
	}

	@Test
	public void testMatchRowsColumns() {
		PuzzleGrid puzzleGrid = new PuzzleGrid();
		int[][] testGrid = { 
				{  3,  0,  0,  0},
				{  3,  1,  4,  4},
				{  3,  3,  4,  5},
				{  3,  3,  4,  5},
		};
		int[][] expectedRowGrid = {
				{  1,  3,  3,  3},
				{  1,  1,  2,  2},
				{  2,  2,  1,  1},
				{  2,  2,  1,  1},
		};
		int[][] expectedColumnGrid = {
				{  4,  1,  1,  1},
				{  4,  1,  3,  1},
				{  4,  2,  3,  2},
				{  4,  2,  3,  2},
		};
		int[][] resultGrid = puzzleGrid.matchRowSlots(testGrid);
		assertTrueGridsAreEqual(resultGrid, expectedRowGrid);
		resultGrid = puzzleGrid.matchColumnSlots(testGrid);
		assertTrueGridsAreEqual(resultGrid, expectedColumnGrid);
	}
	
	@Test
	public void testGetMatchedSlots() {
		PuzzleGrid puzzleGrid = new PuzzleGrid();
		int[][] testGrid = { 
				{  3,  0,  0,  0},
				{  3,  1,  4,  4},
				{  3,  3,  4,  5},
				{  3,  3,  4,  5},
		};
		Array<Tuple> matchedSlots = new Array<Tuple>();
		Array<Tuple> expectedMatchedSlots = new Array<Tuple>();
		expectedMatchedSlots.add(new Tuple(0, 1, 3));
		expectedMatchedSlots.add(new Tuple(0, 2, 3));
		expectedMatchedSlots.add(new Tuple(0, 3, 3));
		expectedMatchedSlots.add(new Tuple(1, 2, 2));		
		expectedMatchedSlots.add(new Tuple(1, 3, 2));
		expectedMatchedSlots.add(new Tuple(2, 0, 2));		
		expectedMatchedSlots.add(new Tuple(2, 1, 2));
		expectedMatchedSlots.add(new Tuple(3, 0, 2));
		expectedMatchedSlots.add(new Tuple(3, 1, 2));
		expectedMatchedSlots.add(new Tuple(0, 0, 4));
		expectedMatchedSlots.add(new Tuple(1, 0, 4));
		expectedMatchedSlots.add(new Tuple(2, 0, 4));
		expectedMatchedSlots.add(new Tuple(3, 0, 4));
		expectedMatchedSlots.add(new Tuple(2, 1, 2));
		expectedMatchedSlots.add(new Tuple(3, 1, 2));
		expectedMatchedSlots.add(new Tuple(1, 2, 3));
		expectedMatchedSlots.add(new Tuple(2, 2, 3));
		expectedMatchedSlots.add(new Tuple(3, 2, 3));
		expectedMatchedSlots.add(new Tuple(2, 3, 2));
		expectedMatchedSlots.add(new Tuple(3, 3, 2));
		
		matchedSlots = puzzleGrid.matchGridSlots(testGrid);
		for (int i = 0; i < matchedSlots.size; i++) {
			assertTrue(matchedSlots.get(i).getR() == expectedMatchedSlots.get(i).getR());
			assertTrue(matchedSlots.get(i).getC() == expectedMatchedSlots.get(i).getC());
			assertTrue(matchedSlots.get(i).getValue() == expectedMatchedSlots.get(i).getValue());
		}		
	}
	
	@Test
	public void testMatchSlotsGameGrid() {
		PuzzleGrid puzzleGrid = new PuzzleGrid();
		int[][] testGrid = { 
			{ 1, 1, 5, 1, 7, 4, 1, 4, 5 }, 
			{ 1, 0, 6, 1, 2, 2, 7, 1, 6 }, 
			{ 5, 7, 0, 5, 6, 7, 0, 2, 6 }, 
			{ 5, 6, 7, 6, 3, 4, 4, 3, 6 }, 
			{ 3, 4, 0, 5, 2, 5, 6, 2, 3 }, 
			{ 6, 4, 7, 4, 3, 7, 4, 5, 1 }, 
			{ 3, 7, 1, 4, 3, 2, 5, 0, 4 }, 
			{ 5, 1, 0, 2, 6, 5, 1, 6, 2 }, 
			{ 4, 7, 0, 5, 4, 3, 1, 4, 3 },
		};
		Array<Tuple> matchedSlots = new Array<Tuple>();
		Array<Tuple> expectedMatchedSlots = new Array<Tuple>();
		expectedMatchedSlots.add(new Tuple(0, 0, 2));
		expectedMatchedSlots.add(new Tuple(0, 1, 2));
		expectedMatchedSlots.add(new Tuple(1, 4, 2));
		expectedMatchedSlots.add(new Tuple(1, 5, 2));
		expectedMatchedSlots.add(new Tuple(3, 5, 2));
		expectedMatchedSlots.add(new Tuple(3, 6, 2));
		expectedMatchedSlots.add(new Tuple(0, 0, 2));
		expectedMatchedSlots.add(new Tuple(1, 0, 2));
		expectedMatchedSlots.add(new Tuple(2, 0, 2));
		expectedMatchedSlots.add(new Tuple(3, 0, 2));
		expectedMatchedSlots.add(new Tuple(4, 1, 2));
		expectedMatchedSlots.add(new Tuple(5, 1, 2));
		expectedMatchedSlots.add(new Tuple(7, 2, 2));
		expectedMatchedSlots.add(new Tuple(8, 2, 2));
		expectedMatchedSlots.add(new Tuple(0, 3, 2));
		expectedMatchedSlots.add(new Tuple(1, 3, 2));
		expectedMatchedSlots.add(new Tuple(5, 3, 2));
		expectedMatchedSlots.add(new Tuple(6, 3, 2));
		expectedMatchedSlots.add(new Tuple(5, 4, 2));
		expectedMatchedSlots.add(new Tuple(6, 4, 2));
		expectedMatchedSlots.add(new Tuple(7, 6, 2));
		expectedMatchedSlots.add(new Tuple(8, 6, 2));
		expectedMatchedSlots.add(new Tuple(1, 8, 3));
		expectedMatchedSlots.add(new Tuple(2, 8, 3));
		expectedMatchedSlots.add(new Tuple(3, 8, 3));

		matchedSlots = puzzleGrid.matchGridSlots(testGrid);
		for (int i = 0; i < matchedSlots.size; i++) {
			assertTrue(matchedSlots.get(i).getR() == expectedMatchedSlots.get(i).getR());
			assertTrue(matchedSlots.get(i).getC() == expectedMatchedSlots.get(i).getC());
			assertTrue(matchedSlots.get(i).getValue() == expectedMatchedSlots.get(i).getValue());
			System.out.print(matchedSlots.get(i).getR() + " " + expectedMatchedSlots.get(i).getR() + " ");
			System.out.print(matchedSlots.get(i).getC() + " " + expectedMatchedSlots.get(i).getC() + " ");
			System.out.println(matchedSlots.get(i).getValue() + " " + expectedMatchedSlots.get(i).getValue());
		}
		PuzzleGrid.printMatchedSlots(matchedSlots);
	}
	
	private void assertTrueGridsAreEqual(int[][] first, int[][] second) {
		for (int x = 0; x < first.length; x++) {
			for (int y = 0; y < first[x].length; y++) {
				assertTrue(first[x][y] == second[x][y]);
			}
		}		
	}
}
