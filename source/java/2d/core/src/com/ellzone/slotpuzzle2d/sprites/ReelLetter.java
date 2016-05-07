package com.ellzone.slotpuzzle2d.sprites;

import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class ReelLetter extends Sprite {
	
	public static int instanceCount = 0;
	private final DelayedRemovalArray<ReelSlotTileListener> listeners = new DelayedRemovalArray<ReelSlotTileListener>(0);
	private int reelId;
	private float x, y;
	private Animation reelAnimationFast;
	private Texture reelText;
	private int reelRows;
	private int reelCols;
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
	private boolean spinning;

	public ReelLetter(Screen screen, Texture reelText, int reelRows, int reelCols, float frameRate, float x, float y, int endReel) {
		reelId = ReelLetter.instanceCount;
		ReelLetter.instanceCount++;
		this.screen = screen;
		this.reelText = reelText;
		this.reelRows = reelRows;
		this.reelCols = reelCols;
		this.frameRate = frameRate;
		this.x = x;
		this.y = y;
		this.endReel = endReel;
		defineReelLetterSprite();
	}

	private void defineReelLetterSprite() {		
		random = new Random();
		TextureRegion[][] reelGrid = TextureRegion.split(reelText, reelText.getWidth() / reelCols, reelText.getHeight() / reelRows);
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
		reelAnimationFast.setPlayMode(Animation.PlayMode.LOOP);
		stateTime = 0f;
		endStateTime = 0f;
		reelSpinTime = reelAnimationFast.getAnimationDuration();
		int initialFrame = random.nextInt(reelFrames.length);
		setBounds(this.x, this.y, reelFrames[initialFrame].getRegionWidth(), reelFrames[initialFrame].getRegionHeight());
		setRegion(reelFrames[initialFrame]);
		spinning = true;		
		animationCompleted = false;
	}
	
	// Have a method to implement reel friction
		
	public void update(float dt) {
		stateTime += dt;
		reelSpinTime -= dt;
		if (reelSpinTime > 0) {
			setRegion(reelAnimationFast.getKeyFrame(stateTime, true));
		} else {
			if (endStateTime == 0) {
				float remainder = ((stateTime / frameRate) % reelCols) * frameRate;
				endStateTime = stateTime + getEndReelFrameTime() - remainder;
			}
			if (stateTime < endStateTime) {
				setRegion(reelAnimationFast.getKeyFrame(stateTime, true));
			} else {
				setRegion(reelAnimationFast.getKeyFrame(endStateTime, true));
				if (!animationCompleted) {
					animationCompleted = true;
					spinning = false;
					ReelLetter.instanceCount--;
					Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "Reel stopped spinning - instanceCount="+String.valueOf(ReelLetter.instanceCount + " reelId=" + reelId));
				}
				FileHandle reelLetterLastPixmapFile = Gdx.files.local("reelLetterLastPixmap" + endReel + ".png");
				if (reelLetterLastPixmapFile.exists()) {
					reelLetterLastPixmapFile.delete();
				}
				PixmapProcessors.saveTextureRegion(reelAnimationFast.getKeyFrame(endStateTime, true), reelLetterLastPixmapFile.file());
			}
		}
	}
	
	private float getEndReelFrameTime() {
       int row;
		if ((endReel - initialRow) < 0) {
			row = reelRows + (endReel - initialRow);
		} else {
			row = endReel - initialRow;
		}
		int endReelFrame = ((row + 1) * reelCols) - 1;
 		return endReelFrame * frameRate;
	}
	
	public boolean isSpinning() {
		return spinning;
	}
	
	public boolean addListener (ReelSlotTileListener listener) {
		if (!listeners.contains(listener, true)) {
			listeners.add(listener);
			return true;
		}
		return false;
	}

	public boolean removeListener (ReelSlotTileListener listener) {
		return listeners.removeValue(listener, true);
	}

	public Array<ReelSlotTileListener> getListeners () {
		return listeners;
	}
	
	private void processEvent(ReelSlotTileEvent reelSlotTileEvent) {
		Array<ReelSlotTileListener> tempReelSlotTileListenerList = new Array<ReelSlotTileListener>();

		synchronized (this) {
			if (listeners.size == 0)
				return;
			for(int i = 0; i < listeners.size; i++) {
				tempReelSlotTileListenerList.add(listeners.get(i));
			}
		}

		for (ReelSlotTileListener listener : tempReelSlotTileListenerList) {
			listener.actionPerformed(reelSlotTileEvent);
		}
	}
		
	public int getEndReel() {
		return this.endReel;
	}	
}
