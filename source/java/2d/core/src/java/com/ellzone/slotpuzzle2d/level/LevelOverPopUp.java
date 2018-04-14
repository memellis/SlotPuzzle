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

package com.ellzone.slotpuzzle2d.level;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quad;

public class LevelOverPopUp {
	
	private TweenManager tweenManager;
	private Array<Sprite> sprites;
	private BitmapFont levelFont, font;
	private String currentLevel;
	private String levelDescription;
	
	public LevelOverPopUp(SpriteBatch batch, TweenManager tweenManager, Array<Sprite> sprites, BitmapFont levelFont, String currentLevel, String levelDescription) {
		this.tweenManager = tweenManager;
		this.sprites = sprites;
		this.levelFont = levelFont;
		this.currentLevel = currentLevel;
		this.levelDescription = levelDescription;
		font = new BitmapFont();
	}

	public void showLevelPopUp(TweenCallback callback) {
		Timeline timeline = Timeline.createSequence()
		    .push(SlotPuzzleTween.set(sprites.get(0), SpriteAccessor.SCALE_XY).target(0.1f, 0))
		    .push(SlotPuzzleTween.set(sprites.get(1), SpriteAccessor.POS_XY). target(-200, Gdx.graphics.getHeight() / 2 - sprites.get(1).getHeight() /2))
		    .pushPause(0.5f)
		    .push(SlotPuzzleTween.to(sprites.get(0), SpriteAccessor.SCALE_XY, 0.8f).target(1, 0.6f).ease(Back.OUT))
		    .pushPause(-0.3f)
		    .beginParallel()
		        .push(SlotPuzzleTween.to(sprites.get(0), SpriteAccessor.SCALE_XY, 0.5f).target(1.1f, 1.1f).ease(Back.IN))
		        .push(SlotPuzzleTween.to(sprites.get(0), SpriteAccessor.OPACITY, 0.5f).target(0.7f).ease(Back.IN))
		    .end()
		    .pushPause(0.3f)
		    .beginParallel()
		         .push(SlotPuzzleTween.to(sprites.get(1), SpriteAccessor.POS_XY, 1.0f).target(Gdx.graphics.getWidth() / 2 - sprites.get(1).getWidth() / 2 - 20, Gdx.graphics.getHeight() / 2 - sprites.get(1).getHeight() /2 + 50).ease(Back.INOUT))
		    .end()    
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
			    	.waypoint(Gdx.graphics.getWidth() / 2 - sprites.get(0).getWidth() / 2, Gdx.graphics.getHeight() / 2 - sprites.get(0).getHeight() / 2 + 64)
			    	.target(Gdx.graphics.getWidth() / 2 - sprites.get(0).getWidth() / 2, -300).ease(Quad.OUT)))
			    .push((SlotPuzzleTween.to(sprites.get(1), SpriteAccessor.POS_XY, 1.5f)
			        .waypoint(Gdx.graphics.getWidth() / 2 - sprites.get(1).getWidth() / 2 - 20, Gdx.graphics.getHeight() / 2 - sprites.get(1).getHeight() / 2 + 64)
			        .target(Gdx.graphics.getWidth() / 2 - sprites.get(1).getWidth() / 2 - 20, -300).ease(Quad.OUT)))
			.end()    
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
}
