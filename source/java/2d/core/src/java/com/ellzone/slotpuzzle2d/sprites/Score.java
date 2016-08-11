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
	
	public int getScore() {
		return this.score;
	}
	
	public void render(SpriteBatch spritebatch) {
		scoreFont.setColor(fontColor);
		scoreFont.draw(spritebatch, Integer.toString(score), x, y);
	}
}
