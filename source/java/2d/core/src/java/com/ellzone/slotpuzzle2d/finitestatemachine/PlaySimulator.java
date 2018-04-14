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

package com.ellzone.slotpuzzle2d.finitestatemachine;

import com.badlogic.gdx.Gdx;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;

public class PlaySimulator implements PlayInterface {
    private String logTag = SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName();

    private int
        numberOfReelsFalling,
        numberOfReelsSpinning,
        numberOfReelsToDelete,
        numberOfReelsMatched,
        reelMatchCount,
        numberOfReelsFlashing;

    private boolean
        reelsFalling,
        reelsSpinning,
        reelsFlashing,
        reelsDeleted,
        startTimer,
        simulateIntroDropDropPlay;

    private float
        timeCount = 0.0f;

    private Play play;

    public PlaySimulator(Play play) {
        this.play = play;
        initialisePlaySimulator();
    }

    private void initialisePlaySimulator() {
        numberOfReelsFalling = 24;
        numberOfReelsSpinning = 24;
        numberOfReelsToDelete = 0;
        numberOfReelsMatched = 0;
        reelMatchCount = 0;
        numberOfReelsFlashing = 0;

        reelsFalling = true;
        reelsSpinning = true;
        reelsFlashing = false;
        startTimer = false;
        simulateIntroDropDropPlay = false;
    }

    @Override
    public int getNumberOfReelsFalling() {
        return numberOfReelsFalling;
    }

    @Override
    public int getNumberOfReelsSpinning() {
        return numberOfReelsSpinning;
    }

    @Override
    public int getNumberOfReelsToDelete() {
        return numberOfReelsToDelete;
    }

    @Override
    public int getNumberOfReelsMatched() {
        return numberOfReelsMatched;
    }

    @Override
    public int getNumberOfReelsFlashing() {
        return numberOfReelsFlashing;
    }

    @Override
    public boolean areReelsFalling() {
        return numberOfReelsFalling > 0;
    }

    @Override
    public boolean areReelsSpinning() {
        return numberOfReelsSpinning > 0;
    }

    @Override
    public boolean areReelsFlashing() {
        return numberOfReelsFlashing > 0;
    }

    @Override
    public boolean areReelsDeleted() {
        return numberOfReelsToDelete > 0;
    }

    public void update(float delta) {
        if (startTimer) {
            timeCount += delta;
            if (timeCount >= 1.0f) {
                switch (play.getStateMachine().getCurrentState()) {
                    case INTRO_FALLING_SEQUENCE:
                        simulateIntroFallingSequence();
                        break;
                    case INTRO_SPINNING_SEQUENCE:
                        simulateIntroSpinningSequence();
                        break;
                    case INTRO_FLASHING_SEQUENCE:
                        simulateIntroReelsFlashingSequence();
                        break;
                    case INTRO_ENDING_SEQUENCE:
                        simulateIntroEndingSequence();
                        break;
                    case DROP:
                        simulateDrop();
                        break;
                    case SPIN:
                        simulateSpin();
                        break;
                   case FLASH:
                        simulateFlash();
                        break;
                }
                timeCount = 0.0f;
            }
        }
    }

    public void setSimulatorIntroDropDropPlay() {
        simulateIntroDropDropPlay = true;
    }

    private void simulateIntroFallingSequence() {
        numberOfReelsFalling--;
        if (numberOfReelsFalling > 0) {
            Gdx.app.debug(logTag, "Another reel has fallen. There are " + numberOfReelsFalling + " left.");
        } else {
            startTimer = false;
        }
    }

    private void simulateIntroSpinningSequence() {
        numberOfReelsSpinning--;
        if (numberOfReelsSpinning > 0) {
            Gdx.app.debug(logTag,"Another reel has stopped spinning. There are " + numberOfReelsSpinning + " left.");
        } else {
            startTimer = false;
            numberOfReelsFlashing = 10;
        }
    }

    private void simulateIntroFallingAndSpinningSeqence() {
    }

    private void simulateIntroReelsFlashingSequence() {
        numberOfReelsFlashing--;
        if (numberOfReelsFlashing > 0) {
            Gdx.app.debug(logTag, "Another reel has stopped flashing. There are " + numberOfReelsFlashing + " left.");
        } else {
            startTimer = false;
            numberOfReelsMatched = 10;
            numberOfReelsToDelete = 10;
        }
    }

    private void simulateIntroEndingSequence() {
        numberOfReelsToDelete--;
        if (numberOfReelsToDelete > 0) {
            Gdx.app.debug(logTag,"Another reel has been deleted. There are " + numberOfReelsToDelete + " left.");
        } else {
            startTimer = false;
            numberOfReelsFalling = 10;
        }
    }

    private void simulateDrop() {
        numberOfReelsFalling--;
        if (numberOfReelsFalling > 0) {
            Gdx.app.debug(logTag,"Another reel has fallen. There are " + numberOfReelsFalling + " left.");
        } else {
            startTimer = false;
            numberOfReelsSpinning = 10;
        }
    }

    private void simulateSpin() {
        numberOfReelsSpinning--;
        if (numberOfReelsSpinning > 0) {
            Gdx.app.debug(logTag,"Another reel has stopped spinning. There are " + numberOfReelsSpinning + " left.");
        } else {
            startTimer = false;
            numberOfReelsFlashing = 10;
            numberOfReelsMatched = 0;
       }
    }

    private void simulateFlash() {
        numberOfReelsFlashing--;
        if (numberOfReelsFlashing > 0) {
            Gdx.app.debug(logTag,"Another reel has stopped flashing. There are " + numberOfReelsFlashing + " left.");
        } else {
            startTimer = false;
            if (simulateIntroDropDropPlay) {
                numberOfReelsMatched = 10;
            }
       }
    }

    @Override
    public void start() {
        startTimer = true;
    }

    @Override
    public boolean isStopped() {
        return !startTimer;
    }
}
