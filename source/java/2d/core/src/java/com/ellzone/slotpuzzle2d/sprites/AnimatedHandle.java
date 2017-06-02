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

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

public class AnimatedHandle extends Sprite {
	private Animation<TextureRegion> handleAnimation;
    private TextureRegion region;
	private float stateTimer = 0;
	private TextureAtlas handleAtlas;
	private float x, y;
	private boolean animate;
	   
	public AnimatedHandle(TextureAtlas handleAtlas, float x, float y) {
		this.handleAtlas = handleAtlas;
		this.x = x;
		this.y = y;
		initialiseAnimateHandle();
	}
	
	private void initialiseAnimateHandle() {
		handleAnimation = new Animation<TextureRegion>(0.033f, this.handleAtlas.findRegions("handle"));
		setPosition(this.x, this.y);
        region = getFrame(0.0f);
        setBounds((int)this.x, (int)this.y, region.getRegionWidth(), region.getRegionHeight());
        setRegion(region);
        animate = false;
    }

    public TextureRegion getFrame(float delta){
        TextureRegion region;
        region = handleAnimation.getKeyFrame(stateTimer);
        stateTimer += delta;
        return region;
    }

	public void update(float delta){
		if (animate) {
			if (handleAnimation.isAnimationFinished(stateTimer)) {
				this.animate = false;
				stateTimer = 0;
			}
		    setRegion(getFrame(delta));
		}
	}
	
	public void setAnimated(boolean animate) {
		this.animate = animate;
	}
}
