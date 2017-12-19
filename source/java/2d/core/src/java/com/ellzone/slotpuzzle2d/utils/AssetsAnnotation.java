package com.ellzone.slotpuzzle2d.utils;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.tiled.TiledMap;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class AssetsAnnotation {

    @AnnotationAssetManager.Asset(TiledMap.class)
    public static String MINI_SLOT_MACHINE_LEVEL = "levels/mini slot machine level.tmx";

    @AnnotationAssetManager.Asset(TextureAtlas.class)
    public static String CARDDECK = "playingcards/carddeck.atlas",
                         REELS = "reel/reels.pack.atlas",
                         SLOT_HANDLE = "slot_handle/slot_handle.pack.atlas";
    ;

    @AnnotationAssetManager.Asset(Sound.class)
    public static String SOUND_CHA_CHING = "sounds/cha-ching.mp3",
                         SOUND_PULL_LEVER = "sounds/pull-lever1.mp3",
                         SOUND_REEL_SPINNING = "sounds/reel-spinning.mp3",
                         SOUND_REEL_STOPPED = "sounds/reel-stopped.mp3",
                         SOUND_JACKPOINT = "sounds/jackpot.mp3";

    public AssetsAnnotation() {}
}
