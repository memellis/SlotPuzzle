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

package com.ellzone.slotpuzzle.level.creator;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.SlotPuzzleConstants;
import com.ellzone.slotpuzzle.SlotPuzzleGameInterface;
import com.ellzone.slotpuzzle.level.FlashSlots;
import com.ellzone.slotpuzzle.level.LevelDoor;
import com.ellzone.slotpuzzle.level.hidden.HiddenPattern;
import com.ellzone.slotpuzzle.level.map.MapLevelNameComparator;
import com.ellzone.slotpuzzle.level.reel.ReelType;
import com.ellzone.slotpuzzle.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle.scene.Hud;
import com.ellzone.slotpuzzle.scene.MapTile;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.lights.HoldLightButton;
import com.ellzone.slotpuzzle.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.sprites.slothandle.SlotHandleSprite;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle.utils.FrameRate;
import com.ellzone.slotpuzzle.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import box2dLight.RayHandler;

public class PlayScreenLevel implements PlayScreenLevelInterface {

    private final LevelCreatorInjectionInterface levelCreatorInjection;
    private final SlotPuzzleGameInterface game;
    private final LevelDoor levelDoor;
    private TiledMap tiledMapLevel;
    private Texture slotReelScrollTexture;
    private World box2dWorld;
    private RayHandler rayHandler;
    private LevelLoader levelLoader;
    protected Array<ReelTile> reelTiles;
    private HiddenPattern hiddenPattern;
    private FlashSlots flashSlots;
    private Array<AnimatedReel> animatedReels;
    private Array<HoldLightButton> holdLightButtons;
    private Array<SlotHandleSprite> slotHandles;
    private ReelSprites reelSprites;
    private GridSize levelGridSize;
    private Hud hud;
    private FrameRate frameRate;

    public PlayScreenLevel(
        LevelCreatorInjectionInterface levelCreatorInjection,
        SlotPuzzleGameInterface game,
        LevelDoor levelDoor) {
        this.levelCreatorInjection = levelCreatorInjection;
        this.game = game;
        this.levelDoor = levelDoor;
        initialise();
    }

    private void initialise() {
        createSprites(game.getAnnotationAssetManager());
        getLevelAssets(game.getAnnotationAssetManager());
        initialiseHud();
        initialiseWorld();
    }

    private void createSprites(AnnotationAssetManager annotationAssetManager) {
        reelSprites = new ReelSprites(annotationAssetManager);
    }

    private void getLevelAssets(AnnotationAssetManager annotationAssetManager) {
        tiledMapLevel = annotationAssetManager.get(
            "levels/level " + (this.levelDoor.getId() + 1) + " - 40x40.tmx");
    }

    private void initialiseHud() {
        hud = new Hud(game.getBatch());
        hud.setLevelName(levelDoor.getLevelName());
        frameRate = new FrameRate();
    }


    private void initialiseWorld() {
        box2dWorld = new World(new Vector2(0, -9.8f), true);
        rayHandler = new RayHandler(box2dWorld);
        RayHandler.useDiffuseLight(true);
        rayHandler.setAmbientLight(0.25f, 0.25f, 0.25f, 0.25f);
    }

    @Override
    public void loadLevel(
        MapTile mapTileLevel,
        LevelCallback stoppedSpinningCallback,
        LevelCallback stoppedFlashingCallback) {
        setUpMapProperties();
        slotReelScrollTexture = createSlotReelScrollTexture();
        createLevelObjects();
        loadLevelDetails(mapTileLevel, stoppedSpinningCallback, stoppedFlashingCallback);
        setUpLevelDetails();
    }

    @Override
    public Texture getSlotReelScrollTexture() {
        return slotReelScrollTexture;
    }

    @Override
    public World getBox2dWorld() { return box2dWorld; }

    @Override
    public AnnotationAssetManager getAnnotationAssetManager() {
        return game.getAnnotationAssetManager();
    }

    @Override
    public Hud getHud() { return hud; }

    @Override
    public FrameRate getFrameRate() { return frameRate; }

    @Override
    public GridSize getLevelGridSize() { return levelGridSize; }

    @Override
    public Array<AnimatedReel> getAnimatedReels() {
        return animatedReels;
    }

    @Override
    public Array<ReelTile> getReelTiles() {
        return reelTiles;
    }

    @Override
    public Array<HoldLightButton> getHoldLightButtons() {
        return holdLightButtons;
    }

    @Override
    public Array<SlotHandleSprite> getSlotHandles() {
        return slotHandles;
    }

    @Override
    public FlashSlots getFlashSlots() {
        return flashSlots;
    }

    @Override
    public HiddenPattern getHiddenPattern() {
        return hiddenPattern;
    }

    @Override
    public LevelLoader getLevelLoader() {
        return levelLoader;
    }

