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
import com.badlogic.gdx.ai.msg.MessageManager;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Timer;
import com.ellzone.slotpuzzle.SlotPuzzleGame;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.finitestatemachine.PlayState;
import com.ellzone.slotpuzzle.finitestatemachine.PlayStates;
import com.ellzone.slotpuzzle.level.LevelDoor;
import com.ellzone.slotpuzzle.level.creator.LevelCreatorSimple;
import com.ellzone.slotpuzzle.level.sequence.PlayScreenIntroSequence;
import com.ellzone.slotpuzzle.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle.physics.PhysicsManagerCustomBodies;
import com.ellzone.slotpuzzle.physics.ReelSink;
import com.ellzone.slotpuzzle.physics.contact.AnimatedReelsManager;
import com.ellzone.slotpuzzle.physics.contact.BoxHittingBoxContactListener;
import com.ellzone.slotpuzzle.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle.puzzlegrid.ReelTileGridValue;
import com.ellzone.slotpuzzle.scene.MapTile;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.tweenengine.BaseTween;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle.utils.Random;
import com.ellzone.slotpuzzle.utils.SlowMotion;

import static com.ellzone.slotpuzzle.level.creator.LevelCreator.FALLING_REELS_LEVEL_TYPE;
import static com.ellzone.slotpuzzle.messaging.MessageType.PauseAudio;
import static com.ellzone.slotpuzzle.messaging.MessageType.PlayAudio;
import static com.ellzone.slotpuzzle.messaging.MessageType.ReelSinkReelsLeftToFall;
import static com.ellzone.slotpuzzle.messaging.MessageType.ReelsLeftToFall;
import static com.ellzone.slotpuzzle.messaging.MessageType.StopAudio;
import static com.ellzone.slotpuzzle.messaging.MessageType.SwapReelsAboveMe;

public class PlayScreenFallingReels extends PlayScreen {
    public static final int LEVEL_TIME_LENGTH = 120;
    private PhysicsManagerCustomBodies physics;
    private int currentReel = 0;
    private LevelCreatorSimple levelCreator;
    private Array<Body> reelBoxes;
    private AnimatedReelsManager animatedReelsManager;
    private int numberOfReelBoxesAsleep = 0;
    private int numberOfReelBoxesCreated = 0;
    private boolean reelsStoppedMoving = false;
    private SlowMotion slowMotion;

    public PlayScreenFallingReels(SlotPuzzleGame game, LevelDoor levelDoor, MapTile mapTile) {
        super(game, levelDoor, mapTile);
        initialiseFallingReels();
    }

    protected void createPlayScreen() {
        initialisePlayFiniteStateMachine();
        playState = PlayStates.INITIALISING;
        slowMotion = new SlowMotion(false);
        initialiseWorld();
        initialiseDependencies();
        setupPlayScreen();
        initialisePhysics();
        configureLevelCreator();
        messageManager = setUpMessageManager();
        activateReelBoxes();
        createReelIntroSequence();
    }

    protected void configureLevelCreator() {
        createLevelCreator();
        reelBoxes = levelCreator.getReelBoxes();
        setupAnimatedReelsManager();
    }

    private void createLevelCreator() {
        levelCreator = new LevelCreatorSimple(
            playScreenLevel,
            levelDoor,
            physics,
            playStateMachine);
    }

    private void setupAnimatedReelsManager() {
        animatedReelsManager = new AnimatedReelsManager(animatedReels, reelBoxes);
        int numberOfReelsToFall = 0;
        animatedReelsManager.setNumberOfReelsToFall(numberOfReelsToFall);
        levelCreator.setAnimatedReelsManager(animatedReelsManager);
    }

    protected MessageManager setUpMessageManager() {
        messageManager = MessageManager.getInstance();
        messageManager.addListeners(
            animatedReelsManager,
            SwapReelsAboveMe.index,
            ReelsLeftToFall.index,
            ReelSinkReelsLeftToFall.index);
        messageManager.addListeners(audioManager,
            PlayAudio.index,
            StopAudio.index,
            PauseAudio.index);
        return messageManager;
    }

    protected void createReelIntroSequence() {
        createStartReelTimer();
        PlayScreenIntroSequence playScreenIntroSequence =
            new PlayScreenIntroSequence(
                getReelTilesFromAnimatedReels(animatedReels), game.getTweenManager());
        playScreenIntroSequence.createReelIntroSequence(introSequenceCallback);
    }

    private Array<ReelTile> getReelTilesFromAnimatedReels(Array<AnimatedReel> animatedReels) {
        Array<ReelTile> reelTiles = new Array<>();
        for (AnimatedReel animatedReel : animatedReels)
            reelTiles.add(animatedReel.getReel());
        return reelTiles;
    }

