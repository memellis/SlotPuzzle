package com.ellzone.slotpuzzle2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.ellzone.effects.Particle;
import com.ellzone.effects.ParticleAccessor;
import com.ellzone.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.utils.Assets;

import aurelienribon.tweenengine.Timeline;
import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.TweenManager;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Quart;

public class PlayScreen implements Screen {
	private static final int PX_PER_METER = 600;
	private SlotPuzzle game;
	private final OrthographicCamera camera = new OrthographicCamera();
	private Viewport viewport;
	private Stage stage;
	private Sprite cheesecake;
	private Sprite cherry;
	private Sprite grapes;
	private Sprite jelly;
	private Sprite lemon;
	private Sprite peach;
	private Sprite pear;
	private Sprite tomato;
 	private final TweenManager tweenManager = new TweenManager();
	private boolean isLoaded = false;
	
	public PlayScreen(SlotPuzzle game) {
		this.game = game;
		createPlayScreen();		
	}
	
	private void createPlayScreen() {
		Tween.setWaypointsLimit(10);
		Tween.setCombinedAttributesLimit(3);
		Tween.registerAccessor(Sprite.class, new SpriteAccessor());

		viewport = new FitViewport(800, 480, new OrthographicCamera());
        stage = new Stage(viewport, game.batch);
        
		Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
		Assets.inst().update();
		Assets.inst().finishLoading();
		isLoaded = true;

		TextureAtlas atlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
		cherry = atlas.createSprite("cherry");
		cheesecake = atlas.createSprite("cheesecake");
		grapes = atlas.createSprite("grapes");
		jelly = atlas.createSprite("jelly");
		lemon = atlas.createSprite("lemon");
		peach = atlas.createSprite("peach");
		pear = atlas.createSprite("pear");
		tomato = atlas.createSprite("tomato");

		float wpw = 1f;
		float wph = wpw * Gdx.graphics.getHeight() / Gdx.graphics.getWidth();

		camera.viewportWidth = wpw;
		camera.viewportHeight = wph;
		camera.update();

		Sprite[] sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
		for (Sprite sp : sprites) {
			sp.setSize(sp.getWidth()/PX_PER_METER, sp.getHeight()/PX_PER_METER);
			sp.setOrigin(sp.getWidth()/2, sp.getHeight()/2);
		}

		cherry.setPosition(-0.325f, 0.028f);
		cheesecake.setPosition(-0.320f, -0.066f);
		grapes.setPosition(0.020f, -0.087f);
		jelly.setPosition(0.238f, 0.022f);
		lemon.setPosition(-0.278f, -0.025f);
		peach.setPosition(0.068f, -0.077f);
		pear.setPosition(0.028f, -0.077f);
		tomato.setPosition(0.168f, -0.077f);
		
		Timeline.createSequence()
			.push(Tween.set(cherry, SpriteAccessor.POS_XY).targetRelative(-1, 0))
			.push(Tween.set(cheesecake, SpriteAccessor.POS_XY).targetRelative(1, 0))
			.push(Tween.set(grapes, SpriteAccessor.POS_XY).targetRelative(1, 0))
			.push(Tween.set(lemon, SpriteAccessor.POS_XY).targetRelative(1, 0))
			.push(Tween.set(peach, SpriteAccessor.POS_XY).targetRelative(1, 0))
			.push(Tween.set(pear, SpriteAccessor.POS_XY).targetRelative(1, 0))
			.push(Tween.set(tomato, SpriteAccessor.POS_XY).targetRelative(1, 0))
			.pushPause(0.5f)
			.push(Tween.to(cherry, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Quart.OUT))
			.push(Tween.to(cheesecake, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
			.push(Tween.to(grapes, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
			.push(Tween.to(lemon, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
			.push(Tween.to(peach, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
			.push(Tween.to(pear, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
			.push(Tween.to(tomato, SpriteAccessor.POS_XY, 0.5f).targetRelative(-1, 0).ease(Quart.OUT))
			.pushPause(0.3f)
			.beginParallel()
				.push(Tween.to(cherry, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(cheesecake, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(grapes, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(lemon, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(peach, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(pear, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
				.push(Tween.to(tomato, SpriteAccessor.POS_XY, 0.5f).targetRelative(1, 0).ease(Back.IN))
			.end()
			.pushPause(-0.3f)
			.start(tweenManager);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);

        Table table = new Table();
        table.center();
        table.setFillParent(true);

        Label gameOverLabel = new Label("PLAY SCREEN", font);
        Label playAgainLabel = new Label("Click to Play Again", font);

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(playAgainLabel).expandX().padTop(10f);

        stage.addActor(table);		
   	}

	@Override
	public void show() {
		// TODO Auto-generated method stub	
	}
	
	public void handleInput(float dt) {
		//if (Gdx.input.isKeyJustPressed(key))
	}
	
	private void update(float delta) {
		tweenManager.update(delta);
	}

	@Override
	public void render(float delta) {
		update(delta);
		if(Gdx.input.justTouched()) {
            game.setScreen(new EndOfGameScreen(game));
            dispose();
        }
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		if(isLoaded) {
			game.batch.setProjectionMatrix(camera.combined);
			game.batch.begin();
			cherry.draw(game.batch);
			grapes.draw(game.batch);
			lemon.draw(game.batch);
			peach.draw(game.batch);
			pear.draw(game.batch);
			tomato.draw(game.batch);
		game.batch.end();
		} else {
			if (Assets.inst().getProgress() < 1) {
				Assets.inst().update();
			} else {
				isLoaded = true;
			}
		}

        stage.draw();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width,  height);		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		stage.dispose();
		
	}

}
