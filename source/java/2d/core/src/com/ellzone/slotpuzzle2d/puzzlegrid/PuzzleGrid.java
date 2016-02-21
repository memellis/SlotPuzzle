package com.ellzone.slotpuzzle2d.puzzlegrid;

public class PuzzleGrid {
	public PuzzleGrid() {
		
	}
	
	public int[][] matchRowSlots(int[][] puzzleGrid) {
		int arraySizeX = puzzleGrid.length;
		int arraySizeY = puzzleGrid[0].length;
		int [][] workingGrid = new int[arraySizeX][arraySizeY];
		
		initialiseGrid(workingGrid);
		int x = 0; 
		while(x < arraySizeX) {
			int y = 0;
			while (y < arraySizeY) {
				if(puzzleGrid[x][y] >= 0) {
					int c = y + 1;
					boolean match = true;
					while (match == true) {
						if (!(c < arraySizeY)) {
							match = false;
						} else {
							if (puzzleGrid[x][y] == puzzleGrid[x][c]) {
								c++;
							} else {
								match = false;
							}
						}
					}
					for (int i = y; i < c; i++) {
						workingGrid[x][i] = c - y;
					}
					y = c - 1;
				}
				y++;
			}
			x++;
		}
		return workingGrid;
	}
	
	public int[][] matchColumnSlots(int[][] puzzleGrid) {
		int arraySizeX = puzzleGrid.length;
		int arraySizeY = puzzleGrid[0].length;
		int [][] workingGrid = new int[arraySizeX][arraySizeY];
			
		initialiseGrid(workingGrid);
		int y = 0; 
		while(y < arraySizeY) {
			int x = 0;
			while (x < arraySizeX) {
				System.out.println("Processing puzzleGrid["+x+"]["+y+"]="+puzzleGrid[x][y]);
				if(puzzleGrid[x][y] >= 0) {
					int c = x + 1;
					boolean match = true;
					while (match == true) {
						if (!(c < arraySizeX)) {
							match = false;
						} else {
							if (puzzleGrid[x][y] == puzzleGrid[c][y]) {
								c++;
							} else {
								match = false;
							}
						}
					}
					for (int i = x; i < c; i++) {
						workingGrid[i][y] = c - x;
					}
					x = c - 1;;
				}
				x++;
			}
			y++;
		}		
		return workingGrid;
	}
	
	public int[][] initialiseGrid(int[][] puzzleGrid) {
		for (int x = 0; x < puzzleGrid.length; x++) {
			for (int y = 0; y < puzzleGrid[x].length; y++) {
				puzzleGrid[x][y] = -1;
			}
		}
		return puzzleGrid;
	}
	
	public boolean compareGrids(int[][] first, int[][] second) {
		if (first.length != second.length) {
			return false;
		}
		for (int x = 0; x < first.length; x++) {
			if (first[x].length != second[x].length) {
				return false;
			}
			for (int y = 0; y < first[x].length; y++) {
				if(first[x][y] != second[x][y]) {
					return false;
				}
			}
		}
		return true;
	}
	
	public void printGrid(int[][] puzzleGrid){
		for(int x = 0; x < puzzleGrid.length; x++){
			for (int y = 0; y < puzzleGrid[x].length; y++) {
				System.out.print(puzzleGrid[x][y] + " ");
			}
			System.out.println();
		}
	}
}
