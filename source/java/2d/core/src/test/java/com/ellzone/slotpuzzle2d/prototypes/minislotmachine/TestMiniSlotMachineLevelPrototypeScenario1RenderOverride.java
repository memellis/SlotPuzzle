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

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.Score;

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

public class TestMiniSlotMachineLevelPrototypeScenario1RenderOverride {
    private static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    private static final String TILE_MAP_RENDERER_FIELD_NAME = "tileMapRenderer";
    private static final String BATCH_FIELD_NAME = "batch";
    private static final String LEVEL_DOOR_FIELD_NAME = "levelDoor";
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private static final String VIEWPORT_FIELD_NAME = "viewport";
    private static final String REEL_BOXES_FIELD_NAME = "reelBoxes";
    private static final String ANIMATED_REELS_FIELD_NAME = "animatedReels";
    private static final String PHYSICS_FIELD_NAME = "physics";
    private static final String HUD_FIELD_NAME = "hud";
    private static final String STAGE_FIELD_NAME = "stage";

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private LevelCreatorScenario1 levelCreatorScenario1Mock;
    private OrthogonalTiledMapRenderer tileMapRendererMock;
    private SpriteBatch batchMock;
    private LevelDoor levelDoorMock;
    private FitViewport viewportMock;
    private Camera cameraMock;
    private Array<Body> reelBoxesMock;
    private Body reelBoxMock;
    private Array animatedReelsMock;
    private AnimatedReel animatedReelMock;
    private ReelTile reelTileMock;
    private Vector2 vector2Mock;
    private PhysicsManagerCustomBodies physicsMock;
    private Hud hudMock;
    private Stage stageMock;

    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "handleInput",
                              "drawPlayingCards");
    }

    private void setUpEasyMocks() {
        setUpLibGDXMocks();
        setUpReelMocks();
        setUpLevelMocks();
        setUpSlotPuzzleMocks();
    }

    private void setUpSlotPuzzleMocks() {
        vector2Mock = createMock(Vector2.class);
        physicsMock = createMock(PhysicsManagerCustomBodies.class);
        hudMock = createMock(Hud.class);
    }

    private void setUpLevelMocks() {
        levelDoorMock = createMock(LevelDoor.class);
        levelCreatorScenario1Mock = createMock(LevelCreatorScenario1.class);
        tileMapRendererMock = createMock(OrthogonalTiledMapRenderer.class);
    }

    private void setUpReelMocks() {
        reelBoxMock = createMock(Body.class);
        reelBoxesMock = new Array<>();
        reelBoxesMock.add(reelBoxMock);
        animatedReelsMock = createMock(Array.class);
        animatedReelMock = createMock(AnimatedReel.class);
        reelTileMock = createMock(ReelTile.class);
    }

    private void setUpLibGDXMocks() {
        batchMock = createMock(SpriteBatch.class);
        viewportMock = createMock(FitViewport.class);
        cameraMock = createMock(Camera.class);
        stageMock = createMock(Stage.class);
    }

    @After
    public void tearDown() {
        tearDownPowerMocks();
        tearDownEasyMocks();
     }

    private void tearDownPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = null;
    }

    private void tearDownEasyMocks() {
        tearDownLibGDXMocks();
        tearDownReelMocks();
        tearDownLevelMocks();
        tearDownSlotPuzzleMocks();
    }

    private void tearDownSlotPuzzleMocks() {
        vector2Mock = null;
        physicsMock = null;
        hudMock = null;
    }

    private void tearDownLevelMocks() {
        levelDoorMock = null;
        levelCreatorScenario1Mock = null;
        tileMapRendererMock = null;
    }

    private void tearDownReelMocks() {
        reelBoxMock = null;
        reelBoxesMock = null;
        animatedReelsMock = null;
        animatedReelMock = null;
        reelTileMock = null;
    }

    private void tearDownLibGDXMocks() {
        batchMock = null;
        viewportMock = null;
        cameraMock = null;
        stageMock = null;
    }

    @Test
    public void testUpRenderOverride() {
        setFields();
        setUpExpectations();
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeScenario1.renderOverride(0.0f);
        verifyAll();
    }

    private void setFields() {
        setLibGDXFields();
        setLevelFields();
        setSlotPuzzleFields();
    }

    private void setSlotPuzzleFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, PHYSICS_FIELD_NAME, physicsMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, HUD_FIELD_NAME, hudMock);
    }

    private void setLevelFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, TILE_MAP_RENDERER_FIELD_NAME, tileMapRendererMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_DOOR_FIELD_NAME, levelDoorMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorScenario1Mock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, REEL_BOXES_FIELD_NAME, reelBoxesMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, ANIMATED_REELS_FIELD_NAME, animatedReelsMock);
    }

    private void setLibGDXFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, BATCH_FIELD_NAME, batchMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, STAGE_FIELD_NAME, stageMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, VIEWPORT_FIELD_NAME, viewportMock);
    }

    private void setUpExpectations() {
        setUpExpectationsRenderOverridePart1();
        setUpExpectationsRenderReelsBoxes();
        setUpExpectationsRenderOverridePart2();
    }

    private void setUpExpectationsRenderOverridePart2() {
        hudMock.stage = stageMock;
        expect(stageMock.getCamera()).andReturn(cameraMock);
        hudMock.stage.draw();
        stageMock.draw();
    }

    private void setUpExpectationsRenderOverridePart1() {
        tileMapRendererMock.render();
        batchMock.begin();
        expect(levelDoorMock.getLevelType()).andReturn(PLAYING_CARD_LEVEL_TYPE);
        expect(levelCreatorScenario1Mock.getScores()).andReturn(new Array<Score>());
        expect(viewportMock.getCamera()).andReturn(cameraMock);
        expect(reelBoxMock.getAngle()).andReturn(1.0f);
    }

    private void setUpExpectationsRenderReelsBoxes() {
        setUpExpectationsRenderReelsBoxesPart1();
        setUPExpectationsRenderReelBoxesPart2();
    }

    private void setUPExpectationsRenderReelBoxesPart2() {
        reelTileMock.setPosition(-20.0f, -20.0f);
        reelTileMock.setOrigin(0, 0);
        reelTileMock.setSize(40, 40);
        reelTileMock.setRotation(MathUtils.radiansToDegrees);
        reelTileMock.draw(batchMock);
        physicsMock.draw(batchMock);
    }

    private void setUpExpectationsRenderReelsBoxesPart1() {
        animatedReelsMock.size = 1;
        expect(animatedReelsMock.get(0)).andReturn(animatedReelMock);
        expect(animatedReelMock.getReel()).andReturn(reelTileMock);
        expect(reelBoxMock.getPosition()).andReturn(vector2Mock).atLeastOnce();
        expect(reelTileMock.isReelTileDeleted()).andReturn(false);
    }

    private void replayAll() {
        replay(tileMapRendererMock,
               levelDoorMock,
               levelCreatorScenario1Mock,
               viewportMock,
               cameraMock,
               reelBoxMock,
               animatedReelsMock,
               animatedReelMock,
               reelTileMock,
               hudMock,
               stageMock);
    }

    private void verifyAll() {
        verify(tileMapRendererMock,
               levelDoorMock,
               levelCreatorScenario1Mock,
               viewportMock,
               cameraMock,
               reelBoxMock,
               animatedReelMock,
               animatedReelsMock,
               reelTileMock,
               hudMock,
               stageMock);
    }
}
