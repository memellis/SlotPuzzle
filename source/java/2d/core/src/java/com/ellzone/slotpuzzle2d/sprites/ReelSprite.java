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
