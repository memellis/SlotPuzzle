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

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.level.light.LightGrid;
import com.ellzone.slotpuzzle.level.reel.ReelGrid;
import com.ellzone.slotpuzzle.spin.SpinWheelSlotPuzzleTileMap;
import com.ellzone.slotpuzzle.sprites.lights.HoldLightButtonTileMap;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedPredictedReel;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.lights.HoldLightButton;
import com.ellzone.slotpuzzle.sprites.reel.ReelHelper;
import com.ellzone.slotpuzzle.sprites.reel.ReelSprites;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.sprites.slothandle.SlotHandleSprite;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReelTileMap;
import com.ellzone.slotpuzzle.sprites.slothandle.SlotHandleTileMap;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle.utils.convert.TileMapToWorldConvert;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import box2dLight.ConeLight;
import box2dLight.PointLight;
import box2dLight.RayHandler;

public class LevelObjectCreatorEntityHolder extends LevelObjectCreator {
    private LevelHoldLightButtonCallback levelHoldLightButtonCallback;
    private LevelAnimatedReelCallback levelAnimatedReelCallback;
    private LevelSlotHandleSpriteCallback levelSlotHandleSpriteCallback;
    private LevelSlotHandleCallback levelSlotHandleCallback;
    private LevelPointLightCallback levelPointLightCallback;
    private LevelConeLightCallback levelConeLightCallback;
    private LevelReelHelperCallback levelReelHelperCallback;
    private LevelSpinWheelCallback levelSpinWheelCallback;
    private LevelReelGridCallback levelReelGridCallback;
    private LevelLightGridCallback levelLightGridCallback;
    private final Array<ReelTile> reelTiles = new Array<>();
    private final Array<AnimatedReel> reels = new Array<>();
    private final Array<AnimatedReelTileMap> animatedReels = new Array<>();
    private final Array<AnimatedPredictedReel> animatedPredictedReels = new Array<>();
    private final Array<PointLight> pointLights = new Array<>();
    private final Array<ConeLight> coneLights = new Array<>();
    private final Array<SlotHandleSprite> handles = new Array<>();
    private final Array<SlotHandleTileMap> slotHandles = new Array<>();
    private final Array<SpinWheelSlotPuzzleTileMap> spinWheels = new Array<>();
    private final Array<ReelGrid> reelGrids = new Array<>();
    private final Array<HoldLightButton> holdLightButtons = new Array<>();
    private final Array<LightGrid> lightGrids = new Array<>();
    private LevelAnimatedReelTileMapCallback levelAnimatedReelTileMapCallback;
    private LevelAnimatedPredictedReelCallback levelAnimatedPredictedReelCallback;

    public LevelObjectCreatorEntityHolder(
        LevelCreatorInjectionInterface injection, World world, RayHandler rayHandler) {
        super(injection, world, rayHandler);
    }

    public World getWorld() {
        return world;
    }

    public RayHandler getRayHandler() {
        return rayHandler;
    }

    @Override
    public TextureAtlas getSlotHandleAtlas() {
        return super.getSlotHandleAtlas();
    }

    @Override
    public TweenManager getTweenManager() {
        return super.getTweenManager();
    }

    @Override
    public AnnotationAssetManager getAnnotationAssetManager() {
        return super.levelCreatorInjectionInterface.getAnnotationAssetManager();
    }

    @Override
    public Texture getSlotReelScrollTexture() {
        return levelCreatorInjectionInterface.getSlotReelScrollTexture();
    }

