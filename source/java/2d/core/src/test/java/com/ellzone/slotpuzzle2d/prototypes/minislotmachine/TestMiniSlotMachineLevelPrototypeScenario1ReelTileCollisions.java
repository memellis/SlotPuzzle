package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.anyBoolean;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.captureBoolean;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.replay;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.expectNew;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class} )

public class TestMiniSlotMachineLevelPrototypeScenario1ReelTileCollisions {
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private ReelTile reelTileMock;
    private LevelCreatorScenario1 levelCreatorScenario1Mock;
    private Capture<Boolean> captureLevelCreatorSetHitSinkBottomArgument;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCapture();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "handleInput");
    }

    private void setUpEasyMocks() {
        reelTileMock = createMock(ReelTile.class);
        levelCreatorScenario1Mock = createMock(LevelCreatorScenario1.class);
    }

    private void setUpCapture() {
        captureLevelCreatorSetHitSinkBottomArgument = EasyMock.newCapture();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testDealWithHitSinkBottom() {
        setFields();
        setExpects();
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeScenario1.dealWithHitSinkBottom(reelTileMock);
        assertThat(captureLevelCreatorSetHitSinkBottomArgument.getValue(),is(true));
        verifyAll();
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorScenario1Mock);
    }

    private void setExpects() {
        expect(levelCreatorScenario1Mock.getPlayState()).andReturn(PlayScreen.PlayStates.INTRO_SPINNING).atLeastOnce();
        levelCreatorScenario1Mock.setHitSinkBottom(captureBoolean(captureLevelCreatorSetHitSinkBottomArgument));
    }

    private void replayAll() {
        replay(reelTileMock, levelCreatorScenario1Mock);
    }

    private void verifyAll() {
        verify(reelTileMock, levelCreatorScenario1Mock);
    }
}
