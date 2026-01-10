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

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.PixmapProcessors;

import net.dermetfan.gdx.assets.AnnotationAssetManager;

import lombok.Getter;

public class SlotMachineAnimatedReel {
    @Getter
    private com.ellzone.slotpuzzle.sprites.reel.AnimatedReel animatedReel;
    private AnnotationAssetManager annotationAssetManager;

    public SlotMachineAnimatedReel(float x,
                                   float y,
                                   float tileWidth,
                                   float tileHeight,
                                   float reelDisplayWidth,
                                   float reelDisplayHeight,
                                   int endReel,
                                   TweenManager tweenManager,
                                   AnnotationAssetManager annotationAssetManager) {
        initialiseAnimatedReel(
            x,
            y ,
            tileWidth,
            tileHeight,
            reelDisplayWidth,
            reelDisplayHeight,
            endReel,
            tweenManager,
            annotationAssetManager);
    }

    private void initialiseAnimatedReel(float x,
                                        float y,
                                        float tileWidth,
                                        float tileHeight,
                                        float reelDisplayWidth,
                                        float reelDisplayHeight,
                                        int endReel,
                                        TweenManager tweenManager,
                                        AnnotationAssetManager annotationAssetManager) {
        this.annotationAssetManager = annotationAssetManager;
        animatedReel = new com.ellzone.slotpuzzle.sprites.reel.AnimatedReel(getReelTexture(),
            x, y,
            tileWidth, tileHeight,
            reelDisplayWidth, reelDisplayHeight,
            endReel,
            tweenManager);
    }

    public Texture getReelTexture() {
        com.ellzone.slotpuzzle.sprites.reel.ReelSprites reelSprites = new ReelSprites(annotationAssetManager);
        Sprite[] sprites = reelSprites.getSprites();
        Pixmap slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        return new Texture(slotReelScrollPixmap);
    }

    public float getX() { return animatedReel.getX(); }

    public float getY() { return  animatedReel.getY(); }
}
