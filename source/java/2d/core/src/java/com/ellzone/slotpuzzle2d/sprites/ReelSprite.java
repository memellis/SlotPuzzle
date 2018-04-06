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

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;

public abstract class ReelSprite extends Sprite {
    private final DelayedRemovalArray<ReelTileListener> listeners = new DelayedRemovalArray<ReelTileListener>(0);
    private boolean spinning = true;
    private int endReel;

    public abstract void update(float dt);
    public abstract void dispose();

    public boolean isSpinning() {
        return this.spinning;
    }

    public void setSpinning(boolean spinning) {
        this.spinning = spinning;
    }

    public int getEndReel() {
        return this.endReel;
    }

    public void setEndReel(int endReel) {
        this.endReel = endReel;
    }

    public boolean addListener (ReelTileListener listener) {
        if (!listeners.contains(listener, true)) {
            listeners.add(listener);
            return true;
        }
        return false;
    }

    public boolean removeListener (ReelTileListener listener) {
        return listeners.removeValue(listener, true);
    }

    public Array<ReelTileListener> getListeners () {
        return listeners;
    }

    public void processEvent(ReelTileEvent reelTileEvent) {
        Array<ReelTileListener> tempReelSlotTileListenerList = new Array<ReelTileListener>();

        synchronized (this) {
            if (listeners.size == 0)
                return;
            for(int i = 0; i < listeners.size; i++) {
                tempReelSlotTileListenerList.add(listeners.get(i));
            }
        }

        for (ReelTileListener listener : tempReelSlotTileListenerList) {
            listener.actionPerformed(reelTileEvent, (ReelTile) this);
        }
    }
}
