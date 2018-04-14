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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Card {
	public final Suit suit;
	public final Pip pip;
	
	private final Sprite front;
	private final Sprite back;
	private float x, y, width, height;
	
	private boolean turned;
	
	public Card(Suit suit, Pip pip, Sprite back, Sprite front) {
		this.suit = suit;
		this.pip = pip;
		this.back = back;
		this.front = front;
	}
	
	public void setPosition(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public void setSize(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
	public void turn() {
		turned = !turned;
	}
	
	public void draw(Batch batch) {
		if (turned) {
			batch.draw(back, this.x, this.y, this.width, this.height);
		} else {
			batch.draw(front, this.x, this.y, this.width, this.height);
		}
	}
}
