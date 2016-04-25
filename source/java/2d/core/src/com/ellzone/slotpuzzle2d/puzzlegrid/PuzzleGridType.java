package com.ellzone.slotpuzzle2d.puzzlegrid;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzle;

public class PuzzleGridType {
	
	public TupleValueIndex[][] matchRowSlots(TupleValueIndex[][] puzzleGrid) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		TupleValueIndex [][] workingGrid = new TupleValueIndex[arraySizeR][arraySizeC];
		
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
								if (puzzleGrid[r][c].value == puzzleGrid[r][co].value) {
									co++;
								} else {
									match = false;
								}
							}
						
						}
						for (int i = c; i < co; i++) {
							workingGrid[r][i].value = co - c;
						}
						c = co - 1;
					}
				}
				c++;
			}	
			r++;
		}
		return workingGrid;
	}
	
	public TupleValueIndex[][] matchColumnSlots(TupleValueIndex[][] puzzleGrid) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		TupleValueIndex[][] workingGrid = new TupleValueIndex[arraySizeR][arraySizeC];
			
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
								if (puzzleGrid[r][c].value == puzzleGrid[co][c].value) {
									co++;
								} else {
									match = false;
								}
							}
						}
						for (int i = r; i < co; i++) {
							workingGrid[i][c].value = co - r;
						}
						r = co - 1;;
					}
				}
				r++;
			}
			c++;
		}		
		return workingGrid;
	}
	
	public Array<TupleValueIndex> matchGridSlots(TupleValueIndex[][] puzzleGrid) {
		TupleValueIndex[][] matchedGridRows = matchRowSlots(puzzleGrid);
		TupleValueIndex[][] matchedGridCols = matchColumnSlots(puzzleGrid);
		Array<TupleValueIndex> matchedSlots = new Array<TupleValueIndex>();
		
		matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
		matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);
		
		return matchedSlots;
	}
	
	private Array<TupleValueIndex> getMatchedRowSlots(TupleValueIndex[][] puzzleGrid, Array<TupleValueIndex> matchedSlots) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		
		for(int r = 0; r < arraySizeR; r++) {
			for(int c = 0; c < arraySizeC; c++) {
				if(puzzleGrid[r][c].value > 1) {
					matchedSlots.add(new TupleValueIndex(r, c, puzzleGrid[r][c].index, puzzleGrid[r][c].value));
				}
			}
		}
		return matchedSlots;
	}

	private Array<TupleValueIndex> getMatchedColSlots(TupleValueIndex[][] puzzleGrid, Array<TupleValueIndex> matchedSlots) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		
		for(int c = 0; c < arraySizeC; c++) {
			for(int r = 0; r < arraySizeR; r++) {
				if(puzzleGrid[r][c].value > 1) {
					matchedSlots.add(new TupleValueIndex(r, c, puzzleGrid[r][c].index, puzzleGrid[r][c].value));
				}
			}
		}
		return matchedSlots;
	}

	
	public TupleValueIndex[][] initialiseGrid(TupleValueIndex[][] workingGrid, TupleValueIndex[][] puzzleGrid) {
		for (int r = 0; r < workingGrid.length; r++) {
			for (int c = 0; c < workingGrid[r].length; c++) {
				if (puzzleGrid[r][c] == null) {
					workingGrid[r][c] = new TupleValueIndex(r, c, -1, -1);
				} else {
					workingGrid[r][c] = new TupleValueIndex(r, c, puzzleGrid[r][c].index, -1);
				}
			}
		}
		return workingGrid;
	}
	
	public boolean compareGrids(TupleValueIndex[][] first, TupleValueIndex[][] second) {
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
	
	public static void printGrid(TupleValueIndex[][] puzzleGrid){
		for(int r = 0; r < puzzleGrid.length; r++){
			for (int c = 0; c < puzzleGrid[r].length; c++) {
				System.out.print(puzzleGrid[r][c].value + " ");
			}
			System.out.println();
		}
	}
	
	public static void printMatchedSlots(Array<TupleValueIndex> tuples) {
		for (int i = 0; i < tuples.size; i++) {
			System.out.println(i + "=[" + tuples.get(i).getR() + "," + tuples.get(i).getC() + "]=" + tuples.get(i).getValue());
		}
	}
}