    private void createStartReelTimer() {
        Timer.schedule(new Timer.Task(){
                           @Override
                           public void run() {
                               startAReel();
                           }
                       }
            , 0.0f
            , 0.02f
            , reelTiles.size
        );
    }

    private void startAReel() {
        if (currentReel < reelTiles.size) {
            animatedReels.get(currentReel).setupSpinning();
            reelTiles.get(currentReel++).setSpinning(true);
        }
    }

    protected void delegateIntroSequenceCallback(int type, ReelTile reelTile) {
        if (type == TweenCallback.END) {
            playState = PlayStates.INTRO_POPUP;
            playScreenPopUps.setPopUpSpritePositions();
            playScreenPopUps.getLevelPopUp().showLevelPopUp(null);
            playScreenLevel.getHud().resetWorldTime(LEVEL_TIME_LENGTH);
            playScreenLevel.getHud().startWorldTimer();
            levelCreator.createStartRandomReelBoxTimer();
            levelCreator.allReelsHaveStoppedSpinning();
        }
    }

    private void activateReelBoxes() {
        for (Body reelBox : reelBoxes)
            if (!((AnimatedReel) (reelBox.getUserData())).getReel().isReelTileDeleted())
                reelBox.setActive(true);
    }

    private void initialiseFallingReels() {
        initialisePhysics();
    }

    private void initialisePhysics() {
        BoxHittingBoxContactListener contactListener = new BoxHittingBoxContactListener();
        box2dWorld.setContactListener(contactListener);
        physics = new PhysicsManagerCustomBodies(camera, box2dWorld, null);
        createReelSink();
    }

