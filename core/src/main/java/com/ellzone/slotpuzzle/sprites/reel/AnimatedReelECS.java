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
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.ellzone.slotpuzzle.effects.ReelAccessor;
import com.ellzone.slotpuzzle.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle.physics.Vector;
import com.ellzone.slotpuzzle.tweenengine.BaseTween;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;
import com.ellzone.slotpuzzle.utils.Random;

import aurelienribon.tweenengine.equations.Elastic;

public class AnimatedReelECS extends AnimatedReel {
    public AnimatedReelECS(
        Texture texture,
        float x,
        float y,
        float tileWidth,
        float tileHeight,
        float reelDisplayWidth,
        float reelDisplayHeight,
        int endReel,
        TweenManager tweenManager) {
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

    public AnimatedReelECS(
        Texture texture,
        float x,
        float y,
        float tileWidth,
        float tileHeight,
        float reelDisplayWidth,
        float reelDisplayHeight,
        int endReel,
        TweenManager tweenManager,
        boolean isReelSpinDirectionClockwise) {
        super(
            texture,
            x,
            y,
            tileWidth,
            tileHeight,
            reelDisplayWidth,
            reelDisplayHeight,
            endReel,
            tweenManager,
            isReelSpinDirectionClockwise);
    }

    @Override
    public void setupSpinning() {
        System.out.println("setUpSpinning called");
    }

    @Override
    public void update(float delta) {
        getReel().update(delta);
    }

    @Override
    public void reinitialise() {
        getReel().setSpinning(false);
        getReel().resetSy();
    }
}
