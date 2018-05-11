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

package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;


import static org.easymock.EasyMock.expect;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class,
                  PuzzleGridType.class,
                  PuzzleGridTypeReelTile.class} )

public class TestMiniSlotMachineLevelPrototypeScenario1SwapReelsAboveMe {

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private ReelTile reelTileAMock, reelTileBMock;
    private LevelCreatorScenario1 levelCreatorMock;
    private Array<ReelTile> reelTilesMock;
    private ReelTileGridValue[][] testPuzzleGrid;
    private ReelTileGridValue[] reelTileGridValues;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
        "swapReels");
        mockStatic(PuzzleGridType.class);
        mockStatic(PuzzleGridTypeReelTile.class);
    }

    private void setUpEasyMocks() {
        reelTileAMock = createMock(ReelTile.class);
        reelTileBMock = createMock(ReelTile.class);
        levelCreatorMock = createMock(LevelCreatorScenario1.class);
        reelTilesMock = createMock(Array.class);
    }

    @After
    public void tearDown() {
        tearDownSetUpPowerMocks();
        tearDownEasyMocks();
    }

    private void tearDownSetUpPowerMocks(){
        partialMockMiniSlotMachineLevelPrototypeScenario1 = null;
    }

    private void tearDownEasyMocks() {
        reelTileAMock = null;
        reelTileBMock = null;
    }

    @Test
    public void testSwapReelsAboveMeReelTileAReelTileB() throws Exception {
        setUpFields();
        setUpTestData();
        setUpExpectations();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeScenario1,
                "swapReelsAboveMe",
                reelTileAMock,
                reelTileBMock);
        verifyAll();
    }

    @Test
    public void whenSwapReelsAboveMeMoreThanOneReel_thenSwapMultipleReels() {

    }

    private void setUpTestData() {
        testPuzzleGrid = new ReelTileGridValue[10][10];
        setUpTestPuzzleGridRowZero();
        setUpTestPuzzleGridRowOne();
        setUpTestPuzzleGridRowTwo();
        setUpTestPuzzleGridRowThree();
        reelTileGridValues = new ReelTileGridValue[0];
    }

    private void setUpTestPuzzleGridRowZero() {
        testPuzzleGrid[0][0] = new ReelTileGridValue(0, 0, 0, 3);
        testPuzzleGrid[0][1] = new ReelTileGridValue(0, 1, 1, 3);
        testPuzzleGrid[0][2] = new ReelTileGridValue(0, 2, 2, 3);
        testPuzzleGrid[0][3] = new ReelTileGridValue(0, 3, 3, -1);
    }

    private void setUpTestPuzzleGridRowOne() {
        testPuzzleGrid[1][0] = new ReelTileGridValue(1, 0, 4, -1);
        testPuzzleGrid[1][1] = new ReelTileGridValue(1, 1, 5, -1);
        testPuzzleGrid[1][2] = new ReelTileGridValue(1, 2, 6, -1);
        testPuzzleGrid[1][3] = new ReelTileGridValue(1, 3, 7, -1);
    }

    private void setUpTestPuzzleGridRowTwo() {
        testPuzzleGrid[2][0] = new ReelTileGridValue(2, 0, 8, 1);
        testPuzzleGrid[2][1] = new ReelTileGridValue(2, 1, 9, 1);
        testPuzzleGrid[2][2] = new ReelTileGridValue(2, 2, 10, 1);
        testPuzzleGrid[2][3] = new ReelTileGridValue(2, 3, 11, 1);
    }

    private void setUpTestPuzzleGridRowThree() {
        testPuzzleGrid[3][0] = new ReelTileGridValue(3, 0, 12, -1);
        testPuzzleGrid[3][1] = new ReelTileGridValue(3, 1, 13, -1);
        testPuzzleGrid[3][2] = new ReelTileGridValue(3, 2, 14, 0);
        testPuzzleGrid[3][3] = new ReelTileGridValue(3, 3, 15, 0);
    }

    private void setUpFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1,
                "reelTiles",
                reelTilesMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1,
                "levelCreator",
                 levelCreatorMock);
    }

    private void setUpExpectations() {
        setUpReelsAboveMeExpectatiosn();
    }

    private void setUpReelsAboveMeExpectatiosn() {
        expect(levelCreatorMock.populateMatchGrid(reelTilesMock,
                MiniSlotMachineLevelPrototypeScenario1.GAME_LEVEL_WIDTH,
                MiniSlotMachineLevelPrototypeScenario1.GAME_LEVEL_HEIGHT)).andReturn(testPuzzleGrid);
        expect(reelTileAMock.getDestinationY()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getRowFromLevel(10.0f,
                MiniSlotMachineLevelPrototypeScenario1.GAME_LEVEL_HEIGHT)).andReturn(2);
        expect(reelTileAMock.getDestinationX()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getColumnFromLevel(10.0f)).andReturn(2);
        expect(PuzzleGridType.getReelsAboveMe(testPuzzleGrid,
                2,
                2)).andReturn(reelTileGridValues);
    }

    private void replayAll() {
        replay(PuzzleGridType.class,
               PuzzleGridTypeReelTile.class,
               levelCreatorMock,
               partialMockMiniSlotMachineLevelPrototypeScenario1,
               reelTileAMock,
               reelTileBMock);
    }

    private void verifyAll() {
        verify(PuzzleGridType.class,
               PuzzleGridTypeReelTile.class,
               levelCreatorMock,
               partialMockMiniSlotMachineLevelPrototypeScenario1,
               reelTileAMock,
               reelTileBMock);
    }
 }
