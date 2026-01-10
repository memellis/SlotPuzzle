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

import com.badlogic.gdx.graphics.Texture;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;

public class AnimatedPredictedReel extends AnimatedReel {
    public AnimatedPredictedReel(
        Texture texture,
        float x,
        float y,
        float tileWidth,
        float tileHeight,
        float reelDisplayWidth,
        float reelDisplayHeight,
        int endReel,
        TweenManager tweenManager,
        int isVisible) {
        super(
            texture,
            x,
            y,
            tileWidth,
            tileHeight,
            reelDisplayWidth,
            reelDisplayHeight,
            endReel,
            tweenManager);
    }
}
