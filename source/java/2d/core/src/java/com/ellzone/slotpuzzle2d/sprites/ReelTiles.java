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

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.utils.Random;

public class ReelTiles {
	private Array<ReelTile> reelTiles;
	private Reels reels;
	private Pixmap slotReelScrollPixmap;
	private Texture slotReelScrollTexture;
	private ReelTile reelTile;
	
	public ReelTiles(Reels reels) {
		this.reels = reels;
		initialiseReelTiles();
	}
	
	private void initialiseReelTiles() {
	    reelTiles = new Array<ReelTile>();
	    slotReelScrollPixmap = new Pixmap(reels.getReelHeight(), reels.getReelHeight(), Pixmap.Format.RGBA8888);
	    PixmapProcessors.savePixmap(slotReelScrollPixmap);
	    slotReelScrollTexture = new Texture(slotReelScrollPixmap);
	    reelTile = new ReelTile(slotReelScrollTexture, slotReelScrollTexture.getHeight() / reels.getReelHeight(), 0, 32, reels.getReelWidth(), reels.getReelHeight(), reels.getReelWidth(), reels.getReelHeight(), 0, null);
	    reelTile.setX(0);
	    reelTile.setY(0);
	    reelTile.setSx(0);
	    reelTile.setEndReel(Random.getInstance().nextInt(reels.getReels().length - 1));
	    reelTile.setSy(slotReelScrollTexture.getHeight() + 128 + reelTile.getEndReel() * 32 );
	    reelTiles.add(reelTile);
	}
	
	public Array<ReelTile> getReelTiles() {
		return reelTiles;
	}

	public Texture getSlotReelScrollTexture() {
		return this.slotReelScrollTexture;
	}

	public int getReelTileTextureHeight() {
		return slotReelScrollTexture.getHeight();
	}
}
