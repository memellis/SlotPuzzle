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

package com.ellzone.slotpuzzle.level.sequence;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.effects.SpriteAccessor;
import com.ellzone.slotpuzzle.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle.tweenengine.Timeline;
import com.ellzone.slotpuzzle.tweenengine.TweenManager;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

public class PlayScreenIntroSequence extends IntroSequence {
    public PlayScreenIntroSequence(Array<ReelTile> reelTiles, TweenManager tweenManager) {
        super(reelTiles, tweenManager);
    }

    @Override
    protected Timeline buildSequence(Sprite target, int id, float delay1, float delay2) {
        Vector2 targetXY = getRandomCorner();
        return Timeline.createSequence()
            .push(SlotPuzzleTween.set(target, SpriteAccessor.POS_XY).target(targetXY.x, targetXY.y))
            .push(SlotPuzzleTween.set(target, SpriteAccessor.SCALE_XY).target(20, 20))
            .push(SlotPuzzleTween.set(target, SpriteAccessor.ROTATION).target(0))
            .push(SlotPuzzleTween.set(target, SpriteAccessor.OPACITY).target(0))
            .pushPause(delay1)
            .beginParallel()
            .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
            .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1, 1).ease(Quart.INOUT))
            .end()
            .pushPause(-0.5f)
            .push(SlotPuzzleTween.to(target, SpriteAccessor.POS_XY, 0.8f).target(reelTiles.get(id).getX(), reelTiles.get(id).getY()).ease(Back.OUT))
            .push(SlotPuzzleTween.to(target, SpriteAccessor.ROTATION, 0.8f).target(360).ease(Cubic.INOUT))
            .pushPause(delay2)
            .beginParallel()
            .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 0.3f).target(3, 3).ease(Quad.IN))
            .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 0.3f).target(0).ease(Quad.IN))
            .end()
            .pushPause(-0.5f)
            .beginParallel()
            .push(SlotPuzzleTween.to(target, SpriteAccessor.OPACITY, 1.0f).target(1).ease(Quart.INOUT))
            .push(SlotPuzzleTween.to(target, SpriteAccessor.SCALE_XY, 1.0f).target(1.0f, 1.0f).ease(Quart.INOUT))
            .setUserData(target)
            .end();
    }
}
