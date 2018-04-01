package com.ellzone.slotpuzzle2d.utils;

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
            WORLD_MAP = "levels/WorldMap.tmx";

    @AnnotationAssetManager.Asset(TextureAtlas.class)
    public static String CARDDECK = "playingcards/carddeck.atlas",
                         REELS = "reel/reels.pack.atlas",
                         SLOT_HANDLE = "slot_handle/slot_handle.pack.atlas",
                         SPLASH = "splash/pack.atlas",
                         SPLASH3 = "splash/splash3.pack.atlas",
                         TILES = "tiles/tiles.pack.atlas";

    @AnnotationAssetManager.Asset(Texture.class)
    public static String PROGRESS_BAR = "loading_screen/progress_bar.png",
                         PROGRESS_BAR_BASE = "loading_screen/progress_bar_base.png";

    @AnnotationAssetManager.Asset(Sound.class)
    public static String SOUND_CHA_CHING = "sounds/cha-ching.mp3",
                         SOUND_PULL_LEVER = "sounds/pull-lever1.mp3",
                         SOUND_REEL_SPINNING = "sounds/reel-spinning.mp3",
                         SOUND_REEL_STOPPED = "sounds/reel-stopped.mp3",
                         SOUND_JACKPOINT = "sounds/jackpot.mp3";

    public static String CHERRY = "cherry",
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
                         GAME_POPUP = "GamePopUp",
                         LEVEL_SPRITE = "level",
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
                         PUZZLE = "machine";

    public AssetsAnnotation() {}
}
