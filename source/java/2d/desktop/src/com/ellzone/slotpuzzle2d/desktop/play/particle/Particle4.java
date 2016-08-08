package com.ellzone.slotpuzzle2d.desktop.play.particle;

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
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle.physics.DampenedSine;
import com.ellzone.slotpuzzle.physics.Particle;
import com.ellzone.slotpuzzle.physics.SPPhysicsCallback;
import com.ellzone.slotpuzzle.physics.SPPhysicsEvent;
import com.ellzone.slotpuzzle.physics.Vector;
import com.ellzone.slotpuzzle2d.screens.TweenGraphsScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileScroll;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class Particle4 implements ApplicationListener {

	private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
	private PerspectiveCamera cam;
    private Sprite cheesecake;
    private Sprite cherry;
    private Sprite grapes;
    private Sprite jelly;
    private Sprite lemon;
    private Sprite peach;
    private Sprite pear;
    private Sprite tomato;
    private Sprite[] sprites;
    private ReelSlotTileScroll reelSlot;
	private boolean isLoaded;
    private Pixmap slotReelScrollPixmap;
	private Texture slotReelScrollTexture;
	private int slotReelScrollheight;
	private Random random;
    private Array<ReelSlotTileScroll> reelSlots;
    private SpriteBatch batch;
    private Array<DampenedSine> dampenedSines;
	private DampenedSine dampenedSine;
	private Vector accelerator;
	private int dampPoint;
    private ShapeRenderer shapeRenderer;
    private Array<Vector2> points = new Array<Vector2>();
    private float graphStep;
    private float savedAmplitude;
    private float savedSy;
    private boolean saveAmplitude;
    private float plotTime;
 
	@Override
	public void create() {
        loadAssets();
        initialiseReelSlots();
        intialiseParticles();
        initialiseCamera();
        initialiseLibGdx();
        initialiseDampenedSine();
	}
		
	private void loadAssets() {
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
        
        sprites = new Sprite[] {cherry, cheesecake, grapes, jelly, lemon, peach, pear, tomato};
        for (Sprite sprite : sprites) {
            sprite.setOrigin(0, 0);
        }
	}
	
	private void initialiseReelSlots() {
        random = new Random();
        reelSlots = new Array<ReelSlotTileScroll>();
        slotReelScrollPixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        slotReelScrollPixmap = PixmapProcessors.createPixmapToAnimate(sprites);
        slotReelScrollTexture = new Texture(slotReelScrollPixmap);
        slotReelScrollheight = slotReelScrollTexture.getHeight();
        reelSlot = new ReelSlotTileScroll(slotReelScrollTexture, slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), 0, 32, 0, TweenGraphsScreen.SIXTY_FPS);
        reelSlot.setX(0);
        reelSlot.setY(0);
        reelSlot.setSx(0);
        reelSlot.setSy(0);
        reelSlot.setEndReel(random.nextInt(sprites.length - 1));
        reelSlots.add(reelSlot);
	}	
	
	private void intialiseParticles() {
		dampenedSines = new Array<DampenedSine>();
		dampenedSine = new DampenedSine(0, reelSlots.get(0).getSy(), 0, 0, 0, slotReelScrollTexture.getHeight() * 20, slotReelScrollTexture.getHeight(), reelSlots.get(0).getEndReel());
		dampenedSine.setCallback(new SPPhysicsCallback() {
			public void onEvent(int type, SPPhysicsEvent event) {
				delegateDSCallback(type);
			};
		});
		dampenedSine.setCallbackTriggers(SPPhysicsCallback.PARTICLE_UPDATE + SPPhysicsCallback.END);
		dampenedSines.add(dampenedSine);
	}
	
	private void delegateDSCallback(int type) {
		if (type == SPPhysicsCallback.PARTICLE_UPDATE) {
		    addGraphPoint(new Vector2(graphStep++ % Gdx.graphics.getWidth(), (Gdx.graphics.getHeight() / 2 + dampenedSines.get(0).dsEndReel)));
		} else {
			if (type == SPPhysicsCallback.END) {
				reelSlots.get(0).setEndReel(random.nextInt(sprites.length - 1));
				dampenedSines.get(0).initialiseDampenedSine();
				dampenedSines.get(0).position.y = 0;				
				dampenedSines.get(0).setEndReel(reelSlots.get(0).getEndReel());
			}
		}
		
	}
	
	private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
	}
	
	private void initialiseLibGdx() {
		batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
	}

	private void initialiseDampenedSine() {
        graphStep = 0;
        savedAmplitude = 0;
        saveAmplitude = true;
        plotTime = 132;
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
        for(ReelSlotTileScroll reelSlot : reelSlots) { 		  
         	dampenedSines.get(0).update();
  		    reelSlot.setSy(dampenedSines.get(0).position.y);
  	       	reelSlot.update(delta); 
        }
    }
    
    private void addGraphPoint(Vector2 newPoint) {
    	points.add(newPoint);
    }
    
    private void drawGraphPoint(ShapeRenderer shapeRenderer) {
        if (points.size >= 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            int rr = 23;
            int rg = 32;
            int rb = 23;
             shapeRenderer.setColor(rr, rg, rb, 255);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }    	
    }

	@Override
	public void render() {	
		final float delta = Math.min(1/30f, Gdx.graphics.getDeltaTime());
        update(delta);
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        if(isLoaded) {
            batch.begin();
            for (ReelSlotTileScroll reelSlot : reelSlots) {
                reelSlot.draw(batch);
                sprites[reelSlot.getEndReel()].setX(32);
                sprites[reelSlot.getEndReel()].draw(batch);
            }
            batch.end();
            drawGraphPoint(shapeRenderer);
       }
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
	}
}
