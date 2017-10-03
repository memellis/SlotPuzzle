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

package com.ellzone.slotpuzzle2d.prototypes;

import com.ellzone.slotpuzzle2d.utils.Assets;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.Sprite;

public class Reels {
	public final static String REEL_PACK_ATLAS = "reel/reels.pack.atlas";
	public final static String CHERRY = "cherry";
	public final static String CHEESECAKE = "cheesecake";
	public final static String GRAPES = "grapes";
	public final static String JELLY = "jelly";
	public final static String LEMON = "lemon";
	public final static String PEACH = "peach";
	public final static String PEAR = "pear";
	public final static String TOMATO = "tomato";
	private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite[] reels;
	private int spriteWidth;
    private int spriteHeight;
	
	public Reels() {
		initialiseReels();
	}
	
	private void initialiseReels() {
	    Assets.inst().load(REEL_PACK_ATLAS, TextureAtlas.class);
	    Assets.inst().update();
	    Assets.inst().finishLoading();

	    TextureAtlas atlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
	    cherry = atlas.createSprite(CHERRY);
	    cheesecake = atlas.createSprite(CHEESECAKE);
	    grapes = atlas.createSprite(GRAPES);	    jelly = atlas.createSprite("jelly");
	    lemon = atlas.createSprite(LEMON);
	    peach = atlas.createSprite(PEACH);
	    pear = atlas.createSprite(PEAR);
	    tomato = atlas.createSprite(TOMATO);

	    reels = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
	    for (Sprite sprite : reels) {
		    sprite.setOrigin(0, 0);
	    }
	    spriteWidth = (int) reels[0].getWidth();
	    spriteHeight = (int) reels[0].getHeight();
    }

	public int getReelWidth() {
		return spriteWidth;
	}
	
	public int getReelHeight() {
		return spriteHeight;
	}
	
	public Sprite[] getReels() {
		return reels;
	}
	
	public void dispose() {
		Assets.inst().dispose();
		for (Sprite reel : reels) {
		    reel.getTexture().dispose();
		}
	}
}
