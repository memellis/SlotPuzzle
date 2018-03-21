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
