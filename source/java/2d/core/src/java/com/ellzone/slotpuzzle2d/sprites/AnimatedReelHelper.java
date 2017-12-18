/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.prototypes.Reels;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;

public class AnimatedReelHelper {
    public static String REELS_ATLAS = "reel/reels.pack.atlas";
    public static String PULL_LEVER_SOUND = "sounds/pull-lever1.wav";
    public static String REEL_SPIN_CLICK_SOUND = "sounds/click2.wav";
    public static String REEL_STOPPED_SOUND = "sounds/reel-stopped.wav";

    private AssetManager assetManager;
    private TweenManager tweenManager;
    private int numberOfAnimatedReels;
    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
    private Array<AnimatedReel> animatedReels;
    private Array<ReelTile> reelTiles;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Reels reels;
    private int spriteWidth, spriteHeight;

    public AnimatedReelHelper(AssetManager assetManager, TweenManager tweenManager, int numberOfAnimatedReels) {
        this.assetManager = assetManager;
        this.tweenManager = tweenManager;
        this.numberOfAnimatedReels = numberOfAnimatedReels;
        create(this.assetManager);
    }

    private void create(AssetManager assetManager) {
        getAssets(assetManager);
        initialiseReels();
        initialiseReelSlots();
    }

     private void getAssets(AssetManager assetManager) {
        this.pullLeverSound = assetManager.get(AssetsAnnotation.SOUND_PULL_LEVER);
        this.reelSpinningSound = assetManager.get(AssetsAnnotation.SOUND_REEL_SPINNING);
        this.reelStoppingSound = assetManager.get(AssetsAnnotation.SOUND_REEL_STOPPED);
    }

    private void initialiseReels(){
        reels = new Reels();
        spriteWidth = reels.getReelWidth();
        spriteHeight = reels.getReelHeight();
    }

    private void initialiseReelSlots() {
        animatedReels = new Array<AnimatedReel>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(reels.getReels());
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        for (int i = 0; i < numberOfAnimatedReels; i++) {
            AnimatedReel animatedReel = new AnimatedReel(slotReelScrollTexture, 0, 0, spriteWidth, spriteHeight, spriteWidth, spriteHeight, 0, null, reelStoppingSound, tweenManager);
            animatedReel.setSx(0);
            animatedReel.setEndReel(Random.getInstance().nextInt(reels.getReels().length - 1));
            animatedReel.getReel().startSpinning();
            animatedReels.add(animatedReel);
        }
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return this.animatedReels;
    }

    public Array<ReelTile> getReelTiles() {
        this.reelTiles = new Array<ReelTile>();
        for (AnimatedReel animatedReel : this.animatedReels) {
            reelTiles.add(animatedReel.getReel());
        }
        return reelTiles;
    }

    public Reels getReels() {
        return this.reels;
    }

    public void update(float dt) {
        for (AnimatedReel animatedReel : animatedReels) {
            animatedReel.update(dt);
        }
    }
 }
