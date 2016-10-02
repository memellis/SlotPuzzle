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

package com.ellzone.slotpuzzle2d.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.ellzone.slotpuzzle2d.SlotPuzzle;
import com.ellzone.slotpuzzle2d.effects.SpriteAccessor;
import com.ellzone.slotpuzzle2d.level.Level;
import com.ellzone.slotpuzzle2d.scene.MapTile;
import com.ellzone.slotpuzzle2d.tweenengine.BaseTween;
import com.ellzone.slotpuzzle2d.tweenengine.SlotPuzzleTween;
import com.ellzone.slotpuzzle2d.tweenengine.TweenCallback;
import com.ellzone.slotpuzzle2d.tweenengine.TweenManager;
import com.ellzone.slotpuzzle2d.utils.ScreenshotFactory;

public class WorldScreen implements Screen {

	public class MapLevel1 extends Level {
		@Override
		public void initialise() {
		}	

		@Override
		public String getImageName() {
			return "MapTile";
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

	private SlotPuzzle game;
	private OrthographicCamera cam;
	private TiledMap worldMap;
	private GestureDetector gestureDetector;
	private MapGestureListener mapGestureListener;
	private Array<Rectangle> levelDoors;
	private OrthogonalTiledMapRenderer renderer;
	private BitmapFont font;
	private float w, h, aspectRatio;
	private Pixmap levelDoorPixmap;
	private Texture levelDoorTexture;
	private Sprite levelDoorSprite;
    private TextureAtlas tilesAtlas;
	private MapTile mapTile, selectedTile;
	private TweenManager tweenManager; 

    public WorldScreen(SlotPuzzle game) {
		this.game = game;
		createWorldScreen();		
	}
    
    private void createWorldScreen() {
    	getAssets();
    	initialiseCamera();
    	initialiseUniversalTweenEngine();
    	initialiseLibGdx();
    	loadWorld();
    }

	private void getAssets() {
		 worldMap = game.assetManager.get("levels/WorldMap.tmx");
	     tilesAtlas = game.assetManager.get("tiles/tiles.pack.atlas", TextureAtlas.class);  
	}
	
    private void initialiseCamera() {
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		aspectRatio = w / h;
        cam = new OrthographicCamera();
        
		cam.setToOrtho(false, aspectRatio * 10, 10);
		cam.zoom = 2;
        cam.update();
    };

    private void initialiseLibGdx() {
      	font = new BitmapFont();
		mapGestureListener = new MapGestureListener(cam);		
		gestureDetector = new GestureDetector(2, 0.5f, 2, 0.15f, mapGestureListener);
		InputMultiplexer multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(gestureDetector);
		Gdx.input.setInputProcessor(multiplexer);
    	renderer = new OrthogonalTiledMapRenderer(worldMap, 1f / 40f);
   }
    
	private void initialiseUniversalTweenEngine() {
	    SlotPuzzleTween.setWaypointsLimit(10);
	    SlotPuzzleTween.setCombinedAttributesLimit(3);
	    SlotPuzzleTween.registerAccessor(Sprite.class, new SpriteAccessor());
	    tweenManager = new TweenManager();
	}
    
    private void loadWorld() {
    	levelDoors = new Array<Rectangle>();
		for (MapObject mapObject : worldMap.getLayers().get(2).getObjects().getByType(RectangleMapObject.class)) {
			Rectangle mapRectangle = ((RectangleMapObject) mapObject).getRectangle();
			levelDoors.add(mapRectangle);
		}
    }
    
	private void createPopUps(Sprite mapTileSprite) {
		Level level1 = new MapLevel1();
		mapTile = new MapTile(20, 20, 200, 200, level1, tilesAtlas, cam, font, tweenManager, mapTileSprite);
	}
		
	private final TweenCallback maximizeCallback = new TweenCallback() {
		@Override
		public void onEvent(int type, BaseTween<?> source) {
			selectedTile = (MapTile) source.getUserData();
			selectedTile.getLevel().initialise();
			game.setScreen(new PlayScreen(game));
		}
	};
    
	@Override
	public void show() {
	}
	
	public void update(float delta) {
		tweenManager.update(delta);
	}

	@Override
	public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        update(delta);
		cam.update();
		game.batch.begin();
		if(levelDoorSprite != null) {
			levelDoorSprite.draw(game.batch);
		}
		if (mapTile != null) {
			mapTile.draw(game.batch);
		}
		game.batch.end();
		mapGestureListener.update();
		renderer.setView(cam);
		renderer.render();
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}
	
