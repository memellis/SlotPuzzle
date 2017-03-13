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

package com.ellzone.slotpuzzle2d.desktop.play.tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.desktop.play.PlayPrototype;
import com.ellzone.slotpuzzle2d.level.LevelPopUp;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.utils.Assets;

public class LevelOverPopUpUsingLevelPopUp extends PlayPrototype {

	public static final String LEVEL_LOST_DESC =  "Sorry you lost that level. Touch/Press to restart the level.";
	public static final String LEVEL_WON_DESC =  "Well done you've won that level. Touch/Press to start the nextlevel.";	
	public static final String CURRENT_LEVEL = "1-1";
	public enum PlayStates {
		LEVEL_LOST, LEVEL_WON
	}
	private PlayStates playState;
	private LevelPopUp levelLostPopUp, levelWonPopUp;
	private Array<Sprite> levelLostSprites, levelWonSprites;
	private BitmapFont currentLevelFont;
	private int sW, sH;
	
	@Override
	protected void initialiseOverride() {
		currentLevelFont = new BitmapFont();
		currentLevelFont.getData().scale(1.5f);
	    levelLostPopUp = new LevelPopUp(batch, tweenManager, levelLostSprites, currentLevelFont, CURRENT_LEVEL, LEVEL_LOST_DESC);
	    levelWonPopUp = new LevelPopUp(batch, tweenManager, levelWonSprites, currentLevelFont, CURRENT_LEVEL, LEVEL_WON_DESC);
		Gdx.input.setInputProcessor(inputProcessor);
		playState = PlayStates.LEVEL_LOST;
	    levelLostPopUp.showLevelPopUp(null);
	}

	@Override
	protected void loadAssetsOverride() {
        Assets.inst().load("tiles/tiles.pack.atlas", TextureAtlas.class);
	    Assets.inst().update();
	    Assets.inst().finishLoading();
	    TextureAtlas tilesAtlas = Assets.inst().get("tiles/tiles.pack.atlas", TextureAtlas.class);

		sW = Gdx.graphics.getWidth();
		sH = Gdx.graphics.getHeight();

	    levelLostSprites = new Array<Sprite>();
	    levelLostSprites.add(tilesAtlas.createSprite("GamePopUp")); 
	    levelLostSprites.add(tilesAtlas.createSprite("level"));
	    levelLostSprites.add(tilesAtlas.createSprite("level"));
	    levelLostSprites.add(tilesAtlas.createSprite("over"));    
	    setLevelLostSpritePositions();
	    
	    levelWonSprites = new Array<Sprite>();
	    levelWonSprites.add(tilesAtlas.createSprite("GamePopUp")); 
	    levelWonSprites.add(tilesAtlas.createSprite("level"));
	    levelWonSprites.add(tilesAtlas.createSprite("level"));
	    levelWonSprites.add(tilesAtlas.createSprite("complete"));	        
	    setLevelWonSpritePositions();
	}

	private void setLevelLostSpritePositions() {
	    levelLostSprites.get(0).setPosition(sW / 2 - levelLostSprites.get(0).getWidth() / 2, sH / 2 - levelLostSprites.get(0).getHeight() /2);
	    levelLostSprites.get(1).setPosition(-200, sH / 2 - levelLostSprites.get(1).getHeight() / 2);
	    levelLostSprites.get(2).setPosition(-200, sH / 2 - levelLostSprites.get(2).getHeight() / 2 + 40);
	    levelLostSprites.get(3).setPosition(200 + sW, sH / 2 - levelLostSprites.get(3).getHeight() / 2 + 40);
	}
	
	private void setLevelWonSpritePositions() {
	    levelWonSprites.get(0).setPosition(sW / 2 - levelWonSprites.get(0).getWidth() / 2, sH / 2 - levelWonSprites.get(0).getHeight() /2);
	    levelWonSprites.get(1).setPosition(-200, sH / 2 - levelWonSprites.get(1).getHeight() / 2);
	    levelWonSprites.get(2).setPosition(-200, sH / 2 - levelWonSprites.get(2).getHeight() / 2 + 40);
	    levelWonSprites.get(3).setPosition(200 + sW, sH / 2 - levelWonSprites.get(3).getHeight() / 2 + 40);
	}
	
	@Override
	protected void disposeOverride() {
		Assets.inst().dispose();
	}

	@Override
	protected void updateOverride(float dt) {
	}

	@Override
	protected void renderOverride(float dt) {
		switch (playState) {
		    case LEVEL_LOST:
		    	levelLostPopUp.draw(batch);
				break;
		    case LEVEL_WON:
				levelWonPopUp.draw(batch);
				break;
		}
	}

	@Override
	protected void initialiseUniversalTweenEngineOverride() {
	}
	
	private TweenCallback switchLevelCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			switch (type) {
			    case TweenCallback.END:
			    	switch (playState) { 
			    	    case LEVEL_LOST:
			    	    	playState = PlayStates.LEVEL_WON;
			    	    	setLevelWonSpritePositions();
			    	    	levelWonPopUp.showLevelPopUp(null);
			    	    	break;
			    	    case LEVEL_WON:
			    	    	setLevelLostSpritePositions();
			    	    	playState = PlayStates.LEVEL_LOST;
			    	    	levelLostPopUp.showLevelPopUp(null);
			    	    	break;
			    	}
			}
		}
	};
	
	private final InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			tweenManager.killAll();
		    switch (playState) {
			case LEVEL_LOST:
				if (levelLostPopUp.isOver(levelLostSprites.get(0), x, y)) {
				    levelLostPopUp.hideLevelPopUp(switchLevelCallback);
				}
			    break;
			case LEVEL_WON:
				if (levelWonPopUp.isOver(levelWonSprites.get(0), x, y)) {
					levelWonPopUp.hideLevelPopUp(switchLevelCallback);
				}
				break;
			}
		    return true;
		}
	};
}