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

package com.ellzone.slotpuzzle.level.creator.utils.box2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle.screens.PlayScreen;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;

import static com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile.getColumnFromLevel;
import static com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile.getRowFromLevel;

public class AnimatedReelsMatrixCreator {
    public static final int SCREEN_OFFSET = 400;
    private PhysicsManagerCustomBodies physicsEngine;
    private Texture slotReelScrollTexture;
    private int spriteWidth, spriteHeight;
    private TweenManager tweenManager;
    private int numberOfReelsToFall = 0;

    public AnimatedReelsMatrixCreator() {}

    public AnimatedReelsMatrixCreator(PhysicsManagerCustomBodies physicsEngine,
                                      Texture slotReelScrollTexture,
                                      int spriteWidth,
                                      int spriteHeight,
                                      TweenManager tweenManager) {
        this.physicsEngine = physicsEngine;
        this.slotReelScrollTexture = slotReelScrollTexture;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.tweenManager = tweenManager;
    }

    public Array<AnimatedReel> createAnimatedReelsFromSlotPuzzleMatrix(
        int[][] slotPuzzleMatrix) {
        return
            createAnimatedReelsFromSlotPuzzleMatrix(slotPuzzleMatrix, true);
    }

    public Array<AnimatedReel> createAnimatedReelsFromSlotPuzzleMatrix(
        int[][] slotPuzzleMatrix, boolean isSpinning) {
        Array<AnimatedReel> animatedReels = new Array<AnimatedReel>();
        int numberOfAnimatedReelsCreated = 0;
        for (int r = 0; r < slotPuzzleMatrix.length; r++) {
            for (int c = 0; c < slotPuzzleMatrix[0].length; c++) {
                animatedReels.add(
                    createAnimatedReel(
                        (int) PlayScreen.PUZZLE_GRID_START_X + (c * 40) + 20,
                        ((slotPuzzleMatrix.length - 1 - r) * 40) + 40,
                        slotPuzzleMatrix[r][c],
                        numberOfAnimatedReelsCreated,
                        isSpinning));
                if (slotPuzzleMatrix[r][c] < 0)
                    animatedReels.get(numberOfAnimatedReelsCreated).getReel().deleteReelTile();
                else
                    numberOfReelsToFall++;
                numberOfAnimatedReelsCreated++;
            }
        }
        return animatedReels;
    }

    public Array<AnimatedReel> createAnimatedReelsFromSlotPuzzleMatrix(
        int[][] slotPuzzleMatrix,
        int screenOffSet) {
        Array<AnimatedReel> animatedReels = new Array<AnimatedReel>();
        int numberOfAnimatedReelsCreated = 0;
        for (int r = 0; r < slotPuzzleMatrix.length; r++) {
            for (int c = 0; c < slotPuzzleMatrix[0].length; c++) {
                animatedReels.add(
                    createAnimatedReel(
                        (int) PlayScreen.PUZZLE_GRID_START_X + (c * 40) + 20,
                        ((slotPuzzleMatrix.length - 1 - r) * 40) + 40,
                        slotPuzzleMatrix[r][c],
                        numberOfAnimatedReelsCreated,
                        screenOffSet)
                );
                if (slotPuzzleMatrix[r][c] < 0)
                    animatedReels.get(numberOfAnimatedReelsCreated).getReel().deleteReelTile();
                else
                    numberOfReelsToFall++;
                numberOfAnimatedReelsCreated++;
            }
        }
        return animatedReels;
    }

    public Array<AnimatedReel> updateAnimatedReelsFromSlotPuzzleMatrix(
        int[][] slotPuzzleMatrix,
        Array<AnimatedReel> animatedReels,
        Array<Integer> replacementReelBoxes) {
        int r, c;

        for (int i = 0; i < animatedReels.size; i++) {
            c = getColumnFromLevel(animatedReels.get(i).getReel().getDestinationX());
            r = getRowFromLevel(animatedReels.get(i).getReel().getDestinationY(), slotPuzzleMatrix.length);

            if (slotPuzzleMatrix[r][c] < 0) {
                animatedReels.get(i).getReel().deleteReelTile();
                replacementReelBoxes.add(i);
            } else
                animatedReels.get(i).getReel().setEndReel(slotPuzzleMatrix[r][c]);
        }
        return animatedReels;
    }

    public int getNumberOfReelsToFall() {
        return numberOfReelsToFall;
    }

    public void setNumberOfReelsToFall(int numberOfReelsToFall) {
        this.numberOfReelsToFall = numberOfReelsToFall;
    }

    public void setSpriteWidth(int spriteWidth) {
        this.spriteWidth = spriteWidth;
    }

    public void setSpriteHeight(int spriteHeight) {
        this.spriteHeight = spriteHeight;
    }

    private AnimatedReel createAnimatedReel(int x, int y, int endReel, int index) {
        AnimatedReel animatedReel = getAnimatedReel(x, y, endReel);
        setUpReelTileInAnimatedReel(index, animatedReel);
        return animatedReel;
    }

    private AnimatedReel createAnimatedReel(int x, int y, int endReel, int index, boolean isSpinning) {
        AnimatedReel animatedReel = getAnimatedReel(x, y, endReel, isSpinning);
        setUpReelTileInAnimatedReel(index, animatedReel);
        return animatedReel;
    }

