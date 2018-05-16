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
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.utils.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class, Vector2.class, Hud.class, Random.class } )

public class TestMiniSlotMachineLevelPrototypeScenario1ProcessIsTiledClicked {
    private final static String VIEWPORT_FIELD_NAME = "viewport";
    private final static String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private final static String REELTILES_FIELD_NAME = "reelTiles";
    private final static String ANIMATEDREELS_FIELD_NAME = "animatedReels";
    private final static String PULLLEVELSOUND_FIELD_NAME = "pullLeverSound";
    private final static String REELSPIININGSOUND_FIELD_NAME = "reelSpinningSound";
    private final static String REELS_FIELD_NAME = "reels";

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
    private boolean spinning;
    private Random randomMock;
    private Reels reelsMock;
    private Sprite spriteMock;

    @Before
    public void setUp() {
        setUpMocks();
        setUpTestData();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "initialiseOverride");
        PowerMock.mockStatic(Hud.class);
        PowerMock.mockStatic(Random.class);
    }

    private void setUpCreateMocks() {
        setUpLibGDXMocks();
        levelcreatorMock();
        setUpReelMocks();
    }

    private void levelcreatorMock() {
        levelCreatorScenario1Mock = createMock(LevelCreatorScenario1.class);
    }

    private void setUpReelMocks() {
        randomMock = createMock(Random.class);
        reelTilesMock = createMock(Array.class);
        reelTileMock = createMock(ReelTile.class);
        animatedReelsMock = createMock(Array.class);
        animatedReelMock = createMock(AnimatedReel.class);
        reelsMock = createMock(Reels.class);
    }

    private void setUpLibGDXMocks() {
        inputMock = createMock(Input.class);
        vector2Mock = createMock(Vector2.class);
        viewportMock = createMock(FitViewport.class);
        pullLeverSoundMock = createMock(Sound.class);
        reelSpinningSoundMock = createMock(Sound.class);
        spriteMock = createMock(Sprite.class);
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
        inputMock = null;
        vector2Mock = null;
        viewportMock = null;
        levelCreatorScenario1Mock = null;
        reelTilesMock = null;
        reelTileMock = null;
        animatedReelsMock = null;
        animatedReelMock = null;
        pullLeverSoundMock = null;
        reelSpinningSoundMock = null;
        randomMock = null;
        reelsMock = null;
    }

    private void tearDownMockGdx() {
        inputMock = null;
    }

    private void tearDownPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = null;
    }

    private void setFieldsInClassUnderTest() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, VIEWPORT_FIELD_NAME, viewportMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorScenario1Mock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, REELTILES_FIELD_NAME, reelTilesMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, ANIMATEDREELS_FIELD_NAME, animatedReelsMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, PULLLEVELSOUND_FIELD_NAME, pullLeverSoundMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, REELSPIININGSOUND_FIELD_NAME, reelSpinningSoundMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, REELS_FIELD_NAME, reelsMock);
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

    private void expectationsProcessIsGetTileClicked() throws Exception {
        expectationsGetTileClicked();
        expectationsProcessTileClicked();
        expectationsProcessReelClicked();
        if (spinning)
            expectationssetEndReelWithCurrentReel();
        else
            expectationsStartSpinning();
    }

    private void expectationssetEndReelWithCurrentReel() {
        expect(reelTileMock.getCurrentReel()).andReturn(0);
        reelTileMock.setEndReel(0);
        expect(reelTileMock.getCurrentReel()).andReturn(0);
        Hud.addScore(-1);
        expect(pullLeverSoundMock.play()).andReturn(0L);
        expect(reelSpinningSoundMock.play()).andReturn(0L);
    }

    private void expectationsStartSpinning() {
        expect(Random.getInstance()).andReturn(randomMock);
        expect(randomMock.nextInt(0)).andReturn(0);
        expect(reelsMock.getReels()).andReturn(new Sprite[] {spriteMock});
        reelTileMock.startSpinning();
        reelTileMock.setEndReel(0);
        expect(levelCreatorScenario1Mock.getNumberOfReelsSpinning()).andReturn(1);
        levelCreatorScenario1Mock.setNumberOfReelsSpinning(2);
        reelTileMock.setSy(0);
        animatedReelMock.reinitialise();
        Hud.addScore(-1);
        expect(pullLeverSoundMock.play()).andReturn(0L);
    }

    private void expectationsProcessReelClicked() {
        expect(reelTileMock.isReelTileDeleted()).andReturn(false);
        expect(reelTileMock.isSpinning()).andReturn(spinning);
        if (spinning)
            expect(animatedReelMock.getDampenedSineState()).andReturn(DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE);
        else
            expect(reelTileMock.getFlashTween()).andReturn(false);
        expect(reelTileMock.isSpinning()).andReturn(spinning);
    }

    private void expectationsProcessTileClicked() {
        expect(levelCreatorScenario1Mock.populateMatchGrid(reelTilesMock,
                partialMockMiniSlotMachineLevelPrototypeScenario1.GAME_LEVEL_WIDTH,
                partialMockMiniSlotMachineLevelPrototypeScenario1.GAME_LEVEL_HEIGHT))
                .andReturn(testPuzzleGrid);
        expect(reelTilesMock.get(testPuzzleGrid[2][2].index)).andReturn(reelTileMock);
        expect(levelCreatorScenario1Mock.getAnimatedReels()).andReturn(animatedReelsMock);
        expect(animatedReelsMock.get(testPuzzleGrid[2][2].index)).andReturn(animatedReelMock);
    }

    private void expectationsGetTileClicked() throws Exception {
        expect(inputMock.getX()).andReturn(2);
        expect(inputMock.getY()).andReturn(2);
        expectNew(Vector2.class,2.0f, 2.0f).andReturn(vector2Mock);
        vector2Mock.x = 2;
        vector2Mock.y = 2;
        expect(viewportMock.unproject(vector2Mock)).andReturn(vector2Mock);
        expectNew(Vector2.class, -3.0f, 8.0f).andReturn(vector2Mock);
    }

    private void expectations() throws Exception {
        setFieldsInClassUnderTest();
        expectationsProcessIsGetTileClicked();
    }

    private void replayAll() {
        replay(partialMockMiniSlotMachineLevelPrototypeScenario1,
               Vector2.class,
               Random.class,
               inputMock,
               vector2Mock,
               viewportMock,
               levelCreatorScenario1Mock,
               reelTilesMock,
               reelTileMock,
               animatedReelsMock,
               animatedReelMock,
               pullLeverSoundMock,
               reelSpinningSoundMock,
               reelsMock,
               randomMock);
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
               reelSpinningSoundMock,
               reelsMock,
               randomMock);
    }

    private void setSpinning(boolean spinning) {
        this.spinning = spinning;
    }

    @Test
    public void testProcessIsTileClicked() throws Exception {
        setSpinning(true);
        expectations();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeScenario1, "processIsTileClicked");
        assertThat(reelTileMock.isSpinning(), is(true));
        verifyAll();
    }

    @Test
    public void testProcessIsTileClickedStartReelSpinning() throws Exception {
        setSpinning(false);
        expectations();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeScenario1, "processIsTileClicked");
        assertThat(reelTileMock.isSpinning(), is(false));
        verifyAll();
    }
}
