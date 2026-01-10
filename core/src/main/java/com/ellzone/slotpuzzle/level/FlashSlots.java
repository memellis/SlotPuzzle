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

package com.ellzone.slotpuzzle.level;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.effects.ReelAccessor;
import com.ellzone.slotpuzzle.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle.sprites.reel.ReelStoppedFlashingEvent;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.tweenengine.BaseTween;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;

import aurelienribon.tweenengine.equations.Sine;

public class FlashSlots {
    public static final int FLASH_BATCH_POOL_SIZE = 3;

    private Timeline reelFlashSeq;
    private TweenManager tweenManager;
    private GridSize levelGridSize;
    private Array<ReelTile> reelTiles;
    Array<ReelTileGridValue> matchedSlotsCopy;
    private int numberOfReelsFlashing, numberOfReelsToDelete;
    private boolean startedFlashing;
    private boolean finishedMatchingSlots;

    public FlashSlots(TweenManager tweenManager,
                      GridSize levelGridSize,
                      Array<ReelTile> reelTiles) {
        this.tweenManager = tweenManager;
        this.levelGridSize = levelGridSize;
        this.reelTiles = reelTiles;
        initialiseFlashSlots();
    }

    private void initialiseFlashSlots() {
        startedFlashing = false;
        finishedMatchingSlots = false;
        numberOfReelsFlashing = 0;
        numberOfReelsToDelete = 0;
    }

    public ReelTileGridValue[][] flashSlots(Array<ReelTile> reelTiles) {
        MatchSlots matchSlots = new MatchSlots(reelTiles, levelGridSize).invoke();
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = matchSlots.getPuzzleGridTypeReelTile();
        ReelTileGridValue[][] puzzleGrid = matchSlots.getPuzzleGrid();
        Array<ReelTileGridValue> matchedSlots = matchSlots.getMatchedSlots();

        for (TupleValueIndex matchedSlot : matchedSlots)
            reelTiles.get(matchedSlot.index).setScore(matchedSlot.value);

        matchedSlotsCopy = new Array<>(matchedSlots);

        flashMatchedSlots(matchedSlots, puzzleGridTypeReelTile);
        return puzzleGrid;
    }

    public void flashMatchedSlots(Array<ReelTileGridValue> matchedSlots, PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
        int matchSlotIndex, batchIndex, batchPosition;
        Array<ReelTileGridValue> matchSlotsBatch = new Array<>();
        float pushPause = 0.0f;
        matchSlotIndex = 0;
        numberOfReelsFlashing = matchedSlots.size;
        numberOfReelsToDelete = numberOfReelsFlashing;
        while (matchedSlots.size > 0) {
            startedFlashing = true;
            batchIndex = matchSlotIndex;
            for (int batchCount = batchIndex; batchCount < batchIndex + FLASH_BATCH_POOL_SIZE; batchCount++) {
                if (batchCount < matchedSlots.size) {
                    batchPosition = matchSlotsBatch.size;
                    matchSlotsBatch = puzzleGridTypeReelTile.depthFirstSearchAddToMatchSlotBatch(matchedSlots.get(0), matchSlotsBatch);

                    for (int deleteIndex = batchPosition; deleteIndex < matchSlotsBatch.size; deleteIndex++)
                        matchedSlots.removeValue(matchSlotsBatch.get(deleteIndex), true);
                }
            }
            flashMatchedSlotsBatch(matchSlotsBatch, pushPause);
            pushPause += 2.0f;
            matchSlotsBatch.clear();
        }
        finishedMatchingSlots = true;
    }

