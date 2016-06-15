package com.ellzone.slotpuzzle2d.sprites;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class ReelSlotTile extends ReelSprite {
	public static int instanceCount = 0;
	public static int reelsSpinning = 0;
	private final DelayedRemovalArray<ReelSlotTileListener> listeners = new DelayedRemovalArray<ReelSlotTileListener>(0);
	private Screen screen;
	private Animation reelAnimationFast;
	private Texture reelTexture;
	private int reelRows;
	private int reelCols;
	private float frameRate;
	private float x;
	private float y;
	private int endReel;
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
	private boolean spinning;
	
	public ReelSlotTile(Screen screen, Texture reelTexture, int reelRows, int reelCols, float frameRate, float x, float y, int endReel) {
		this.screen = screen;
		this.reelTexture = reelTexture;
		this.reelRows = reelRows;
		this.reelCols = reelCols;
		this.frameRate = frameRate;
		this.x = x;
		this.y = y;
		this.endReel = endReel;
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
		
		reelAnimationFast = new Animation(frameRate, reelFrames);
		stateTime = 0f;
		endStateTime = 0f;
		reelSpinTime = reelAnimationFast.getAnimationDuration();
		int initialFrame = random.nextInt(reelFrames.length);
		setBounds(this.x, this.y, reelFrames[initialFrame].getRegionWidth(), reelFrames[initialFrame].getRegionHeight());
		setRegion(reelFrames[initialFrame]);
		spinning = true;
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
			setRegion(reelAnimationFast.getKeyFrame(stateTime, true));
		} else {
			if (endStateTime == 0) {
				endStateTime = stateTime;
			}
			if (stateTime <= endStateTime + ((reelRows - initialRow + endReel) * reelCols) * frameRate) {
				setRegion(reelAnimationFast.getKeyFrame(stateTime, true));
			} else {
				if (spinning) {
					setRegion(reelAnimationFast.getKeyFrame(getEndReelFrameTime(), true));
					spinning = false;
					processEvent(new ReelStoppedSpinningReelSlotTileEvent());
					ReelSlotTile.instanceCount--;
					ReelSlotTile.reelsSpinning--;
					Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "Reel stopped spinning - instanceCount="+String.valueOf(ReelLetter.instanceCount + " reelId=" + reelId));
				} else {
					if (reelFlash) {
						reelFlashTimer -= dt;
						if (reelFlashTimer < 0) {
							reelFlashTimer = 0.4f;
							reelFlashCount--;
							if (reelFlashCount <= 0) {
								reelFlash = false;
								reelFlashState = FlashState.FLASH_OFF;
								deleteReelTile = true;
								processEvent(new ReelStoppedFlashingReelSlotTileEvent());
							} else {
								if (reelFlashState == FlashState.FLASH_OFF) {
									reelFlashState = FlashState.FLASH_ON;
									TextureRegion flashReel = drawFlashOn(reelAnimationFast.getKeyFrame(getEndReelFrameTime(), true));
									setRegion(flashReel);
								} else {
									reelFlashState = FlashState.FLASH_OFF;									
									setRegion(reelAnimationFast.getKeyFrame(getEndReelFrameTime(), true));
								}
							}
						}
					}
				}
			}
		}
	}
	
	private float getEndReelFrameTime() {
		return endStateTime + ((reelRows - initialRow + endReel) * reelCols) * frameRate;
	}

	public void setSpinning(boolean spinMode) {
		spinning = spinMode;
		if (spinning) {
			stateTime = 0f;
			endStateTime = 0f;
			reelSpinTime = reelAnimationFast.getAnimationDuration();
			endReel = random.nextInt(reelRows);
			ReelSlotTile.reelsSpinning++;
		}
	}
	
	public void setEndReel(int endReel) {
		this.endReel = endReel;
	}
	
	public int getCurrentReel() {
		int frameIndex = reelAnimationFast.getKeyFrameIndex(stateTime);
		TextureRegion currentFrame = reelAnimationFast.getKeyFrame(stateTime);
		TextureRegion firstFrame = reelAnimationFast.getKeyFrame(0);
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
		Pixmap reelPixmap = PixmapProcessors.getPixmapFromtextureRegion(reel);
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
	
	public void dispose() {
		for (TextureRegion frame : reelFrames) {
			frame.getTexture().dispose();
		}
	}
}