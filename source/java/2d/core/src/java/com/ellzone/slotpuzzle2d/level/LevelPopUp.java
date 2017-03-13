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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quad;

public class LevelPopUp {

    public static final String LOG_TAG = "SlotPuzzle_PlayScreen";
    private TweenManager tweenManager;
	private Array<Sprite> sprites;
	private BitmapFont levelFont, font;
	private String currentLevel;
	private String levelDescription;
	private int sW = SlotPuzzle.V_WIDTH;
	private int sH = SlotPuzzle.V_HEIGHT;
	
	public LevelPopUp(SpriteBatch batch, TweenManager tweenManager, Array<Sprite> sprites, BitmapFont levelFont, String currentLevel, String levelDescription) {
		this.tweenManager = tweenManager;
		this.sprites = sprites;
		this.levelFont = levelFont;
		this.currentLevel = currentLevel;
		this.levelDescription = levelDescription;
		font = new BitmapFont();
	}
	
	public void showLevelPopUp(TweenCallback callback) {
		Gdx.app.log(LOG_TAG, "sW="+sW);
        Gdx.app.log(LOG_TAG, "sH="+sH);

        Timeline timeline = Timeline.createSequence()
		    .push(SlotPuzzleTween.set(sprites.get(0), SpriteAccessor.SCALE_XY).target(0.1f, 0))
		    .push(SlotPuzzleTween.set(sprites.get(1), SpriteAccessor.POS_XY). target(-200, sH / 2 - sprites.get(1).getHeight() / 2));
		
		if(sprites.size == 4) {
			timeline = timeline
			    .push(SlotPuzzleTween.set(sprites.get(2), SpriteAccessor.POS_XY). target(-200, sH / 2 - sprites.get(2).getHeight() / 2 - 20))
		        .push(SlotPuzzleTween.set(sprites.get(3), SpriteAccessor.POS_XY). target(sW + 200, sH / 2 - sprites.get(3).getHeight() /2 - 20));
		}
		
		timeline = timeline    
		    .pushPause(0.5f)
		    .push(SlotPuzzleTween.to(sprites.get(0), SpriteAccessor.SCALE_XY, 0.8f).target(1, 0.6f).ease(Back.OUT))
		    .pushPause(-0.3f)
		    .beginParallel()
		        .push(SlotPuzzleTween.to(sprites.get(0), SpriteAccessor.SCALE_XY, 0.5f).target(1.1f, 1.1f).ease(Back.IN))
		        .push(SlotPuzzleTween.to(sprites.get(0), SpriteAccessor.OPACITY, 0.5f).target(0.7f).ease(Back.IN))
		    .end()
		    .pushPause(0.3f)
		    .beginParallel()
		         .push(SlotPuzzleTween.to(sprites.get(1), SpriteAccessor.POS_XY, 1.0f).target(sW / 2 - sprites.get(1).getWidth() / 2 - 20, sH / 2 - sprites.get(1).getHeight() /2 + 50).ease(Back.INOUT))
		    .end();
		
		if (sprites.size == 4) {
			float tW = sprites.get(2).getWidth() + sprites.get(3).getWidth();
			float tH = Math.max(sprites.get(2).getHeight(), sprites.get(3).getHeight());
			timeline = timeline
			    .beginParallel()
			        .push(SlotPuzzleTween.to(sprites.get(2), SpriteAccessor.POS_XY, 1.0f).target((sW - tW) / 2, sH / 2 - tH / 2 - 80).ease(Back.INOUT))
			        .push(SlotPuzzleTween.to(sprites.get(3), SpriteAccessor.POS_XY, 1.0f).target((sW - tW) / 2 + sprites.get(2).getWidth(), sH / 2 - tH / 2 - 80).ease(Back.INOUT)) 		        
			    .end();
		}
		
		timeline = timeline
		    .pushPause(0.5f);
		
		if(callback != null) {
			timeline.setCallback(callback);	
			timeline.setCallbackTriggers(TweenCallback.END);
		}
		timeline.start(tweenManager);
	}

	public void hideLevelPopUp(TweenCallback callback) {
		Timeline timeline = Timeline.createSequence()
		    .pushPause(0.25f)
			.beginParallel()
			    .push((SlotPuzzleTween.to(sprites.get(0), SpriteAccessor.POS_XY, 1.5f)
			    	.waypoint(sW / 2 - sprites.get(0).getWidth() / 2, sH / 2 - sprites.get(0).getHeight() / 2 + 64)
			    	.target(sW / 2 - sprites.get(0).getWidth() / 2, -300).ease(Quad.OUT)))
			    .push((SlotPuzzleTween.to(sprites.get(1), SpriteAccessor.POS_XY, 1.5f)
			        .waypoint(sW / 2 - sprites.get(1).getWidth() / 2 - 20, sH / 2 - sprites.get(1).getHeight() / 2 + 64)
			        .target(sW / 2 - sprites.get(1).getWidth() / 2 - 20, -300).ease(Quad.OUT)));

		if (sprites.size == 4) {
			timeline = timeline
			        .push((SlotPuzzleTween.to(sprites.get(2), SpriteAccessor.POS_XY, 1.5f)
				        .waypoint(sW / 2  - sprites.get(2).getWidth() / 2 - 40, sH / 2 - sprites.get(2).getHeight() / 2 - 20)
				        .target(sW / 2  - sprites.get(2).getWidth() / 2 - 40, -300).ease(Quad.OUT)))
			        .push((SlotPuzzleTween.to(sprites.get(3), SpriteAccessor.POS_XY, 1.5f)
					        .waypoint(sW / 2 + sprites.get(3).getWidth() / 2 - 35, sH / 2 - sprites.get(2).getHeight() / 2 - 28)
					        .target(sW / 2 + sprites.get(3).getWidth() / 2 - 35, -300).ease(Quad.OUT)))
			    .end();
		}
		timeline = timeline
			.pushPause(0.5f);
			
		if(callback != null) {
			timeline.setCallback(callback);
			timeline.setCallbackTriggers(TweenCallback.END);
		}
		timeline.start(tweenManager);
	}
	
	public void draw(SpriteBatch batch) {
		batch.begin();
		for (Sprite sprite : sprites) {
			sprite.draw(batch);
		}
		levelFont.draw(batch, currentLevel, sprites.get(1).getX() + 100, sprites.get(1).getY() + 32);
		font.draw(batch, levelDescription, sprites.get(1).getX() - 16, sprites.get(1).getY() - 32, 175, -15, true);
		batch.end();
	}
	
	public boolean isOver(Sprite sprite, float x, float y) {
		return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
			&& sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
	}

}
