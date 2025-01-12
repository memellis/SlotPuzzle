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

package com.ellzone.slotpuzzle.utils.assets;

import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class AssetsAnnotation {

    @AnnotationAssetManager.Asset(TiledMap.class)
    public static
    String
        MINI_SLOT_MACHINE_LEVEL = "levels/mini slot machine level.tmx",
        MINI_SLOT_MACHINE_LEVEL1 = "levels/mini slot machine level 1.tmx",
        LEVEL1 = "levels/level 1 - 40x40.tmx",
        LEVEL2 = "levels/level 2 - 40x40.tmx",
        LEVEL3 = "levels/level 3 - 40x40.tmx",
        LEVEL4 = "levels/level 4 - 40x40.tmx",
        LEVEL5 = "levels/level 5 - 40x40.tmx",
        LEVEL6 = "levels/level 6 - 40x40.tmx",
        LEVEL6_COMPONENT_BASED = "levels/level 6 component based - 40x40.tmx",
        LEVEL7 = "levels/level 7 - 40x40.tmx",
        LEVLE8 = "levels/level 8 - 40x40.tmx",
        WORLD_MAP = "levels/WorldMap.tmx";

    @AnnotationAssetManager.Asset(TextureAtlas.class)
    public final static
    String
        CARDDECK = "playingcards/carddeck.atlas",
        REELS = "reel/reels.pack.atlas",
        REELS_EXTENDED = "reel/reels_extended.pack.atlas",
        SLOT_HANDLE = "slot_handle/slot_handle.pack.atlas",
        SLOT_HANDLE_ANIMATION = "handle/handle.pack.atlas",
        SPLASH = "splash/pack.atlas",
        SPLASH3 = "splash/splash3.pack.atlas",
        TILES = "tiles/tiles.pack.atlas",
        BOMB_ANIMATION = "bomb/bomb_animation.pack.atlas",
        COIN_ANIMATION = "coin/coin40x40.pack.atlas",
        SPIN_WHEEL = "spin/spin_wheel_ui.atlas";

    @AnnotationAssetManager.Asset(Texture.class)
    public final static
    String
        PROGRESS_BAR = "loading_screen/progress_bar.png",
        PROGRESS_BAR_BASE = "loading_screen/progress_bar_base.png",
        PLAYBACK_PNG = "playback.png";

    @AnnotationAssetManager.Asset(Sound.class)
    public final static
    String
        SOUND_CHA_CHING = "sounds/cha-ching.mp3",
        SOUND_PULL_LEVER = "sounds/pull-lever1.mp3",
        SOUND_REEL_SPINNING = "sounds/reel-spinning.mp3",
        SOUND_REEL_STOPPED = "sounds/reel-stopped.mp3",
        SOUND_JACKPOINT = "sounds/jackpot.mp3";

    @AnnotationAssetManager.Asset(Music.class)
    public final static
    String
        MUSIC_INTRO_SCREEN = "sounds/8.12.mp3";

    public final static
    String
        CHERRY = "cherry",
        CHERRY40x40 = "cherry 40x40",
        CHEESECAKE = "cheesecake",
        CHEESECAKE40x40 = "cheesecake 40x40",
        GRAPES = "grapes",
        GRAPES40x40 = "grapes 40x40",
        JELLY = "jelly",
        JELLY40x40 = "jelly 40x40",
        LEMON = "lemon",
        LEMON40x40 = "lemon 40x40",
        PEACH = "peach",
        PEACH40x40 = "peach 40x40",
        PEAR = "pear",
        PEAR40x40 = "pear 40x40",
        TOMATO = "tomato",
        TOMATO40x40 = "tomato 40x40",
        BOMB40x40 = "bomb 40x40",
        GAME_POPUP = "GamePopUp",
        LEVEL_SPRITE = "level",
        BONUS = "Bonus",
        OVER = "over",
        COMPLETE = "complete",
        UNIVERSAL = "universal",
        TWEEN = "tween",
        ENGINE = "engine",
        LOGO = "logo",
        WHITE = "white",
        POWERED = "powered",
        GDXBLUR = "gdxblur",
        GDX = "gdx",
        SLOT = "slot",
        PUZZLE = "puzzle";

    public AssetsAnnotation() {}
}
