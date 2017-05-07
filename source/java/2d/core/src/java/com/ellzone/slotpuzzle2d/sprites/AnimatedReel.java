package com.ellzone.slotpuzzle2d.sprites;

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
		reel = new ReelTile(this.texture, this.x, this.y, this.tileWidth, this.tileHeight, this.endReel, this.spinningSound);
		reelScrollHeight = this.texture.getHeight();
		reel.setSpinning(false);
		velocityY = 4.0f;
		velocityYMin = 2.0f;
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
	private Vector accelerate;
	
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
        this.reel.setSy(0);
        dampenedSine.initialiseDampenedSine();
        dampenedSine.position.y = 0;
        dampenedSine.velocity = new Vector(0, velocityY);
        accelerator = new Vector(0, acceleratorY);
        dampenedSine.accelerator = accelerator;
        accelerate = new Vector(0, accelerateY);
        dampenedSine.accelerate(accelerate);
        dampenedSine.velocityMin.y = velocityMin.y;
	}
	
	public DampenedSineParticle.DSState getDampenedSineState() {
		return dampenedSine.getDSState();
	}
}
