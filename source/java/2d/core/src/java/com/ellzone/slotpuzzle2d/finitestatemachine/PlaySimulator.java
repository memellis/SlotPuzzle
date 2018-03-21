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

public class PlaySimulator implements PlayInterface {
    private int
        numberOfReelsFalling,
        numberOfReelsSpinning,
        numberOfReelsToDelete,
        numberOfReelsMatched,
        numberOfReelsFlashing;

    private boolean
        reelsFalling,
        reelsSpinning,
        reelsFlashing,
        reelsDeleted,
        startTimer;

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
        numberOfReelsFlashing = 0;

        reelsFalling = true;
        reelsSpinning = true;
        reelsFlashing = false;
        startTimer = false;
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
                    case DROP_REELS:
                        simulateDropReels();
                        break;
                }
                timeCount = 0.0f;
            }
        }
    }

    private void simulateIntroFallingSequence() {
        numberOfReelsFalling--;
        if (numberOfReelsFalling > 0) {
            System.out.println("Another reel has fallen. There are " + numberOfReelsFalling + " left.");
        } else {
            startTimer = false;
        }
    }

    private void simulateIntroSpinningSequence() {
        numberOfReelsSpinning--;
        if (numberOfReelsSpinning > 0) {
            System.out.println("Another reel has stopped spinning. There are " + numberOfReelsSpinning + " left.");
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
            System.out.println("Another reel has stopped flashing. There are " + numberOfReelsFlashing + " left.");
        } else {
            startTimer = false;
            numberOfReelsMatched = 10;
            numberOfReelsToDelete = 10;
        }
    }

    private void simulateIntroEndingSequence() {
        numberOfReelsToDelete--;
        if (numberOfReelsToDelete > 0) {
            System.out.println("Another reel has been deleted. There are " + numberOfReelsToDelete + " left.");
        } else {
            startTimer = false;
        }
    }

    private void simulateDropReels() {
        startTimer = false;
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
