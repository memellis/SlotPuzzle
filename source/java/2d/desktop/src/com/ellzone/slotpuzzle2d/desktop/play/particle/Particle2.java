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
import com.ellzone.slotpuzzle.physics.Particle;
import com.ellzone.slotpuzzle.physics.Vector;
import com.ellzone.slotpuzzle2d.screens.TweenGraphsScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileScroll;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class Particle2 implements ApplicationListener {

	private static final float MINIMUM_VIEWPORT_SIZE = 15.0f;
	private static final float VELOCITY_MIN = 2.0f;
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
	private Random random;
    private Array<ReelSlotTileScroll> reelSlots;
    private SlotPuzzleTween tween;
    private SpriteBatch batch;
	private Array<Particle> reelParticles;
	private Particle reelParticle;
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
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        graphStep = 0;
        savedAmplitude = 0;
        saveAmplitude = true;
        plotTime = 132;
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
        reelSlot = new ReelSlotTileScroll(slotReelScrollTexture, slotReelScrollTexture.getWidth(), slotReelScrollTexture.getHeight(), 0, 32, 0, TweenGraphsScreen.SIXTY_FPS);
        reelSlot.setX(0);
        reelSlot.setY(0);
        reelSlot.setSx(0);
        reelSlot.setEndReel(random.nextInt(sprites.length - 1));
        reelSlot.setSy(slotReelScrollTexture.getHeight() + 128 + reelSlot.getEndReel() * 32 );
        reelSlots.add(reelSlot);
	}	
	
	private void intialiseParticles() {
		accelerator = new Vector(0, 3f);
		reelParticles = new Array<Particle>();
		reelParticle = new Particle(0, reelSlots.get(0).getSy(), 0.0001f , 0, 0);
		reelParticle.velocity.setX(0);
		reelParticle.velocity.setY(4);
		reelParticle.accelerate(new Vector(0, 2f));
		reelParticles.add(reelParticle);
		dampPoint = slotReelScrollTexture.getHeight() * 20;
	}
	
	private float dampenedSine(float initialAmplitude, float lambda, float angularFrequency, float time, float phaseAngle) {
		return (float) (initialAmplitude * Math.exp(-lambda * time) *  Math.cos(angularFrequency * time + phaseAngle));
	}
	
	private void initialiseCamera() {
        cam = new PerspectiveCamera();
        cam.position.set(0, 0, 10);
        cam.lookAt(0, 0, 0);
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
    	for (Particle reelParticle : reelParticles) {
    		reelParticle.update();
    	}
        for(ReelSlotTileScroll reelSlot : reelSlots) {
    		if (reelParticles.get(0).velocity.getY() > Particle2.VELOCITY_MIN) {
    	 		reelParticles.get(0).velocity.mulitplyBy(0.97f);
        		reelParticles.get(0).accelerate(accelerator);
        		accelerator.mulitplyBy(0.97f);       			
         		reelSlot.setSy(reelParticles.get(0).position.getY());
         	} else {
      			if (reelSlot.getSy() < dampPoint) {
      				if (saveAmplitude) {
      					saveAmplitude = false;
      					savedSy = reelSlot.getSy();
      			      	savedAmplitude = (dampPoint - savedSy);      					
      				}
      		       	float ds = dampenedSine(savedAmplitude, 1.0f, (float) (2 * Math.PI), plotTime++/30, 0);
      		       	reelSlot.setSy(savedSy+ds);
        			
      			} 
      			
      		}
    		reelSlot.update(delta);
         }
    }

    private void drawGraphPoint(ShapeRenderer shapeRenderer, Vector2 newPoint) {
        if (points.size >= 2) {
            shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
            shapeRenderer.setColor(0, 255, 255, 255);
            for (int i = 0; i < points.size - 1; i++) {
                shapeRenderer.line(points.get(i).x, points.get(i).y, points.get(i + 1).x, points.get(i + 1).y);
            }
            shapeRenderer.end();
        }
        points.add(newPoint);
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
            }
            batch.end();
            float dsine = dampenedSine(savedAmplitude, 1.0f, (float) (2 * Math.PI), graphStep / 75, (float) Math.PI/2);
            drawGraphPoint(shapeRenderer, new Vector2(graphStep % Gdx.graphics.getWidth(), 200 + dsine % Gdx.graphics.getHeight()));
            graphStep++;
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