    private void createReelSink() {
        ReelSink reelSink = new ReelSink(physics);
        reelSink.createReelSink(
            SlotPuzzleConstants.VIRTUAL_WIDTH / 2,
            SlotPuzzleConstants.VIRTUAL_HEIGHT / 2 + 20,
            12,
            9,
            40,
            40);
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
            if (playStateMachine.getStateMachine().getCurrentState() == PlayState.PLAY &&
                playState != PlayStates.BONUS_LEVEL_ENDED) {
                Gdx.app.debug(SLOTPUZZLE_SCREEN, "Play");
                processIsTileClicked();
            }
        }
    }

    private void processHideLevelPopUp(int type) {
        if (type == TweenCallback.END) {
            playStateMachine.getStateMachine().changeState(PlayState.PLAY);
            playScreenLevel.getHud().resetWorldTime(LEVEL_TIME_LENGTH);
            playScreenLevel.getHud().startWorldTimer();
            if (levelDoor.getLevelType().equals(FALLING_REELS_LEVEL_TYPE))
                System.out.println("Falling Reels Level");
        }
    }

    private void processIsTileClicked() {
        Vector2 tileClicked = getTileClicked();
        processTileClicked(tileClicked);
    }

    private Vector2 getTileClicked() {
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();
        Vector2 newPoints = new Vector2(touchX, touchY);
        newPoints = viewport.unproject(newPoints);
        int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / SlotPuzzleConstants.TILE_WIDTH;
        int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / SlotPuzzleConstants.TILE_HEIGHT;
        r = SlotPuzzleConstants.GAME_LEVEL_HEIGHT - 1 - r ;
        return new Vector2(c, r);
    }

    private void processTileClicked(Vector2 tileClicked) {
        int r = (int) tileClicked.y;
        int c = (int) tileClicked.x;
        if (r>=0 && r< SlotPuzzleConstants.GAME_LEVEL_HEIGHT && c>=0 && c< SlotPuzzleConstants.GAME_LEVEL_WIDTH) {
            ReelTileGridValue[][] grid = levelCreator.populateMatchGrid(
                reelTiles,
                new GridSize(
                    SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                    SlotPuzzleConstants.GAME_LEVEL_HEIGHT));
            if (grid[r][c] != null) {
                ReelTile reel = reelTiles.get(grid[r][c].index);
                AnimatedReel animatedReel = levelCreator.getAnimatedReels().get(grid[r][c].index);
                processReelClicked(reel, animatedReel);
            }
        }
    }

    private void processReelClicked(ReelTile reel, AnimatedReel animatedReel) {
        if (!reel.isReelTileDeleted()) {
            if (reel.isSpinning()) {
                if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE)
                    setEndReelWithCurrentReel(reel);
            } else
            if (!reel.getFlashTween()) {
                startReelSpinning(reel, animatedReel);
            }
        }
    }

    private void setEndReelWithCurrentReel(ReelTile reel) {
        reel.setEndReel(reel.getCurrentReel());
        displaySpinHelp = true;
        displaySpinHelpSprite = reel.getCurrentReel();
        playScreenLevel.getHud().addScore(-1);
        pullLeverSound.play();
        reelSpinningSound.play();
    }

    private void startReelSpinning(ReelTile reel, AnimatedReel animatedReel) {
        reel.setEndReel(Random.getInstance().nextInt(reelSprites.getSprites().length - 1));
        reel.startSpinning();
        levelCreator.setNumberOfReelsSpinning(levelCreator.getNumberOfReelsSpinning() + 1);
        reel.setSy(0);
        animatedReel.reinitialise();
        playScreenLevel.getHud().addScore(-1);
        if (pullLeverSound != null)
            pullLeverSound.play();
    }



    protected void update(float delta) {
        if (slowMotion.isSlowMotionEnabled() &
            slowMotion.isSlowMotionTimerEnded(delta))
            return;
        playStateMachine.update();
        game.getTweenManager().update(delta);
        levelCreator.update(delta);
        renderer.setView(camera);
        updateAnimatedReels(delta);
        playScreenLevel.getHud().update(delta);
        if (isOutOfTime())
            weAreOutOfTime();
        playScreenLevel.getFrameRate().update();
        checkForGameOverCondition();
    }

    protected void weAreOutOfTime() {
        playState = PlayStates.BONUS_LEVEL_ENDED;
        gameOver = true;
        mapTileLevel.getLevel().setLevelCompleted();
        mapTileLevel.getLevel().setScore(playScreenLevel.getHud().getScore());
        playScreenPopUps.setLevelBonusSpritePositions();
        playScreenPopUps.getLevelBonusCompletedPopUp().showLevelPopUp(null);
    }

    protected void renderGame(float delta) {
        update(delta);
        handleInput();
        renderer.render();
        renderMainGameElements();
        drawCurrentPlayState(delta);
        renderHud();
        stage.draw();
        playScreenLevel.getFrameRate().render();
    }

    protected void renderMainGameElements() {
        game.batch.begin();
        renderHiddenPattern();
        renderAnimatedReels();
        renderScore();
        renderSpinHelper();
        game.batch.end();
        renderReelBoxes(game.batch, reelBoxes);
        levelCreator.render(game.batch, 0, viewport);
        if (isReelsStoppedMoving())
            processReelsStoppedMoving();
        else
            reelsStoppedMoving = false;

        game.batch.begin();
        shapeRenderer.setProjectionMatrix(viewport.getCamera().combined);
        renderAnimatedReelsFlash();
        game.batch.end();
        renderWorld();
        renderRayHandler();
    }

    private void renderReelBoxes(SpriteBatch batch, Array<Body> reelBoxes) {
        batch.begin();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        numberOfReelBoxesCreated = 0;
        numberOfReelBoxesAsleep = 0;
        for (Body reelBox : reelBoxes) {
            if (reelBox != null) {
                float angle = MathUtils.radiansToDegrees * reelBox.getAngle();
                AnimatedReel animatedReel = (AnimatedReel) reelBox.getUserData();
                if (!animatedReel.getReel().isReelTileDeleted()) {
                    renderReel(batch, reelBox, angle, animatedReel.getReel());
                    numberOfReelBoxesCreated++;
                }
                if (!reelBox.isAwake())
                    numberOfReelBoxesAsleep++;
            }
        }
        batch.end();
    }

    private void renderReel(SpriteBatch batch, Body reelBox, float angle, ReelTile reelTile) {
        reelTile.setPosition(
            reelBox.getPosition().x * 100 - 20,
            reelBox.getPosition().y * 100 - 20);
        reelTile.updateReelFlashSegments(
            reelBox.getPosition().x * 100 - 20,
            reelBox.getPosition().y * 100 - 20);
        reelTile.setOrigin(0, 0);
        reelTile.setSize(40, 40);
        reelTile.setRotation(angle);
        reelTile.draw(batch);
    }

    private boolean isReelsStoppedMoving() {
        return numberOfReelBoxesAsleep == numberOfReelBoxesCreated;
    }

    private void processReelsStoppedMoving() {
        if (animatedReelsManager.getNumberOfReelsToFall() <= 0 &
            levelCreator.getNumberOfReelsSpinning() < 1) {
            if (!reelsStoppedMoving) {
                reelsStoppedMoving = true;
                levelCreator.allReelsHaveStoppedSpinning();
            }
        }
    }
}
