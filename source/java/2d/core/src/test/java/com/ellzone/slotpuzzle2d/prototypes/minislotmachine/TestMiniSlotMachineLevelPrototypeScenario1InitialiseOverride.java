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


import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.camera.CameraHelper;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

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

import static com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeScenario1.GAME_LEVEL_HEIGHT;
import static com.ellzone.slotpuzzle2d.prototypes.minislotmachine.MiniSlotMachineLevelPrototypeScenario1.GAME_LEVEL_WIDTH;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class, LevelCreatorScenario1.class, CameraHelper.class} )

public class TestMiniSlotMachineLevelPrototypeScenario1InitialiseOverride {
    private static final String ANNOTATION_ASSET_MANAGER_FIELD_NAME = "annotationAssetManager";
    private static final String LEVEL_DOOR_FIELD_NAME = "levelDoor";
    private static final String CARD_DECK_ATLAS_FIELD_NAME = "carddeckAtlas";
    private static final String TWEEN_MANAGER_FIELD_NAME = "tweenManager";
    private static final String PHYSICS_FIELD_NAME = "physics";
    private static final String BATCH_FIELD_NAME = "batch";

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private LevelCreatorScenario1 levelCreatorScenario1Mock;
    private Hud hudMock;
    private OrthographicCamera orthographicCameraMock;
    private AnnotationAssetManager annotationAssetManagerMock;
    private LevelDoor levelDoorMock;
    private TiledMap tiledMapMock;
    private TextureAtlas cardDeckAtlasMock;
    private TweenManager tweenManagerMock;
    private PhysicsManagerCustomBodies physicsMock;
    private SpriteBatch spriteBatchMock;
    private Capture<PlayScreen.PlayStates> playStatesCapture;

    @Before
    public void setUp() {
        setUpMocks();
    }

    private void setUpMocks() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCaptureArguments();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "initialiseReels",
                              "createSlotReelTexture",
                              "getAssets",
                              "getMapProperties",
                              "initialiseLevelDoor",
                              "createPlayScreen",
                              "initialisePhysics"
        );
        mockStatic(CameraHelper.class);
    }

    private void setUpEasyMocks() {
        setUpEasyMocksPart1();
        setUpEasyMocksPart2();
    }

    private void setUpEasyMocksPart2() {
        tiledMapMock = createMock(TiledMap.class);
        cardDeckAtlasMock = createMock(TextureAtlas.class);
        tweenManagerMock = createMock(TweenManager.class);
        physicsMock = createMock(PhysicsManagerCustomBodies.class);
        spriteBatchMock = createMock(SpriteBatch.class);
    }

    private void setUpEasyMocksPart1() {
        levelCreatorScenario1Mock = createMock(LevelCreatorScenario1.class);
        hudMock = createMock(Hud.class);
        orthographicCameraMock = createMock(OrthographicCamera.class);
        annotationAssetManagerMock = createMock(AnnotationAssetManager.class);
        levelDoorMock = createMock(LevelDoor.class);
    }

    private void setUpCaptureArguments() {
        playStatesCapture = EasyMock.newCapture();
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
        levelCreatorScenario1Mock = null;
        hudMock = null;
        orthographicCameraMock = null;
        annotationAssetManagerMock = null;
        levelDoorMock = null;
        tiledMapMock = null;
        cardDeckAtlasMock = null;
        tweenManagerMock = null;
        physicsMock = null;
        spriteBatchMock = null;
    }

    private void expectations() throws Exception {
        expect(CameraHelper.GetCamera(SlotPuzzleConstants.V_WIDTH, SlotPuzzleConstants.V_HEIGHT)).andReturn(orthographicCameraMock);
        expect(annotationAssetManagerMock.get(AssetsAnnotation.MINI_SLOT_MACHINE_LEVEL1)).andReturn(tiledMapMock);
        levelCreatorExpectations();
        hudExpectations();
        levelCreatorScenario1Mock.setPlayState(capture(playStatesCapture));
    }

    private void hudExpectations() throws Exception {
        whenNew(Hud.class).withArguments(spriteBatchMock).thenReturn(hudMock);
        hudMock.setLevelName(null);
        hudMock.startWorldTimer();
    }

    private void levelCreatorExpectations() throws Exception {
        whenNew(LevelCreatorScenario1.class).withArguments(levelDoorMock,
                                                           tiledMapMock,
                                                           annotationAssetManagerMock,
                                                           cardDeckAtlasMock,
                                                           tweenManagerMock,
                                                           physicsMock,
                                                           GAME_LEVEL_WIDTH,
                                                           GAME_LEVEL_HEIGHT,
                                                           PlayScreen.PlayStates.INITIALISING).thenReturn(levelCreatorScenario1Mock);
        levelCreatorScenario1Mock.setPlayState(PlayScreen.PlayStates.INITIALISING);
        expect(levelCreatorScenario1Mock.getReelTiles()).andReturn(null);
        expect(levelCreatorScenario1Mock.getAnimatedReels()).andReturn(null);
        expect(levelCreatorScenario1Mock.getReelBoxes()).andReturn(null);
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, ANNOTATION_ASSET_MANAGER_FIELD_NAME, annotationAssetManagerMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_DOOR_FIELD_NAME, levelDoorMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, CARD_DECK_ATLAS_FIELD_NAME, cardDeckAtlasMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, TWEEN_MANAGER_FIELD_NAME, tweenManagerMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, PHYSICS_FIELD_NAME, physicsMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, BATCH_FIELD_NAME, spriteBatchMock);
    }

    private void replayAll() {
        replay(CameraHelper.class,
               levelCreatorScenario1Mock,
               annotationAssetManagerMock,
               hudMock,
               partialMockMiniSlotMachineLevelPrototypeScenario1);
    }

    private void verifyAll() {
        verify(levelCreatorScenario1Mock,
               annotationAssetManagerMock,
               hudMock,
               partialMockMiniSlotMachineLevelPrototypeScenario1);
    }

    @Test
    public void testInitialiseOverride() throws Exception {
        setFields();
        expectations();
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeScenario1.initialiseOverride();
        assertThat(playStatesCapture.getValue().toString(), CoreMatchers.equalTo(PlayScreen.PlayStates.INTRO_SPINNING.toString()));
        verifyAll();
   }
}