    @Override
    public Sound getReelSpinningSound() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager()
            .get(AssetsAnnotation.SOUND_REEL_SPINNING);
    }

    @Override
    public Sound getReelStoppingSound() {
        return levelCreatorInjectionInterface.getAnnotationAssetManager()
            .get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    @Override
    public ReelSprites getReelSprites() {
        return levelCreatorInjectionInterface.getReelSprites();
    }

    public TileMapToWorldConvert getTileMapToWorldConvert() {
        return levelCreatorInjectionInterface.getTileMapToWorldConvert();
    }

    public Array<ReelTile> getReelTiles() { return reelTiles; }

    public Array<ReelGrid> getReelGrids() { return reelGrids; }

    public Array<LightGrid> getLightGrids() { return lightGrids; }

    public Color getReelPointLightColor() {
        return super.reelPointLightColor;
    }

    public Color getConeLightColor() {
        return new Color(Color.RED);
    }

    public void addTo(HoldLightButton holdLightButton) {
        holdLightButtons.add(holdLightButton);
    }

    public void addTo(AnimatedReel reel) {
        reels.add(reel);
        reelTiles.add(reel.getReel());
    }

    public void addTo(AnimatedReelTileMap animatedReel) {
        animatedReels.add(animatedReel);
    }

    public void addTo(AnimatedPredictedReel animatedPredictedReel) {
        animatedPredictedReels.add(animatedPredictedReel);
    }

    public void addTo(SlotHandleSprite handle) { handles.add(handle); }

    public void addTo(SlotHandleTileMap handle) { slotHandles.add(handle); }

    public void addTo(PointLight pointLight) { pointLights.add(pointLight); }

    public void addTo(ConeLight coneLight) { coneLights.add(coneLight); }

    public void addTo(ReelHelper reelHelper) {
    }

    public void addTo(SpinWheelSlotPuzzleTileMap spinWheel) { spinWheels.add(spinWheel); }

    public void addTo(ReelGrid reelGrid) {
        reelGrids.add(reelGrid);
    }

    public void addTo(LightGrid lightGrid) { lightGrids.add(lightGrid); }

    public void addHoldLightButtonCallback(LevelHoldLightButtonCallback callback) {
        this.levelHoldLightButtonCallback = callback;
    }

    public void addAnimatedReelCallback(LevelAnimatedReelCallback callback) {
        this.levelAnimatedReelCallback = callback;
    }

    public void addSlotHandleCallback(LevelSlotHandleSpriteCallback callback) {
        this.levelSlotHandleSpriteCallback = callback;
    }

    public void addPointLightCallback(LevelPointLightCallback callback) {
        this.levelPointLightCallback = callback;
    }

    public void addConeLightCallback(LevelConeLightCallback callback) {
        this.levelConeLightCallback = callback;
    }

    public void addReelHelperCallback(LevelReelHelperCallback callback) {
        this.levelReelHelperCallback = callback;
    }

    public void addSpinWheelCallback(LevelSpinWheelCallback callback) {
        this.levelSpinWheelCallback = callback;
    }

    public void addReelGridCallback(LevelReelGridCallback callback) {
        this.levelReelGridCallback = callback;
    }

    public void delegateToCallback(HoldLightButton holdLightButton) {
        if (levelHoldLightButtonCallback != null)
            levelHoldLightButtonCallback.onEvent(holdLightButton);
    }

    public void delegateToCallback(HoldLightButtonTileMap holdLightButton) {
        if (levelHoldLightButtonCallback != null)
            levelHoldLightButtonCallback.onEvent((HoldLightButton) holdLightButton.getHoldLightButton());
    }

    public void addComponentToEntity(PointLight pointLight, Component component) {
        if (levelPointLightCallback != null)
            levelPointLightCallback.addComponent(component);
    }

    public void addComponentToEntity(HoldLightButton holdLightButton, Component component) {
        if (levelHoldLightButtonCallback != null)
            levelHoldLightButtonCallback.addComponent(component);
    }

    public void addComponentToEntity(HoldLightButtonTileMap holdLightButton, Component component) {
        if (levelHoldLightButtonCallback != null)
            levelHoldLightButtonCallback.addComponent(component);
    }

    public void addComponentToEntity(ReelHelper reelHelper, Component component) {
        if (levelReelHelperCallback != null)
            levelReelHelperCallback.addComponent(component);
    }

    public void delegateToCallback(AnimatedReel animatedReel) {
        if (levelAnimatedReelCallback != null)
            levelAnimatedReelCallback.onEvent(animatedReel);
    }

    public void delegateToCallback(AnimatedReelTileMap animatedReelTileMap) {
        if (levelAnimatedReelTileMapCallback != null)
            levelAnimatedReelTileMapCallback.onEvent(animatedReelTileMap);
    }

    public void delegateToCallback(AnimatedPredictedReel animatedReel) {
        if (levelAnimatedPredictedReelCallback != null)
            levelAnimatedPredictedReelCallback.onEvent(animatedReel);
    }

    public void delegateToCallback(SlotHandleSprite slotHandleSprite) {
        if (levelSlotHandleSpriteCallback != null)
            levelSlotHandleSpriteCallback.onEvent(slotHandleSprite);
    }

    public void delegateToCallback(SlotHandleTileMap slotHandle) {
        if (levelSlotHandleCallback != null)
            levelSlotHandleCallback.onEvent(slotHandle);
    }

    public void delegateToCallback(PointLight pointLight) {
        if (levelPointLightCallback != null)
            levelPointLightCallback.onEvent(pointLight);
    }

    public void delegateToCallback(ConeLight coneLight) {
        if (levelConeLightCallback != null)
            levelConeLightCallback.onEvent(coneLight);
    }

    public void delegateToCallback(ReelHelper reelHelper) {
        if (levelReelHelperCallback != null)
            levelReelHelperCallback.onEvent(reelHelper);
    }

    public void delegateToCallback(SpinWheelSlotPuzzleTileMap spinWheel) {
        if (levelSpinWheelCallback != null)
            levelSpinWheelCallback.onEvent(spinWheel);
    }

    public void delegateToCallback(ReelGrid reelGrid) {
        if (levelReelGridCallback != null)
            levelReelGridCallback.onEvent(reelGrid);
    }

    public void delegateToCallback(LightGrid lightGrid) {
        if (levelLightGridCallback != null)
            levelLightGridCallback.onEvent(lightGrid);
    }

    public Array<HoldLightButton> getHoldLightButtons() {
        return holdLightButtons;
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return reels;
    }

    public Array<AnimatedReelTileMap> getAnimatedReelsTileMap() { return animatedReels; }

    public Array<AnimatedPredictedReel> getAnimatedPredictedReels() {
        return animatedPredictedReels;
    }

    public Array<SlotHandleSprite> getHandles() { return handles; }

    public Array<SlotHandleTileMap> getSlotHandles() { return slotHandles; }

    public Array<SpinWheelSlotPuzzleTileMap> getSpinWheels() { return spinWheels; }
}
