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
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class ReelTile extends ReelSprite {
    private Texture texture;
    private int numberOfReelsInTexture = 0;
    private TextureRegion region, flashReel;
    private float tileWidth;
    private float tileHeight;
    private float reelDisplayWidth = 0, reelDisplayHeight = 0;
    private float x;
    private float y;
    private float sx = 0;
    private float sy = 0;
	private boolean tileDeleted;
	private boolean reelFlash;
	private boolean reelFlashTween;
	public enum FlashState {FLASH_OFF, FLASH_ON};
	private FlashState reelFlashState, flashingState;
	private Color flashColor;
	private int score;
	private Sound spinningSound;
	private long spinningSoundId;
	private float spinngPitch;
	private Random random;
	
    public ReelTile(Texture texture, float x, float y, float tileWidth, float tileHeight, int endReel, Sound spinningSound) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        super.setEndReel(endReel);
        this.spinningSound = spinningSound;
        defineReelSlotTileScroll();
    }

    public ReelTile(Texture texture, int numberOfReelsInTexture, float x, float y, float tileWidth, float tileHeight, float reelDisplayWidth, float reelDisplayHeight, int endReel, Sound spinningSound) {
        this.texture = texture;
        this.numberOfReelsInTexture = numberOfReelsInTexture;
        this.x = x;
        this.y = y;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.reelDisplayWidth = reelDisplayWidth;
        this.reelDisplayHeight = reelDisplayHeight;
        super.setEndReel(endReel);
        this.spinningSound = spinningSound;
        defineReelSlotTileScroll();
    }

    private void defineReelSlotTileScroll() {
    	random = new Random();
        setPosition((int)this.x, (int)this.y);
        setOrigin((int)this.x, (int)this.y);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        region = new TextureRegion(texture);
        int randomSy = 0;
        if (numberOfReelsInTexture > 0) {
            randomSy = random.nextInt(numberOfReelsInTexture) * (int)tileHeight;
        }

        if ((reelDisplayWidth == 0) && (reelDisplayHeight == 0)) {
            region.setRegion((int) 0, randomSy, (int) tileWidth, (int) tileHeight);
            setBounds((int) this.x, (int) this.y, (int) tileWidth, (int) tileHeight);
        } else {
            region.setRegion((int) 0, randomSy, (int) reelDisplayWidth, (int) reelDisplayHeight);
            setBounds((int) this.x, (int) this.y, (int) reelDisplayWidth, (int) reelDisplayHeight);
        }
        setRegion(region);
        reelFlash = false;
		reelFlashTween = false;
		reelFlashState = FlashState.FLASH_OFF;
        flashingState = FlashState.FLASH_OFF;
		flashColor = Color.RED;
        tileDeleted = false;
    }

	@Override
    public void update(float delta) {
        if (super.isSpinning()) {
        	processSpinningState();
        }
        if (reelFlashTween) {
        	processFlashTweenState(delta);
        }
    }
	
	private void processSpinningState() {
        float syModulus = sy % texture.getHeight();      
        region.setRegion((int) sx, (int) syModulus, (int)reelDisplayWidth, (int)reelDisplayHeight);
        setRegion(region);
        if (this.spinningSound != null) {
        	this.spinngPitch = this.spinngPitch * 0.999f;
        	this.spinningSound.setPitch(this.spinningSoundId, this.spinngPitch);
        }
 	}
		
	private void processFlashTweenState(float delta) {
        if (flashingState == FlashState.FLASH_OFF) {
            flashingState = FlashState.FLASH_ON;
            return;
        }
        this.setFlashOn();
	}

    public float getSx() {
        return this.sx;
    }

    public float getSy() {
        return this.sy;
    }

    public void setSx(float sx) {
        this.sx = sx;
    }

    public void setSy(float sy) {
        this.sy = sy;
    }

    public void setEndReel() {
        float syModulus = sy % texture.getHeight();
        super.setEndReel((int) ((int) syModulus / tileHeight));
    }

	public int getCurrentReel() {
        float syModulus = sy % texture.getHeight();
 		return (int) ((int) ((syModulus + (tileHeight / 2)) % texture.getHeight()) / tileHeight);
	}

	public boolean isReelTileDeleted() {
        return this.tileDeleted;
	}
	
	public void deleteReelTile() {
        this.tileDeleted = true;
	}
	
	public void startSpinning() {
		super.setSpinning(true);
		if (this.spinningSound != null) {
			startSpinningSound();
		}
	}

	private void startSpinningSound() {
		this.spinngPitch = 1.0f;
		this.spinningSoundId = this.spinningSound.play(1.0f, this.spinngPitch, 1.0f);
		this.spinningSound.setLooping(this.spinningSoundId, true);
	}
	
	public void stopSpinning() {
		super.setSpinning(false);
		if (this.spinningSound != null) {
			stopSpinningSound();
		}
	}
	
	public void stopSpinningSound() {
        this.spinningSound.stop(this.spinningSoundId);
	}
	
	public FlashState getFlashState() {
        return this.reelFlashState;
	}
	
	public boolean isFlashing() {
        return this.reelFlash;
	}
	
	public void setFlashMode(boolean reelFlash) {
        this.reelFlash = reelFlash;
	}
	
	public Color getFlashColor() {
        return this.flashColor;
	}
	
	public void setFlashColor(Color flashColor) {
        this.flashColor = flashColor;
	}
	
	public void setFlashTween(boolean reelFlashTween) {
        this.reelFlashTween = reelFlashTween;
	}
	
	public boolean getFlashTween() {
        return this.reelFlashTween;
	}
	
	public void setFlashOn() {
        flashReel = drawFlashOn(this.region);
		this.setRegion(flashReel);
	}
	
	public void setFlashOff() {
        this.setRegion(region);
	}
	
	private TextureRegion drawFlashOn(TextureRegion reel) {
		Pixmap reelPixmap = PixmapProcessors.getPixmapFromTextureRegion(reel);
		reelPixmap.setColor(flashColor);
		reelPixmap.drawRectangle(0, 0, (int)tileWidth    , (int)tileHeight);
		reelPixmap.drawRectangle(1, 1, (int)tileWidth - 2, (int)tileHeight - 2);
		reelPixmap.drawRectangle(2, 2, (int)tileWidth - 4, (int)tileHeight - 4);
		return new TextureRegion(new Texture(reelPixmap));
	}
	
	public void setScore(int score) {
        this.score = score;
	}
	
	public int getScore() {
        return this.score;
	}
	
	public TextureRegion getRegion() {
        return this.region;
	}

	@Override
	public void dispose() {
		if (region != null) {
			region.getTexture().dispose();
		}
	}
}