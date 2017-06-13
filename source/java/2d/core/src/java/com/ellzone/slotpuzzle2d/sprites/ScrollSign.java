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

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class ScrollSign extends Sprite {
    Texture texture;
	private TextureRegion region;
	float x, y, signWidth, signHeight, sx, sy; 
	public static enum SignDirection {LEFT, RIGHT};
	private SignDirection signDirection;
	
	public ScrollSign(Texture texture, float x, float y, float signWidth, float signHeight, SignDirection signDirection) {
        this.texture = texture;
        this.x = x;
        this.y = y;
        this.signWidth = signWidth;
        this.signHeight = signHeight;
		this.signDirection = signDirection;
        defineScrollSign(); 		
	}
	
	void defineScrollSign() {
		sx = 0;
		sy = 0;
		setPosition((int)x, (int)y);
        setOrigin((int)x, (int)y);
        texture.setWrap(Texture.TextureWrap.Repeat, Texture.TextureWrap.Repeat);
        region = new TextureRegion(texture);
        region.setRegion(0, 0, (int)signWidth, (int)signHeight);
        setBounds((int)x, (int)y, (int)signWidth, (int)signHeight);
        setRegion(region);
	}
	
	public void update(float dt) {
		float sxModulus = sx % texture.getWidth();
        region.setRegion((int) sxModulus, (int) sy, (int) signWidth, (int) signHeight);
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
	
	public void dispose() {
		if (region != null) {
			region.getTexture().dispose();
		}
	}
}
