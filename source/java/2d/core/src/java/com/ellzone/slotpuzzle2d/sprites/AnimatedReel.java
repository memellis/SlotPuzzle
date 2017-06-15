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

import java.util.Random;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.physics.Vector;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import aurelienribon.tweenengine.equations.Elastic;

public class AnimatedReel {
	private static float VELOCITY_MIN = 1;
	private static float VELOCITY_MAX = 3;
	private ReelTile reel;
	private DampenedSineParticle dampenedSine;
	private float velocityY;
	private float velocityYMin;
	private Vector velocityMin;
	private float acceleratorY;
	private Vector accelerator;
	private float accelerateY;
	private float acceleratorFriction;
	private float velocityFriction;
	private Texture texture;
	private float x;
	private float y;
	private float tileWidth;
	private float tileHeight;
	private int endReel;
	private Sound spinningSound, stoppingSound;
	private int reelScrollHeight;
	private TweenManager tweenManager;
	private float reelSlowingTargetTime;
	private Vector accelerate;
	private Random random;
	
	public AnimatedReel(Texture texture, float x, float y, float tileWidth, float tileHeight, int endReel, Sound spinningSound, Sound stoppingSound, TweenManager tweenManager) {
		this.texture = texture;
		this.x = x;
		this.y = y;
		this.tileWidth = tileWidth;
		this.tileHeight = tileHeight;
		this.endReel = endReel;
		this.spinningSound = spinningSound;
		this.stoppingSound = stoppingSound;
		this.tweenManager = tweenManager;
		initialiseAnimatedReel();
	}
	
	private void initialiseAnimatedReel() {
		random = new Random();
		reel = new ReelTile(this.texture, this.x, this.y, this.tileWidth, this.tileHeight, this.endReel, this.spinningSound);
		reelScrollHeight = this.texture.getHeight();
		reel.setSpinning(false);
		velocityY = 4.0f;
		velocityYMin = getRandomVelocityMin();
		velocityMin = new Vector(0, velocityYMin);
		acceleratorY = 3.0f;
		accelerator = new Vector(0, acceleratorY);
		accelerateY = 2.0f;
		acceleratorFriction = 0.97f;
		velocityFriction = 0.97f;
		reelSlowingTargetTime = 3.0f;
		dampenedSine = new DampenedSineParticle(0, reel.getSy(), 0, 0, 0, new Vector(0, velocityY), velocityMin, new Vector(0, acceleratorY), new Vector(0, accelerateY), velocityFriction, acceleratorFriction);
		dampenedSine.setCallback(dsCallback);
		dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE);
		dampenedSine.setUserData(reel);
	}
	
	private SPPhysicsCallback dsCallback = new SPPhysicsCallback() {
		@Override
		public void onEvent(int type, SPPhysicsEvent source) {
			delegateDSCallback(type, source); 
		}
	};
	
	private void delegateDSCallback(int type, SPPhysicsEvent source) {
		if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
			reel.stopSpinningSound();
			this.stoppingSound.play();
			DampenedSineParticle ds = (DampenedSineParticle)source.getSource();
			ReelTile reel = (ReelTile)ds.getUserData();
			Timeline endReelSeq = Timeline.createSequence();
			float endSy = (reel.getEndReel() * this.tileHeight) % this.reelScrollHeight;		
			reel.setSy(reel.getSy() % (this.reelScrollHeight));
	        endReelSeq = endReelSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.SCROLL_XY, reelSlowingTargetTime)
	        		               .target(0f, endSy)
	        		               .ease(Elastic.OUT)
	        		               .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
	        		               .setCallback(slowingSpinningCallback)
	        		               .setUserData(reel));	        					
	        endReelSeq = endReelSeq
	        				.start(tweenManager);
		}
	}
	
	private TweenCallback slowingSpinningCallback = new TweenCallback() {
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
	
	public void setX(float x) {
		reel.setX(x);
	}
	
	public void setY(float y) {
		reel.setY(y);
	}
	
	public void setSx(float sx) {
		reel.setSx(sx);
	}
	
	public void setSy(float sy) {
		reel.setSy(sy);
	}
	
	public float getSx() {
		return reel.getSx();
	}
	
	public float getSy() {
		return reel.getSy();
	}
	
	public int getEndReel() {
		return reel.getEndReel();
	}
	
	public void setEndReel(int endReel) {
		reel.setEndReel(endReel);
	}
	
	public void update(float delta) {
		reel.update(delta);
		dampenedSine.update();
		if (dampenedSine.getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
     		reel.setSy(dampenedSine.position.y);
 	    }
	}
	
	public void draw(SpriteBatch spriteBatch) {
		reel.draw(spriteBatch);
	}
	
	public ReelTile getReel() {
		return reel;
	}
	
	public void reinitialise() {
        this.reel.setSpinning(true);
        dampenedSine.initialiseDampenedSine();
        dampenedSine.position.y = reel.getSy();
        dampenedSine.velocity = new Vector(0, velocityY);
        accelerator = new Vector(0, acceleratorY);
        dampenedSine.accelerator = accelerator;
        accelerate = new Vector(0, accelerateY);
        dampenedSine.accelerate(accelerate);
        dampenedSine.velocityMin.y = getRandomVelocityMin();
	}
	
	public DampenedSineParticle.DSState getDampenedSineState() {
		return dampenedSine.getDSState();
	}

    private float getRandomVelocityMin() {
    	return random.nextFloat() * (VELOCITY_MAX - VELOCITY_MIN + 1.0f) + VELOCITY_MIN; 
    }
}
