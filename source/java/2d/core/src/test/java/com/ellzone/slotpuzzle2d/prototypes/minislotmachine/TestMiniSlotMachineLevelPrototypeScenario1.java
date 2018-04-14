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

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.easymock.PowerMock.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class} )

public class TestMiniSlotMachineLevelPrototypeScenario1 {
    public static final String VIEWPORT_FIELD_NAME = "viewport";
    public static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";

    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private Input mockInput;
    private Application mockApplication;
    private FitViewport mockViewPort;
    private LevelCreatorScenario1 levelCreatorScenario1Mock;
    private Vector3 vector3Mock;
    Capture<String> logCaptureArgument1, logCaptureArgument2;

    private void setUp() {
        setUpMocks();
        mockGdx();
        setUpCaptureArguments();
    }

    private void setUpMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class, "processIsTileClicked");
        mockInput = createMock(Input.class);
        mockApplication = createMock(Application.class);
        mockViewPort = createMock(FitViewport.class);
        levelCreatorScenario1Mock = createMock(LevelCreatorScenario1.class);
        vector3Mock = createMock(Vector3.class);
    }

    private void setUpCaptureArguments() {
        logCaptureArgument1 = EasyMock.newCapture();
        logCaptureArgument2 = EasyMock.newCapture();
    }

    private void mockGdx() {
        Gdx.input = mockInput;
        Gdx.app = mockApplication;
    }

    @Test
    public void testHandleInput() throws Exception {
        for (PlayScreen.PlayStates playState : PlayScreen.PlayStates.values()) {
            testHandleInputPlayStates(playState);
        }
    }

    private void testHandleInputPlayStates(PlayScreen.PlayStates playState) throws Exception {
        setUp();
        expectations(playState);
        replayAll();
        inokeHandleInput();
        assertThat(logCaptureArgument2.getValue(), CoreMatchers.<String>equalTo(playState.toString()));
        verifyAll();
    }

    private void verifyAll() {
        verify(mockInput, mockApplication, levelCreatorScenario1Mock, partialMockMiniSlotMachineLevelPrototypeScenario1);
    }

    private void inokeHandleInput() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, VIEWPORT_FIELD_NAME, mockViewPort);
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorScenario1Mock);
        partialMockMiniSlotMachineLevelPrototypeScenario1.handleInput(0.0f);
    }

    private void replayAll() {
        replay(mockInput, mockApplication, levelCreatorScenario1Mock, partialMockMiniSlotMachineLevelPrototypeScenario1);
    }

    private void expectations(PlayScreen.PlayStates playState) throws Exception {
        expect(mockInput.justTouched()).andReturn(true);
        expect(mockInput.getX()).andReturn(10);
        expect(mockInput.getY()).andReturn(10);
        whenNew(Vector3.class).withArguments(10.0f, 10.0f, 0.0f).thenReturn(vector3Mock);
        expect(levelCreatorScenario1Mock.getPlayState()).andReturn(playState);
        mockApplication.debug(capture(logCaptureArgument1), capture(logCaptureArgument2));
        if (playState == PlayScreen.PlayStates.PLAYING) {
            PowerMock.expectPrivate(partialMockMiniSlotMachineLevelPrototypeScenario1, "processIsTileClicked").atLeastOnce();
        }
    }
}