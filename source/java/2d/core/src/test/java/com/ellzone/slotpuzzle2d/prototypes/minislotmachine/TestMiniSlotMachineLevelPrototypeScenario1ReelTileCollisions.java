package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.captureBoolean;
import static org.easymock.EasyMock.captureInt;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.*;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class, PuzzleGridTypeReelTile.class} )

public class TestMiniSlotMachineLevelPrototypeScenario1ReelTileCollisions {
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private static final String GAME_LEVEL_HEIGHT_FIELD_NAME = "GAME_LEVEL_HEIGHT";

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private ReelTile reelTileMock;
    private LevelCreatorScenario1 levelCreatorScenario1Mock;
    private Capture<Boolean> captureLevelCreatorSetHitSinkBottomArgument;
    private Capture<ReelTile> captureSwapReelsAboveArgument;
    private Capture<Integer> captureReelsLeftToFallArgumentRow, captureReelsLeftToFallArgumentCol;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCapture();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "swapReelsAboveMe",
                "reelsLeftToFall");
        mockStatic(PuzzleGridTypeReelTile.class);
    }

    private void setUpEasyMocks() {
        reelTileMock = createMock(ReelTile.class);
        levelCreatorScenario1Mock = createMock(LevelCreatorScenario1.class);
    }

    private void setUpCapture() {
        captureLevelCreatorSetHitSinkBottomArgument = EasyMock.newCapture();
        captureSwapReelsAboveArgument = EasyMock.newCapture();
        captureReelsLeftToFallArgumentRow = EasyMock.newCapture();
        captureReelsLeftToFallArgumentCol = EasyMock.newCapture();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testDealWithHitSinkBottomIntroSpinning() throws Exception {
        setFields();
        setExpects(PlayScreen.PlayStates.INTRO_SPINNING);
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeScenario1.dealWithHitSinkBottom(reelTileMock);
        assertThat(captureLevelCreatorSetHitSinkBottomArgument.getValue(), is(true));
        verifyAll();
    }

    @Test
    public void testDealWithHitSinkBottomReelsFlashing() throws Exception {
        setFields();
        setExpects(PlayScreen.PlayStates.INTRO_FLASHING);
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeScenario1.dealWithHitSinkBottom(reelTileMock);
        assertThat(captureSwapReelsAboveArgument.getValue(), is(reelTileMock));
        assertThat(captureReelsLeftToFallArgumentRow.getValue(), is(2));
        assertThat(captureReelsLeftToFallArgumentCol.getValue(), is(2));
        verifyAll();
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorScenario1Mock);
    }

    private void setExpects(PlayScreen.PlayStates playState) throws Exception {
        expect(levelCreatorScenario1Mock.getPlayState()).andReturn(playState);
        if (playState == PlayScreen.PlayStates.INTRO_SPINNING) {
            levelCreatorScenario1Mock.setHitSinkBottom(captureBoolean(captureLevelCreatorSetHitSinkBottomArgument));
        }
        expect(levelCreatorScenario1Mock.getPlayState()).andReturn(playState);
        expect(levelCreatorScenario1Mock.getPlayState()).andReturn(playState);
        expectIsFlashing(playState);
    }

    private void expectIsFlashing(PlayScreen.PlayStates playState) throws Exception {
        if ((playState == PlayScreen.PlayStates.INTRO_FLASHING) |
            (playState == PlayScreen.PlayStates.REELS_FLASHING))
            expectFlashing();

    }

    private void expectFlashing() throws Exception {
        expectrc();
        expectSwapReelsAboveMe();
    }

    private void expectSwapReelsAboveMe() throws Exception {
        expect(levelCreatorScenario1Mock.findReel(10, 120)).andReturn(0);
        expect(reelTileMock.getDestinationX()).andReturn(10.0f);
        expectPrivate(partialMockMiniSlotMachineLevelPrototypeScenario1, "swapReelsAboveMe", capture(captureSwapReelsAboveArgument));
        expectPrivate(partialMockMiniSlotMachineLevelPrototypeScenario1, "reelsLeftToFall", captureInt(captureReelsLeftToFallArgumentRow), captureInt(captureReelsLeftToFallArgumentCol));
    }

    private void expectrc() {
        expect(PuzzleGridTypeReelTile.getRowFromLevel(10.0f, 9)).andReturn(2);
        expect(reelTileMock.getDestinationY()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getColumnFromLevel(10.0f)).andReturn(2);
        expect(reelTileMock.getDestinationX()).andReturn(10.0f);
    }

    private void replayAll() {
        replay(PuzzleGridTypeReelTile.class,
               partialMockMiniSlotMachineLevelPrototypeScenario1,
               reelTileMock,
               levelCreatorScenario1Mock);
    }

    private void verifyAll() {
        verify(partialMockMiniSlotMachineLevelPrototypeScenario1,
               reelTileMock,
               levelCreatorScenario1Mock);
    }
}
