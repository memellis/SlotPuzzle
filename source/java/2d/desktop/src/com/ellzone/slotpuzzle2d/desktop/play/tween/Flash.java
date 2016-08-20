package com.ellzone.slotpuzzle2d.desktop.play.tween;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class Flash implements ApplicationListener {
	private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
	private PerspectiveCamera cam;
    private SpriteBatch batch;
	private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite[] sprites;
    private int spriteWidth;
    private int spriteHeight;
    private Pixmap slotReelScrollPixmap;
	private Texture slotReelScrollTexture;
	private int slotReelScrollheight;
	private Random random;
    private Array<ReelTile> reelTiles;
    private Timeline flashSeq;
    private TweenManager tweenManager;


	@Override
	public void create() {
        loadAssets();
        initialiseCamera();
        initialiseLibGdx();
        initialiseUniversalTweenEngine();
        initialiseReelSlots();
 	}

	private void loadAssets() {
        Assets.inst().load("reel/reels.pack.atlas", TextureAtlas.class);
        Assets.inst().update();
        Assets.inst().finishLoading();

        TextureAtlas atlas = Assets.inst().get("reel/reels.pack.atlas", TextureAtlas.class);
        cherry = atlas.createSprite("cherry");
        cheesecake = atlas.createSprite("cheesecake");
        grapes = atlas.createSprite("grapes");
        jelly = atlas.createSprite("jelly");
        lemon = atlas.createSprite("lemon");
        peach = atlas.createSprite("peach");
        pear = atlas.createSprite("pear");
        tomato = atlas.createSprite("tomato");
        
        sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
        }
        spriteWidth = (int) sprites[0].getWidth();
        spriteHeight = (int) sprites[0].getHeight();
	}

	private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
	}
	
	private void initialiseLibGdx() {
		batch = new SpriteBatch();
 	}
	
	private void initialiseUniversalTweenEngine() {
	    SlotPuzzleTween.setWaypointsLimit(10);
	    SlotPuzzleTween.setCombinedAttributesLimit(3);
	    SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
	    tweenManager = new TweenManager();
	}

	private void initialiseReelSlots() {
        random = new Random();
        reelTiles = new Array<ReelTile>();
        slotReelScrollPixmap = new Pixmap(spriteWidth, spriteHeight, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        slotReelScrollheight = slotReelScrollTexture.getHeight();
        ReelTile reel = new ReelTile(slotReelScrollTexture, spriteWidth, spriteHeight, 0, 32, 0);
        reel.setX(0);
        reel.setY(0);
        reel.setSx(0);
        reel.setSy(0);
        reel.setEndReel(random.nextInt(sprites.length - 1));
        initialiseReelFlash(reel);
        reelTiles.add(reel);
	}	
	
	private void initialiseReelFlash(ReelTile reel) {
		reel.setFlashTween(true);
		flashSeq = Timeline.createSequence();
		flashSeq = flashSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.FLASH_TINT, 0.05f)
						   .target(0.3f, reel.getFlashColor().g, reel.getFlashColor().b)
				           .repeatYoyo(33, 0))
						   .setCallback(reelFlashCallback)
						   .setCallbackTriggers(TweenCallback.END)
						   .setUserData(reel)
						   .start(tweenManager);
				           
	}
	
	private TweenCallback reelFlashCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			delegateReelFlashCallback(type, source);
		}
	};
	
	private void delegateReelFlashCallback(int type, BaseTween<?> source) {
		ReelTile reel = (ReelTile)source.getUserData();
		reel.setFlashOff();
		reel.setFlashTween(false);
		reel.setSpinning();
	}
	
	private void update(float delta) {
		tweenManager.update(delta);
		for (ReelTile reel : reelTiles) { 		  
         	reel.update(delta);
		}
	}
	
	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void render() {
		final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        batch.begin();
        for (ReelTile reel : reelTiles) {
            reel.draw(batch);
            sprites[reel.getEndReel()].setX(32);
            sprites[reel.getEndReel()].draw(batch);
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
	}
}
