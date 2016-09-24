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

package com.ellzone.slotpuzzle2d.physics;

public class DampenedSineParticle extends Particle {
	public static enum DSState { INITIALISED, UPDATING_DAMPENED_SINE, UPDATING_PARTICLE};
	public float dsEndReel;
	public Vector accelerator;
	public Vector velocityMin;
	public float velocityFriction;
	public float acceleratorFriction;
	private SPPhysicsCallback callback;
	private int callbackTriggers;
	private Object userData;
	private DSState dsState;
	private boolean invokedCallback; 
	
	public DampenedSineParticle(float x, float y, float speed, float direction, float grav, Vector velocity, Vector velocityMin, Vector accelerator, Vector accelerate, float velocityFriction, float acceleratorFriction) {
		super(x, y, speed, direction, grav);
		this.accelerator = accelerator;
		this.accelerate(accelerate);
		this.velocity = velocity;
		this.velocityMin = velocityMin;
		this.velocityFriction = velocityFriction;
		this.acceleratorFriction = acceleratorFriction;
		initialiseDampenedSine();
	}

	public void initialiseDampenedSine() {
		dsState = DSState.INITIALISED;
		invokedCallback = false;
	}
	
	public void update() {
		super.update();
		if (velocity.getY() > velocityMin.getY()) {
			updateDampenedSine();
		} else {
			invokeParticleCallback();
		}
	}
	
	private void invokeParticleCallback() {
		dsState = DSState.UPDATING_PARTICLE;
		if (!invokedCallback) {
			invokedCallback = true;
			callCallback(SPPhysicsCallback.PARTICLE_UPDATE);
		}
	}
	
	private void updateDampenedSine() {
		dsState = DSState.UPDATING_DAMPENED_SINE;
		velocity.mulitplyBy(velocityFriction);
		accelerate(accelerator);
		accelerator.mulitplyBy(acceleratorFriction); 
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
}