package com.ellzone.slotpuzzle2d.desktop.play.tween;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.ellzone.slotpuzzle2d.desktop.play.Prototype;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.utils.Assets;

import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quad;

public class LevelPopUp extends Prototype {

	public static final String POPUP_DESC =  "Reveal the hidden pattern to complete the level.";
	private Sprite complete, levelPopUp, level, over;
	private BitmapFont currentLevelFont;

	@Override
	protected void initialiseOverride() {	
		createLevelPopUp();
		currentLevelFont = new BitmapFont();
		currentLevelFont.getData().scale(1.5f);
		Gdx.input.setInputProcessor(inputProcessor);
		
	}
	
	@Override
	protected void loadAssetsOverride() {
        Assets.inst().load("tiles/tiles.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();
        TextureAtlas tilesAtlas = Assets.inst().get("tiles/tiles.pack.atlas", TextureAtlas.class);

        levelPopUp = tilesAtlas.createSprite("GamePopUp"); 
        level = tilesAtlas.createSprite("level");
        over = tilesAtlas.createSprite("over");
        complete = tilesAtlas.createSprite("complete");
        
        levelPopUp.setPosition(Gdx.graphics.getWidth() / 2 - levelPopUp.getWidth() / 2, Gdx.graphics.getHeight() / 2 - levelPopUp.getHeight() /2);
        level.setPosition(-200, Gdx.graphics.getHeight() / 2 - level.getHeight() /2);
        over.setPosition(200 + Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 - over.getHeight() /2);
        complete.setPosition(200 + Gdx.graphics.getWidth(), Gdx.graphics.getHeight() / 2 - complete.getHeight() /2);
	}
	
	private void createLevelPopUp() {
		Timeline.createSequence()
		.push(SlotPuzzleTween.set(levelPopUp, SpriteAccessor.SCALE_XY).target(0.1f, 0))
		.push(SlotPuzzleTween.set(level, SpriteAccessor.POS_XY). target(-200, Gdx.graphics.getHeight() / 2 - level.getHeight() /2))
		.pushPause(0.5f)
		.push(SlotPuzzleTween.to(levelPopUp, SpriteAccessor.SCALE_XY, 0.8f).target(1, 0.6f).ease(Back.OUT))
		.pushPause(-0.3f)
		.beginParallel()
		    .push(SlotPuzzleTween.to(levelPopUp, SpriteAccessor.SCALE_XY, 0.5f).target(1.1f, 1.1f).ease(Back.IN))
		    .push(SlotPuzzleTween.to(levelPopUp, SpriteAccessor.OPACITY, 0.5f).target(0.7f).ease(Back.IN))
		.end()
		.pushPause(0.3f)
		.beginParallel()
		    .push(SlotPuzzleTween.to(level, SpriteAccessor.POS_XY, 1.0f).target(Gdx.graphics.getWidth() / 2 - level.getWidth() / 2 - 20, Gdx.graphics.getHeight() / 2 - level.getHeight() /2 + 50).ease(Back.INOUT))
		.end()    
		.pushPause(0.5f)
		.start(tweenManager);
	}

	private void hideLevelPopUp() {
		Timeline.createSequence()
		    .pushPause(0.25f)
			.beginParallel()
			    .push((SlotPuzzleTween.to(levelPopUp, SpriteAccessor.POS_XY, 1.5f)
			    	.waypoint(Gdx.graphics.getWidth() / 2 - levelPopUp.getWidth() / 2, Gdx.graphics.getHeight() / 2 - levelPopUp.getHeight() / 2 + 64)
			    	.target(Gdx.graphics.getWidth() / 2 - levelPopUp.getWidth() / 2, -300).ease(Quad.OUT)))
			    .push((SlotPuzzleTween.to(level, SpriteAccessor.POS_XY, 1.5f)
			        .waypoint(Gdx.graphics.getWidth() / 2 - level.getWidth() / 2 - 20, Gdx.graphics.getHeight() / 2 - level.getHeight() / 2 + 64)
			        .target(Gdx.graphics.getWidth() / 2 - level.getWidth() / 2 - 20, -300).ease(Quad.OUT)))
			.end()    
			.pushPause(0.5f)    
			.start(tweenManager);
	}

	@Override
	protected void disposeOverride() {
	}

	@Override
	protected void updateOverride() {
	}

	@Override
	protected void renderOverride() {
		batch.begin();
		levelPopUp.draw(batch);
		level.draw(batch);
		currentLevelFont.draw(batch, "1-1", level.getX() + 100, level.getY() + 32);
		over.draw(batch);
		complete.draw(batch);
	    font.draw(batch, POPUP_DESC, level.getX() - 16, level.getY() - 32, 175, -15, true); 
		batch.end();
	}
	
	private final InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			tweenManager.killAll();
			hideLevelPopUp();
			return true;
		}
	};
}