    @Override
    public TweenManager getTweenManager() {
        return levelCreatorInjection.getTweenManager();
    }

    @Override
    public TiledMap getTiledMapLevel() {
        return tiledMapLevel;
    }

    private void setUpLevelDetails() {
        hiddenPattern = levelLoader.getHiddenPattern();
        flashSlots = new FlashSlots(
            game.getTweenManager(),
            levelGridSize,
            reelTiles);
    }

    private void loadLevelDetails(MapTile mapTileLevel,
                                  LevelCallback stoppedSpinningCallback,
                                  LevelCallback stoppedFlashingCallback) {
        levelLoader = createLevelLoader(
            mapTileLevel, stoppedSpinningCallback, stoppedFlashingCallback);
        levelLoader.createAnimatedReelsInLevel(levelGridSize);
    }

    private void setUpMapProperties() {
        MapProperties mapProperties = getMapProperties(tiledMapLevel);
        getLevelGrid(mapProperties);
        String addReel = getStringProperty(mapProperties);

        if (addReel.equals(ReelType.Bomb.name))
            addBombSprite();
    }

    private void createLevelObjects() {
        LevelObjectCreatorEntityHolder levelObjectCreator =
            new LevelObjectCreatorEntityHolder(levelCreatorInjection, box2dWorld, rayHandler);
        Array<MapObject> extractedLevelMapObjects = extractLevelAssets(tiledMapLevel);
        levelObjectCreator.createLevel(extractedLevelMapObjects);
        getLevelEntities(levelObjectCreator);
    }

    private MapProperties getMapProperties(TiledMap tiledMapLevel) {
        return tiledMapLevel.getProperties();
    }

    private void getLevelGrid(MapProperties mapProperties) {
        int gridWidth = getIntProperty(mapProperties, SlotPuzzleConstants.GRID_WIDTH_KEY);
        int gridHeight = getIntProperty(mapProperties, SlotPuzzleConstants.GRID_HEIGHT_KEY);
        if (gridWidth == 0 | gridHeight == 0)
            levelGridSize = new GridSize(
                SlotPuzzleConstants.GAME_LEVEL_WIDTH,
                SlotPuzzleConstants.GAME_LEVEL_HEIGHT);
        else
            levelGridSize = new GridSize(
                gridWidth,
                gridHeight
            );
    }

    private String getStringProperty(MapProperties mapProperties) {
        return mapProperties.get(SlotPuzzleConstants.ADD_REEL_KEY, String.class) == null ?
            "" : mapProperties.get(SlotPuzzleConstants.ADD_REEL_KEY, String.class);
    }

    private int getIntProperty(MapProperties mapProperties, String key) {
        return mapProperties.get(key, String.class) == null ?
            0 :
            Integer.parseInt(mapProperties.get(key, String.class));
    }

    private Texture createSlotReelScrollTexture() {
        Pixmap slotReelScrollPixmap =
            PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        return new Texture(slotReelScrollPixmap);
    }

    private void addBombSprite() {
        TextureAtlas reelAtlas =
            game.getAnnotationAssetManager().get(AssetsAnnotation.REELS_EXTENDED);
        reelSprites.addSprite(reelAtlas.createSprite(AssetsAnnotation.BOMB40x40));
    }


    private void getLevelEntities(LevelObjectCreatorEntityHolder levelObjectCreator) {
        animatedReels = levelObjectCreator.getAnimatedReels();
        reelTiles = levelObjectCreator.getReelTiles();
        holdLightButtons = levelObjectCreator.getHoldLightButtons();
        slotHandles = levelObjectCreator.getHandles();
    }

    private Array<MapObject> extractLevelAssets(TiledMap level) {
        Array<MapObject> levelMapObjects =
            getMapObjectsFromLevel(level);
        MapLevelNameComparator mapLevelNameComparator = new MapLevelNameComparator();
        levelMapObjects.sort(mapLevelNameComparator);
        return levelMapObjects;
    }

    private Array<MapObject> getMapObjectsFromLevel(TiledMap level) {
        return level.getLayers().get(SlotPuzzleConstants.REEL_OBJECT_LAYER).
            getObjects().getByType(MapObject.class);
    }

    private LevelLoader createLevelLoader(
        MapTile mapTileLevel,
        LevelCallback stoppedSpinningCallback,
        LevelCallback stoppedFlashingCallback) {
        LevelLoader levelLoader =
            new LevelLoader(
                game.getAnnotationAssetManager(),
                levelDoor,
                mapTileLevel,
                animatedReels);
        levelLoader.setStoppedSpinningCallback(stoppedSpinningCallback);
        levelLoader.setStoppedFlashingCallback(stoppedFlashingCallback);
        return levelLoader;
    }
}
