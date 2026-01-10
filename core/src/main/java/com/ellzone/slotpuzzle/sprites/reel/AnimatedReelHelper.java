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

package com.ellzone.slotpuzzle.sprites.reel;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.assets.AssetsAnnotation;
import com.ellzone.slotpuzzle.utils.PixmapProcessors;
import com.ellzone.slotpuzzle.utils.Random;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import lombok.Getter;

public class AnimatedReelHelper {
    public static String REELS_ATLAS = "reel/reels.pack.atlas";
    public static String PULL_LEVER_SOUND = "sounds/pull-lever1.wav";
    public static String REEL_SPIN_CLICK_SOUND = "sounds/click2.wav";
    public static String REEL_STOPPED_SOUND = "sounds/reel-stopped.wav";

    private final AnnotationAssetManager annotationAssetManager;

    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
    private final TweenManager tweenManager;
    private final int numberOfAnimatedReels;
     @Getter
    private Array<com.ellzone.slotpuzzle.sprites.reel.AnimatedReel> animatedReels;
    @Getter
    private ReelSprites reelSprites;
    private int spriteWidth, spriteHeight;
    private int reelDisplayHeight = 0;

    public AnimatedReelHelper(
        AnnotationAssetManager annotationAssetManager,
        TweenManager tweenManager,
        int numberOfAnimatedReels) {
        this.annotationAssetManager = annotationAssetManager;
        this.tweenManager = tweenManager;
        this.numberOfAnimatedReels = numberOfAnimatedReels;
        create(annotationAssetManager);
    }

    public AnimatedReelHelper(
        AnnotationAssetManager annotationAssetManager,
        TweenManager tweenManager,
        int numberOfAnimatedReels,
        int reelDisplayHeight) {
        this.annotationAssetManager = annotationAssetManager;
        this.tweenManager = tweenManager;
        this.numberOfAnimatedReels = numberOfAnimatedReels;
        this.reelDisplayHeight = reelDisplayHeight;
        create(annotationAssetManager);
    }

    public AnimatedReelHelper(
        AnnotationAssetManager annotationAssetManager,
        TweenManager tweenManager,
        int numberOfAnimatedReels,
        int reelDisplayHeight,
        ReelSprites extendedSprites) {
        this.annotationAssetManager = annotationAssetManager;
        this.tweenManager = tweenManager;
        this.numberOfAnimatedReels = numberOfAnimatedReels;
        this.reelDisplayHeight = reelDisplayHeight;
        this.reelSprites = extendedSprites;
        createWithExtendedSprites(annotationAssetManager);
    }

    private void create(AnnotationAssetManager annotationAssetManager) {
        getAssets(annotationAssetManager);
        initialiseReels(annotationAssetManager);
        initialiseReelSlots();
    }

    private void createWithExtendedSprites(AnnotationAssetManager annotationAssetManager) {
        getAssets(annotationAssetManager);
        addExtendedSprites();
        initialiseReelSlots();
    }

    private void addExtendedSprites() {
        spriteWidth = reelSprites.getReelWidth();
        spriteHeight = reelSprites.getReelHeight();
        if (reelDisplayHeight == 0)
            reelDisplayHeight = spriteHeight;
    }


    private void getAssets(AnnotationAssetManager annotationAssetManager) {
        pullLeverSound = annotationAssetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        reelSpinningSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        reelStoppingSound = annotationAssetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    private void initialiseReels(AnnotationAssetManager annotationAssetManager){
        reelSprites = new ReelSprites(annotationAssetManager);
        spriteWidth = reelSprites.getReelWidth();
        spriteHeight = reelSprites.getReelHeight();
        if (reelDisplayHeight == 0)
            reelDisplayHeight = spriteHeight;
    }

    private void initialiseReelSlots() {
        animatedReels = new Array<com.ellzone.slotpuzzle.sprites.reel.AnimatedReel>();
        Pixmap slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reelSprites.getSprites());
        Texture slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        for (int i = 0; i < numberOfAnimatedReels; i++) {
            com.ellzone.slotpuzzle.sprites.reel.AnimatedReel animatedReel =
                new com.ellzone.slotpuzzle.sprites.reel.AnimatedReel(
                    slotReelScrollTexture,
                    0,
                    0,
                    spriteWidth,
                    spriteHeight,
                    spriteWidth,
                    reelDisplayHeight,
                    0,
                    tweenManager);
            animatedReel.setSx(0);
            animatedReel.setEndReel(Random.getInstance().nextInt(reelSprites.getSprites().length - 1));
            animatedReels.add(animatedReel);
        }
    }

    public Array<ReelTile> getReelTiles() {
        Array<ReelTile> reelTiles = new Array<ReelTile>();
        for (AnimatedReel animatedReel : new Array.ArrayIterator<>(animatedReels)) {
            reelTiles.add(animatedReel.getReel());
        }
        return reelTiles;
    }

    public void update(float dt) {
        for (AnimatedReel animatedReel : new Array.ArrayIterator<AnimatedReel>(animatedReels))
            animatedReel.update(dt);
    }
}
