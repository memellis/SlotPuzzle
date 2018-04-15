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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.testpuzzlegrid.TestPuzzleGridType;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class, Vector2.class, Hud.class } )

public class TestMiniSlotMachineLevelPrototypeScenario1ProcessIsTiledClicked {
    private final static String VIEWPORT_FIELD_NAME = "viewport";
    private final static String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private final static String REELTILES_FIELD_NAME = "reelTiles";
    private final static String ANIMATEDREELS_FIELD_NAME = "animatedReels";
    private final static String PULLLEVELSOUND_FIELD_NAME = "pullLeverSound";
    private final static String REELSPIININGSOUND_FIELD_NAME = "reelSpinningSound";

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private LevelCreatorScenario1 levelCreatorScenario1Mock;
    private Input inputMock;
    private Vector2 vector2Mock;
    private Viewport viewportMock;
    private ReelTileGridValue[][] testPuzzleGrid;
    private Array<ReelTile> reelTilesMock;
    private ReelTile reelTileMock;
    private Array<AnimatedReel> animatedReelsMock;
    private AnimatedReel animatedReelMock;
    private Sound pullLeverSoundMock, reelSpinningSoundMock;

    @Before
    public void setUp() {
        setUpMocks();
        setUpTestData();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "initialiseOverride");
        PowerMock.mockStatic(Hud.class);
    }

    private void setUpCreateMocks() {
        inputMock = createMock(Input.class);
        vector2Mock = createMock(Vector2.class);
        viewportMock = createMock(FitViewport.class);
        levelCreatorScenario1Mock = createMock(LevelCreatorScenario1.class);
        reelTilesMock = createMock(Array.class);
        reelTileMock = createMock(ReelTile.class);
        animatedReelsMock = createMock(Array.class);
        animatedReelMock = createMock(AnimatedReel.class);
        pullLeverSoundMock = createMock(Sound.class);
        reelSpinningSoundMock = createMock(Sound.class);
    }

    private void mockGdx() {
        Gdx.input = inputMock;
    }

    private void setUpMocks() {
        setUpPowerMocks();
        setUpCreateMocks();
        mockGdx();
    }

    @After
    public void tearDown() {
        tearDownMockGdx();
        tearDownCreateMocks();
        tearDownPowerMocks();
    }

    private void tearDownCreateMocks() {

    }


    private void tearDownMockGdx() {

    }

    private void tearDownPowerMocks() {

    }

    private void setFieldsInClassUnderTest() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, VIEWPORT_FIELD_NAME, viewportMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorScenario1Mock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, REELTILES_FIELD_NAME, reelTilesMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, ANIMATEDREELS_FIELD_NAME, animatedReelsMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, PULLLEVELSOUND_FIELD_NAME, pullLeverSoundMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, REELSPIININGSOUND_FIELD_NAME, reelSpinningSoundMock);
    }

    private void setUpTestData() {
        testPuzzleGrid = new ReelTileGridValue[10][10];
        testPuzzleGrid[0][0] = new ReelTileGridValue(0, 0, 0, 3);
        testPuzzleGrid[0][1] = new ReelTileGridValue(0, 1, 1, 3);
        testPuzzleGrid[0][2] = new ReelTileGridValue(0, 2, 2, 3);
        testPuzzleGrid[0][3] = new ReelTileGridValue(0, 3, 3, -1);

        testPuzzleGrid[1][0] = new ReelTileGridValue(1, 0, 4, -1);
        testPuzzleGrid[1][1] = new ReelTileGridValue(1, 1, 5, -1);
        testPuzzleGrid[1][2] = new ReelTileGridValue(1, 2, 6, -1);
        testPuzzleGrid[1][3] = new ReelTileGridValue(1, 3, 7, -1);

        testPuzzleGrid[2][0] = new ReelTileGridValue(2, 0, 8, 1);
        testPuzzleGrid[2][1] = new ReelTileGridValue(2, 1, 9, 1);
        testPuzzleGrid[2][2] = new ReelTileGridValue(2, 2, 10, 1);
        testPuzzleGrid[2][3] = new ReelTileGridValue(2, 3, 11, 1);

        testPuzzleGrid[3][0] = new ReelTileGridValue(3, 0, 12, -1);
        testPuzzleGrid[3][1] = new ReelTileGridValue(3, 1, 13, -1);
        testPuzzleGrid[3][2] = new ReelTileGridValue(3, 2, 14, 0);
        testPuzzleGrid[3][3] = new ReelTileGridValue(3, 3, 15, 0);
    }

    private void expectationsInGetTileClicked() throws Exception {
        expect(inputMock.getX()).andReturn(2);
        expect(inputMock.getY()).andReturn(2);
        expectNew(Vector2.class,2.0f, 2.0f).andReturn(vector2Mock);
        vector2Mock.x = 2;
        vector2Mock.y = 2;
        expect(viewportMock.unproject(vector2Mock)).andReturn(vector2Mock);
        expectNew(Vector2.class, -3.0f, 8.0f).andReturn(vector2Mock);
        expect(levelCreatorScenario1Mock.populateMatchGrid(reelTilesMock,
                partialMockMiniSlotMachineLevelPrototypeScenario1.GAME_LEVEL_WIDTH,
                partialMockMiniSlotMachineLevelPrototypeScenario1.GAME_LEVEL_HEIGHT))
               .andReturn(testPuzzleGrid);
        expect(reelTilesMock.get(testPuzzleGrid[2][2].index)).andReturn(reelTileMock);
        expect(levelCreatorScenario1Mock.getAnimatedReels()).andReturn(animatedReelsMock);
        expect(animatedReelsMock.get(testPuzzleGrid[2][2].index)).andReturn(animatedReelMock);
        expect(reelTileMock.isReelTileDeleted()).andReturn(false);
        expect(reelTileMock.isSpinning()).andReturn(true);
        expect(animatedReelMock.getDampenedSineState()).andReturn(DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE);
        expect(reelTileMock.getCurrentReel()).andReturn(0);
        reelTileMock.setEndReel(0);
        expect(reelTileMock.getCurrentReel()).andReturn(0);
        Hud.addScore(-1);
        expect(pullLeverSoundMock.play()).andReturn(0L);
        expect(reelSpinningSoundMock.play()).andReturn(0L);
    }

    private void expectations() throws Exception {
        setFieldsInClassUnderTest();
        expectationsInGetTileClicked();
    }

    private void replayAll() {
        replay(partialMockMiniSlotMachineLevelPrototypeScenario1,
                Vector2.class,
                inputMock,
                vector2Mock,
                viewportMock,
                levelCreatorScenario1Mock,
                reelTilesMock,
                reelTileMock,
                animatedReelsMock,
                animatedReelMock,
                pullLeverSoundMock,
                reelSpinningSoundMock);
    }

    private void verifyAll() {
        verify(partialMockMiniSlotMachineLevelPrototypeScenario1,
                inputMock,
                vector2Mock,
                viewportMock,
                levelCreatorScenario1Mock,
                reelTilesMock,
                reelTileMock,
                animatedReelMock,
                animatedReelMock,
                pullLeverSoundMock,
                reelSpinningSoundMock);
    }

    @Test
    public void testProcessIsTileClicked() throws Exception {
        expectations();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeScenario1, "processIsTileClicked");
        verifyAll();
    }
}
