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

package com.ellzone.slotpuzzle2d.sprites;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

public abstract class AbstractAnimatedReelHelper {
    public static String REELS_ATLAS = "reel/reels.pack.atlas";
    public static String PULL_LEVER_SOUND = "sounds/pull-lever1.wav";
    public static String REEL_SPIN_CLICK_SOUND = "sounds/click2.wav";
    public static String REEL_STOPPED_SOUND = "sounds/reel-stopped.wav";

    private TweenManager tweenManager;
    private int numberOfAnimatedReels;
    private Sound pullLeverSound, reelSpinningSound, reelStoppingSound;
    private Array<AnimatedReel> animatedReels;
    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Reels reels;
    private int spriteWidth, spriteHeight;

    void loadAssets(AssetManager assetManager) {
        assetManager.load(REELS_ATLAS, TextureAtlas.class);
        assetManager.load(PULL_LEVER_SOUND, Sound.class);
        assetManager.load(REEL_SPIN_CLICK_SOUND, Sound.class);
        assetManager.load(REEL_STOPPED_SOUND, Sound.class);
        assetManager.update();
        assetManager.finishLoading();

        this.pullLeverSound = assetManager.get("sounds/pull-lever1.wav");
        this.reelSpinningSound = assetManager.get("sounds/click2.wav");
        this.reelStoppingSound = assetManager.get("sounds/reel-stopped.wav");
    }

    Reels initialiseReels(AnnotationAssetManager annotationAssetManager) {
        reels = new Reels(annotationAssetManager);
        spriteWidth = reels.getReelWidth();
        spriteHeight = reels.getReelHeight();
        return reels;
    }

    public Array<AnimatedReel> getAnimatedReels() {
        return this.animatedReels;
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