    public void flashMatchedSlotsForLevelCreator(Array<ReelTileGridValue> matchedSlots, PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
        int matchSlotIndex, batchIndex, batchPosition;
        Array<ReelTileGridValue> matchSlotsBatch = new Array<ReelTileGridValue>();
        float pushPause = 0.0f;
        matchSlotIndex = 0;
        numberOfReelsFlashing = matchedSlots.size;
        numberOfReelsToDelete = numberOfReelsFlashing;
        while (matchedSlots.size > 0) {
            batchIndex = matchSlotIndex;
            for (int batchCount = batchIndex; batchCount < batchIndex + 3; batchCount++) {
                if (batchCount < matchedSlots.size) {
                    batchPosition = matchSlotsBatch.size;
                    matchSlotsBatch = puzzleGridTypeReelTile.depthFirstSearchAddToMatchSlotBatch(matchedSlots.get(0), matchSlotsBatch);

                    for (int deleteIndex = batchPosition; deleteIndex < matchSlotsBatch.size; deleteIndex++)
                        matchedSlots.removeValue(matchSlotsBatch.get(deleteIndex), true);
                }
            }
            if (matchSlotsBatch.size == 0)
                break;

            flashMatchedSlotsBatchForLevelCreator(matchSlotsBatch, pushPause);
            pushPause += 2.0f;
            matchSlotsBatch.clear();
        }
    }

    public void flashSlotsForMiniSlotMachine(Array<ReelTileGridValue> miniSlotMachineReelsToFlash) {
        startedFlashing = true;
        for (ReelTileGridValue reelTileGridValue : miniSlotMachineReelsToFlash) {
            ReelTile reelTile = reelTiles.get(reelTileGridValue.c);
            reelTile.setFlashMode(true);
            reelTile.addReelFlashSegment(  reelTile.getX(),
                reelTile.getY() + (2 - reelTileGridValue.r) * reelTile.getWidth());
            reelTile.setFlashColor(new Color(Color.WHITE));
            initialiseReelFlash(reelTile, 2.0f);
        }
        finishedMatchingSlots = true;
    }

    public Array<ReelTileGridValue> getMatchedSlots() {
        return matchedSlotsCopy;
    }

    private void flashMatchedSlotsBatch(Array<ReelTileGridValue> matchedSlots, float pushPause) {
        int index;
        for (int i = 0; i < matchedSlots.size; i++) {
            index = matchedSlots.get(i).getIndex();
            if (index  >= 0) {
                ReelTile reel = reelTiles.get(index);
                if (!reel.getFlashTween()) {
                    reel.setFlashMode(true);
                    reel.setFlashColor(new Color(Color.WHITE));
                    initialiseReelFlash(reel, pushPause);
                }
            }
        }
    }

    private void flashMatchedSlotsBatchForLevelCreator(Array<ReelTileGridValue> matchedSlots, float pushPause) {
        int index;
        for (int i = 0; i < matchedSlots.size; i++) {
            index = matchedSlots.get(i).getIndex();
            if (index >= 0) {
                ReelTile reel = reelTiles.get(index);
                if (!reel.getFlashTween()) {
                    reel.setFlashMode(true);
                    Color flashColor = new Color(Color.WHITE);
                    reel.setFlashColor(flashColor);
                    initialiseReelFlashForLevelCreator(reel, pushPause);
                }
            }
        }
    }

    private void initialiseReelFlash(ReelTile reel, float pushPause) {
        Array<Object> userData = new Array<>();
        reel.setFlashTween(true);
        reel.addReelFlashSegment(reel.getX(), reel.getY());
        reelFlashSeq = Timeline.createSequence();
        reelFlashSeq = reelFlashSeq.pushPause(pushPause);

        Color fromColor = new Color(Color.WHITE);
        fromColor.a = 1;
        Color toColor = new Color(Color.RED);
        toColor.a = 1;

        userData.add(reel);
        userData.add(reelFlashSeq);

        setUpFlashSequence(reel, userData, fromColor, toColor);
    }

