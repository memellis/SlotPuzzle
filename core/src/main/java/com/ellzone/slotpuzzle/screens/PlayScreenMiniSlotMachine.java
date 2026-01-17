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

package com.ellzone.slotpuzzle.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.SlotPuzzleGame;
import com.ellzone.slotpuzzle.effects.ScoreAccessor;
import com.ellzone.slotpuzzle.finitestatemachine.PlayState;
import com.ellzone.slotpuzzle.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle.level.creator.LevelCallback;
import com.ellzone.slotpuzzle.level.creator.LevelCreator;
import com.ellzone.slotpuzzle.level.LevelDoor;
import com.ellzone.slotpuzzle.level.creator.PlayScreenLevel;
import com.ellzone.slotpuzzle.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle.puzzlegrid.PuzzleGridTypeReelTile;
import com.ellzone.slotpuzzle.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle.scene.MapTile;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.lights.HoldLightButton;
import com.ellzone.slotpuzzle.sprites.lights.LightButton;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.sprites.score.ScoreSprite;
import com.ellzone.slotpuzzle.sprites.slothandle.SlotHandleSprite;
import com.ellzone.slotpuzzle.tweenengine.BaseTween;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;

import java.util.Comparator;

import aurelienribon.tweenengine.equations.Quad;

public class PlayScreenMiniSlotMachine extends PlayScreen {

    public static final int LEVEL_TIME_LENGTH = 120;
    private final int[][] reelGrid = new int[3][3];
    private Array<Array<Vector2>> rowMatchesToDraw;
    private ShapeRenderer shapeRenderer;
    private Array<HoldLightButton> holdLightButtons;
    private Array<SlotHandleSprite> slotHandles;

    public PlayScreenMiniSlotMachine(SlotPuzzleGame game, LevelDoor levelDoor, MapTile mapTile) {
        super(game, levelDoor, mapTile);
        initialiseMiniSlotMachine();
    }

    private void initialiseMiniSlotMachine() {
        rowMatchesToDraw = new Array<>();
        shapeRenderer = new ShapeRenderer();
    }

    protected void getLevelEntities() {
        animatedReels = playScreenLevel.getAnimatedReels();
        reelTiles = playScreenLevel.getReelTiles();
        holdLightButtons = playScreenLevel.getHoldLightButtons();
        slotHandles = playScreenLevel.getSlotHandles();
    }

    protected void loadLevel() {
        playScreenLevel =  new PlayScreenLevel(
            this, game, levelDoor);
        playScreenLevel.loadLevel(
            mapTileLevel,
            new LevelCallback() {
                @Override
                public void onEvent(ReelTile source) {
                    if (levelDoor.getLevelType().equals(LevelCreator.MINI_SLOT_MACHINE_LEVEL_TYPE))
                        processReelStopped();
                }

            },
            new LevelCallback() {
                @Override
                public void onEvent(ReelTile source) {
                    processReelFlashingStopped(source);
                }
            });
        getLevelEntities();
    }

