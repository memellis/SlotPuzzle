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

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.ellzone.slotpuzzle2d.effects.Particle;

public class Score extends Particle {
	private BitmapFont scoreFont;
	private Color fontColor;
	private int score;
	
	public Score(float x, float y, int score) {
		super.setX(x);
		super.setY(y);
		this.score = score;
		scoreFont = new BitmapFont();
		fontColor = Color.WHITE;
	}
	
	public Color getColor() {
		return this.fontColor;
	}
	
	public void setColor(Color color) {
		this.fontColor = color;
	}
	
	public float getScaleX() {
		return this.scoreFont.getScaleX();
	}
	
	public float getScaleY() {
		return this.scoreFont.getScaleY();
	}
	
	public void setScale(float scaleX, float scaleY) {
		this.scoreFont.getData().setScale(scaleX, scaleY);
	}
	
	public int getScore() {
		return this.score;
	}
	
	public void render(SpriteBatch spritebatch) {
		scoreFont.setColor(fontColor);
		scoreFont.draw(spritebatch, Integer.toString(score), x, y);
	}
}
