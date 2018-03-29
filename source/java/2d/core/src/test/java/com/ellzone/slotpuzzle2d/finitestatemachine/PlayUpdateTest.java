package com.ellzone.slotpuzzle2d.finitestatemachine;


import com.badlogic.gdx.ai.fsm.StateMachine;

import org.easymock.EasyMock;
import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.EasyMock.verify;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.powermock.api.easymock.PowerMock.createMock;
import static org.powermock.api.easymock.PowerMock.replay;

@RunWith(PowerMockRunner.class)
@PrepareForTest( {Play.class} )

public class PlayUpdateTest {
    @Test
    public void testUpdate() {
        PlayFactory playFactoryMock = createMock(PlayFactory.class);
        RealPlay realPlayMock = createMock(RealPlay.class);
        PlaySimulator playSimulatorMock = createMock(PlaySimulator.class);
        StateMachine stateMachineMock = createMock(StateMachine.class);

        Play play = new Play();

        expect(playFactoryMock.getPlay(PlaySimulator.class.getSimpleName(), play)).andReturn(playSimulatorMock);
        play.update();
        stateMachineMock.update();
        play.getConcretePlay().update(0.0f);
        expectLastCall().atLeastOnce();
        replay(Play.class, playFactoryMock, PlayFactory.class);

        assertThat(playFactoryMock.getPlay(PlaySimulator.class.getSimpleName(), play), CoreMatchers.<PlayInterface>equalTo(playSimulatorMock));

        verify(playFactoryMock);
    }
}