    private void processReelStopped() {
        reelsSpinning--;
        if (reelsSpinning < 1)
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY ||
                playStateMachine.getStateMachine().getCurrentState() == PlayState.INTRO_SPINNING_SEQUENCE)
                matchReels();
    }

    private void processReelFlashingStopped(ReelTile reelTile) {
        reelScoreAnimation(reelTile);
    }

    protected void reelScoreAnimation(ReelTile source) {
        Array<Vector2> sortedReelFlashSegments = new Array<>(source.getReelFlashSegments());
        sortedReelFlashSegments.sort(new Comparator<Vector2>() {
            @Override
            public int compare(Vector2 v1, Vector2 v2) {
                return Float.compare(v1.y, v2.y);
            }
        });
        if (sortedReelFlashSegments.size > 0) {
            float baseFlashSegmentY = sortedReelFlashSegments.get(0).y;
            for (Vector2 reelFlashSegment : new Array.ArrayIterator<>(sortedReelFlashSegments)) {
                ScoreSprite score =
                    new ScoreSprite(reelFlashSegment.x,
                        reelFlashSegment.y,
                        (int) (source.getEndReel() + (reelFlashSegment.y - baseFlashSegmentY) / source.getWidth() + 1));
                scores.add(score);
                Timeline.createSequence()
                    .beginParallel()
                    .push(SlotPuzzleTween.to(score, ScoreAccessor.POS_XY, 2.0f).targetRelative(random.nextInt(20), random.nextInt(160)).ease(Quad.IN))
                    .push(SlotPuzzleTween.to(score, ScoreAccessor.SCALE_XY, 2.0f).target(2.0f, 2.0f).ease(Quad.IN))
                    .end()
                    .setUserData(score)
                    .setCallback(new TweenCallback() {
                        @Override
                        public void onEvent(int type, BaseTween<?> source) {
                            processDeleteScore(type, source);
                        }
                    })
                    .setCallbackTriggers(TweenCallback.COMPLETE)
                    .start(game.getTweenManager());
            }
            source.clearReelFlashSegments();
        }
    }

    private void processDeleteScore(int type, BaseTween<?> source) {
        if (type == TweenCallback.COMPLETE) {
            ScoreSprite score = (ScoreSprite) source.getUserData();
            scores.removeValue(score, false);
        }
    }

    private void matchReels() {
        if (playStateMachine.getStateMachine().getCurrentState() == PlayState.INTRO_FLASHING_SEQUENCE)
            playScreenLevel.getFlashSlots().setReelsStartedFlashing(true);

        captureReelPositions();
        PuzzleGridTypeReelTile puzzleGrid = new PuzzleGridTypeReelTile();
        ReelTileGridValue[][] matchGrid = puzzleGrid.populateMatchGrid(reelGrid);
        PuzzleGridTypeReelTile puzzleGridTypeReelTile = new PuzzleGridTypeReelTile();
        matchGrid = puzzleGridTypeReelTile.createGridLinks(matchGrid);
        PuzzleGridTypeReelTile.printGrid(matchGrid);
        matchRowsToDraw(matchGrid, puzzleGridTypeReelTile);

        if (playStateMachine.getStateMachine().getCurrentState() == PlayState.INTRO_FLASHING_SEQUENCE)
            playScreenLevel.getFlashSlots().setFinishedMatchingSlots(true);
    }

    private void matchRowsToDraw(ReelTileGridValue[][] matchGrid, PuzzleGridTypeReelTile puzzleGridTypeReelTile) {
        Array<ReelTileGridValue> depthSearchResults;
        rowMatchesToDraw = new Array<>();
        for (ReelTileGridValue[] reelTileGridValues : matchGrid) {
            depthSearchResults = puzzleGridTypeReelTile.depthFirstSearchIncludeDiagonals(reelTileGridValues[0]);
            if (puzzleGridTypeReelTile.isRow(depthSearchResults, matchGrid)) {
                rowMatchesToDraw.add(drawMatches(depthSearchResults));
                playScreenLevel.getFlashSlots().flashSlotsForMiniSlotMachine(depthSearchResults);
            }
        }
    }

    private Array<Vector2> drawMatches(Array<ReelTileGridValue> depthSearchResults) {
        Array<Vector2> points = new Array<>();
        for (ReelTileGridValue cell : new Array.ArrayIterator<>(depthSearchResults)) {
            playScreenLevel.getHud().addScore((reelGrid[cell.r][cell.c] + 1));
            points.add(new Vector2(340 + cell.c * 40, 300 - cell.r * 40));
        }
        return points;
    }

    private void captureReelPositions() {
        for (int r = 0; r < reelGrid.length; r++)
            for (int c = 0; c < reelGrid[0].length; c++)
                reelGrid[r][c] = getReelPosition(r, c);
    }

    private int getReelPosition(int r, int c) {
        int reelPosition = reelTiles.get(c).getEndReel() + r;
        if (reelPosition < 0)
            reelPosition = sprites.length - 1;
        else if (reelPosition > sprites.length - 1)
            reelPosition = 0;
        return reelPosition;
    }

    private void handleReelsTouchedSlotMachine(float touchX, float touchY) {
        for (AnimatedReel animatedReel : new Array.ArrayIterator<>(animatedReels)) {
            if (animatedReel.getReel().getBoundingRectangle().contains(touchX, touchY)) {
                clearRowMatchesToDraw();
                if (animatedReel.getReel().isSpinning()) {
                    if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                        int currentReel = animatedReel.getReel().getCurrentReel();
                        processReelTouchedWhileSpinning(
                            animatedReel.getReel(),
                            currentReel,
                            currentReel + 1 == animatedReel.getReel().getNumberOfReelsInTexture() ?
                                0 : currentReel + 1);
                    }
                } else {
                    animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                    animatedReel.reinitialise();
                    animatedReel.getReel().startSpinning();
                    reelsSpinning++;
                }
            }
        }
    }

    private void clearRowMatchesToDraw() {
        if (rowMatchesToDraw.size > 0)
            rowMatchesToDraw.removeRange(0, rowMatchesToDraw.size - 1);
    }

    protected void handleInput() {
        if (Gdx.input.justTouched()) {
            float touchX = Gdx.input.getX();
            float touchY = Gdx.input.getY();
            Vector3 unprojectTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojectTouch);
            switch (playState) {
                case INTRO_POPUP:
                    if (isOver(playScreenPopUps.getLevelPopUpSprites().get(0), unprojectTouch.x, unprojectTouch.y))
                        playScreenPopUps.getLevelPopUp().hideLevelPopUp(new TweenCallback() {
                            @Override
                            public void onEvent(int type, BaseTween<?> source) {
                                processHideLevelPopUp(type);
                            }
                        });
                    break;
                case BONUS_LEVEL_ENDED:
                    Gdx.app.debug(SLOTPUZZLE_SCREEN, "Bonus Level");
                    if (isOver(playScreenPopUps.getLevelBonusSprites().get(0), unprojectTouch.x, unprojectTouch.y))
                        playScreenPopUps.getLevelWonPopUp().hideLevelPopUp(levelWonCallback);
                    break;
                default:
                    break;
            }
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY && playState != PlayStates.BONUS_LEVEL_ENDED) {
                Gdx.app.debug(SLOTPUZZLE_SCREEN, "Play");
                if (levelDoor.getLevelType().equals(
                    SlotPuzzleConstants.MINI_SLOT_MACHINE_LEVEL_TYPE)) {
                    handleReelsTouchedSlotMachine(unprojectTouch.x, unprojectTouch.y);
                    handleSlotHandleIsTouch(unprojectTouch.x, unprojectTouch.y);
                    unprojectTouch = new Vector3(touchX, touchY, 0);
                    lightViewport.unproject(unprojectTouch);
                    handleLightButtonTouched(unprojectTouch.x, unprojectTouch.y);
                }
            }
        }
    }

    private void processHideLevelPopUp(int type) {
        if (type == TweenCallback.END) {
            playState = PlayStates.PLAYING;
            playScreenLevel.getHud().resetWorldTime(LEVEL_TIME_LENGTH);
            playScreenLevel.getHud().startWorldTimer();
            if (levelDoor.getLevelType().equals(
                SlotPuzzleConstants.MINI_SLOT_MACHINE_LEVEL_TYPE))
                matchReels();
        }
    }

    protected void handleLightButtonTouched(float touchX, float touchY) {
        for (LightButton lightButton : new Array.ArrayIterator<>(holdLightButtons))
            if (lightButton.getSprite().getBoundingRectangle().contains(touchX, touchY))
                lightButton.getLight().setActive(!lightButton.getLight().isActive());
    }

    private void handleSlotHandleIsTouch(float touchX, float touchY) {
        for (SlotHandleSprite slotHandle : new Array.ArrayIterator<>(slotHandles))
            if (slotHandle.getBoundingRectangle().contains(touchX, touchY))
                if (isReelsNotSpinning())
                    slotHandlePulled(slotHandle);
                else
                    reelStoppedSound.play();
    }

    private boolean isReelsNotSpinning() {
        boolean reelsNotSpinning = true;
        for (AnimatedReel animatedReel : new Array.ArrayIterator<>(animatedReels))
            if (animatedReel.getReel().isSpinning()) {
                reelsNotSpinning = false;
                break;
            }
        return reelsNotSpinning;
    }

    private void slotHandlePulled(SlotHandleSprite slotHandle) {
        slotHandle.pullSlotHandle();
        pullLeverSound.play();
        clearRowMatchesToDraw();
        int i = 0;
        for (AnimatedReel animatedReel : new Array.ArrayIterator<>(animatedReels)) {
            if (!holdLightButtons.get(i).getLight().isActive()) {
                animatedReel.setEndReel(random.nextInt(sprites.length - 1));
                animatedReel.reinitialise();
                animatedReel.getReel().startSpinning();
                reelsSpinning++;
            }
            i++;
        }
    }

    protected void weAreOutOfTime() {
        playState = PlayStates.BONUS_LEVEL_ENDED;
        gameOver = true;
        mapTileLevel.getLevel().setLevelCompleted();
        mapTileLevel.getLevel().setScore(playScreenLevel.getHud().getScore());
        playScreenPopUps.setLevelBonusSpritePositions();
        playScreenPopUps.getLevelBonusCompletedPopUp().showLevelPopUp(null);
    }

    protected void renderMainGameElements() {
        game.batch.begin();
        renderAnimatedReels();
        renderSlotHandle();
        renderScore();
        renderSpinHelper();
        game.batch.end();
        game.batch.begin();
        renderMatchedRows();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        renderAnimatedReelsFlash();
        game.batch.end();
        renderLightButtons();
        renderWorld();
        renderRayHandler();
    }

    protected void renderAnimatedReels() {
        for (AnimatedReel animatedReel : new Array.ArrayIterator<>(animatedReels))
            if (!animatedReel.getReel().isReelTileDeleted())
                animatedReel.draw(game.batch);
    }

    protected void renderAnimatedReelsFlash() {
        for (AnimatedReel animatedReel : new Array.ArrayIterator<>(animatedReels))
            if (!animatedReel.getReel().isReelTileDeleted())
                if (animatedReel.getReel().getFlashState() == ReelTile.FlashState.FLASH_ON)
                    animatedReel.draw(shapeRenderer);
    }

    protected void renderMatchedRows() {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.RED);
        for (Array<Vector2> matchedRow : new Array.ArrayIterator<>(rowMatchesToDraw))
            if (matchedRow.size >= 2)
                for (int i = 0; i < matchedRow.size - 1; i++)
                    shapeRenderer.rectLine(matchedRow.get(i).x, matchedRow.get(i).y,
                        matchedRow.get(i + 1).x, matchedRow.get(i + 1).y,
                        2);
        shapeRenderer.end();
    }

    protected void renderSlotHandle() {
        for (SlotHandleSprite slotHandle : new Array.ArrayIterator<>(slotHandles))
            slotHandle.draw(game.batch);
    }

    protected void renderLightButtons() {
        game.batch.setProjectionMatrix(lightViewport.getCamera().combined);
        game.batch.begin();
        for (HoldLightButton lightButton : new Array.ArrayIterator<>(holdLightButtons))
            lightButton.getSprite().draw(game.batch);
        game.batch.end();
    }
}
