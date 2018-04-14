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

package com.ellzone.slotpuzzle2d.physics;

public class Vector {
	public float x = 1;
	public float y = 0;
	
	public Vector(float x, float y) {
		setX(x);
		setY(y);
	}
	
	public void setX(float x) {
		this.x = x;
	}
	
	public void setY(float y) {
		this.y = y;
	}
	
	public float getX() {
		return this.x;
	}
	
	public float getY() {
		return this.y;
	}
	
	public void setAngle(float angle) {
		float length = this.getLength();
		this.x = (float) Math.cos(angle) * length;
		this.y = (float) Math.sin(angle) * length;
	}
	
	public float getAngle() {
		return (float) Math.atan2(this.y, this.x);
	}
	
	public void setLength(float length) {
		float angle = this.getAngle();
		this.x = (float) Math.cos(angle) * length;
		this.y = (float) Math.sin(angle) * length;
	}
	
	public float getLength() {
		return (float) Math.sqrt(this.x * this.x + this.y + this.y);
	}
	
	public Vector add(Vector v2) {
		return new Vector(this.x + v2.getX(), this.y + v2.getY());
	}

	
	public Vector subtract(Vector v2) {
		return new Vector(this.x - v2.getX(), this.y - v2.getY());
	}
	
	public Vector multiply(float multiplier) {
		return new Vector(this.x * multiplier, this.y * multiplier);
	}
	
	public Vector divide(float divisor) {
		return new Vector(this.x / divisor, this.y / divisor);
	}
	
	public void addTo(Vector v2) {
		this.x += v2.getX();
		this.y += v2.getY();
	}
	
	public void subtractFrom(Vector v2) {
		this.x -= v2.getX();
		this.y -= v2.getY();
	}
	
	public void mulitplyBy(float multiplier) {
		this.x *= multiplier;
		this.y *= multiplier;
	}
	
	public void divideBy(float divisor) {
		this.x /= divisor;
		this.y /= divisor;
	}
}
