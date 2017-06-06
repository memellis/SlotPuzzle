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

public class Particle {
	public Vector position;
	public Vector velocity;
	public float mass = 1.0f;
	public float radius = 0;
	public float bounce = -1;
	public float friction = 1;
	public Vector gravity;

	public Particle(float x, float y, float speed, float direction, float grav) {
		this.position = new Vector(x, y);
		this.velocity = new Vector(0, 0);
		this.velocity.setLength(speed);
		this.velocity.setAngle(direction);
		this.gravity = new Vector(0, grav);
	}
	
	public void accelerate(Vector accel) {
		this.velocity.addTo(accel);
	}
	
	public void update() {
		this.velocity.mulitplyBy(this.friction);
		this.velocity.addTo(this.gravity);
		this.position.addTo(this.velocity);
	}
	
	public float angleTo(Particle p2) {
		return (float) Math.atan2(p2.position.getY() - this.position.getY(), p2.position.getX() - this.position.getX());
	}
	
	public float distanceTo(Particle p2) {
		float dx = p2.position.getX() - this.position.getX(),
			  dy = p2.position.getY() - this.position.getY();
		return (float) Math.sqrt(dx * dx + dy + dy);
	}
	
	public void gravitateTo(Particle p2) {
		Vector grav = new Vector(0, 0);
		float dist = this.distanceTo(p2);
		
		grav.setLength(p2.mass / (dist * dist));
		grav.setAngle(this.angleTo(p2));
		this.velocity.add(grav);
	}
}
