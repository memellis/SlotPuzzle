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

package com.ellzone.slotpuzzle2d.sprites;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.SlotPuzzleConstants;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class ReelSlotTile extends ReelSprite {
	public static int instanceCount = 0;
	public static int reelsSpinning = 0;
	private Animation<TextureRegion> reelAnimationFast;
	private Texture reelTexture;
	private int reelRows;
	private int reelCols;
	private float frameRate;
	private float x;
	private float y;
	private int reelId;
	private TextureRegion[] reelFrames;
	private float stateTime;
	private float endStateTime;
	private float reelSpinTime;
	private Random random;
	private int initialRow;
	private boolean reelFlash;
	private float reelFlashTimer;
	private int reelFlashCount = 35;
	public enum FlashState {FLASH_OFF, FLASH_ON};
	private FlashState reelFlashState; 
	private boolean deleteReelTile;
	private int score;

	public ReelSlotTile(Texture reelTexture, int reelRows, int reelCols, float frameRate, float x, float y, int endReel) {
		this.reelTexture = reelTexture;
		this.reelRows = reelRows;
		this.reelCols = reelCols;
		this.frameRate = frameRate;
		this.x = x;
		this.y = y;
		super.setEndReel(endReel);
		defineReelSlotSprite();
	}
	
	private void defineReelSlotSprite() {
		random = new Random();
		TextureRegion[][] reelGrid = TextureRegion.split(reelTexture, reelTexture.getWidth() / reelCols, reelTexture.getHeight() / reelRows);
		reelFrames = new TextureRegion[reelCols * reelRows];
				
		int index = 0;
		initialRow = random.nextInt(reelRows);
		
		for (int i = initialRow; i < reelRows; i++) {
			for (int j = 0; j < reelCols; j++) {
				reelFrames[index++] = reelGrid[i][j];
			}
		}
		for (int i = 0; i < initialRow; i++) {
			for (int j = 0; j < reelCols; j++) {
				reelFrames[index++] = reelGrid[i][j];
			}
		}
		
		reelAnimationFast = new Animation<TextureRegion>(frameRate, reelFrames);
		stateTime = 0f;
		endStateTime = 0f;
		reelSpinTime = reelAnimationFast.getAnimationDuration();
		int initialFrame = random.nextInt(reelFrames.length);
		setBounds(this.x, this.y, reelFrames[initialFrame].getRegionWidth(), reelFrames[initialFrame].getRegionHeight());
		setRegion(reelFrames[initialFrame]);
		super.setSpinning(true);
		reelFlash = false;
		reelFlashTimer = 0.4f;
		ReelSlotTile.reelsSpinning++;
		reelFlashState = FlashState.FLASH_OFF;
		deleteReelTile = false;
	}

	public void update(float dt) {
		stateTime += dt;
		reelSpinTime -= dt;
		if (reelSpinTime > 0) {
			setRegion((TextureRegion) reelAnimationFast.getKeyFrame(stateTime, true));
		} else {
			if (endStateTime == 0) {
				endStateTime = stateTime;
			}
			if (stateTime <= endStateTime + ((reelRows - initialRow + getEndReel()) * reelCols) * frameRate) {
				setRegion((TextureRegion) reelAnimationFast.getKeyFrame(stateTime, true));
			} else {
				if (isSpinning()) {
					setRegion((TextureRegion) reelAnimationFast.getKeyFrame(getEndReelFrameTime(), true));
					super.setSpinning(false);
					processEvent(new ReelStoppedSpinningEvent());
					ReelSlotTile.instanceCount--;
					ReelSlotTile.reelsSpinning--;
					Gdx.app.debug(SlotPuzzleConstants.SLOT_PUZZLE, "Reel stopped spinning - instanceCount="+String.valueOf(ReelLetter.instanceCount + " reelId=" + reelId));
				} else {
					if (reelFlash) {
						reelFlashTimer -= dt;
						if (reelFlashTimer < 0) {
							reelFlashTimer = 0.4f;
							reelFlashCount--;
							if (reelFlashCount <= 0) {
								reelFlash = false;
								reelFlashState = FlashState.FLASH_OFF;
								processEvent(new ReelStoppedFlashingEvent());
							} else {
								if (reelFlashState == FlashState.FLASH_OFF) {
									reelFlashState = FlashState.FLASH_ON;
									TextureRegion flashReel = drawFlashOn((TextureRegion) reelAnimationFast.getKeyFrame(getEndReelFrameTime(), true));
									setRegion(flashReel);
								} else {
									reelFlashState = FlashState.FLASH_OFF;									
									setRegion((TextureRegion) reelAnimationFast.getKeyFrame(getEndReelFrameTime(), true));
								}
							}
						}
					}
				}
			}
		}
	}
	
	private float getEndReelFrameTime() {
		return endStateTime + ((reelRows - initialRow + getEndReel()) * reelCols) * frameRate;
	}

	public void setSpinning(boolean spinning) {
		super.setSpinning(spinning);
		if (spinning) {
			stateTime = 0f;
			endStateTime = 0f;
			reelSpinTime = reelAnimationFast.getAnimationDuration();
			setEndReel(random.nextInt(reelRows));
			ReelSlotTile.reelsSpinning++;
		}
	}

	public int getCurrentReel() {
		int frameIndex = reelAnimationFast.getKeyFrameIndex(stateTime);
		return (((frameIndex % reelCols) /  4) + 1 + initialRow) % reelRows;
	}
	
	public FlashState getFlashState() {
		return reelFlashState;
	}
	
	public boolean isFlashing() {
		return reelFlash;
	}
	
	public void setFlashMode(boolean reelFlash) {
		this.reelFlash = reelFlash;
	}
	
	private TextureRegion drawFlashOn(TextureRegion reel) {
		Pixmap reelPixmap = PixmapProcessors.getPixmapFromTextureRegion(reel);
		reelPixmap.setColor(Color.RED);
		reelPixmap.drawRectangle(0, 0, 32, 32);
		reelPixmap.drawRectangle(1, 1, 30, 30);
		reelPixmap.drawRectangle(2, 2, 28, 28);
		TextureRegion textureRegion = new TextureRegion(new Texture(reelPixmap));
		return textureRegion;
	}

	public boolean isReelTileDeleted() {
		return deleteReelTile;
	}
	
	public void deleteReelTile() {
		deleteReelTile = true;
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public int getScore() {
		return this.score;
	}
	
	public void dispose() {
		for (TextureRegion frame : reelFrames) {
			frame.getTexture().dispose();
		}
	}
}
