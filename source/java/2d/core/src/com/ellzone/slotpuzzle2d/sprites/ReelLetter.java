package com.ellzone.slotpuzzle2d.sprites;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.screens.IntroScreen;

public class ReelLetter extends Sprite {
	
	public static int instanceCount = 0;
	
	private int reelId;
	private float x, y;
	private Animation reelLetterAnimationFast;
	private Texture reelText;
	private int reelTextRows;
	private int reelTextCols;
	private float frameRate;
	private TextureRegion[] reelFrames;
	private Screen screen;
	private float stateTime;
	private float endStateTime;
	private float reelSpinTime;
	private Random random;
	private int endReel;
	private int initialRow;
	private boolean animationCompleted;

	public ReelLetter(Screen screen, Texture reelText, int reelTextRows, int reelTextCols, float frameRate, float x, float y, int endReel) {
		reelId = ReelLetter.instanceCount;
		ReelLetter.instanceCount++;
		this.screen = screen;
		this.reelText = reelText;
		this.reelTextRows = reelTextRows;
		this.reelTextCols = reelTextCols;
		this.frameRate = frameRate;
		this.x = x;
		this.y = y;
		this.endReel = endReel;
		defineReelLetterSprite();
	}

	private void defineReelLetterSprite() {		
		random = new Random();
		TextureRegion[][] reelGrid = TextureRegion.split(reelText, reelText.getWidth() / reelTextCols, reelText.getHeight() / reelTextRows);
		reelFrames = new TextureRegion[reelTextCols * reelTextRows];
		
		int index = 0;
		initialRow = random.nextInt(reelTextRows);
		
		for (int i = initialRow; i < reelTextRows; i++) {
			for (int j = 0; j < reelTextCols; j++) {
				reelFrames[index++] = reelGrid[i][j];
			}
		}
		for (int i = 0; i < initialRow; i++) {
			for (int j = 0; j < reelTextCols; j++) {
				reelFrames[index++] = reelGrid[i][j];
			}
		}

		reelLetterAnimationFast = new Animation(frameRate, reelFrames);
		stateTime = 0f;
		endStateTime = 0f;
		reelSpinTime = reelLetterAnimationFast.getAnimationDuration();
		int initialFrame = random.nextInt(reelFrames.length);
		setBounds(this.x, this.y, reelFrames[initialFrame].getRegionWidth(), reelFrames[initialFrame].getRegionHeight());
		setRegion(reelFrames[initialFrame]);
		
		animationCompleted = false;
	}
	
	// Have a method to implement reel friction
		
	public void update(float dt) {
		stateTime += dt;
		reelSpinTime -= dt;
		if (reelSpinTime > 0) {
			setRegion(reelLetterAnimationFast.getKeyFrame(stateTime, true));
		} else {
			if (endStateTime == 0 ) {
				endStateTime = stateTime;
			}
			if (stateTime <= endStateTime + ((reelTextRows - initialRow + endReel) * reelTextCols) * frameRate) {
				setRegion(reelLetterAnimationFast.getKeyFrame(stateTime, true));
			} else {
				setRegion(reelLetterAnimationFast.getKeyFrame(endStateTime + ((reelTextRows - initialRow + endReel) * reelTextCols) * frameRate, true));
				if (!animationCompleted) {
					animationCompleted = true;
					ReelLetter.instanceCount--;
					Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "Reel stopped spinning - instanceCount="+String.valueOf(ReelLetter.instanceCount + " reelId=" + reelId));
				}
			}
		}
	}
}
