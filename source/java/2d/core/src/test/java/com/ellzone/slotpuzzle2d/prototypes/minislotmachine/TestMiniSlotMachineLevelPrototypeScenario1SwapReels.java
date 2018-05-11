package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
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
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class} )


public class TestMiniSlotMachineLevelPrototypeScenario1SwapReels {
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private static final String REEL_TILES_FIELD_NAME = "reelTiles";

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private ReelTile reelTileAMock, reelTileBMock, deletedReelMock;
    private LevelCreatorScenario1 levelCreatorMock;
    private Array<ReelTile> reelTilesMock;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "swapReelsAboveMe");
    }

    private void setUpEasyMocks() {
        reelTileAMock = createMock(ReelTile.class);
        reelTileBMock = createMock(ReelTile.class);
        levelCreatorMock = createMock(LevelCreatorScenario1.class);
        reelTilesMock = createMock(Array.class);
        deletedReelMock = createMock(ReelTile.class);
    }

    @After
    public void tearDown() {
        reelTileAMock = null;
        reelTileBMock = null;
        levelCreatorMock = null;
        reelTileAMock = null;
        deletedReelMock = null;
    }

    @Test
    public void testSwapReelsReelTileAReelTileB() throws Exception {
        setFields();
        setUpExpectations();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeScenario1,
                "swapReels",
                reelTileAMock,
                reelTileBMock);
        verifyAll();
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, REEL_TILES_FIELD_NAME, reelTilesMock);
    }

    private void setUpExpectations() {
        expect(reelTileAMock.getDestinationY()).andReturn(80.0f);
        expect(reelTileBMock.getDestinationX()).andReturn((40.0f));
        expect(reelTileBMock.getDestinationY()).andReturn(120.0f);
        expect(levelCreatorMock.findReel(40, 160)).andReturn(0);
        expect(reelTilesMock.get(0)).andReturn(deletedReelMock);
        expect(reelTileBMock.getDestinationY()).andReturn(120.0f);
        reelTileAMock.setDestinationY(160.0f);
        expect(reelTileBMock.getDestinationY()).andReturn(120.0f);
        reelTileAMock.setY(160.0f);
        reelTileAMock.unDeleteReelTile();
        deletedReelMock.setDestinationY(80.0f);
        deletedReelMock.setY(80.0f);
    }

    private void replayAll() {
        replay(reelTileAMock,
               reelTileBMock,
               partialMockMiniSlotMachineLevelPrototypeScenario1,
               reelTilesMock,
               deletedReelMock
        );
    }

    private void verifyAll() {
        verify(reelTileAMock,
               reelTileBMock,
               partialMockMiniSlotMachineLevelPrototypeScenario1,
               reelTilesMock,
               deletedReelMock
        );
    }
}