	public class MapGestureListener implements GestureListener {

		private final OrthographicCamera camera;
		float velX, velY;
		boolean flinging = false;
		float initialScale = 1;
		
		public MapGestureListener(OrthographicCamera camera) {
		     this.camera = camera;	
		}
		
	    @Override
	    public boolean touchDown(float x, float y, int pointer, int button) {
			flinging = false;
			initialScale = camera.zoom;
	        return false;
	    }

	    @Override
	    public boolean tap(float x, float y, int count, int button) {
	    	float wx = screenXToWorldX(x);
	    	float wy = screenYToWorldY(y);	    	
	    	for (Rectangle levelDoor : levelDoors) {
	    		if (levelDoor.contains(wx, wy)) {
	    			int sx = (int)worldXToScreenX(levelDoor.x);
	    			int sy = (int)worldYToScreenY(levelDoor.y);
	    			int sw = (int)(levelDoor.width * 0.6f);
	    			int sh = (int)(levelDoor.height * 0.6f);
	    			levelDoorPixmap = ScreenshotFactory.getScreenshot(sx, sy, sw, sh, true);
	    			levelDoorTexture = new Texture(levelDoorPixmap);
	    			levelDoorSprite = new Sprite(levelDoorTexture);
	    			levelDoorSprite.setPosition(sx, sy);
	    			levelDoorSprite.setBounds(sx, sy, sw, sh);
	    			createPopUps(levelDoorSprite);
					mapTile.maximize(maximizeCallback);
	    		}
	    	}
	        return false;
	    }

	    @Override
	    public boolean longPress(float x, float y) {
	    	System.out.println("longPress");
	        return false;
	    }

	    @Override
	    public boolean fling(float velocityX, float velocityY, int button) {
			flinging = true;
			velX = camera.zoom * velocityX * 0.1f;
			velY = camera.zoom * velocityY * 0.1f;
	        return false;
	    }

	    @Override
	    public boolean pan(float x, float y, float deltaX, float deltaY) {
		    camera.position.add(-deltaX * camera.zoom * 0.1f, deltaY * camera.zoom * 0.1f, 0);
	        clampCamera();
		    return false;
	    }

	    @Override
	    public boolean panStop(float x, float y, int pointer, int button) {
	        return false;
	    }

	    @Override
	    public boolean zoom (float originalDistance, float currentDistance){
			float ratio = originalDistance / currentDistance;
			camera.zoom = initialScale * ratio;
	       return false;
	    }
	 
		@Override
		public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
			return false;
		}	

		public void update () {
			if (flinging) {
				velX *= 0.98f;
				velY *= 0.98f;
				camera.position.add(-velX * Gdx.graphics.getDeltaTime(), velY * Gdx.graphics.getDeltaTime(), 0);
				clampCamera();
				if (Math.abs(velX) < 0.01f) velX = 0;
				if (Math.abs(velY) < 0.01f) velY = 0;
			}
		}

		private void clampCamera() {
			if (camera.position.x < 0) {
				camera.position.x = 0;
			}
			if (camera.position.x > 100) {
				camera.position.x = 100;
			}
			if (camera.position.y < 0) {
				camera.position.y = 0;
			}
			if (camera.position.y > 400) {
				camera.position.y = 400;
			}			
		}
		
		private float screenXToWorldX(float x) {
			return ((camera.position.x - aspectRatio * 10) * 40) + (x / 0.6f);
		}
		
		private float screenYToWorldY(float y) {
			return ((camera.position.y - 10) * 40) + ((h - y) / 0.6f);
		}
		
		private float worldXToScreenX(float wx) {
			return (wx  - ((camera.position.x - aspectRatio * 10) * 40)) * 0.6f;
		}
		
		private float worldYToScreenY(float wy) {
			return (wy - ((camera.position.y - 10) * 40)) * 0.6f;	
		}
	}	
}
