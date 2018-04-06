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

package com.ellzone.slotpuzzle2d.puzzlegrid;

import com.badlogic.gdx.utils.Array;

public class PuzzleGrid {
	
	public int[][] matchRowSlots(int[][] puzzleGrid) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		int [][] workingGrid = new int[arraySizeR][arraySizeC];
		
		initialiseGrid(workingGrid);
		int r = 0; 
		while(r < arraySizeR) {
			int c = 0;
			while (c < arraySizeC) {
				if(puzzleGrid[r][c] >= 0) {
					int co = c + 1;
					boolean match = true;
					while (match == true) {
						if (!(co < arraySizeC)) {
							match = false;
						} else {
							if (puzzleGrid[r][c] == puzzleGrid[r][co]) {
								co++;
							} else {
								match = false;
							}
						}
					}
					for (int i = c; i < co; i++) {
						workingGrid[r][i] = co - c;
					}
					c = co - 1;
				}
				c++;
			}
			r++;
		}
		return workingGrid;
	}
	
	public int[][] matchColumnSlots(int[][] puzzleGrid) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		int [][] workingGrid = new int[arraySizeR][arraySizeC];
			
		initialiseGrid(workingGrid);
		int c = 0; 
		while(c < arraySizeC) {
			int r = 0;
			while (r < arraySizeR) {
				if(puzzleGrid[r][c] >= 0) {
					int co = r + 1;
					boolean match = true;
					while (match == true) {
						if (!(co < arraySizeR)) {
							match = false;
						} else {
							if (puzzleGrid[r][c] == puzzleGrid[co][c]) {
								co++;
							} else {
								match = false;
							}
						}
					}
					for (int i = r; i < co; i++) {
						workingGrid[i][c] = co - r;
					}
					r = co - 1;;
				}
				r++;
			}
			c++;
		}		
		return workingGrid;
	}
	
	public Array<Tuple> matchGridSlots(int[][] puzzleGrid) {
		int[][] matchedGridRows = matchRowSlots(puzzleGrid);
		int[][] matchedGridCols = matchColumnSlots(puzzleGrid);
		Array<Tuple> matchedSlots = new Array<Tuple>();
		
		matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
		matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);
		
		return matchedSlots;
	}
	
	private Array<Tuple> getMatchedRowSlots(int[][] puzzleGrid, Array<Tuple> matchedSlots) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		
		for(int r = 0; r < arraySizeR; r++) {
			for(int c = 0; c < arraySizeC; c++) {
				if(puzzleGrid[r][c] > 1) {
					matchedSlots.add(new Tuple(r, c, puzzleGrid[r][c]));
				}
			}
		}
		return matchedSlots;
	}

	private Array<Tuple> getMatchedColSlots(int[][] puzzleGrid, Array<Tuple> matchedSlots) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		
		for(int c = 0; c < arraySizeC; c++) {
			for(int r = 0; r < arraySizeR; r++) {
				if(puzzleGrid[r][c] > 1) {
					matchedSlots.add(new Tuple(r, c, puzzleGrid[r][c]));
				}
			}
		}
		return matchedSlots;
	}

	
	public int[][] initialiseGrid(int[][] puzzleGrid) {
		for (int r = 0; r < puzzleGrid.length; r++) {
			for (int c = 0; c < puzzleGrid[r].length; c++) {
				puzzleGrid[r][c] = -1;
			}
		}
		return puzzleGrid;
	}
	
	public boolean compareGrids(int[][] first, int[][] second) {
		if (first.length != second.length) {
			return false;
		}
		for (int r = 0; r < first.length; r++) {
			if (first[r].length != second[r].length) {
				return false;
			}
			for (int c = 0; c < first[r].length; c++) {
				if(first[r][c] != second[r][c]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public static void printGrid(int[][] puzzleGrid){
		for(int r = 0; r < puzzleGrid.length; r++){
			for (int c = 0; c < puzzleGrid[r].length; c++) {
				System.out.print(puzzleGrid[r][c] + " ");
			}
			System.out.println();
		}
	}
	
	public static void printMatchedSlots(Array<Tuple> tuples) {
		System.out.println("PuzzleGrid printMatchedSlots");
		for (int i = 0; i < tuples.size; i++) {
			System.out.println(i + "=[" + tuples.get(i).getR() + "," + tuples.get(i).getC() + "]=" + tuples.get(i).getValue());
		}
	}
}
