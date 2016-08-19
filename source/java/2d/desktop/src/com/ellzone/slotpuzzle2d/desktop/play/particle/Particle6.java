package com.ellzone.slotpuzzle2d.desktop.play.particle;

import java.util.Random;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.effects.ReelAccessor;
import com.ellzone.slotpuzzle2d.physics.DampenedSineParticle;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle2d.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle2d.sprites.ReelTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.Timeline;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;
import aurelienribon.tweenengine.equations.Elastic;

public class Particle6 implements ApplicationListener {

	private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
	private PerspectiveCamera cam;
    private Sprite cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato;
    private Sprite[] sprites;
    private int spriteWidth;
    private int spriteHeight;
    private Pixmap slotReelScrollPixmap;
	private Texture slotReelScrollTexture;
	private int slotReelScrollheight;
	private Random random;
    private Array<ReelTile> reelTiles;
    private SpriteBatch batch;
    private Array<DampenedSineParticle> dampenedSines;
	private DampenedSineParticle dampenedSine;
    private ShapeRenderer shapeRenderer;
    private Array<Vector2> points = new Array<Vector2>();
    private float graphStep;
    private Vector2 touch;
    private Timeline endReelSeq;
    private TweenManager tweenManager;

	@Override
	public void create() {
        loadAssets();
        initialiseReelSlots();
        initialiseCamera();
        initialiseLibGdx();
        initialiseUniversalTweenEngine();
        initialiseDampenedSine();
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
        reelTiles.add(reel);
	}	

	private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
	}
	
	private void initialiseLibGdx() {
		batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        touch = new Vector2();
	}
	
	private void initialiseUniversalTweenEngine() {
	    SlotPuzzleTween.setWaypointsLimit(10);
	    SlotPuzzleTween.setCombinedAttributesLimit(3);
	    SlotPuzzleTween.registerAccessor(ReelTile.class, new ReelAccessor());
	    tweenManager = new TweenManager();
	}

	private void initialiseDampenedSine() {
		dampenedSines = new Array<DampenedSineParticle>();
		for (ReelTile reel : reelTiles) { 
			dampenedSine = new DampenedSineParticle(0, reel.getSy(), 0, 0, 0, slotReelScrollheight * 20, slotReelScrollheight, reel.getEndReel());
			dampenedSine.setCallback(dsCallback);
			dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE);
			dampenedSine.setUserData(reel);
			dampenedSines.add(dampenedSine);
		}
		graphStep=0f;
	}
	
	private SPPhysicsCallback dsCallback = new SPPhysicsCallback() {
		@Override
		public void onEvent(int type, SPPhysicsEvent source) {
			delegateDSCallback(type, source); 
		}
	};
	
	private void delegateDSCallback(int type, SPPhysicsEvent source) {
		if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
			DampenedSineParticle ds = (DampenedSineParticle)source.getSource();
			ReelTile reel = (ReelTile)ds.getUserData();
			endReelSeq = Timeline.createSequence();
			float remainingSy = slotReelScrollheight - (reel.getSy() % slotReelScrollheight);
			float endSy = reel.getSy() + remainingSy + reel.getEndReel() * spriteHeight;
	        endReelSeq = endReelSeq.push(SlotPuzzleTween.to(reel, ReelAccessor.SCROLL_XY, 5.0f)
	        		               .target(0f, endSy)
	        		               .ease(Elastic.OUT)
	        		               .setCallbackTriggers(TweenCallback.STEP + TweenCallback.END)
	        		               .setCallback(slowingSpinningCallback)
	        		               .setUserData(reel));	        					
	        endReelSeq = endReelSeq
	        				.start(tweenManager);
		}
	}
	
	private TweenCallback slowingSpinningCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			delegateSlowingSpinning(type, source);
		}
	};
	
	private void delegateSlowingSpinning(int type, BaseTween<?> source) {
		ReelTile reel = (ReelTile)source.getUserData();
		if (type == TweenCallback.END) {
			reel.setSpinning(false);
		} else if (type == TweenCallback.STEP) {
		    addGraphPoint(new Vector2(graphStep++ % Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() / 2 + (reel.getSy() % slotReelScrollheight))));
		}
	}

    private void addGraphPoint(Vector2 newPoint) {
    	points.add(newPoint);
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
		int dsIndex = 0;
		for (ReelTile reel : reelTiles) { 		  
			dampenedSines.get(dsIndex).update();
         	if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
         		reel.setSy(dampenedSines.get(dsIndex).position.y);
  	       	}
         	reel.update(delta);
  	       	dsIndex++;
		}
	}

	public void handleInput(float delta) {
		int dsIndex = 0;
		for (ReelTile reel : reelTiles) { 		  		
			if (Gdx.input.justTouched()) {
				touch = touch.set(Gdx.input.getX(), cam.viewportHeight - Gdx.input.getY());
				if(reel.getBoundingRectangle().contains(touch)) {
					if (reel.isSpinning()) {
						if (dampenedSines.get(dsIndex).getDSState() == DampenedSineParticle.DSState.UPDATING_DAMPENED_SINE) {
							reel.setEndReel(reel.getCurrentReel());
							dampenedSines.get(dsIndex).setEndReel(reel.getCurrentReel());
						}
					} else {
						reel.setEndReel(random.nextInt(sprites.length - 1));
				        reel.setSpinning(true);
						dampenedSines.get(dsIndex).initialiseDampenedSine();
						dampenedSines.get(dsIndex).position.y = 0;
					}
				}
			}
			dsIndex++;
		}
	}

	@Override
	public void render() {
		final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        update(delta);
        handleInput(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

        batch.begin();
        for (ReelTile reelSlot : reelTiles) {
            reelSlot.draw(batch);
            sprites[reelSlot.getEndReel()].setX(32);
            sprites[reelSlot.getEndReel()].draw(batch);
        }
        batch.end();
        drawGraphPoint(shapeRenderer);
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

	private void drawGraphPoint(ShapeRenderer shapeRenderer) {
        if (points.size >= 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(Color.YELLOW);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }    	
    }
}