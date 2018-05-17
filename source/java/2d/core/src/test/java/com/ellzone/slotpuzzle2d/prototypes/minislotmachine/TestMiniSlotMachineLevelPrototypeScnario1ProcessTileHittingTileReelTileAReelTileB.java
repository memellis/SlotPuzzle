package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

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

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.captureInt;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createNicePartialMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class} )


public class TestMiniSlotMachineLevelPrototypeScnario1ProcessTileHittingTileReelTileAReelTileB {
    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private ReelTile reelTileAMock, reelTileBMock;
    private Capture<ReelTile> reelTileCaptureA, reelTileCaptureB;
    private Capture<Integer> rCapture, cCapture;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCaptures();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "swapReelsAboveMe",
                "reelsLeftToFall");
    }

    private void setUpEasyMocks() {
        reelTileAMock = createMock(ReelTile.class);
        reelTileBMock = createMock(ReelTile.class);
    }

    private void setUpCaptures() {
        reelTileCaptureA = EasyMock.newCapture();
        reelTileCaptureB = EasyMock.newCapture();
        rCapture = EasyMock.newCapture();
        cCapture = EasyMock.newCapture();
    }

    @After
    public void tearDown() {
        tearDownPowerMocks();
        tearDownEasyMocks();
        tearDownCaptures();
    }

    private void tearDownCaptures() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = null;
    }

    private void tearDownEasyMocks() {
        reelTileAMock = null;
        reelTileBMock = null;
    }

    private void tearDownPowerMocks() {
        reelTileCaptureA = null;
        reelTileCaptureB = null;
        rCapture = null;
        cCapture = null;
    }

    @Test
    public void testProcessReelTileHitReelAGreatherThanReelB() throws Exception {
        setExpectationsReelAGreatherThanReelB();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeScenario1,
                    "processTileHittingTile",
                    reelTileAMock,
                    reelTileBMock,
                    3, 2, 1, 2
                    );
        assertThat(reelTileCaptureA.getValue(), is(equalTo(reelTileBMock)));
        assertThat(reelTileCaptureB.getValue(), is(equalTo(reelTileAMock)));
        assertThat(rCapture.getValue(), is(equalTo(1)));
        assertThat(cCapture.getValue(), is(equalTo(2)));
        verifyAll();
    }

    private void setExpectationsReelAGreatherThanReelB() throws Exception {
        PowerMock.expectPrivate(partialMockMiniSlotMachineLevelPrototypeScenario1,
                "swapReelsAboveMe",
                capture(reelTileCaptureA),
                capture(reelTileCaptureB));
        PowerMock.expectPrivate(partialMockMiniSlotMachineLevelPrototypeScenario1,
                "reelsLeftToFall",
                  captureInt(rCapture),
                  captureInt(cCapture));
    }

    private void replayAll(){
        replay(partialMockMiniSlotMachineLevelPrototypeScenario1);
    }

    private void verifyAll() {
        verify(partialMockMiniSlotMachineLevelPrototypeScenario1);
    }
}