    private AnimatedReel createAnimatedReel(
        int x, int y, int endReel, int index, int screenOffset) {
        AnimatedReel animatedReel = getAnimatedReel(x, y, endReel);
        setUpReelTileInAnimatedReel(index, animatedReel, screenOffset);
        return animatedReel;
    }

    private AnimatedReel createAnimatedReel(
        int x, int y, int endReel, int index, int screenOffset, boolean isSpinning) {
        AnimatedReel animatedReel = getAnimatedReel(x, y, endReel, isSpinning);
        setUpReelTileInAnimatedReel(index, animatedReel, screenOffset);
        return animatedReel;
    }


    private void setUpReelTileInAnimatedReel(int index, AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        reelTile.setDestinationX(reelTile.getX());
        reelTile.setDestinationY(reelTile.getY());
        reelTile.setY(reelTile.getY() + SCREEN_OFFSET);
        reelTile.setIsFallen(false);
        reelTile.setIsStoppedFalling(false);
        reelTile.setIndex(index);
    }

    private void setUpReelTileInAnimatedReel(int index, AnimatedReel animatedReel, int screenOffset) {
        ReelTile reelTile = animatedReel.getReel();
        reelTile.setDestinationX(reelTile.getX());
        reelTile.setDestinationY(reelTile.getY());
        reelTile.setY(reelTile.getY() + screenOffset);
        reelTile.setIsFallen(false);
        reelTile.setIsStoppedFalling(false);
        reelTile.setIndex(index);
    }

    private AnimatedReel getAnimatedReel(int x, int y, int endReel) {
        AnimatedReel animatedReel = new AnimatedReel(
            slotReelScrollTexture,
            x,
            y,
            spriteWidth,
            spriteHeight,
            spriteWidth,
            spriteHeight,
            0,
            tweenManager);
        animatedReel.setSx(0);
        animatedReel.setEndReel(endReel);
        return animatedReel;
    }

    private AnimatedReel getAnimatedReel(int x, int y, int endReel, boolean isSpinning) {
        AnimatedReel animatedReel = getAnimatedReel(x, y, endReel);
        if (isSpinning) {
            animatedReel.setupSpinning();
            animatedReel.getReel().startSpinning();
        }
        return animatedReel;
    }

    public Array<Body> createBoxBodiesFromAnimatedReels(
        Array<AnimatedReel> animatedReels,
        Array<Body> reelBoxBodies)  {
        for (AnimatedReel animatedReel : animatedReels) {
            if (!animatedReel.getReel().isReelTileDeleted())
                reelBoxBodies.add(createBoxBody(animatedReel, false));
            else
                reelBoxBodies.add(null);
        }
        return reelBoxBodies;
    }

    private Body createBoxBody(AnimatedReel animatedReel, boolean isActive) {
        Body reelTileBody = createReelTileBodyAt(
            (int) animatedReel.getReel().getX(),
            (int) animatedReel.getReel().getY());
        reelTileBody.setActive(isActive);
        reelTileBody.setUserData(animatedReel);
        return reelTileBody;
    }

    private Body createReelTileBodyAt(int x, int y) {
        return physicsEngine.createBoxBody(
            BodyDef.BodyType.DynamicBody,
            x,
            y,
            19,
            19,
            true);
    }

    public Array<Body> updateBoxBodiesFromAnimatedReels(
        Array<AnimatedReel> animatedReels,
        Array<Body> reelBoxBodies) {
        for (AnimatedReel animatedReel : animatedReels)
            updateBoxBodyFromAnimatedReel(reelBoxBodies, animatedReel);
        return reelBoxBodies;
    }
    private void updateBoxBodyFromAnimatedReel(Array<Body> reelBoxBodies, AnimatedReel animatedReel) {
        ReelTile reelTile = animatedReel.getReel();
        if (!reelTile.isReelTileDeleted()) {
            if (reelBoxBodies.get(reelTile.getIndex()) == null)
                reelBoxBodies.set(
                    reelTile.getIndex(),
                    createBoxBody(animatedReel, false));
            else
                updateBoxBody(
                    animatedReel,
                    false,
                    reelBoxBodies.get(reelTile.getIndex()));
        } else {
            if (reelBoxBodies.get(reelTile.getIndex()) != null)
                updateBoxBody(
                    animatedReel,
                    false,
                    reelBoxBodies.get(animatedReel.getReel().getIndex()));
        }
    }

    public void updateBoxBody(AnimatedReel animatedReel, boolean isActive, Body reelTileBody) {
        reelTileBody.setTransform(
            (animatedReel.getReel().getX() + 20) / 100,
            (animatedReel.getReel().getY() + 400) / 100,
            0);
        reelTileBody.setActive(isActive);
        reelTileBody.setUserData(animatedReel);
    }

    public Array<ReelTile> getReelTilesFromAnimatedReels(Array<AnimatedReel> animatedReels) {
        Array<ReelTile> reelTiles = new Array<>();
        for (AnimatedReel animatedReel : animatedReels)
            reelTiles.add(animatedReel.getReel());
        return reelTiles;
    }

}
