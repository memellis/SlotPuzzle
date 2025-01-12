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

public class AnimatedReel implements AnimatedReelInterface {
    private ReelTile reel;
    private DampenedSineParticle dampenedSine;
    private float velocityY;
    private Vector velocityMin;
    private float acceleratorY;
    private Vector accelerator;
    private float accelerateY;
    private float acceleratorFriction;
    private float velocityFriction;
    private final Texture texture;
    private final float x;
    private final float y;
    private final float tileWidth;
    private final float tileHeight;
    private final float reelDisplayWidth;
    private final float reelDisplayHeight;
    private final int endReel;
    private int reelScrollHeight;
    private final TweenManager tweenManager;
    private float reelSlowingTargetTime;
    private boolean isReelSpinDirectionClockwise;

    public AnimatedReel(
        Texture texture,
        float x,
        float y,
        float tileWidth,
        float tileHeight,
        float reelDisplayWidth,
        float reelDisplayHeight,
        int endReel,
        TweenManager tweenManager) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.reelDisplayWidth = reelDisplayWidth;
        this.reelDisplayHeight = reelDisplayHeight;
        this.endReel = endReel;
        this.tweenManager = tweenManager;
        initialiseAnimatedReel();
    }

    public AnimatedReel(
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
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.reelDisplayWidth = reelDisplayWidth;
        this.reelDisplayHeight = reelDisplayHeight;
        this.endReel = endReel;
        this.tweenManager = tweenManager;
        this.isReelSpinDirectionClockwise = isReelSpinDirectionClockwise;
        initialiseAnimatedReel();
    }



    private void initialiseAnimatedReel() {
        reel = new ReelTile(
            texture,
            (int) (texture == null ? 0 : texture.getHeight() / tileWidth),
            x,
            y,
            tileWidth,
            tileHeight,
            reelDisplayWidth,
            reelDisplayHeight,
            endReel,
            isReelSpinDirectionClockwise
        );
        reelScrollHeight = texture == null ? 0 : texture.getHeight();
        reel.setSpinning(false);
        velocityY = 4.0f;
        float velocityYMin = getRandomVelocityMin();
        velocityMin = new Vector(0, velocityYMin);
        acceleratorY = 3.0f;
        accelerator = new Vector(0, acceleratorY);
        accelerateY = 2.0f;
        acceleratorFriction = 0.97f;
        velocityFriction = 0.97f;
        reelSlowingTargetTime = 1.0f;
    }

    @Override
    public void setupSpinning() {
        dampenedSine = new DampenedSineParticle(
            0,
            reel.getSy(),
            0,
            0,
            0,
            new Vector(0, velocityY),
            velocityMin,
            new Vector(0, acceleratorY),
            new Vector(0, accelerateY),
            velocityFriction,
            acceleratorFriction);
        dampenedSine.setCallback(dsCallback);
        dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE);
        dampenedSine.setUserData(reel);
    }

    private final SPPhysicsCallback dsCallback = new SPPhysicsCallback() {
        @Override
        public void onEvent(int type, SPPhysicsEvent source) {
            delegateDSCallback(type, source);
        }
    };

    private void delegateDSCallback(int type, SPPhysicsEvent source) {
        if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
            DampenedSineParticle ds = (DampenedSineParticle) source.getSource();
            ReelTile reel = (ReelTile) ds.getUserData();
            Timeline endReelSeq = Timeline.createSequence();
            float endSy = (reel.getEndReel() * this.tileWidth) % this.reelScrollHeight;
            reel.setSy(reel.getSy() % (this.reelScrollHeight));
            endReelSeq.push(
                SlotPuzzleTween.to(reel, ReelAccessor.SCROLL_XY, reelSlowingTargetTime)
                    .target(0f, endSy)
                    .ease(Elastic.OUT)
                    .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
                    .setCallback(slowingSpinningCallback)
                    .setUserData(reel));
            endReelSeq.start(tweenManager);
        }
    }

    private final TweenCallback slowingSpinningCallback = new TweenCallback() {
        @Override
        public void onEvent(int type, BaseTween<?> source) {
            delegateSlowingSpinning(type, source);
        }
    };

    private void delegateSlowingSpinning(int type, BaseTween<?> source) {
        ReelTile reel = (ReelTile)source.getUserData();
        if (type == TweenCallback.END) {
            reel.stopSpinning();
            reel.processEvent(new ReelStoppedSpinningEvent());
        }
    }

    @Override
    public void setX(float x) {
        reel.setX(x);
    }

    @Override
    public void setY(float y) {
        reel.setY(y);
    }

    @Override
    public void setSx(float sx) {
        reel.setSx(sx);
    }

    @Override
    public void setSy(float sy) {
        reel.setSy(sy);
    }

    @Override
    public Texture getTexture() {
        return texture;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public float getSx() {
        return reel.getSx();
    }

    @Override
    public float getSy() {
        return reel.getSy();
    }

    @Override
    public float getTileWidth() {
        return tileWidth;
    }

    @Override
    public float getTileHeight() {
        return tileHeight;
    }

    @Override
    public float getReelDisplayWidth() {
        return reelDisplayWidth;
    }

    @Override
    public float getReelDisplayHeight() {
        return reelDisplayHeight;
    }

    @Override
    public int getEndReel() {
        return reel.getEndReel();
    }

    @Override
    public void setEndReel(int endReel) {
        reel.setEndReel(endReel);
    }

    @Override
    public void update(float delta) {
        reel.update(delta);
        if (dampenedSine == null)
            return;
        dampenedSine.update();
        if (dampenedSine.getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE)
            reel.setSy(dampenedSine.position.y);
    }

    @Override
    public void draw(SpriteBatch spriteBatch) {
        reel.draw(spriteBatch);
    }

    @Override
    public void draw(ShapeRenderer shapeRenderer) {
        reel.drawFlashSegments(shapeRenderer);
    }

    @Override
    public ReelTile getReel() {
        return reel;
    }

    @Override
    public void reinitialise() {
        reel.clearReelFlashSegments();
        if (dampenedSine == null)
            return;
        dampenedSine.initialiseDampenedSine();
        dampenedSine.position.y = reel.getSy();
        dampenedSine.velocity = new Vector(0, velocityY);
        accelerator = new Vector(0, acceleratorY);
        dampenedSine.accelerator = accelerator;
        Vector accelerate = new Vector(0, accelerateY);
        dampenedSine.accelerate(accelerate);
        dampenedSine.velocityMin.y = getRandomVelocityMin();
    }

    @Override
    public DampenedSineParticle.DSState getDampenedSineState() {
        return dampenedSine == null ? null : dampenedSine.getDSState();
    }

    private float getRandomVelocityMin() {
        float VELOCITY_MIN = 1;
        float VELOCITY_MAX = 3;
        return Random.getInstance().nextFloat() * (VELOCITY_MAX - VELOCITY_MIN + 1.0f) + VELOCITY_MIN;
    }
}
