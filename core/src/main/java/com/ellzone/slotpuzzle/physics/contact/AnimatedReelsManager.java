/*
 Copyright 2011 See AUTHORS file.

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package com.ellzone.slotpuzzle.physics.contact;

import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.messaging.MessageType;
import com.ellzone.slotpuzzle.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle.puzzlegrid.Point;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;

import java.util.HashSet;

public class AnimatedReelsManager implements Telegraph {
    public static final int SCREEN_OFFSET = 400; //duplaticated fixme
    private static final float BOTTOM_ROW = 40.0f;
    private Array<AnimatedReel> animatedReels;
    private Array<Body> reelBodies;
    private Array<ReelTile> reelTiles;
    private int numberOfReelsToFall = 0;
    private int reelsStoppedFalling = 0;
    private HashSet<Point> reelPoints = new HashSet<>();

    public AnimatedReelsManager(Array<AnimatedReel> animatedReels) {
        if (animatedReels == null)
            throw new IllegalArgumentException("animatedReels is null");
        this.animatedReels = animatedReels;
        reelTiles = getReelTilesFromAnimatedReels(animatedReels);
    }

    public AnimatedReelsManager(Array<AnimatedReel> animatedReels,
                                Array<Body> reelBodies) {
        this(animatedReels);
        this.reelBodies = reelBodies;
    }

    public void setAnimatedReels(Array<AnimatedReel> animatedReels) {
        this.animatedReels = animatedReels;
        reelTiles = getReelTilesFromAnimatedReels(animatedReels);
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return animatedReels;
    }

    public Array<ReelTile> getReelTiles() {
        return reelTiles;
    }

    public void setReelBodies(Array<Body> reelBodies) {
        this.reelBodies = reelBodies;
    }

    public void setNumberOfReelsToFall(int numberOfReelsToFall) {
        this.numberOfReelsToFall = numberOfReelsToFall;
        this.reelsStoppedFalling = numberOfReelsToFall;
    }

    public int getNumberOfReelsToFall() {
        return numberOfReelsToFall;
    }

    public int getReelsStoppedFalling() {
        return reelsStoppedFalling;
    }

    @Override
    public boolean handleMessage(Telegram message) {
        if (message == null) {
            throw new IllegalArgumentException("message is null");
        }

        if (message.message == MessageType.SwapReelsAboveMe.index) {
            Array<AnimatedReel> reelsAB = (Array<AnimatedReel>) message.extraInfo;
            if (reelsAB == null)
                throw new IllegalArgumentException("message.extrainfo is null");
            if (reelsAB.size != 2)
                throw new IllegalArgumentException("message.extrainfo does have a two AnimatedReels");

            AnimatedReel animatedReelA = reelsAB.get(0);
            AnimatedReel animatedReelB = reelsAB.get(1);

            swapReelsAboveReel(
                animatedReelA.getReel(),
                animatedReelB.getReel(),
                swapReelActionStoppedFalling);

            return true;
        }
        if (message.message == MessageType.ReelsLeftToFall.index) {
            AnimatedReel animatedReel = (AnimatedReel) message.extraInfo;
            if (animatedReel == null)
                throw new IllegalArgumentException("message.extrainfo is null");
            reelsLeftToFall(animatedReel);
            return true;
        }
        if (message.message == MessageType.ReelSinkReelsLeftToFall.index) {
            AnimatedReel animatedReel = (AnimatedReel) message.extraInfo;
            if (animatedReel == null)
                throw new IllegalArgumentException("message.extrainfo is null");
            reelSinkReelsLeftToFall(animatedReel);
        }
        return false;
    }

    private SwapReelAction swapReelActionStoppedFalling = new SwapReelAction() {
        @Override
        public void doAction(ReelTile reelTile) {
            recordDecrementReelsLeftToFall(reelTile);
        }
    };

    private void swapReelsAboveReel(
        ReelTile reelBelow,
        ReelTile reelAbove,
        SwapReelAction swapReelAction) {
        moveDeletedReelsToTheTopOfTheColumn(reelBelow, reelAbove);
        setFallenReelsToCurrentPositions(reelBelow, reelAbove, swapReelAction);
    }

    private void moveDeletedReelsToTheTopOfTheColumn(ReelTile reelBelow, ReelTile reelAbove) {
        Array<Integer> reelsDeleted = getTheReelsDeletedInColumn(reelBelow.getDestinationX());
        int numberOfReelsDeletedInColumn = reelsDeleted.size;
        for (int index = 0; index < numberOfReelsDeletedInColumn; index++ ) {
            ReelTile reelTileDeleted = reelTiles.get(reelsDeleted.get(index));
            reelTileDeleted.setDestinationY(360 - (index * 40));
            reelTileDeleted.setY(360 + SCREEN_OFFSET - (index * 40));
        }
    }

    private void setFallenReelsToCurrentPositions(ReelTile reelBelow,
                                                  ReelTile reelAbove,
                                                  SwapReelAction swapReelAction) {
        Array<Integer> reelsAboveInContact = getReelsInContactAbove(reelBelow.getIndex());
        for (Integer reelAboveInContact : reelsAboveInContact)
            setFallReelToCurrentPosition(reelAboveInContact, swapReelAction);
        setFallReelToCurrentPosition(reelBelow.getIndex(), swapReelAction);
    }

    private void setFallReelToCurrentPosition(Integer reelAboveInContact, SwapReelAction swapReelAction) {
        if (reelAboveInContact>=0) {
            ReelTile reelTile = reelTiles.get(reelAboveInContact);
            if (!reelTile.isReelTileDeleted()) {
                reelTile.setDestinationY(reelTile.getSnapY());
                swapReelAction.doAction(reelTile);
            }
        }
    }

    public Array<Integer> getTheReelsDeletedInColumn(float column) {
        Array<Integer> reelsDeleted = new Array<>();
        for (ReelTile reelTile : reelTiles) {
            if (reelTile.getDestinationX() == column)
                if (reelTile.isReelTileDeleted())
                    reelsDeleted.add(reelTile.getIndex());
        }
        return reelsDeleted;
    }

    public Array<Integer> getTheReelsDeleted() {
        Array<Integer> reelsDeleted = new Array<>();
        for (ReelTile reelTile : reelTiles)
            if (reelTile.isReelTileDeleted())
                reelsDeleted.add(reelTile.getIndex());
        return reelsDeleted;
    }


    public Array<Integer> getReelsInContactAbove(int reel) {
        Array<Integer> reelsAbove = new Array<>();
        if (reel < 0)
            return reelsAbove;
        ReelTile reelTile = reelTiles.get(reel);
        boolean foundReelAvove;
        do {
            int reelAbove = findReelUsingSnapYIgnoringDeletedReels(
                (int) reelTile.getDestinationX(),
                (int) reelTile.getSnapY() + 40);
            foundReelAvove =
                reelAbove >= 0 &&
                    !reelTiles.get(reelAbove).isReelTileDeleted() ? true : false;
            if (foundReelAvove) {
                reelsAbove.add(reelAbove);
                reelTile = reelTiles.get(reelAbove);
            }

        } while (foundReelAvove);
        return reelsAbove;
    }

    private TupleValueIndex[] getReelsAboveMe(ReelTile reelTile) {
        return PuzzleGridType.getReelsAboveMe(
            PuzzleGridTypeReelTile.populateMatchGridStatic(
                reelTiles,
                new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT)),
            PuzzleGridTypeReelTile.getRowFromLevel(
                reelTile.getDestinationY(),
                SlotPuzzleConstants.GAME_LEVEL_HEIGHT),
            PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX()));
    }

    private void reelSinkReelsLeftToFall(AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        if (isReelAtDestination(reelTile, BOTTOM_ROW)) {
            recordDecrementReelsLeftToFall(reelTile);
            markAllReelsAvoveInContactAsFallen(reelTile);
        }
        if (isReelAtSnapY(reelTile, BOTTOM_ROW)) {
            recordDecrementReelsLeftToFall(reelTile);
            markAllReelsAvoveInContactAsFallen(reelTile);
        }
        if (isReelFallenFromAbove(reelTile, BOTTOM_ROW))
            processReelFallenBelowDestinationRow(reelTile, getReelsAboveMe(reelTile));
    }

    private boolean isReelAtSnapY(ReelTile reelTile, float snapY) {
        return reelTile.getSnapY() == snapY;
    }

    private boolean isReelAtDestination(ReelTile currentReel, float destinationY) {
        return currentReel.getDestinationY() == destinationY;
    }

    private boolean isReelFallenFromAbove(ReelTile currentReel, float destinationY) {
        return currentReel.getDestinationY() > destinationY;
    }

    private void markAllReelsAvoveInContactAsFallen(ReelTile reelTile) {
        TupleValueIndex[] reelsAboveMe = getReelsAboveMe(reelTile);
        if (isReelFallenBelowDestinationRow(reelTile))
            processReelFallenBelowDestinationRow(reelTile, reelsAboveMe);
        else
            processReelsLeftToFall(reelTile, reelsAboveMe);
    }

    private void processReelsLeftToFall(ReelTile currentReelTile, TupleValueIndex[] reelsAboveMe) {
        for (int i = 0; i < reelsAboveMe.length; i++) {
            if (PuzzleGridTypeReelTile.getRowFromLevel(
                currentReelTile.getDestinationY(),
                SlotPuzzleConstants.GAME_LEVEL_HEIGHT) - 1 == reelsAboveMe[i].getR()) {
                recordDecrementReelsLeftToFall(animatedReels.get(reelsAboveMe[i].index).getReel());
            }
            currentReelTile = animatedReels.get(reelsAboveMe[i].index).getReel();
        }
    }

    private boolean isReelFallenBelowDestinationRow(ReelTile currentReelTile) {
        int currentRow = PuzzleGridTypeReelTile.getRowFromLevel(
            currentReelTile.getY(), SlotPuzzleConstants.GAME_LEVEL_HEIGHT);
        int destinationRow = PuzzleGridTypeReelTile.getRowFromLevel(
            currentReelTile.getDestinationY(), SlotPuzzleConstants.GAME_LEVEL_HEIGHT);
        return destinationRow < currentRow;
    }

    private void reelsLeftToFall(AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        recordDecrementReelsLeftToFall(reelTile);
    }

    private void recordDecrementReelsLeftToFall(ReelTile reelTile) {
        if (!reelTile.isFallen()) {
            reelTile.setIsFallen(true);
            decrementReelsLeftToFall();
        }
    }

    private void processReelFallenBelowDestinationRow(ReelTile reelTile, TupleValueIndex[] reelsAboveMe) {
        int bottomDeletedReelIndex = findReel((int) reelTile.getDestinationX(), 40);
        if (bottomDeletedReelIndex>=0) {
            ReelTile bottomDeletedReel = reelTiles.get(bottomDeletedReelIndex);
            swapReelsForFallenReel(reelTile, bottomDeletedReel);
            for (int i = 0; i < reelsAboveMe.length; i++) {
                bottomDeletedReel = reelTiles.get(findReel((int) reelTile.getDestinationX(), 40 + 40 * (i + 1)));
                swapReelsForFallenReel(reelTiles.get(reelsAboveMe[i].index), bottomDeletedReel);
            }
        }
    }

    private void swapReelsForFallenReel(ReelTile reelTileA, ReelTile reelTileB) {
        float savedDestinationY = reelTileA.getDestinationY();
        reelTileA.setDestinationY(reelTileB.getDestinationY());
        reelTileA.setY(reelTileB.getDestinationY());
        reelTileA.unDeleteReelTile();
        reelTileB.setDestinationY(savedDestinationY);
    }

    private Array<ReelTile> getReelTilesFromAnimatedReels(Array<AnimatedReel> animatedReels) {
        Array<ReelTile> reelTiles = new Array<>();
        for (AnimatedReel animatedReel : animatedReels)
            reelTiles.add(animatedReel.getReel());
        return reelTiles;
    }

    private int findReel(int destinationX, int destinationY) {
        int findReelIndex = 0;
        while (findReelIndex < reelTiles.size) {
            if (isReelFound(destinationX, destinationY, findReelIndex)) {
                return findReelIndex;
            }
            findReelIndex++;
        }
        return -1;
    }

    private boolean isReelFound(int destinationX, int destinationY, int findReelIndex) {
        return (reelTiles.get(findReelIndex).getDestinationX() == destinationX) &
            ((reelTiles.get(findReelIndex).getDestinationY() == destinationY) |
                (reelTiles.get(findReelIndex).getDestinationY()+SCREEN_OFFSET == destinationY));
    }

    private int findReelUsingSnapYIgnoringDeletedReels(int destinationX, int destinationY) {
        int findReelIndex = 0;
        while (findReelIndex < reelTiles.size) {
            if (isReelFoundUsingSnapYIgnoringDeletedReels(destinationX, destinationY, findReelIndex))
                return findReelIndex;
            findReelIndex++;
        }
        return -1;
    }

    private boolean isReelFoundUsingSnapYIgnoringDeletedReels(
        int destinationX,
        int destinationY,
        int findReelIndex) {
        if (reelTiles.get(findReelIndex).isReelTileDeleted())
            return false;
        return (reelTiles.get(findReelIndex).getDestinationX() == destinationX) &
            ((reelTiles.get(findReelIndex).getSnapY() == destinationY) |
                (reelTiles.get(findReelIndex).getSnapY()+SCREEN_OFFSET == destinationY));
    }

    private void decrementReelsLeftToFall() {
        numberOfReelsToFall--;
    }

    public void checkForReelsStoppedFalling() {
        for (Body reelBoxBody : reelBodies) {
            if (reelBoxBody != null)
                if (PhysicsManagerCustomBodies.isStopped(reelBoxBody)) {
                    AnimatedReel animatedReel = (AnimatedReel) reelBoxBody.getUserData();
                    if (!animatedReel.getReel().isReelTileDeleted())
                        if (!animatedReel.getReel().isStoppedFalling()) {
                            animatedReel.getReel().setIsStoppedFalling(true);
                            reelsStoppedFalling--;
                        }
                }
        }
    }

    public void printSlotMatrix() {
        PuzzleGridTypeReelTile.printGrid(
            PuzzleGridTypeReelTile.populateMatchGridStatic(
                reelTiles,
                new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT)
            )
        );
        System.out.println();
    }

    public Array<ReelTile> checkForDuplicateReels() {
        Array<ReelTile> duplicateReels = new Array<>();
        reelPoints.clear();
        for (ReelTile reelTile : reelTiles)
            if (isDuplicateReel(reelTile))
                duplicateReels.add(reelTile);
        return duplicateReels;
    }

    private boolean isDuplicateReel(ReelTile reelTile) {
        int r = PuzzleGridTypeReelTile.getRowFromLevel(
            reelTile.getDestinationY(),
            SlotPuzzleConstants.GAME_LEVEL_HEIGHT);
        int c = PuzzleGridTypeReelTile.getColumnFromLevel(reelTile.getDestinationX());
        Point point = new Point(r, c, SlotPuzzleConstants.GAME_LEVEL_WIDTH);
        boolean isNotADuplicate = reelPoints.add(point);
        if (!isNotADuplicate)
            reelPoints.add(point);
        return !isNotADuplicate;
    }

    public void printColumn(int column) {
        for (ReelTile reelTile : reelTiles) {
            if (reelTile.getDestinationX() == column)
                System.out.print(" "+reelTile.getIndex());
        }
        System.out.println();
    }
}
