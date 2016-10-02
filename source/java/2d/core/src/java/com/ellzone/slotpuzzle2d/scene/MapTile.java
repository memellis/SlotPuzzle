package com.ellzone.slotpuzzle2d.scene;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Level;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;

import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.primitives.MutableFloat;

public class MapTile {

	public class MapTileLevel1 extends Level {
		@Override
		public void initialise() {
		}	

		@Override
		public String getImageName() {
			return "GamePopUp";
		}

		@Override
		public InputProcessor getInput() {
			return null;
		}

		@Override
		public String getTitle() {
			String title = "World 1 Level 1";
			return title;
		}

		@Override
		public void dispose() {			
		}
	}

	private float x, y;
	private Level level;
	private BitmapFont font;
	private TweenManager tweenManager;
	private Sprite sprite, interactiveIcon, veil;
	private MutableFloat textOpacity = new MutableFloat(1);

	public MapTile(float x, float y, float w, float h, Level level, TextureAtlas atlas, OrthographicCamera camera, BitmapFont font, TweenManager tweenManager, Sprite mapTileSprite) {
		this.x = x;
		this.y = y;
		this.level = level;
		this.font = font;
		this.tweenManager = tweenManager;
		
		if (level.getImageName().equalsIgnoreCase("MapTile")) {
			sprite = mapTileSprite;
		}
		else {
			this.sprite = level.getImageName() != null ? atlas.createSprite(level.getImageName()) : atlas.createSprite("tile");
		}
		this.veil = atlas.createSprite("white");

		sprite.setOrigin(0, 0);

		veil.setSize(w, h);
		veil.setOrigin(0, 0);
		veil.setPosition(x, y);
		veil.setColor(1, 1, 1, 0);
	}
	
	public void draw(SpriteBatch batch) {
		sprite.draw(batch);

		font.setColor(1, 1, 1, textOpacity.floatValue());
		
		font.draw(batch, level.getTitle(),
				sprite.getX() + sprite.getWidth()/20,
				sprite.getY() + sprite.getHeight()*19/20);
		veil.setPosition(0, 0);

		if (veil.getColor().a > 0.1f) veil.draw(batch);
	}
	
	public void enter(float delay) {
		Timeline.createSequence()
			.push(SlotPuzzleTween.to(sprite, SpriteAccessor.POS_XY, 0.7f).target(x, y).ease(Cubic.INOUT))
			.pushPause(0.1f)
			.delay(delay)
			.start(tweenManager);
	}

	public void maximize(TweenCallback callback) {
		tweenManager.killTarget(textOpacity);
		tweenManager.killTarget(sprite);

		float tx = 0;
		float ty = 0;
		float sx = Gdx.graphics.getWidth() / sprite.getWidth();
		float sy = Gdx.graphics.getHeight() / sprite.getHeight();
						
		Timeline.createSequence()
			.push(SlotPuzzleTween.set(veil, SpriteAccessor.POS_XY).target(tx, ty))
			.push(SlotPuzzleTween.set(veil, SpriteAccessor.SCALE_XY).target(sx, sy))
			.beginParallel()
				.push(SlotPuzzleTween.to(textOpacity, 0, 0.2f).target(0))
				.push(SlotPuzzleTween.to(interactiveIcon, SpriteAccessor.OPACITY, 0.2f).target(0))
			.end()
			.push(SlotPuzzleTween.to(sprite, SpriteAccessor.SCALE_XY, 0.3f).target(0.9f, 0.9f).ease(Quad.OUT))
			.beginParallel()
				.push(SlotPuzzleTween.to(sprite, SpriteAccessor.SCALE_XY, 0.5f).target(sx, sy).ease(Cubic.IN))
				.push(SlotPuzzleTween.to(sprite, SpriteAccessor.POS_XY, 0.5f).target(tx, ty).ease(Quad.IN))
			.end()
			.pushPause(-0.3f)
			.push(SlotPuzzleTween.to(veil, SpriteAccessor.OPACITY, 0.7f).target(1))
			.setUserData(this)
			.setCallback(callback)
			.start(tweenManager);
	}

	public void minimize(TweenCallback minimizeCallback) {
		tweenManager.killTarget(sprite);
		tweenManager.killTarget(textOpacity);

		Timeline.createSequence()
			.push(SlotPuzzleTween.set(veil, SpriteAccessor.OPACITY).target(0))
			.beginParallel()
				.push(SlotPuzzleTween.to(sprite, SpriteAccessor.SCALE_XY, 0.3f).target(1, 1).ease(Quad.OUT))
				.push(SlotPuzzleTween.to(sprite, SpriteAccessor.POS_XY, 0.5f).target(x, y).ease(Quad.OUT))
			.end()
			.beginParallel()
				.push(SlotPuzzleTween.to(textOpacity, 0, 0.3f).target(1))
				.push(SlotPuzzleTween.to(interactiveIcon, SpriteAccessor.OPACITY, 0.3f).target(1))
			.end()
			.setUserData(this)
			.setCallback(minimizeCallback)
			.start(tweenManager);
	}

	public boolean isOver(float x, float y) {
		return sprite.getX() <= x && x <= sprite.getX() + sprite.getWidth()
			&& sprite.getY() <= y && y <= sprite.getY() + sprite.getHeight();
	}

	public Level getLevel() {
		return level;
	}
}
