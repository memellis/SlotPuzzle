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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ReelLetterTile extends ReelSprite {

	private Texture texture;
    private TextureRegion region;
	private float tileWidth, tileHeight, sx, sy;
	private boolean tileDeleted = false;
	
	public ReelLetterTile(Texture texture, float x, float y, float tileWidth, float tileHeight, int endReel) {
        this.texture = texture;
        setX(x);
        setY(y);
        this.sx = 0;
        this.sy = 0;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        super.setEndReel(endReel);
        defineReelLetterTile();
    }
	
	private void defineReelLetterTile() {
        setPosition((int)getX(), (int)getY());
        setOrigin((int)getX(), (int)getY());
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        region = new TextureRegion(texture);
        region.setRegion((int)0, (int)0, (int)tileWidth, (int)tileHeight);
        setBounds((int)getX(), (int)getY(), (int)tileWidth, (int)tileHeight);
        setRegion(region);
		super.setSpinning(false);
	}

	@Override
	public void update(float dt) {
        if (super.isSpinning()) {
        	processSpinningState();
        }
	}
	
	private void processSpinningState() {
        float syModulus = sy % texture.getHeight();
        region.setRegion((int) sx, (int) syModulus, (int)tileWidth, (int)tileHeight);
        setRegion(region);		
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
	
	public TextureRegion getRegion() {
		return this.region;
	}

	@Override
	public void dispose() {
		if (region != null) {
			region.getTexture().dispose();
		}
	}	
	
	public int getTextureHeight() {
		return this.texture.getHeight();
	}
}
