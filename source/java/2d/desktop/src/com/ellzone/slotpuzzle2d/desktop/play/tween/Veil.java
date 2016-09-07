package com.ellzone.slotpuzzle2d.desktop.play.tween;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

import aurelienribon.tweenengine.Tween;
import aurelienribon.tweenengine.equations.Back;
import aurelienribon.tweenengine.equations.Cubic;
import aurelienribon.tweenengine.equations.Quad;
import aurelienribon.tweenengine.equations.Quart;

public class Veil implements ApplicationListener {
	private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
	private PerspectiveCamera cam;
    private SpriteBatch batch;
	private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato, strip, veil, gdx;
    private Sprite[] sprites;
    private int spriteWidth;
    private int spriteHeight;
    private Pixmap slotReelScrollPixmap;
	private Texture slotReelScrollTexture;
	private Random random;
    private Array<ReelTile> reels;
    private Timeline introSequence;
    private TweenManager tweenManager; 
    

	@Override
	public void create() {
		loadAssets();
        initialiseCamera();
        initialiseLibGdx();
        initialiseUniversalTweenEngine();
        createSequence();
	}

	private void loadAssets() {
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().load("splash/pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();

        TextureAtlas spriteAtlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = spriteAtlas.createSprite("cherry");
        cheesecake = spriteAtlas.createSprite("cheesecake");
        grapes = spriteAtlas.createSprite("grapes");
        jelly = spriteAtlas.createSprite("jelly");
        lemon = spriteAtlas.createSprite("lemon");
        peach = spriteAtlas.createSprite("peach");
        pear = spriteAtlas.createSprite("pear");
        tomato = spriteAtlas.createSprite("tomato");

        TextureAtlas splashAtlas = Assets.inst().get("splash/pack.atlas", TextureAtlas.class);
        strip = splashAtlas.createSprite("white");
        veil = splashAtlas.createSprite("white");
        
        strip.setOrigin(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        strip.setPosition(0, 0);
        strip.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());        
        
        veil.setOrigin(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        veil.setPosition(0, 0);
        veil.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		veil.setColor(1, 1, 1, 0);		

        int i = 0;
        sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
            sprite.setPosition(192 + i * sprite.getWidth(), Gdx.graphics.getHeight() / 2 - sprite.getHeight() / 2);
            i++;
        }
        spriteWidth = (int) sprites[0].getWidth();
        spriteHeight = (int) sprites[0].getHeight();
	}
	
	private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
        cam.update();
	}
	
	private void initialiseLibGdx() {
		batch = new SpriteBatch();
		Gdx.input.setInputProcessor(inputProcessor);
 	}
	
	private void initialiseUniversalTweenEngine() {
	    SlotPuzzleTween.setWaypointsLimit(10);
	    SlotPuzzleTween.setCombinedAttributesLimit(3);
	    SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
	    tweenManager = new TweenManager();
	}

	private void createSequence() {
		Timeline.createSequence()
		.push(SlotPuzzleTween.set(strip, SpriteAccessor.SCALE_XY).target(1, 0))
		.pushPause(0.5f)
		.push(SlotPuzzleTween.to(strip, SpriteAccessor.SCALE_XY, 0.8f).target(1, 0.6f).ease(Back.OUT))
		.pushPause(-0.3f)
		.pushPause(0.3f)
		.pushPause(0.3f)
		.push(SlotPuzzleTween.to(strip, SpriteAccessor.SCALE_XY, 0.5f).target(1, 1).ease(Back.IN))
		.pushPause(0.3f)
		.pushPause(-0.3f)
		.pushPause(0.5f)
		.start(tweenManager);
	}

		
	@Override
	public void resize(int width, int height) {
		float halfHeight = MINIMUM_VIEWPORT_SIZE * 0.5f;
        if (height > width)
            halfHeight *= (float)height / (float)width;
        float halfFovRadians = MathUtils.degreesToRadians * cam.fieldOfView * 0.5f;
        float distance = halfHeight / (float)Math.tan(halfFovRadians);
        cam.viewportWidth = width;
        cam.viewportHeight = height;
        cam.position.set(0, 0, distance);
        cam.lookAt(0, 0, 0);
        cam.update();
	}
	
	private void update(float delta) {
		tweenManager.update(delta);
	}

	@Override
	public void render() {
		final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        update(delta);
        batch.begin();        
        strip.draw(batch);
        for (Sprite sprite : sprites) {
        	sprite.draw(batch);
        }
        if (veil.getColor().a > 0.1f) {
        	veil.draw(batch);
        }
        batch.end();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		batch.dispose();
		Assets.inst().dispose();
	}
	
	private final InputProcessor inputProcessor = new InputAdapter() {
		@Override
		public boolean touchUp(int x, int y, int pointer, int button) {
			SlotPuzzleTween.to(veil, SpriteAccessor.OPACITY, 0.7f)
				.target(1)
				.start(tweenManager);
			return true;
		}
	};

}