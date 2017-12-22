/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.prototypes.minislotmachine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.ScoreAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Card;
import com.ellzone.slotpuzzle2d.level.LevelCreator;
import com.ellzone.slotpuzzle2d.level.LevelDoor;
import com.ellzone.slotpuzzle2d.level.MiniSlotMachineLevel;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import com.ellzone.slotpuzzle2d.puzzlegrid.PuzzleGridType;
import com.ellzone.slotpuzzle2d.puzzlegrid.TupleValueIndex;
import com.ellzone.slotpuzzle2d.scene.Hud;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.screens.PlayScreen;
import com.ellzone.slotpuzzle2d.sprites.AnimatedReel;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.sprites.Reels;
import com.ellzone.slotpuzzle2d.sprites.Score;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class MiniSlotMachineLevelPrototypeWithLevelCreator extends SPPrototypeTemplate {
    public static final int GAME_LEVEL_WIDTH = 12;
    public static final int GAME_LEVEL_HEIGHT = 9;
    public static final String REEL_OBJECT_LAYER = "Reels";
    public static final String HIDDEN_PATTERN_LEVEL_TYPE = "HiddenPattern";
    public static final String PLAYING_CARD_LEVEL_TYPE = "PlayingCard";
    public static final String MINI_SLOT_MACHINE_LEVEL_NAME = "Mini Slot Machine";
    public static final String BONUS_LEVEL_TYPE = "BonusLevelType";
    public static final String HIDDEN_PATTERN_LAYER_NAME = "Hidden Pattern Object";
    public static final int NUMBER_OF_SUITS = 4;
    public static final int NUMBER_OF_CARDS_IN_A_SUIT = 13;

    private String logTag = SlotPuzzleConstants.SLOT_PUZZLE + this.getClass().getName();
    private TiledMap miniSlotMachineLevel;
    private MapTile mapTile;
    private MapProperties levelProperties;
    private TextureAtlas reelAtlas, tilesAtlas, carddeckAtlas;
    private Array<DampenedSineParticle> dampenedSines;
    private Reels reels;
    private Array<ReelTile> reelTiles;
    private LevelDoor levelDoor;
    private Array<Score> scores;
    private Array<Card> cards;
    private Array<Integer> hiddenPlayingCards;
    private OrthogonalTiledMapRenderer tileMapRenderer;
    private int sW, sH;
    private Pixmap slotReelPixmap, slotReelScrollPixmap;
    private Texture slotReelTexture, slotReelScrollTexture;
    private int slotReelScrollheight;
    private int reelsSpinning;
    private Sound chaChingSound, pullLeverSound, reelSpinningSound, reelStoppedSound, jackpotSound;
    private Vector accelerator, velocityMin;
    private float acceleratorY, accelerateY, acceleratorFriction, velocityFriction, velocityY, velocityYMin;
    private float reelSlowingTargetTime;
    private Array<Timeline> endReelSeqs;
    private Timeline reelFlashSeq;
    private LevelCreator levelCreator;
    private boolean gameOver = false;
    private boolean inRestartLevel = false;
    private boolean win = false;
    private boolean displaySpinHelp;
    private int displaySpinHelpSprite;
    private int mapWidth, mapHeight, tilePixelWidth, tilePixelHeight;
    private Hud hud;

    @Override
    protected void initialiseOverride() {
        initialiseReels(this.annotationAssetManager);
        createSlotReelTexture();
        getAssets(annotationAssetManager);
        this.miniSlotMachineLevel = annotationAssetManager.get(AssetsAnnotation.MINI_SLOT_MACHINE_LEVEL);
        getMapProperties(this.miniSlotMachineLevel);
        initialiseLevelDoor();
        createPlayScreen();
        this.levelCreator = new LevelCreator(this.levelDoor, this.miniSlotMachineLevel, this.annotationAssetManager, this.carddeckAtlas, this.tweenManager, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT, PlayScreen.PlayStates.INITIALISING);
        this.reelTiles = this.levelCreator.getReelTiles();
        reelsSpinning = reelTiles.size - 1;
        hud = new Hud(batch);
        hud.setLevelName(levelDoor.levelName);
        levelCreator.setPlayState(PlayScreen.PlayStates.PLAYING);
    }

    private void getAssets(AnnotationAssetManager annotationAssetManager) {
        this.carddeckAtlas = annotationAssetManager.get(AssetsAnnotation.CARDDECK);
        this.chaChingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_CHA_CHING);
        this.pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        this.reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        this.reelStoppedSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
        this.jackpotSound = annotationAssetManager.get(AssetsAnnotation.SOUND_JACKPOINT);
    }

    private void getMapProperties(TiledMap level) {
        MapProperties mapProperties = level.getProperties();
        mapWidth = mapProperties.get("width", Integer.class);
        mapHeight = mapProperties.get("height", Integer.class);
        tilePixelWidth = mapProperties.get("tilewidth", Integer.class);
        tilePixelHeight = mapProperties.get("tileheight", Integer.class);
    }

    private void createSlotReelTexture() {
        slotReelPixmap = new Pixmap(PlayScreen.TILE_WIDTH, PlayScreen.TILE_HEIGHT, Pixmap.Format.RGBA8888);
        slotReelPixmap = PixmapProcessors.createDynamicScrollAnimatedPixmap(reels.getReels(), reels.getReels().length);
        slotReelTexture = new Texture(slotReelPixmap);
        slotReelScrollPixmap = new Pixmap((int) reels.getReelWidth(), (int)reels.getReelHeight(), Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reels.getReels());
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        slotReelScrollheight = slotReelScrollTexture.getHeight();
    }

    @Override
    protected void initialiseScreenOverride() {
    }

    private void createPlayScreen() {
        initialisePlayScreen();
    }

    private void initialiseReels(AnnotationAssetManager annotationAssetManager) {
        this.reels = new Reels(annotationAssetManager);
    }

    private void initialiseLevelDoor() {
        levelDoor = new LevelDoor();
        levelDoor.levelName = MINI_SLOT_MACHINE_LEVEL_NAME;
        levelDoor.levelType = BONUS_LEVEL_TYPE;
    }

    private void initialisePlayScreen() {
        this.tileMapRenderer = new OrthogonalTiledMapRenderer(miniSlotMachineLevel);
        this.font = new BitmapFont();
        this.sW = SlotPuzzleConstants.V_WIDTH;
        this.sH = SlotPuzzleConstants.V_HEIGHT;
        reelTiles = new Array<ReelTile>();
        scores = new Array<Score>();
    }

     public void handleInput(float dt) {
        int touchX, touchY;

        if (Gdx.input.justTouched()) {
            touchX = Gdx.input.getX();
            touchY = Gdx.input.getY();
            Vector3 unprojTouch = new Vector3(touchX, touchY, 0);
            viewport.unproject(unprojTouch);
            switch (levelCreator.getPlayState()) {
                case INITIALISING:
                    Gdx.app.debug(logTag, "Initialising");
                    break;
                case INTRO_SEQUENCE:
                    Gdx.app.debug(logTag, "Intro Sequence");
                    break;
                case INTRO_POPUP:
                    break;
                case INTRO_SPINNING:
                    Gdx.app.debug(logTag, "Intro Spinning");
                    break;
                case INTRO_FLASHING:
                    Gdx.app.debug(logTag, "Intro Flashing");
                    break;
                case PLAYING:
                    Gdx.app.debug(logTag, "Playing");
                    processIsTileClicked();
                    break;
                case LEVEL_LOST:
                    Gdx.app.debug(logTag, "Lost Level");
                    break;
                case WON_LEVEL:
                    Gdx.app.debug(logTag, "Won Level");
                    break;
                case RESTARTING_LEVEL:
                    Gdx.app.debug(logTag, "Restarting Level");
                    break;
                default: break;
            }
        }
    }

    public boolean isOver(Sprite sprite, float x, float y) {
        return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
                && sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
    }

    private void processIsTileClicked() {
        int touchX = Gdx.input.getX();
        int touchY = Gdx.input.getY();
        Vector2 newPoints = new Vector2(touchX, touchY);
        newPoints = viewport.unproject(newPoints);
        int c = (int) (newPoints.x - PlayScreen.PUZZLE_GRID_START_X) / PlayScreen.TILE_WIDTH;
        int r = (int) (newPoints.y - PlayScreen.PUZZLE_GRID_START_Y) / PlayScreen.TILE_HEIGHT;
        r = GAME_LEVEL_HEIGHT - r;
        if ((r >= 0) & (r <= GAME_LEVEL_HEIGHT) & (c >= 0) & (c <= GAME_LEVEL_WIDTH)) {
            TupleValueIndex[][] grid = levelCreator.populateMatchGrid(reelTiles, GAME_LEVEL_WIDTH, GAME_LEVEL_HEIGHT);
            PuzzleGridType.printGrid(grid);
            ReelTile reel = reelTiles.get(grid[r][c].index);
            System.out.println("r="+r+" c="+c);
            AnimatedReel animatedReel = levelCreator.getAnimatedReels().get(grid[r][c].index);
            if (!reel.isReelTileDeleted()) {
                if (reel.isSpinning()) {
                    if (animatedReel.getDampenedSineState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
                        reel.setEndReel(reel.getCurrentReel());
                        displaySpinHelp = true;
                        displaySpinHelpSprite = reel.getCurrentReel();
                        Hud.addScore(-1);
                        pullLeverSound.play();
                        reelSpinningSound.play();
                    }
                } else {
                    if (!reel.getFlashTween()) {
                        reelSlowingTargetTime = 3.0f;
                        reel.setEndReel(Random.getInstance().nextInt(reels.getReels().length - 1));
                        reel.startSpinning();
                        reelsSpinning++;
                        reel.setSy(0);
                        animatedReel.reinitialise();
                        Hud.addScore(-1);
                        if (pullLeverSound != null) {
                            pullLeverSound.play();
                        }
                    }
                }
            }
        } else {
            Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "I don't respond to r="+r+"c="+c);
        }
    }


    @Override
    protected void loadAssetsOverride() {
     }

    @Override
    protected void disposeOverride() {
    }

    @Override
    protected void updateOverride(float dt) {
        this.tweenManager.update(dt);
        this.levelCreator.update(dt);
        tileMapRenderer.setView(orthographicCamera);
        hud.update(dt);
        if (hud.getWorldTime() == 0) {
            if ((Hud.getLives() > 0) & (!inRestartLevel)) {
                inRestartLevel = true;
                levelCreator.setPlayState(PlayScreen.PlayStates.LEVEL_LOST);
            } else {
                gameOver = true;
            }
        }

    }

    @Override
    protected void renderOverride(float dt) {
        handleInput(dt);
        tileMapRenderer.render();
        batch.begin();
        if (levelDoor.levelType.equals(PLAYING_CARD_LEVEL_TYPE)) {
            drawPlayingCards(batch);
        }
        for (ReelTile reel : reelTiles) {
            if (!reel.isReelTileDeleted()) {
                reel.draw(batch);
            }
        }
        for (Score score : scores) {
            score.render(batch);
        }
        if (displaySpinHelp) {
            reels.getReels()[displaySpinHelpSprite].draw(batch);
        }
        batch.end();
        switch (levelCreator.getPlayState()) {
            case INTRO_POPUP:
                break;
            case LEVEL_LOST:
                break;
            case WON_LEVEL:
                break;
            case INITIALISING:
                break;
            case INTRO_FLASHING:
                break;
            case INTRO_SEQUENCE:
                break;
            case INTRO_SPINNING:
                break;
            case PLAYING:
                break;
            case RESTARTING_LEVEL:
                break;
            default:
                break;
        }
        batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();
        stage.draw();
    }

    private void drawPlayingCards(SpriteBatch spriteBatch) {
        for (Card card : cards) {
            card.draw(spriteBatch);
        }
    }

    @Override
    protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
        SlotPuzzleTween.registerAccessor(Score.class, new ScoreAccessor());
    }

    private void playSound(Sound sound) {
        if (sound != null) {
            sound.play();
        }
    }
}
