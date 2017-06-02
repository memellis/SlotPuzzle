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

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.DelayedRemovalArray;
import com.ellzone.slotpuzzle2d.SlotPuzzle;

public class ReelLetter extends ReelSprite {
	
	public static int instanceCount = 0;
	private final DelayedRemovalArray<ReelTileListener> listeners = new DelayedRemovalArray<ReelTileListener>(0);
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
		setEndReel(endReel);
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

		reelAnimationFast = new Animation<TextureRegion>(frameRate, reelFrames);
		reelAnimationFast.setPlayMode(Animation.PlayMode.LOOP);
		stateTime = 0f;
		endStateTime = 0f;
		reelSpinTime = reelAnimationFast.getAnimationDuration();
		int initialFrame = 0;
		setBounds(this.x, this.y, reelFrames[initialFrame].getRegionWidth(), reelFrames[initialFrame].getRegionHeight());
		setRegion(reelFrames[initialFrame]);
		setSpinning(true);
		animationCompleted = false;
	}

	public void update(float dt) {
		stateTime += dt;
		reelSpinTime -= dt;
		if (reelSpinTime > 0) {
			setRegion((TextureRegion) reelAnimationFast.getKeyFrame(stateTime, true));
		} else {
			if (endStateTime == 0) {
				float remainder = (((stateTime / frameRate)) % reelCols) * frameRate;
                endStateTime = stateTime + (getEndReelFrame() * frameRate)  - remainder  - 1;
			}
			if (stateTime < endStateTime) {
				setRegion((TextureRegion)reelAnimationFast.getKeyFrame(stateTime, true));
			} else {
    			setRegion(reelFrames[getEndReelFrame()]);
				if (!animationCompleted) {
					animationCompleted = true;
					setSpinning(false);
					ReelLetter.instanceCount--;
					Gdx.app.debug(SlotPuzzle.SLOT_PUZZLE, "Reel stopped spinning - instanceCount="+String.valueOf(ReelLetter.instanceCount + " reelId=" + reelId));
				}
			}
		}
	}
	
	private int getEndReelFrame() {
        int row;
        if ((getEndReel() - initialRow) < 0) {
			row = reelRows + (getEndReel() - initialRow);
		} else {
			row = getEndReel() - initialRow;
		}
        int endReelFrame = ((row + 1) * reelCols) - 1;
 		return endReelFrame;
	}

    public void dispose() {
        for (TextureRegion frame : reelFrames) {
            frame.getTexture().dispose();
        }
    }
}
