package com.ellzone.slotpuzzle2d.desktop.play.bezier;

import java.util.Random;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.CatmullRomSpline;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.screens.TweenGraphsScreen;
import com.ellzone.slotpuzzle2d.sprites.ReelSlotTileScroll;

import com.ellzone.slotpuzzle2d.utils.Assets;
import com.ellzone.slotpuzzle2d.utils.PixmapProcessors;

public class Bezier3 implements ApplicationListener {

	public class MyInputProcessor implements InputProcessor {
	    @Override
	    public boolean keyDown(int keycode){
	        return false;
	    }
	    @Override
	    public boolean keyUp(int keycode){
	        return false;
	    }
	    @Override
	    public boolean touchDown(int screenX, int screenY, int pointer, int button){
	        return false;
	    }
	    @Override
	    public boolean touchUp(int screenX, int screenY, int pointer, int button){
	    	dragHit = false;
	        return false;
	    }
	    @Override 
	    public boolean keyTyped(char character){
	           return false;
	    }
	    @Override 
	    public boolean touchDragged(int screenX, int screenY, int pointer){
            Vector2 touchPoint = new Vector2(screenX, Gdx.graphics.getHeight() - screenY);            
	    	if(dragHit) {
    			reelSpinPath[dragControlPoint].y = touchPoint.y - Gdx.graphics.getHeight() / 4;
    			myCatmull = new CatmullRomSpline<Vector2>(reelSpinPath, true);
    			reelSpinBezier.clear();
    		    for(int i = 0; i < graphSize; i++) {
    		        reelSpinBezier.add(new Vector2());
    		        myCatmull.valueAt(reelSpinBezier.get(i), ((float)i)/((float)graphSize-1));
    		    }
    			
	    	} else {
	    		dragControlPoint = instersectControlPoint(touchPoint, reelSpinPath); 
				if (dragControlPoint >= 0) {
	    			dragHit = true;
				}
            }
	        return true;
	    }
	    @Override 
	    public boolean mouseMoved(int screenX, int screenY){
	        return false;
	    }
	    @Override 
	    public boolean scrolled(int amount) {
	        return false;
	    }   
	}
	
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
	private Random random;
    private Array<ReelSlotTileScroll> reelSlots;
    private SpriteBatch batch;
    private ShapeRenderer shapeRenderer;
    private Array<Vector2> points = new Array<Vector2>();
    private Array<Vector2> reelSpinBezier = new Array<Vector2>();
    private int graphStep;
    private int graphSize = 512;
    private Vector2[] reelSpinPath;
    private MyInputProcessor inputProcessor;
    private boolean dragHit;
    private int dragControlPoint;
    CatmullRomSpline<Vector2> myCatmull;
 
	@Override
	public void create() {
        loadAssets();
        initialiseReelSlots();
        initialiseBezier();
        initialiseCamera();
        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        inputProcessor = new MyInputProcessor();
        Gdx.input.setInputProcessor(inputProcessor);
        dragHit = false;
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
        reelSlot.setSy(0);
        reelSlots.add(reelSlot);
	}	
	
	private void initialiseBezier() {
		reelSpinPath = new Vector2[20];
		int idx=0;
		for (int l=0; l<10; l++) {
			reelSpinPath[idx++] = new Vector2(0, 512*l);
		}
		reelSpinPath[idx++] = new Vector2(0, +370);		
		reelSpinPath[idx++] = new Vector2(0, -256);
		reelSpinPath[idx++] = new Vector2(0, +128);
		reelSpinPath[idx++] = new Vector2(0, -64);
		reelSpinPath[idx++] = new Vector2(0, +48);
		reelSpinPath[idx++] = new Vector2(0, -32);
		reelSpinPath[idx++] = new Vector2(0, +40);
		reelSpinPath[idx++] = new Vector2(0, -16);
		reelSpinPath[idx++] = new Vector2(0, +32);
		reelSpinPath[idx++] = new Vector2(0, +32);
				
		CatmullRomSpline<Vector2> myCatmull = new CatmullRomSpline<Vector2>(reelSpinPath, true);
	    for(int i = 0; i < graphSize; i++) {
	        reelSpinBezier.add(new Vector2());
	        myCatmull.valueAt(reelSpinBezier.get(i), ((float)i)/((float)graphSize-1));
	    }
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
        for(ReelSlotTileScroll reelSlot : reelSlots) {
        	if (graphStep < reelSpinBezier.size - (graphSize/reelSpinPath.length)) {
        		reelSlot.setSy(reelSpinBezier.get(graphStep).y);
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
            if (graphStep < reelSpinBezier.size - (graphSize/reelSpinPath.length)) {
            	drawGraphPoint(shapeRenderer, new Vector2(graphStep % Gdx.graphics.getWidth(), reelSpinBezier.get(graphStep).y + Gdx.graphics.getHeight() / 4 % Gdx.graphics.getHeight()));
            	graphStep++;
            } else {
            	graphStep = 0;
            }
            drawControlPoints(shapeRenderer, reelSpinPath);
       }
	}
	
	private void drawControlPoints(ShapeRenderer shapeRenderer, Vector2[] reelSpinPath) {
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(Color.GOLD);
        for (int i=0; i<reelSpinPath.length; i++) {
            shapeRenderer.circle(i*(graphSize/reelSpinPath.length), reelSpinPath[i].y + Gdx.graphics.getHeight() / 4, 4);
        }
        shapeRenderer.end();
	}
	
	private int instersectControlPoint(Vector2 touchPoint, Vector2[] controlPoints) {
		for (int i=0; i<controlPoints.length; i++){
			float xD = touchPoint.x - i*(graphSize/reelSpinPath.length);
			float yD = touchPoint.y - (controlPoints[i].y + Gdx.graphics.getHeight() / 4);
			float sqDist = xD * xD + yD * yD;
 			boolean collesion = sqDist <= (0.5+4) * (0.5+4);
			if (collesion) {
				return i;
			}
		}
		return -1;
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
