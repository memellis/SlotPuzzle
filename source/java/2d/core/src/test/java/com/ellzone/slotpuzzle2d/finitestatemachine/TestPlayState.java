package com.ellzone.slotpuzzle2d.finitestatemachine;

/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.gdx.ai.fsm.StateMachine;

import org.easymock.Capture;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.easymock.PowerMock;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Play.class} )
public class TestPlayState {
    private
        Play playMock;
        StateMachine stateMachineMock;
        PlaySimulator playSimulatorMock;

    @Before
    public void setUp() {
        playMock = PowerMock.createMock(Play.class);
        stateMachineMock = PowerMock.createMock(StateMachine.class);
        playSimulatorMock = PowerMock.createMock(PlaySimulator.class);
    }

    @After
    public void tearDown() {
        playMock = null;
        stateMachineMock = null;
        playSimulatorMock = null;
    }

    @Test
    public void testPlayStateIntroFallingSequence() throws Exception {
        expect(playMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsFalling()).andReturn(false);
        expect(playMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.INTRO_SPINNING_SEQUENCE);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.INTRO_FALLING_SEQUENCE.update(playMock);
        verifyAll();
    }

    @Test
    public void testPlayStateIntroSpinningSequence() throws Exception {
        expect(playMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsSpinning()).andReturn(false);
        expect(playMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.INTRO_FLASHING_SEQUENCE);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.INTRO_SPINNING_SEQUENCE.update(playMock);
        verifyAll();
    }

    @Test
    public void testPlayStateIntroFlashingSequence() throws Exception {
        expect(playMock.getConcretePlay()).andReturn(playSimulatorMock);
        expect(playSimulatorMock.areReelsFlashing()).andReturn(false);
        expect(playMock.getStateMachine()).andReturn(stateMachineMock);
        stateMachineMock.changeState(PlayState.INTRO_ENDING_SEQUENCE);
        PowerMock.expectLastCall();
        replayAll();
        PlayState.INTRO_FLASHING_SEQUENCE.update(playMock);
        verifyAll();
    }

    private void replayAll() {
        PowerMock.replay(playMock, playSimulatorMock, Play.class);
    }

    private void verifyAll() {
        PowerMock.verify(playMock, playSimulatorMock, Play.class);
    }
}