    private void setUpFlashSequence(ReelTile reel, Array<Object> userData, Color fromColor, Color toColor) {
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
            .target(fromColor.r, fromColor.g, fromColor.b)
            .ease(Sine.IN));
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.2f)
            .target(toColor.r, toColor.g, toColor.b)
            .ease(Sine.OUT)
            .repeatYoyo(17, 0));

        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
            .target(fromColor.r, fromColor.g, fromColor.b)
            .ease(Sine.IN));

        if (tweenManager != null)
            reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.05f)
                    .target(toColor.r, toColor.g, toColor.b)
                    .ease(Sine.OUT)
                    .repeatYoyo(25, 0))
                .setCallback(reelFlashCallback)
                .setCallbackTriggers(TweenCallback.COMPLETE)
                .setUserData(userData)
                .start(tweenManager);
    }

    private TweenCallback reelFlashCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            switch (type) {
                case TweenCallback.COMPLETE:
                    delegateReelFlashCallback(source);
            }
        }
    };

    private void delegateReelFlashCallback(BaseTween<?> source) {
        @SuppressWarnings("unchecked")
        Array<Object> userData = (Array<Object>) source.getUserData();
        ReelTile reel = (ReelTile) userData.get(0);
        Timeline reelFlashSeq = (Timeline) userData.get(1);
        reelFlashSeq.kill();
        if (reel.getFlashTween()) {
            reel.setFlashOff();
            reel.setFlashTween(false);
            reel.processEvent(new ReelStoppedFlashingEvent());
        }
        numberOfReelsFlashing--;
    }

    private void initialiseReelFlashForLevelCreator(ReelTile reel, float pushPause) {
        Array<Object> userData = new Array<Object>();
        reel.setFlashTween(true);
        reel.addReelFlashSegment(reel.getX(),
            reel.getY());
        reelFlashSeq = Timeline.createSequence();
        reelFlashSeq = reelFlashSeq.pushPause(pushPause);

        Color fromColor = new Color(Color.WHITE);
        fromColor.a = 1;
        Color toColor = new Color(Color.RED);
        toColor.a = 1;

        userData.add(reel);
        userData.add(reelFlashSeq);

        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
            .target(fromColor.r, fromColor.g, fromColor.b)
            .ease(Sine.IN));
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.2f)
            .target(toColor.r, toColor.g, toColor.b)
            .ease(Sine.OUT)
            .repeatYoyo(17, 0));

        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.set(reel, ReelAccessor.FLASH_TINT)
            .target(fromColor.r, fromColor.g, fromColor.b)
            .ease(Sine.IN));
        reelFlashSeq = reelFlashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.05f)
                .target(toColor.r, toColor.g, toColor.b)
                .ease(Sine.OUT)
                .repeatYoyo(25, 0))
            .setCallback(reelFlashCallbackForLevelCreator)
            .setCallbackTriggers(TweenCallback.COMPLETE)
            .setUserData(userData)
            .start(tweenManager);
    }

    private TweenCallback reelFlashCallbackForLevelCreator = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            switch (type) {
                case TweenCallback.COMPLETE:
                    delegateReelFlashCallbackForLevelCreator(type, source);
            }
        }
    };

    private void delegateReelFlashCallbackForLevelCreator(int type, BaseTween<?> source) {
        @SuppressWarnings("unchecked")
        Array<Object> userData = (Array<Object>) source.getUserData();
        ReelTile reel = (ReelTile) userData.get(0);
        Timeline reelFlashSeq = (Timeline) userData.get(1);
        reelFlashSeq.kill();
        if (reel.getFlashTween()) {
            reel.setFlashOff();
            reel.setFlashTween(false);
            reel.processEvent(new ReelStoppedFlashingEvent());
        }
        numberOfReelsFlashing--;
    }

    public int getNumberOfReelsFlashing() {
        return numberOfReelsFlashing;
    }

    public int getNumberOfReelsToDelete() {
        return numberOfReelsToDelete;
    }

    public boolean areReelsDeleted() {
        return numberOfReelsToDelete == 0;
    }

    public boolean areReelsFlashing() {
        return numberOfReelsFlashing > 0;
    }

    public boolean areReelsStartedFlashing() {
        return startedFlashing;
    }

    public boolean isFinishedMatchingSlots() { return finishedMatchingSlots; }

    public void setNumberOfReelsFlashing(int numberOfReelsFlashing) {
        this.numberOfReelsFlashing = numberOfReelsFlashing;
    }

    public void setReelsStartedFlashing(boolean startedFlashing) {
        this.startedFlashing = startedFlashing;
    }

    public void setFinishedMatchingSlots(boolean finishedMatchingSlots) {
        this.finishedMatchingSlots = finishedMatchingSlots;
    }

    public void deleteAReel() {
        numberOfReelsToDelete--;
    }
}
