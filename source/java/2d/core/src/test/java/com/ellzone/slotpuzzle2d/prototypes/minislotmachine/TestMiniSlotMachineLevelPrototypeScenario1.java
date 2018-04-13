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
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;

import org.easymock.Capture;
import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.easymock.EasyMock.and;
import static org.easymock.EasyMock.capture;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.mock;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.mockStatic;
import static org.powermock.api.easymock.PowerMock.replay;
import static org.powermock.api.mockito.PowerMockito.whenNew;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class} )

public class TestMiniSlotMachineLevelPrototypeScenario1 {
    public static final String VIEWPORT_FIELD_NAME = "viewport";
    public static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";


    @Test
    public void testHandleInput() throws Exception {

        Input mockInput = createMock(Input.class);
        Application mockApplication = createMock(Application.class);
        FitViewport mockViewPort = createMock(FitViewport.class);

        Gdx.input = mockInput;
        Gdx.app = mockApplication;

        LevelCreatorScenario1 levelCreatorScenario1Mock = createMock(LevelCreatorScenario1.class);
        Vector3 vector3Mock = createMock(Vector3.class);

        Capture<String> logCaptureArgument1 = EasyMock.newCapture();
        Capture<String> logCaptureArgument2 = EasyMock.newCapture();

        expect(mockInput.justTouched()).andReturn(true);
        expect(mockInput.getX()).andReturn(10);
        expect(mockInput.getY()).andReturn(10);
        whenNew(Vector3.class).withArguments(10.0f, 10.0f, 0.0f).thenReturn(vector3Mock);
        expect(levelCreatorScenario1Mock.getPlayState()).andReturn(PlayScreen.PlayStates.INITIALISING);
        mockApplication.debug(capture(logCaptureArgument1), capture(logCaptureArgument2));
        replay(mockInput, mockApplication, levelCreatorScenario1Mock, MiniSlotMachineLevelPrototypeScenario1.class);
        MiniSlotMachineLevelPrototypeScenario1 miniSlotMachineLevelPrototypeScenario1 = new MiniSlotMachineLevelPrototypeScenario1();
        Whitebox.setInternalState(miniSlotMachineLevelPrototypeScenario1, VIEWPORT_FIELD_NAME, mockViewPort);
        Whitebox.setInternalState(miniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorScenario1Mock);
        miniSlotMachineLevelPrototypeScenario1.handleInput(0.0f);
        assertThat(logCaptureArgument2.getValue(), CoreMatchers.<String>equalTo(PlayScreen.PlayStates.INITIALISING.toString()));
    }
}
