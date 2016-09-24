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
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class ReelTile extends ReelSprite {
    private Texture texture;
    private TextureRegion region;
    private float tileWidth;
    private float tileHeight;
    private float x;
    private float y;
    private float sx = 0;
    private float sy = 0;
	private boolean tileDeleted;
	private boolean reelFlash;
	private boolean reelFlashTween;
	public enum FlashState {FLASH_OFF, FLASH_ON};
	private FlashState reelFlashState;
	private Color flashColor;
	private int score;

    public ReelTile(Texture texture, float x, float y, float tileWidth, float tileHeight, int endReel) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        super.setEndReel(endReel);
        defineReelSlotTileScroll();
    }

    private void defineReelSlotTileScroll() {
        setPosition((int)this.x, (int)this.y);
        setOrigin((int)this.x, (int)this.y);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        region = new TextureRegion(texture);
        region.setRegion((int)0, (int)0, (int)tileWidth, (int)tileHeight);
        setBounds((int)this.x, (int)this.y, (int)tileWidth, (int)tileHeight);
        setRegion(region);
		super.setSpinning(true);
		reelFlash = false;
		reelFlashTween = false;
		reelFlashState = FlashState.FLASH_OFF;
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
        region.setRegion((int) sx, (int) syModulus, (int)tileWidth, (int)tileHeight);
        setRegion(region);
 	}
		
	private void processFlashTweenState(float delta) {
		setFlashOn();
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
	
	public void setSpinning() {
		super.setSpinning(true);
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
		TextureRegion flashReel = drawFlashOn(region);
		setRegion(flashReel);
	}
	
	public void setFlashOff() {
		setRegion(region);
	}
	
	private TextureRegion drawFlashOn(TextureRegion reel) {
		Pixmap reelPixmap = PixmapProcessors.getPixmapFromTextureRegion(reel);
		reelPixmap.setColor(flashColor);
		reelPixmap.drawRectangle(0, 0, (int)tileWidth    , (int)tileHeight);
		reelPixmap.drawRectangle(1, 1, (int)tileWidth - 2, (int)tileHeight - 2);
		reelPixmap.drawRectangle(2, 2, (int)tileWidth - 4, (int)tileHeight - 4);
		TextureRegion textureRegion = new TextureRegion(new Texture(reelPixmap));
		return textureRegion;
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