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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.level.FlashSlots;
import com.ellzone.slotpuzzle.level.hidden.HiddenPattern;
import com.ellzone.slotpuzzle.puzzlegrid.GridSize;
import com.ellzone.slotpuzzle.scene.Hud;
import com.ellzone.slotpuzzle.scene.MapTile;
import com.ellzone.slotpuzzle.sprites.reel.AnimatedReel;
import com.ellzone.slotpuzzle.sprites.lights.HoldLightButton;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.sprites.slothandle.SlotHandleSprite;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.FrameRate;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public interface PlayScreenLevelInterface {
    void loadLevel(
        MapTile mapTileLevel,
        LevelCallback stoppedSpinningCallback,
        LevelCallback stoppedFlashingCallback);

    Texture getSlotReelScrollTexture();

    World getBox2dWorld();

    AnnotationAssetManager getAnnotationAssetManager();

    Hud getHud();

    FrameRate getFrameRate();

    GridSize getLevelGridSize();

    Array<AnimatedReel> getAnimatedReels();

    Array<ReelTile> getReelTiles();

    Array<HoldLightButton> getHoldLightButtons();

    Array<SlotHandleSprite> getSlotHandles();

    FlashSlots getFlashSlots();

    HiddenPattern getHiddenPattern();

    LevelLoader getLevelLoader();

    TweenManager getTweenManager();

    TiledMap getTiledMapLevel();
}
