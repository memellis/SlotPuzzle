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

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.ellzone.slotpuzzle2d.utils.AssetsAnnotation;
import net.dermetfan.gdx.assets.AnnotationAssetManager;

public class Reels {
    public final static String REEL_PACK_ATLAS = "reel/reels.pack.atlas";
    public final static String CHERRY = "cherry 40x40";
    public final static String CHEESECAKE = "cheesecake 40x40";
    public final static String GRAPES = "grapes 40x40";
    public final static String JELLY = "jelly 40x40";
    public final static String LEMON = "lemon 40x40";
    public final static String PEACH = "peach 40x40";
    public final static String PEAR = "pear 40x40";
    public final static String TOMATO = "tomato 40x40";
    private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite[] reels;
    private int spriteWidth;
    private int spriteHeight;

    public Reels(AnnotationAssetManager annotationAssetManager) {
        initialiseReels(annotationAssetManager);
    }

    private void initialiseReels(AnnotationAssetManager annotationAssetManager) {
        TextureAtlas reelAtlas = annotationAssetManager.get(AssetsAnnotation.REELS);
        cherry = reelAtlas.createSprite(AssetsAnnotation.CHERRY40x40);
        cheesecake = reelAtlas.createSprite(AssetsAnnotation.CHEESECAKE40x40);
        grapes = reelAtlas.createSprite(AssetsAnnotation.GRAPES40x40);
        jelly = reelAtlas.createSprite(AssetsAnnotation.JELLY40x40);
        lemon = reelAtlas.createSprite(AssetsAnnotation.LEMON40x40);
        peach = reelAtlas.createSprite(AssetsAnnotation.PEACH40x40);
        pear = reelAtlas.createSprite(AssetsAnnotation.PEAR40x40);
        tomato = reelAtlas.createSprite(AssetsAnnotation.TOMATO40x40);

        reels = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : reels) {
            sprite.setOrigin(0, 0);
        }
        spriteWidth = (int) reels[0].getWidth();
        spriteHeight = (int) reels[0].getHeight();
    }

    public int getReelWidth() {
        return spriteWidth;
    }

    public int getReelHeight() {
        return spriteHeight;
    }

    public Sprite[] getReels() {
        return reels;
    }

    public void dispose() {
        for (Sprite reel : reels) {
            reel.getTexture().dispose();
        }
    }
}
