package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

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
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class, Hud.class} )

public class TestMiniSlotMachineLevelPrototypeScenario1UpdateOverride {
    private static final String TWEEN_MANAGER_FIELD_NAME = "tweenManager";
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private static final String TILE_MAP_RENDERER_FIELD_NAME = "tileMapRenderer";
    private static final String ORTHOGRAPHIC_CAMERA_FIELD_NAME = "orthographicCamera";
    private static final String HUD_FIELD_NAME = "hud";
    private static final String IN_RESTART_LEVEL_FIELD_NAME = "inRestartLevel";
    private static final String GAME_OVER_FIELD_NAME = "gameOver";

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private LevelCreatorScenario1 levelCreatorScenario1Mock;
    private TweenManager tweenManagerMock;
    private OrthogonalTiledMapRenderer tileMapRendererMock;
    private OrthographicCamera orthographicCameraMock;
    private Hud hudMock;

    @Before
    public void setUp() {
        setUpMocks();
    }

    private void setUpMocks() {
        setUpPowerMocks();
        setUpEasyMock();
    }

    private void setUpEasyMock() {
        levelCreatorScenario1Mock = createMock(LevelCreatorScenario1.class);
        tweenManagerMock = createMock(TweenManager.class);
        tileMapRendererMock = createMock(OrthogonalTiledMapRenderer.class);
        orthographicCameraMock = createMock(OrthographicCamera.class);
        hudMock = createMock(Hud.class);
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "initialiseOverride");
        mockStatic(Hud.class);
    }

    @After
    public void tearDown() {
        tearDownEasyMocks();
        tearDownPowerMocks();
    }

    private void tearDownEasyMocks() {
        levelCreatorScenario1Mock = null;
        tweenManagerMock = null;
        tileMapRendererMock = null;
        orthographicCameraMock = null;
        hudMock = null;
    }

    private void tearDownPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = null;
    }

    @Test
    public void testUpdateOverride() {
        setFields();
        setExpectations();
        replayAll();
        partialMockMiniSlotMachineLevelPrototypeScenario1.updateOverride(0.0f);
        verifyAll();
    }

    private void setFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, TWEEN_MANAGER_FIELD_NAME, tweenManagerMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorScenario1Mock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, TILE_MAP_RENDERER_FIELD_NAME, tileMapRendererMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, ORTHOGRAPHIC_CAMERA_FIELD_NAME, orthographicCameraMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, HUD_FIELD_NAME, hudMock);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, IN_RESTART_LEVEL_FIELD_NAME, false);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, GAME_OVER_FIELD_NAME, false);
    }

    private void setExpectations() {
        tweenManagerMock.update(0.0f);
        levelCreatorScenario1Mock.update(0.0f);
        tileMapRendererMock.setView(orthographicCameraMock);
        hudMock.update(0.0f);
        expect(hudMock.getWorldTime()).andReturn(0);
        expect(Hud.getLives()).andReturn(1);
        levelCreatorScenario1Mock.setPlayState(PlayScreen.PlayStates.LEVEL_LOST);
        expect(levelCreatorScenario1Mock.getPlayState()).andReturn(PlayScreen.PlayStates.LEVEL_LOST);
    }

    private void replayAll() {
        replay(tweenManagerMock,
                levelCreatorScenario1Mock,
                tileMapRendererMock,
                orthographicCameraMock,
                hudMock,
                Hud.class);
    }
    
    private void verifyAll() {
        verify(tweenManagerMock,
                levelCreatorScenario1Mock,
                tileMapRendererMock,
                orthographicCameraMock,
                hudMock);
    }
}
