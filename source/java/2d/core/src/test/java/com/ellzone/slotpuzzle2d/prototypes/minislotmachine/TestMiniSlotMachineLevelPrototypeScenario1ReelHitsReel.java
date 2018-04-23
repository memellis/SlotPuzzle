package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridTypeReelTile;
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

import sun.util.resources.cldr.es.CalendarData_es_PY;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.captureFloat;
import static org.easymock.EasyMock.captureInt;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.createNicePartialMock;
import static org.powermock.api.easymock.PowerMock.expectPrivate;
import static org.powermock.api.easymock.PowerMock.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class, PuzzleGridTypeReelTile.class, PhysicsManagerCustomBodies.class} )

public class TestMiniSlotMachineLevelPrototypeScenario1ReelHitsReel {
    private static final String REELBOXES_FIELD_NAME = "reelBoxes";
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private ReelTile reelTileAMock, reelTileBMock;
    private Array reelBoxesMock;
    private Body reelBoxMock;
    private Capture<Float> captureReelSetY;
    private LevelCreatorScenario1 levelCreatorMock;
    private Capture<ReelTile> reelTileCaptureArg1, reelTileCaptureArg2;
    private Capture<Integer> rACapture, cACapture, rBCapture, cBCapture;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCaptures();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "swapReelsAboveMe",
                              "reelsLeftToFall",
                              "processTileHittingTile");
        mockStatic(PuzzleGridTypeReelTile.class);
        mockStatic(PhysicsManagerCustomBodies.class);
    }

    private void setUpEasyMocks() {
        reelTileAMock = createMock(ReelTile.class);
        reelTileBMock = createMock(ReelTile.class);
        reelBoxesMock = createMock(Array.class);
        reelBoxMock = createMock(Body.class);
        levelCreatorMock = createMock(LevelCreatorScenario1.class);
    }

    private void setUpCaptures(){
        captureReelSetY = EasyMock.newCapture();
        reelTileCaptureArg1 = EasyMock.newCapture();
        reelTileCaptureArg2 = EasyMock.newCapture();
        rACapture = EasyMock.newCapture();
        cACapture = EasyMock.newCapture();
        rBCapture = EasyMock.newCapture();
        cBCapture = EasyMock.newCapture();
    }

    @After
    public void tearDown() {

    }

    @Test
    public void testDealWithReelHitsReelProcessReelA() throws Exception {
        setFields();
        setExpects();
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeScenario1.dealWithReelTileHittingReelTile(reelTileAMock, reelTileBMock);
        assertions();
        verifyAll();
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, REELBOXES_FIELD_NAME, reelBoxesMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorMock);
    }

    private void setExpects() throws Exception {
        setExpectsRowColumn();
        setExpectsProcessReelTileHit(reelTileAMock);
        setExpectsProcessReelTileHit(reelTileBMock);
        setExpectsFlashing();
    }

    private void setExpectsRowColumn() {
        expect(PuzzleGridTypeReelTile.getRowFromLevel(10.0f, 9)).andReturn(3);
        expect(reelTileAMock.getDestinationY()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getColumnFromLevel(10.0f)).andReturn(2);
        expect(reelTileAMock.getDestinationX()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getRowFromLevel(10.0f, 9)).andReturn(2);
        expect(reelTileBMock.getDestinationY()).andReturn(10.0f);
        expect(PuzzleGridTypeReelTile.getColumnFromLevel(10.0f)).andReturn(2);
        expect(reelTileBMock.getDestinationX()).andReturn(10.0f);
    }


    private void setExpectsProcessReelTileHit(ReelTile reelTileMock) {
        reelTileMock.setY(captureFloat(captureReelSetY));
        expect(reelTileMock.getDestinationY()).andReturn(10.0f);
        expect(reelBoxesMock.get(0)).andReturn(reelBoxMock);
        expect(reelTileMock.getIndex()).andReturn(0);
        expect(PhysicsManagerCustomBodies.isStopped(reelBoxMock)).andReturn(true);
        expect(levelCreatorMock.getPlayState()).andReturn(PlayScreen.PlayStates.INTRO_SPINNING);
    }

    private void setExpectsFlashing() throws Exception {
        expect(levelCreatorMock.getPlayState()).andReturn(PlayScreen.PlayStates.INTRO_FLASHING).times(2);
        expectPrivate(partialMockMiniSlotMachineLevelPrototypeScenario1,
                "processTileHittingTile",
                            capture(reelTileCaptureArg1),
                            capture(reelTileCaptureArg2),
                            captureInt(rACapture),
                            captureInt(cACapture),
                            captureInt(rBCapture),
                            captureInt(cBCapture));
    }

    private void replayAll() {
        PowerMock.replay(PuzzleGridTypeReelTile.class,
                         partialMockMiniSlotMachineLevelPrototypeScenario1,
                         reelBoxesMock,
                         reelTileAMock,
                         reelTileBMock,
                         PhysicsManagerCustomBodies.class,
                         levelCreatorMock);
    }

    private void verifyAll() {
        PowerMock.verify(PuzzleGridTypeReelTile.class,
                         partialMockMiniSlotMachineLevelPrototypeScenario1,
                         reelBoxesMock,
                         reelTileAMock,
                         reelTileBMock,
                         PhysicsManagerCustomBodies.class,
                         levelCreatorMock);
    }

    private void assertions() {
        assertThat(reelTileCaptureArg1.getValue(), is(equalTo(reelTileAMock)));
        assertThat(reelTileCaptureArg2.getValue(), is(equalTo(reelTileBMock)));
        assertThat(rACapture.getValue(), is(equalTo(3)));
        assertThat(cACapture.getValue(), is(equalTo(2)));
        assertThat(rBCapture.getValue(), is(equalTo(2)));
        assertThat(cBCapture.getValue(), is(equalTo(2)));
    }
}
