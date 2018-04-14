/*
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
 */

package com.ellzone.slotpuzzle2d.physics;

public class DampenedSine extends Particle {
	public static final float VELOCITY_MIN = 2.0f;
	public static final float MIN_DAMPENED_SINE_VALUE = 0.0000001f;
	public static final int PLOTTIME_DIVISOR_DAMPER = 32;
	public static final int SPRITE_SQUARE_SIZE = 32;
	public static enum DSState { INITIALISED, UPDATING_DAMPENED_SINE, UPDATING_PARTICLE};
	public float dsEndReel;
	private Vector accelerator;
	private float savedAmplitude;
	private float savedSy;
	private boolean saveAmplitude;
	private float dampPoint;
	private float plotTime;
	private int height;
	private int endReel;
	private SPPhysicsCallback callback;
	private int callbackTriggers;
	private float ds;
	private Object userData;
	private DSState dsState;
	private boolean dsComplete;
	
	public DampenedSine(float x, float y, float speed, float direction, float grav, float dampPoint, int height, int endReel) {
		super(x, y, speed, direction, grav);
		this.dampPoint = dampPoint;
		this.height = height;
		this.endReel = endReel;
		initialiseDampenedSine();
	}

	public void initialiseDampenedSine() {
		plotTime = 132;
		savedAmplitude = 0;
		saveAmplitude = true;
		savedSy = 0;
		accelerator = new Vector(0, 3f);
		accelerate(new Vector(0, 2f));
		velocity.setX(0);
		velocity.setY(4);
		dsState = DSState.INITIALISED;
		dsComplete = false;
	}
	
	public void setEndReel(int endReel) {
		this.endReel = endReel;
	}

	public void update() {
		super.update();
		if (velocity.getY() > DampenedSine.VELOCITY_MIN) {
			updateDampenedSine();
		} else {
			updateParticle();
		}
	}
	
	private void updateParticle() {
		dsState = DSState.UPDATING_PARTICLE;
		if (position.getY() < dampPoint) {
			if (saveAmplitude) {
				saveAmplitude = false;
				savedSy = position.getY() + height - (position.getY() % height);
			    savedAmplitude = (dampPoint - savedSy);
			}
		    ds = dampenedSine(savedAmplitude, 1.0f, (float) (3 * Math.PI), plotTime++ / DampenedSine.PLOTTIME_DIVISOR_DAMPER, 0);
		    dsEndReel = ds + endReel * SPRITE_SQUARE_SIZE;
		    position.y = savedSy + dsEndReel;
		    callCallback(SPPhysicsCallback.PARTICLE_UPDATE);
		    if ((Math.abs(ds) < DampenedSine.MIN_DAMPENED_SINE_VALUE) & (!dsComplete)) {
		    	callCallback(SPPhysicsCallback.END);	
		    	dsComplete = true;
		    }
		} 
	}
	
	private void updateDampenedSine() {
		dsState = DSState.UPDATING_DAMPENED_SINE;
		velocity.mulitplyBy(0.97f);
		accelerate(accelerator);
		accelerator.mulitplyBy(0.97f); 
 	}
	
	public float dampenedSine(float initialAmplitude, float lambda, float angularFrequency, float time, float phaseAngle) {
		return (float) (initialAmplitude * Math.exp(-lambda * time) *  Math.cos(angularFrequency * time + phaseAngle));
	}
	
	public void setCallback(SPPhysicsCallback callback) {
		this.callback = callback;
	}
	
	public void setCallbackTriggers(int flags) {
	      this.callbackTriggers = flags; 	
	}
	
	public void callCallback(int type) {
	      if (callback != null && (callbackTriggers & type) > 0) callback.onEvent(type, new SPPhysicsEvent(this));
	}
	
	public void setUserData(Object userData) {
		this.userData = userData;
	}
	
	public Object getUserData() {
		return userData;
	}
	
	public DSState getDSState() {
		return this.dsState;
	}
	
	public void setDampPoint(float dampPoint) {
		this.dampPoint = dampPoint;
	}
}
