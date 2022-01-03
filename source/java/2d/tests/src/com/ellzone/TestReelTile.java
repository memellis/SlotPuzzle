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

package com.ellzone;

import static org.junit.Assert.assertTrue;

import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.ellzone.slotpuzzle2d.sprites.reel.ReelTile;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import de.tomgrill.gdxtesting.GdxTestRunnerGetAllTestClasses;

@RunWith(GdxTestRunnerGetAllTestClasses.class)
public class TestReelTile {
	private AssetManager assetManager;
 	private TextureAtlas reelAtlas;
	private Sprite cheesecake, cherry, grapes, jelly, lemon, peach, pear, tomato;
	private Sprite[] sprites;
	private float spriteWidth, spriteHeight;
	private Pixmap slotReelScrollPixmap;
	private Texture slotReelScrollTexture;
	private Random random;
	
	@Before
	public void setUp() throws Exception {
		loadAssets();
		createSprites();
		createSlotReelTexture();
		random = new Random();
	}
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testReelTile() {
        int endReel = random.nextInt(sprites.length);
        spriteWidth = sprites[0].getWidth();
        spriteHeight = sprites[0].getHeight();;
        ReelTile reelTile = new ReelTile(
        		slotReelScrollTexture,
				0,
				0,
				(int) spriteWidth,
				(int) spriteHeight,
				endReel
        );
        reelTile.setX(0);
        reelTile.setY(0);
        reelTile.setSx(0);
        reelTile.setSy(0);
        
        TextureRegion region;
        Pixmap reelRegionPixmap;
        Pixmap spritePixmap;
  
        for (int loopThruReelTileCo = 0;
			 loopThruReelTileCo < sprites.length + 1;
			 loopThruReelTileCo++) {
        	for (int i = 0; i < sprites.length; i++) {
        		for(int sY = i * (int) spriteHeight +
								(loopThruReelTileCo * slotReelScrollTexture.getHeight());
        				sY < i * (int) spriteHeight +
								(loopThruReelTileCo * slotReelScrollTexture.getHeight()) + 1;
        				sY++) {
        			reelTile.setSy(sY);
        			reelTile.update(0f);
        		}
        		region = reelTile.getRegion();
        		reelRegionPixmap = PixmapProcessors.getPixmapFromTextureRegion(region);
        		PixmapProcessors.savePixmap(reelRegionPixmap, "pixmap1.png");
        		spritePixmap = PixmapProcessors.getPixmapFromSprite(sprites[i]);
        		PixmapProcessors.savePixmap(spritePixmap, "pixmap2.png");
        		assertTrue(PixmapProcessors.arePixmapsEqual(reelRegionPixmap, spritePixmap));
        	}
        }
	}
		
	private void loadAssets() {
		assetManager = new AssetManager();
		assetManager.load("reel/reels.pack.atlas", TextureAtlas.class);
 		assetManager.finishLoading();
		reelAtlas = assetManager.get("reel/reels.pack.atlas", TextureAtlas.class);
	}
	
	private void createSprites() {
		cherry = reelAtlas.createSprite("cherry");
		cheesecake = reelAtlas.createSprite("cheesecake");
		grapes = reelAtlas.createSprite("grapes");
		jelly = reelAtlas.createSprite("jelly");
		lemon = reelAtlas.createSprite("lemon");
		peach = reelAtlas.createSprite("peach");
		pear = reelAtlas.createSprite("pear");
		tomato = reelAtlas.createSprite("tomato");
		
		sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
		for (Sprite sprite : sprites) {
			sprite.setOrigin(0, 0);
		}
		spriteWidth = sprites[0].getWidth();
		spriteHeight = sprites[0].getHeight();
	}

	private void createSlotReelTexture() {
        slotReelScrollPixmap = new Pixmap(
        		(int) spriteWidth, (int) spriteWidth, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        PixmapProcessors.savePixmap(slotReelScrollPixmap, "mySlotReelScrollPixmap.png");
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
	}
}
