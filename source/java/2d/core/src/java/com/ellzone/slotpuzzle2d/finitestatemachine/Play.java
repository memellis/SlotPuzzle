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

package com.ellzone.slotpuzzle2d.finitestatemachine;

import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;

public class Play {
    private StateMachine<Play, PlayState> stateMachine;
    private PlayInterface concretePlay;

    public Play() {
        this(RealPlay.class.getSimpleName());
    }

    public Play(String playType) {
        stateMachine = new DefaultStateMachine<Play, PlayState>(this, PlayState.INTRO_FALLING_SEQUENCE);

        PlayFactory playFactory = new PlayFactory();
        concretePlay = playFactory.getPlay(playType, this);
    }

    public StateMachine<Play, PlayState> getStateMachine() {
        return stateMachine;
    }

    public PlayInterface getConcretePlay() {
        return concretePlay;
    }

    public void update() {
        stateMachine.update();
        concretePlay.update(System.currentTimeMillis());
    }
}
