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
                                if (puzzleGrid[r][c] != null) {
                                    if (puzzleGrid[r][co] != null) {
                                        if (puzzleGrid[r][c].value == puzzleGrid[r][co].value) {
                                            co++;
                                        } else {
                                            match = false;
                                        }
                                    } else {
                                        Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "r=" + r + " c=" + c + " is Null - ignoring this tile.");
                                        match = false;
                                    }
                                } else {
                                    Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "r=" + r + " co=" + co + " is Null - ignoring this tile.");
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
                                if (puzzleGrid[r][c] != null) {
                                    if (puzzleGrid[r][co] != null) {
                                        if (puzzleGrid[r][c].value == puzzleGrid[co][c].value) {
                                            co++;
                                        } else {
                                            match = false;
                                        }
                                    } else {
                                        Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "r=" + r + " c=" + c + " is Null - ignoring this tile.");
                                        System.out.println("r=" + r + " c=" + c + " is Null - ignoring this tile.");
                                        match = false;
                                    }
                                } else {
                                    Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "r=" + r + " co=" + co + " is Null - ignoring this tile.");
                                    System.out.println("r=" + r + " co=" + co + " is Null - ignoring this tile.");
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
	
	public boolean anyLonelyTiles(TupleValueIndex[][] puzzleGrid) {
		TupleValueIndex[][] matchedGridRows = matchRowSlots(puzzleGrid);
		TupleValueIndex[][] matchedGridCols = matchColumnSlots(puzzleGrid);
		Array<TupleValueIndex> matchedSlots = new Array<TupleValueIndex>();
		
		matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
		matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);
	
		TupleValueIndex[][] workingGrid = crossOffMatchSlots(matchedSlots, puzzleGrid);	
		
		matchedGridRows = findLonelyRowTiles(workingGrid);
		matchedGridCols = findLonelyColumnTiles(workingGrid);
		matchedSlots = new Array<TupleValueIndex>();
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

	public Array<TupleValueIndex> getLonelyTiles(TupleValueIndex[][] puzzleGrid) {
		TupleValueIndex[][] matchedGridRows = matchRowSlots(puzzleGrid);
		TupleValueIndex[][] matchedGridCols = matchColumnSlots(puzzleGrid);
		Array<TupleValueIndex> matchedSlots = new Array<TupleValueIndex>();
		
		matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
		matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);
	
		TupleValueIndex[][] workingGrid = crossOffMatchSlots(matchedSlots, puzzleGrid);	
		
		matchedGridRows = findLonelyRowTiles(workingGrid);
		matchedGridCols = findLonelyColumnTiles(workingGrid);
		matchedSlots = new Array<TupleValueIndex>();
		matchedSlots = getMatchedRowSlots(matchedGridRows, matchedSlots);
		matchedSlots = getMatchedColSlots(matchedGridCols, matchedSlots);
	
		workingGrid = crossOffMatchSlots(matchedSlots, workingGrid);

		matchedSlots = new Array<TupleValueIndex>();
		for (int r = 0; r < workingGrid.length; r++) {
			for (int c = 0; c < workingGrid[0].length; c++) {
				if (workingGrid[r][c].value >= 0) {
					matchedSlots.add(new TupleValueIndex(r, c, workingGrid[r][c].index, workingGrid[r][c].value));
				}
			}
		}
		return matchedSlots;
	}

	private TupleValueIndex[][] findLonelyRowTiles(TupleValueIndex[][] puzzleGrid) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		TupleValueIndex[][] workingGrid = new TupleValueIndex[arraySizeR][arraySizeC];

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
                                        } else {
                                            match = false;
                                        }
                                    } else {
                                        Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "r=" + r + " c=" + c + " is Null - ignoring this tile.");
                                        match = false;
                                    }
                                } else {
                                    Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "r=" + r + " co=" + co + " is Null - ignoring this tile.");
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
	
	private TupleValueIndex[][] findLonelyColumnTiles(TupleValueIndex[][] puzzleGrid) {
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
                                if (puzzleGrid[r][c] != null) {
                                    if (puzzleGrid[r][co] != null) {
                                        if (puzzleGrid[co][c].value >=0) {
                                            co++;
                                        } else {
                                            match = false;
                                        }
                                    } else {
                                        Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "r=" + r + " c=" + c + " is Null - ignoring this tile.");
                                        System.out.println("r=" + r + " c=" + c + " is Null - ignoring this tile.");
                                        match = false;
                                    }
                                } else {
                                    Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "r=" + r + " co=" + co + " is Null - ignoring this tile.");
                                    System.out.println("r=" + r + " co=" + co + " is Null - ignoring this tile.");
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

	private TupleValueIndex[][] crossOffMatchSlots(Array<TupleValueIndex> matchedSlots, TupleValueIndex[][] puzzleGrid) {
		TupleValueIndex[][] workingGrid = copyGrid(puzzleGrid);
		for (TupleValueIndex matchSlot : matchedSlots) {
			workingGrid[matchSlot.r][matchSlot.c].value = -1;
		}
		return workingGrid;
	}
	
	public static void printGrid(TupleValueIndex[][] puzzleGrid){
		for(int r = 0; r < puzzleGrid.length; r++){
			for (int c = 0; c < puzzleGrid[r].length; c++) {
				if (puzzleGrid[r][c].value == -1) {
					System.out.print(puzzleGrid[r][c].value + " ");
				} else {
					System.out.print(" " + puzzleGrid[r][c].value + " ");					
				}
			}
			System.out.println();
		}
	}
	
	private static TupleValueIndex[][] copyGrid(TupleValueIndex[][] puzzleGrid) {
		int arraySizeR = puzzleGrid.length;
		int arraySizeC = puzzleGrid[0].length;
		TupleValueIndex[][] targetGrid = new TupleValueIndex[arraySizeR][arraySizeC];

		for(int r = 0; r < puzzleGrid.length; r++){
			for (int c = 0; c < puzzleGrid[r].length; c++) {
				targetGrid[r][c] = new TupleValueIndex(puzzleGrid[r][c].r,
						                               puzzleGrid[r][c].c,
						                               puzzleGrid[r][c].index,
						                               puzzleGrid[r][c].value); 
			}
		}
		return targetGrid;
	}
	
	public static void printMatchedSlots(Array<TupleValueIndex> tuples) {
		for (int i = 0; i < tuples.size; i++) {
			System.out.println(i + "=[" + tuples.get(i).getR() + "," + tuples.get(i).getC() + "]=" + tuples.get(i).getValue());
		}
	}
}
