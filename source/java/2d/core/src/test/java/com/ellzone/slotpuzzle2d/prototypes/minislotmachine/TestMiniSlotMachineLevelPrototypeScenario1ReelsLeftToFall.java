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

import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.level.LevelCreatorScenario1;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;

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

import static org.easymock.EasyMock.captureBoolean;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {MiniSlotMachineLevelPrototypeScenario1.class} )

public class TestMiniSlotMachineLevelPrototypeScenario1ReelsLeftToFall {
    private static final String LEVEL_CREATOR_FIELD_NAME = "levelCreator";
    private MiniSlotMachineLevelPrototypeScenario1 partialMockMiniSlotMachineLevelPrototypeScenario1;
    private LevelCreatorScenario1 levelCreatorMock;
    private Array<TupleValueIndex> reelsToFallMock;
    private Array<TupleValueIndex>  reelsToFall;
    private Capture<Boolean> reelsAvoveHaveFallen;



    @Before
    public void setUp() {
        setUpPowerMocks();
        setUpEasyMocks();
        setUpCaptures();
    }

    private void setUpPowerMocks() {
        partialMockMiniSlotMachineLevelPrototypeScenario1 = PowerMock.createNicePartialMock(MiniSlotMachineLevelPrototypeScenario1.class,
                "swapReels");
    }

    private void setUpEasyMocks() {
        levelCreatorMock = PowerMock.createMock(LevelCreatorScenario1.class);
        reelsToFallMock = PowerMock.createMock(Array.class);
    }

    private void setUpCaptures() {
        reelsAvoveHaveFallen = EasyMock.newCapture();
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
        levelCreatorMock = null;
    }

    @Test
    public void testReelsLeftToFall_whenOneReelToFall() throws Exception {
        setUpTestDataOneReelToFall();
        setUpFields();
        setUpExpectations_whenOneReelToFall();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeScenario1,
                "reelsLeftToFall",
                3, 3);
        assertThat(reelsToFall.size,is(equalTo(0)));
        assertThat(reelsAvoveHaveFallen.getValue(), is(true));
        verifyAll();
    }

    @Test
    public void testReelsLeftToFall_whenNoReelToFall() throws Exception {
        setUpTestDataNoReelToFall();
        setUpFields();
        setUpExpectations_whenNoReelToFall();
        replayAll();
        Whitebox.invokeMethod(partialMockMiniSlotMachineLevelPrototypeScenario1,
                "reelsLeftToFall",
                3, 3);
        verifyAll();
    }

    private void setUpTestDataOneReelToFall() {
         reelsToFall = new Array<TupleValueIndex>();
         reelsToFall.add(new TupleValueIndex(3, 3, 0, 1));
    }

    private void setUpTestDataNoReelToFall() {
        reelsToFall = new Array<TupleValueIndex>();
        reelsToFall.add(new TupleValueIndex(3, 2, 0, 1));
    }

    private void setUpFields() {
        Whitebox.setInternalState(partialMockMiniSlotMachineLevelPrototypeScenario1, LEVEL_CREATOR_FIELD_NAME, levelCreatorMock);
    }

    private void setUpExpectations_whenOneReelToFall() {
        expect(levelCreatorMock.getReelsToFall()).andReturn(reelsToFall);
        levelCreatorMock.setReelsToFall(reelsToFall);
        levelCreatorMock.setReelsAboveHaveFallen(captureBoolean(reelsAvoveHaveFallen));
    }

    private void setUpExpectations_whenNoReelToFall() {
        expect(levelCreatorMock.getReelsToFall()).andReturn(reelsToFall);
    }

    private void replayAll() {
        replay(partialMockMiniSlotMachineLevelPrototypeScenario1,
               levelCreatorMock
        );
    }

    private void verifyAll() {
        verify(partialMockMiniSlotMachineLevelPrototypeScenario1,
               levelCreatorMock);
    }
}
