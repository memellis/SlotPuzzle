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

public class RealPlay implements PlayInterface {
    @Override
    public int getNumberOfReelsFalling() {
        return 0;
    }

    @Override
    public int getNumberOfReelsSpinning() {
        return 0;
    }

    @Override
    public int getNumberOfReelsMatched() {
        return 0;
    }

    @Override
    public int getNumberOfReelsFlashing() {
        return 0;
    }

    @Override
    public int getNumberOfReelsToDelete() {
        return 0;
    }

    @Override
    public boolean areReelsFalling() {
        return false;
    }

    @Override
    public boolean areReelsSpinning() {
        return false;
    }

    @Override
    public boolean areReelsFlashing() {
        return false;
    }

    @Override
    public boolean areReelsDeleted() {
        return false;
    }

    @Override
    public void update(float delta) {
    }

    @Override
    public void start() {
    }

    @Override
    public boolean isStopped() {
        return false;
    }
}
