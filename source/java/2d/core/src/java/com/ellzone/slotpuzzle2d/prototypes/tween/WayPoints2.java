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

package com.ellzone.slotpuzzle2d.prototypes.tween;

import java.util.Random;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototype;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import com.ellzone.slotpuzzle2d.prototypes.SPPrototypeTemplate;
import aurelienribon.tweenengine.TweenPaths;

public class WayPoints2 extends SPPrototypeTemplate {

    private Pixmap slotReelScrollPixmap;
    private Texture slotReelScrollTexture;
    private Random random;
    private ReelTile reelTile;
    private Array<ReelTile> reelTiles;
    private float tweenDuration;

	@Override
	protected void initialiseOverride() {
        initialiseReelSlots();
        initialiseTweens();
	}

	@Override
	protected void loadAssetsOverride() {
	}

	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride(float dt) {
        for(ReelTile reelTile : reelTiles) {
            reelTile.update(dt);
        }
	}

	@Override
	protected void renderOverride(float dt) {
        batch.begin();
        for (ReelTile reelTile : reelTiles) {
            reelTile.draw(batch);
        }
        batch.end();
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
        SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
	}
	
    private void initialiseReelSlots() {
        random = new Random();
        reelTiles = new Array<ReelTile>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        reelTile = new ReelTile(slotReelScrollTexture, 0, 32, spriteWidth, spriteHeight, 0, null);
        reelTile.setX(0);
        reelTile.setY(0);
        reelTile.setEndReel(random.nextInt(sprites.length));
        reelTiles.add(reelTile);
    }

    private void initialiseTweens() {
        tweenDuration = 10.0f;
        SlotPuzzleTween.set(reelTiles.get(0), ReelAccessor.SCROLL_XY)
                .setDuration(tweenDuration)
                .target(0, reelTile.getEndReel() * 32)
                .waypoint(0,  slotReelScrollTexture.getHeight()*4)
                .waypoint(0, -slotReelScrollTexture.getHeight()*8)
                .waypoint(0,  slotReelScrollTexture.getHeight()*10)
                .waypoint(0, -slotReelScrollTexture.getHeight()*12 + reelTile.getEndReel() * 32)
                .waypoint(0,  reelTile.getEndReel() * 32 + 16)
                .waypoint(0,  reelTile.getEndReel() * 32 - 15)
                .waypoint(0,  reelTile.getEndReel() * 32 + 8)
                .waypoint(0,  reelTile.getEndReel() * 32 - 8)
                .waypoint(0,  reelTile.getEndReel() * 32)
                .path(TweenPaths.catmullRom)
                .start(tweenManager);
    }
}
